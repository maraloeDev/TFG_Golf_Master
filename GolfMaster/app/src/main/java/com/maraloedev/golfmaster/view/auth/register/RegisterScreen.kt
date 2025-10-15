package com.maraloedev.golfmaster.view.auth.register

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color

/**
 * Pantalla de registro de usuario.
 * Permite introducir email, contraseña y confirmación, y realizar el registro.
 */
@Composable
fun RegisterScreen(
    navController: androidx.navigation.NavController,
    registerViewModel: RegisterViewModel = viewModel()
) {
    // Suscribirse a los estados del ViewModel
    val email by registerViewModel.email.collectAsState()
    val password by registerViewModel.password.collectAsState()
    val confirmPassword by registerViewModel.confirmPassword.collectAsState()
    val registerMessage by registerViewModel.registerMessage.collectAsState()
    val isRegisterEnabled by registerViewModel.isRegisterEnabled.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título de la pantalla
            Text(
                text = "Registro de usuario",
                modifier = Modifier.padding(top = 32.dp, bottom = 24.dp),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            // Campo de email
            Text(text = "Correo electrónico", modifier = Modifier.align(Alignment.Start))
            OutlinedTextField(
                value = email,
                onValueChange = { registerViewModel.onEmailChange(it) },
                placeholder = { Text("Introduce tu correo electrónico") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 24.dp)
            )

            // Campo de contraseña
            Text(text = "Contraseña", modifier = Modifier.align(Alignment.Start))
            OutlinedTextField(
                value = password,
                onValueChange = { registerViewModel.onPasswordChange(it) },
                placeholder = { Text("Introduce tu contraseña") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 24.dp)
            )

            // Campo de confirmación de contraseña
            Text(text = "Confirmar contraseña", modifier = Modifier.align(Alignment.Start))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { registerViewModel.onConfirmPasswordChange(it) },
                placeholder = { Text("Repite la contraseña") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 24.dp)
            )

            // Botón de registro
            Button(
                onClick = {
                    registerViewModel.register(
                        onSuccess = {
                            Toast.makeText(context, "Registro correcto", Toast.LENGTH_SHORT).show()
                        },
                        onError = { errorMsg ->
                            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                enabled = isRegisterEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 24.dp)
            ) {
                Text("Registrarse")
            }

            // Mensaje de registro (error o éxito)
            if (registerMessage.isNotEmpty()) {
                Text(
                    text = registerMessage,
                    color = if (registerMessage.contains("correcto")) Color.Green else Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}