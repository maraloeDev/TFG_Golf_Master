package com.maraloedev.golfmaster.model

data class Jugadores(
    val id: String = "",
    val apellido_jugador: String = "",
    val codigo_postal_jugador: String = "",
    val correo_jugador: String = "",
    val direccion_jugador: String = "",
    val handicap_jugador: Double = 0.0,
    val nombre_jugador: String = "",
    val numero_licencia_jugador: String = "", // 🆕 Campo añadido
    val sexo_jugador: String = "",
    val socio_jugador: Boolean = false,
    val telefono_jugador: String = "",
    val provincia_jugador: String = "",
    val password_jugador: String = ""
)