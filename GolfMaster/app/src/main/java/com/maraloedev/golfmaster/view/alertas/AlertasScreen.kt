package com.maraloedev.golfmaster.view.alertas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maraloedev.golfmaster.viewmodel.NotificacionesViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AlertasScreen(vm: NotificacionesViewModel = viewModel()) {
    val ui by vm.ui.collectAsState()
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()) }

    when {
        ui.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        ui.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: ${ui.error}", color = MaterialTheme.colorScheme.error)
        }

        ui.notificaciones.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay notificaciones disponibles.")
        }

        else -> LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(ui.notificaciones) { notif ->
                NotificacionCard(
                    titulo = notif.titulo,
                    mensaje = notif.mensaje,
                    fecha = notif.fecha?.toDate()?.let { dateFormat.format(it) } ?: "Sin fecha"
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun NotificacionCard(titulo: String, mensaje: String, fecha: String) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(titulo, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(mensaje)
            Spacer(Modifier.height(6.dp))
            Text(
                fecha,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
