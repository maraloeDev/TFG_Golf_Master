package com.maraloedev.golfmaster.view.inicio

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun InicioContent() {
    Surface(color = Color.Transparent) {
        Text(
            text = "Bienvenido a GolfMaster 🏌️",
            color = Color.White,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge
        )
    }
}
