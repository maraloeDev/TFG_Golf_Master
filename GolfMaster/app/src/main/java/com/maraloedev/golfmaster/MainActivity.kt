package com.maraloedev.golfmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.maraloedev.golfmaster.view.core.navigation.MainNavHost
import com.maraloedev.golfmaster.ui.theme.GolfMasterTheme

/**
 * Activity principal de la aplicación (Single-Activity Architecture).
 *
 * - Se encarga de montar toda la interfaz mediante Jetpack Compose.
 * - Implementa el patrón Single Activity: toda la navegación se gestiona
 *   mediante NavHost dentro de Compose, evitando múltiples Activities.
 *
 * Flujo:
 *  1. Se crea un NavController (gestiona navegación entre pantallas).
 *  2. MainNavHost define todas las rutas disponibles.
 *  3. GolfMasterTheme aplica la paleta personalizada de la app.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Jetpack Compose inicializa la UI
        setContent {

            // 🎨 Tema visual corporativo de GolfMaster
            GolfMasterTheme {

                // 🔹 Controlador de navegación
                val navController = rememberNavController()

                // 🔹 Árbol de navegación principal
                MainNavHost(navController)
            }
        }
    }
}
