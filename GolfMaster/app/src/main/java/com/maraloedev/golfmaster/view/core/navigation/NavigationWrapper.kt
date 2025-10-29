package com.maraloedev.golfmaster.view.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.maraloedev.golfmaster.view.perfil.PerfilScreen
import com.maraloedev.golfmaster.view.reservas.ReservasScreen
import com.maraloedev.golfmaster.view.amigos.AmigosScreen
import com.maraloedev.golfmaster.view.alertas.AlertasScreen
import com.maraloedev.golfmaster.view.auth.login.LoginScreen
import com.maraloedev.golfmaster.view.auth.register.RegisterScreen
import com.maraloedev.golfmaster.view.preferencias.PreferenciasScreen
import com.maraloedev.golfmaster.view.informacion.InformacionScreen
import com.maraloedev.golfmaster.view.inicio.HomeScreen

@Composable
fun NavigationWrapper(
    navController: NavHostController,
    startDestination: String = NavRoutes.LOGIN,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // 🔹 Autenticación
        composable(NavRoutes.LOGIN) {
            LoginScreen(navController)
        }
        composable(NavRoutes.REGISTER) {
            RegisterScreen(navController)
        }

        // 🔹 Pantalla principal con Drawer
        composable(NavRoutes.INICIO) {
            HomeScreen(navController)
        }

        // 🔹 Módulos de usuario
        composable(NavRoutes.PERFIL) {
            PerfilScreen(navController)
        }
        composable(NavRoutes.RESERVAS) {
            ReservasScreen()
        }
        composable(NavRoutes.AMIGOS) {
            AmigosScreen()
        }
        composable(NavRoutes.ALERTAS) {
            AlertasScreen()
        }
        composable(NavRoutes.PREFERENCIAS) {
            PreferenciasScreen()
        }
        composable(NavRoutes.INFORMACION) {
            InformacionScreen()
        }
    }
}
