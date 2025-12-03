package com.maraloedev.golfmaster.model

/**
 * Información de un jugador del club.
 *
 * IMPORTANTE:
 *  - Se mantiene el nombre de la clase `Jugadores` para no romper código existente.
 *  - Podría ser mejor llamarla `Jugador`, pero requeriría cambiar referencias.
 */
data class Jugadores(
    val id: String = "",
    val nombre_jugador: String = "",
    val apellido_jugador: String = "",
    val correo_jugador: String = "",
    val direccion_jugador: String = "",
    val codigo_postal_jugador: String = "",
    val telefono_jugador: String = "",
    val sexo_jugador: String = "",
    val socio_jugador: Boolean = false,
    val handicap_jugador: Double = 0.0,
    val provincia_jugador: String = "",
    val ciudad_jugador: String = "",
    // ⚠️ Idealmente la contraseña NO debería guardarse en texto claro en Firestore.
    // Lo suyo es guardar hash + salt, o incluso no guardarla aquí si usas Firebase Auth.
    val password_jugador: String = ""
)
