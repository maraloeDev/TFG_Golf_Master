package com.maraloedev.golfmaster.model
data class Notificacion(
    val id: String = "",
    val receptorId: String = "",
    val emisorId: String = "",
    val reservaId: String = "",
    val mensaje: String = "",
    val estado: String = "pendiente",
    val fecha: Long = System.currentTimeMillis()
)
