package com.maraloedev.golfmaster.view.inicio

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.maraloedev.golfmaster.R
import com.maraloedev.golfmaster.view.alertas.AlertasScreen
import com.maraloedev.golfmaster.view.amigos.AmigosScreen
import com.maraloedev.golfmaster.view.eventos.EventosScreen
import com.maraloedev.golfmaster.view.perfil.PerfilScreen
import com.maraloedev.golfmaster.view.preferencias.PreferenciasScreen
import com.maraloedev.golfmaster.view.reservas.ReservasScreen
import com.maraloedev.golfmaster.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

/**
 * Pantalla principal con AppBar, Drawer lateral y BottomNavigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen( navController: NavController)
 {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var current by remember { mutableStateOf("Inicio") }

    val homeVm: HomeViewModel = viewModel()
    val jugador by homeVm.jugador.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                jugadorNombre = jugador?.nombre_jugador ?: "Cargando...",
                jugadorEmail = jugador?.correo_jugador ?: "",
                selectedItem = current,
                onItemClick = {
                    current = it
                    scope.launch { drawerState.close() }
                },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Box(
                            Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = current,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Filled.Menu,
                                contentDescription = "Menú",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { current = "Mi Perfil" }) {
                            Icon(
                                Icons.Filled.AccountCircle,
                                contentDescription = "Perfil",
                                tint = Color(0xFFFFCBA4)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF0B3D2E)
                    )
                )
            },
            bottomBar = {
                BottomNavBar(current = current, onItemSelected = { current = it })
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                when (current) {
                    "Inicio" -> HomeLandingContent()
                    "Reservas" -> ReservasScreen()
                    "Eventos" -> EventosScreen()
                    "Amigos" -> AmigosScreen()
                    "Alertas" -> AlertasScreen()
                    "Mi Perfil" -> PerfilScreen()
                    "Preferencias" -> PreferenciasScreen()
                }
            }
        }
    }
}

/**
 * Contenido del menú lateral (drawer).
 */
@Composable
private fun DrawerContent(
    jugadorNombre: String,
    jugadorEmail: String,
    selectedItem: String,
    onItemClick: (String) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        Modifier
            .fillMaxHeight()
            .width(270.dp)
            .background(Color(0xFF0B3D2E))
            .padding(16.dp)
    ) {
        // Cabecera del usuario
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1F4D3E))
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(jugadorNombre, color = Color.White, fontWeight = FontWeight.Bold)
                Text(jugadorEmail, color = Color.Gray, fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(24.dp))

        // Menú lateral
        val menuItems = listOf(
            "Inicio" to Icons.Filled.Home,
            "Información" to Icons.Filled.Info,
            "Contacto" to Icons.Filled.Lightbulb,
            "Mi Perfil" to Icons.Filled.Person,
            "Preferencias" to Icons.Filled.Settings
        )

        menuItems.forEach { (label, icon) ->
            val selected = selectedItem == label
            val bg by animateColorAsState(if (selected) Color(0xFF1F4D3E) else Color.Transparent)
            val fg by animateColorAsState(if (selected) Color(0xFF00FF77) else Color.White)
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(bg)
                    .clickable { onItemClick(label) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = label, tint = fg)
                Spacer(Modifier.width(12.dp))
                Text(label, color = fg)
            }
            Spacer(Modifier.height(6.dp))
        }

        Spacer(Modifier.weight(1f))

        // Botón de cerrar sesión
        TextButton(
            onClick = onLogout,
            colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Salir")
            Spacer(Modifier.width(8.dp))
            Text("Cerrar sesión")
        }
    }
}

/**
 * Barra de navegación inferior (BottomNav).
 */
@Composable
private fun BottomNavBar(current: String, onItemSelected: (String) -> Unit) {
    NavigationBar(containerColor = Color(0xFF0B3D2E)) {
        val items = listOf(
            "Reservas" to Icons.Filled.EventAvailable,
            "Eventos" to Icons.Filled.Flag,
            "Inicio" to Icons.Filled.Home,
            "Amigos" to Icons.Filled.Group,
            "Alertas" to Icons.Filled.Notifications
        )
        items.forEach { (label, icon) ->
            val selected = current == label
            val tint by animateColorAsState(if (selected) Color(0xFF00FF77) else Color.White)
            NavigationBarItem(
                selected = selected,
                onClick = { onItemSelected(label) },
                icon = { Icon(icon, contentDescription = label, tint = tint) },
                label = { Text(label, fontSize = 12.sp, color = tint) }
            )
        }
    }
}

/**
 * Contenido central de la pantalla "Inicio".
 */
@Composable
private fun HomeLandingContent() {
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.logo_app),
            contentDescription = "Fondo Golf",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0xCC000000))
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GlowingLogo()
            Spacer(Modifier.height(20.dp))
            Text(
                text = "GolfMaster",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "“Cada golpe es una nueva oportunidad para la grandeza.”",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

/**
 * Logo animado con efecto de brillo.
 */
@Composable
private fun GlowingLogo() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(130.dp)
            .clip(CircleShape)
            .background(Color(0xFF00FF77).copy(alpha = 0.1f))
            .scale(scale)
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color(0xFF00FF77).copy(alpha = alpha))
        )
        Icon(
            imageVector = Icons.Filled.MyLocation,
            contentDescription = "Logo",
            tint = Color(0xFF00FF77),
            modifier = Modifier.size(60.dp)
        )
    }
}
