package com.maraloedev.golfmaster.view.inicio

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InicioScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "GolfMaster", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Cada golpe es una nueva oportunidad para la grandeza.", fontSize = 16.sp)
        }
    }
}

