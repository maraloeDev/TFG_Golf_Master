package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class AlertaAmistad (
    val id: String = "",
    val de: String = "",
    val para: String = "",
    val nombreDe: String = "",
    val estado: String = "pendiente",
    val fecha: Timestamp = Timestamp.now()
)