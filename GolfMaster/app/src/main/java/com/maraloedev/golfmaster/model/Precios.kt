package com.maraloedev.golfmaster.model

data class Precios(
    val id: String = "",
    val es_socio: Boolean = false,
    val id_club_de_golf: String = "", // referencia a /club_de_golf/{id}
    val id_reserva: String = "",      // referencia a /reservas/{id}
    val importe_precio: String = "",
    val tipo_dia_precio: String = ""
)
