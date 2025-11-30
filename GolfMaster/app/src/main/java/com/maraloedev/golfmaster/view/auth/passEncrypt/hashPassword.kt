package com.maraloedev.golfmaster.view.auth.passEncrypt

import android.util.Base64
import java.security.MessageDigest

/**
 * Devuelve un hash SHA-256 en Base64 de la contraseña.
 * No es reversible (mucho mejor que guardar algo que podamos descifrar).
 */
fun hashPassword(password: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
    return Base64.encodeToString(hashBytes, Base64.NO_WRAP)
}
