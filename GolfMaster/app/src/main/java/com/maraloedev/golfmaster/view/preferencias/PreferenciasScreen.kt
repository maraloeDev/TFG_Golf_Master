package com.maraloedev.golfmaster.view.preferencias

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PreferenciasScreen() {
    var modoOscuro by remember { mutableStateOf(false) }
    var notificaciones by remember { mutableStateOf(true) }

    Scaffold(containerColor = if (modoOscuro) Color.Black else Color(0xFF0B3D2E)) { pv ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pv)
                .padding(24.dp)
        ) {
            Text("Preferencias", color = Color.White, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(24.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Modo oscuro", color = Color.White)
                Switch(checked = modoOscuro, onCheckedChange = { modoOscuro = it })
            }

            Spacer(Modifier.height(16.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Recibir notificaciones", color = Color.White)
                Switch(checked = notificaciones, onCheckedChange = { notificaciones = it })
            }
        }
    }
}
