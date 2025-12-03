package com.maraloedev.golfmaster.view.menuHamburguesa.informacion.detalles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

/** Lista estática de reglas locales para mostrar en la pantalla. */
private val reglasLocales = listOf(
    "1️⃣ Cada jugador puede portar un máximo de 14 palos.",
    "2️⃣ Se debe jugar la bola como reposa en el terreno.",
    "3️⃣ Está prohibido mejorar las condiciones del golpe alterando el campo.",
    "4️⃣ En caso de bola perdida, se debe repetir desde el punto anterior con penalización de un golpe.",
    "5️⃣ Los bunkers deben ser alisados después del golpe.",
    "6️⃣ El jugador debe reparar los piques y las marcas en el green.",
    "7️⃣ No se permite el uso de teléfonos móviles o dispositivos de audio durante la ronda.",
    "8️⃣ Los jugadores deben mantener un ritmo adecuado de juego (máximo 4h por 18 hoyos)."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleReglasScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reglas Locales", color = Color.White) },
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(reglasLocales.size) { index ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C3C2C)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = reglasLocales[index],
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        }
    }
}
