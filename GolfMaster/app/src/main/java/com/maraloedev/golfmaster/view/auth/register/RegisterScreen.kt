package com.maraloedev.golfmaster.view.auth.register

import android.util.Patterns
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.maraloedev.golfmaster.vm.AuthViewModel

// ====================
// FUNCIONES AUXILIARES
// ====================

// Tabla de rangos CP -> Provincia
fun obtenerProvinciaPorCP(cp: String): String? {
    if (cp.length < 2) return null
    val prefijo = cp.take(2).toIntOrNull() ?: return null
    return when (prefijo) {
        1 -> "🏴 Álava"
        2 -> "🏴 Albacete"
        3 -> "🏴 Alicante"
        4 -> "🏴 Almería"
        5 -> "🏴 Ávila"
        6 -> "🏴 Badajoz"
        7 -> "🏴 Baleares"
        8 -> "🏴 Barcelona"
        9 -> "🏴 Burgos"
        10 -> "🏴 Cáceres"
        11 -> "🏴 Cádiz"
        12 -> "🏴 Castellón"
        13 -> "🏴 Ciudad Real"
        14 -> "🏴 Córdoba"
        15 -> "🏴 A Coruña"
        16 -> "🏴 Cuenca"
        17 -> "🏴 Girona"
        18 -> "🏴 Granada"
        19 -> "🏴 Guadalajara"
        20 -> "🏴 Guipúzcoa"
        21 -> "🏴 Huelva"
        22 -> "🏴 Huesca"
        23 -> "🏴 Jaén"
        24 -> "🏴 León"
        25 -> "🏴 Lleida"
        26 -> "🏴 La Rioja"
        27 -> "🏴 Lugo"
        28 -> "🏴 Madrid"
        29 -> "🏴 Málaga"
        30 -> "🏴 Murcia"
        31 -> "🏴 Navarra"
        32 -> "🏴 Ourense"
        33 -> "🏴 Asturias"
        34 -> "🏴 Palencia"
        35 -> "🏴 Las Palmas"
        36 -> "🏴 Pontevedra"
        37 -> "🏴 Salamanca"
        38 -> "🏴 Santa Cruz de Tenerife"
        39 -> "🏴 Cantabria"
        40 -> "🏴 Segovia"
        41 -> "🏴 Sevilla"
        42 -> "🏴 Soria"
        43 -> "🏴 Tarragona"
        44 -> "🏴 Teruel"
        45 -> "🏴 Toledo"
        46 -> "🏴 Valencia"
        47 -> "🏴 Valladolid"
        48 -> "🏴 Vizcaya"
        49 -> "🏴 Zamora"
        50 -> "🏴 Zaragoza"
        51 -> "🏴 Ceuta"
        52 -> "🏴 Melilla"
        else -> null
    }
}

