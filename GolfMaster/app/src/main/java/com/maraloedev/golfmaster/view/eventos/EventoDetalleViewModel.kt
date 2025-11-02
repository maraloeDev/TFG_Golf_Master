package com.maraloedev.golfmaster.view.eventos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maraloedev.golfmaster.model.FirebaseRepo
import com.maraloedev.golfmaster.model.Torneos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventoDetalleViewModel(
    private val repo: FirebaseRepo = FirebaseRepo()
) : ViewModel() {

    private val _torneo = MutableStateFlow<Torneos?>(null)
    val torneo = _torneo.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun cargarTorneo(id: String) = viewModelScope.launch {
        _loading.value = true
        runCatching { repo.getTorneoById(id) }
            .onSuccess { _torneo.value = it }
            .onFailure { _error.value = it.message }
        _loading.value = false
    }

}
