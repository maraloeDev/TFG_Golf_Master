package com.maraloedev.golfmaster.view.inicio

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.maraloedev.golfmaster.view.contacto.ContactoScreen
import com.maraloedev.golfmaster.view.eventos.EventosScreen
import com.maraloedev.golfmaster.view.perfil.PerfilScreen
import com.maraloedev.golfmaster.view.preferencias.PreferenciasScreen
import com.maraloedev.golfmaster.view.reservas.ReservasScreen
import com.maraloedev.golfmaster.view.core.navigation.NavRoutes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var current by remember { mutableStateOf("Inicio") }

    val homeVm: HomeViewModel = viewModel()
    val jugador by homeVm.jugador.collectAsState()

    // 🔒 Comprobación inicial de sesión
    LaunchedEffect(Unit) {
        if (FirebaseAuth.getInstance().currentUser == null) {
            navController.navigate(NavRoutes.LOGIN) {
                popUpTo(NavRoutes.INICIO) { inclusive = true }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                jugadorNombre = jugador?.nombre_jugador?.takeIf { it.isNotBlank() } ?: "Cargando...",
                jugadorEmail = jugador?.correo_jugador?.takeIf { it.isNotBlank() } ?: "Sin correo",
                selectedItem = current,
                onItemClick = {
                    current = it
                    scope.launch { drawerState.close() }
                },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.INICIO) { inclusive = true }
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(current, color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menú", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            current = "Mi Perfil"
                        }) {
                            Icon(
                                Icons.Filled.AccountCircle,
                                contentDescription = "Perfil",
                                tint = Color(0xFFFFCBA4)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B3D2E))
                )
            },
            bottomBar = { BottomNavBar(current = current, onItemSelected = { current = it }) }
        ) { innerPadding ->
            Box(
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                when (current) {
                    "Inicio" -> HomeLandingContent(nombreJugador = jugador?.nombre_jugador)
                    "Reservas" -> runCatching { ReservasScreen() }.getOrElse { ErrorContent("Error al cargar Reservas") }
                    "Eventos" -> runCatching { EventosScreen() }.getOrElse { ErrorContent("Error al cargar Eventos") }
                    "Amigos" -> runCatching { AmigosScreen() }.getOrElse { ErrorContent("Error al cargar Amigos") }
                    "Alertas" -> runCatching { AlertasScreen() }.getOrElse { ErrorContent("Error al cargar Alertas") }
                    "Mi Perfil" -> runCatching { PerfilScreen(navController) }.getOrElse { ErrorContent("Error al cargar Perfil") }
                    "Preferencias" -> runCatching { PreferenciasScreen() }.getOrElse { ErrorContent("Error al cargar Preferencias") }
                    "Contacto" -> runCatching { ContactoScreen() }.getOrElse { ErrorContent("Error al cargar Contacto") }
                    else -> ErrorContent("Pantalla desconocida")
                }
            }
        }
    }
}

/* ---------------- Drawer ---------------- */

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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1F4D3E)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(jugadorNombre, color = Color.White, fontWeight = FontWeight.Bold)
                Text(jugadorEmail, color = Color.Gray, fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(24.dp))

        val menuItems = listOf(
            "Inicio" to Icons.Filled.Home,
            "Información" to Icons.Filled.Info,
            "Contacto" to Icons.Filled.Email,
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

        TextButton(onClick = onLogout, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Salir")
            Spacer(Modifier.width(8.dp))
            Text("Cerrar sesión")
        }
    }
}

/* ---------------- Bottom Navigation ---------------- */

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

/* ---------------- Pantalla de inicio ---------------- */

@Composable
private fun HomeLandingContent(nombreJugador: String?) {
    val nombreActual by rememberUpdatedState(nombreJugador?.takeIf { it.isNotBlank() } ?: "Jugador")
    val painter = safePainterResource(R.drawable.logo_app)

    Box(Modifier.fillMaxSize()) {
        painter?.let {
            Image(painter = it, contentDescription = "Fondo Golf", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        }

        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xCC000000)))))

        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GlowingLogo()
            Spacer(Modifier.height(20.dp))
            Text("Bienvenido, $nombreActual", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Text(
                "“Cada golpe es una nueva oportunidad para la grandeza.”",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

/* ---------------- Error Content ---------------- */

@Composable
private fun ErrorContent(msg: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(msg, color = Color.Red, textAlign = TextAlign.Center)
    }
}

/* ---------------- Función segura para recursos ---------------- */

@SuppressLint("LocalContextResourcesRead")
@Composable
private fun safePainterResource(@DrawableRes id: Int): Painter? {
    val context = LocalContext.current
    val exists = remember(id) {
        runCatching {
            context.resources.getResourceName(id)
            true
        }.getOrElse { false }
    }
    return if (exists) painterResource(id) else null
}

/* ---------------- Logo animado ---------------- */

@Composable
private fun GlowingLogo() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutQuad), RepeatMode.Reverse),
        label = ""
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 0.4f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutQuad), RepeatMode.Reverse),
        label = ""
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(130.dp).clip(CircleShape)
            .background(Color(0xFF00FF77).copy(alpha = 0.1f))
            .scale(scale)
    ) {
        Box(
            modifier = Modifier.size(110.dp).clip(CircleShape)
                .background(Color(0xFF00FF77).copy(alpha = alpha))
        )
        Icon(Icons.Filled.MyLocation, contentDescription = "Logo", tint = Color(0xFF00FF77), modifier = Modifier.size(60.dp))
    }
}
