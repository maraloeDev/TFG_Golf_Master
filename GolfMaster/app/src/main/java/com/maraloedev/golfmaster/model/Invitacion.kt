package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class Invitacion(
    val id: String = "",
    val deId: String = "",           // uid de quien invita
    val paraId: String = "",         // uid invitado
    val reservaId: String = "",      // id de la reserva
    val nombreDe: String = "",       // NOMBRE del jugador que invita
    val fecha: Timestamp? = null,    // fecha/hora de la reserva
    val estado: String = "pendiente",
    val creadaEn: Timestamp = Timestamp.now()
)
