@file:OptIn(ExperimentalMaterial3Api::class)

package com.maraloedev.golfmaster.view.auth.login

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import kotlinx.coroutines.launch

/* ============================================================
   🎨 COLORES GLOBALES
   ============================================================ */
private val ScreenBg = Color(0xFF00281F)
private val PillUnselected = Color(0xFF00FF77)
private val BorderNormal = Color(0xFF00FF77)
private val BorderError = Color(0xFFFF4444)

/* ============================================================
   🟩 LOGIN SCREEN
   ============================================================ */
@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onRegisterClick: () -> Unit,
    errorMessage: String? = null,
    erroresExternos: Map<String, String> = emptyMap()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Errores internos (campos vacíos) + externos (Firebase)
    var errores by remember { mutableStateOf(mapOf<String, String>()) }

    // Mostrar Snackbar si llega un error nuevo
    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrBlank()) {
            scope.launch { snackbarHostState.showSnackbar(errorMessage) }
        }
    }

    // Combinar errores externos (Firebase)
    LaunchedEffect(erroresExternos) {
        if (erroresExternos.isNotEmpty()) errores = erroresExternos
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = ScreenBg
    ) { pad ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .background(ScreenBg),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_app),
                    contentDescription = "Logo GolfMaster",
                    modifier = Modifier.size(120.dp)
                )

                Spacer(Modifier.height(12.dp))
                Text(
                    "Bienvenido a GolfMaster",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(30.dp))

                // --- CAMPO EMAIL ---
                AnimatedTextFieldLogin(
                    label = "Correo electrónico",
                    value = email,
                    onValueChange = {
                        email = it
                        // Si escribes algo, se quita el error
                        if (errores.containsKey("email") && it.isNotBlank()) {
                            errores = errores - "email"
                        }
                    },
                    keyboardType = KeyboardType.Email,
                    isError = errores.containsKey("email"),
                    errorMessage = errores["email"]
                )

                // --- CAMPO CONTRASEÑA ---
                AnimatedTextFieldLogin(
                    label = "Contraseña",
                    value = password,
                    onValueChange = {
                        password = it
                        if (errores.containsKey("password") && it.isNotBlank()) {
                            errores = errores - "password"
                        }
                    },
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    showPassword = passwordVisible,
                    onTogglePassword = { passwordVisible = !passwordVisible },
                    isError = errores.containsKey("password"),
                    errorMessage = errores["password"]
                )

                Spacer(Modifier.height(24.dp))

                // --- BOTÓN INICIAR SESIÓN ---
                Button(
                    onClick = {
                        val nuevosErrores = mutableMapOf<String, String>()
                        if (email.isBlank()) nuevosErrores["email"] = "Campo obligatorio"
                        if (password.isBlank()) nuevosErrores["password"] = "Campo obligatorio"

                        errores = nuevosErrores

                        if (errores.isNotEmpty()) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Corrige los campos marcados en rojo")
                            }
                            return@Button
                        }

                        onLogin(email.trim(), password.trim())
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PillUnselected),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Iniciar Sesión", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("¿Aún no tienes cuenta?", color = Color.White)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Regístrate",
                        color = PillUnselected,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onRegisterClick() }
                    )
                }
            }
        }
    }
}

/* ============================================================
   🧩 TEXTFIELD LOGIN CON ANIMACIÓN Y ERRORES
   ============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedTextFieldLogin(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePassword: (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    val borderColor by animateColorAsState(
        targetValue = if (isError) BorderError else BorderNormal,
        label = "borderAnimLogin"
    )

    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, color = Color.White) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            visualTransformation = if (isPassword && !showPassword)
                PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                if (isPassword && onTogglePassword != null) {
                    IconButton(onClick = onTogglePassword) {
                        val icon = if (showPassword)
                            Icons.Default.VisibilityOff else Icons.Default.Visibility
                        Icon(icon, contentDescription = null, tint = PillUnselected)
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = borderColor,
                unfocusedBorderColor = borderColor,
                cursorColor = PillUnselected,
                focusedLabelColor = PillUnselected,
                unfocusedLabelColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
        if (isError && !errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage,
                color = BorderError,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 2.dp)
            )
        }
    }
}
