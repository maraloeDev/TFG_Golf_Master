package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class Handicap(
    val numero_de_licencia_jugador: String = "",
    val id_jugador: String = "",
    val nombre_jugador_handicap: String = "",
    val modificacion_handicap: Timestamp? = null
)

