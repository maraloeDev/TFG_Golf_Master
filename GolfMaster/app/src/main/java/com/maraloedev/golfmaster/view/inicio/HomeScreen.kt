package com.maraloedev.golfmaster.view.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maraloedev.golfmaster.view.core.navigation.NavRoutes
import com.maraloedev.golfmaster.view.home.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, vm: HomeViewModel = viewModel()) {
    val ui by vm.ui.collectAsState()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbar = remember { SnackbarHostState() }

    val items = listOf(
        DrawerItem("Perfil", Icons.Default.Person, NavRoutes.PERFIL),
        DrawerItem("Reservas", Icons.Default.Event, NavRoutes.RESERVAS),
        DrawerItem("Amigos", Icons.Default.Group, NavRoutes.AMIGOS),
        DrawerItem("Alertas", Icons.Default.Notifications, NavRoutes.ALERTAS),
        DrawerItem("Preferencias", Icons.Default.Settings, NavRoutes.PREFERENCIAS),
        DrawerItem("Información", Icons.Default.Info, NavRoutes.INFORMACION)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF173E34)
            ) {
                Spacer(Modifier.height(16.dp))
                Text(
                    "GolfMaster",
                    color = Color(0xFF00FF77),
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    ui.usuarioNombre.ifBlank { "Usuario" },
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Text(ui.correo, color = Color.Gray, fontSize = MaterialTheme.typography.bodySmall.fontSize, modifier = Modifier.padding(start = 16.dp))
                Divider(Modifier.padding(vertical = 12.dp))

                items.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.label, color = Color.White) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(item.route)
                        },
                        icon = { Icon(item.icon, contentDescription = item.label, tint = Color(0xFF00FF77)) },
                        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                    )
                }

                Divider(Modifier.padding(vertical = 12.dp))
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión", color = Color.Red) },
                    selected = false,
                    onClick = {
                        vm.logout {
                            navController.navigate(NavRoutes.LOGIN) {
                                popUpTo(NavRoutes.INICIO) { inclusive = true }
                            }
                        }
                    },
                    icon = { Icon(Icons.Default.Logout, contentDescription = null, tint = Color.Red) },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )
            }
        }
    ) {
        Scaffold(
            containerColor = Color(0xFF0B3D2E),
            snackbarHost = { SnackbarHost(snackbar) },
            topBar = {
                TopAppBar(
                    title = { Text("Inicio", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = null, tint = Color(0xFF00FF77))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B3D2E))
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { scope.launch { snackbar.showSnackbar("Bienvenido a GolfMaster ⛳") } },
                    containerColor = Color(0xFF00FF77)
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFF0B3D2E))
                }
            }
        ) { pv ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(pv)
                    .background(Color(0xFF0B3D2E)),
                contentAlignment = Alignment.Center
            ) {
                if (ui.loading) {
                    CircularProgressIndicator(color = Color(0xFF00FF77))
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Bienvenido, ${ui.usuarioNombre}", color = Color.White, fontSize = MaterialTheme.typography.headlineSmall.fontSize)
                        Spacer(Modifier.height(8.dp))
                        Text("Selecciona una sección desde el menú lateral", color = Color.Gray)
                    }
                }
            }
        }
    }
}

data class DrawerItem(val label: String, val icon: ImageVector, val route: String)
