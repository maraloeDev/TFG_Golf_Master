package com.maraloedev.golfmaster.view.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Estado de UI para la pantalla de inicio.
 */
data class HomeUiState(
    val loading: Boolean = true,
    val jugador: JugadorPerfil? = null,
    val error: String? = null
)

/**
 * Modelo ligero de perfil (string-safe).
 * Mantiene compatibilidad con tu UI actual.
 */
data class JugadorPerfil(
    val id: String = "",
    val nombre_jugador: String = "",
    val correo_jugador: String = "",
    val telefono_jugador: String = "",
    val sexo_jugador: String = "Hombre",
    val pais_jugador: String = "",
    val codigo_postal_jugador: String = "",
    val licencia_jugador: String = "",
    val handicap_jugador: String = "" // lo mantenemos String para evitar crashes por tipos
)

/**
 * ViewModel de Home con comprobaciones robustas:
 * - Verifica sesión
 * - Comprueba existencia de documento
 * - Sanea tipos y nullables
 * - Expone recarga y limpieza de error
 */
class HomeViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _ui = MutableStateFlow(HomeUiState())
    val ui: StateFlow<HomeUiState> = _ui

    val jugador: StateFlow<JugadorPerfil?> get() = MutableStateFlow(_ui.value.jugador) // compat con tu HomeScreen

    init {
        cargarJugador()
    }

    /**
     * Carga/recarga el jugador autenticado con protecciones.
     */
    fun cargarJugador() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null)

            val uid = auth.currentUser?.uid
            if (uid.isNullOrBlank()) {
                _ui.value = HomeUiState(
                    loading = false,
                    jugador = null,
                    error = "No hay sesión activa. Inicia sesión."
                )
                return@launch
            }

            db.collection("jugadores").document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    if (!doc.exists()) {
                        // Si no existe, creamos un esqueleto mínimo con valores seguros
                        val minimo = JugadorPerfil(
                            id = uid,
                            correo_jugador = auth.currentUser?.email.orEmpty()
                        )
                        db.collection("jugadores").document(uid).set(minimo)
                            .addOnSuccessListener {
                                _ui.value = HomeUiState(
                                    loading = false,
                                    jugador = minimo,
                                    error = null
                                )
                            }
                            .addOnFailureListener { e ->
                                _ui.value = HomeUiState(
                                    loading = false,
                                    jugador = null,
                                    error = "No existe perfil y falló la creación: ${e.localizedMessage ?: "Error desconocido"}"
                                )
                            }
                        return@addOnSuccessListener
                    }

                    // Saneado de tipos y nullables
                    val data = doc.data.orEmpty()
                    val perfilSeguro = JugadorPerfil(
                        id = (data["id"] as? String).orEmpty().ifBlank { uid },
                        nombre_jugador = (data["nombre_jugador"] as? String).orEmpty(),
                        correo_jugador = (data["correo_jugador"] as? String)
                            ?: auth.currentUser?.email.orEmpty(),
                        telefono_jugador = (data["telefono_jugador"] as? String).orEmpty(),
                        sexo_jugador = (data["sexo_jugador"] as? String).orEmpty().ifBlank { "Hombre" },
                        pais_jugador = (data["pais_jugador"] as? String).orEmpty(),
                        codigo_postal_jugador = (data["codigo_postal_jugador"] as? String).orEmpty(),
                        licencia_jugador = (data["licencia_jugador"] as? String).orEmpty(),
                        handicap_jugador = when (val v = data["handicap_jugador"]) {
                            is Number -> v.toString()
                            is String -> v
                            else -> ""
                        }
                    )

                    _ui.value = HomeUiState(
                        loading = false,
                        jugador = perfilSeguro,
                        error = null
                    )
                }
                .addOnFailureListener { e ->
                    _ui.value = HomeUiState(
                        loading = false,
                        jugador = null,
                        error = e.localizedMessage ?: "Error al cargar el perfil"
                    )
                }
        }
    }

    /**
     * Permite a la UI descartar un error mostrado (p.ej. tras Snackbar).
     */
    fun limpiarError() {
        _ui.value = _ui.value.copy(error = null)
    }

    /**
     * Cierre de sesión seguro (opcional para usar desde UI).
     */
    fun logout() {
        runCatching { auth.signOut() }
        _ui.value = HomeUiState(loading = false, jugador = null, error = null)
    }
}
