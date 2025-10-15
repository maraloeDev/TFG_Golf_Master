package com.maraloedev.golfmaster.view.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de registro. Gestiona los estados de los campos y la lógica de registro.
 */
class RegisterViewModel : ViewModel() {
    // Estado del email
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    // Estado de la contraseña
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    // Estado de la confirmación de contraseña
    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    // Mensaje de registro (error o éxito)
    private val _registerMessage = MutableStateFlow("")
    val registerMessage: StateFlow<String> = _registerMessage

    // Estado para habilitar el botón
    val isRegisterEnabled: StateFlow<Boolean> = MutableStateFlow(false)

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Actualiza el email y valida el formulario.
     */
    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        validateForm()
    }

    /**
     * Actualiza la contraseña y valida el formulario.
     */
    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        validateForm()
    }

    /**
     * Actualiza la confirmación de contraseña y valida el formulario.
     */
    fun onConfirmPasswordChange(newConfirm: String) {
        _confirmPassword.value = newConfirm
        validateForm()
    }

    /**
     * Valida el formulario y actualiza el estado del botón.
     */
    private fun validateForm() {
        (isRegisterEnabled as MutableStateFlow).value =
            _email.value.contains("@") &&
            _password.value.length >= 6 &&
            _password.value == _confirmPassword.value
    }

    /**
     * Realiza el registro con Firebase.
     */
    fun register(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val email = _email.value
        val password = _password.value
        if (email.isNotBlank() && password.isNotBlank() && password == _confirmPassword.value) {
            viewModelScope.launch {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _registerMessage.value = "Registro correcto"
                            onSuccess()
                        } else {
                            _registerMessage.value = "Error: " + (task.exception?.message ?: "Datos incorrectos")
                            onError(_registerMessage.value)
                        }
                    }
            }
        } else {
            _registerMessage.value = "Introduce datos válidos"
            onError(_registerMessage.value)
        }
    }
}

