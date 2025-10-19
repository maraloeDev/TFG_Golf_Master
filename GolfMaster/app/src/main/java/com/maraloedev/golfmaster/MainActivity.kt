package com.maraloedev.golfmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.maraloedev.golfmaster.ui.theme.GolfMasterTheme
import com.maraloedev.golfmaster.view.core.navigation.NavigationWrapper

/**
 * Actividad principal del proyecto.
 * Renderiza todo el contenido Jetpack Compose y maneja la navegación.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GolfMasterAppContent()
        }
    }
}

@Composable
fun GolfMasterAppContent() {
    GolfMasterTheme {
        Surface(
            color = Color(0xFF0B3D2E) // Fondo principal corporativo
        ) {
            val navController = rememberNavController()
            NavigationWrapper(navController = navController)
        }
    }
}
