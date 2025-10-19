package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

class Notificacion (
    val id: String = "",
    val fecha: Timestamp,
    val mensaje: String = "",
    val titulo: String = ""
)
