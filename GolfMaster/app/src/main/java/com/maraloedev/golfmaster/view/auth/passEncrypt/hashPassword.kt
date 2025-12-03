package com.maraloedev.golfmaster.view.auth.passEncrypt

import android.util.Base64
import java.security.MessageDigest

fun hashPassword(password: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
    return Base64.encodeToString(hashBytes, Base64.NO_WRAP)
}
