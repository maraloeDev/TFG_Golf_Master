package com.maraloedev.golfmaster.view.menuHamburguesa.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Modelo de datos del perfil del jugador.
 *
 * Se separa la estructura de presentación (perfil) de otras entidades
 * de dominio para simplificar el uso desde la UI.
 */
data class JugadorPerfil(
    val id: String = "",
    val nombre_jugador: String = "",
    val telefono_jugador: String = "",
    val sexo_jugador: String = "",
    val ciudad_jugador: String? = null,
    val provincia_jugador: String? = null,
    val codigo_postal_jugador: String = "",
    val licencia_jugador: String = "",
    val handicap_jugador: String = "",
    val correo_jugador: String = ""
)

/**
 * ViewModel responsable de gestionar el perfil del jugador:
 *  - Carga inicial de datos desde Firestore.
 *  - Generación automática de licencia si no existe.
 *  - Actualización de información de perfil.
 *  - Eliminación completa de cuenta (datos + autenticación).
 */
class PerfilViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Estado observable del perfil del jugador
    private val _jugador = MutableStateFlow<JugadorPerfil?>(null)
    val jugador: StateFlow<JugadorPerfil?> get() = _jugador

    init {
        cargarPerfil()
    }

    /**
     * Carga el perfil del jugador desde la colección "jugadores".
     *
     * Si no existe el documento o no dispone de licencia, se genera
     * una nueva y se actualiza en Firestore.
     */
    fun cargarPerfil() {
        val user = auth.currentUser ?: return
        val uid = user.uid

        db.collection("jugadores").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val data = doc.data ?: return@addOnSuccessListener

                    // Si no existe campo de licencia, se genera una nueva
                    val licencia = (data["licencia_jugador"] as? String)
                        ?: generarLicencia().also { nuevaLicencia ->
                            db.collection("jugadores").document(uid)
                                .update("licencia_jugador", nuevaLicencia)
                        }

                    _jugador.value = JugadorPerfil(
                        id = uid,
                        nombre_jugador = data["nombre_jugador"] as? String ?: "",
                        correo_jugador = data["correo_jugador"] as? String ?: user.email.orEmpty(),
                        telefono_jugador = data["telefono_jugador"] as? String ?: "",
                        sexo_jugador = data["sexo_jugador"] as? String ?: "",
                        ciudad_jugador = data["ciudad_jugador"] as? String ?: "",
                        provincia_jugador = data["provincia_jugador"] as? String ?: "",
                        codigo_postal_jugador = data["codigo_postal_jugador"] as? String ?: "",
                        licencia_jugador = licencia,
                        handicap_jugador = when (val h = data["handicap_jugador"]) {
                            is Number -> h.toString()
                            is String -> h
                            else -> ""
                        }
                    )
                } else {
                    // Documento inexistente: se crea uno mínimo con licencia nueva
                    val nuevaLicencia = generarLicencia()
                    val nuevoPerfil = JugadorPerfil(
                        id = uid,
                        correo_jugador = user.email.orEmpty(),
                        licencia_jugador = nuevaLicencia
                    )
                    db.collection("jugadores").document(uid).set(nuevoPerfil)
                    _jugador.value = nuevoPerfil
                }
            }
            .addOnFailureListener {
                // En caso de error se deja el estado en null para que la UI gestione el fallback
                _jugador.value = null
            }
    }

    /**
     * Genera un identificador de licencia sencillo de 6 dígitos.
     *
     * Ejemplo: 583201
     */
    private fun generarLicencia(): String =
        Random.nextInt(100_000, 999_999).toString()

    /**
     * Actualiza el perfil del jugador en Firestore.
     *
     * @param perfil     Nueva información de perfil a persistir.
     * @param onSuccess  Callback de éxito.
     * @param onError    Callback con mensaje de error legible para la UI.
     */
    fun actualizarPerfil(
        perfil: JugadorPerfil,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: run {
            onError("Usuario no autenticado")
            return
        }

        viewModelScope.launch {
            runCatching {
                db.collection("jugadores").document(uid).set(perfil)
            }.onSuccess {
                _jugador.value = perfil
                onSuccess()
            }.onFailure { e ->
                onError(e.localizedMessage ?: "Error al actualizar perfil")
            }
        }
    }

    /**
     * Elimina completamente la cuenta del usuario:
     *  - Documento de "jugadores".
     *  - Documento de "preferencias" asociado.
     *  - Usuario de Firebase Authentication.
     */
    fun eliminarCuenta(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: run {
            onError("Usuario no autenticado")
            return
        }
        val user = auth.currentUser

        viewModelScope.launch {
            runCatching {
                db.collection("jugadores").document(uid).delete()
                db.collection("preferencias").document(uid).delete()
                user?.delete()
            }.onSuccess {
                onSuccess()
            }.onFailure { e ->
                onError(e.localizedMessage ?: "Error al eliminar cuenta")
            }
        }
    }
}
