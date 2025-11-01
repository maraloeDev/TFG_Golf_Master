package com.maraloedev.golfmaster.view.alertas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maraloedev.golfmaster.model.Notificacion
import com.maraloedev.golfmaster.view.notificaciones.NotificacionesViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertasScreen(vm: NotificacionesViewModel = viewModel()) {
    val notificaciones by vm.notificaciones.collectAsState()
    val background = Brush.verticalGradient(listOf(Color(0xFF0B3D2E), Color(0xFF173E34)))

    Scaffold(containerColor = Color.Transparent) { pv ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .padding(pv)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Notificaciones",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )

            if (notificaciones.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes notificaciones", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(notificaciones) { notif ->
                        NotificacionCard(notif, vm)
                    }
                }
            }
        }
    }
}

@Composable
fun NotificacionCard(notif: Notificacion, vm: NotificacionesViewModel) {
    val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(notif.fecha))
    val colorEstado = when (notif.estado) {
        "aceptada" -> Color(0xFF00FF77)
        "rechazada" -> Color.Red
        else -> Color(0xFFBBA864)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F4A3B)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(notif.mensaje, color = Color.White, fontSize = 16.sp)
            Text(fecha, color = Color.Gray, fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))

            if (notif.estado == "pendiente") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { vm.aceptarReserva(notif) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF77))
                    ) {
                        Text("Aceptar", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { vm.rechazarReserva(notif) },
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                    ) {
                        Text("Rechazar", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Text(
                    text = "Estado: ${notif.estado.uppercase()}",
                    color = colorEstado,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
