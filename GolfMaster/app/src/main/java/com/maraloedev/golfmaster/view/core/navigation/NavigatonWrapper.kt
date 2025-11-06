package com.maraloedev.golfmaster.view.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.maraloedev.golfmaster.view.auth.login.LoginScreenContainer
import com.maraloedev.golfmaster.view.auth.register.RegisterScreen
import com.maraloedev.golfmaster.view.inicio.HomeScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.contactos.ContactoScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.informacion.InformacionScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.informacion.detalles.DetalleCamposScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.informacion.detalles.DetalleReglasScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.informacion.detalles.DetalleReservasScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.informacion.detalles.DetalleTorneosScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.perfil.PerfilScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.preferencias.PreferenciasScreen
import com.maraloedev.golfmaster.view.core.splash.SplashScreen

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash" // 👈 ahora empieza en Splash
    ) {
        // 🔹 Splash — comprueba si el usuario ya tiene sesión
        composable("splash") {
            SplashScreen(navController = navController)
        }

        // 🔹 Login
        composable("login") {
            LoginScreenContainer(navController)
        }

        // 🔹 Registro
        composable("register") {
            RegisterScreen(navController = navController)
        }

        // 🔹 Home principal (con Drawer + Información por defecto)
        composable("home") {
            HomeScreen(navController)
        }

        // 🔹 Información
        composable("informacion") {
            InformacionScreen(navController = navController)
        }

        // 🔹 Subpantallas de información
        composable("detalle_reservas") { DetalleReservasScreen(navController) }
        composable("detalle_campos") { DetalleCamposScreen(navController) }
        composable("detalle_reglas") { DetalleReglasScreen(navController) }
        composable("detalle_torneos") { DetalleTorneosScreen(navController) }

        // 🔹 Contacto
        composable("contacto") {
            ContactoScreen()
        }

        // 🔹 Perfil
        composable("perfil") {
            PerfilScreen(navController)
        }

        // 🔹 Preferencias
        composable("preferencias") {
            PreferenciasScreen()
        }
    }
}
