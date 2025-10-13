package com.maraloedev.golfmaster.model

data class Precio(
    val id: String = "",
    val id_club_de_golf: String = "", // Referencia a ClubDeGolf
    val id_de_reserva: String = "", // Referencia a Reservas
    val cantidad: Double = 0.0
)

