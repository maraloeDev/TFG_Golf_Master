package com.maraloedev.golfmaster.view.alertas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
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
fun AlertasScreen(vm: AlertasViewModel = viewModel()) {
    val ui by vm.ui.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color(0xFF0B3D2E),
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("Alertas", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B3D2E))
            )
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
                            onClick = { vm.cargarAlertas() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF77))
                        ) {
                            Text("Reintentar", color = Color(0xFF0B3D2E))
                        }
                    }
                }

                ui.alertas.isEmpty() -> {
                    Text(
                        "No tienes alertas 📭",
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
                        items(ui.alertas) { alerta ->
                            AlertaCard(
                                alerta = alerta,
                                onMarcarLeida = { vm.marcarLeida(alerta.id) },
                                onEliminar = {
                                    vm.eliminarAlerta(alerta.id) { e ->
                                        scope.launch { snackbar.showSnackbar("⚠️ $e") }
                                    }
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
fun AlertaCard(
    alerta: Alerta,
    onMarcarLeida: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { if (!alerta.leida) onMarcarLeida() },
        colors = CardDefaults.cardColors(
            containerColor = if (alerta.leida) Color(0xFF173E34) else Color(0xFF1E5C47)
        )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when {
                            alerta.leida -> Icons.Default.NotificationsOff
                            alerta.titulo.contains("reserva", true) -> Icons.Default.NotificationsActive
                            else -> Icons.Default.Notifications
                        },
                        contentDescription = null,
                        tint = if (alerta.leida) Color.Gray else Color(0xFF00FF77)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(alerta.titulo, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(4.dp))
                Text(alerta.mensaje, color = Color.White.copy(alpha = 0.9f))
                Spacer(Modifier.height(4.dp))
                Text(alerta.fecha, color = Color.Gray, fontSize = 12.sp)
            }

            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.NotificationsOff, contentDescription = null, tint = Color.Red)
            }
        }
    }
}
