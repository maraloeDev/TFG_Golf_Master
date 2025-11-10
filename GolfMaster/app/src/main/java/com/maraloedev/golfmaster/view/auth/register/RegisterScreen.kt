@file:OptIn(ExperimentalMaterial3Api::class)

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
private val LabelColor = Color.White
private val TextColor = Color.White
private val CursorColor = Color(0xFF00FF77)

/* ============================================================
   🟩 REGISTER SCREEN
   ============================================================ */
@Composable
fun RegisterScreen(navController: NavController, vm: AuthViewModel = viewModel()) {
    val context = LocalContext.current

    // Campos
    var nombre by rememberSaveable { mutableStateOf("") }
    var apellido by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var direccion by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var handicapText by rememberSaveable { mutableStateOf("") }
    var socio by rememberSaveable { mutableStateOf(false) }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var loading by rememberSaveable { mutableStateOf(false) }
    var sexo by rememberSaveable { mutableStateOf("") }
    var errores by remember { mutableStateOf(mapOf<String, String>()) }

    // Comunidad / Provincia / CP
    var comunidad by rememberSaveable { mutableStateOf("") }
    var provincia by rememberSaveable { mutableStateOf("") }
    var codigoPostal by rememberSaveable { mutableStateOf("") }
    var prefijoCP by rememberSaveable { mutableStateOf("") }

    // ============================================================
    // DATOS DE COMUNIDADES Y PROVINCIAS
    // ============================================================
    val provinciasPorComunidad = mapOf(
        "Andalucía" to listOf("Almería","Cádiz","Córdoba","Granada","Huelva","Jaén","Málaga","Sevilla"),
        "Aragón" to listOf("Huesca","Teruel","Zaragoza"),
        "Principado de Asturias" to listOf("Asturias"),
        "Islas Baleares" to listOf("Illes Balears"),
        "Islas Canarias" to listOf("Las Palmas","Santa Cruz de Tenerife"),
        "Cantabria" to listOf("Cantabria"),
        "Castilla-La Mancha" to listOf("Albacete","Ciudad Real","Cuenca","Guadalajara","Toledo"),
        "Castilla y León" to listOf("Ávila","Burgos","León","Palencia","Salamanca","Segovia","Soria","Valladolid","Zamora"),
        "Cataluña" to listOf("Barcelona","Girona","Lleida","Tarragona"),
        "Comunidad Valenciana" to listOf("Alicante","Castellón","Valencia"),
        "Extremadura" to listOf("Badajoz","Cáceres"),
        "Galicia" to listOf("A Coruña","Lugo","Ourense","Pontevedra"),
        "Comunidad de Madrid" to listOf("Madrid"),
        "Región de Murcia" to listOf("Murcia"),
        "Comunidad Foral de Navarra" to listOf("Navarra"),
        "País Vasco" to listOf("Álava","Bizkaia","Guipúzcoa"),
        "La Rioja" to listOf("La Rioja"),
    )

    val prefijoPorProvincia = mapOf(
        "Álava" to "01", "Albacete" to "02", "Alicante" to "03", "Almería" to "04",
        "Ávila" to "05", "Badajoz" to "06", "Illes Balears" to "07", "Barcelona" to "08",
        "Burgos" to "09", "Cáceres" to "10", "Cádiz" to "11", "Castellón" to "12",
        "Ciudad Real" to "13", "Córdoba" to "14", "A Coruña" to "15", "Cuenca" to "16",
        "Girona" to "17", "Granada" to "18", "Guadalajara" to "19", "Guipúzcoa" to "20",
        "Huelva" to "21", "Huesca" to "22", "Jaén" to "23", "León" to "24", "Lleida" to "25",
        "La Rioja" to "26", "Lugo" to "27", "Madrid" to "28", "Málaga" to "29", "Murcia" to "30",
        "Navarra" to "31", "Ourense" to "32", "Asturias" to "33", "Palencia" to "34",
        "Las Palmas" to "35", "Pontevedra" to "36", "Salamanca" to "37", "Santa Cruz de Tenerife" to "38",
        "Cantabria" to "39", "Segovia" to "40", "Sevilla" to "41", "Soria" to "42",
        "Tarragona" to "43", "Teruel" to "44", "Toledo" to "45", "Valencia" to "46",
        "Valladolid" to "47", "Bizkaia" to "48", "Zamora" to "49", "Zaragoza" to "50"
    )

    val comunidades = listOf(
        "Andalucía","Aragón","Principado de Asturias","Islas Baleares","Islas Canarias","Cantabria",
        "Castilla-La Mancha","Castilla y León","Cataluña","Comunidad Valenciana","Extremadura",
        "Galicia","Comunidad de Madrid","Region de Murcia","Comunidad Foral de Navarra",
        "País Vasco","La Rioja"
    )

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
                    isError = errores.containsKey("nombre"), errorMessage = errores["nombre"])
                AnimatedTextField("Apellidos", apellido, KeyboardType.Text, onChange = { apellido = it },
                    isError = errores.containsKey("apellido"), errorMessage = errores["apellido"])
                AnimatedTextField("Correo electrónico", correo, KeyboardType.Email, onChange = { correo = it },
                    isError = errores.containsKey("correo"), errorMessage = errores["correo"])
                AnimatedTextField("Contraseña", password, KeyboardType.Password,
                    onChange = { password = it },
                    isPassword = true, showPassword = showPassword,
                    onTogglePassword = { showPassword = !showPassword },
                    isError = errores.containsKey("password"), errorMessage = errores["password"]
                )

                SexoDropdown(
                    label = "Sexo",
                    opciones = listOf("Masculino", "Femenino"),
                    value = sexo,
                    onValueChange = { sexo = it },
                    isError = errores.containsKey("sexo"),
                    errorMessage = errores["sexo"]
                )

                ComunidadDropdown(
                    label = "Comunidad Autónoma",
                    opciones = comunidades,
                    value = comunidad,
                    onValueChange = {
                        comunidad = it
                        provincia = ""
                    },
                    isError = errores.containsKey("comunidad"),
                    errorMessage = errores["comunidad"]
                )

                ProvinciaDependienteDropdown(
                    label = "Provincia",
                    comunidad = comunidad,
                    provinciasPorComunidad = provinciasPorComunidad,
                    value = provincia,
                    onValueChange = {
                        provincia = it
                        val pref = prefijoPorProvincia[it] ?: ""
                        prefijoCP = pref
                        codigoPostal = if (codigoPostal.length >= 2)
                            pref + codigoPostal.drop(2)
                        else pref
                    },
                    isError = errores.containsKey("provincia"),
                    errorMessage = errores["provincia"]
                )

                AnimatedTextField("Código Postal", codigoPostal, KeyboardType.Number, onChange = { nuevo ->
                    if (prefijoCP.isNotEmpty()) {
                        codigoPostal = if (!nuevo.startsWith(prefijoCP)) {
                            val resto = nuevo.filter { it.isDigit() }.drop(prefijoCP.length)
                            prefijoCP + resto.take(3)
                        } else {
                            prefijoCP + nuevo.drop(prefijoCP.length).filter { it.isDigit() }.take(3)
                        }
                    } else {
                        codigoPostal = nuevo.filter { it.isDigit() }.take(5)
                    }
                },
                    isError = errores.containsKey("codigoPostal"), errorMessage = errores["codigoPostal"]
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

                Button(
                    onClick = {
                        val nuevosErrores = mutableMapOf<String, String>()
                        if (nombre.isBlank()) nuevosErrores["nombre"] = "Campo vacío"
                        if (apellido.isBlank()) nuevosErrores["apellido"] = "Campo vacío"
                        if (!correo.contains("@")) nuevosErrores["correo"] = "Formato incorrecto"
                        if (password.isBlank()) nuevosErrores["password"] = "Campo vacío"
                        if (sexo.isBlank()) nuevosErrores["sexo"] = "Seleccione un sexo"
                        if (comunidad.isBlank()) nuevosErrores["comunidad"] = "Seleccione una comunidad"
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
                            sexo_jugador = sexo,
                            socio_jugador = socio,
                            handicap_jugador = handicap ?: 0.0,
                            provincia_jugador = provincia,
                            ciudad_jugador = comunidad, // de momento comunidad aquí
                            password_jugador = password
                        )

                        vm.registerJugador(
                            email = correo,
                            password = password,
                            jugador = jugador,
                            onSuccess = {
                                loading = false
                                Toast.makeText(context, "Registrado correctamente ✅", Toast.LENGTH_SHORT).show()
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
                    modifier = Modifier.fillMaxWidth().height(52.dp)
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
   🧩 COMPONENTES DE FORMULARIO
   ============================================================ */

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
    val borderColor by animateColorAsState(targetValue = if (isError) BorderError else BorderNormal, label = "")
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            label = { Text(label, color = LabelColor) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                if (isPassword && onTogglePassword != null) {
                    IconButton(onClick = onTogglePassword) {
                        Icon(
                            painter = painterResource(if (showPassword) R.drawable.ic_ojo_abierto else R.drawable.ic_ojo_cerrado),
                            contentDescription = "Mostrar contraseña",
                            tint = BorderNormal
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = type),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = borderColor,
                unfocusedBorderColor = borderColor,
                cursorColor = CursorColor,
                focusedLabelColor = BorderNormal,
                unfocusedLabelColor = LabelColor,
                focusedTextColor = TextColor,
                unfocusedTextColor = TextColor
            )
        )
        if (isError && !errorMessage.isNullOrBlank()) {
            Text(text = errorMessage, color = BorderError, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp, bottom = 2.dp))
        }
    }
}

@Composable
fun SexoDropdown(
    label: String,
    opciones: List<String>,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(targetValue = if (isError) BorderError else BorderNormal, label = "")
    Column {
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                label = { Text(label, color = LabelColor) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth().padding(vertical = 4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = borderColor,
                    unfocusedBorderColor = borderColor,
                    cursorColor = CursorColor,
                    focusedLabelColor = BorderNormal,
                    unfocusedLabelColor = LabelColor,
                    focusedTextColor = TextColor,
                    unfocusedTextColor = TextColor
                )
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(text = { Text(opcion) }, onClick = {
                        onValueChange(opcion)
                        expanded = false
                    })
                }
            }
        }
        if (isError && !errorMessage.isNullOrBlank()) {
            Text(text = errorMessage, color = BorderError, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp, bottom = 2.dp))
        }
    }
}

