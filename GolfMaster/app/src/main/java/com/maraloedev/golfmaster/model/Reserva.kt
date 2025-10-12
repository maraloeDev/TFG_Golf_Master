package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class Reserva(
    val id_reserva: String = "",
    val id_jugador: String = "",
    val id_factura: String = "",
    val fecha: Timestamp? = null,
    val recorrido: String = "",
    val numero_de_hoyos: Int = 0,
    val numero_de_jugadores: Int = 0,
    val hora: String = ""
)

