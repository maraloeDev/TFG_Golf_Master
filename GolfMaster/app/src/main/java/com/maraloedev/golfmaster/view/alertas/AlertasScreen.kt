package com.maraloedev.golfmaster.view.alertas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maraloedev.golfmaster.model.Invitacion
import java.text.SimpleDateFormat
import java.util.*

private val ScreenBg = Color(0xFF00281F)
private val CardBg = Color(0xFF0D1B12)
private val Accent = Color(0xFF00FF77)

@Composable
fun AlertasScreen(vm: AlertasViewModel = viewModel()) {
    val invitaciones by vm.invitaciones.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(Unit) { vm.observarInvitaciones() }

    Scaffold(containerColor = ScreenBg) { pad ->
        Box(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .background(ScreenBg)
        ) {
            when {
                loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Accent)
                }

                error != null -> Text("Error: $error", color = Color.Red)

                invitaciones.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Notifications, contentDescription = null, tint = Color.White.copy(alpha = 0.6f))
                        Spacer(Modifier.height(8.dp))
                        Text("No tienes alertas pendientes", color = Color.White.copy(alpha = 0.7f))
                    }
                }

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(invitaciones, key = { it.id }) { inv ->
                        AmistadCard(
                            inv = inv,
                            onAceptar = { vm.aceptarAmistad(inv.id, inv.de, inv.nombreDe) },
                            onRechazar = { vm.rechazarAmistad(inv.id) },
                            onEliminar = { vm.eliminarAlerta(inv.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AmistadCard(
    inv: Invitacion,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit,
    onEliminar: () -> Unit
) {
    ElevatedCard(colors = CardDefaults.cardColors(containerColor = CardBg), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Text("👤 Solicitud de amistad de ${inv.nombreDe}", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = onAceptar, colors = ButtonDefaults.buttonColors(containerColor = Accent)) {
                    Text("Aceptar", color = Color.Black, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = onRechazar, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) {
                    Text("Rechazar")
                }
                OutlinedButton(onClick = onEliminar, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)) {
                    Text("Eliminar")
                }
            }
        }
    }
}
