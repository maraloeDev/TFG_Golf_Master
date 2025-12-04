@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maraloedev.golfmaster.model.AlertaAmistad
import com.maraloedev.golfmaster.model.Invitacion
import com.maraloedev.golfmaster.view.reservas.ReservasViewModel
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Pantalla de alertas:
 *  - Solicitudes de amistad
 *  - Invitaciones a reservas
 *
 * Se apoya en:
 *  - AlertasViewModel → amistad
 *  - ReservasViewModel → invitaciones a reserva
 */
@Composable
fun AlertasScreen(
    vmAlertas: AlertasViewModel = viewModel(),
    vmReservas: ReservasViewModel = viewModel()
) {
    val colors = MaterialTheme.colorScheme

    //  Solicitudes de amistad
    val invitacionesAmistad by vmAlertas.invitaciones.collectAsState()
    val loadingAmistad by vmAlertas.loading.collectAsState()
    val errorAmistad by vmAlertas.error.collectAsState()

    //  Invitaciones a reserva
    val invitacionesReserva by vmReservas.invitacionesPendientes.collectAsState()

    // Lanzamos la observación de datos una vez al entrar en la pantalla
    LaunchedEffect(Unit) {
        vmAlertas.observarInvitaciones()
        vmReservas.cargarInvitacionesPendientes()
    }

    Scaffold(
        containerColor = colors.background,

    ) { pad ->
        Box(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .background(colors.background)
        ) {
            when {
                //  Cargando solicitudes de amistad
                loadingAmistad -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colors.primary)
                }

                //  Error en la carga (solo de amistad, pero puede bastar para la pantalla)
                errorAmistad != null -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error al cargar alertas:\n$errorAmistad",
                        color = colors.error
                    )
                }

                // Sin alertas de ningún tipo
                invitacionesAmistad.isEmpty() && invitacionesReserva.isEmpty() -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = null,
                            tint = colors.onBackground.copy(alpha = 0.6f)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "No tienes alertas pendientes",
                            color = colors.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }

                //  Hay alguna alerta
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    //  ALERTAS DE AMISTAD
                    items(invitacionesAmistad, key = { it.id }) { inv ->
                        AmistadCard(
                            inv = inv,
                            onAceptar = {
                                vmAlertas.aceptarAmistad(
                                    alertaId = inv.id,
                                    deUid = inv.de,
                                    nombreDe = inv.nombreDe
                                )
                            },
                            onRechazar = {
                                vmAlertas.rechazarAmistad(inv.id)
                            }
                        )
                    }

                    // INVITACIONES A RESERVA
                    items(invitacionesReserva, key = { it.id }) { invReserva ->
                        InvitacionReservaCard(
                            inv = invReserva,
                            onAceptar = {
                                vmReservas.responderInvitacion(
                                    invitacion = invReserva,
                                    aceptar = true
                                )
                            },
                            onRechazar = {
                                vmReservas.responderInvitacion(
                                    invitacion = invReserva,
                                    aceptar = false
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta que representa una solicitud de amistad individual.
 */
@Composable
fun AmistadCard(
    inv: AlertaAmistad,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    ElevatedCard(
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                text = " Solicitud de amistad de ${inv.nombreDe}",
                color = colors.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAceptar,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    )
                ) {
                    Text("Aceptar", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onRechazar,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colors.onSurface
                    )
                ) {
                    Text("Rechazar")
                }
            }
        }
    }
}

/**
 * Tarjeta que representa una invitación a una reserva.
 */
@Composable
fun InvitacionReservaCard(
    inv: Invitacion,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    // Formateador de fecha recordado para no recrearlo en cada recomposición
    val df = remember {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "ES"))
    }

    val fechaTexto = inv.fecha?.toDate()?.let(df::format) ?: "Fecha sin definir"
    val nombre = if (inv.nombreDe.isNotBlank()) inv.nombreDe else "un jugador"

    ElevatedCard(
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                text = " $nombre te ha invitado a una reserva",
                color = colors.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Fecha y hora: $fechaTexto",
                color = colors.onSurface.copy(alpha = 0.85f)
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAceptar,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    )
                ) {
                    Text("Aceptar", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onRechazar,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colors.onSurface
                    )
                ) {
                    Text("Rechazar")
                }
            }
        }
    }
}
