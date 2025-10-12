package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class Factura(
    val id_factura: String = "",
    val id_reserva: String = "",
    val fecha_emision: Timestamp? = null,
    val precio_total_factura: Double = 0.0,
    val metodo_de_pago: String = ""
)

