package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class Handicaps(
    val id: String = "",
    val id_jugador: String = "", // referencia a /jugadores/{id}
    val modificacion_handicap: Timestamp? = null,
    val nombre_jugador_handicap: String = "",
    val numero_licencia_jugador: String = ""
)
