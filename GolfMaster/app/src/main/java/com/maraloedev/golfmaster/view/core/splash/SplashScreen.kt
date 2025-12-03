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
import androidx.compose.material3.Icon

/**
 * Pantalla de Splash:
 *
 * - Muestra un logo animado y texto mientras la app "carga".
 * - Tras un pequeño delay, comprueba si hay usuario logueado.
 *   - Si hay usuario → navega a "home".
 *   - Si no lo hay → navega a "login".
 */
@Composable
fun SplashScreen(navController: NavController) {

    // ✅ Obtenemos la instancia de FirebaseAuth solo una vez por composición
    val auth = remember { FirebaseAuth.getInstance() }

    // Efecto que se ejecuta una sola vez cuando entra la pantalla
    LaunchedEffect(Unit) {
        // Pequeña pausa para que se vea el splash
        delay(1600)

        val user = auth.currentUser

        // 🚀 Navegación según si el usuario está logueado o no
        if (user != null) {
            navController.navigate("home") {   // aquí podrías usar Routes.HOME si tienes constantes
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("login") {  // idem con Routes.LOGIN
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    // ==========================
    // 🎨 CONTENIDO VISUAL
    // ==========================
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00281F)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // Logo animado
            GlowingLogo()

            Spacer(Modifier.height(30.dp))

            // Nombre de la app
            Text(
                text = "GolfMaster",
                color = Color(0xFF00FF77),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(10.dp))

            // Subtítulo / mensaje de carga
            Text(
                text = "Cargando experiencia golfística...",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }
    }
}

/* ============================================================
   🟢 LOGO ANIMADO
   - Usa una animación infinita de escala y alpha para simular
     un "pulso" luminoso alrededor del icono.
   ============================================================ */
@Composable
private fun GlowingLogo() {
    // Transición infinita para animar valor de escala y alpha
    val infiniteTransition = rememberInfiniteTransition(label = "logoGlow")

    // Animamos la escala (tamaño) del círculo exterior
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scaleAnim"
    )

    // Animamos la opacidad del brillo interior
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alphaAnim"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(130.dp)
            .clip(CircleShape)
            // Halo exterior suave
            .background(Color(0xFF00FF77).copy(alpha = 0.1f))
            .scale(scale) // aplicamos la animación de escala al conjunto
    ) {
        // Círculo brillante interior
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color(0xFF00FF77).copy(alpha = alpha))
        )

        // Icono central (puedes cambiar MyLocation por un icono de golf si quieres)
        Icon(
            imageVector = Icons.Filled.MyLocation,
            contentDescription = "Logo",
            tint = Color(0xFF00FF77),
            modifier = Modifier.size(60.dp)
        )
    }
}
