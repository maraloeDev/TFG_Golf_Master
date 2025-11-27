package com.maraloedev.golfmaster.view.alertas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.maraloedev.golfmaster.model.Invitacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

    fun observarInvitaciones() {
        val uid = auth.currentUser?.uid ?: return
        _loading.value = true
        listener?.remove()

        listener = db.collection("amigo")
            .whereEqualTo("para", uid)
            .addSnapshotListener { snaps, e ->
                if (e != null) {
                    _error.value = e.localizedMessage
                    _loading.value = false
                    return@addSnapshotListener
                }

                val lista = snaps?.documents?.mapNotNull { doc ->
                    doc.toObject(Invitacion::class.java)?.copy(id = doc.id)
                }.orEmpty()

                _invitaciones.value = lista.sortedByDescending { it.fecha?.seconds ?: 0 }
                _loading.value = false
            }
    }

    fun aceptarAmistad(alertaId: String, deUid: String, nombreDe: String) = viewModelScope.launch {
        val currentUid = auth.currentUser?.uid ?: return@launch
        try {
            // Opcional: marcar como aceptada antes de borrar
            db.collection("amigo").document(alertaId)
                .update("estado", "aceptada")
                .await()

            val currentSnap = db.collection("jugadores").document(currentUid).get().await()
            val miNombre = currentSnap.getString("nombre_jugador") ?: "Jugador"

            // Añadir ambos como amigos
            db.collection("jugadores").document(currentUid)
                .collection("amigos").document(deUid)
                .set(mapOf("nombre" to nombreDe))
                .await()

            db.collection("jugadores").document(deUid)
                .collection("amigos").document(currentUid)
                .set(mapOf("nombre" to miNombre))
                .await()

            // Borrar la notificación para que desaparezca
            db.collection("amigo").document(alertaId).delete().await()

        } catch (e: Exception) {
            _error.value = e.localizedMessage
        }
    }

    fun rechazarAmistad(alertaId: String) = viewModelScope.launch {
        try {
            // Opcional: marcar como rechazada antes de borrarla
            db.collection("amigo").document(alertaId)
                .update("estado", "rechazada")
                .await()

            // Borrar la notificación
            db.collection("amigo").document(alertaId)
                .delete()
                .await()
        } catch (e: Exception) {
            _error.value = e.localizedMessage
        }
    }

    fun eliminarAlerta(alertaId: String) = viewModelScope.launch {
        try {
            db.collection("amigo").document(alertaId).delete().await()
        } catch (e: Exception) {
            _error.value = e.localizedMessage
        }
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}
