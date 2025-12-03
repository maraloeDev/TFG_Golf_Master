package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

/**
 * Representa una alerta de petición de amistad entre jugadores.
 */
data class AlertaAmistad(
    val id: String = "",
    val de: String = "",                // UID del jugador que envía la solicitud
    val para: String = "",             // UID del jugador que la recibe
    val nombreDe: String = "",         // Nombre del jugador que envía la solicitud
    val estado: String = "pendiente",  // pendiente | aceptada | rechazada
    val fecha: Timestamp = Timestamp.now()
)
