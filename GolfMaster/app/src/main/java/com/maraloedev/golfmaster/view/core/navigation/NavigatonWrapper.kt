package com.maraloedev.golfmaster.view.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.maraloedev.golfmaster.view.alertas.AlertasScreen
import com.maraloedev.golfmaster.view.amigos.AgregarAmigoScreen
import com.maraloedev.golfmaster.view.amigos.AmigosScreen
import com.maraloedev.golfmaster.view.auth.login.LoginScreenContainer
import com.maraloedev.golfmaster.view.auth.register.RegisterScreen
import com.maraloedev.golfmaster.view.core.splash.SplashScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.contactos.ContactoScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.home.HomeScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.informacion.InformacionScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.informacion.detalles.DetalleCamposScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.informacion.detalles.DetalleReglasScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.informacion.detalles.DetalleReservasScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.informacion.detalles.DetalleTorneosScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.perfil.PerfilScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.preferencias.PreferenciasScreen
import com.maraloedev.golfmaster.view.reservas.ReservasScreen

/**
 * Aquí se definen todas las rutas (pantallas) y cómo se conectan entre sí:
 *  - Splash
 *  - Login / Register
 *  - Home (drawer)
 *  - Información y sus detalles
 *  - Contacto, Perfil, Preferencias
 *  - Reservas, Alertas
 *  - Amigos y Agregar amigos
 */
@Composable
fun MainNavHost(navController: NavHostController) {

    NavHost(
        navController = navController,
        // Pantalla inicial de la app (se lanza nada más abrirla)
        startDestination = "splash"
    ) {

        // ============================================================
        //  SPLASH / ARRANQUE
        // ============================================================
        composable("splash") {
            SplashScreen(navController = navController)
        }

        // ============================================================
        //  AUTENTICACIÓN
        // ============================================================

        // Pantalla de login (contenedor con lógica + UI)
        composable("login") {
            LoginScreenContainer(navController = navController)
        }

        // Pantalla de registro de nuevo usuario
        composable("register") {
            RegisterScreen(navController = navController)
        }

        // ============================================================
        // HOME PRINCIPAL (Drawer con secciones)
        // ============================================================
        composable("home") {
            HomeScreen(navController = navController)
        }

        // ============================================================
        //   INFORMACIÓN Y DETALLES
        // ============================================================
        composable("informacion") {
            InformacionScreen(navController = navController)
        }

        composable("detalle_reservas") {
            DetalleReservasScreen(navController = navController)
        }

        composable("detalle_campos") {
            DetalleCamposScreen(navController = navController)
        }

        composable("detalle_reglas") {
            DetalleReglasScreen(navController = navController)
        }

        composable("detalle_torneos") {
            DetalleTorneosScreen(navController = navController)
        }

        // ============================================================
        //   CONTACTO,  PERFIL Y ️ PREFERENCIAS
        // ============================================================

        // Formulario de contacto
        composable("contacto") {
            ContactoScreen()
        }

        // Perfil del usuario logueado
        composable("perfil") {
            PerfilScreen(navController = navController)
        }

        // Preferencias del usuario (idioma, intereses, etc.)
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
        //   AMIGOS
        // ============================================================

        // Lista de amigos + swipe to delete + FAB para agregar
        composable("amigos") {
            AmigosScreen(navController = navController)
        }

        // Pantalla secundaria para buscar y enviar solicitudes de amistad
        composable("amigosAgregar") {
            AgregarAmigoScreen(
                onFinish = { navController.popBackStack() }
            )
        }
    }
}
