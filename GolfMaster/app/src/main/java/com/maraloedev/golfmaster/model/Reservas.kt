package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class Reserva(
    val id: String? = null,
    val usuarioId: String = "",
    val fecha: com.google.firebase.Timestamp? = null,
    val hora: com.google.firebase.Timestamp? = null,
    val recorrido: String? = null,
    val hoyos: String? = null,
    val jugadores: String? = null
)
