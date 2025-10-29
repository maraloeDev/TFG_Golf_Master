package com.maraloedev.golfmaster.view.amigos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmigosScreen(vm: AmigosViewModel = viewModel()) {
    val ui by vm.ui.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var correoBusqueda by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color(0xFF0B3D2E),
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("Amigos", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B3D2E))
            )
        }
    ) { pv ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pv)
                .background(Color(0xFF0B3D2E))
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = correoBusqueda,
                onValueChange = { correoBusqueda = it },
                label = { Text("Correo del jugador") },
                placeholder = { Text("ejemplo@golf.com") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00FF77),
                    unfocusedBorderColor = Color(0xFF1F4D3E),
                    focusedContainerColor = Color(0xFF173E34),
                    unfocusedContainerColor = Color(0xFF173E34),
                    focusedLabelColor = Color(0xFF00FF77),
                    unfocusedLabelColor = Color.White
                ),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    vm.enviarSolicitud(
                        correoDestino = correoBusqueda.trim(),
                        onSuccess = { scope.launch { snackbar.showSnackbar("Solicitud enviada ✅") } },
                        onError = { e -> scope.launch { snackbar.showSnackbar("⚠️ $e") } }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF77)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color(0xFF0B3D2E))
                Spacer(Modifier.width(8.dp))
                Text("Enviar solicitud", color = Color(0xFF0B3D2E), fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(20.dp))

            if (ui.loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF00FF77))
                }
                return@Column
            }

            if (ui.error != null) {
                Text(ui.error ?: "Error desconocido", color = Color.Red)
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = { vm.cargarAmigos() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF77))
                ) { Text("Reintentar", color = Color(0xFF0B3D2E)) }
                return@Column
            }

            if (ui.amigos.isEmpty() && ui.solicitudesPendientes.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes amigos ni solicitudes aún 👋", color = Color.White, fontSize = 16.sp)
                }
                return@Column
            }

            if (ui.solicitudesPendientes.isNotEmpty()) {
                Text("Solicitudes pendientes", color = Color.White, fontWeight = FontWeight.Bold)
                LazyColumn {
                    items(ui.solicitudesPendientes) { solicitud ->
                        AmigoCard(
                            amigo = solicitud,
                            onAceptar = {
                                vm.aceptarSolicitud(
                                    solicitud.id,
                                    onSuccess = { scope.launch { snackbar.showSnackbar("Solicitud aceptada ✅") } },
                                    onError = { e -> scope.launch { snackbar.showSnackbar("⚠️ $e") } }
                                )
                            },
                            onEliminar = {
                                vm.eliminarAmigo(
                                    solicitud.id,
                                    onSuccess = { scope.launch { snackbar.showSnackbar("Solicitud eliminada ❌") } },
                                    onError = { e -> scope.launch { snackbar.showSnackbar("⚠️ $e") } }
                                )
                            },
                            esSolicitud = true
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            if (ui.amigos.isNotEmpty()) {
                Text("Tus amigos", color = Color.White, fontWeight = FontWeight.Bold)
                LazyColumn {
                    items(ui.amigos) { amigo ->
                        AmigoCard(
                            amigo = amigo,
                            onAceptar = {},
                            onEliminar = {
                                vm.eliminarAmigo(
                                    amigo.id,
                                    onSuccess = { scope.launch { snackbar.showSnackbar("Amigo eliminado ❌") } },
                                    onError = { e -> scope.launch { snackbar.showSnackbar("⚠️ $e") } }
                                )
                            },
                            esSolicitud = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AmigoCard(
    amigo: Amigo,
    onAceptar: () -> Unit,
    onEliminar: () -> Unit,
    esSolicitud: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF173E34))
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(amigo.nombre, color = Color.White, fontWeight = FontWeight.Bold)
                Text(amigo.correo, color = Color.Gray, fontSize = 13.sp)
            }

            if (esSolicitud) {
                Row {
                    IconButton(onClick = onAceptar) {
                        Icon(Icons.Default.Group, contentDescription = null, tint = Color(0xFF00FF77))
                    }
                    IconButton(onClick = onEliminar) {
                        Icon(Icons.Default.PersonRemove, contentDescription = null, tint = Color.Red)
                    }
                }
            } else {
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Default.PersonRemove, contentDescription = null, tint = Color.Red)
                }
            }
        }
    }
}
