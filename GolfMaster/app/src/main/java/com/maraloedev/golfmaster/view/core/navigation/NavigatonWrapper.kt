package com.maraloedev.golfmaster.view.core.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.maraloedev.golfmaster.view.auth.login.LoginScreen
import com.maraloedev.golfmaster.view.auth.register.RegisterScreen
import com.maraloedev.golfmaster.view.eventos.EventosScreen
import com.maraloedev.golfmaster.view.inicio.HomeScreen
import com.maraloedev.golfmaster.view.perfil.PerfilScreen
import com.maraloedev.golfmaster.view.preferencias.PreferenciasScreen
import com.maraloedev.golfmaster.view.splash.SplashScreen
import com.maraloedev.golfmaster.view.torneos.TorneosScreen
import com.maraloedev.golfmaster.vm.SimpleAuthViewModel

@Composable
fun NavigationWrapper(navController: NavHostController) {
    val authVm: SimpleAuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") { SplashScreen(navController) }

        composable("login") {
            val err = authVm.errorMessage
            LoginScreen(
                onLogin = { email, pass ->
                    authVm.login(email, pass) { success ->
                        if (success) {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                },
                onRegisterClick = { navController.navigate("register") },
                onForgotPasswordClick = { /* TODO: recuperar contraseña */ },
                errorMessage = err
            )
        }

        composable("register") {
            RegisterScreen(navController
            )
        }

        composable("home") { HomeScreen(navController) }
        composable("perfil") { PerfilScreen(navController) }
        composable("preferencias") { PreferenciasScreen() }

        composable("eventos") {
            EventosScreen()
        }

        composable("torneosCrear") {
            TorneosScreen(
                onFinish = { _ ->
                    navController.popBackStack()
                }
            )
        }
    }
}
