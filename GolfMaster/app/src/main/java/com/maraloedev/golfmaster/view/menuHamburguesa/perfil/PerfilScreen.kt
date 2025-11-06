package com.maraloedev.golfmaster.view.menuHamburguesa.perfil

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.maraloedev.golfmaster.R
import kotlinx.coroutines.launch

// 🎨 Paleta de colores
private val Fondo = Color(0xFF0B3D2E)
private val Verde = Color(0xFF00FF77)
private val VerdeOscuro = Color(0xFF173E34)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavController = rememberNavController(),
    vm: PerfilViewModel = viewModel()
) {
    val jugador by vm.jugador.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var modoEdicion by remember { mutableStateOf(false) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    if (jugador == null) {
        Box(
            Modifier.fillMaxSize().background(Fondo),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Verde)
        }
        return
    }

    // Variables del jugador
    var nombre by remember { mutableStateOf(jugador!!.nombre_jugador) }
    var telefono by remember { mutableStateOf(jugador!!.telefono_jugador) }
    var sexo by remember { mutableStateOf(jugador!!.sexo_jugador) }
    var ciudad by remember { mutableStateOf(jugador!!.ciudad_jugador ?: "") }
    var provincia by remember { mutableStateOf(jugador!!.provincia_jugador ?: "") }
    var codigoPostal by remember { mutableStateOf(jugador!!.codigo_postal_jugador) }
    var licencia by remember { mutableStateOf(jugador!!.licencia_jugador) }
    var handicap by remember { mutableStateOf(jugador!!.handicap_jugador) }

    val colorBoton by animateColorAsState(
        targetValue = if (modoEdicion) Verde else Color(0xFF00C761),
        label = "colorBoton"
    )

    Scaffold(
        containerColor = Fondo,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🟩 Imagen del perfil
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .size(130.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(VerdeOscuro)
                    .border(3.dp, Verde, CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_usuario),
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )

                if (modoEdicion) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Función para cambiar foto próximamente 📸")
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = (-6).dp, y = (-6).dp)
                            .size(36.dp)
                            .background(Verde, CircleShape)
                            .border(1.dp, Color.White, CircleShape)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Editar foto", tint = Fondo)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            Text(
                text = jugador!!.nombre_jugador.ifBlank { "Jugador" },
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Licencia nº: ${jugador!!.licencia_jugador.ifBlank { "No especificada" }}",
                color = Color.Gray,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(28.dp))

            // 🟢 BOTONES EN LA MISMA FILA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (modoEdicion) {
                            val perfilActualizado = jugador!!.copy(
                                nombre_jugador = nombre,
                                telefono_jugador = telefono,
                                sexo_jugador = sexo,
                                ciudad_jugador = ciudad,
                                provincia_jugador = provincia,
                                codigo_postal_jugador = codigoPostal,
                                licencia_jugador = licencia,
                                handicap_jugador = handicap
                            )
                            vm.actualizarPerfil(
                                perfil = perfilActualizado,
                                onSuccess = {
                                    scope.launch { snackbarHostState.showSnackbar("Perfil actualizado ✅") }
                                    modoEdicion = false
                                },
                                onError = { e ->
                                    scope.launch { snackbarHostState.showSnackbar("Error: $e") }
                                }
                            )
                        } else modoEdicion = true
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = colorBoton),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        if (modoEdicion) Icons.Default.Check else Icons.Default.Edit,
                        contentDescription = null,
                        tint = Fondo
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (modoEdicion) "Guardar" else "Editar",
                        color = Fondo,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { mostrarDialogo = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(6.dp))
                    Text("Eliminar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(26.dp))

            // Campos del perfil
            PerfilCampo(label = "Nombre", valor = nombre, editable = modoEdicion) { nombre = it }
            PerfilCampo(label = "Email", valor = jugador!!.correo_jugador, editable = false)
            PerfilCampo(label = "Teléfono", valor = telefono, editable = modoEdicion) { telefono = it }
            PerfilCampo(label = "Sexo", valor = sexo, editable = modoEdicion) { sexo = it }
            PerfilCampo(label = "Ciudad", valor = ciudad, editable = modoEdicion) { ciudad = it }
            PerfilCampo(label = "Provincia", valor = provincia, editable = modoEdicion) { provincia = it }
            PerfilCampo(label = "Código postal", valor = codigoPostal, editable = modoEdicion) { codigoPostal = it }
            PerfilCampo(label = "Licencia de golf", valor = licencia, editable = false)
            PerfilCampo(label = "Handicap", valor = handicap, editable = modoEdicion) { handicap = it }

            Spacer(Modifier.height(32.dp))

            // 🗑️ Diálogo confirmación
            if (mostrarDialogo) {
                AlertDialog(
                    onDismissRequest = { mostrarDialogo = false },
                    title = { Text("Eliminar cuenta", fontWeight = FontWeight.Bold) },
                    text = { Text("¿Seguro que deseas eliminar tu cuenta? Esta acción no se puede deshacer.") },
                    confirmButton = {
                        TextButton(onClick = {
                            mostrarDialogo = false
                            vm.eliminarCuenta(
                                onSuccess = {
                                    scope.launch { snackbarHostState.showSnackbar("Cuenta eliminada 🗑️") }
                                    navController.navigate("login") { popUpTo("home") { inclusive = true } }
                                },
                                onError = { e -> scope.launch { snackbarHostState.showSnackbar("Error: $e") } }
                            )
                        }) { Text("Sí, eliminar", color = Color.Red) }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar", color = Color.Gray) }
                    },
                    containerColor = VerdeOscuro,
                    titleContentColor = Color.White,
                    textContentColor = Color.White
                )
            }
        }
    }
}

@Composable
private fun PerfilCampo(
    label: String,
    valor: String,
    editable: Boolean,
    onValueChange: (String) -> Unit = {}
) {
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(label, color = Color.Gray, fontSize = 13.sp)
        OutlinedTextField(
            value = valor,
            onValueChange = onValueChange,
            readOnly = !editable,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 3.dp),
            textStyle = LocalTextStyle.current.copy(color = Color.White),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00FF77),
                unfocusedBorderColor = Color(0xFF1F4D3E),
                focusedContainerColor = VerdeOscuro,
                unfocusedContainerColor = VerdeOscuro
            ),
            shape = RoundedCornerShape(10.dp)
        )
    }
}
