package com.maraloedev.golfmaster.view.amigos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maraloedev.golfmaster.R

@Composable
fun AmigosScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF5A9149)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo_app),
                contentDescription = "Logo GolfMaster",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Amigos", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Conecta con otros golfistas.", fontSize = 16.sp)
        }
    }
}

