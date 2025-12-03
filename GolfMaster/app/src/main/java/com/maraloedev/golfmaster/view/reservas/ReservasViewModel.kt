package com.maraloedev.golfmaster.view.reservas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.maraloedev.golfmaster.model.FirebaseRepo
import com.maraloedev.golfmaster.model.Invitacion
import com.maraloedev.golfmaster.model.Jugadores
import com.maraloedev.golfmaster.model.Reserva
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de gestionar el módulo de reservas:
 *  - Escucha en tiempo real las reservas del usuario.
 *  - Escucha invitaciones pendientes.
 *  - Crea reservas e invitaciones asociadas.
 *  - Permite eliminar reservas y responder invitaciones.
 */
class ReservasViewModel(
    private val repo: FirebaseRepo = FirebaseRepo(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // Listeners en tiempo real para reservas e invitaciones
    private var reservasListener: ListenerRegistration? = null
    private var invitacionesListener: ListenerRegistration? = null

    // Estados principales
    private val _reservas = MutableStateFlow<List<Reserva>>(emptyList())
    val reservas = _reservas.asStateFlow()

    private val _jugadores = MutableStateFlow<List<Jugadores>>(emptyList())
    val jugadores = _jugadores.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _loadingJugadores = MutableStateFlow(false)
    val loadingJugadores = _loadingJugadores.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // Invitaciones pendientes asociadas al usuario autenticado
    private val _invitacionesPendientes = MutableStateFlow<List<Invitacion>>(emptyList())
    val invitacionesPendientes = _invitacionesPendientes.asStateFlow()

    init {
        cargarReservas()
        cargarInvitacionesPendientes()
    }

    /**
     * Escucha las reservas en las que participa el usuario.
     */
    fun cargarReservas() {
        val uid = auth.currentUser?.uid ?: return
        _loading.value = true

        // Eliminar listener previo si existía
        reservasListener?.remove()

        reservasListener = db.collection("reservas")
            .whereArrayContains("participantesIds", uid)
            .addSnapshotListener { snaps, e ->
                if (e != null) {
                    _error.value = e.message ?: "Error al escuchar reservas"
                    _reservas.value = emptyList()
                    _loading.value = false
                    return@addSnapshotListener
                }

                val lista = snaps?.documents
                    ?.mapNotNull { it.toObject(Reserva::class.java) }
                    .orEmpty()

                // Se ordenan por fecha descendente (últimas primero)
                _reservas.value = lista.sortedByDescending { it.fecha?.seconds ?: 0 }
                _loading.value = false
            }
    }

    /**
     * Escucha las invitaciones pendientes dirigidas al usuario actual.
     */
    fun cargarInvitacionesPendientes() {
        val uid = auth.currentUser?.uid ?: return

        invitacionesListener?.remove()

        invitacionesListener = db.collection("invitaciones")
            .whereEqualTo("paraId", uid)
            .whereEqualTo("estado", "pendiente")
            .addSnapshotListener { snaps, e ->
                if (e != null) {
                    _error.value = e.message ?: "Error al escuchar invitaciones"
                    _invitacionesPendientes.value = emptyList()
                    return@addSnapshotListener
                }

                val lista = snaps?.documents
                    ?.mapNotNull { doc ->
                        doc.toObject(Invitacion::class.java)?.copy(id = doc.id)
                    }
                    .orEmpty()

                _invitacionesPendientes.value =
                    lista.sortedByDescending { it.creadaEn.seconds }
            }
    }

    /**
     * Búsqueda de jugadores por nombre (excluyendo al usuario actual).
     */
    fun buscarJugadores(nombre: String) {
        if (nombre.isBlank()) {
            _jugadores.value = emptyList()
            return
        }

        val currentUid = auth.currentUser?.uid
        viewModelScope.launch {
            _loadingJugadores.value = true
            runCatching {
                repo.buscarJugadoresPorNombre(nombre)
                    .filter { it.id != currentUid }
            }.onSuccess { lista ->
                _jugadores.value = lista
            }.onFailure { e ->
                _jugadores.value = emptyList()
                _error.value = e.message ?: "Error al buscar jugadores"
            }
            _loadingJugadores.value = false
        }
    }

    /**
     * Elimina una reserva por su identificador.
     */
    fun eliminarReserva(id: String) {
        if (id.isBlank()) return
        viewModelScope.launch {
            _loading.value = true
            runCatching {
                repo.eliminarReserva(id)
            }.onFailure { e ->
                _error.value = e.message ?: "Error al eliminar reserva"
            }
            _loading.value = false
        }
    }

    /**
     * Crea una reserva e invitaciones asociadas.
     *
     * - La reserva se crea inicialmente solo con el creador como participante.
     * - Las invitaciones quedan como "pendiente" hasta que el destinatario acepte.
     */
    fun crearReservaConInvitaciones(
        fecha: Timestamp?,
        hoyos: String?,
        jugadores: List<Jugadores>
    ) {
        val uid = auth.currentUser?.uid ?: return
        if (fecha == null || hoyos.isNullOrBlank()) return

        viewModelScope.launch {
            _loading.value = true
            try {
                val participantesIds = listOf(uid)

                val nombresJugadores = if (jugadores.isEmpty()) {
                    "Solo"
                } else {
                    jugadores.joinToString { it.nombre_jugador }
                }

                val reserva = Reserva(
                    id = "",
                    usuarioId = uid,
                    fecha = fecha,
                    hora = fecha,
                    recorrido = hoyos,
                    hoyos = hoyos,
                    jugadores = nombresJugadores,
                    participantesIds = participantesIds
                )

                val idReserva = repo.crearReserva(reserva)

                // Por cada jugador invitado se genera un documento "invitación"
                jugadores.forEach { j ->
                    repo.crearInvitacion(
                        de = uid,
                        para = j.id,
                        reservaId = idReserva,
                        fecha = fecha
                    )
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al crear reserva con invitaciones"
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Responde a una invitación (aceptar o rechazar).
     *
     * - Si se acepta, se añade el usuario a la reserva y se marca "aceptada".
     * - Si se rechaza, solo se actualiza el estado de la invitación.
     */
    fun responderInvitacion(
        invitacion: Invitacion,
        aceptar: Boolean
    ) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                if (aceptar) {
                    repo.anadirParticipanteAReserva(invitacion.reservaId, uid)
                    repo.actualizarEstadoInvitacion(invitacion.id, "aceptada")
                } else {
                    repo.actualizarEstadoInvitacion(invitacion.id, "rechazada")
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al responder invitación"
            }
        }
    }

    /**
     * Elimina los listeners en tiempo real al destruirse el ViewModel.
     */
    override fun onCleared() {
        reservasListener?.remove()
        invitacionesListener?.remove()
        super.onCleared()
    }
}
