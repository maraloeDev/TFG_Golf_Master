package com.maraloedev.golfmaster.view.core.navigation

<<<<<<< HEAD
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
=======
import androidx.compose.runtime.Composable
>>>>>>> parent of ff2be93 (EventosScreen + Amigos Screen Success)
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.maraloedev.golfmaster.view.auth.login.LoginScreen
import com.maraloedev.golfmaster.view.auth.register.RegisterScreen
<<<<<<< HEAD
import com.maraloedev.golfmaster.view.eventos.EventoDetalleScreen
import com.maraloedev.golfmaster.view.eventos.EventoDetalleViewModel
import com.maraloedev.golfmaster.view.eventos.EventosScreen
=======
>>>>>>> parent of ff2be93 (EventosScreen + Amigos Screen Success)
import com.maraloedev.golfmaster.view.inicio.HomeScreen
import com.maraloedev.golfmaster.view.perfil.PerfilScreen
import com.maraloedev.golfmaster.view.preferencias.PreferenciasScreen
import com.maraloedev.golfmaster.view.splash.SplashScreen

@Composable
fun NavigationWrapper(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
<<<<<<< HEAD
        // ------------------ Auth ------------------
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }

        // ------------------ Home ------------------
        composable("home") { HomeScreen(navController) }

        // ----------- Perfil / Preferencias --------
        composable("perfil") { PerfilScreen(navController) }
        composable("preferencias") { PreferenciasScreen() }

        // ------------------ Eventos ----------------
        composable("eventos") { backStackEntry ->
            val torneoRecienCreado =
                backStackEntry.savedStateHandle.get<Torneos>("torneoRecienCreado")

            EventosScreen(
                torneoRecienCreado = torneoRecienCreado,
                onTorneoClick = { torneo ->
                    navController.navigate("eventoDetalle/${torneo.id}")
                },
                onCrearTorneo = {
                    navController.navigate("torneosCrear")
                }
            )
        }

        composable("torneosCrear") {
            TorneosScreen(
                onFinish = { nuevoTorneo ->
                    // Devolver el torneo creado a la pantalla anterior (eventos)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("torneoRecienCreado", nuevoTorneo)

                    navController.popBackStack()
                }
            )
        }

        composable("eventoDetalle/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id").orEmpty()
            val vm: EventoDetalleViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

            LaunchedEffect(id) {
                if (id.isNotBlank()) vm.cargarTorneo(id)
            }

            val torneo by vm.torneo.collectAsState()
            val loading by vm.loading.collectAsState()
            val error by vm.error.collectAsState()

            when {
                loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF6BF47F))
                }
                error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: $error", color = Color.Red)
                }
                torneo != null -> EventoDetalleScreen(torneo = torneo!!, navController = navController)
                else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontró el evento", color = Color.White)
                }
            }
        }

        // ------------------ Amigos -----------------
        composable("amigos") {
            AmigosScreen(navController = navController)
        }
        composable("amigosAgregar") {
            AgregarAmigoScreen(onFinish = { navController.popBackStack() })
        }
=======
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("perfil") { PerfilScreen(navController) }
        composable("preferencias") { PreferenciasScreen() }
>>>>>>> parent of ff2be93 (EventosScreen + Amigos Screen Success)
    }
}
