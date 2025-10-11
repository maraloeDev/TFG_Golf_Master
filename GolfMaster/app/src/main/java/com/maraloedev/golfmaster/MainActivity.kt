package com.maraloedev.golfmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.maraloedev.golfmaster.view.core.navigation.Alertas
import com.maraloedev.golfmaster.view.core.navigation.Amigos
import com.maraloedev.golfmaster.view.core.navigation.Eventos
import com.maraloedev.golfmaster.view.core.navigation.Inicio
import com.maraloedev.golfmaster.view.core.navigation.NavigationWrapper
import com.maraloedev.golfmaster.view.core.navigation.Reservas

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            Scaffold(
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                NavigationWrapper(navController = navController, modifier = Modifier.padding(innerPadding))
            }
        }
    }
}

data class Screen(val name: String, val route: String, val icon: Int)

val screens = listOf(
    Screen("Reservar", Reservas.toString(), R.drawable.ic_reserva),
    Screen("Eventos", Eventos.toString(), R.drawable.ic_eventos),
    Screen("Inicio", Inicio.toString(), R.drawable.ic_inicio),
    Screen("Amigos", Amigos.toString(), R.drawable.ic_amigos),
    Screen("Alertas", Alertas.toString(), R.drawable.ic_notificacion)
)

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
                        modifier = Modifier.size(25.dp)
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
