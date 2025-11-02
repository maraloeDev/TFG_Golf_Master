@file:OptIn(ExperimentalMaterial3Api::class)

package com.maraloedev.golfmaster.view.alertas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AlertasScreen(vm: AlertasViewModel = viewModel()) {
    val alertas by vm.alertas.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(Unit) { vm.cargarAlertas() }

    Scaffold(
        containerColor = Color(0xFF00281F),
        topBar = {
            TopAppBar(
                title = { Text("Alertas", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0C1A12))
            )
        }
    ) { pad ->
        Box(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .background(Color(0xFF00281F))
        ) {
            when {
                loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF00FF77))
                }

                error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: $error", color = Color.Red)
                }

                alertas.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("No tienes alertas pendientes", color = Color.White.copy(alpha = 0.7f))
                    }
                }

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(alertas) { alerta ->
                        AlertaCard(alerta)
                    }
                }
            }
        }
    }
}

/* ------------------- CARD DE ALERTA ------------------- */
@Composable
private fun AlertaCard(alerta: Alerta) {
    val formato = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "ES")) }
    val fecha = alerta.fecha?.toDate()?.let { formato.format(it) } ?: "Sin fecha"

    ElevatedCard(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B12)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (alerta.tipo == "advertencia") Icons.Filled.Warning else Icons.Filled.Notifications,
                    contentDescription = null,
                    tint = Color(0xFF00FF77)
                )
                Spacer(Modifier.width(8.dp))
                Text(alerta.titulo, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(6.dp))
            Text(alerta.descripcion, color = Color.White.copy(alpha = 0.9f))
            Spacer(Modifier.height(6.dp))
            Text(fecha, color = Color(0xFF6BF47F), fontSize = MaterialTheme.typography.bodySmall.fontSize)
        }
    }
}
