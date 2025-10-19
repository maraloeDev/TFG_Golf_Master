package com.maraloedev.golfmaster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maraloedev.golfmaster.model.FirebaseRepo
import com.maraloedev.golfmaster.model.Jugadores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para el perfil del jugador actual.
 * Obtiene y actualiza la información desde Firestore.
 */
class PerfilViewModel(
    private val repo: FirebaseRepo = FirebaseRepo()
) : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val jugador: Jugadores? = null,
        val error: String? = null
    )

    private val _ui = MutableStateFlow(UiState(loading = true))
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    init {
        cargarPerfil()
    }

    /** Obtiene los datos del jugador actual. */
    fun cargarPerfil() = viewModelScope.launch {
        val uid = repo.currentUid ?: return@launch
        _ui.update { it.copy(loading = true, error = null) }

        runCatching {
            repo.getJugador(uid)
        }.onSuccess { jugador ->
            _ui.update { it.copy(loading = false, jugador = jugador, error = null) }
        }.onFailure { e ->
            _ui.update { it.copy(loading = false, error = e.message ?: "Error al cargar el perfil") }
        }
    }

    /** Permite actualizar la información del jugador. */
    fun actualizarPerfil(nombre: String, apellido: String, telefono: String?) = viewModelScope.launch {
        val jugadorActual = _ui.value.jugador ?: return@launch
        _ui.update { it.copy(loading = true) }

        runCatching {
            repo.createOrUpdateJugador(
                jugadorActual.copy(
                    nombre_jugador = nombre,
                    apellido_jugador = apellido,
                    telefono_jugador = telefono ?: jugadorActual.telefono_jugador
                )
            )
        }.onSuccess {
            cargarPerfil()
        }.onFailure { e ->
            _ui.update { it.copy(loading = false, error = e.message) }
        }
    }
}
