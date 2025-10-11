package com.maraloedev.golfmaster.view.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.maraloedev.golfmaster.view.inicio.InicioScreen

@Composable
fun NavigationWrapper(navController: NavController, modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Inicio
    ) {
        composable <Inicio> {
            InicioScreen()
        }
    }
}