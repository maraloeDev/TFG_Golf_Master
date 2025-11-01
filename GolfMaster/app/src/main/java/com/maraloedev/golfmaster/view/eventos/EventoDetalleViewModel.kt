package com.maraloedev.golfmaster.view.eventos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maraloedev.golfmaster.model.FirebaseRepo
import com.maraloedev.golfmaster.model.Torneos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventoDetalleViewModel(
    val repo: FirebaseRepo = FirebaseRepo()
) : ViewModel() {

    private val _torneo = MutableStateFlow<Torneos?>(null)
    val torneo = _torneo.asStateFlow()

    private val _inscrito = MutableStateFlow(false)
    val inscrito = _inscrito.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun cargarTorneo(id: String) = viewModelScope.launch {
        _loading.value = true
        runCatching {
            repo.getTorneoById(id)
        }.onSuccess {
            _torneo.value = it
        }.onFailure {
            _error.value = it.message
        }
        _loading.value = false
    }

    suspend fun enviarSolicitudInscripcion(torneoId: String, usuarioId: String): Result<Unit> {
        return runCatching {
            repo.enviarSolicitudInscripcion(torneoId, usuarioId)
            _inscrito.value = true
        }.onFailure {
            _error.value = it.message
        }
    }

    fun inscribirse(usuarioId: String) = viewModelScope.launch {
        val actual = _torneo.value ?: return@launch
        _loading.value = true
        runCatching {
            repo.inscribirseEnTorneo(actual, usuarioId)
        }.onSuccess {
            _inscrito.value = true
        }.onFailure {
            _error.value = it.message
        }
        _loading.value = false
    }
}
