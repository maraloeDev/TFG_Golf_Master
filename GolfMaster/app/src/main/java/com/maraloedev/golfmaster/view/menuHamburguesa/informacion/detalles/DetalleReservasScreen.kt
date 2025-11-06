package com.maraloedev.golfmaster.view.menuHamburguesa.informacion.detalles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class Equipamiento(val nombre: String, val descripcion: String, val precio: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleReservasScreen(navController: NavController) {
    val listaEquipamiento = listOf(
        Equipamiento("Buggy", "Vehículo eléctrico de 2 plazas", "25 € / ronda"),
        Equipamiento("Carro eléctrico", "Carro motorizado para bolsas de golf", "12 € / día"),
        Equipamiento("Carro manual", "Carro clásico sin motor", "5 € / día"),
        Equipamiento("Set de palos completo", "Palos de alta gama (Callaway, TaylorMade...)", "20 € / día"),
        Equipamiento("Bolsa de golf", "Bolsa ligera con soporte", "8 € / día")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reservas de Equipamiento", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("informacion") }) {
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(listaEquipamiento) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C3C2C)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(item.nombre, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(item.descripcion, color = Color.LightGray, fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(item.precio, color = Color(0xFF00FF77), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
