package com.maraloedev.golfmaster.model

data class Reserva(
    val id: String = "",
    val id_factura: String = "", // Referencia a Facturas
    val id_jugador: String = "", // Referencia a Jugadores
    val recorrido: List<Int> = listOf(), // Array para elegir entre 9 o 18
    val fecha: String = ""
)

