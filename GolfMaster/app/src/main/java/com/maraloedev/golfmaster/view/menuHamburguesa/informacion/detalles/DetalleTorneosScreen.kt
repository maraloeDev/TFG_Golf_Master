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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleTorneosScreen(navController: NavController) {
    val terminos = listOf(
        "1️⃣ Los jugadores deben tener licencia federativa en vigor.",
        "2️⃣ La inscripción se considera definitiva una vez realizado el pago.",
        "3️⃣ El jugador debe estar presente en el tee de salida 10 minutos antes del turno.",
        "4️⃣ En caso de empate, se aplicará el hándicap más bajo o la mejor vuelta.",
        "5️⃣ El Comité de Competición podrá modificar horarios por causas meteorológicas.",
        "6️⃣ La organización no se hace responsable de pérdidas o daños en material personal.",
        "7️⃣ La participación implica la aceptación de las normas del torneo y del club."
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Términos y Condiciones", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
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
            items(terminos.size) { index ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C3C2C)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = terminos[index],
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
