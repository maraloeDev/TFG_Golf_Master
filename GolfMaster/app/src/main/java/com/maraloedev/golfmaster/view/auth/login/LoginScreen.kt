package com.maraloedev.golfmaster.view.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maraloedev.golfmaster.R

/* 🎨 Colores principales */
private val ScreenBg = Color(0xFF00281F)
private val Accent = Color(0xFF00FF77)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    erroresCampo: Map<String, String>,
    errorMessage: String?,
    onLogin: (String, String) -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // 👁️ Control de visibilidad de la contraseña
    var passwordVisible by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    // Cuando cambian los errores desde el ViewModel los aplicamos
    LaunchedEffect(erroresCampo) {
        emailError = erroresCampo["email"]
        passwordError = erroresCampo["password"]
    }

    Surface(modifier = Modifier.fillMaxSize(), color = ScreenBg) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo_app),
                contentDescription = "Logo",
                modifier = Modifier.size(110.dp)
            )

            Spacer(Modifier.height(24.dp))

            // EMAIL
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (emailError != null) emailError = null
                },
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Outlined.Email, null, tint = Accent) },
                modifier = Modifier.fillMaxWidth(),
                isError = emailError != null,
                supportingText = {
                    emailError?.let { msg -> Text(msg, color = Color.Red, fontSize = 12.sp) }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                )
            )

            Spacer(Modifier.height(16.dp))

            // PASSWORD
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (passwordError != null) passwordError = null
                },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = Accent) },

                // 👁️ Cambia entre ver / ocultar contraseña
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },

                modifier = Modifier.fillMaxWidth(),
                isError = passwordError != null,
                supportingText = {
                    passwordError?.let { msg -> Text(msg, color = Color.Red, fontSize = 12.sp) }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    val desc = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

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

            Button(
                onClick = { onLogin(email, password) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Accent)
            ) {
                Text("Iniciar sesión", color = Color.Black)
            }

            if (!errorMessage.isNullOrBlank()) {
                Spacer(Modifier.height(16.dp))
                Text(errorMessage, color = Color.Red)
            }

            TextButton(onClick = onRegisterClick) {
                Text("¿No tienes cuenta? Regístrate", color = Accent)
            }
        }
    }
}
