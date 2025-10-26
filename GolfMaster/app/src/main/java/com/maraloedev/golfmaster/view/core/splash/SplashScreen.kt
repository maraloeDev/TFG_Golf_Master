package com.maraloedev.golfmaster.view.core.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maraloedev.golfmaster.vm.AuthViewModel
import com.maraloedev.golfmaster.view.core.navigation.NavRoutes
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, vm: AuthViewModel = viewModel()) {
    val fondo = Color(0xFF0A1A0E)
    val verde = Color(0xFF2BD67B)

    // Comprobar sesión tras pequeña espera
    LaunchedEffect(Unit) {
        delay(1800)
        val loggedIn = vm.isUserLoggedIn()
        if (loggedIn)
            navController.navigate(NavRoutes.INICIO) {
                popUpTo(NavRoutes.SPLASH) { inclusive = true }
            }
        else
            navController.navigate(NavRoutes.LOGIN) {
                popUpTo(NavRoutes.SPLASH) { inclusive = true }
            }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(fondo),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("GolfMaster", color = verde, fontSize = 36.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            CircularProgressIndicator(color = verde)
        }
    }
}
