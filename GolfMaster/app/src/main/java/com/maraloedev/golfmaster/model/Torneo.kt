package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class Torneo(
    val id_torneo: String = "",
    val nombre_torneo: String = "",
    val fecha_inicial_torneo: Timestamp? = null,
    val fecha_final_torneo: Timestamp? = null,
    val tipo_torneo: String = "",
    val premio_torneo: String = ""
)

