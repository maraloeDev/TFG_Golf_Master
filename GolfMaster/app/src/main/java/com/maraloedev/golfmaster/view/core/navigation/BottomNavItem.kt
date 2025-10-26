package com.maraloedev.golfmaster.view.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(NavRoutes.RESERVAS, "Reservas", Icons.Default.CalendarMonth),
    BottomNavItem(NavRoutes.EVENTOS, "Eventos", Icons.Default.EmojiEvents),
    BottomNavItem(NavRoutes.INICIO, "Inicio", Icons.Default.Home),
    BottomNavItem(NavRoutes.PERFIL, "Perfil", Icons.Default.Person),
    BottomNavItem(NavRoutes.INFORMACION, "Info", Icons.Default.Info)
)
