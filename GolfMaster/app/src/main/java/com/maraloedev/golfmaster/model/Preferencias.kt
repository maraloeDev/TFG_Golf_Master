package com.maraloedev.golfmaster.model

data class Preferencias(
    val idioma: String = "Español",
    val dias_juego: List<String> = emptyList(),
    val intereses: List<String> = emptyList()
)
