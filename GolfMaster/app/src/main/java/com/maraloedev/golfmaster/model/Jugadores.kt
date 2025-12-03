package com.maraloedev.golfmaster.model

data class Jugadores (
    val id: String ="",
    val nombre_jugador: String = "",
    val apellido_jugador: String = "",
    val correo_jugador: String = "",
    val direccion_jugador: String = "",
    val codigo_postal_jugador: String = "",
    val telefono_jugador: String = "",
    val sexo_jugador: String = "",
    val socio_jugador: Boolean = false,
    val handicap_jugador: Double = 0.0,
    val provincia_jugador: String = "",
    val ciudad_jugador: String = "",
    val password_jugador: String = ""
)
