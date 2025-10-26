package com.maraloedev.golfmaster.view.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Modelo de datos del jugador (sólo Strings para seguridad)
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
    val handicap_jugador: String = ""
)

/**
 * Estado de UI seguro y centralizado
 */
data class PerfilUiState(
    val jugador: JugadorPerfil? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class PerfilViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _ui = MutableStateFlow(PerfilUiState(loading = true))
    val ui: StateFlow<PerfilUiState> = _ui

    init {
        cargarPerfil()
    }

    /**
     * Carga el perfil del jugador autenticado con comprobaciones seguras
     */
    fun cargarPerfil() {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrBlank()) {
            _ui.value = PerfilUiState(
                loading = false,
                jugador = null,
                error = "No hay sesión activa. Por favor, inicia sesión."
            )
            return
        }

        _ui.value = _ui.value.copy(loading = true, error = null)

        db.collection("jugadores").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    val nuevo = JugadorPerfil(id = uid, correo_jugador = auth.currentUser?.email ?: "")
                    db.collection("jugadores").document(uid).set(nuevo)
                    _ui.value = PerfilUiState(loading = false, jugador = nuevo)
                    return@addOnSuccessListener
                }

                val data = doc.data ?: emptyMap()
                val jugador = JugadorPerfil(
                    id = (data["id"] as? String).orEmpty().ifBlank { uid },
                    nombre_jugador = (data["nombre_jugador"] as? String).orEmpty(),
                    correo_jugador = (data["correo_jugador"] as? String)
                        ?: auth.currentUser?.email.orEmpty(),
                    telefono_jugador = (data["telefono_jugador"] as? String).orEmpty(),
                    sexo_jugador = (data["sexo_jugador"] as? String).orEmpty().ifBlank { "Hombre" },
                    pais_jugador = (data["pais_jugador"] as? String).orEmpty(),
                    codigo_postal_jugador = (data["codigo_postal_jugador"] as? String).orEmpty(),
                    licencia_jugador = (data["licencia_jugador"] as? String).orEmpty(),
                    handicap_jugador = when (val valor = data["handicap_jugador"]) {
                        is Number -> valor.toString()
                        is String -> valor
                        else -> ""
                    }
                )

                _ui.value = PerfilUiState(jugador = jugador, loading = false)
            }
            .addOnFailureListener { e ->
                _ui.value = PerfilUiState(
                    jugador = null,
                    loading = false,
                    error = e.localizedMessage ?: "Error al cargar perfil"
                )
            }
    }

    /**
     * Guarda cambios con validación de datos
     */
    fun actualizarPerfil(
        perfil: JugadorPerfil,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrBlank()) {
            onError("Usuario no autenticado.")
            return
        }

        // Validaciones básicas
        if (perfil.nombre_jugador.isBlank()) {
            onError("El nombre no puede estar vacío.")
            return
        }
        if (!perfil.correo_jugador.contains("@")) {
            onError("Correo electrónico no válido.")
            return
        }

        _ui.value = _ui.value.copy(loading = true)

        viewModelScope.launch {
            db.collection("jugadores").document(uid)
                .set(perfil)
                .addOnSuccessListener {
                    _ui.value = PerfilUiState(jugador = perfil, success = true, loading = false)
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    _ui.value = _ui.value.copy(loading = false, error = e.localizedMessage)
                    onError(e.localizedMessage ?: "Error al guardar cambios.")
                }
        }
    }

    /**
     * Elimina la cuenta (Firestore + Auth)
     */
    fun eliminarCuenta(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val uid = auth.currentUser?.uid
        val user = auth.currentUser

        if (uid.isNullOrBlank() || user == null) {
            onError("Usuario no autenticado.")
            return
        }

        _ui.value = _ui.value.copy(loading = true)

        viewModelScope.launch {
            db.collection("jugadores").document(uid).delete()
            db.collection("preferencias").document(uid).delete()
            user.delete()
                .addOnSuccessListener {
                    _ui.value = PerfilUiState(jugador = null, loading = false)
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    _ui.value = _ui.value.copy(loading = false, error = e.localizedMessage)
                    onError(e.localizedMessage ?: "Error al eliminar cuenta.")
                }
        }
    }

    fun limpiarError() {
        _ui.value = _ui.value.copy(error = null)
    }
}
