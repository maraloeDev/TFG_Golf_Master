package com.maraloedev.golfmaster.view.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

    fun cargarPerfil() {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrBlank()) {
            _ui.value = PerfilUiState(error = "No hay sesión activa.")
            return
        }

        _ui.value = _ui.value.copy(loading = true, error = null)

        db.collection("jugadores").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    val nuevo = JugadorPerfil(id = uid, correo_jugador = auth.currentUser?.email ?: "")
                    db.collection("jugadores").document(uid).set(nuevo)
                    _ui.value = PerfilUiState(jugador = nuevo, loading = false)
                } else {
                    val data = doc.data ?: emptyMap()
                    val perfil = JugadorPerfil(
                        id = data["id"] as? String ?: uid,
                        nombre_jugador = data["nombre_jugador"] as? String ?: "",
                        correo_jugador = data["correo_jugador"] as? String ?: (auth.currentUser?.email ?: ""),
                        telefono_jugador = data["telefono_jugador"] as? String ?: "",
                        sexo_jugador = data["sexo_jugador"] as? String ?: "Hombre",
                        pais_jugador = data["pais_jugador"] as? String ?: "",
                        codigo_postal_jugador = data["codigo_postal_jugador"] as? String ?: "",
                        licencia_jugador = data["licencia_jugador"] as? String ?: "",
                        handicap_jugador = when (val v = data["handicap_jugador"]) {
                            is Number -> v.toString()
                            is String -> v
                            else -> ""
                        }
                    )
                    _ui.value = PerfilUiState(jugador = perfil, loading = false)
                }
            }
            .addOnFailureListener { e ->
                _ui.value = PerfilUiState(error = e.localizedMessage ?: "Error al cargar perfil", loading = false)
            }
    }

    fun actualizarPerfil(perfil: JugadorPerfil, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrBlank()) return onError("Usuario no autenticado")

        if (perfil.nombre_jugador.isBlank()) return onError("El nombre no puede estar vacío.")
        if (!perfil.correo_jugador.contains("@")) return onError("Correo electrónico no válido.")

        _ui.value = _ui.value.copy(loading = true)
        viewModelScope.launch {
            db.collection("jugadores").document(uid)
                .set(perfil)
                .addOnSuccessListener {
                    _ui.value = PerfilUiState(jugador = perfil, loading = false, success = true)
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    _ui.value = _ui.value.copy(loading = false, error = e.localizedMessage)
                    onError(e.localizedMessage ?: "Error al guardar cambios.")
                }
        }
    }

    fun eliminarCuenta(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val uid = auth.currentUser?.uid
        val user = auth.currentUser

        if (uid.isNullOrBlank() || user == null) return onError("Usuario no autenticado")

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
}
