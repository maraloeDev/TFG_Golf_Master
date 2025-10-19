package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class Torneos(
    val id: String = "",
    val fecha_final_torneo: Timestamp? = null,
    val fecha_inicial_torneo: Timestamp? = null,
    val nombre_torneo: String = "",
    val plazas: Int? = null,
    val precioNoSocio: Double? = null,
    val precioSocio: Double? = null,
    val premio_torneo: String = "",
    val tipo_torneo: String = ""
)
