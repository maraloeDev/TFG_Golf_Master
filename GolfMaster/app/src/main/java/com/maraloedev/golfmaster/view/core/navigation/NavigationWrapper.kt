// Este archivo gestiona la navegación principal de la app GolfMaster usando Jetpack Compose.
// Incluye la definición de las pantallas, el wrapper de navegación y la barra de navegación inferior.

package com.maraloedev.golfmaster.view.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.maraloedev.golfmaster.R
import com.maraloedev.golfmaster.view.alertas.AlertasScreen
import com.maraloedev.golfmaster.view.amigos.AmigosScreen
import com.maraloedev.golfmaster.view.auth.login.LoginScreen
import com.maraloedev.golfmaster.view.auth.register.RegisterScreen
import com.maraloedev.golfmaster.view.eventos.EventosScreen
import com.maraloedev.golfmaster.view.inicio.InicioScreen
import com.maraloedev.golfmaster.view.reservas.ReservasScreen

// Definición de las pantallas principales de la app y sus iconos
sealed class Screen(val route: String, val icon: Int) {
    object Login : Screen(route = "login", icon = R.drawable.ic_login)
    object Register : Screen(route = "registro", icon = R.drawable.ic_login)
    object Reservar : Screen(route = "reservar", icon = R.drawable.ic_reserva)
    object Eventos : Screen(route = "eventos", icon = R.drawable.ic_eventos)
    object Inicio : Screen(route = "inicio", icon = R.drawable.ic_inicio)
    object Amigos : Screen(route = "amigos", icon = R.drawable.ic_amigos)
    object Alertas : Screen(route = "notificaciones", icon = R.drawable.ic_notificacion)
}

// Lista de pantallas para la barra de navegación inferior
val screens = listOf(
    Screen.Reservar,
    Screen.Eventos,
    Screen.Inicio,
    Screen.Amigos,
    Screen.Alertas,
    Screen.Login,
    Screen.Register
)

// Composable principal que envuelve la navegación de la app
@Composable
fun NavigationWrapper() {
    val navController = rememberNavController() // Controlador de navegación
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // Definición de las rutas y sus composables
        composable(route = Screen.Login.route) { LoginScreen(
            navController = navController
        ) }
        composable(route = Screen.Reservar.route) { ReservasScreen() }
        composable(route = Screen.Eventos.route) { EventosScreen() }
        composable(route = Screen.Inicio.route) { InicioScreen() }
        composable(route = Screen.Amigos.route) { AmigosScreen() }
        composable(route = Screen.Alertas.route) { AlertasScreen() }
        composable(route = Screen.Register.route) { RegisterScreen(navController = navController) }
    }
}