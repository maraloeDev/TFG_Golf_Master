package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

/**
 * Evento (torneo, clinic, etc.) creado por el club o un organizador.
 */
data class Evento(
    val id: String? = null,            // ID de Firestore (se rellena tras crear/leer)
    val nombre: String? = null,
    val tipo: String? = null,          // p.ej. "Torneo", "Clinic", "Social"
    val plazas: Int? = null,
    val precioSocio: Double? = null,
    val precioNoSocio: Double? = null,
    val fechaInicio: Timestamp? = null,
    val fechaFin: Timestamp? = null,
    val organizadorId: String? = null, // UID del organizador
    val inscritos: List<String> = emptyList() // Lista de UIDs inscritos
)
