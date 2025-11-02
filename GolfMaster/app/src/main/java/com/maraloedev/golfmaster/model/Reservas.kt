package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class Reserva(
    val id: String = "",
    val usuarioId: String = "",
    val fecha: Timestamp? = null,
    val hora: Timestamp? = null,
    val recorrido: String? = null,
    val hoyos: String? = null,
    val jugadores: String? = null,
    val jugadorPrincipal: String? = null
)
