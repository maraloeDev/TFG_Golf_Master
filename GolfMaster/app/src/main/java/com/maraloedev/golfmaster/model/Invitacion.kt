package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

/**
 * 🟩 Modelo de datos para las alertas e invitaciones.
 * Sirve tanto para invitaciones de juego como para solicitudes de amistad.
 */
data class Invitacion(
    val id: String = "",          // ID del documento Firestore
    val tipo: String = "",        // "reserva" o "amistad"
    val de: String = "",          // UID del usuario que envía
    val nombreDe: String = "",    // Nombre del usuario que envía
    val para: String = "",        // UID del usuario que recibe
    val nombrePara: String = "",  // Nombre del usuario que recibe
    val reservaId: String = "",   // ID de la reserva (solo si tipo = "reserva")
    val estado: String = "pendiente", // "pendiente", "aceptada", "rechazada"
    val fecha: Timestamp? = null  // Fecha de envío o de la reserva
)
