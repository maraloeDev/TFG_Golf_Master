package com.maraloedev.golfmaster.view.splash

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }

    LaunchedEffect(Unit) {
        delay(1500) // animación o pausa visual
        val user = auth.currentUser

        if (user != null) {
            Toast.makeText(context, "Bienvenido, ${user.email}", Toast.LENGTH_SHORT).show()
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
            .background(Color(0xFF0B3D2E)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "GolfMaster",
            color = Color(0xFF00FF77),
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
