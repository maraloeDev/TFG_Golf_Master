package com.maraloedev.golfmaster.view.reservas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maraloedev.golfmaster.model.Reservas
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservasScreen(vm: ReservasViewModel = viewModel()) {
    val reservas by vm.reservas.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    var tabSeleccionada by remember { mutableStateOf(0) }

    val verdePrincipal = Color(0xFF2BD67B)
    val fondoOscuro = Color(0xFF0E1A13)
    val fondoTabs = Color(0xFF122418)

    LaunchedEffect(Unit) {
        vm.cargar()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Reservas",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = fondoOscuro
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { vm.crearReserva() },
                containerColor = verdePrincipal,
                contentColor = Color.Black,
                shape = RoundedCornerShape(50.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva reserva")
            }
        },
        containerColor = fondoOscuro
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(fondoOscuro)
                .padding(horizontal = 16.dp)
        ) {
            // === Segmento de pestañas ===
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(fondoTabs, RoundedCornerShape(50.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Próximas", "Pasadas").forEachIndexed { index, texto ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (tabSeleccionada == index) verdePrincipal else Color.Transparent,
                                RoundedCornerShape(50.dp)
                            )
                            .clickable { tabSeleccionada = index }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = texto,
                            color = if (tabSeleccionada == index) Color.Black else Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // === Contenido de reservas ===
            when {
                loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = verdePrincipal)
                }

                error != null -> Text(
                    "Error: $error",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                reservas.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes reservas registradas.", color = Color.White)
                }

                else -> {
                    val ahora = Date()
                    val proximas = reservas.filter {
                        it.fecha_reserva?.toDate()?.after(ahora) == true
                    }.sortedBy { it.fecha_reserva?.toDate() }

                    val pasadas = reservas.filter {
                        it.fecha_reserva?.toDate()?.before(ahora) == true
                    }.sortedByDescending { it.fecha_reserva?.toDate() }

                    val listaMostrar = if (tabSeleccionada == 0) proximas else pasadas

                    LazyColumn {
                        items(listaMostrar) { reserva ->
                            ReservaCardVisual(reserva)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReservaCardVisual(reserva: Reservas) {
    val verde = Color(0xFF2BD67B)
    val fondoCard = Color(0xFF111F1A)
    val dateFormat = SimpleDateFormat("dd MMMM, yyyy", Locale("es", "ES"))
    val horaFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val fechaTexto = reserva.fecha_reserva?.toDate()?.let(dateFormat::format) ?: "Sin fecha"
    val horaTexto = reserva.hora_reserva?.toDate()?.let(horaFormat::format) ?: "Sin hora"
    val hoyos = reserva.recorrido_reserva.firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "Recorrido"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(fondoCard, RoundedCornerShape(12.dp))
            .clickable { /* abrir detalle si lo necesitas */ }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column {
            Text(
                text = hoyos,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "$fechaTexto - $horaTexto",
                color = verde,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
