package com.maraloedev.golfmaster.view.reservas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Modelo de datos para las reservas
 */
data class Reserva(
    val id: String = "",
    val jugadorId: String = "",
    val fecha: String = "",
    val hora: String = "",
    val campo: String = "",
    val jugadores: Int = 1,
    val estado: String = "Pendiente"
)

/**
 * Estado de la interfaz
 */
data class ReservasUiState(
    val reservas: List<Reserva> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class ReservasViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _ui = MutableStateFlow(ReservasUiState(loading = true))
    val ui: StateFlow<ReservasUiState> = _ui

    init {
        cargarReservas()
    }

    /**
     * Cargar las reservas del usuario autenticado
     */
    fun cargarReservas() {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrBlank()) {
            _ui.value = ReservasUiState(
                loading = false,
                error = "No hay sesión activa. Inicia sesión para ver tus reservas."
            )
            return
        }

        _ui.value = _ui.value.copy(loading = true)

        db.collection("reservas")
            .whereEqualTo("jugadorId", uid)
            .get()
            .addOnSuccessListener { result ->
                val reservas = result.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    try {
                        Reserva(
                            id = doc.id,
                            jugadorId = data["jugadorId"] as? String ?: uid,
                            fecha = data["fecha"] as? String ?: "",
                            hora = data["hora"] as? String ?: "",
                            campo = data["campo"] as? String ?: "",
                            jugadores = (data["jugadores"] as? Number)?.toInt() ?: 1,
                            estado = data["estado"] as? String ?: "Pendiente"
                        )
                    } catch (e: Exception) {
                        null // datos corruptos → ignora la reserva
                    }
                }

                _ui.value = if (reservas.isEmpty()) {
                    ReservasUiState(
                        reservas = emptyList(),
                        loading = false,
                        error = "No tienes reservas registradas todavía."
                    )
                } else {
                    ReservasUiState(reservas = reservas, loading = false)
                }
            }
            .addOnFailureListener { e ->
                _ui.value = ReservasUiState(
                    reservas = emptyList(),
                    loading = false,
                    error = e.localizedMessage ?: "Error al cargar reservas."
                )
            }
    }

    /**
     * Crear una nueva reserva
     */
    fun crearReserva(
        fecha: String,
        hora: String,
        campo: String,
        jugadores: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrBlank()) {
            onError("No hay sesión activa.")
            return
        }

        if (fecha.isBlank() || hora.isBlank() || campo.isBlank()) {
            onError("Debes completar todos los campos.")
            return
        }
        if (jugadores <= 0) {
            onError("El número de jugadores debe ser al menos 1.")
            return
        }

        val nuevaReserva = Reserva(
            jugadorId = uid,
            fecha = fecha.trim(),
            hora = hora.trim(),
            campo = campo.trim(),
            jugadores = jugadores,
            estado = "Pendiente"
        )

        viewModelScope.launch {
            db.collection("reservas")
                .add(nuevaReserva)
                .addOnSuccessListener {
                    onSuccess()
                    cargarReservas()
                    _ui.value = _ui.value.copy(successMessage = "Reserva creada correctamente ✅")
                }
                .addOnFailureListener { e ->
                    onError(e.localizedMessage ?: "Error al crear la reserva.")
                }
        }
    }

    /**
     * Cancelar una reserva existente
     */
    fun cancelarReserva(reservaId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (reservaId.isBlank()) {
            onError("ID de reserva no válido.")
            return
        }

        viewModelScope.launch {
            db.collection("reservas").document(reservaId)
                .update("estado", "Cancelada")
                .addOnSuccessListener {
                    onSuccess()
                    cargarReservas()
                }
                .addOnFailureListener { e ->
                    onError(e.localizedMessage ?: "Error al cancelar la reserva.")
                }
        }
    }

    fun limpiarError() {
        _ui.value = _ui.value.copy(error = null, successMessage = null)
    }
}
