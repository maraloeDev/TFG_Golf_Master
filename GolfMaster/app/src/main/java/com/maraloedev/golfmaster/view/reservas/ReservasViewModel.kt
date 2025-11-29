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
 * ViewModel de Reservas
 * -------------------------------------------------------
 * Gestiona las reservas del usuario logueado:
 *  - Carga, creación, actualización y eliminación.
 *  - Búsqueda de jugadores (excluye el actual).
 *  - Invitaciones pendientes (aceptar / rechazar).
 */
class ReservasViewModel(
    private val repo: FirebaseRepo = FirebaseRepo(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    // ============================================================
    // FIRESTORE + LISTENERS
    // ============================================================
    private val db = FirebaseFirestore.getInstance()
    private var reservasListener: ListenerRegistration? = null
    private var invitacionesListener: ListenerRegistration? = null

    // ============================================================
    // ESTADOS
    // ============================================================

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

    // 🔔 Invitaciones pendientes para el usuario logueado
    private val _invitacionesPendientes = MutableStateFlow<List<Invitacion>>(emptyList())
    val invitacionesPendientes = _invitacionesPendientes.asStateFlow()

    init {
        // 👇 Se enganchan los listeners en tiempo real
        cargarReservas()
        cargarInvitacionesPendientes()
    }

    // ============================================================
    // 🔄 CARGAR RESERVAS (en tiempo real, donde el usuario participa)
    // ============================================================
    fun cargarReservas() {
        val uid = auth.currentUser?.uid ?: return
        _loading.value = true

        // Quitar listener anterior si existía
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

                _reservas.value = lista.sortedByDescending { it.fecha?.seconds ?: 0 }
                _loading.value = false
            }
    }

    // ============================================================
    // 🔔 CARGAR INVITACIONES PENDIENTES (en tiempo real)
    // ============================================================
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

    // ============================================================
    // 🔍 BUSCAR JUGADORES (EXCLUYENDO AL USUARIO ACTUAL)
    // ============================================================
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
                    .filter { it.id != currentUid } // excluimos al actual
            }.onSuccess {
                _jugadores.value = it
            }.onFailure {
                _jugadores.value = emptyList()
                _error.value = it.message ?: "Error al buscar jugadores"
            }
            _loadingJugadores.value = false
        }
    }

    // ============================================================
    // 🟩 CREAR NUEVA RESERVA SIMPLE (si la usas en otro sitio)
    // ============================================================
    fun crearReserva(
        fecha: Timestamp?,
        hora: Timestamp?,
        recorrido: String?,
        hoyos: String?,
        jugadores: String?
    ) {
        val uid = auth.currentUser?.uid ?: return
        if (fecha == null || hora == null || recorrido.isNullOrBlank() || jugadores.isNullOrBlank()) return

        viewModelScope.launch {
            _loading.value = true
            val reserva = Reserva(
                id = "",
                usuarioId = uid,
                fecha = fecha,
                hora = hora,
                recorrido = recorrido,
                hoyos = hoyos,
                jugadores = jugadores,
                participantesIds = listOf(uid) // solo el creador
            )
            runCatching {
                repo.crearReserva(reserva)
                // ❌ ya no hace falta llamar a cargarReservas():
                // el listener se enterará solo del cambio.
            }.onFailure {
                _error.value = it.message ?: "Error al crear reserva"
            }
            _loading.value = false
        }
    }

    // ============================================================
    // ✏️ ACTUALIZAR RESERVA
    // ============================================================
    fun actualizarReserva(
        id: String,
        fecha: Timestamp?,
        hora: Timestamp?,
        recorrido: String?,
        hoyos: String?,
        jugadores: String?
    ) {
        if (id.isBlank()) return
        viewModelScope.launch {
            _loading.value = true
            val nuevosDatos = mutableMapOf<String, Any>()
            fecha?.let { nuevosDatos["fecha"] = it }
            hora?.let { nuevosDatos["hora"] = it }
            recorrido?.let { nuevosDatos["recorrido"] = it }
            hoyos?.let { nuevosDatos["hoyos"] = it }
            jugadores?.let { nuevosDatos["jugadores"] = it }

            runCatching {
                repo.actualizarReserva(id, nuevosDatos)
                // El listener de reservas se actualiza solo
            }.onFailure {
                _error.value = it.message ?: "Error al actualizar reserva"
            }
            _loading.value = false
        }
    }

    // ============================================================
    // ❌ ELIMINAR RESERVA
    // ============================================================
    fun eliminarReserva(id: String) {
        if (id.isBlank()) return
        viewModelScope.launch {
            _loading.value = true
            runCatching {
                repo.eliminarReserva(id)
                // El listener de reservas se actualiza solo
            }.onFailure {
                _error.value = it.message ?: "Error al eliminar reserva"
            }
            _loading.value = false
        }
    }

    // ============================================================
    // 🟩 CREAR RESERVA + INVITACIONES
    //  - La reserva se crea solo con el creador en participantesIds
    //  - Los invitados no ven la reserva hasta que acepten
    // ============================================================
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

                // Crear invitaciones PENDIENTES con nombreDe + fecha
                jugadores.forEach { j ->
                    repo.crearInvitacion(
                        de = uid,
                        para = j.id,
                        reservaId = idReserva,
                        fecha = fecha
                    )
                }

                // El listener actualizará la lista solo
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al crear reserva con invitaciones"
            } finally {
                _loading.value = false
            }
        }
    }



    // ============================================================
    // ✅ RESPONDER INVITACIÓN (aceptar / rechazar)
    // ============================================================
    fun responderInvitacion(
        invitacion: Invitacion,
        aceptar: Boolean
    ) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                if (aceptar) {
                    // Añadir al usuario a la reserva
                    repo.anadirParticipanteAReserva(invitacion.reservaId, uid)
                    // Marcar invitación como aceptada
                    repo.actualizarEstadoInvitacion(invitacion.id, "aceptada")
                    // El listener de reservas traerá la nueva reserva al usuario
                } else {
                    // Solo cambiar estado a rechazada
                    repo.actualizarEstadoInvitacion(invitacion.id, "rechazada")
                }

                // El listener de invitaciones actualizará _invitacionesPendientes solo
                // pero si quieres forzar, puedes volver a llamar:
                // cargarInvitacionesPendientes()
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al responder invitación"
            }
        }
    }

    // ============================================================
    // 🔚 LIMPIAR LISTENERS
    // ============================================================
    override fun onCleared() {
        reservasListener?.remove()
        invitacionesListener?.remove()
        super.onCleared()
    }
}
