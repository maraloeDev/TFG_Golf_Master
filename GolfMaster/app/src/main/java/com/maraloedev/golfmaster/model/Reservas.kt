package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class Reserva(
    val id: String = "",              // 🔹 ID del documento en Firestore
    val id_jugador: String = "",      // 🔹 UID del jugador que creó la reserva
    val fecha: Timestamp? = null,     // 🔹 Fecha de juego
    val hora: String = "",
    val hoyos: Int = 9,               // 🔹 9 o 18
    val numJugadores: Int = 1,
    val invitados: List<String> = emptyList(),
    val fechaCreacion: Timestamp? = null
)
