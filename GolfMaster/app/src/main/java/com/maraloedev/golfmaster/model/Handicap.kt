package com.maraloedev.golfmaster.model

data class Handicap(
    val id: String = "",
    val id_jugador: String = "", // Referencia a Jugador
    val valor: Int = 0
)