@Composable
fun ComunidadDropdown(
    label: String,
    opciones: List<String>,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(targetValue = if (isError) BorderError else BorderNormal, label = "")
    Column {
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                label = { Text(label, color = LabelColor) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth().padding(vertical = 4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = borderColor,
                    unfocusedBorderColor = borderColor,
                    cursorColor = CursorColor,
                    focusedLabelColor = BorderNormal,
                    unfocusedLabelColor = LabelColor,
                    focusedTextColor = TextColor,
                    unfocusedTextColor = TextColor
                )
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(text = { Text(opcion) }, onClick = {
                        onValueChange(opcion)
                        expanded = false
                    })
                }
            }
        }
        if (isError && !errorMessage.isNullOrBlank()) {
            Text(text = errorMessage, color = BorderError, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp, bottom = 2.dp))
        }
    }
}

@Composable
fun ProvinciaDependienteDropdown(
    label: String,
    comunidad: String,
    provinciasPorComunidad: Map<String, List<String>>,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val provincias = provinciasPorComunidad[comunidad] ?: emptyList()
    val borderColor by animateColorAsState(targetValue = if (isError) BorderError else BorderNormal, label = "")
    Column {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                if (comunidad.isNotBlank()) expanded = !expanded
            }
        ) {
            OutlinedTextField(
                value = if (comunidad.isBlank()) "" else value,
                onValueChange = {},
                readOnly = true,
                label = { Text(label, color = LabelColor) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth().padding(vertical = 4.dp),
                placeholder = {
                    if (comunidad.isBlank())
                        Text("Selecciona primero la Comunidad Autónoma", color = LabelColor.copy(alpha = 0.6f))
                },
                enabled = comunidad.isNotBlank(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = borderColor,
                    unfocusedBorderColor = borderColor,
                    disabledBorderColor = BorderNormal.copy(alpha = 0.4f),
                    cursorColor = CursorColor,
                    focusedLabelColor = BorderNormal,
                    unfocusedLabelColor = LabelColor,
                    disabledLabelColor = LabelColor.copy(alpha = 0.6f),
                    focusedTextColor = TextColor,
                    unfocusedTextColor = TextColor,
                    disabledTextColor = TextColor.copy(alpha = 0.6f)
                )
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                provincias.forEach { p ->
                    DropdownMenuItem(text = { Text(p) }, onClick = {
                        onValueChange(p)
                        expanded = false
                    })
                }
            }
        }
        if (isError && !errorMessage.isNullOrBlank()) {
            Text(text = errorMessage, color = BorderError, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp, bottom = 2.dp))
        }
    }
}
