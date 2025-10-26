@file:OptIn(ExperimentalMaterial3Api::class)

package com.maraloedev.golfmaster.view.informacion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.GolfCourse
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun InformacionScreen(
    navController: NavController,
    vm: InformacionViewModel = viewModel()
) {
    // Colores centralizados (puedes moverlos a tu Theme)
    val fondo = Color(0xFF0A1A0E)
    val cardColor = Color(0xFF111F1A)
    val iconBg = Color(0xFF163021)
    val iconColor = Color(0xFF2BD67B)
    val textoSecundario = Color(0xFF9CA3AF)

    // State
    val state = vm.state.value

    Scaffold(
        containerColor = fondo,
        topBar = {
            TopAppBar(
                title = { Text("Información", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = fondo,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(state.sections) { sec ->
                SectionBlock(
                    title = sec.header,
                    entries = sec.entries,
                    cardColor = cardColor,
                    iconBg = iconBg,
                    iconColor = iconColor,
                    textoSecundario = textoSecundario,
                    onClick = { entry ->
                        entry.route?.let { navController.navigate(it) }
                    }
                )
            }
        }
    }
}

/** Bloque de sección (nombre distinto para no chocar con LazyColumn.items) */
@Composable
private fun SectionBlock(
    title: String,
    entries: List<InfoEntry>,
    cardColor: Color,
    iconBg: Color,
    iconColor: Color,
    textoSecundario: Color,
    onClick: (InfoEntry) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 22.sp)
        entries.forEach { entry ->
            InfoCard(
                entry = entry,
                cardColor = cardColor,
                iconBg = iconBg,
                iconColor = iconColor,
                textoSecundario = textoSecundario,
                onClick = { onClick(entry) }
            )
        }
    }
}

/** Tarjeta reutilizable */
@Composable
private fun InfoCard(
    entry: InfoEntry,
    cardColor: Color,
    iconBg: Color,
    iconColor: Color,
    textoSecundario: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBg, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = entry.iconName.toIcon(),
                    contentDescription = null,
                    tint = iconColor
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(entry.title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                Text(entry.subtitle, color = textoSecundario, fontSize = 15.sp)
            }
        }
    }
}

/** Mapear cadena a icono para no acoplar el VM a Iconos concretos */
private fun String.toIcon(): ImageVector = when (this.lowercase()) {
    "golf" -> Icons.Filled.GolfCourse
    "map" -> Icons.Filled.Map
    "flag" -> Icons.Filled.Flag
    "trophy" -> Icons.Filled.EmojiEvents
    else -> Icons.Filled.GolfCourse
}
