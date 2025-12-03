package com.maraloedev.golfmaster.view.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.maraloedev.golfmaster.R

/* 🎨 Colores principales de la pantalla (podrían ir en tu Theme) */
private val ScreenBg = Color(0xFF00281F)
private val Accent = Color(0xFF00FF77)

/**
 * Pantalla de Login.
 *
 * @param erroresCampo mapa de errores por campo (p.ej. "email" -> "Email inválido").
 * @param errorMessage error general (p.ej. credenciales incorrectas desde el ViewModel).
 * @param onLogin callback que se llama al pulsar "Iniciar sesión".
 * @param onRegisterClick callback para navegar a la pantalla de registro.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    erroresCampo: Map<String, String>,
    errorMessage: String?,
    onLogin: (String, String) -> Unit,
    onRegisterClick: () -> Unit
) {
    // ✅ rememberSaveable para que sobrevivan a cambios de configuración
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    // Errores específicos de cada campo
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // 👁️ Control de visibilidad de la contraseña
    var passwordVisible by remember { mutableStateOf(false) }

    // Para ocultar el teclado al tocar fuera de los TextFields
    val focusManager = LocalFocusManager.current

    // Cuando cambian los errores desde el ViewModel los aplicamos
    LaunchedEffect(erroresCampo) {
        emailError = erroresCampo["email"]
        passwordError = erroresCampo["password"]
    }

    // Validación mínima para habilitar/deshabilitar el botón
    val isLoginEnabled = email.isNotBlank() && password.isNotBlank()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ScreenBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                // 👇 Ocultar teclado cuando se pulse fuera de los campos
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // 🏌️‍♂️ Logo de la app
            Image(
                painter = painterResource(id = R.drawable.logo_app),
                contentDescription = "Logo GolfMaster",
                modifier = Modifier.size(110.dp)
            )

            Spacer(Modifier.height(24.dp))

            // ==========================
            // ✉️ CAMPO EMAIL
            // ==========================
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    // Si el usuario corrige el campo, limpiamos el error visual
                    if (emailError != null) emailError = null
                },
                label = { Text("Correo electrónico") },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Email,
                        contentDescription = null,
                        tint = Accent
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                isError = emailError != null,
                supportingText = {
                    emailError?.let { msg ->
                        Text(msg, color = Color.Red, fontSize = 12.sp)
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    // Al pulsar "Next" pasamos al siguiente campo
                    onNext = { focusManager.clearFocus() }
                )
            )

            Spacer(Modifier.height(16.dp))

            // ==========================
            // 🔒 CAMPO PASSWORD
            // ==========================
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (passwordError != null) passwordError = null
                },
                label = { Text("Contraseña") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = Accent
                    )
                },
                // 👁️ Cambia entre ver / ocultar contraseña
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                modifier = Modifier.fillMaxWidth(),
                isError = passwordError != null,
                supportingText = {
                    passwordError?.let { msg ->
                        Text(msg, color = Color.Red, fontSize = 12.sp)
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    // Al pulsar "Done" intentamos hacer login
                    onDone = {
                        focusManager.clearFocus()
                        if (isLoginEnabled) {
                            onLogin(email.trim(), password.trim())
                        }
                    }
                ),
                trailingIcon = {
                    val icon = if (passwordVisible) {
                        Icons.Filled.VisibilityOff
                    } else {
                        Icons.Filled.Visibility
                    }
                    val desc = if (passwordVisible) {
                        "Ocultar contraseña"
                    } else {
                        "Mostrar contraseña"
                    }

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = icon,
                            contentDescription = desc,
                            tint = Accent
                        )
                    }
                }
            )

            Spacer(Modifier.height(24.dp))

            // ==========================
            // 🔓 BOTÓN LOGIN
            // ==========================
            Button(
                onClick = {
                    focusManager.clearFocus()
                    onLogin(email.trim(), password.trim())
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Accent),
                enabled = isLoginEnabled
            ) {
                Text("Iniciar sesión", color = Color.Black)
            }

            // ==========================
            // ⚠️ ERROR GENERAL
            // ==========================
            if (!errorMessage.isNullOrBlank()) {
                Spacer(Modifier.height(16.dp))
                Text(
                    errorMessage,
                    color = Color.Red,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(8.dp))

            // ==========================
            // 📝 LINK A REGISTRO
            // ==========================
            TextButton(onClick = onRegisterClick) {
                Text("¿No tienes cuenta? Regístrate", color = Accent)
            }
        }
    }
}
