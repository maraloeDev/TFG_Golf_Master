package com.maraloedev.golfmaster.view.alertas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Invitacion(
    val id: String = "",
    val de: String = "",            // uid del emisor
    val para: String = "",          // uid del receptor (usuario actual)
    val reservaId: String = "",
    val estado: String = "pendiente",
    val fecha: Timestamp? = null
)

class AlertasViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _invitaciones = MutableStateFlow<List<Invitacion>>(emptyList())
    val invitaciones = _invitaciones.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var listener: ListenerRegistration? = null

    /** 🟢 Empieza a escuchar invitaciones dirigidas al usuario actual */
    fun observarInvitaciones() {
        val uid = auth.currentUser?.uid ?: run {
            _error.value = "Usuario no autenticado"
            return
        }
        _loading.value = true

        listener?.remove()
        listener = db.collection("invitaciones")
            .whereEqualTo("para", uid)
            .orderBy("fecha", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snaps, e ->
                if (e != null) {
                    _error.value = e.localizedMessage
                    _loading.value = false
                    return@addSnapshotListener
                }
                val lista = snaps?.documents?.mapNotNull { doc ->
                    val inv = doc.toObject(Invitacion::class.java)
                    inv?.copy(id = doc.id)
                }.orEmpty()
                _invitaciones.value = lista
                _loading.value = false
            }
    }

    /** 🟢 Aceptar invitación (crea copia segura de la reserva para el usuario actual) */
    fun aceptarInvitacion(invitacionId: String) = viewModelScope.launch {
        val uid = auth.currentUser?.uid ?: return@launch
        try {
            // 1️⃣ Leer la invitación
            val invDoc = db.collection("invitaciones").document(invitacionId).get().await()
            val reservaId = invDoc.getString("reservaId") ?: throw Exception("Reserva no encontrada")

            // 2️⃣ Actualizar el estado a 'aceptada'
            db.collection("invitaciones").document(invitacionId)
                .update("estado", "aceptada").await()

            // 3️⃣ Obtener los datos de la reserva original
            val reservaDoc = db.collection("reservas").document(reservaId).get().await()
            if (!reservaDoc.exists()) throw Exception("La reserva original ya no existe")

            val data = reservaDoc.data ?: throw Exception("Datos de reserva vacíos")

            // 4️⃣ Crear una copia segura para el usuario que acepta
            val reservaCopia = hashMapOf(
                "usuarioId" to uid,
                "fecha" to (data["fecha"] ?: Timestamp.now()),
                "hora" to (data["hora"] ?: data["fecha"] ?: Timestamp.now()),
                "recorrido" to (data["recorrido"] ?: "Campo principal"),
                "hoyos" to (data["hoyos"] ?: "18 hoyos"),
                "jugadores" to (data["jugadores"] ?: "Solo")
            )

            // 5️⃣ Guardar la nueva reserva en la colección 'reservas'
            db.collection("reservas").add(reservaCopia).await()

            // Refrescar las invitaciones
            observarInvitaciones()

        } catch (e: Exception) {
            _error.value = "❌ Error al aceptar invitación: ${e.message}"
        }
    }

    /** 🟥 Rechazar invitación */
    fun rechazarInvitacion(invitacionId: String) = viewModelScope.launch {
        try {
            db.collection("invitaciones").document(invitacionId)
                .update("estado", "rechazada").await()
        } catch (e: Exception) {
            _error.value = e.localizedMessage
        }
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}
