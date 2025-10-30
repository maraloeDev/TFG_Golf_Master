package com.maraloedev.golfmaster.view.reservas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.maraloedev.golfmaster.model.FirebaseRepo
import com.maraloedev.golfmaster.model.Reservas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReservasViewModel(
    private val repo: FirebaseRepo = FirebaseRepo()
) : ViewModel() {

    private val _reservas = MutableStateFlow<List<Reservas>>(emptyList())
    val reservas: StateFlow<List<Reservas>> get() = _reservas

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    fun cargar() = viewModelScope.launch {
        val uid = repo.currentUid ?: return@launch
        _loading.value = true
        _error.value = null
        runCatching {
            repo.getReservasPorJugador(uid)
        }.onSuccess {
            _reservas.value = it
        }.onFailure {
            _error.value = it.message
        }
        _loading.value = false
    }

    fun crearReserva() = viewModelScope.launch {
        val uid = repo.currentUid ?: return@launch
        runCatching {
            repo.crearReserva(
                Reservas(
                    id_jugador = uid,
                    fecha_reserva = Timestamp.now(),
                    hora_reserva = Timestamp.now(),
                    recorrido_reserva = listOf("18 hoyos"),
                    numero_de_jugadores = "4"
                )
            )
        }.onSuccess { cargar() }
            .onFailure { _error.value = it.message }
    }
}
