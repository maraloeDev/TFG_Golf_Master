package com.maraloedev.golfmaster.view.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.maraloedev.golfmaster.view.campos.correspondencias.CorrespondenciaCamposScreen
import com.maraloedev.golfmaster.view.campos.reglasLocales.ReglasLocalesScreen
import com.maraloedev.golfmaster.view.campos.terminos.TerminosScreen
import com.maraloedev.golfmaster.view.core.navigation.NavRoutes
import com.maraloedev.golfmaster.view.core.navigation.bottomNavItems
import com.maraloedev.golfmaster.view.eventos.EventosScreen
import com.maraloedev.golfmaster.view.informacion.InformacionScreen
import com.maraloedev.golfmaster.view.perfil.PerfilScreen
import com.maraloedev.golfmaster.view.reservas.ReservasScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(rootNavController: NavController) {
    val navController = rememberNavController()
    val fondo = Color(0xFF0A1A0E)
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val menuExpanded = remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("GolfMaster", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { menuExpanded.value = true }) {
                        Icon(Icons.Default.Menu, contentDescription = "Abrir menú", tint = Color(0xFF00FF77))
                    }
                    DropdownMenu(
                        expanded = menuExpanded.value,
                        onDismissRequest = { menuExpanded.value = false },
                        containerColor = Color(0xFF0B3D2E)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Información", color = Color.White) },
                            leadingIcon = { Icon(Icons.Default.Info, null, tint = Color(0xFF00FF77)) },
                            onClick = {
                                menuExpanded.value = false
                                navController.navigate(NavRoutes.INFORMACION)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Correspondencia de Campos", color = Color.White) },
                            leadingIcon = { Icon(Icons.Default.Public, null, tint = Color(0xFF00FF77)) },
                            onClick = {
                                menuExpanded.value = false
                                navController.navigate(NavRoutes.CORRESPONDENCIA_CAMPOS)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Reglas Locales", color = Color.White) },
                            leadingIcon = { Icon(Icons.Default.Gavel, null, tint = Color(0xFF00FF77)) },
                            onClick = {
                                menuExpanded.value = false
                                navController.navigate(NavRoutes.REGLAS_LOCALES)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Términos y Condiciones", color = Color.White) },
                            leadingIcon = { Icon(Icons.Default.Description, null, tint = Color(0xFF00FF77)) },
                            onClick = {
                                menuExpanded.value = false
                                navController.navigate(NavRoutes.TERMINOS_CONDICIONES)
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B3D2E))
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF0B3D2E)) {
                bottomNavItems
                    .filter { it.route != NavRoutes.INFORMACION }
                    .forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    item.icon,
                                    contentDescription = item.label,
                                    tint = if (selected) Color(0xFF00FF77) else Color.White
                                )
                            },
                            label = {
                                Text(
                                    item.label,
                                    color = if (selected) Color(0xFF00FF77) else Color.White
                                )
                            }
                        )
                    }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.INICIO_CONTENT,
            modifier = Modifier
                .background(fondo)
                .padding(innerPadding)
        ) {
            // Tabs inferiores
            composable(NavRoutes.INICIO_CONTENT) { InicioContent() }
            composable(NavRoutes.RESERVAS) { ReservasScreen() }
            composable(NavRoutes.EVENTOS) { EventosScreen() }
            composable(NavRoutes.PERFIL) { PerfilScreen(rootNavController) }

            // Menú hamburguesa
            composable(NavRoutes.INFORMACION)            { InformacionScreen(navController) }
            composable(NavRoutes.CORRESPONDENCIA_CAMPOS) { CorrespondenciaCamposScreen(navController) }
            composable(NavRoutes.REGLAS_LOCALES)         { ReglasLocalesScreen(navController) }
            composable(NavRoutes.TERMINOS_CONDICIONES)   { TerminosScreen(navController) }
        }
    }
}
