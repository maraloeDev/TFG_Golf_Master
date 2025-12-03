package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

/**
 * Invitación de un jugador a otro para unirse a una reserva concreta.
 */
data class Invitacion(
    val id: String = "",                    // ID del documento
    val deId: String = "",                  // UID de quien invita
    val paraId: String = "",                // UID de quien recibe
    val reservaId: String = "",             // ID de la reserva a la que invita
    val nombreDe: String = "",              // Nombre de quien invita (para mostrar en UI)
    val fecha: Timestamp? = null,           // Fecha/hora de la reserva (opcional)
    val estado: String = "pendiente",       // pendiente | aceptada | rechazada
    val creadaEn: Timestamp = Timestamp.now()
)
