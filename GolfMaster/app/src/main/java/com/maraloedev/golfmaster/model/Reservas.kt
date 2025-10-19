package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class Reservas(
    val id: String = "",
    val fecha_reserva: Timestamp? = null,
    val hora_reserva: Timestamp? = null,
    val id_factura: String = "",   // Referencia a /facturas/{id}
    val id_jugador: String = "",   // Referencia a /jugadores/{id}
    val numero_de_jugadores: String = "",
    val recorrido_reserva: List<String> = listOf("9", "18")
)
