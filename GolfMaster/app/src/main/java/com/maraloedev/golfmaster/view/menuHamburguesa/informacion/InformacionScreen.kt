package com.maraloedev.golfmaster.view.menuHamburguesa.informacion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GolfCourse
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * Pantalla de “Información” del menú lateral.
 *
 * Agrupa distintos bloques (Reservas, Campos, Torneos) con tarjetas
 * que navegan a pantallas de detalle (informativa, solo lectura).
 */
@Composable
fun InformacionScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF00281F))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Section(
            title = "Reservas",
            cards = listOf(
                InfoCardData(
                    title = "Reservas de Equipamiento",
                    subtitle = "Reserva de buggies, palos y carros",
                    icon = Icons.Default.GolfCourse,
                    route = "detalle_reservas"
                )
            ),
            navController = navController
        )

        Section(
            title = "Campos",
            cards = listOf(
                InfoCardData(
                    title = "Correspondencia de Campos",
                    subtitle = "Información de contacto y ubicación",
                    icon = Icons.Default.Map,
                    route = "detalle_campos"
                ),
                InfoCardData(
                    title = "Reglas Locales",
                    subtitle = "Reglas locales de los campos de golf",
                    icon = Icons.Default.Rule,
                    route = "detalle_reglas"
                )
            ),
            navController = navController
        )

        Section(
            title = "Torneos",
            cards = listOf(
                InfoCardData(
                    title = "Términos y Condiciones",
                    subtitle = "Términos y condiciones de los torneos",
                    icon = Icons.Default.EmojiEvents,
                    route = "detalle_torneos"
                )
            ),
            navController = navController
        )
    }
}

/**
 * Sección con título + lista de tarjetas de navegación.
 */
@Composable
private fun Section(
    title: String,
    cards: List<InfoCardData>,
    navController: NavController
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        cards.forEach { card ->
            InfoCard(data = card, navController = navController)
        }
    }
}

/**
 * Tarjeta reutilizable de “información + navegación”.
 *
 * Muestra icono, título y subtítulo, y navega a la ruta especificada.
 */
@Composable
private fun InfoCard(
    data: InfoCardData,
    navController: NavController
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C3C2C)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // Navegación simple: se apila sobre la pantalla de información.
                navController.navigate(data.route)
            }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = data.icon,
                contentDescription = null,
                tint = Color(0xFF00FF77),
                modifier = Modifier.size(30.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = data.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = data.subtitle,
                    color = Color.LightGray,
                    fontSize = 13.sp
                )
            }
        }
    }
}

/**
 * Modelo de datos para cada tarjeta de información.
 *
 * @param title      Título principal de la tarjeta.
 * @param subtitle   Descripción breve del contenido.
 * @param icon       Icono asociado.
 * @param route      Ruta de navegación dentro del NavHost.
 */
data class InfoCardData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String
)
