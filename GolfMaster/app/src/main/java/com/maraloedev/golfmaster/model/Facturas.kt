package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class Facturas(
    val id: String = "",
    val fecha_factura: Timestamp? = null,
    val id_reserva: String = "",  // Referencia a /reservas/{id}
    val metodo_de_pago_reserva: String = "",
    val precio_factura: String = ""
)
