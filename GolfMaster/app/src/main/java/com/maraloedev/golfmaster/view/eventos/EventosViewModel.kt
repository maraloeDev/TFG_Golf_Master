package com.maraloedev.golfmaster.view.eventos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.maraloedev.golfmaster.model.FirebaseRepo
import com.maraloedev.golfmaster.model.Torneos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
            .onSuccess { lista -> _torneos.value = lista.sortedBy { it.fecha_inicial_torneo?.toDate() } }
            .onFailure { e -> _error.value = e.message }
        _loading.value = false
    }

    fun crearTorneo(
        nombre: String,
        tipo: String,
        premio: String,
        fechaInicio: Timestamp,
        fechaFinal: Timestamp,
        lugar: String,
        formato: String,
        imagenUrl: String? = null
    ) = viewModelScope.launch {
        if (nombre.isBlank() || tipo.isBlank() || lugar.isBlank() || formato.isBlank()) {
            _error.value = "Todos los campos obligatorios deben completarse"
            return@launch
        }

        _loading.value = true
        runCatching {
            val torneo = Torneos(
                nombre_torneo = nombre.trim(),
                tipo_torneo = tipo.trim(),
                premio_torneo = premio.trim(),
                fecha_inicial_torneo = fechaInicio,
                fecha_final_torneo = fechaFinal,
                lugar_torneo = lugar.trim(),
                formato_torneo = formato.trim(),
            )
            repo.crearTorneo(torneo)
        }.onSuccess { cargarTorneos() }
            .onFailure { _error.value = it.message }
        _loading.value = false
    }
}
