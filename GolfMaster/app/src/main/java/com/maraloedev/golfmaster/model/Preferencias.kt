package com.maraloedev.golfmaster.model

/**
 * Preferencias de uso del usuario dentro de la app.
 */
data class Preferencias(
    val usuario: String = "",              // UID del usuario
    val idioma: String = "Español",        // Idioma preferido
    val dias_juego: List<String> = emptyList(),   // Días favoritos para jugar
    val intereses: List<String> = emptyList()
)
