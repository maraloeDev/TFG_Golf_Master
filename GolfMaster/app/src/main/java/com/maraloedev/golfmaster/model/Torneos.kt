package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class Torneos(
    val id: String = "",
    val nombre_torneo: String = "",
    val tipo_torneo: String = "",
    val premio_torneo: String = "",
    val fecha_inicial_torneo: com.google.firebase.Timestamp? = null,
    val fecha_final_torneo: com.google.firebase.Timestamp? = null,
    val lugar_torneo: String = "",
    val formato_torneo: String = "",
    val imagen_url: String? = null,
    val plazas: Int? = null,
    val precioSocio: Double? = null,
    val precioNoSocio: Double? = null
)
