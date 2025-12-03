package com.maraloedev.golfmaster.view.auth.passEncrypt

import android.util.Base64
import java.security.MessageDigest

/**
 * Función sencilla para generar un hash de una contraseña utilizando SHA-256.
 *
 * ¿Qué hace esta función?
 * -----------------------------------------
 * 1. Convierte la contraseña a bytes (UTF-8).
 * 2. Aplica el algoritmo SHA-256 para obtener su hash.
 * 3. Convierte ese hash a un texto Base64 para poder guardarlo fácilmente.
 */
fun hashPassword(password: String): String {

    // 1) Obtenemos un objeto que sabe calcular SHA-256
    val digest = MessageDigest.getInstance("SHA-256")

    // 2) Convertimos la contraseña a bytes y calculamos el hash
    val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))

    // 3) Convertimos el hash a Base64 para almacenarlo como texto normal
    return Base64.encodeToString(hashBytes, Base64.NO_WRAP)
}
