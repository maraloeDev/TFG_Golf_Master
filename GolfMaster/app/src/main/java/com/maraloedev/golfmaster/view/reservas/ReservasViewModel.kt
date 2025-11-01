package com.maraloedev.golfmaster.view.reservas

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.maraloedev.golfmaster.model.Jugadores
import com.maraloedev.golfmaster.model.Reserva
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReservasViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _reservas = MutableStateFlow<List<Reserva>>(emptyList())
    val reservas = _reservas.asStateFlow()

    private val _jugadores = MutableStateFlow<List<Jugadores>>(emptyList())
    val jugadores = _jugadores.asStateFlow()

    // Estado del sheet para edición
    data class UiSheet(
        val visible: Boolean = false,
        val editando: Boolean = false,
        val idEdicion: String? = null,
        val hoyos: Int = 9,
        val fecha: String = "",
        val hora: String = "",
        val numJugadores: Int = 1,
        val invitadosSeleccionados: List<Jugadores> = emptyList()
    )
    private val _uiSheet = MutableStateFlow(UiSheet())
    val uiSheet get() = _uiSheet.value

    private var regCreador: ListenerRegistration? = null
    private var regPart: ListenerRegistration? = null
    private var regJug: ListenerRegistration? = null

    init {
        suscribirReservas()
        cargarJugadores()
    }

    /* --- Firestore listeners --- */
    private fun suscribirReservas() {
        val uid = auth.currentUser?.uid ?: return

        regCreador?.remove()
        regPart?.remove()

        regCreador = db.collection("reservas")
            .whereEqualTo("usuarioId", uid)
            .addSnapshotListener { s, e ->
                if (e != null || s == null) return@addSnapshotListener
                fusionar(s.toObjects(Reserva::class.java))
            }
        regPart = db.collection("reservas")
            .whereArrayContains("participantesIds", uid)
            .addSnapshotListener { s, e ->
                if (e != null || s == null) return@addSnapshotListener
                fusionar(s.toObjects(Reserva::class.java))
            }
    }

    private fun fusionar(nuevas: List<Reserva>) {
        val mapa = _reservas.value.associateBy { it.id }.toMutableMap()
        nuevas.forEach { r -> mapa[r.id] = r }
        _reservas.value = mapa.values.sortedBy { it.fecha }
    }

    private fun cargarJugadores() {
        val uid = auth.currentUser?.uid
        regJug?.remove()
        regJug = db.collection("jugadores")
            .addSnapshotListener { s, e ->
                if (e != null || s == null) return@addSnapshotListener
                val all = s.toObjects(Jugadores::class.java)
                _jugadores.value = all.filter { it.id != uid } // excluir actual
            }
    }

    /* --- Crear / Editar / Eliminar --- */
    fun crearReservaConInvitados(
        base: Reserva,
        invitadosIds: List<String>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return onError("Usuario no autenticado")
        val doc = db.collection("reservas").document()
        val participantes = (listOf(uid) + invitadosIds).distinct()

        val nueva = base.copy(
            id = doc.id,
            usuarioId = uid,
            participantesIds = participantes
        )

        db.collection("reservas").document(doc.id)
            .set(nueva)
            .addOnSuccessListener {
                // Crear notificaciones para invitados
                invitadosIds.forEach { invitadoId ->
                    val nDoc = db.collection("notificaciones").document()
                    val notif = mapOf(
                        "id" to nDoc.id,
                        "receptorId" to invitadoId,
                        "emisorId" to uid,
                        "reservaId" to doc.id,
                        "mensaje" to "Te han invitado a jugar el ${nueva.fecha} a las ${nueva.hora}.",
                        "estado" to "pendiente",
                        "fecha" to System.currentTimeMillis()
                    )
                    db.collection("notificaciones").document(nDoc.id).set(notif)
                }
                onSuccess()
            }
            .addOnFailureListener { e -> onError(e.message ?: "Error al crear reserva") }
    }

    fun actualizarReserva(id: String, nuevosDatos: Map<String, Any>, onSuccess: () -> Unit) {
        db.collection("reservas").document(id)
            .update(nuevosDatos)
            .addOnSuccessListener { onSuccess() }
    }

    fun eliminarReserva(id: String) {
        db.collection("reservas").document(id).delete()
    }

    /* --- UI helpers para edición --- */
    fun mostrarEditor(reserva: Reserva) {
        _uiSheet.value = UiSheet(
            visible = true,
            editando = true,
            idEdicion = reserva.id,
            hoyos = reserva.hoyos,
            fecha = reserva.fecha,
            hora = reserva.hora,
            numJugadores = reserva.numJugadores
        )
    }

    fun cerrarEditor() {
        _uiSheet.value = UiSheet()
    }
}
