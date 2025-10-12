package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class ClubDeGolf(
    val id_club_de_golf: String = "",
    val nombre_club_de_golf: String = "",
    val telefono_club_de_golf: String = "",
    val correo_electronico_campo_de_golf: String = "",
    val provincia_club_de_golf: String = "",
    val ubicacion_club_de_golf: String = ""
)

