package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp

data class Torneos(
    val id: String = "",
    val nombre: String = "",
    val tipo: String = "",
    val premio: String = "",
    val lugar: String = "",
    val formato: String = "",
    val fechaInicio: Timestamp? = null,
    val fechaFin: Timestamp? = null,
    val creadorId: String = ""
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "nombre" to nombre,
        "tipo" to tipo,
        "premio" to premio,
        "lugar" to lugar,
        "formato" to formato,
        "fechaInicio" to fechaInicio,
        "fechaFin" to fechaFin,
        "creadorId" to creadorId
    )
}
