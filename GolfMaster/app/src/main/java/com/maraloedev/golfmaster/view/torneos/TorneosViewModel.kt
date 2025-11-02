package com.maraloedev.golfmaster.view.torneos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.maraloedev.golfmaster.model.FirebaseRepo
import com.maraloedev.golfmaster.model.Torneos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    /** 🔹 Cargar torneos desde Firestore */
    fun cargarTorneos() = viewModelScope.launch {
        _ui.update { it.copy(loading = true, error = null) }
        runCatching { repo.getTorneos() }
            .onSuccess { lista ->
                _ui.update { it.copy(loading = false, torneos = lista, error = null) }
            }
            .onFailure { e ->
                _ui.update { it.copy(loading = false, error = e.message ?: "Error al cargar torneos") }
            }
    }

    /** 🔹 Crear un torneo (pasando el objeto directamente) */
    fun crearTorneo(torneo: Torneos) = viewModelScope.launch {
        _ui.update { it.copy(loading = true, error = null) }
        runCatching { repo.crearTorneo(torneo) }
            .onSuccess { cargarTorneos() }
            .onFailure { e ->
                _ui.update { it.copy(loading = false, error = e.message ?: "Error al crear torneo") }
            }
    }

    /** 🔹 Crear torneo desde parámetros sueltos (para TorneosScreen) */
    fun crearTorneoCompleto(
        nombre: String,
        tipo: String,
        premio: String,
        lugar: String,
        formato: String,
        inicio: Timestamp,
        fin: Timestamp,
        onSuccess: (Torneos) -> Unit
    ) = viewModelScope.launch {
        val nuevo = Torneos(
            nombre_torneo = nombre,
            tipo_torneo = tipo,
            premio_torneo = premio,
            lugar_torneo = lugar,
            formato_torneo = formato,
            fecha_inicial_torneo = inicio,
            fecha_final_torneo = fin
        )

        runCatching { repo.crearTorneo(nuevo) }
            .onSuccess { onSuccess(nuevo) }
            .onFailure { e ->
                _ui.update { it.copy(error = e.message ?: "Error al crear torneo") }
            }
    }
}
