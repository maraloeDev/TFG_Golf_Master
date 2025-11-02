package com.maraloedev.golfmaster.view.amigos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maraloedev.golfmaster.model.FirebaseRepo
import com.maraloedev.golfmaster.model.Jugadores
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AmigosViewModel(
    private val repo: FirebaseRepo = FirebaseRepo()
) : ViewModel() {

    private val _resultados = MutableStateFlow<List<Jugadores>>(emptyList())
    val resultados: StateFlow<List<Jugadores>> = _resultados

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    private var listenJob: Job? = null
    private var ultimaQuery: String = ""

    // ============================================================
    // 🔍 BÚSQUEDA (por nombre o licencia)
    // ============================================================
    fun buscarJugadores(query: String) {
        ultimaQuery = query
        if (query.isBlank()) {
            listenJob?.cancel()
            _resultados.value = emptyList()
            _error.value = null
            _mensaje.value = null
            return
        }

        _loading.value = true
        _error.value = null
        _mensaje.value = null

        // 🔹 Búsqueda puntual (no en tiempo real)
        viewModelScope.launch {
            runCatching { repo.buscarJugadoresPorNombreOLicencia(query) }
                .onSuccess { _resultados.value = it }
                .onFailure { _error.value = it.message }
            _loading.value = false
        }

        // 🔹 Escucha en tiempo real
        listenJob?.cancel()
        listenJob = viewModelScope.launch {
            try {
                repo.listenJugadoresPorNombrePrefix(query).collectLatest { lista ->
                    if (ultimaQuery == query) _resultados.value = lista
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al escuchar actualizaciones."
            }
        }
    }

    // ============================================================
    // ➕ AGREGAR NUEVO AMIGO
    // ============================================================
    fun agregarAmigo(nombre: String, numeroLicencia: String) = viewModelScope.launch {
        if (nombre.isBlank() || numeroLicencia.isBlank()) {
            _error.value = "⚠️ No puede haber campos vacíos."
            return@launch
        }

        _loading.value = true
        _error.value = null
        _mensaje.value = null

        runCatching {
            repo.createOrUpdateJugadorPorNombreYLicencia(nombre, numeroLicencia)
        }.onSuccess {
            _mensaje.value = "✅ Amigo agregado correctamente"
            if (ultimaQuery.isNotBlank()) buscarJugadores(ultimaQuery)
        }.onFailure {
            _error.value = it.message ?: "Error al agregar amigo."
        }

        _loading.value = false
    }

    fun limpiarResultados() {
        listenJob?.cancel()
        _resultados.value = emptyList()
        _error.value = null
        _mensaje.value = null
        ultimaQuery = ""
    }
}
