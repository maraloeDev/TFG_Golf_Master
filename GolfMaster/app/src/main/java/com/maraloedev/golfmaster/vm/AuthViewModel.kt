package com.maraloedev.golfmaster.vm

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AuthUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _ui = MutableStateFlow(AuthUiState())
    val ui: StateFlow<AuthUiState> = _ui

    /**
     * Inicia sesión con email y contraseña
     */
    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onError("Por favor, completa todos los campos.")
            return
        }

        _ui.value = _ui.value.copy(loading = true)

        auth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnSuccessListener {
                _ui.value = AuthUiState(success = true)
                onSuccess()
            }
            .addOnFailureListener { e ->
                _ui.value = AuthUiState(loading = false, error = e.localizedMessage)
                onError(e.localizedMessage ?: "Error al iniciar sesión.")
            }
    }

    /**
     * Registra un nuevo usuario
     */
    fun register(
        nombre: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
            onError("Debes completar todos los campos.")
            return
        }

        _ui.value = _ui.value.copy(loading = true)

        auth.createUserWithEmailAndPassword(email.trim(), password.trim())
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener

                val jugador = hashMapOf(
                    "id" to uid,
                    "nombre_jugador" to nombre.trim(),
                    "correo_jugador" to email.trim(),
                    "telefono_jugador" to "",
                    "sexo_jugador" to "Hombre",
                    "pais_jugador" to "",
                    "codigo_postal_jugador" to "",
                    "licencia_jugador" to "",
                    "handicap_jugador" to ""
                )

                db.collection("jugadores").document(uid)
                    .set(jugador)
                    .addOnSuccessListener {
                        _ui.value = AuthUiState(success = true)
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        onError(e.localizedMessage ?: "Error al guardar el perfil.")
                    }
            }
            .addOnFailureListener { e ->
                _ui.value = AuthUiState(loading = false, error = e.localizedMessage)
                onError(e.localizedMessage ?: "Error al registrarse.")
            }
    }

    /**
     * Cierra sesión
     */
    fun logout() {
        auth.signOut()
        _ui.value = AuthUiState()
    }

    /**
     * Comprueba si hay sesión activa
     */
    fun haySesionActiva(): Boolean = auth.currentUser != null
}
