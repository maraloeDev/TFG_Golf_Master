package com.maraloedev.golfmaster.view.eventos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.maraloedev.golfmaster.model.Evento
import com.maraloedev.golfmaster.model.FirebaseRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventosViewModel(
    private val repo: FirebaseRepo = FirebaseRepo()
) : ViewModel() {

    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos = _eventos.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // ================== 🔹 Cargar eventos (sólo del usuario) ==================
    fun cargarEventos() {
        val uid = repo.currentUid ?: return   // 👈 sin usuario logueado, no cargamos nada

        viewModelScope.launch {
            _loading.value = true
            runCatching { repo.getEventosDeUsuario(uid) }   // 👈 usamos el nuevo método
                .onSuccess { lista ->
                    _eventos.value = lista.sortedBy { e -> e.fechaInicio?.seconds }
                }
                .onFailure { _error.value = it.message }
            _loading.value = false
        }
    }

    // ================== 🔹 Crear nuevo evento ==================
    fun crearEvento(
        nombre: String,
        tipo: String?,
        precioSocio: String,
        precioNoSocio: String,
        fechaInicio: Timestamp?,
        fechaFin: Timestamp?
    ) {
        viewModelScope.launch {
            if (nombre.isBlank() || tipo == null || fechaInicio == null || fechaFin == null) return@launch

            val nuevo = Evento(
                nombre = nombre,
                tipo = tipo,
                precioSocio = precioSocio.toDoubleOrNull(),
                precioNoSocio = precioNoSocio.toDoubleOrNull(),
                fechaInicio = fechaInicio,
                fechaFin = fechaFin
                // 👈 creadorId se setea en el repo, no aquí
            )

            runCatching { repo.addEvento(nuevo) }
                .onSuccess { cargarEventos() }
                .onFailure { _error.value = it.message }
        }
    }

    // ================== 🔹 Inscribirse a un evento (varios usuarios) ==================
    fun inscribirseEnEvento(evento: Evento) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            if (evento.inscritos.contains(uid)) return@launch

            val eventoId = evento.id ?: return@launch

            runCatching { repo.inscribirseEnEvento(eventoId, uid) }
                .onSuccess { cargarEventos() }
                .onFailure { _error.value = it.message }
        }
    }

    // 🔹 Editar evento
    fun actualizarEvento(evento: Evento) {
        viewModelScope.launch {
            runCatching { repo.updateEvento(evento) }
                .onSuccess { cargarEventos() }
                .onFailure { _error.value = it.message }
        }
    }

    // 🔹 Eliminar evento
    fun eliminarEvento(id: String) {
        viewModelScope.launch {
            runCatching { repo.deleteEvento(id) }
                .onSuccess { cargarEventos() }
                .onFailure { _error.value = it.message }
        }
    }
}
