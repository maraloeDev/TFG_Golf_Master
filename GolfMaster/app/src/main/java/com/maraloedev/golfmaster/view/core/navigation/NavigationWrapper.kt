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
import com.maraloedev.golfmaster.view.core.splash.SplashScreen
import com.maraloedev.golfmaster.view.eventos.EventosScreen
import com.maraloedev.golfmaster.view.informacion.InformacionScreen
import com.maraloedev.golfmaster.view.inicio.HomeScreen
import com.maraloedev.golfmaster.view.perfil.PerfilScreen
import com.maraloedev.golfmaster.view.reservas.ReservasScreen


@Composable
fun NavigationWrapper(navController: NavHostController) {
    val fondo = Color(0xFF0A1A0E)

    // === CONTENEDOR PRINCIPAL DE NAVEGACIÓN ===
    NavHost(
        navController = navController,
        startDestination = NavRoutes.SPLASH,
        modifier = Modifier.background(fondo)
    ) {

        // 🟢 === AUTH ===
        composable(route = NavRoutes.SPLASH) {
            SplashScreen(navController = navController)
        }

        composable(route = NavRoutes.LOGIN) {
            LoginScreen(navController = navController)
        }

        composable(route = NavRoutes.REGISTRO) {
            RegisterScreen(navController = navController)
        }

        // 🟢 === PANTALLAS PRINCIPALES ===
        composable(route = NavRoutes.INICIO) {
            HomeScreen(navController = navController)
        }

        composable(route = NavRoutes.RESERVAS) {
            ReservasScreen()
        }

        composable(route = NavRoutes.EVENTOS) {
            EventosScreen()
        }

        composable(route = NavRoutes.PERFIL) {
            PerfilScreen(navController = navController)
        }

        composable(route = NavRoutes.INFORMACION) {
            InformacionScreen(navController = navController)
        }

        // 🟢 === CAMPOS ===
        composable(route = NavRoutes.CORRESPONDENCIA_CAMPOS) {
            CorrespondenciaCamposScreen(navController = navController)
        }

        composable(route = NavRoutes.REGLAS_LOCALES) {
            ReglasLocalesScreen(navController = navController)
        }

        // 🟢 === TORNEOS ===
        composable(route = NavRoutes.TERMINOS_CONDICIONES) {
            TerminosScreen(
                navController = navController
            )
        }
    }
}
