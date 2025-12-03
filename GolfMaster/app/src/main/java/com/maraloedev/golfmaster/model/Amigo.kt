package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

/**
 * Representa una relación de amistad entre dos jugadores.
 */
data class Amigo(
    val id: String = "",                   // ID del documento en Firestore
    val nombre: String = "",               // Nombre del amigo
    val numero_licencia: String = "",      // Licencia federativa
    val fechaAmistad: Timestamp? = null    // Cuándo se creó la amistad (nullable por compatibilidad)
)
