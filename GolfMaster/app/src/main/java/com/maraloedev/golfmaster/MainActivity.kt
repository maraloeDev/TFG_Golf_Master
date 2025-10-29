package com.maraloedev.golfmaster

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.maraloedev.golfmaster.view.core.navigation.NavRoutes
import com.maraloedev.golfmaster.view.core.navigation.NavigationWrapper
import com.maraloedev.golfmaster.vm.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val authViewModel = AuthViewModel()

            // Verificamos si hay sesión activa
            val startDestination = if (authViewModel.haySesionActiva()) {
                NavRoutes.INICIO
            } else {
                NavRoutes.LOGIN
            }

            Surface(color = Color(0xFF0B3D2E)) {
                NavigationWrapper(navController = navController, startDestination = startDestination)
            }
        }
    }
}
