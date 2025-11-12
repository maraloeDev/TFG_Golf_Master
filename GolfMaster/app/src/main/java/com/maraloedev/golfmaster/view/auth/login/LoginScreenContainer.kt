package com.maraloedev.golfmaster.view.auth.login

import android.util.Patterns
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreenContainer(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    var errorGeneral by remember { mutableStateOf<String?>(null) }
    var erroresCampo by remember { mutableStateOf(mapOf<String, String>()) }

    LoginScreen(
        erroresCampo = erroresCampo,
        errorMessage = errorGeneral,
        onLogin = { email, password ->
            // --- Validación local ---
            val nuevos = mutableMapOf<String, String>()
            if (email.isBlank()) {
                nuevos["email"] = "Campo obligatorio"
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                nuevos["email"] = "Formato de correo no válido"
            }
            if (password.isBlank()) {
                nuevos["password"] = "Campo obligatorio"
            }

            erroresCampo = nuevos
            if (erroresCampo.isNotEmpty()) {
                errorGeneral = "Corrige los campos marcados en rojo"
                return@LoginScreen
            }

            // --- Firebase Auth ---
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        errorGeneral = null
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        val mapped = mapFirebaseLoginError(task.exception)
                        erroresCampo = mapped.first
                        errorGeneral = mapped.second
                    }
                }
        },
        onRegisterClick = { navController.navigate("register") }
    )
}

/**
 * Mapea excepciones de Firebase a:
 *  - Pair<erroresPorCampo, mensajeGeneral>
 */
private fun mapFirebaseLoginError(ex: Exception?): Pair<Map<String, String>, String> {
    val campo = mutableMapOf<String, String>()
    val mensajeGeneral: String = when (ex) {
        is com.google.firebase.auth.FirebaseAuthException -> {
            when (ex.errorCode) {
                "ERROR_USER_NOT_FOUND" -> {
                    campo["email"] = "El usuario no existe o el correo no está registrado"
                    "El usuario no existe o el correo no está registrado"
                }
                "ERROR_WRONG_PASSWORD" -> {
                    campo["password"] = "Contraseña incorrecta"
                    "La contraseña es incorrecta"
                }
                "ERROR_INVALID_EMAIL" -> {
                    campo["email"] = "Formato de correo no válido"
                    "Formato de correo no válido"
                }
                "ERROR_USER_DISABLED" -> {
                    campo["email"] = "Usuario deshabilitado"
                    "El usuario está deshabilitado"
                }
                "ERROR_TOO_MANY_REQUESTS" -> {
                    campo["email"] = "Demasiados intentos fallidos"
                    "Demasiados intentos. Intenta más tarde"
                }
                else -> "Error al iniciar sesión. Verifica tus datos."
            }
        }
        else -> "No se pudo conectar con el servidor. Inténtalo de nuevo."
    }

    if (campo.isEmpty() && mensajeGeneral.isNotBlank()) {
        campo["email"] = mensajeGeneral
    }

    return campo to mensajeGeneral
}
