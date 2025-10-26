package com.maraloedev.golfmaster.view.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.maraloedev.golfmaster.view.core.navigation.NavRoutes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(navController: NavController = rememberNavController(), vm: PerfilViewModel = viewModel()) {
    val jugador by vm.jugador.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogo by remember { mutableStateOf(false) }

    // Si aún no hay datos cargados
    if (jugador == null) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xFF0B3D2E)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF00FF77))
        }
        return
    }

    // Variables editables inicializadas una vez que el jugador ya está cargado
    var nombre by remember { mutableStateOf(jugador!!.nombre_jugador) }
    var correo by remember { mutableStateOf(jugador!!.correo_jugador) }
    var telefono by remember { mutableStateOf(jugador!!.telefono_jugador) }
    var sexo by remember { mutableStateOf(jugador!!.sexo_jugador) }
    var pais by remember { mutableStateOf(jugador!!.pais_jugador) }
    var codigoPostal by remember { mutableStateOf(jugador!!.codigo_postal_jugador) }
    var licencia by remember { mutableStateOf(jugador!!.licencia_jugador) }
    var handicap by remember { mutableStateOf(jugador!!.handicap_jugador) }

    Scaffold(
        containerColor = Color(0xFF0B3D2E),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B3D2E))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1F4D3E)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(60.dp))
                }
                IconButton(
                    onClick = {
                        scope.launch { snackbarHostState.showSnackbar("Editar foto próximamente") }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFF00FF77))
                        .size(28.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF0B3D2E))
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(nombre.ifBlank { "Jugador" }, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("ID: ${jugador!!.id}", color = Color.Gray, fontSize = 13.sp)

            Spacer(Modifier.height(24.dp))

            // Género
            Text("Sexo:", color = Color.White, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                RadioButton(
                    selected = sexo == "Hombre",
                    onClick = { sexo = "Hombre" },
                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00FF77))
                )
                Text("Hombre", color = Color.White)
                RadioButton(
                    selected = sexo == "Mujer",
                    onClick = { sexo = "Mujer" },
                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00FF77))
                )
                Text("Mujer", color = Color.White)
            }

            Spacer(Modifier.height(12.dp))

            // Campos editables
            PerfilCampoEditable(label = "Nombre", value = nombre, onValueChange = { nombre = it })
            PerfilCampoEditable(label = "Email", value = correo, readOnly = true)
            PerfilCampoEditable(label = "Teléfono", value = telefono, onValueChange = { telefono = it })
            PerfilCampoEditable(label = "País", value = pais, onValueChange = { pais = it })
            PerfilCampoEditable(label = "Código postal", value = codigoPostal, onValueChange = { codigoPostal = it })
            PerfilCampoEditable(label = "Licencia de golf", value = licencia, onValueChange = { licencia = it })
            PerfilCampoEditable(label = "Handicap", value = handicap, onValueChange = { handicap = it })

            Spacer(Modifier.height(24.dp))

            // Guardar
            Button(
                onClick = {
                    val perfilActualizado = jugador!!.copy(
                        nombre_jugador = nombre,
                        telefono_jugador = telefono,
                        sexo_jugador = sexo,
                        pais_jugador = pais,
                        codigo_postal_jugador = codigoPostal,
                        licencia_jugador = licencia,
                        handicap_jugador = handicap
                    )
                    vm.actualizarPerfil(
                        perfil = perfilActualizado,
                        onSuccess = { scope.launch { snackbarHostState.showSnackbar("Perfil actualizado ✅") } },
                        onError = { e -> scope.launch { snackbarHostState.showSnackbar("Error: $e") } }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF77))
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = Color(0xFF0B3D2E))
                Spacer(Modifier.width(8.dp))
                Text("Guardar cambios", color = Color(0xFF0B3D2E), fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))

            // Eliminar cuenta
            Button(
                onClick = { mostrarDialogo = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.9f))
            ) {
                Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Eliminar cuenta", color = Color.White, fontWeight = FontWeight.Bold)
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
                                    navController.navigate(NavRoutes.LOGIN) { popUpTo(NavRoutes.INICIO) { inclusive = true } }
                                },
                                onError = { e -> scope.launch { snackbarHostState.showSnackbar("Error: $e") } }
                            )
                        }) {
                            Text("Sí, eliminar", color = Color.Red)
                        }
                    },
                    dismissButton = { TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar", color = Color.Gray) } },
                    containerColor = Color(0xFF1F4D3E),
                    titleContentColor = Color.White,
                    textContentColor = Color.White
                )
            }
        }
    }
}

@Composable
private fun PerfilCampoEditable(
    label: String,
    value: String,
    onValueChange: (String) -> Unit = {},
    readOnly: Boolean = false
) {
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(label, color = Color.Gray, fontSize = 13.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = readOnly,
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.White),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00FF77),
                unfocusedBorderColor = Color(0xFF1F4D3E),
                focusedContainerColor = Color(0xFF173E34),
                unfocusedContainerColor = Color(0xFF173E34)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
    }
}
