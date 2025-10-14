package com.maraloedev.golfmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.auth.FirebaseAuth
import com.maraloedev.golfmaster.view.core.navigation.NavigationWrapper
import com.maraloedev.golfmaster.view.core.navigation.screens


class MainActivity : ComponentActivity() {
    // Variable de autenticación Firebase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Inicializa la instancia de FirebaseAuth
        auth = FirebaseAuth.getInstance()
        setContent {
            // Inicializa la navegación principal de la app
            NavigationWrapper()
        }
    }
}

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