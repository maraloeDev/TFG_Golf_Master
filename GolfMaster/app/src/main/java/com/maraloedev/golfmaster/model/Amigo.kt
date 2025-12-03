package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class Amigo (
    val id: String = "",
    val nombre: String = "",
    val numero_licencia: String = "",
    val fechaAmistad: Timestamp? = null
)
