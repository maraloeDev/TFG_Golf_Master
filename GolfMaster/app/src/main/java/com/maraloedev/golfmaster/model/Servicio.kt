package com.maraloedev.golfmaster.model

enum class TipoServicio {
    CAFETERIA, ALQUILER_PALOS, ALQUILER_CARROS, ALQUILER_BOOGIE
}

data class Servicio(
    val id: String = "",
    val id_club_de_golf: String = "", // Referencia a ClubDeGolf
    val tipo: TipoServicio = TipoServicio.CAFETERIA
)

