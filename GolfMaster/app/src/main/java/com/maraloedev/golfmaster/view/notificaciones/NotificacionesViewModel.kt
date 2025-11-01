package com.maraloedev.golfmaster.view.notificaciones

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.maraloedev.golfmaster.model.Notificacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificacionesViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _notificaciones = MutableStateFlow<List<Notificacion>>(emptyList())
    val notificaciones = _notificaciones.asStateFlow()

    private var listener: ListenerRegistration? = null

    init {
        suscribirNotificaciones()
    }

    /** 🔹 Escucha en tiempo real todas las notificaciones del usuario logueado */
    private fun suscribirNotificaciones() {
        listener?.remove()

        val uid = auth.currentUser?.uid ?: return
        listener = db.collection("notificaciones")
            .whereEqualTo("receptorId", uid)
            .addSnapshotListener { snap, e ->
                if (e != null || snap == null) return@addSnapshotListener

                _notificaciones.value = snap.documents.mapNotNull { it.toObject(Notificacion::class.java) }
                    .sortedByDescending { it.fecha }
            }
    }

    /** 🔹 Aceptar la reserva */
    fun aceptarReserva(notif: Notificacion) {
        val uid = auth.currentUser?.uid ?: return

        // Actualiza estado
        db.collection("notificaciones").document(notif.id)
            .update("estado", "aceptada")

        // Añade al usuario al array de participantes de la reserva
        db.collection("reservas").document(notif.reservaId)
            .update("participantesIds", FieldValue.arrayUnion(uid))
    }

    /** 🔹 Rechazar invitación */
    fun rechazarReserva(notif: Notificacion) {
        db.collection("notificaciones").document(notif.id)
            .update("estado", "rechazada")
    }

    /** 🔹 Limpiar listeners (por ejemplo al cerrar sesión) */
    fun limpiar() {
        listener?.remove()
        listener = null
        _notificaciones.value = emptyList()
    }
}
