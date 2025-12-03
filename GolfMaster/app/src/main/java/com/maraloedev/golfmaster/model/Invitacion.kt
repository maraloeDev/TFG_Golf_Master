package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class Invitacion(
    val id: String = "",
    val deId: String = "",
    val paraId: String = "",
    val reservaId: String = "",
    val nombreDe: String = "",
    val fecha: Timestamp? = null,
    val estado: String = "pendiente",
    val creadaEn: Timestamp = Timestamp.now()
)
