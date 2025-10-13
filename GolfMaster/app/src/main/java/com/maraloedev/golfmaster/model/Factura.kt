package com.maraloedev.golfmaster.model

data class Factura(
    val id: String = "",
    val id_reserva: String = "", // Referencia a Reservas
    val fecha: String = "",
    val total: Double = 0.0
)

