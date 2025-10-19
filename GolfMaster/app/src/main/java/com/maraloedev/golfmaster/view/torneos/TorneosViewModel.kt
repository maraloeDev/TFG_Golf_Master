package com.maraloedev.golfmaster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maraloedev.golfmaster.model.FirebaseRepo
import com.maraloedev.golfmaster.model.Torneos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de gestionar los Torneos/Eventos de Golf.
 * Controla la carga, creación y manejo de errores de la lista de torneos.
 */
class TorneosViewModel(
    private val repo: FirebaseRepo = FirebaseRepo()
) : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val torneos: List<Torneos> = emptyList(),
        val error: String? = null
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    /** Carga todos los torneos desde Firestore. */
    fun cargarTorneos() = viewModelScope.launch {
        _ui.update { it.copy(loading = true, error = null) }
        runCatching {
            repo.getTorneos()
        }.onSuccess { lista ->
            _ui.update { it.copy(loading = false, torneos = lista, error = null) }
        }.onFailure { e ->
            _ui.update { it.copy(loading = false, error = e.message ?: "Error al cargar torneos") }
        }
    }

    /** Crea un nuevo torneo en Firestore. */
    fun crearTorneo(torneo: Torneos) = viewModelScope.launch {
        _ui.update { it.copy(loading = true, error = null) }
        runCatching {
            repo.crearTorneo(torneo)
        }.onSuccess {
            cargarTorneos() // recarga automáticamente
        }.onFailure { e ->
            _ui.update { it.copy(loading = false, error = e.message ?: "Error al crear torneo") }
        }
    }
}
