package com.maraloedev.golfmaster.view.menuHamburguesa.home

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
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
import com.maraloedev.golfmaster.view.menuHamburguesa.contactos.ContactoScreen
import com.maraloedev.golfmaster.view.eventos.EventosScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.informacion.InformacionScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.perfil.PerfilScreen
import com.maraloedev.golfmaster.view.menuHamburguesa.preferencias.PreferenciasScreen
import com.maraloedev.golfmaster.view.reservas.ReservasScreen
import kotlinx.coroutines.launch

/**
 * Pantalla principal de la aplicación cuando el usuario ya ha iniciado sesión.
 *
 * - Contiene un Drawer lateral con opciones (Inicio, Información, Perfil, etc.).
 * - Tiene una barra superior con título dinámico.
 * - Barra inferior con navegación rápida (Reservas, Eventos, Amigos, Alertas).
 * - El contenido central cambia según la pestaña/elemento seleccionado.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    // Estado del Drawer lateral
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Pestaña / sección actual (usamos String, pero podría ser un sealed class)
    var current by remember { mutableStateOf("Inicio") }

    // ViewModel que carga los datos básicos del jugador
    val homeVm: HomeViewModel = viewModel()
    val jugador by homeVm.jugador.collectAsState()

    //  Estructura principal con Drawer lateral y Scaffold
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                jugadorNombre = jugador?.nombre ?: "Cargando...",
                jugadorEmail = jugador?.correo ?: "",
                selectedItem = current,
                onItemClick = { label ->
                    current = label
                    scope.launch { drawerState.close() }
                },
                onLogout = {
                    // Cierre de sesión y retorno a login
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    ) {
        Scaffold(
            //  Barra superior con título dinámico y acceso directo a Perfil
            topBar = {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
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
            //  Barra inferior con iconos de navegación rápida
            bottomBar = {
                BottomNavBar(
                    current = current,
                    onItemSelected = { current = it }
                )
            }
        ) { innerPadding ->
            //  Contenido central (sección activa)
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                when (current) {
                    "Inicio"       -> HomeLandingContent(nombreJugador = jugador?.nombre)
                    "Información"  -> InformacionScreen(navController)
                    "Reservas"     -> ReservasScreen()
                    "Eventos"      -> EventosScreen()
                    "Amigos"       -> AmigosScreen(navController)
                    "Alertas"      -> AlertasScreen()
                    "Mi Perfil"    -> PerfilScreen(navController)
                    "Preferencias" -> PreferenciasScreen()
                    "Contacto"     -> ContactoScreen()
                    else           -> HomeLandingContent(nombreJugador = jugador?.nombre)
                }
            }
        }
    }
}

/* ============================================================
 *  DrawerContent — Menú lateral con datos del jugador
 * ============================================================ */
@Composable
private fun DrawerContent(
    jugadorNombre: String,
    jugadorEmail: String,
    selectedItem: String,
    onItemClick: (String) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(270.dp)
            .background(Color(0xFF0B3D2E))
            .padding(16.dp)
    ) {
        //  Encabezado con avatar y datos del jugador
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1F4D3E)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = jugadorNombre,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = jugadorEmail,
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        //  Opciones del menú lateral
        val menuItems = listOf(
            "Inicio"       to Icons.Filled.Home,
            "Información"  to Icons.Filled.Info,
            "Contacto"     to Icons.Filled.Email,
            "Mi Perfil"    to Icons.Filled.Person,
            "Preferencias" to Icons.Filled.Settings
        )

        menuItems.forEach { (label, icon) ->
            val selected = selectedItem == label
            val bg by animateColorAsState(
                targetValue = if (selected) Color(0xFF1F4D3E) else Color.Transparent,
                label = ""
            )
            val fg by animateColorAsState(
                targetValue = if (selected) Color(0xFF00FF77) else Color.White,
                label = ""
            )

            Row(
                modifier = Modifier
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

        //  Botón de cierre de sesión
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

/* ============================================================
 *  Bottom Navigation Bar — Navegación inferior
 * ============================================================ */
@Composable
private fun BottomNavBar(
    current: String,
    onItemSelected: (String) -> Unit
) {
    NavigationBar(containerColor = Color(0xFF0B3D2E)) {
        val items = listOf(
            "Reservas" to Icons.Filled.EventAvailable,
            "Eventos"  to Icons.Filled.Flag,
            "Inicio"   to Icons.Filled.Home,
            "Amigos"   to Icons.Filled.Group,
            "Alertas"  to Icons.Filled.Notifications
        )

        items.forEach { (label, icon) ->
            val selected = current == label
            val tint by animateColorAsState(
                targetValue = if (selected) Color(0xFF00FF77) else Color.White,
                label = ""
            )

            NavigationBarItem(
                selected = selected,
                onClick = { onItemSelected(label) },
                icon = { Icon(icon, contentDescription = label, tint = tint) },
                label = {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        color = tint
                    )
                }
            )
        }
    }
}

/* ============================================================
 *  Pantalla de inicio — Mensaje de bienvenida
 * ============================================================ */
@Composable
private fun HomeLandingContent(
    nombreJugador: String?
) {
    // Si no hay nombre, usamos "Jugador" como fallback.
    val nombreActual by rememberUpdatedState(
        nombreJugador?.takeIf { it.isNotBlank() } ?: "Jugador"
    )

    val painter = safePainterResource(R.drawable.logo_app)

    Box(Modifier.fillMaxSize()) {

        // 🖼️ Fondo con imagen (logo_app), si existe el recurso
        painter?.let {
            Image(
                painter = it,
                contentDescription = "Fondo Golf",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        //  Gradiente oscuro en la parte inferior para mejorar legibilidad
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0xCC000000))
                    )
                )
        )

        // ️ Mensaje de bienvenida
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
                text = "Bienvenido, $nombreActual",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(10.dp))
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

/* ============================================================
 *  safePainterResource — Evita crash si el drawable no existe
 * ============================================================ */
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

/* ============================================================
 * ✨ GlowingLogo — Logo animado (mismo estilo que en Splash)
 *  Sería buena idea extraerlo a un archivo común si lo reutilizas.
 * ============================================================ */
@Composable
private fun GlowingLogo() {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    //  Animación de escala (efecto pulsación)
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = EaseInOutQuad),
            RepeatMode.Reverse
        ),
        label = ""
    )

    //  Animación de la opacidad del brillo
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = EaseInOutQuad),
            RepeatMode.Reverse
        ),
        label = ""
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
