package com.maraloedev.golfmaster.view.reservas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
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
fun ReservasScreen(vm: ReservasViewModel = viewModel()) {
    val ui by vm.ui.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    Scaffold(
        containerColor = Color(0xFF0B3D2E),
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { vm.crearReserva("2025-10-28", "10:00", "Campo Sur", 2,
                    onSuccess = { scope.launch { snackbar.showSnackbar("Reserva creada ✅") } },
                    onError = { e -> scope.launch { snackbar.showSnackbar("⚠️ $e") } }
                ) },
                containerColor = Color(0xFF00FF77)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir reserva", tint = Color(0xFF0B3D2E))
            }
        }
    ) { pv ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(pv)
                .background(Color(0xFF0B3D2E))
        ) {
            when {
                ui.loading -> {
                    CircularProgressIndicator(
                        color = Color(0xFF00FF77),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                ui.error != null -> {
                    Column(
                        Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(ui.error ?: "Error desconocido", color = Color.Red)
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { vm.cargarReservas() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF77))
                        ) {
                            Text("Reintentar", color = Color(0xFF0B3D2E))
                        }
                    }
                }

                ui.reservas.isEmpty() -> {
                    Text(
                        "No tienes reservas todavía 🏌️‍♂️",
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        items(ui.reservas) { reserva ->
                            ReservaCard(
                                reserva = reserva,
                                onCancel = {
                                    vm.cancelarReserva(
                                        reserva.id,
                                        onSuccess = { scope.launch { snackbar.showSnackbar("Reserva cancelada ❌") } },
                                        onError = { e -> scope.launch { snackbar.showSnackbar("⚠️ $e") } }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReservaCard(reserva: Reserva, onCancel: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF173E34))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (reserva.estado == "Cancelada") Icons.Default.EventBusy else Icons.Default.EventAvailable,
                    contentDescription = null,
                    tint = if (reserva.estado == "Cancelada") Color.Red else Color(0xFF00FF77)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${reserva.campo} - ${reserva.fecha} ${reserva.hora}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(8.dp))
            Text("Jugadores: ${reserva.jugadores}", color = Color.White.copy(alpha = 0.8f))
            Text("Estado: ${reserva.estado}", color = Color.Gray, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            if (reserva.estado != "Cancelada") {
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f))
                ) {
                    Text("Cancelar", color = Color.White)
                }
            }
        }
    }
}
