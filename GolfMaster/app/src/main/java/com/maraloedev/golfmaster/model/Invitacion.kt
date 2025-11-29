package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class Invitacion(
    val id: String = "",
    val deId: String = "",           // uid de quien invita
    val paraId: String = "",         // uid invitado
    val reservaId: String = "",      // id de la reserva
    val nombreDe: String = "",       // nombre de quien invita
    val fecha: Timestamp? = null,    // fecha/hora de la reserva
    val estado: String = "pendiente",   // "pendiente", "aceptada", "rechazada"
    val creadaEn: Timestamp = Timestamp.now()
)
