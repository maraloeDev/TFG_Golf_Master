package com.maraloedev.golfmaster.view.eventos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Modelo de datos de Evento / Torneo
 */
data class Evento(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val fecha: String = "",
    val hora: String = "",
    val lugar: String = "",
    val inscritos: List<String> = emptyList()
)

/**
 * Estado de UI
 */
data class EventosUiState(
    val eventos: List<Evento> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val success: String? = null
)

class EventosViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _ui = MutableStateFlow(EventosUiState(loading = true))
    val ui: StateFlow<EventosUiState> = _ui

    init {
        cargarEventos()
    }

    /**
     * Cargar todos los eventos
     */
    fun cargarEventos() {
        _ui.value = _ui.value.copy(loading = true, error = null)

        db.collection("eventos")
            .get()
            .addOnSuccessListener { result ->
                val eventos = result.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    try {
                        Evento(
                            id = doc.id,
                            titulo = data["titulo"] as? String ?: "Evento sin título",
                            descripcion = data["descripcion"] as? String ?: "",
                            fecha = data["fecha"] as? String ?: "",
                            hora = data["hora"] as? String ?: "",
                            lugar = data["lugar"] as? String ?: "",
                            inscritos = (data["inscritos"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                        )
                    } catch (e: Exception) {
                        null
                    }
                }

                _ui.value = if (eventos.isEmpty()) {
                    EventosUiState(
                        eventos = emptyList(),
                        loading = false,
                        error = "No hay eventos disponibles por ahora."
                    )
                } else {
                    EventosUiState(eventos = eventos, loading = false)
                }
            }
            .addOnFailureListener { e ->
                _ui.value = EventosUiState(
                    loading = false,
                    error = e.localizedMessage ?: "Error al cargar los eventos."
                )
            }
    }

    /**
     * Inscribirse en un evento
     */
    fun inscribirse(eventoId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrBlank()) {
            onError("No hay sesión activa.")
            return
        }

        viewModelScope.launch {
            val ref = db.collection("eventos").document(eventoId)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(ref)
                val actuales = (snapshot.get("inscritos") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                if (uid in actuales) throw Exception("Ya estás inscrito en este evento.")
                val actualizados = actuales + uid
                transaction.update(ref, "inscritos", actualizados)
            }.addOnSuccessListener {
                onSuccess()
                cargarEventos()
                _ui.value = _ui.value.copy(success = "Inscripción confirmada ✅")
            }.addOnFailureListener { e ->
                onError(e.localizedMessage ?: "Error al inscribirse.")
            }
        }
    }

    /**
     * Cancelar inscripción
     */
    fun cancelarInscripcion(eventoId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrBlank()) {
            onError("No hay sesión activa.")
            return
        }

        viewModelScope.launch {
            val ref = db.collection("eventos").document(eventoId)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(ref)
                val actuales = (snapshot.get("inscritos") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                if (uid !in actuales) throw Exception("No estabas inscrito en este evento.")
                val actualizados = actuales.filterNot { it == uid }
                transaction.update(ref, "inscritos", actualizados)
            }.addOnSuccessListener {
                onSuccess()
                cargarEventos()
                _ui.value = _ui.value.copy(success = "Inscripción cancelada ❌")
            }.addOnFailureListener { e ->
                onError(e.localizedMessage ?: "Error al cancelar la inscripción.")
            }
        }
    }

    fun limpiarMensajes() {
        _ui.value = _ui.value.copy(error = null, success = null)
    }
}
