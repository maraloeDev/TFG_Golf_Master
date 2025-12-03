package com.maraloedev.golfmaster.view.menuHamburguesa.informacion.detalles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * Modelo simple para mostrar información básica de un campo de golf.
 */
data class CampoGolf(
    val nombre: String,
    val ubicacion: String,
    val telefono: String
)

/** Lista estática de campos para la pantalla (contenido informativo). */
private val camposDemo = listOf(
    CampoGolf("Real Club Valderrama", "Sotogrande, Cádiz", "956 791 200"),
    CampoGolf("PGA Catalunya Golf", "Caldes de Malavella, Girona", "972 472 577"),
    CampoGolf("Real Club de Golf El Prat", "Terrassa, Barcelona", "937 281 000"),
    CampoGolf("La Reserva Club", "Sotogrande, Cádiz", "956 785 252"),
    CampoGolf("Finca Cortesín Golf Club", "Casares, Málaga", "952 937 883"),
    CampoGolf("Real Club de Golf de Sevilla", "Alcalá de Guadaíra, Sevilla", "954 124 301"),
    CampoGolf("Club de Golf La Moraleja", "Alcobendas, Madrid", "916 505 400"),
    CampoGolf("Real Club de Golf de Las Palmas", "Bandama, Gran Canaria", "928 350 104"),
    CampoGolf("Club de Golf Son Gual", "Palma de Mallorca, Baleares", "971 785 888"),
    CampoGolf(
        "Real Sociedad Hípica Española Club de Campo",
        "San Sebastián de los Reyes, Madrid",
        "916 582 100"
    ),
    CampoGolf("Real Club de Golf de La Herrería", "San Lorenzo de El Escorial, Madrid", "918 905 038"),
    CampoGolf("Golf Santander", "Boadilla del Monte, Madrid", "916 348 000"),
    CampoGolf("Real Golf de Pedreña", "Pedreña, Cantabria", "942 500 001"),
    CampoGolf("Club de Golf Retamares", "Alalpardo, Madrid", "918 414 500"),
    CampoGolf("Golf Son Muntaner", "Palma de Mallorca, Baleares", "971 783 000"),
    CampoGolf("Club de Golf Aloha", "Marbella, Málaga", "952 907 085"),
    CampoGolf("Real Club de Golf Campoamor", "Orihuela Costa, Alicante", "965 320 410"),
    CampoGolf("Club de Golf La Manga", "Cartagena, Murcia", "968 175 000"),
    CampoGolf("Golf Las Américas", "Playa de las Américas, Tenerife", "922 752 005"),
    CampoGolf("Real Club de Golf de San Sebastián", "Fontarabie, Gipuzkoa", "943 630 061")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCamposScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Correspondencia de Campos", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF003122))
            )
        },
        containerColor = Color(0xFF00281F)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(camposDemo) { campo ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C3C2C)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = campo.nombre,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = campo.ubicacion,
                            color = Color.LightGray,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Tel: ${campo.telefono}",
                            color = Color(0xFF00FF77),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
