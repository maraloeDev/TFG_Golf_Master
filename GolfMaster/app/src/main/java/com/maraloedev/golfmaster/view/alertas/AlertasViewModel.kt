package com.maraloedev.golfmaster.view.alertas

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Modelo de datos para una alerta o notificación
 */
data class Alerta(
    val id: String = "",
    val titulo: String = "",
    val mensaje: String = "",
    val fecha: String = "",
    val leida: Boolean = false
)

/**
 * Estado de la interfaz
 */
data class AlertasUiState(
    val alertas: List<Alerta> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

class AlertasViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _ui = MutableStateFlow(AlertasUiState(loading = true))
    val ui: StateFlow<AlertasUiState> = _ui

    init {
        cargarAlertas()
    }

    /**
     * Carga las alertas del usuario autenticado
     */
    fun cargarAlertas() {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrBlank()) {
            _ui.value = AlertasUiState(
                loading = false,
                error = "No hay sesión activa. Inicia sesión para ver tus alertas."
            )
            return
        }

        _ui.value = _ui.value.copy(loading = true)

        db.collection("alertas")
            .whereEqualTo("usuarioId", uid)
            .orderBy("fecha")
            .get()
            .addOnSuccessListener { result ->
                val alertas = result.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    try {
                        Alerta(
                            id = doc.id,
                            titulo = data["titulo"] as? String ?: "Sin título",
                            mensaje = data["mensaje"] as? String ?: "",
                            fecha = data["fecha"] as? String ?: "",
                            leida = data["leida"] as? Boolean ?: false
                        )
                    } catch (_: Exception) {
                        null
                    }
                }

                _ui.value = if (alertas.isEmpty()) {
                    AlertasUiState(
                        alertas = emptyList(),
                        loading = false,
                        error = "No tienes alertas en este momento."
                    )
                } else {
                    AlertasUiState(alertas = alertas, loading = false)
                }
            }
            .addOnFailureListener { e ->
                _ui.value = AlertasUiState(
                    alertas = emptyList(),
                    loading = false,
                    error = e.localizedMessage ?: "Error al cargar alertas."
                )
            }
    }

    /**
     * Marcar una alerta como leída
     */
    fun marcarLeida(alertaId: String) {
        if (alertaId.isBlank()) return
        db.collection("alertas").document(alertaId)
            .update("leida", true)
            .addOnSuccessListener { cargarAlertas() }
    }

    /**
     * Eliminar una alerta
     */
    fun eliminarAlerta(alertaId: String, onError: (String) -> Unit) {
        if (alertaId.isBlank()) return
        db.collection("alertas").document(alertaId)
            .delete()
            .addOnSuccessListener { cargarAlertas() }
            .addOnFailureListener { e -> onError(e.localizedMessage ?: "Error al eliminar alerta.") }
    }
}
