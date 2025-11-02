package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class Notificacion(
    val id: String = "",
    val reservaId: String = "",          // 🔹 ID de la reserva asociada
    val receptorId: String = "",         // 🔹 UID del usuario que recibe la notificación
    val emisorId: String = "",           // 🔹 UID del usuario que la envía (opcional)
    val mensaje: String = "",
    val fecha: Timestamp? = null,
    val estado: String = "pendiente"     // 🔹 "pendiente", "aceptada", "rechazada"
)
