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
                    "Reservas de Equipamiento",
                    "Reserva de buggies, palos y carros",
                    Icons.Default.GolfCourse,
                    route = "detalle_reservas" // ✅ Ruta
                )
            ),
            navController = navController
        )

        Section(
            title = "Campos",
            cards = listOf(
                InfoCardData(
                    "Correspondencia de Campos",
                    "Información de contacto y ubicación",
                    Icons.Default.Map,
                    route = "detalle_campos"
                ),
                InfoCardData(
                    "Reglas Locales",
                    "Reglas locales de los campos de golf",
                    Icons.Default.Rule,
                    route = "detalle_reglas"
                )
            ),
            navController = navController
        )

        Section(
            title = "Torneos",
            cards = listOf(
                InfoCardData(
                    "Términos y Condiciones",
                    "Términos y condiciones de los torneos",
                    Icons.Default.EmojiEvents,
                    route = "detalle_torneos"
                )
            ),
            navController = navController
        )
    }
}

@Composable
private fun Section(
    title: String,
    cards: List<InfoCardData>,
    navController: NavController
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        cards.forEach { card ->
            InfoCard(data = card, navController = navController)
        }
    }
}

@Composable
private fun InfoCard(data: InfoCardData, navController: NavController) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C3C2C)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // ✅ NAVEGACIÓN SIMPLE: deja "informacion" debajo en el back stack
                navController.navigate(data.route)
            }
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(data.icon, contentDescription = null, tint = Color(0xFF00FF77), modifier = Modifier.size(30.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(data.title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(data.subtitle, color = Color.LightGray, fontSize = 13.sp)
            }
        }
    }
}



data class InfoCardData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String // ✅ añadimos la ruta
)
