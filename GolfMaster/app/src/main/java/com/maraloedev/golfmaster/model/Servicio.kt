package com.maraloedev.golfmaster.model

data class Servicio(
    val id_servicio: String = "",
    val id_club_de_golf: String = "",
    val descripcion: String = "",
    val cafeteria: Boolean = false,
    val alquiler_palos: Boolean = false,
    val alquiler_carros: Boolean = false,
    val alquiler_boogie: Boolean = false
)

