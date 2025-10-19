package com.maraloedev.golfmaster.view.auth.login

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maraloedev.golfmaster.R
import com.maraloedev.golfmaster.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, vm: AuthViewModel = viewModel()) {
    val context = LocalContext.current

    // Campos
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    // Validaciones
    val emailError = email.isNotBlank() && !email.contains("@")
    val passwordError = password.isNotBlank() && password.length < 6

    val background = Brush.verticalGradient(listOf(Color(0xFF0B3D2E), Color(0xFF173E34)))

    Scaffold(containerColor = Color.Transparent) { pv ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .padding(pv)
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 🏌️ Logo
                Image(
                    painter = painterResource(id = R.drawable.logo_app),
                    contentDescription = "Logo GolfMaster",
                    modifier = Modifier.size(80.dp)
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Bienvenido a\nGolfMaster",
                    color = Color.White,
                    fontSize = 26.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp
                )

                Spacer(Modifier.height(32.dp))

                // 📨 Email
                AnimatedTextField(
                    label = "Correo electrónico",
                    value = email,
                    type = KeyboardType.Email,
                    onChange = { email = it },
                    isError = emailError,
                    errorText = "Correo inválido."
                )

                // 🔑 Contraseña
                AnimatedTextField(
                    label = "Contraseña",
                    value = password,
                    type = KeyboardType.Password,
                    onChange = { password = it },
                    isPassword = true,
                    showPassword = showPassword,
                    onTogglePassword = { showPassword = !showPassword },
                    isError = passwordError,
                    errorText = "Mínimo 6 caracteres."
                )

                Spacer(Modifier.height(28.dp))

                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank() || emailError || passwordError) {
                            Toast.makeText(context, "Revisa los campos con error.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        loading = true
                        vm.login(
                            email = email,
                            password = password,
                            onSuccess = {
                                loading = false
                                Toast.makeText(context, "Inicio de sesión correcto", Toast.LENGTH_SHORT).show()
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onError = {
                                loading = false
                                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    enabled = !loading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF77)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Iniciar Sesión", color = Color.Black, fontSize = 17.sp)
                }

                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { /* TODO: Implementar recuperación */ }) {
                    Text("Olvidé mi contraseña", color = Color.LightGray)
                }

                Spacer(Modifier.height(20.dp))

                Row {
                    Text("¿Aún no tienes cuenta?", color = Color.White)
                    Spacer(Modifier.width(4.dp))
                    TextButton(onClick = { navController.navigate("register") }) {
                        Text("Regístrate", color = Color(0xFF00FF77))
                    }
                }

                if (loading) {
                    Spacer(Modifier.height(16.dp))
                    CircularProgressIndicator(color = Color(0xFF00FF77))
                }
            }
        }
    }
}

/**
 * Campo animado con borde dorado y validación visual
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedTextField(
    label: String,
    value: String,
    type: KeyboardType = KeyboardType.Text,
    onChange: (String) -> Unit,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePassword: (() -> Unit)? = null,
    isError: Boolean = false,
    errorText: String? = null
) {
    val borderColor by animateColorAsState(
        targetValue = if (isError) Color.Red else Color(0xFFBBA864),
        label = "borderAnim"
    )

    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            label = { Text(label, color = Color.White) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            visualTransformation = when {
                isPassword && !showPassword -> PasswordVisualTransformation()
                else -> VisualTransformation.None
            },
            trailingIcon = {
                if (isPassword && onTogglePassword != null) {
                    IconButton(onClick = onTogglePassword) {
                        val iconRes =
                            if (showPassword) R.drawable.ic_ojo_abierto else R.drawable.ic_ojo_cerrado
                        Icon(
                            painter = painterResource(iconRes),
                            contentDescription = "Mostrar contraseña",
                            tint = Color(0xFFBBA864)
                        )
                    }
                }
            },
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = borderColor,
                unfocusedBorderColor = borderColor,
                cursorColor = Color(0xFF00FF77),
                focusedLabelColor = Color(0xFFBBA864),
                unfocusedLabelColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(keyboardType = type)
        )

        if (isError && errorText != null) {
            Text(errorText, color = Color.Red, fontSize = 12.sp)
        }
    }
}
