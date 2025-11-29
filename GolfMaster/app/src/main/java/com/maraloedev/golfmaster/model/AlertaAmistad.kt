package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class AlertaAmistad(
    val id: String = "",
    val de: String = "",          // uid de quien envía
    val para: String = "",        // uid de quien recibe
    val nombreDe: String = "",    // nombre de quien envía (para mostrar)
    val estado: String = "pendiente",
    val fecha: Timestamp = Timestamp.now()
)
