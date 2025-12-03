package com.maraloedev.golfmaster.view.auth.login

import android.util.Patterns
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

/**
 * Contenedor de la pantalla de login.
 *
 * Aquí manejo :
 *  - Estado de errores de campo.
 *  - Error general.
 *  - Llamada a FirebaseAuth.
 *  - Navegación a otras pantallas.
 *
 * La UI pura está en [LoginScreen].
 */
@Composable
fun LoginScreenContainer(navController: NavController) {
    // ✅ Mejor obtener la instancia una sola vez por composición
    val auth = remember { FirebaseAuth.getInstance() }

    // Error general (mensaje arriba o debajo del botón, no ligado a un campo concreto)
    var errorGeneral by remember { mutableStateOf<String?>(null) }

    // Errores específicos por campo: "email" -> "obligatorio", "password" -> "..."
    var erroresCampo by remember { mutableStateOf(mapOf<String, String>()) }

    LoginScreen(
        erroresCampo = erroresCampo,
        errorMessage = errorGeneral,
        onLogin = { email, password ->
            // ====================================================
            // 1) Validación local antes de ir a Firebase
            // ====================================================
            val nuevosErrores = mutableMapOf<String, String>()

            if (email.isBlank()) {
                nuevosErrores["email"] = "Campo obligatorio"
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                nuevosErrores["email"] = "Formato de correo no válido"
            }

            if (password.isBlank()) {
                nuevosErrores["password"] = "Campo obligatorio"
            }

            erroresCampo = nuevosErrores

            if (erroresCampo.isNotEmpty()) {
                // Si hay errores de campo, mostramos un mensaje general y no llamamos a Firebase
                errorGeneral = "Corrige los campos marcados en rojo"
                return@LoginScreen
            }

            // ====================================================
            // 2) Login con Firebase Auth
            // ====================================================
            auth
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Login correcto → limpiamos errores y navegamos
                        errorGeneral = null
                        erroresCampo = emptyMap()

                        navController.navigate("home") {
                            // Eliminamos la pantalla de login del back stack
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        // Error en Firebase → mapeamos a mensaje legible
                        val mapped = mapFirebaseLoginError(task.exception)
                        erroresCampo = mapped.first
                        errorGeneral = mapped.second
                    }
                }
        },
        onRegisterClick = {
            navController.navigate("register")
        }
    )
}


private fun mapFirebaseLoginError(ex: Exception?): Pair<Map<String, String>, String> {
    val campo = mutableMapOf<String, String>()

    val mensajeGeneral: String = when (ex) {
        is FirebaseAuthException -> {
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
                else -> {
                    "Error al iniciar sesión. Verifica tus datos."
                }
            }
        }
        else -> {
            "No se pudo conectar con el servidor. Inténtalo de nuevo."
        }
    }

    if (campo.isEmpty() && mensajeGeneral.isNotBlank()) {
        campo["email"] = mensajeGeneral
    }

    return campo to mensajeGeneral
}
