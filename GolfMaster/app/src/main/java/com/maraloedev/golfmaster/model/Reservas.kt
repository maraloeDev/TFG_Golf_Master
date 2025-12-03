package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

/**
 * Reserva de salida al campo.
 */
data class Reserva(
    val id: String = "",                       // ID del documento
    val usuarioId: String = "",                // UID del creador de la reserva
    val fecha: Timestamp? = null,              // Fecha de juego
    val hora: Timestamp? = null,               // Hora de salida
    val recorrido: String? = null,             // Ej: "18 hoyos", "Pitch & Putt"
    val hoyos: String? = null,                 // Ej: "9", "18"
    val jugadores: String? = null,             // Descripción libre de jugadores (opcional)
    val participantesIds: List<String> = emptyList() // UIDs de jugadores apuntados
)
