package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

/**
 * 🟩 Modelo de un amigo dentro de la app
 */
data class Amigo(
    val id: String = "",                 // UID del amigo
    val nombre: String = "",             // Nombre visible
    val numeroLicencia: String = "",     // Número de licencia del jugador
    val fechaAmistad: Timestamp? = null  // Fecha en que se aceptó la amistad
)
