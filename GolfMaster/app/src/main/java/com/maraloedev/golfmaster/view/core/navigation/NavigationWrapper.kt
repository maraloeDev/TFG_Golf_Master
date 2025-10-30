package com.maraloedev.golfmaster.view.core.navigation

import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.maraloedev.golfmaster.view.auth.login.LoginScreen
import com.maraloedev.golfmaster.view.auth.register.RegisterScreen
import com.maraloedev.golfmaster.view.campos.correspondencias.CorrespondenciaCamposScreen
import com.maraloedev.golfmaster.view.campos.reglasLocales.ReglasLocalesScreen
import com.maraloedev.golfmaster.view.campos.terminos.TerminosScreen
import com.maraloedev.golfmaster.view.eventos.EventosScreen
import com.maraloedev.golfmaster.view.informacion.InformacionScreen
import com.maraloedev.golfmaster.view.inicio.HomeScreen
import com.maraloedev.golfmaster.view.perfil.PerfilScreen
import com.maraloedev.golfmaster.view.reservas.ReservasScreen
import com.maraloedev.golfmaster.view.splash.SplashScreen

@Composable
fun NavigationWrapper(navController: NavHostController) {
    val fondo = Color(0xFF0A1A0E)

    NavHost(
        navController = navController,
        startDestination = NavRoutes.SPLASH,
        modifier = Modifier.background(fondo)
    ) {
        // 🟢 AUTH
        composable(route = NavRoutes.SPLASH) {
            SplashScreen(navController = navController)
        }
        composable(route = NavRoutes.LOGIN) {
            LoginScreen(navController = navController)
        }
        composable(route = NavRoutes.REGISTRO) {
            RegisterScreen(navController = navController)
        }

        // 🟢 HOME PRINCIPAL
        composable(route = NavRoutes.INICIO) {
            HomeScreen(rootNavController = navController)
        }

        // (no incluir las pantallas internas aquí: están dentro del HomeScreen)
    }
}
