package com.maraloedev.golfmaster.view.auth.register

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import com.maraloedev.golfmaster.model.Jugadores
import com.maraloedev.golfmaster.view.core.navigation.NavRoutes
import com.maraloedev.golfmaster.vm.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    vm: AuthViewModel = viewModel()
) {
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var codigoPostal by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var handicapText by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("Masculino") }
    var socio by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    val handicap = handicapText.toDoubleOrNull() ?: -1.0
    val telefonoValido = telefono.length == 9 && telefono.all { it.isDigit() }
    val handicapValido = handicap in 0.0..36.0
    val camposVacios = listOf(
        nombre, apellido, correo, password, codigoPostal, direccion, telefono, handicapText
    ).any { it.isBlank() }

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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_app),
                    contentDescription = "Logo GolfMaster",
                    modifier = Modifier.size(80.dp)
                )

                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Crear cuenta",
                    color = Color.White,
                    fontSize = 26.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))

                AnimatedTextField("Nombre", nombre, onChange = { nombre = it })
                AnimatedTextField("Apellidos", apellido, onChange = { apellido = it })
                AnimatedTextField("Correo electrónico", correo, KeyboardType.Email, onChange = { correo = it })

                AnimatedTextField(
                    label = "Contraseña",
                    value = password,
                    type = KeyboardType.Password,
                    onChange = { password = it },
                    isPassword = true,
                    showPassword = showPassword,
                    onTogglePassword = { showPassword = !showPassword }
                )

                AnimatedTextField("Código Postal", codigoPostal, KeyboardType.Number, onChange = { codigoPostal = it })
                AnimatedTextField("Dirección", direccion, onChange = { direccion = it })

                AnimatedTextField(
                    label = "Teléfono",
                    value = telefono,
                    type = KeyboardType.Phone,
                    onChange = { telefono = it },
                    isError = telefono.isNotEmpty() && !telefonoValido,
                    errorText = "Debe contener 9 números."
                )

                AnimatedTextField(
                    label = "Handicap",
                    value = handicapText,
                    type = KeyboardType.Number,
                    onChange = { handicapText = it },
                    isError = handicapText.isNotEmpty() && !handicapValido,
                    errorText = "Debe ser un número entre 0 y 36."
                )

                Spacer(Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = sexo,
                        onValueChange = {},
                        label = { Text("Sexo", color = Color.White) },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = textFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf("Masculino", "Femenino").forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    sexo = it
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = socio, onCheckedChange = { socio = it })
                    Text("¿Es socio?", color = Color.White)
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (camposVacios || !telefonoValido || !handicapValido) {
                            Toast.makeText(context, "Revisa los campos con error.", Toast.LENGTH_SHORT).show()
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
                            handicap_jugador = handicap
                        )

                        vm.reg(
                            email = correo,
                            password = password,
                            jugador = jugador,
                            onSuccess = {
                                loading = false
                                Toast.makeText(context, "Registrado correctamente", Toast.LENGTH_SHORT).show()
                                navController.navigate(NavRoutes.LOGIN) {
                                    popUpTo(NavRoutes.REGISTRO) { inclusive = true }
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
                    Text("Registrar", color = Color.Black, fontSize = 17.sp)
                }

                if (loading) {
                    Spacer(Modifier.height(16.dp))
                    CircularProgressIndicator(color = Color(0xFF00FF77))
                }

                Spacer(Modifier.height(20.dp))

                Row {
                    Text("¿Ya tienes cuenta?", color = Color.White)
                    Spacer(Modifier.width(4.dp))
                    TextButton(onClick = { navController.navigate(NavRoutes.LOGIN) }) {
                        Text("Inicia sesión", color = Color(0xFF00FF77))
                    }
                }
            }
        }
    }
}

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
                        Icon(
                            imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPassword) "Ocultar" else "Mostrar",
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

@Composable
fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFFBBA864),
    unfocusedBorderColor = Color(0xFFBBA864),
    cursorColor = Color(0xFF00FF77),
    focusedLabelColor = Color(0xFFBBA864),
    unfocusedLabelColor = Color.White,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)
