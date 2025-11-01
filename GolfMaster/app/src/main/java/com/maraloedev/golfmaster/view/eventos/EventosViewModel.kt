package com.maraloedev.golfmaster.viewmodel

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

    private val _proximos = MutableStateFlow<List<Torneos>>(emptyList())
    val proximos = _proximos.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun cargar() = viewModelScope.launch {
        _loading.value = true
        runCatching { repo.getTorneos() }
            .onSuccess { lista -> _proximos.value = lista.sortedBy { it.fecha_inicial_torneo?.toDate() } }
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
        _loading.value = true
        runCatching {
            val torneo = Torneos(
                nombre_torneo = nombre,
                tipo_torneo = tipo,
                premio_torneo = premio,
                fecha_inicial_torneo = fechaInicio,
                fecha_final_torneo = fechaFinal,
                lugar_torneo = lugar,
                formato_torneo = formato,
                imagen_url = imagenUrl
            )
            repo.crearTorneo(torneo)
        }.onSuccess { cargar() }
            .onFailure { _error.value = it.message }
        _loading.value = false
    }
}
