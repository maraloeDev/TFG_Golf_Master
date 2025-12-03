package com.maraloedev.golfmaster.view.core.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Text
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
    LaunchedEffect(Unit) {
        delay(1600)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00281F)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            GlowingLogo() // 🟢 Logo animado con el icono del Home
            Spacer(Modifier.height(30.dp))
            Text(
                "GolfMaster",
                color = Color(0xFF00FF77),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Cargando experiencia golfística...",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }
    }
}

/* ============================================================
   🟢 LOGO ANIMADO — igual que el de HomeLandingContent
   ============================================================ */
@Composable
private fun GlowingLogo() {
    val infiniteTransition = rememberInfiniteTransition(label = "logoGlow")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = EaseInOutQuad),
            RepeatMode.Reverse
        ),
        label = "scaleAnim"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = EaseInOutQuad),
            RepeatMode.Reverse
        ),
        label = "alphaAnim"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(130.dp)
            .clip(CircleShape)
            .background(Color(0xFF00FF77).copy(alpha = 0.1f))
            .scale(scale)
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color(0xFF00FF77).copy(alpha = alpha))
        )
        androidx.compose.material3.Icon(
            imageVector = Icons.Filled.MyLocation,
            contentDescription = "Logo",
            tint = Color(0xFF00FF77),
            modifier = Modifier.size(60.dp)
        )
    }
}
