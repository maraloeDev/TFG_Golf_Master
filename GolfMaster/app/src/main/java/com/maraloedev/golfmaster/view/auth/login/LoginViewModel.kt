package com.maraloedev.instadev.view.auth.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel() : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onEmailChange(email: String) {
        _uiState.update { state ->
            state.copy(email = email) //el .copy devuelve el mismo objeto, pero con los cambios que digas
        }
        verifyLogin()
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(password = password)
        }
        verifyLogin()
    }

    private fun verifyLogin() {

        val enableLogin =
            isEmailValid(_uiState.value.email) && isPasswordValid(_uiState.value.password)

        _uiState.update {
            it.copy(isLoginEnabled = enableLogin)
        }
    }
}


//Accedes a la funcion del sistema en la que verifica si el EMAil introducido es correcto
private fun isEmailValid(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()
private fun isPasswordValid(password: String): Boolean = password.length >= 6

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoginEnabled: Boolean = false
)