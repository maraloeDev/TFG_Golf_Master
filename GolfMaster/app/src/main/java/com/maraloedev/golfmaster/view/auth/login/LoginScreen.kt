package com.maraloedev.golfmaster.view.auth.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maraloedev.golfmaster.R
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maraloedev.golfmaster.view.auth.register.RegisterScreen

/**
 * Pantalla de Login para GolfMaster.
 * - Valida email y contraseña.
 * - Navega a inicio si el login es correcto.
 * - Permite navegar a registro.
 * - Muestra mensajes de error.
 */
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel(),
    navController: NavController,
    navigateToRegister: () -> Unit = { navController.navigate("registro") } // Navegación a registro
) {
    // Estado de la UI proveniente del ViewModel
    val state by loginViewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current // Captura el contexto antes del callback

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A2B1F))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo de la app
            Image(
                painter = painterResource(id = R.drawable.logo_app),
                contentDescription = "Logo GolfMaster",
                modifier = Modifier
                    .size(80.dp)
                    .padding(top = 32.dp, bottom = 16.dp)
            )

            // Título principal
            Text(
                text = "Bienvenido a\nGolfMaster",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Campo de correo electrónico
            Text(
                text = "Correo electrónico",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            OutlinedTextField(
                value = state.email,
                onValueChange = { loginViewModel.onEmailChange(it) },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedPlaceholderColor = Color(0xFFB0B0B0),
                    unfocusedPlaceholderColor = Color(0xFFB0B0B0)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            // Campo de contraseña
            Text(
                text = "Contraseña",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            OutlinedTextField(
                value = state.password,
                onValueChange = { loginViewModel.onPasswordChange(it) },
                placeholder = { Text("Introduce tu contraseña") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(vertical = 4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedPlaceholderColor = Color(0xFFB0B0B0),
                    unfocusedPlaceholderColor = Color(0xFFB0B0B0)
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                // Icono personalizado para mostrar/ocultar contraseña
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = if (passwordVisible) R.drawable.ic_ojo_abierto else R.drawable.ic_ojo_cerrado),
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                        tint = Color.White,
                        modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            // Botón de iniciar sesión
            Button(
                onClick = {
                    loginViewModel.login(
                        onSuccess = {
                            navController.navigate("inicio") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onError = { errorMsg ->
                            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                        }
                    )
                },
                enabled = state.isLoginEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC40))
            ) {
                Text("Iniciar Sesión", fontWeight = FontWeight.Bold, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Texto para registro
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("¿Aún no tienes cuenta? ", color = Color.White, fontSize = 14.sp)
                Text(
                    text = "Regístrate",
                    color = Color(0xFF2ECC40),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate("registro") // Navega correctamente a la pantalla de registro
                    }
                )
            }
        }
    }

}