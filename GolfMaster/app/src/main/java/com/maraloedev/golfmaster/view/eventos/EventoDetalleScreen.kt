package com.maraloedev.golfmaster.view.eventos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.maraloedev.golfmaster.model.Torneos
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventoDetalleScreen(
    torneo: Torneos,
    navController: NavController? = null
) {
    val df = remember { SimpleDateFormat("dd MMM yyyy", Locale("es", "ES")) }
    val inicio = torneo.fechaInicio?.toDate()?.let(df::format) ?: "Sin fecha"
    val fin = torneo.fechaFin?.toDate()?.let(df::format) ?: "Sin fecha"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(torneo.nombre, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D1B12))
            )
        },
        containerColor = Color(0xFF0D1B12)
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .fillMaxSize()
                .background(Color(0xFF0D1B12))
                .padding(16.dp)
        ) {
            Text("Tipo: ${torneo.tipo}", color = Color.White)
            Text("Lugar: ${torneo.lugar}", color = Color.White)
            Text("Formato: ${torneo.formato}", color = Color.White)
            Spacer(Modifier.height(8.dp))
            Text("Del $inicio al $fin", color = Color(0xFF6BF47F))
            Spacer(Modifier.height(16.dp))
            Text("Premio: ${torneo.premio.ifBlank { "No especificado" }}", color = Color.LightGray)
        }
    }
}
