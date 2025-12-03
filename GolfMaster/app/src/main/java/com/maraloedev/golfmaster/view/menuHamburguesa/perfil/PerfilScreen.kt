package com.maraloedev.golfmaster.view.menuHamburguesa.perfil

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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

    // Campos del jugador
    var nombre by remember { mutableStateOf(jugador!!.nombre_jugador) }
    var telefono by remember { mutableStateOf(jugador!!.telefono_jugador) }
    var sexo by remember { mutableStateOf(jugador!!.sexo_jugador) }
    var provincia by remember { mutableStateOf(jugador!!.provincia_jugador ?: "") }
    var codigoPostal by remember { mutableStateOf(jugador!!.codigo_postal_jugador) }
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
            // 🟩 Imagen de perfil
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(VerdeOscuro)
                    .shadow(10.dp, CircleShape)
                    .border(3.dp, Verde, CircleShape)
                    .clickable(enabled = modoEdicion) {
                        if (modoEdicion) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Función para cambiar foto próximamente 📸")
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_usuario),
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (modoEdicion) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xAA000000), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Editar foto",
                            tint = Verde,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // 🧍 Nombre y correo
            Text(
                text = jugador!!.nombre_jugador.ifBlank { "Jugador" },
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = jugador!!.correo_jugador.ifBlank { "Sin correo" },
                color = Color.Gray,
                fontSize = 13.sp
            )

            // 🏷️ Tarjeta de licencia (nueva)
            Spacer(Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Verde.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, Verde),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "LICENCIA",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "#${jugador!!.licencia_jugador.ifBlank { "Pendiente" }}",
                        color = Verde,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // 🟢 Botón Editar / Guardar
            Button(
                onClick = {
                    if (modoEdicion) {
                        val perfilActualizado = jugador!!.copy(
                            nombre_jugador = nombre,
                            telefono_jugador = telefono,
                            sexo_jugador = sexo,
                            provincia_jugador = provincia,
                            codigo_postal_jugador = codigoPostal,
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
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorBoton),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    if (modoEdicion) Icons.Default.Check else Icons.Default.Edit,
                    contentDescription = null,
                    tint = Fondo
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    if (modoEdicion) "Guardar cambios" else "Editar perfil",
                    color = Fondo,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(26.dp))

            // Campos del perfil
            PerfilCampo("Nombre", nombre, modoEdicion) { nombre = it }
            PerfilCampo("Teléfono", telefono, modoEdicion) { telefono = it }
            PerfilCampo("Sexo", sexo, modoEdicion) { sexo = it }
            PerfilCampo("Provincia", provincia, modoEdicion) { provincia = it }
            PerfilCampo("Código Postal", codigoPostal, modoEdicion) { codigoPostal = it }
            PerfilCampo("Handicap", handicap, modoEdicion) { handicap = it }

            Spacer(Modifier.height(32.dp))

            // 🗑️ Eliminar cuenta
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { mostrarDialogo = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Eliminar cuenta", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

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
                        TextButton(onClick = { mostrarDialogo = false }) {
                            Text("Cancelar", color = Color.Gray)
                        }
                    },
                    containerColor = VerdeOscuro,
                    titleContentColor = Color.White,
                    textContentColor = Color.White
                )
            }
        }
    }
}

/* ============================================================
   🧩 COMPONENTE DE CAMPO REUTILIZABLE
   ============================================================ */
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
