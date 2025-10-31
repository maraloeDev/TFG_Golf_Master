package com.maraloedev.golfmaster.view.reservas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maraloedev.golfmaster.viewmodel.ReservasViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservasScreen(vm: ReservasViewModel = viewModel()) {
    val reservas by vm.reservas.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(Unit) { vm.cargar() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { vm.crearReserva() }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva reserva")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when {
                loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                error != null -> Text("Error: $error", color = MaterialTheme.colorScheme.error)
                reservas.isEmpty() -> Text(
                    "No tienes reservas registradas.",
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> LazyColumn {
                    items(reservas) { reserva ->
                        ReservaCard(reserva)
                    }
                }
            }
        }
    }
}

@Composable
fun ReservaCard(reserva: com.maraloedev.golfmaster.model.Reservas) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Reserva de ${reserva.numero_de_jugadores} jugadores",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Recorrido: ${reserva.recorrido_reserva.joinToString(", ")}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Fecha: ${
                    reserva.fecha_reserva?.toDate()?.let(dateFormat::format) ?: "Sin fecha"
                }"
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "Hora: ${
                    reserva.hora_reserva?.toDate()?.let(dateFormat::format) ?: "Sin hora"
                }"
            )
        }
    }
}
