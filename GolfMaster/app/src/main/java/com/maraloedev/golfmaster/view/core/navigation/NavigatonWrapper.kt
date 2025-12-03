package com.maraloedev.golfmaster.view.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.maraloedev.golfmaster.view.auth.login.LoginScreenContainer
import com.maraloedev.golfmaster.view.auth.register.RegisterScreen
import com.maraloedev.golfmaster.view.core.splash.SplashScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.home.HomeScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.contactos.ContactoScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.informacion.InformacionScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.informacion.detalles.DetalleCamposScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.informacion.detalles.DetalleReglasScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.informacion.detalles.DetalleReservasScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.informacion.detalles.DetalleTorneosScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.perfil.PerfilScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.preferencias.PreferenciasScreen
import com.maraloedev.golfmaster.view.reservas.ReservasScreen
import com.maraloedev.golfmaster.view.alertas.AlertasScreen
import com.maraloedev.golfmaster.view.amigos.AmigosScreen
import com.maraloedev.golfmaster.view.amigos.AgregarAmigoScreen

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        composable("splash") {
            SplashScreen(navController = navController)
        }
        // ============================================================
        //  AUTENTICACIÓN
        // ============================================================

        composable("login") {
            LoginScreenContainer(navController)
        }

        composable("register") {
            RegisterScreen(navController = navController)
        }

        // ============================================================
        //  HOME PRINCIPAL (Drawer con secciones)
        // ============================================================
        composable("home") {
            HomeScreen(navController)
        }

        // ============================================================
        //  INFORMACIÓN Y DETALLES
        // ============================================================
        composable("informacion") {
            InformacionScreen(navController = navController)
        }

        composable("detalle_reservas") {
            DetalleReservasScreen(navController)
        }

        composable("detalle_campos") {
            DetalleCamposScreen(navController)
        }

        composable("detalle_reglas") {
            DetalleReglasScreen(navController)
        }

        composable("detalle_torneos") {
            DetalleTorneosScreen(navController)
        }

        // ============================================================
        //  CONTACTO, PERFIL Y PREFERENCIAS
        // ============================================================
        composable("contacto") {
            ContactoScreen()
        }

        composable("perfil") {
            PerfilScreen(navController)
        }

        composable("preferencias") {
            PreferenciasScreen()
        }

        // ============================================================
        //  RESERVAS
        // ============================================================
        composable("reservas") {
            ReservasScreen()
        }

        // ============================================================
        //  ALERTAS (invitaciones y solicitudes)
        // ============================================================
        composable("alertas") {
            AlertasScreen()
        }

        // ============================================================
        //  AMIGOS
        // ============================================================
        composable("amigos") {
            AmigosScreen(navController = navController)
        }

        // 🔹 Agregar amigo (pantalla secundaria)
        composable("amigosAgregar") {
            AgregarAmigoScreen(onFinish = { navController.popBackStack() })
        }
    }
}