// ====================
// REGISTER SCREEN
// ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, vm: AuthViewModel = viewModel()) {
    val context = LocalContext.current

    // Campos principales
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

    // Prefijo telefónico
    var prefijo by remember { mutableStateOf("🇪🇸 +34") }
    var prefijoExpanded by remember { mutableStateOf(false) }

    // Provincia
    var provinciaExpanded by remember { mutableStateOf(false) }

    // Estados de error
    var nombreError by remember { mutableStateOf(false) }
    var apellidoError by remember { mutableStateOf(false) }
    var correoError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var codigoPostalError by remember { mutableStateOf(false) }
    var provinciaError by remember { mutableStateOf(false) }
    var direccionError by remember { mutableStateOf(false) }
    var telefonoError by remember { mutableStateOf(false) }
    var handicapError by remember { mutableStateOf(false) }

    var loading by remember { mutableStateOf(false) }
    var sexoExpanded by remember { mutableStateOf(false) }

    // Validaciones
    val emailValido = Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    val telefonoValido = telefono.length == 9 && telefono.all { it.isDigit() }
    val codigoPostalValido = codigoPostal.length == 5 && codigoPostal.all { it.isDigit() }
    val handicap = handicapText.toDoubleOrNull()
    val handicapValido = handicap != null && handicap in 0.0..36.0

    // 🔹 Autocompletar provincia por CP
    LaunchedEffect(codigoPostal) {
        if (codigoPostal.length == 5 && codigoPostal.all { it.isDigit() }) {
            val prov = obtenerProvinciaPorCP(codigoPostal)
            if (prov != null) {
                provincia = prov
                codigoPostalError = false
                provinciaError = false
            } else {
                provincia = ""
                codigoPostalError = true
            }
        }
    }

    // Fondo degradado
    val background = Brush.verticalGradient(listOf(Color(0xFF0C3B2E), Color(0xFF145C45)))

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
                    modifier = Modifier.size(90.dp)
                )

                Spacer(Modifier.height(16.dp))
                Text("Crear cuenta", color = Color.White, fontSize = 28.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(24.dp))

                // Campos
                AnimatedTextField("Nombre", nombre, onChange = { nombre = it; nombreError = false }, isError = nombreError)
                AnimatedTextField("Apellidos", apellido, onChange = { apellido = it; apellidoError = false }, isError = apellidoError)
                AnimatedTextField(
                    "Correo electrónico", correo, KeyboardType.Email,
                    onChange = { correo = it; correoError = false },
                    isError = correoError,
                    errorText = if (correo.isBlank()) "Campo obligatorio" else "Correo inválido"
                )
                AnimatedTextField(
                    "Contraseña", password, KeyboardType.Password,
                    onChange = { password = it; passwordError = false },
                    isPassword = true,
                    showPassword = showPassword,
                    onTogglePassword = { showPassword = !showPassword },
                    isError = passwordError,
                    errorText = "Campo obligatorio"
                )
                AnimatedTextField(
                    "Código Postal", codigoPostal, KeyboardType.Number,
                    onChange = { codigoPostal = it; codigoPostalError = false },
                    isError = codigoPostalError,
                    errorText = if (codigoPostal.isBlank()) "Campo obligatorio" else "CP inválido o desconocido"
                )

                // Provincia (editable si no autocompleta)
                OutlinedTextField(
                    value = provincia,
                    onValueChange = { provincia = it; provinciaError = false },
                    label = { Text("Provincia", color = Color.White) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    singleLine = true,
                    readOnly = false,
                    isError = provinciaError,
                    colors = textFieldColors()
                )

                AnimatedTextField("Dirección", direccion, onChange = { direccion = it; direccionError = false },
                    isError = direccionError, errorText = "Campo obligatorio")

                // Prefijo + teléfono
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(0.4f)) {
                        ExposedDropdownMenuBox(
                            expanded = prefijoExpanded,
                            onExpandedChange = { prefijoExpanded = !prefijoExpanded }
                        ) {
                            OutlinedTextField(
                                value = prefijo,
                                onValueChange = {},
                                label = { Text("Prefijo", color = Color.White) },
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = prefijoExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                colors = textFieldColors()
                            )

                            val prefijos = listOf(
                                Triple("🇪🇸", "España", "+34"),
                                Triple("🇫🇷", "Francia", "+33"),
                                Triple("🇵🇹", "Portugal", "+351"),
                                Triple("🇩🇪", "Alemania", "+49"),
                                Triple("🇮🇹", "Italia", "+39"),
                                Triple("🇬🇧", "Reino Unido", "+44"),
                                Triple("🇺🇸", "EE.UU.", "+1")
                            )

                            ExposedDropdownMenu(expanded = prefijoExpanded, onDismissRequest = { prefijoExpanded = false }) {
                                prefijos.forEach { (flag, name, code) ->
                                    DropdownMenuItem(
                                        text = { Row { Text("$flag $name ($code)") } },
                                        onClick = {
                                            prefijo = "$flag $code"
                                            prefijoExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(Modifier.weight(0.6f)) {
                        AnimatedTextField(
                            "Teléfono", telefono, KeyboardType.Phone,
                            onChange = { telefono = it; telefonoError = false },
                            isError = telefonoError,
                            errorText = if (telefono.isBlank()) "Campo obligatorio" else "Debe tener 9 números"
                        )
                    }
                }

                AnimatedTextField(
                    "Handicap", handicapText, KeyboardType.Number,
                    onChange = { handicapText = it; handicapError = false },
                    isError = handicapError,
                    errorText = if (handicapText.isBlank()) "Campo obligatorio" else "Debe ser 0–36"
                )

                // Sexo
                ExposedDropdownMenuBox(expanded = sexoExpanded, onExpandedChange = { sexoExpanded = !sexoExpanded }) {
                    OutlinedTextField(
                        value = sexo,
                        onValueChange = {},
                        label = { Text("Sexo", color = Color.White) },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sexoExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = textFieldColors()
                    )
                    ExposedDropdownMenu(expanded = sexoExpanded, onDismissRequest = { sexoExpanded = false }) {
                        listOf("Masculino", "Femenino").forEach {
                            DropdownMenuItem(text = { Text(it) }, onClick = { sexo = it; sexoExpanded = false })
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = socio, onCheckedChange = { socio = it })
                    Text("¿Es socio?", color = Color.White)
                }

                Spacer(Modifier.height(24.dp))

                // Botón registrar
                Button(
                    onClick = {
                        nombreError = nombre.isBlank()
                        apellidoError = apellido.isBlank()
                        correoError = correo.isBlank() || !emailValido
                        passwordError = password.isBlank()
                        codigoPostalError = codigoPostal.isBlank() || !codigoPostalValido
                        provinciaError = provincia.isBlank()
                        direccionError = direccion.isBlank()
                        telefonoError = telefono.isBlank() || !telefonoValido
                        handicapError = handicapText.isBlank() || !handicapValido

                        val hayErrores = listOf(
                            nombreError, apellidoError, correoError, passwordError,
                            codigoPostalError, provinciaError, direccionError,
                            telefonoError, handicapError
                        ).any { it }

                        if (hayErrores) {
                            Toast.makeText(context, "Revisa los campos en rojo.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        loading = true
                        val jugador = Jugadores(
                            nombre_jugador = nombre,
                            apellido_jugador = apellido,
                            correo_jugador = correo,
                            direccion_jugador = direccion,
                            codigo_postal_jugador = codigoPostal,
                            telefono_jugador = "${prefijo.takeLastWhile { it != ' ' }} $telefono",
                            sexo_jugador = sexo,
                            socio_jugador = socio,
                            handicap_jugador = handicap ?: 0.0,
                            provincia_jugador = provincia,
                            password_jugador = password // 🔹 Se guarda también
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF77)),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) { Text("Registrar", color = Color.Black, fontSize = 17.sp) }

                if (loading) {
                    Spacer(Modifier.height(16.dp))
                    CircularProgressIndicator(color = Color(0xFF00FF77))
                }

                Spacer(Modifier.height(24.dp))
                Row {
                    Text("¿Ya tienes cuenta?", color = Color.White)
                    Spacer(Modifier.width(4.dp))
                    TextButton(onClick = { navController.navigate("login") }) {
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
            maxLines = 1,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
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
