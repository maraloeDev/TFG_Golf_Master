package com.maraloedev.golfmaster.view.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Modelo de datos del jugador, usando Strings.
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
    val handicap_jugador: String = "" // ← se mantiene como String
)

/**
 * ViewModel de gestión de perfil del jugador.
 */
class PerfilViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _jugador = MutableStateFlow<JugadorPerfil?>(null)
    val jugador: StateFlow<JugadorPerfil?> = _jugador

    init {
        cargarPerfil()
    }

    /**
     * Cargar los datos del jugador autenticado
     */
    fun cargarPerfil() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("jugadores").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val data = doc.data ?: return@addOnSuccessListener

                    val perfil = JugadorPerfil(
                        id = data["id"] as? String ?: uid,
                        nombre_jugador = data["nombre_jugador"] as? String ?: "",
                        correo_jugador = data["correo_jugador"] as? String ?: (auth.currentUser?.email ?: ""),
                        telefono_jugador = data["telefono_jugador"] as? String ?: "",
                        sexo_jugador = data["sexo_jugador"] as? String ?: "Hombre",
                        pais_jugador = data["pais_jugador"] as? String ?: "",
                        codigo_postal_jugador = data["codigo_postal_jugador"] as? String ?: "",
                        licencia_jugador = data["licencia_jugador"] as? String ?: "",
                        handicap_jugador = when (val valor = data["handicap_jugador"]) {
                            is Number -> valor.toString()   // 🔥 Convierte Double → String sin crash
                            is String -> valor
                            else -> ""
                        }
                    )
                    _jugador.value = perfil
                } else {
                    // Crear documento base si no existe
                    val nuevo = JugadorPerfil(
                        id = uid,
                        correo_jugador = auth.currentUser?.email ?: ""
                    )
                    db.collection("jugadores").document(uid).set(nuevo)
                    _jugador.value = nuevo
                }
            }
            .addOnFailureListener {
                _jugador.value = null
            }
    }


    /**
     * Guardar cambios del perfil
     */
    fun actualizarPerfil(
        perfil: JugadorPerfil,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return onError("Usuario no autenticado")

        viewModelScope.launch {
            db.collection("jugadores").document(uid)
                .set(perfil)
                .addOnSuccessListener {
                    _jugador.value = perfil
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onError(e.localizedMessage ?: "Error al actualizar perfil")
                }
        }
    }

    /**
     * Eliminar cuenta (Auth + documentos)
     */
    fun eliminarCuenta(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onError("Usuario no autenticado")

        viewModelScope.launch {
            val user = auth.currentUser
            db.collection("jugadores").document(uid).delete()
            db.collection("preferencias").document(uid).delete()
            user?.delete()
                ?.addOnSuccessListener { onSuccess() }
                ?.addOnFailureListener { e -> onError(e.localizedMessage ?: "Error al eliminar cuenta") }
        }
    }
}
