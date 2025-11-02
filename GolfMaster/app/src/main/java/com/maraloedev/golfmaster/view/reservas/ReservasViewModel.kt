package com.maraloedev.golfmaster.view.reservas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.maraloedev.golfmaster.model.FirebaseRepo
import com.maraloedev.golfmaster.model.Reserva
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReservasViewModel(
    private val repo: FirebaseRepo = FirebaseRepo()
) : ViewModel() {

    private val _reservas = MutableStateFlow<List<Reserva>>(emptyList())
    val reservas = _reservas.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun cargarReservas(uid: String) = viewModelScope.launch {
        _loading.value = true
        runCatching { repo.getReservasPorJugador(uid) }
            .onSuccess { _reservas.value = it.sortedBy { r -> r.fecha?.toDate() } }
            .onFailure { e -> _error.value = e.message }
        _loading.value = false
    }

    fun crearReservaConInvitados(
        base: Reserva,
        invitadosIds: List<String>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = viewModelScope.launch {
        try {
            val nueva = base.copy(fechaCreacion = Timestamp.now())
            repo.crearReserva(nueva)
            onSuccess()
        } catch (e: Exception) {
            onError(e.message ?: "Error al crear reserva")
        }
    }
}
