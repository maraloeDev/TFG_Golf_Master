package com.maraloedev.golfmaster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maraloedev.golfmaster.model.FirebaseRepo
import com.maraloedev.golfmaster.model.Notificacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de manejar las notificaciones del usuario.
 */
class NotificacionesViewModel(
    private val repo: FirebaseRepo = FirebaseRepo()
) : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val notificaciones: List<Notificacion> = emptyList(),
        val error: String? = null
    )

    private val _ui = MutableStateFlow(UiState(loading = true))
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    init {
        cargarNotificaciones()
    }

    /** Carga las notificaciones desde Firestore. */
    fun cargarNotificaciones() = viewModelScope.launch {
        _ui.update { it.copy(loading = true, error = null) }
        runCatching {
            repo.getNotificaciones()
        }.onSuccess { lista ->
            _ui.update { it.copy(loading = false, notificaciones = lista, error = null) }
        }.onFailure { e ->
            _ui.update { it.copy(loading = false, error = e.message ?: "Error al cargar notificaciones") }
        }
    }
}
