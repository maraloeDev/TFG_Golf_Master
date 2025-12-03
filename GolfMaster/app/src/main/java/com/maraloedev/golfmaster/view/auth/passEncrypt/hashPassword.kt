package com.maraloedev.golfmaster.view.auth.passEncrypt

import android.util.Base64
import java.security.MessageDigest

fun hashPassword(password: String): String {

    // 1) Obtenemos un objeto que sabe calcular SHA-256
    val digest = MessageDigest.getInstance("SHA-256")

    // 2) Convertimos la contraseña a bytes y calculamos el hash
    val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))

    // 3) Convertimos el hash a Base64 para almacenarlo como texto normal
    return Base64.encodeToString(hashBytes, Base64.NO_WRAP)
}
