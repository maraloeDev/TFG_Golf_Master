package com.maraloedev.golfmaster.view.alertas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Alerta(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val tipo: String = "info", // info / advertencia / recordatorio
    val fecha: Timestamp? = null
)

class AlertasViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _alertas = MutableStateFlow<List<Alerta>>(emptyList())
    val alertas = _alertas.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun cargarAlertas() = viewModelScope.launch {
        _loading.value = true
        runCatching {
            val res = db.collection("alertas")
                .orderBy("fecha", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            res.documents.mapNotNull { it.toObject(Alerta::class.java) }
        }.onSuccess {
            _alertas.value = it
        }.onFailure {
            _error.value = it.message
        }
        _loading.value = false
    }
}
