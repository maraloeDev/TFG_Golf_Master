package com.maraloedev.golfmaster.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel sin persistencia que mantiene las preferencias en memoria.
 */
class PreferenciasViewModel : ViewModel() {

    private val _notificaciones = MutableStateFlow(true)
    val notificaciones: StateFlow<Boolean> = _notificaciones

    private val _modoOscuro = MutableStateFlow(true)
    val modoOscuro: StateFlow<Boolean> = _modoOscuro

    private val _privacidad = MutableStateFlow(false)
    val privacidad: StateFlow<Boolean> = _privacidad

    fun setNotificaciones(enabled: Boolean) {
        _notificaciones.value = enabled
    }

    fun setModoOscuro(enabled: Boolean) {
        _modoOscuro.value = enabled
    }

    fun setPrivacidad(enabled: Boolean) {
        _privacidad.value = enabled
    }
}
