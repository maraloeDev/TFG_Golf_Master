package com.maraloedev.golfmaster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maraloedev.golfmaster.model.FirebaseRepo
import com.maraloedev.golfmaster.model.Jugadores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel del Home. Carga los datos del jugador autenticado.
 */
class HomeViewModel(
    private val repo: FirebaseRepo = FirebaseRepo()
) : ViewModel() {

    private val _jugador = MutableStateFlow<Jugadores?>(null)
    val jugador: StateFlow<Jugadores?> = _jugador

    init {
        viewModelScope.launch {
            repo.currentUid?.let {
                _jugador.value = repo.getJugador(it)
            }
        }
    }

    fun logout() {
        repo.logout()
    }
}
