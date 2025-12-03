package com.maraloedev.golfmaster.view.core.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import androidx.compose.animation.core.*
import androidx.compose.foundation.shape.CircleShape

@Composable
fun SplashScreen(navController: NavController) {
    val colors = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        delay(1300)
        val user = FirebaseAuth.getInstance().currentUser

        navController.navigate(if (user != null) "home" else "login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            GlowingLogo(colors)
            Spacer(Modifier.height(30.dp))

            Text(
                "GolfMaster",
                color = colors.primary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(10.dp))

            Text(
                "Cargando experiencia golfística...",
                color = colors.onBackground.copy(alpha = .7f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun GlowingLogo(colors: ColorScheme) {
    val infiniteTransition = rememberInfiniteTransition()

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = EaseInOutQuad),
            RepeatMode.Reverse
        )
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = EaseInOutQuad),
            RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .size(130.dp)
            .clip(CircleShape)
            .background(colors.primary.copy(alpha = 0.1f))
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(colors.primary.copy(alpha = alpha))
        )

        Icon(
            imageVector = Icons.Default.MyLocation,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(60.dp)
        )
    }
}
