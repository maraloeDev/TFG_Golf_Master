package com.maraloedev.golfmaster.model

data class Jugador(
    val id_jugador: String = "",
    val numero_de_licencia_jugador: String = "",
    val id_torneo: String = "",
    val nombre_jugador: String = "",
    val apellido_jugador: String = "",
    val telefono_jugador: String = "",
    val correo_electronico_jugador: String = "",
    val sexo_jugador: String = "",
    val direccion_jugador: String = "",
    val localidad_jugador: String = "",
    val codigo_postal_jugador: String = "",
    val socio_jugador: Boolean = false,
    val handicap_jugador: String = "",
    val id_club_de_golf: String = ""
)

