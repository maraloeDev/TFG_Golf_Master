package com.maraloedev.golfmaster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maraloedev.golfmaster.model.FirebaseRepo
import com.maraloedev.golfmaster.model.Jugadores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * AmigosViewModel
 * ----------------
 * Gestiona la búsqueda y gestión de amigos (jugadores)
 * en la base de datos Firebase.
 *
 * Usa FirebaseRepo para interactuar con Firestore.
 */
class AmigosViewModel(
    private val repo: FirebaseRepo = FirebaseRepo()
) : ViewModel() {

    // Lista observable de resultados de búsqueda
    private val _resultados = MutableStateFlow<List<Jugadores>>(emptyList())
    val resultados: StateFlow<List<Jugadores>> get() = _resultados

    // Estado de carga y posibles errores
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    /**
     * Busca jugadores en Firestore cuyo nombre o apellido coincida
     * con el texto introducido en el campo de búsqueda.
     */
    fun buscarJugadores(query: String) = viewModelScope.launch {
        if (query.isBlank()) {
            _resultados.value = emptyList()
            return@launch
        }

        _loading.value = true
        _error.value = null

        runCatching {
            repo.buscarJugadoresPorNombre(query)
        }.onSuccess {
            _resultados.value = it
        }.onFailure {
            _error.value = it.message
        }

        _loading.value = false
    }

    /**
     * Limpia los resultados actuales (útil al salir de la pantalla)
     */
    fun limpiarResultados() {
        _resultados.value = emptyList()
        _error.value = null
    }
}
