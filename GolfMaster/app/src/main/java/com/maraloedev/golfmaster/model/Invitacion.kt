package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class Invitacion(
    val id: String = "",
    val tipo: String = "",
    val de: String = "",
    val nombreDe: String = "",
    val para: String = "",
    val nombrePara: String = "",
    val reservaId: String = "",
    val estado: String = "pendiente",
    val fecha: Timestamp? = null
)