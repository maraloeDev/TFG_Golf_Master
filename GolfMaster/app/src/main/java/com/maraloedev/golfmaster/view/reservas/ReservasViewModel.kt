package com.maraloedev.golfmaster.view.reservas

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ReservasViewModel : ViewModel() {

    private val _state = mutableStateOf(ReservasState())
    val state: State<ReservasState> = _state

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var pasadasListener: ListenerRegistration? = null
    private var triedPasadasFallback = false

    init {
        cargarJugadores()
    }

    override fun onCleared() {
        super.onCleared()
        pasadasListener?.remove()
    }

    private fun cargarJugadores() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                jugadores = listOf(
                    Jugador(nombre = "Carlos Sánchez", detalle = "Socio #12345", seleccionado = true),
                    Jugador(nombre = "Invitado", invitado = true)
                )
            )
        }
    }

    // --- Actualizaciones de campos ---
    fun seleccionarPestaña(pestaña: String) {
        _state.value = _state.value.copy(pestañaSeleccionada = pestaña)
        if (pestaña == "Pasadas") {
            escucharReservasPasadas()
        } else {
            pasadasListener?.remove()
            pasadasListener = null
        }
    }
    fun seleccionarFecha(fecha: String) { _state.value = _state.value.copy(fechaJuego = fecha) }
    fun seleccionarHora(hora: Int, minuto: Int) {
        val horaFormateada = String.format(Locale.getDefault(), "%02d:%02d", hora, minuto)
        _state.value = _state.value.copy(horaJuego = horaFormateada)
    }
    fun seleccionarRecorrido(valor: String) { _state.value = _state.value.copy(recorrido = valor) }
    fun seleccionarNumeroJugadores(valor: String) { _state.value = _state.value.copy(numJugadores = valor) }

    fun seleccionarJugador(nombre: String) {
        _state.value = _state.value.copy(
            jugadores = _state.value.jugadores.map {
                if (it.nombre == nombre)
                    it.copy(seleccionado = !it.seleccionado)
                else it
            }
        )
    }

    fun añadirJugador(nombre: String) {
        if (nombre.isBlank()) return
        val lista = _state.value.jugadores.toMutableList()
        if (lista.none { it.nombre.equals(nombre, ignoreCase = true) }) {
            lista.add(Jugador(nombre = nombre))
            _state.value = _state.value.copy(jugadores = lista)
        }
    }

    fun buscarYAgregarAmigo(termino: String, onComplete: (Boolean, String?) -> Unit) {
        if (termino.isBlank()) {
            onComplete(false, "Escribe un nombre o correo")
            return
        }
        val jugadoresRef = db.collection("jugadores")
        val esCorreo = termino.contains("@")
        val campo = if (esCorreo) "correo_jugador" else "nombre_jugador"
        jugadoresRef
            .whereEqualTo(campo, termino)
            .limit(1)
            .get()
            .addOnSuccessListener { snaps ->
                val doc = snaps.documents.firstOrNull()
                if (doc == null) {
                    onComplete(false, "No se encontró el jugador")
                } else {
                    val nombre = doc.getString("nombre_jugador") ?: termino
                    val detalle = doc.getString("correo_jugador") ?: ""
                    val yaExiste = _state.value.jugadores.any { it.nombre.equals(nombre, ignoreCase = true) }
                    if (!yaExiste) {
                        val nueva = _state.value.jugadores.toMutableList()
                        nueva.add(Jugador(nombre = nombre, detalle = detalle))
                        _state.value = _state.value.copy(jugadores = nueva)
                    }
                    onComplete(true, null)
                }
            }
            .addOnFailureListener { e -> onComplete(false, e.message ?: "Error al buscar jugador") }
    }

    // --- Bloquear la reserva temporalmente (3 minutos) ---
    fun bloquearReserva() {
        if (_state.value.enProgreso) return

        viewModelScope.launch {
            _state.value = _state.value.copy(
                enProgreso = true,
                tiempoRestante = 180,
                bloqueada = true
            )

            guardarReservaEnFirestore("pendiente")

            while (_state.value.tiempoRestante > 0 && _state.value.bloqueada) {
                delay(1000)
                _state.value = _state.value.copy(tiempoRestante = _state.value.tiempoRestante - 1)
            }

            if (_state.value.bloqueada) {
                // Expiró el tiempo sin confirmar
                _state.value = _state.value.copy(enProgreso = false, bloqueada = false)
            }
        }
    }

    fun confirmarReserva() {
        if (!_state.value.bloqueada) return

        viewModelScope.launch {
            guardarReservaEnFirestore("confirmada")
            _state.value = _state.value.copy(
                enProgreso = false,
                bloqueada = false,
                confirmacionGuardada = true
            )
        }
    }

    private fun guardarReservaEnFirestore(estado: String) {
        val uid = auth.currentUser?.uid
        val ahora = Timestamp.now()
        val reserva = hashMapOf(
            "userId" to uid,
            "fecha" to _state.value.fechaJuego,
            "hora" to _state.value.horaJuego,
            "recorrido" to _state.value.recorrido,
            "numJugadores" to _state.value.numJugadores,
            "jugadores" to _state.value.jugadores.filter { it.seleccionado }.map { it.nombre },
            "estado" to estado,
            // Timestamp nativo para ordenar/filtrar correctamente
            "timestamp" to ahora,
            // String para compatibilidad visual
            "timestampStr" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )

        db.collection("reservas")
            .add(reserva)
            .addOnSuccessListener {
                if (estado == "confirmada")
                    _state.value = _state.value.copy(confirmacionGuardada = true)
            }
            .addOnFailureListener {
                _state.value = _state.value.copy(errorGuardado = it.message ?: "Error desconocido")
            }
    }

    private fun escucharReservasPasadas() {
        _state.value = _state.value.copy(loadingPasadas = true, errorPasadas = null)
        val uid = auth.currentUser?.uid
        var q: Query = db.collection("reservas")
            .whereEqualTo("estado", "confirmada")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .whereLessThan("timestamp", Timestamp.now())
        if (uid != null) q = q.whereEqualTo("userId", uid)

        pasadasListener?.remove()
        pasadasListener = q.addSnapshotListener { snaps, e ->
            if (e != null) {
                // Si falla por índice o cualquier motivo, hacemos fallback una sola vez
                if (!triedPasadasFallback) {
                    triedPasadasFallback = true
                    pasadasListener?.remove()
                    val qFallback = db.collection("reservas")
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .whereLessThan("timestamp", Timestamp.now())
                    pasadasListener = qFallback.addSnapshotListener { snaps2, e2 ->
                        if (e2 != null) {
                            _state.value = _state.value.copy(loadingPasadas = false, errorPasadas = e2.message ?: "Error al escuchar reservas")
                            return@addSnapshotListener
                        }
                        actualizarPasadasDesdeSnaps(snaps2, uid)
                    }
                    return@addSnapshotListener
                } else {
                    _state.value = _state.value.copy(loadingPasadas = false, errorPasadas = e.message ?: "Error al escuchar reservas")
                    return@addSnapshotListener
                }
            }
            actualizarPasadasDesdeSnaps(snaps, uid)
        }
    }

    private fun actualizarPasadasDesdeSnaps(snaps: com.google.firebase.firestore.QuerySnapshot?, uid: String?) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val lista = snaps?.documents?.mapNotNull { doc ->
            val estado = doc.getString("estado") ?: return@mapNotNull null
            val userDoc = doc.getString("userId")
            if (uid != null && userDoc != uid) return@mapNotNull null
            val tsObj = doc.get("timestamp")
            val ts: Date? = when (tsObj) {
                is Timestamp -> tsObj.toDate()
                is String -> runCatching { sdf.parse(tsObj) }.getOrNull()
                else -> null
            }
            val fecha = doc.getString("fecha") ?: ""
            val hora = doc.getString("hora") ?: ""
            val recorrido = doc.getString("recorrido") ?: ""
            val numJug = doc.getString("numJugadores") ?: ""
            if (estado == "confirmada" && ts != null && ts.before(Date())) {
                ReservaGuardada(
                    fecha = fecha,
                    hora = hora,
                    recorrido = recorrido,
                    numJugadores = numJug,
                    estado = estado,
                    timestamp = sdf.format(ts)
                )
            } else null
        } ?: emptyList()

        _state.value = _state.value.copy(
            reservasPasadas = lista,
            loadingPasadas = false,
            errorPasadas = null
        )
    }

    fun cerrarConfirmacion() {
        _state.value = _state.value.copy(confirmacionGuardada = false)
    }
}

// Estado y modelos

data class ReservasState(
    val pestañaSeleccionada: String = "Proximas",
    val fechaJuego: String = "24/10/2025",
    val recorrido: String = "9 hoyos",
    val numJugadores: String = "1",
    val horaJuego: String = "--:--",
    val jugadores: List<Jugador> = emptyList(),
    val enProgreso: Boolean = false,
    val bloqueada: Boolean = false,
    val tiempoRestante: Int = 0,
    val confirmacionGuardada: Boolean = false,
    val errorGuardado: String? = null,
    // "Pasadas"
    val reservasPasadas: List<ReservaGuardada> = emptyList(),
    val loadingPasadas: Boolean = false,
    val errorPasadas: String? = null
)

data class Jugador(
    val nombre: String,
    val detalle: String = "",
    val invitado: Boolean = false,
    val seleccionado: Boolean = false
)

data class ReservaGuardada(
    val fecha: String,
    val hora: String,
    val recorrido: String,
    val numJugadores: String,
    val estado: String,
    val timestamp: String
)
