package com.maraloedev.golfmaster.view.notificaciones

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
fun NotificacionesScreen(vm: NotificacionesViewModel = viewModel()) {
    val ui by vm.ui.collectAsState()
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()) }

    // Si quieres recargar manualmente desde otra parte, expón vm.cargarNotificaciones()

    when {
        ui.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        ui.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: ${ui.error}", color = MaterialTheme.colorScheme.error)
        }

        ui.notificaciones.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No tienes notificaciones.")
        }

        else -> LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ui.notificaciones) { notif ->
                NotificacionCard(
                    titulo = notif.titulo,
                    mensaje = notif.mensaje,
                    fechaFormateada = notif.fecha?.toDate()?.let(dateFormat::format) ?: "Sin fecha",
                )
            }
        }
    }
}

@Composable
private fun NotificacionCard(
    titulo: String,
    mensaje: String,
    fechaFormateada: String,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                titulo.ifBlank { "Notificación" },
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(6.dp))
            Text(
                mensaje.ifBlank { "Sin contenido" },
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                fechaFormateada,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
