package com.maraloedev.golfmaster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.maraloedev.golfmaster.model.FirebaseRepo
import com.maraloedev.golfmaster.model.Torneos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * Gestiona los torneos del club:
 * - Obtiene torneos desde Firebase
 * - Crea nuevos torneos
 */
class EventosViewModel(
    private val repo: FirebaseRepo = FirebaseRepo()
) : ViewModel() {

    val proximos = MutableStateFlow<List<Torneos>>(emptyList())
    val error = MutableStateFlow<String?>(null)
    val loading = MutableStateFlow(false)

    fun cargar() = viewModelScope.launch {
        loading.value = true
        runCatching {
            repo.getTorneos()
        }.onSuccess {
            proximos.value = it
        }.onFailure {
            error.value = it.message
        }
        loading.value = false
    }

    fun crearTorneo(
        nombre: String,
        tipo: String,
        premio: String,
        fechaInicio: Timestamp,
        fechaFinal: Timestamp
    ) = viewModelScope.launch {
        loading.value = true
        runCatching {
            val torneo = Torneos(
                nombre_torneo = nombre,
                tipo_torneo = tipo,
                premio_torneo = premio,
                fecha_inicial_torneo = fechaInicio,
                fecha_final_torneo = fechaFinal
            )
            repo.crearTorneo(torneo)
        }.onSuccess {
            cargar()
        }.onFailure {
            error.value = it.message
        }
        loading.value = false
    }
}
