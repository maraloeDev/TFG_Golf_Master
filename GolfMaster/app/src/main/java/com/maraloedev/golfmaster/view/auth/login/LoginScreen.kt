@file:OptIn(ExperimentalMaterial3Api::class)

package com.maraloedev.golfmaster.view.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
private val CardBg = Color(0xFF0D1B12)

/* ============================================================
   🟩 LOGIN SCREEN
   ============================================================ */
@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    errorMessage: String? = null
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrBlank()) {
            scope.launch { snackbarHostState.showSnackbar(errorMessage) }
        }
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
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico", color = Color.White) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PillUnselected,
                        unfocusedBorderColor = Color.DarkGray,
                        cursorColor = PillUnselected
                    ),
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña", color = Color.White) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PillUnselected,
                        unfocusedBorderColor = Color.DarkGray,
                        cursorColor = PillUnselected
                    ),
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(icon, contentDescription = null, tint = PillUnselected)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = { onLogin(email, password) },
                    colors = ButtonDefaults.buttonColors(containerColor = PillUnselected),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Iniciar Sesión", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(10.dp))
                TextButton(onClick = onForgotPasswordClick) {
                    Text("Olvidé mi contraseña", color = Color.White.copy(alpha = 0.8f))
                }

                Spacer(Modifier.height(10.dp))
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
