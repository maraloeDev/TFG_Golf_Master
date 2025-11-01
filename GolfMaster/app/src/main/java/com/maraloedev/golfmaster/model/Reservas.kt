package com.maraloedev.golfmaster.model

data class Reserva(
    val id: String = "",
    val usuarioId: String = "",
    val hoyos: Int = 9,
    val fecha: String = "",
    val hora: String = "",
    val numJugadores: Int = 1,
    val participantesIds: List<String> = emptyList()
)
