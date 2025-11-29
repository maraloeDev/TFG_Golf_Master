package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class Evento(
    val id: String? = null,
    val nombre: String? = null,
    val tipo: String? = null,
    val plazas: Int? = null,
    val precioSocio: Double? = null,
    val precioNoSocio: Double? = null,
    val fechaInicio: Timestamp? = null,
    val fechaFin: Timestamp? = null,
    val organizadorId: String? = null,
    val inscritos: List<String> = emptyList() // 👈 NUEVO: lista de uids inscritos
)
