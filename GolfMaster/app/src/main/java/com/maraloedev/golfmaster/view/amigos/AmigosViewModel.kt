package com.maraloedev.golfmaster.view.amigos

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Modelo de datos para un amigo o solicitud
 */
data class Amigo(
    val id: String = "",
    val nombre: String = "",
    val correo: String = "",
    val estado: String = "Pendiente" // "Pendiente", "Aceptado"
)

/**
 * Estado de la interfaz
 */
data class AmigosUiState(
    val amigos: List<Amigo> = emptyList(),
    val solicitudesPendientes: List<Amigo> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class AmigosViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _ui = MutableStateFlow(AmigosUiState(loading = true))
    val ui: StateFlow<AmigosUiState> = _ui

    init {
        cargarAmigos()
    }

    /**
     * Cargar la lista de amigos y solicitudes del usuario actual
     */
    fun cargarAmigos() {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrBlank()) {
            _ui.value = AmigosUiState(
                loading = false,
                error = "No hay sesión activa. Inicia sesión."
            )
            return
        }

        _ui.value = _ui.value.copy(loading = true)

        db.collection("amigos")
            .whereArrayContains("usuarios", uid)
            .get()
            .addOnSuccessListener { result ->
                val amigos = mutableListOf<Amigo>()
                val pendientes = mutableListOf<Amigo>()

                for (doc in result.documents) {
                    val data = doc.data ?: continue
                    val estado = data["estado"] as? String ?: "Pendiente"
                    val usuarios = (data["usuarios"] as? List<*>)?.filterIsInstance<String>() ?: continue
                    val otroUsuario = usuarios.firstOrNull { it != uid } ?: continue

                    val nombre = data["nombre_$otroUsuario"] as? String ?: "Desconocido"
                    val correo = data["correo_$otroUsuario"] as? String ?: ""

                    val amigo = Amigo(
                        id = doc.id,
                        nombre = nombre,
                        correo = correo,
                        estado = estado
                    )

                    if (estado == "Pendiente") pendientes.add(amigo)
                    else amigos.add(amigo)
                }

                _ui.value = AmigosUiState(
                    amigos = amigos,
                    solicitudesPendientes = pendientes,
                    loading = false
                )
            }
            .addOnFailureListener { e ->
                _ui.value = AmigosUiState(
                    loading = false,
                    error = e.localizedMessage ?: "Error al cargar amigos."
                )
            }
    }

    /**
     * Enviar una solicitud de amistad por correo
     */
    fun enviarSolicitud(correoDestino: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val uid = auth.currentUser?.uid
        val correoOrigen = auth.currentUser?.email

        if (uid.isNullOrBlank() || correoOrigen.isNullOrBlank()) {
            onError("Usuario no autenticado.")
            return
        }
        if (correoDestino.isBlank()) {
            onError("Debes introducir un correo.")
            return
        }
        if (correoDestino == correoOrigen) {
            onError("No puedes enviarte una solicitud a ti mismo.")
            return
        }

        db.collection("jugadores")
            .whereEqualTo("correo_jugador", correoDestino)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    onError("No existe ningún usuario con ese correo.")
                    return@addOnSuccessListener
                }

                val otroUsuario = result.documents.first().id

                val datos = hashMapOf(
                    "usuarios" to listOf(uid, otroUsuario),
                    "estado" to "Pendiente",
                    "nombre_$uid" to auth.currentUser?.displayName.orEmpty(),
                    "correo_$uid" to correoOrigen,
                    "nombre_$otroUsuario" to (result.documents.first().getString("nombre_jugador") ?: "Jugador"),
                    "correo_$otroUsuario" to correoDestino
                )

                db.collection("amigos")
                    .add(datos)
                    .addOnSuccessListener {
                        onSuccess()
                        cargarAmigos()
                        _ui.value = _ui.value.copy(successMessage = "Solicitud enviada ✅")
                    }
                    .addOnFailureListener { e ->
                        onError(e.localizedMessage ?: "Error al enviar solicitud.")
                    }
            }
            .addOnFailureListener { e ->
                onError(e.localizedMessage ?: "Error al buscar jugador.")
            }
    }

    /**
     * Aceptar una solicitud pendiente
     */
    fun aceptarSolicitud(amigoId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        db.collection("amigos").document(amigoId)
            .update("estado", "Aceptado")
            .addOnSuccessListener {
                onSuccess()
                cargarAmigos()
            }
            .addOnFailureListener { e ->
                onError(e.localizedMessage ?: "Error al aceptar solicitud.")
            }
    }

    /**
     * Eliminar un amigo o rechazar una solicitud
     */
    fun eliminarAmigo(amigoId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        db.collection("amigos").document(amigoId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
                cargarAmigos()
            }
            .addOnFailureListener { e ->
                onError(e.localizedMessage ?: "Error al eliminar amigo.")
            }
    }
}
