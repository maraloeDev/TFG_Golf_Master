// Este archivo gestiona la navegación principal de la app GolfMaster usando Jetpack Compose.
// Incluye la definición de las pantallas, el wrapper de navegación y la barra de navegación inferior.

package com.maraloedev.golfmaster.view.core.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.maraloedev.golfmaster.R
import com.maraloedev.golfmaster.view.inicio.InicioScreen
import com.maraloedev.golfmaster.view.eventos.EventosScreen
import com.maraloedev.golfmaster.view.amigos.AmigosScreen
import com.maraloedev.golfmaster.view.alertas.AlertasScreen
import com.maraloedev.golfmaster.view.reservas.ReservasScreen

// Definición de las pantallas principales de la app y sus iconos
sealed class Screen(val route: String, val icon: Int) {
    object Reservar : Screen(route = "reservar", icon = R.drawable.ic_reserva)
    object Eventos : Screen(route = "eventos", icon = R.drawable.ic_eventos)
    object Inicio : Screen(route = "inicio", icon = R.drawable.ic_inicio)
    object Amigos : Screen(route = "amigos", icon = R.drawable.ic_amigos)
    object Alertas : Screen(route = "notificaciones", icon =R.drawable.ic_notificacion)
}

// Lista de pantallas para la barra de navegación inferior
val screens = listOf(
    Screen.Reservar,
    Screen.Eventos,
    Screen.Inicio,
    Screen.Amigos,
    Screen.Alertas
)

// Composable principal que envuelve la navegación de la app
@Composable
fun NavigationWrapper() {
    val navController = rememberNavController() // Controlador de navegación
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) } // Barra de navegación inferior
    ) { innerPadding ->
        // NavHost gestiona el cambio entre pantallas
        NavHost(
            navController = navController,
            startDestination = Screen.Inicio.route, // Pantalla inicial
            modifier = Modifier.padding(paddingValues = innerPadding)
        ) {
            // Definición de las rutas y sus composables
            composable(route = Screen.Reservar.route) { ReservasScreen() }
            composable(route = Screen.Eventos.route) { EventosScreen() }
            composable(route = Screen.Inicio.route) { InicioScreen() }
            composable(route = Screen.Amigos.route) { AmigosScreen() }
            composable(route = Screen.Alertas.route) { AlertasScreen() }
        }
    }
}

// Composable para la barra de navegación inferior
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState() // Estado de la navegación
    val currentRoute = navBackStackEntry?.destination?.route // Ruta actual
    NavigationBar {
        screens.forEach { screen ->
            NavigationBarItem(
                icon = {
                    // Icono de cada pantalla
                    Image(
                        painterResource(id = screen.icon),
                        contentDescription = null,
                        modifier = Modifier.size(38.dp),
                    )
                },
                selected = currentRoute == screen.route, // Indica si está seleccionada
                onClick = {
                    // Navega solo si no está ya en la ruta
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
