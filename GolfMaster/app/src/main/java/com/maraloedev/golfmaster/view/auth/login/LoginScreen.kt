package com.maraloedev.golfmaster.view.auth.login

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maraloedev.golfmaster.R

/* 🎨 Colores principales */
private val ScreenBg = Color(0xFF00281F)
private val Accent = Color(0xFF00FF77)
private val TextMuted = Color.White.copy(alpha = 0.8f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    erroresCampo: Map<String, String> = emptyMap(),
    errorMessage: String? = null,
    onLogin: (String, String) -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // 🔹 Control dinámico de errores: si el usuario escribe, desaparece el borde rojo
    var emailError by remember { mutableStateOf(erroresCampo["email"]) }
    var passwordError by remember { mutableStateOf(erroresCampo["password"]) }

    LaunchedEffect(erroresCampo) {
        emailError = erroresCampo["email"]
        passwordError = erroresCampo["password"]
    }

    Surface(modifier = Modifier.fillMaxSize(), color = ScreenBg) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 🏌️ LOGO
            Image(
                painter = painterResource(id = R.drawable.logo_app),
                contentDescription = "Logo GolfMaster",
                modifier = Modifier
                    .size(110.dp)
                    .padding(bottom = 24.dp)
            )

            // 🏷️ TÍTULO
            Text(
                "Iniciar Sesión",
                fontSize = 26.sp,
                color = Accent,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(32.dp))

            // 📧 EMAIL
            // 📧 EMAIL
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (emailError != null && it.isNotBlank()) emailError = null
                },
                label = { Text("Correo electrónico") },
                leadingIcon = {
                    Icon(Icons.Outlined.Email, contentDescription = null, tint = Accent)
                },
                isError = emailError != null,
                supportingText = {
                    emailError?.let { msg ->
                        Text(msg, color = Color.Red, fontSize = 12.sp)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = TextMuted,
                    errorBorderColor = Color.Red,
                    focusedLabelColor = Accent,
                    errorLabelColor = Color.Red
                )
            )


            Spacer(Modifier.height(16.dp))

            // 🔐 CONTRASEÑA
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (passwordError != null && it.isNotBlank()) passwordError = null
                },
                label = { Text("Contraseña") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Accent)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Default.VisibilityOff
                            else
                                Icons.Default.Visibility,
                            contentDescription = if (passwordVisible)
                                "Ocultar contraseña"
                            else
                                "Mostrar contraseña",
                            tint = Accent
                        )
                    }
                },
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                isError = passwordError != null,
                supportingText = {
                    passwordError?.let { msg ->
                        Text(msg, color = Color.Red, fontSize = 12.sp)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = TextMuted,
                    errorBorderColor = Color.Red,
                    focusedLabelColor = Accent,
                    errorLabelColor = Color.Red
                )
            )


            Spacer(Modifier.height(24.dp))

            // 🟩 BOTÓN LOGIN
            Button(
                onClick = { onLogin(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent)
            ) {
                Text("Iniciar sesión", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))

            // 🔹 ENLACE REGISTRO
            TextButton(onClick = onRegisterClick) {
                Text("¿No tienes cuenta? Regístrate", color = Accent)
            }

            // ⚠️ MENSAJE GENERAL DE ERROR
            if (!errorMessage.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
