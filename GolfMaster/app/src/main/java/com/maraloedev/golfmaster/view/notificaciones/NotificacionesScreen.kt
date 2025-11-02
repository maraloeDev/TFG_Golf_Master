package com.maraloedev.golfmaster.view.notificaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesScreen(
    vm: NotificacionesViewModel = viewModel(),
    onBack: (() -> Unit)? = null
) {
    val notificaciones by vm.notificaciones.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(Unit) { vm.cargarNotificaciones() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones del Club", color = Color.White) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0C1A12))
            )
        },
        containerColor = Color(0xFF00281F)
    ) { padding ->
        Box(
            Modifier
                .padding(padding)
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

                notificaciones.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay notificaciones disponibles", color = Color.White.copy(alpha = 0.7f))
                }

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(notificaciones) { notif ->
                        NotificacionCard(notif)
                    }
                }
            }
        }
    }
}

/* ---------------- CARD ---------------- */
@Composable
private fun NotificacionCard(notif: Notificacion) {
    val formato = remember { SimpleDateFormat("d MMM", Locale("es", "ES")) }
    val fecha = notif.fecha?.toDate()?.let { formato.format(it) } ?: ""

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B12)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(6.dp)
                            .background(Color(0xFF00FF77), shape = MaterialTheme.shapes.small)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(notif.titulo, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Text(fecha, color = Color.White.copy(alpha = 0.6f), fontSize = MaterialTheme.typography.bodySmall.fontSize)
            }
            Spacer(Modifier.height(6.dp))
            Text(notif.descripcion, color = Color.White.copy(alpha = 0.9f))
        }
    }
}
