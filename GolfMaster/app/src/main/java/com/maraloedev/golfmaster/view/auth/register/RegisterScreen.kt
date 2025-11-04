package com.maraloedev.golfmaster.view.auth.register

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
import androidx.compose.runtime.saveable.rememberSaveable
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
   🎨 COLORES
   ============================================================ */
private val ScreenBg = Color(0xFF00281F)
private val PillUnselected = Color(0xFF00FF77)
private val BorderNormal = Color(0xFF00FF77)
private val BorderError = Color(0xFFFF4444)

/* ============================================================
   🟩 REGISTER SCREEN
   ============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, vm: AuthViewModel = viewModel()) {
    val context = LocalContext.current

    // Campos
    var nombre by rememberSaveable { mutableStateOf("") }
    var apellido by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var provincia by rememberSaveable { mutableStateOf("") }
    var codigoPostal by rememberSaveable { mutableStateOf("") }
    var prefijoCP by rememberSaveable { mutableStateOf("") }
    var direccion by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var handicapText by rememberSaveable { mutableStateOf("") }
    var socio by rememberSaveable { mutableStateOf(false) }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var loading by rememberSaveable { mutableStateOf(false) }


    // Errores
    var errores by remember { mutableStateOf(mapOf<String, String>()) }

    // Provincias y prefijos
    val provinciasPrefijos = mapOf(
        "Álava" to "01", "Albacete" to "02", "Alicante" to "03", "Almería" to "04",
        "Ávila" to "05", "Badajoz" to "06", "Islas Baleares" to "07", "Barcelona" to "08",
        "Burgos" to "09", "Cáceres" to "10", "Cádiz" to "11", "Castellón" to "12",
        "Ciudad Real" to "13", "Córdoba" to "14", "A Coruña" to "15", "Cuenca" to "16",
        "Girona" to "17", "Granada" to "18", "Guadalajara" to "19", "Guipúzcoa" to "20",
        "Huelva" to "21", "Huesca" to "22", "Jaén" to "23", "León" to "24", "Lleida" to "25",
        "La Rioja" to "26", "Lugo" to "27", "Madrid" to "28", "Málaga" to "29", "Murcia" to "30",
        "Navarra" to "31", "Ourense" to "32", "Asturias" to "33", "Palencia" to "34",
        "Las Palmas" to "35", "Pontevedra" to "36", "Salamanca" to "37", "Santa Cruz de Tenerife" to "38",
        "Cantabria" to "39", "Segovia" to "40", "Sevilla" to "41", "Soria" to "42",
        "Tarragona" to "43", "Teruel" to "44", "Toledo" to "45", "Valencia" to "46",
        "Valladolid" to "47", "Bizkaia" to "48", "Zamora" to "49", "Zaragoza" to "50",
        "Ceuta" to "51", "Melilla" to "52"
    )

    val provincias = provinciasPrefijos.keys.sorted()
    val handicap = handicapText.toDoubleOrNull()

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
                AnimatedTextField("Nombre", nombre, KeyboardType.Text, onChange = { nombre = it },
                    isError = errores.containsKey("nombre"), errorMessage = errores["nombre"]
                )
                AnimatedTextField("Apellidos", apellido, KeyboardType.Text, onChange = { apellido = it },
                    isError = errores.containsKey("apellido"), errorMessage = errores["apellido"]
                )
                AnimatedTextField("Correo electrónico", correo, KeyboardType.Email, onChange = { correo = it },
                    isError = errores.containsKey("correo"), errorMessage = errores["correo"]
                )
                AnimatedTextField("Contraseña", password, KeyboardType.Password,
                    onChange = { password = it },
                    isPassword = true, showPassword = showPassword,
                    onTogglePassword = { showPassword = !showPassword },
                    isError = errores.containsKey("password"), errorMessage = errores["password"]
                )

                // 🔹 PRIMERO: Provincia
                ProvinciaDropdown(
                    label = "Provincia",
                    provincias = provincias,
                    value = provincia,
                    onValueChange = {
                        provincia = it
                        val prefijo = provinciasPrefijos[it] ?: ""
                        prefijoCP = prefijo
                        // si ya había un CP escrito, actualizamos los 2 primeros dígitos
                        codigoPostal = if (codigoPostal.length >= 2)
                            prefijo + codigoPostal.drop(2)
                        else prefijo
                    },
                    isError = errores.containsKey("provincia"),
                    errorMessage = errores["provincia"]
                )

                // 🔹 Después de seleccionar la provincia
                AnimatedTextField(
                    label = "Código Postal",
                    value = codigoPostal,
                    type = KeyboardType.Number,
                    onChange = { nuevo ->
                        // siempre debe comenzar con el prefijo provincial
                        if (prefijoCP.isNotEmpty()) {
                            // Si intenta borrar el prefijo → lo reponemos
                            codigoPostal = if (!nuevo.startsWith(prefijoCP)) {
                                // Mantiene el prefijo y añade lo que haya después
                                val resto = nuevo.filter { it.isDigit() }.drop(prefijoCP.length)
                                prefijoCP + resto.take(3)
                            } else {
                                // Deja escribir sólo los tres últimos dígitos
                                prefijoCP + nuevo.drop(prefijoCP.length).filter { it.isDigit() }.take(3)
                            }
                        } else {
                            // Si aún no se ha seleccionado provincia, solo permitimos escribir hasta 5 números
                            codigoPostal = nuevo.filter { it.isDigit() }.take(5)
                        }
                    },
                    isError = errores.containsKey("codigoPostal"),
                    errorMessage = errores["codigoPostal"]
                )


                AnimatedTextField("Dirección", direccion, KeyboardType.Text, onChange = { direccion = it },
                    isError = errores.containsKey("direccion"), errorMessage = errores["direccion"]
                )
                AnimatedTextField("Teléfono", telefono, KeyboardType.Phone, onChange = { telefono = it },
                    isError = errores.containsKey("telefono"), errorMessage = errores["telefono"]
                )
                AnimatedTextField("Handicap", handicapText, KeyboardType.Number, onChange = { handicapText = it },
                    isError = errores.containsKey("handicap"), errorMessage = errores["handicap"]
                )

                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = socio, onCheckedChange = { socio = it })
                    Text("¿Es socio?", color = Color.White)
                }

                Spacer(Modifier.height(24.dp))

                // --- BOTÓN REGISTRAR ---
                Button(
                    onClick = {
                        val nuevosErrores = mutableMapOf<String, String>()

                        if (nombre.isBlank()) nuevosErrores["nombre"] = "Campo vacío"
                        if (apellido.isBlank()) nuevosErrores["apellido"] = "Campo vacío"
                        if (!correo.contains("@")) nuevosErrores["correo"] = "Formato incorrecto"
                        if (password.isBlank()) nuevosErrores["password"] = "Campo vacío"
                        if (provincia.isBlank()) nuevosErrores["provincia"] = "Seleccione una provincia"
                        if (codigoPostal.length != 5) nuevosErrores["codigoPostal"] = "Debe tener 5 números"
                        if (direccion.isBlank()) nuevosErrores["direccion"] = "Campo vacío"
                        if (telefono.isBlank()) nuevosErrores["telefono"] = "Campo vacío"
                        else if (telefono.length != 9 || telefono.any { !it.isDigit() })
                            nuevosErrores["telefono"] = "Debe tener 9 dígitos"
                        if (handicap == null || handicap < 0.0 || handicap > 36.0)
                            nuevosErrores["handicap"] = "Debe estar entre 0 y 36"

                        errores = nuevosErrores
                        if (errores.isNotEmpty()) {
                            Toast.makeText(context, "Corrige los campos marcados en rojo", Toast.LENGTH_LONG).show()
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
                            sexo_jugador = "Masculino",
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

                Spacer(Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text("¿Ya tienes cuenta?", color = Color.White)
                    Spacer(Modifier.width(6.dp))
                    TextButton(onClick = { navController.navigate("login") }) {
                        Text("Inicia sesión", color = PillUnselected, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/* ============================================================
   🧩 COMPONENTES (igual que antes)
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
    onTogglePassword: (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    val borderColor by animateColorAsState(
        targetValue = if (isError) BorderError else BorderNormal,
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
            visualTransformation = if (isPassword && !showPassword)
                PasswordVisualTransformation() else VisualTransformation.None,
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
            keyboardOptions = KeyboardOptions(keyboardType = type),
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
                modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProvinciaDropdown(
    label: String,
    provincias: List<String>,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(
        targetValue = if (isError) BorderError else BorderNormal,
        label = "provinciaAnim"
    )

    Column {
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                label = { Text(label, color = Color.White) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
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
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                provincias.forEach { p ->
                    DropdownMenuItem(
                        text = { Text(p) },
                        onClick = {
                            onValueChange(p)
                            expanded = false
                        }
                    )
                }
            }
        }
        if (isError && !errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage,
                color = BorderError,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
            )
        }
    }
}
