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

    // Estado de las notificaciones
    private val _notificaciones = MutableStateFlow<List<Notificacion>>(emptyList())
    val notificaciones = _notificaciones.asStateFlow()

    private var listener: ListenerRegistration? = null

    init {
        suscribirNotificaciones()
    }

    // ================================================================
    // 🔹 Escucha en tiempo real las notificaciones del usuario logueado
    // ================================================================
    private fun suscribirNotificaciones() {
        listener?.remove() // Elimina listener anterior, si existía

        val uid = auth.currentUser?.uid ?: return

        listener = db.collection("notificaciones")
            .whereEqualTo("receptorId", uid)
            .addSnapshotListener { snap, e ->
                if (e != null || snap == null) return@addSnapshotListener

                _notificaciones.value = snap.documents.mapNotNull { doc ->
                    // Convertimos el documento en objeto y añadimos su ID de Firestore
                    doc.toObject(Notificacion::class.java)?.copy(id = doc.id)
                }.sortedByDescending { it.fecha?.toDate() }
            }
    }

    // ================================================================
    // 🔹 Aceptar una invitación de reserva
    // ================================================================
    fun aceptarReserva(notif: Notificacion) {
        val uid = auth.currentUser?.uid ?: return

        // 1️⃣ Actualiza el estado de la notificación
        db.collection("notificaciones").document(notif.id)
            .update("estado", "aceptada")

        // 2️⃣ Añade al usuario al array de participantes de la reserva
        if (notif.reservaId.isNotBlank()) {
            db.collection("reservas").document(notif.reservaId)
                .update("invitados", FieldValue.arrayUnion(uid))
        }
    }

    // ================================================================
    // 🔹 Rechazar una invitación
    // ================================================================
    fun rechazarReserva(notif: Notificacion) {
        if (notif.id.isNotBlank()) {
            db.collection("notificaciones").document(notif.id)
                .update("estado", "rechazada")
        }
    }

    // ================================================================
    // 🔹 Limpieza al cerrar sesión o salir de la app
    // ================================================================
    fun limpiar() {
        listener?.remove()
        listener = null
        _notificaciones.value = emptyList()
    }
}
