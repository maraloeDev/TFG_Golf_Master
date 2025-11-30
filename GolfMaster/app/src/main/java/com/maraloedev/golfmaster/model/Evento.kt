package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class Evento(
    val id: String? = null,
    val nombre: String = "",
    val tipo: String = "",
    val precioSocio: Double? = null,
    val precioNoSocio: Double? = null,
    val fechaInicio: Timestamp? = null,
    val fechaFin: Timestamp? = null,
    val inscritos: List<String> = emptyList(),
    val creadorId: String = ""     // 👈 NUEVO: identificamos quién creó el evento
)
