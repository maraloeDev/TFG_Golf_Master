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

sealed class Screen(val route: String, val icon: Int) {
    object Reservar : Screen("reservar", R.drawable.ic_reserva)
    object Eventos : Screen("eventos", R.drawable.ic_eventos)
    object Inicio : Screen("inicio", R.drawable.ic_inicio)
    object Amigos : Screen("amigos", R.drawable.ic_amigos)
    object Alertas : Screen("notificaciones", R.drawable.ic_notificacion)
}

val screens = listOf(
    Screen.Reservar,
    Screen.Eventos,
    Screen.Inicio,
    Screen.Amigos,
    Screen.Alertas
)

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Inicio.route,
            modifier = Modifier.padding(paddingValues = innerPadding)
        ) {
            composable(route = Screen.Reservar.route) { ReservasScreen() }
            composable(route = Screen.Eventos.route) { EventosScreen() }
            composable(route = Screen.Inicio.route) { InicioScreen() }
            composable(route = Screen.Amigos.route) { AmigosScreen() }
            composable(route = Screen.Alertas.route) { AlertasScreen() }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    NavigationBar {
        screens.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Image(
                        painterResource(id = screen.icon),
                        contentDescription = null,
                        modifier = Modifier.size(38.dp),
                        colorFilter = ColorFilter.tint(Color(0xFF599149))
                    )
                },
                selected = currentRoute == screen.route,
                onClick = {
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

