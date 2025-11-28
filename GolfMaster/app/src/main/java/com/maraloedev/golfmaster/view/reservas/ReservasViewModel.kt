package com.maraloedev.golfmaster.view.reservas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.maraloedev.golfmaster.model.FirebaseRepo
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
 */
class ReservasViewModel(
    private val repo: FirebaseRepo = FirebaseRepo(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

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

    init {
        cargarReservas()
    }

    // ============================================================
    // 🔄 CARGAR RESERVAS (donde el usuario participa)
    // ============================================================
    fun cargarReservas() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _loading.value = true
            runCatching { repo.getReservasPorJugador(uid) }
                .onSuccess {
                    _reservas.value = it.sortedByDescending { r -> r.fecha?.seconds }
                }
                .onFailure {
                    _reservas.value = emptyList()
                    _error.value = it.message ?: "Error al cargar reservas"
                }
            _loading.value = false
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
                    .filter { it.id != currentUid } // ya excluimos al actual
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
    // 🟩 CREAR NUEVA RESERVA SIMPLE (si en algún sitio la usas)
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
                cargarReservas()
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
                cargarReservas()
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
                cargarReservas()
            }.onFailure {
                _error.value = it.message ?: "Error al eliminar reserva"
            }
            _loading.value = false
        }
    }

    // ============================================================
    // 🟩 CREAR RESERVA + INVITACIONES
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
                // 1️⃣ Participantes: creador + invitados
                val participantesIds = buildList {
                    add(uid)
                    addAll(jugadores.map { it.id })
                }

                // 2️⃣ Texto para mostrar en UI
                val nombresJugadores = if (jugadores.isEmpty()) {
                    "Solo"
                } else {
                    jugadores.joinToString { it.nombre_jugador }
                }

                // 3️⃣ Crear la reserva
                val reserva = Reserva(
                    id = "",
                    usuarioId = uid,
                    fecha = fecha,
                    hora = fecha,       // usas misma Timestamp para hora
                    recorrido = hoyos,  // si luego tienes otro campo de recorrido real, se cambia aquí
                    hoyos = hoyos,
                    jugadores = nombresJugadores,
                    participantesIds = participantesIds
                )

                val idReserva = repo.crearReserva(reserva)

                // 4️⃣ Crear invitaciones para los demás
                jugadores.forEach { j ->
                    repo.crearInvitacion(de = uid, para = j.id, reservaId = idReserva)
                }

                // 5️⃣ Recargar reservas del usuario
                cargarReservas()
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al crear reserva con invitaciones"
            } finally {
                _loading.value = false
            }
        }
    }
}
