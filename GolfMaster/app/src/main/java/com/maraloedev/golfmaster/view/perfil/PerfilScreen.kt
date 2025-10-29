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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maraloedev.golfmaster.view.core.navigation.NavRoutes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(navController: NavController, vm: PerfilViewModel = viewModel()) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Estado: cargando
    if (ui.loading) {
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

    // Estado: error
    if (ui.error != null) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xFF0B3D2E)),
            contentAlignment = Alignment.Center
        ) {
            Text(ui.error ?: "Error desconocido", color = Color.Red)
        }
    }

    val jugador = ui.jugador ?: return

    var nombre by remember { mutableStateOf(jugador.nombre_jugador) }
    var correo by remember { mutableStateOf(jugador.correo_jugador) }
    var telefono by remember { mutableStateOf(jugador.telefono_jugador) }
    var sexo by remember { mutableStateOf(jugador.sexo_jugador) }
    var pais by remember { mutableStateOf(jugador.pais_jugador) }
    var codigoPostal by remember { mutableStateOf(jugador.codigo_postal_jugador) }
    var licencia by remember { mutableStateOf(jugador.licencia_jugador) }
    var handicap by remember { mutableStateOf(jugador.handicap_jugador) }

    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFF0B3D2E),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B3D2E))
            )
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .padding(pv)
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
                    onClick = { scope.launch { snackbarHostState.showSnackbar("Función próximamente disponible 📷") } },
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
            Text("ID: ${jugador.id}", color = Color.Gray, fontSize = 13.sp)
            Spacer(Modifier.height(24.dp))

            // Sexo
            Text("Sexo:", color = Color.White, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                SexoOption("Hombre", sexo) { sexo = it }
                SexoOption("Mujer", sexo) { sexo = it }
            }

            Spacer(Modifier.height(12.dp))
            PerfilCampoEditable("Nombre", nombre, onValueChange = { nombre = it })
            PerfilCampoEditable("Email", correo, readOnly = true)
            PerfilCampoEditable("Teléfono", telefono, onValueChange = { telefono = it })
            PerfilCampoEditable("País", pais, onValueChange = { pais = it })
            PerfilCampoEditable("Código postal", codigoPostal, onValueChange = { codigoPostal = it })
            PerfilCampoEditable("Licencia de golf", licencia, onValueChange = { licencia = it })
            PerfilCampoEditable("Handicap", handicap, onValueChange = { handicap = it })

            Spacer(Modifier.height(24.dp))

            // Botón guardar cambios
            Button(
                onClick = {
                    val perfilActualizado = jugador.copy(
                        nombre_jugador = nombre.trim(),
                        telefono_jugador = telefono.trim(),
                        sexo_jugador = sexo,
                        pais_jugador = pais.trim(),
                        codigo_postal_jugador = codigoPostal.trim(),
                        licencia_jugador = licencia.trim(),
                        handicap_jugador = handicap.trim()
                    )
                    vm.actualizarPerfil(
                        perfilActualizado,
                        onSuccess = { scope.launch { snackbarHostState.showSnackbar("Perfil actualizado ✅") } },
                        onError = { e -> scope.launch { snackbarHostState.showSnackbar("⚠️ $e") } }
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

            // Botón eliminar cuenta
            Button(
                onClick = { mostrarDialogo = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.9f))
            ) {
                Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Eliminar cuenta", color = Color.White, fontWeight = FontWeight.Bold)
            }

            // Confirmación de borrado
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
                                    navController.navigate(NavRoutes.LOGIN) {
                                        popUpTo(NavRoutes.INICIO) { inclusive = true }
                                    }
                                },
                                onError = { e -> scope.launch { snackbarHostState.showSnackbar("⚠️ $e") } }
                            )
                        }) { Text("Sí, eliminar", color = Color.Red) }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarDialogo = false }) {
                            Text("Cancelar", color = Color.Gray)
                        }
                    },
                    containerColor = Color(0xFF1F4D3E),
                    titleContentColor = Color.White,
                    textContentColor = Color.White
                )
            }
        }
    }
}

@Composable
private fun SexoOption(label: String, selected: String, onSelect: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selected == label,
            onClick = { onSelect(label) },
            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00FF77))
        )
        Text(label, color = Color.White)
    }
}

@Composable
private fun PerfilCampoEditable(
    label: String,
    value: String,
    onValueChange: (String) -> Unit = {},
    readOnly: Boolean = false
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
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
