package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class Torneos(
    val id: String = "",
    val nombre_torneo: String = "",
    val tipo_torneo: String = "",
    val premio_torneo: String = "",
    val fecha_inicial_torneo: Timestamp? = null,
    val fecha_final_torneo: Timestamp? = null,
    val lugar_torneo: String = "",
    val formato_torneo: String = "",
    val plazas: Int? = null,
    val precioSocio: Double? = null,
    val precioNoSocio: Double? = null
)
