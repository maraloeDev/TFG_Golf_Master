package com.maraloedev.golfmaster.view.eventos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.maraloedev.golfmaster.model.Evento
import com.maraloedev.golfmaster.model.FirebaseRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la gestión de eventos.
 *
 * Responsabilidades:
 *  - Escuchar los eventos en tiempo real desde Firebase.
 *  - Crear nuevos eventos.
 *  - Inscribir al usuario actual en un evento.
 *  - Eliminar eventos.
 */
class EventosViewModel(
    private val repo: FirebaseRepo = FirebaseRepo()
) : ViewModel() {

    // Lista de eventos (estado expuesto a la UI)
    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos = _eventos.asStateFlow()

    // Indicador de carga
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    // Mensaje de error (si ocurre algún problema)
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        // Suscribimos al flujo de eventos nada más crear el ViewModel
        observarEventosTiempoReal()
    }

    /**
     * Escucha los eventos en tiempo real usando el Flow del repositorio.
     * Cualquier cambio en Firestore se refleja en la UI automáticamente.
     */
    private fun observarEventosTiempoReal() {
        viewModelScope.launch {
            _loading.value = true
            try {
                repo.getEventosFlow().collect { lista ->
                    // Ordenamos los eventos por fecha de inicio
                    _eventos.value = lista.sortedBy { it.fechaInicio?.seconds }
                    _loading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _loading.value = false
            }
        }
    }

    // ================== 🔹 Crear nuevo evento ==================
    /**
     * Crea un nuevo evento en Firebase.
     *
     * @param nombre nombre del torneo.
     * @param tipo tipo de evento (Stableford, Stroke Play, etc.).
     * @param precioSocio precio para socios (string que se convertirá a Double).
     * @param precioNoSocio precio para no socios.
     * @param fechaInicio fecha de inicio como Timestamp.
     * @param fechaFin fecha de fin como Timestamp.
     */
    fun crearEvento(
        nombre: String,
        tipo: String?,
        precioSocio: String,
        precioNoSocio: String,
        fechaInicio: Timestamp?,
        fechaFin: Timestamp?
    ) {
        viewModelScope.launch {
            // Pequeña validación defensiva (la UI ya valida, pero mejor asegurarse)
            if (nombre.isBlank() || tipo == null || fechaInicio == null || fechaFin == null) {
                return@launch
            }

            val nuevo = Evento(
                nombre = nombre,
                tipo = tipo,
                precioSocio = precioSocio.toDoubleOrNull(),
                precioNoSocio = precioNoSocio.toDoubleOrNull(),
                fechaInicio = fechaInicio,
                fechaFin = fechaFin
            )

            runCatching {
                repo.addEvento(nuevo)
            }.onFailure {
                _error.value = it.message
            }
            // No hace falta recargar: el Flow se encarga de actualizar la lista
        }
    }

    // ================== 🔹 Inscribirse a un evento ==================
    /**
     * Inscribe al usuario actual en el evento indicado.
     *
     * - Comprueba que haya usuario logueado.
     * - Evita inscribir dos veces al mismo usuario.
     */
    fun inscribirseEnEvento(evento: Evento) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            // Si ya estaba inscrito, no hacemos nada
            if (evento.inscritos.contains(uid)) return@launch

            val eventoId = evento.id ?: return@launch

            runCatching {
                repo.inscribirseEnEvento(eventoId, uid)
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    // ================== 🔹 Eliminar evento ==================
    /**
     * Elimina un evento por su id.
     */
    fun eliminarEvento(id: String) {
        viewModelScope.launch {
            runCatching {
                repo.deleteEvento(id)
            }.onFailure {
                _error.value = it.message
            }
        }
    }
}
