package com.maraloedev.golfmaster.view.auth.register

import android.util.Patterns
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maraloedev.golfmaster.R
import com.maraloedev.golfmaster.model.Jugadores
import com.maraloedev.golfmaster.vm.AuthViewModel

/* ============================================================
   🎨 COLORES GLOBALES (igual que Login)
   ============================================================ */
private val ScreenBg = Color(0xFF00281F)
private val PillUnselected = Color(0xFF00FF77)

/* ============================================================
   🟩 REGISTER SCREEN
   ============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, vm: AuthViewModel = viewModel()) {
    val context = LocalContext.current

    // Campos
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var codigoPostal by remember { mutableStateOf("") }
    var provincia by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var handicapText by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("Masculino") }
    var socio by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    val emailValido = Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    val telefonoValido = telefono.length == 9 && telefono.all { it.isDigit() }
    val codigoPostalValido = codigoPostal.length == 5 && codigoPostal.all { it.isDigit() }
    val handicap = handicapText.toDoubleOrNull()
    val handicapValido = handicap != null && handicap in 0.0..36.0

    Scaffold(containerColor = ScreenBg) { pv ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBg)
                .padding(pv)
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_app),
                    contentDescription = "Logo GolfMaster",
                    modifier = Modifier.size(90.dp)
                )

                Spacer(Modifier.height(16.dp))
                Text(
                    "Crear cuenta",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))

                // --- CAMPOS ---
                AnimatedTextField("Nombre", nombre, onChange = { nombre = it })
                AnimatedTextField("Apellidos", apellido, onChange = { apellido = it })
                AnimatedTextField("Correo electrónico", correo, KeyboardType.Email, onChange = { correo = it })
                AnimatedTextField(
                    "Contraseña",
                    password,
                    KeyboardType.Password,
                    onChange = { password = it },
                    isPassword = true,
                    showPassword = showPassword,
                    onTogglePassword = { showPassword = !showPassword }
                )
                AnimatedTextField("Código Postal", codigoPostal, KeyboardType.Number, onChange = { codigoPostal = it })
                AnimatedTextField("Provincia", provincia, onChange = { provincia = it })
                AnimatedTextField("Dirección", direccion, onChange = { direccion = it })
                AnimatedTextField("Teléfono", telefono, KeyboardType.Phone, onChange = { telefono = it })
                AnimatedTextField("Handicap", handicapText, KeyboardType.Number, onChange = { handicapText = it })

                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = socio, onCheckedChange = { socio = it })
                    Text("¿Es socio?", color = Color.White)
                }

                Spacer(Modifier.height(24.dp))

                // --- BOTÓN REGISTRAR ---
                Button(
                    onClick = {
                        if (nombre.isBlank() || apellido.isBlank() || correo.isBlank() || password.isBlank()) {
                            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        loading = true
                        val jugador = Jugadores(
                            nombre_jugador = nombre,
                            apellido_jugador = apellido,
                            correo_jugador = correo,
                            direccion_jugador = direccion,
                            codigo_postal_jugador = codigoPostal,
                            telefono_jugador = telefono,
                            sexo_jugador = sexo,
                            socio_jugador = socio,
                            handicap_jugador = handicap ?: 0.0,
                            provincia_jugador = provincia,
                            password_jugador = password
                        )

                        vm.registerJugador(
                            email = correo,
                            password = password,
                            jugador = jugador,
                            onSuccess = {
                                loading = false
                                Toast.makeText(context, "Registrado correctamente", Toast.LENGTH_SHORT).show()
                                navController.navigate("login") { popUpTo("register") { inclusive = true } }
                            },
                            onError = {
                                loading = false
                                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    enabled = !loading,
                    colors = ButtonDefaults.buttonColors(containerColor = PillUnselected),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Registrar", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                if (loading) {
                    Spacer(Modifier.height(16.dp))
                    CircularProgressIndicator(color = PillUnselected)
                }

                // --- TEXTO FINAL CENTRADO ---
                Spacer(Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("¿Ya tienes cuenta?", color = Color.White)
                        Spacer(Modifier.width(6.dp))
                        TextButton(
                            onClick = { navController.navigate("login") },
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Text(
                                "Inicia sesión",
                                color = PillUnselected,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

/* ============================================================
   🧩 TEXTFIELD PERSONALIZADO
   ============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedTextField(
    label: String,
    value: String,
    type: KeyboardType = KeyboardType.Text,
    onChange: (String) -> Unit,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    val borderColor by animateColorAsState(targetValue = Color(0xFFBBA864), label = "borderAnim")

    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label, color = Color.White) },
        singleLine = true,
        maxLines = 1,
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
                    Icon(
                        painter = painterResource(
                            if (showPassword) R.drawable.ic_ojo_abierto else R.drawable.ic_ojo_cerrado
                        ),
                        contentDescription = "Mostrar contraseña",
                        tint = PillUnselected
                    )
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor,
            cursorColor = PillUnselected,
            focusedLabelColor = Color(0xFFBBA864),
            unfocusedLabelColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        keyboardOptions = KeyboardOptions(keyboardType = type)
    )
}
