package com.maraloedev.golfmaster.view.notificaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Notificacion(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val fecha: Timestamp? = null
)

class NotificacionesViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _notificaciones = MutableStateFlow<List<Notificacion>>(emptyList())
    val notificaciones = _notificaciones.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun cargarNotificaciones() = viewModelScope.launch {
        _loading.value = true
        runCatching {
            val res = db.collection("notificaciones")
                .orderBy("fecha", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            res.documents.mapNotNull { it.toObject(Notificacion::class.java) }
        }.onSuccess {
            _notificaciones.value = it
        }.onFailure {
            _error.value = it.message
        }
        _loading.value = false
    }
}
