package com.maraloedev.golfmaster.view.notificaciones

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maraloedev.golfmaster.model.Notificacion
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificacionesScreen(vm: NotificacionesViewModel = viewModel()) {
    val notificaciones by vm.notificaciones.collectAsState()
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()) }

    when {
        notificaciones.isEmpty() -> Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No tienes notificaciones.", color = Color.Gray)
        }

        else -> LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notificaciones) { notif ->
                val fechaFormateada = dateFormat.format(Date(notif.fecha))
                NotificacionCard(notif = notif, vm = vm, fechaFormateada = fechaFormateada)
            }
        }
    }
}

@Composable
private fun NotificacionCard(
    notif: Notificacion,
    vm: NotificacionesViewModel,
    fechaFormateada: String
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF0F4A3B))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                notif.tituloOrDefault(),
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(Modifier.height(6.dp))
            Text(
                notif.mensaje.ifBlank { "Sin contenido" },
                color = Color.White
            )
            Spacer(Modifier.height(8.dp))
            Text(
                fechaFormateada,
                color = Color(0xFFBBA864),
                fontSize = MaterialTheme.typography.labelSmall.fontSize
            )
            Spacer(Modifier.height(12.dp))

            when (notif.estado) {
                "pendiente" -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { vm.aceptarReserva(notif) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF77))
                    ) {
                        Text("Aceptar", color = Color.Black)
                    }

                    OutlinedButton(
                        onClick = { vm.rechazarReserva(notif) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                    ) {
                        Text("Rechazar", color = Color.Red)
                    }
                }

                "aceptada" -> Text("✅ Aceptada", color = Color(0xFF00FF77))
                "rechazada" -> Text("❌ Rechazada", color = Color.Red)
                else -> Text("ℹ️ ${notif.estado}", color = Color(0xFFBBA864))
            }
        }
    }
}

/** 🔹 Extensión para manejar título */
private fun Notificacion.tituloOrDefault(): String {
    return when {
        mensaje.contains("invitado", ignoreCase = true) -> "Invitación de partida"
        else -> "Notificación"
    }
}
