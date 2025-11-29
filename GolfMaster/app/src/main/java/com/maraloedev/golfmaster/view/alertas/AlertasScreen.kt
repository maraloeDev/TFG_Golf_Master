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
import com.maraloedev.golfmaster.model.AlertaAmistad
import com.maraloedev.golfmaster.model.Invitacion
import com.maraloedev.golfmaster.view.reservas.ReservasViewModel
import java.text.SimpleDateFormat
import java.util.Locale

private val ScreenBg = Color(0xFF00281F)
private val CardBg = Color(0xFF0D1B12)
private val Accent = Color(0xFF00FF77)

@Composable
fun AlertasScreen(
    vmAlertas: AlertasViewModel = viewModel(),
    vmReservas: ReservasViewModel = viewModel()
) {
    // 🧑‍🤝‍🧑 Solicitudes de amistad
    val invitacionesAmistad by vmAlertas.invitaciones.collectAsState()
    val loadingAmistad by vmAlertas.loading.collectAsState()
    val errorAmistad by vmAlertas.error.collectAsState()

    // ⛳ Invitaciones a reserva
    val invitacionesReserva by vmReservas.invitacionesPendientes.collectAsState()

    LaunchedEffect(Unit) {
        vmAlertas.observarInvitaciones()
        // ReservasViewModel ya engancha listeners en init, pero por si acaso:
        vmReservas.cargarInvitacionesPendientes()
    }

    Scaffold(containerColor = ScreenBg) { pad ->
        Box(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .background(ScreenBg)
        ) {
            when {
                loadingAmistad -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Accent)
                }

                errorAmistad != null -> Text("Error: $errorAmistad", color = Color.Red)

                invitacionesAmistad.isEmpty() && invitacionesReserva.isEmpty() -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.6f)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "No tienes alertas pendientes",
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 🧑‍🤝‍🧑 ALERTAS DE AMISTAD
                    items(invitacionesAmistad, key = { it.id }) { inv ->
                        AmistadCard(
                            inv = inv,
                            onAceptar = { vmAlertas.aceptarAmistad(inv.id, inv.de, inv.nombreDe) },
                            onRechazar = { vmAlertas.rechazarAmistad(inv.id) }
                        )
                    }

                    // ⛳ INVITACIONES A RESERVA
                    items(invitacionesReserva, key = { it.id }) { invReserva ->
                        InvitacionReservaCard(
                            inv = invReserva,
                            onAceptar = {
                                vmReservas.responderInvitacion(invReserva, aceptar = true)
                            },
                            onRechazar = {
                                vmReservas.responderInvitacion(invReserva, aceptar = false)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AmistadCard(
    inv: AlertaAmistad,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit
) {
    ElevatedCard(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                text = "👤 Solicitud de amistad de ${inv.nombreDe}",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAceptar,
                    colors = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    Text("Aceptar", color = Color.Black, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onRechazar,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("Rechazar")
                }
            }
        }
    }
}

@Composable
fun InvitacionReservaCard(
    inv: Invitacion,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit
) {
    val df = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "ES")) }
    val fechaTexto = inv.fecha?.toDate()?.let(df::format) ?: "fecha sin definir"
    val nombre = if (inv.nombreDe.isNotBlank()) inv.nombreDe else "un jugador"

    ElevatedCard(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                text = "⛳ $nombre te ha invitado a una reserva",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Fecha y hora: $fechaTexto",
                color = Color.White.copy(alpha = 0.85f)
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAceptar,
                    colors = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    Text("Aceptar", color = Color.Black, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onRechazar,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("Rechazar")
                }
            }
        }
    }
}
