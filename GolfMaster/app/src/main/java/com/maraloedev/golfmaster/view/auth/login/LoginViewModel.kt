package com.maraloedev.golfmaster.view.auth.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.google.firebase.auth.FirebaseAuth

/**
 * Estado de la pantalla de login.
 * email: correo introducido
 * password: contraseña introducida
 * isLoginEnabled: si el botón de login está habilitado
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoginEnabled: Boolean = false
)

/**
 * ViewModel para la pantalla de login.
 * Gestiona el estado y la autenticación con Firebase.
 */
class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Actualiza el email y valida el estado
    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
        validateFields()
    }

    // Actualiza la contraseña y valida el estado
    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
        validateFields()
    }

    // Valida los campos para habilitar el botón
    private fun validateFields() {
        val email = _uiState.value.email
        val password = _uiState.value.password
        val isValid = email.contains("@") && password.length >= 6
        _uiState.update { it.copy(isLoginEnabled = isValid) }
    }

    /**
     * Realiza el login con FirebaseAuth.
     * Llama a onSuccess si es correcto, onError si hay error.
     */
    fun login(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val email = _uiState.value.email
        val password = _uiState.value.password
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception?.message ?: "Error desconocido")
                }
            }
    }
}

