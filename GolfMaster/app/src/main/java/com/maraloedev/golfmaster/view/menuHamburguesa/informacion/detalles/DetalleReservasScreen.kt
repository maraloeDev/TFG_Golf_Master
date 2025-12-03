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
 * Modelo informativo para el equipamiento reservable.
 */
data class Equipamiento(
    val nombre: String,
    val descripcion: String,
    val precio: String
)

/** Lista estática de equipamiento que se muestra en la pantalla. */
private val equipamientoDemo = listOf(
    Equipamiento("Buggy", "Vehículo eléctrico de 2 plazas", "25 € / ronda"),
    Equipamiento("Carro eléctrico", "Carro motorizado para bolsas de golf", "12 € / día"),
    Equipamiento("Carro manual", "Carro clásico sin motor", "5 € / día"),
    Equipamiento("Set de palos completo", "Palos de alta gama (Callaway, TaylorMade...)", "20 € / día"),
    Equipamiento("Bolsa de golf", "Bolsa ligera con soporte", "8 € / día")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleReservasScreen(navController: NavController) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reservas de Equipamiento", color = Color.White) },
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
            items(equipamientoDemo) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C3C2C)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = item.nombre,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = item.descripcion,
                            color = Color.LightGray,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = item.precio,
                            color = Color(0xFF00FF77),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
