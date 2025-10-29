package com.maraloedev.golfmaster.view.home

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class HomeUiState(
    val usuarioNombre: String = "",
    val correo: String = "",
    val loading: Boolean = false
)

class HomeViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val _ui = MutableStateFlow(HomeUiState(loading = true))
    val ui: StateFlow<HomeUiState> = _ui

    init {
        cargarUsuario()
    }

    fun cargarUsuario() {
        val user = auth.currentUser
        if (user != null) {
            _ui.value = HomeUiState(
                usuarioNombre = user.displayName ?: user.email?.substringBefore("@") ?: "Jugador",
                correo = user.email ?: "",
                loading = false
            )
        } else {
            _ui.value = HomeUiState(loading = false)
        }
    }

    fun logout(onLogout: () -> Unit) {
        auth.signOut()
        onLogout()
    }
}
