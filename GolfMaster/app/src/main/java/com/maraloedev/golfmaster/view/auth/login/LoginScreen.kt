package com.maraloedev.golfmaster.view.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun LoginScreen(navController: NavController) { // Recibe NavController desde el padre
    // Estado para los campos de texto
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loginMessage by remember { mutableStateOf("") } // Mensaje de error o éxito
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    // Estado para habilitar el botón según la validación
    val isLoginEnabled = email.contains("@") && password.length >= 6

    // Fondo de pantalla (puedes cambiar el color o poner una imagen de fondo si lo deseas)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A2B1F)) // Verde oscuro, puedes ajustar el color
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
                value = email,
                onValueChange = {
                    email = it
                    // Se actualiza el estado de validación automáticamente
                },
                placeholder = { Text("Introduce tu correo electrónico") },
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
                value = password,
                onValueChange = {
                    password = it
                    // Se actualiza el estado de validación automáticamente
                },
                placeholder = { Text("Introduce tu contraseña") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp) // Caja más pequeña
                    .padding(vertical = 4.dp), // Menos padding
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
                trailingIcon = {
                    val icon = if (passwordVisible) R.drawable.ic_ojo_abierto else R.drawable.ic_ojo_cerrado
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
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
                    // Autenticación con Firebase
                    if (email.isNotBlank() && password.isNotBlank()) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    val userName = user?.displayName ?: user?.email ?: "Usuario"
                                    loginMessage = "Inicio de sesión correcto"
                                    Toast.makeText(context, loginMessage, Toast.LENGTH_SHORT).show()
                                    // Navega a la pantalla de inicio y pasa el nombre de usuario
                                    navController.navigate("inicio?userName=$userName") {
                                        popUpTo("login") { inclusive = true } // Evita volver al login
                                    } 
                                } else {
                                    loginMessage = "Error: " + (task.exception?.message ?: "Datos incorrectos")
                                    Toast.makeText(context, loginMessage, Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        loginMessage = "Introduce correo y contraseña"
                        Toast.makeText(context, loginMessage, Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = isLoginEnabled, // Solo se habilita si los campos son válidos
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC40)) // Verde brillante
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
                    modifier = Modifier.clickable { /* Acción de registro */ }
                )
            }
        }
    }

}