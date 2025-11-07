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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*

/* 🎨 Colores globales */
private val ScreenBg = Color(0xFF00281F)
private val CardBg = Color(0xFF0D1B12)
private val Accent = Color(0xFF00FF77)

/* ============================================================
   🟩 PANTALLA DE ALERTAS (Invitaciones)
   ============================================================ */
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

                error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "⚠️ Error al cargar alertas",
                            color = Color.Yellow,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            error ?: "",
                            color = Color.Red.copy(alpha = 0.9f),
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "📘 Si el error menciona un índice, créalo en Firebase Console → Firestore → Indexes.",
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }

                invitaciones.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("No tienes invitaciones", color = Color.White.copy(alpha = 0.7f))
                    }
                }

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 🔒 Claves seguras aunque el id esté vacío
                    items(
                        items = invitaciones,
                        key = { inv -> inv.id.ifBlank { inv.hashCode().toString() } }
                    ) { inv ->
                        InvitacionCard(
                            inv = inv,
                            onAceptar = { vm.aceptarInvitacion(inv.id) },
                            onRechazar = { vm.rechazarInvitacion(inv.id) }
                        )
                    }
                }
            }
        }
    }
}

/* ============================================================
   🟩 CARD DE INVITACIÓN
   ============================================================ */
@Composable
private fun InvitacionCard(
    inv: Invitacion,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit
) {
    val formato = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "ES")) }
    val fecha = inv.fecha?.toDate()?.let { formato.format(it) } ?: "—"

    ElevatedCard(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Text("Invitación de juego", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("Estado: ${inv.estado}", color = Color.White.copy(alpha = .9f))
            Text("Fecha: $fecha", color = Color.White.copy(alpha = .9f))
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onAceptar,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent)
                ) { Text("Aceptar", color = Color.Black, fontWeight = FontWeight.Bold) }

                OutlinedButton(
                    onClick = onRechazar,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = ButtonDefaults.outlinedButtonBorder(true)
                ) { Text("Rechazar") }
            }
        }
    }
}
