package com.maraloedev.golfmaster.view.eventos

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.maraloedev.golfmaster.model.Torneos
import com.google.firebase.Timestamp

@Composable
fun EventosScreen(
    vm: EventosViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onTorneoClick: (Torneos) -> Unit = {},
    onCrearTorneo: () -> Unit = {},
    torneoRecienCreado: Torneos? = null
) {
    val torneos by vm.torneos.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    var selectedTab by remember { mutableStateOf("Próximos") }

    LaunchedEffect(Unit) { vm.cargarTorneos() }
    torneoRecienCreado?.let { vm.cargarTorneos() }

    val ahora = Timestamp.now()
    val proximos = torneos.filter { it.fechaInicio?.seconds ?: 0 > ahora.seconds }
    val pasados = torneos.filter { it.fechaInicio?.seconds ?: 0 <= ahora.seconds }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCrearTorneo,
                containerColor = Color(0xFF00FF77)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo torneo", tint = Color.Black)
            }
        },
        containerColor = Color(0xFF00281F)
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFF00281F))
        ) {
            SegmentedSelectorEventos(
                options = listOf("Próximos", "Pasados"),
                selectedOption = selectedTab,
                onOptionSelected = { selectedTab = it }
            )

            when {
                loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF00FF77))
                }

                error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: $error", color = Color.Red)
                }

                else -> {
                    val lista = if (selectedTab == "Próximos") proximos else pasados
                    if (lista.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "No hay torneos ${selectedTab.lowercase()}",
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            items(lista) { torneo ->
                                TorneoCard(torneo) { onTorneoClick(torneo) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TorneoCard(torneo: Torneos, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B12)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(torneo.nombre, color = Color.White, fontWeight = FontWeight.Bold)
            Text("📍 ${torneo.lugar}", color = Color.White.copy(alpha = 0.8f))
            Text("🏁 ${torneo.tipo}", color = Color.White.copy(alpha = 0.8f))
            Text(
                "📅 ${torneo.fechaInicio?.toDate()?.toString()?.substring(0, 10) ?: "Sin fecha"}",
                color = Color(0xFF6BF47F)
            )
        }
    }
}

@Composable
fun SegmentedSelectorEventos(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .background(Color(0xFF0D1B12), RoundedCornerShape(50))
            .padding(6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        options.forEach { option ->
            val selected = option == selectedOption
            val bg = if (selected) Color(0xFF00FF77) else Color.Transparent
            val fg = if (selected) Color.Black else Color.White

            OutlinedButton(
                onClick = { onOptionSelected(option) },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = bg,
                    contentColor = fg
                ),
                border = if (selected)
                    BorderStroke(1.dp, Color(0xFF00FF77))
                else
                    BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                modifier = Modifier.height(44.dp)
            ) {
                Text(option, fontWeight = FontWeight.Bold)
            }
        }
    }
}
