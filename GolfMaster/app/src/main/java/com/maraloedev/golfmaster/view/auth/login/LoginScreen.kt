package com.maraloedev.golfmaster.view.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maraloedev.golfmaster.view.core.navigation.NavRoutes
import com.maraloedev.golfmaster.vm.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, vm: AuthViewModel = viewModel()) {
    val ui by vm.ui.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color(0xFF0B3D2E),
        snackbarHost = { SnackbarHost(snackbar) }
    ) { pv ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(pv)
                .background(Color(0xFF0B3D2E)),
            contentAlignment = Alignment.Center
        ) {
            if (ui.loading) {
                CircularProgressIndicator(color = Color(0xFF00FF77))
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF00FF77), modifier = Modifier.size(80.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("GolfMaster", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("Inicia sesión para continuar", color = Color.Gray, fontSize = 16.sp)
                    Spacer(Modifier.height(24.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") },
                        leadingIcon = { Icon(Icons.Default.MailOutline, contentDescription = null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00FF77),
                            unfocusedBorderColor = Color(0xFF1F4D3E),
                            focusedContainerColor = Color(0xFF173E34),
                            unfocusedContainerColor = Color(0xFF173E34)
                        ),
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00FF77),
                            unfocusedBorderColor = Color(0xFF1F4D3E),
                            focusedContainerColor = Color(0xFF173E34),
                            unfocusedContainerColor = Color(0xFF173E34)
                        ),
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = {
                            vm.login(
                                email = email,
                                password = password,
                                onSuccess = {
                                    navController.navigate(NavRoutes.INICIO) {
                                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                                    }
                                },
                                onError = { e -> scope.launch { snackbar.showSnackbar("⚠️ $e") } }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF77)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Iniciar sesión", color = Color(0xFF0B3D2E), fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(16.dp))
                    TextButton(onClick = { navController.navigate(NavRoutes.REGISTER) }) {
                        Text("¿No tienes cuenta? Regístrate", color = Color(0xFF00FF77))
                    }
                }
            }
        }
    }
}
