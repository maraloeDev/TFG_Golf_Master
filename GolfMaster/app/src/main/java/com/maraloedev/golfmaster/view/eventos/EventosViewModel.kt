package com.maraloedev.golfmaster.view.eventos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.maraloedev.golfmaster.model.FirebaseRepo
import com.maraloedev.golfmaster.model.Torneos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Gestiona los torneos del club:
 * - Obtiene torneos desde Firebase
 * - Crea nuevos torneos
 */
class EventosViewModel(
    private val repo: FirebaseRepo = FirebaseRepo()
) : ViewModel() {

    private val _torneos = MutableStateFlow<List<Torneos>>(emptyList())
    val torneos = _torneos.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun cargarTorneos() = viewModelScope.launch {
        _loading.value = true
        runCatching { repo.getTorneos() }
            .onSuccess { lista -> _torneos.value = lista.sortedBy { it.fechaInicio?.toDate() } }
            .onFailure { e -> _error.value = e.message }
        _loading.value = false
    }

    fun crearTorneo(
        nombre: String,
        tipo: String,
        premio: String,
        lugar: String,
        formato: String,
        fechaInicio: Timestamp,
        fechaFinal: Timestamp
    ) = viewModelScope.launch {
        if (nombre.isBlank() || tipo.isBlank() || lugar.isBlank() || formato.isBlank()) {
            _error.value = "Todos los campos obligatorios deben completarse"
            return@launch
        }

        _loading.value = true
        runCatching {
            val torneo = Torneos(
                nombre = nombre.trim(),
                tipo = tipo.trim(),
                premio = premio.trim(),
                lugar = lugar.trim(),
                formato = formato.trim(),
                fechaInicio = fechaInicio,
                fechaFin = fechaFinal
            )
            repo.crearTorneo(torneo)
        }.onSuccess { cargarTorneos() }
            .onFailure { _error.value = it.message }
        _loading.value = false
    }
}
