package com.maraloedev.golfmaster.model

data class Servicios(
    val id: String = "",
    val descripcion_servicio: String = "",
    val id_club_de_golf: String = "", // referencia a /club_de_golf/{id}
    val tipo_servicio: List<String> = emptyList() // ["cafeteria", "alquiler_carros", ...]
)
