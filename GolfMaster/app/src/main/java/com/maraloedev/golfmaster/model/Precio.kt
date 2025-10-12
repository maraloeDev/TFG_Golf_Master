package com.maraloedev.golfmaster.model

data class Precio(
    val id_precio: String = "",
    val id_reserva: String = "",
    val id_club_de_golf: String = "",
    val dia_tipo: String = "",
    val es_socio: Boolean = false,
    val importe: Double = 0.0
)

