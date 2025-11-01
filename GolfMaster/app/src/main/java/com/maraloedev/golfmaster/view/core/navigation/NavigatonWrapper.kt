package com.maraloedev.golfmaster.view.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.maraloedev.golfmaster.model.Torneos
import com.maraloedev.golfmaster.view.amigos.AgregarAmigoScreen
import com.maraloedev.golfmaster.view.amigos.AmigosScreen
import com.maraloedev.golfmaster.view.auth.login.LoginScreen
import com.maraloedev.golfmaster.view.auth.register.RegisterScreen
import com.maraloedev.golfmaster.view.eventos.EventoDetalleScreen
import com.maraloedev.golfmaster.view.eventos.EventosScreen
import com.maraloedev.golfmaster.view.inicio.HomeScreen
import com.maraloedev.golfmaster.view.perfil.PerfilScreen
import com.maraloedev.golfmaster.view.preferencias.PreferenciasScreen
import com.maraloedev.golfmaster.view.splash.SplashScreen
import com.maraloedev.golfmaster.view.torneos.TorneosScreen

/**
 * Controlador central de navegación de toda la app.
 *
 * Define todas las rutas de pantallas principales y secundarias:
 * - Autenticación
 * - Inicio (Home + Drawer + BottomBar)
 * - Eventos y Torneos
 * - Amigos
 * - Perfil / Preferencias
 */
@Composable
fun NavigationWrapper(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // ===============================
        // 🚀 PANTALLAS DE AUTENTICACIÓN
        // ===============================
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }

        // ===============================
        // 🏠 HOME (con Drawer + BottomBar)
        // ===============================
        composable("home") { HomeScreen(navController) }

        // ===============================
        // 👤 PERFIL Y PREFERENCIAS
        // ===============================
        composable("perfil") { PerfilScreen(navController) }
        composable("preferencias") { PreferenciasScreen() }

        // ===============================
        // 🏌️ EVENTOS / TORNEOS
        // ===============================
        composable("eventos") { backStackEntry ->
            val torneoRecienCreado =
                backStackEntry.savedStateHandle.get<Torneos>("torneoRecienCreado")

            EventosScreen(
                onTorneoClick = { torneo ->
                    // Navegar al detalle del torneo
                    navController.navigate("eventoDetalle/${torneo.id}")
                },
                onCrearTorneo = {
                    // Navegar al formulario de creación
                    navController.navigate("torneosCrear")
                },
                torneoRecienCreado = torneoRecienCreado
            )
        }

        composable("torneosCrear") {
            TorneosScreen(
                onFinish = { nuevoTorneo ->
                    // Guardar el torneo recién creado en la ruta anterior
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("torneoRecienCreado", nuevoTorneo)
                    navController.popBackStack()
                }
            )
        }

        // ===============================
        // 🧑‍🤝‍🧑 AMIGOS
        // ===============================
        composable("amigos") {
            AmigosScreen(navController = navController)
        }
        composable("amigosAgregar") {
            AgregarAmigoScreen(onFinish = { navController.popBackStack() })
        }

        composable("eventoDetalle/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val vm: com.maraloedev.golfmaster.view.eventos.EventoDetalleViewModel =
                androidx.lifecycle.viewmodel.compose.viewModel()

            LaunchedEffect(id) {
                if (id.isNotBlank()) vm.cargarTorneo(id)
            }

            val torneo by vm.torneo.collectAsState()
            val loading by vm.loading.collectAsState()
            val error by vm.error.collectAsState()

            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF6BF47F))
                }
            } else if (error != null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: $error", color = Color.Red)
                }
            } else if (torneo != null) {
                EventoDetalleScreen(torneo = torneo!!)
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontró el evento", color = Color.White)
                }
            }
        }

    }
}