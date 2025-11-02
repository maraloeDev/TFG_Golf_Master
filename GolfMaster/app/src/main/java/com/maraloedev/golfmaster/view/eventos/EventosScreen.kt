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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventosScreen(
    vm: EventosViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onTorneoClick: (Torneos) -> Unit = {},
    onCrearTorneo: () -> Unit = {},
    torneoRecienCreado: Torneos? = null
) {
    val openSheet = remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("Próximos") }

    val torneos by vm.torneos.collectAsState()
    val loading by vm.loading.collectAsState()

    // Cargar torneos al iniciar
    LaunchedEffect(Unit) {
        vm.cargarTorneos()
    }

    val ahora = Timestamp.now()
    val proximos = torneos.filter { it.fecha_inicial_torneo?.seconds ?: 0 > ahora.seconds }
    val pasados = torneos.filter { it.fecha_inicial_torneo?.seconds ?: 0 <= ahora.seconds }

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
            // --- Tabs (Próximos / Pasados) ---
            SegmentedSelectorEventos(
                options = listOf("Próximos", "Pasados"),
                selectedOption = selectedTab,
                onOptionSelected = { selectedTab = it }
            )

            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF00FF77))
                }
            } else {
                val listaMostrar = if (selectedTab == "Próximos") proximos else pasados

                if (listaMostrar.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No hay torneos ${selectedTab.lowercase()}",
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(listaMostrar) { torneo ->
                            TorneoCard(torneo = torneo, onClick = { onTorneoClick(torneo) })
                        }
                    }
                }
            }
        }
    }
}

/* ------------------------------ CARD TORNEO ------------------------------ */
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
            Text(torneo.nombre_torneo, color = Color.White, fontWeight = FontWeight.Bold)
            Text("📍 Lugar: ${torneo.lugar_torneo}", color = Color.White.copy(alpha = 0.8f))
            Text("🏁 Tipo: ${torneo.tipo_torneo}", color = Color.White.copy(alpha = 0.8f))
            Text(
                "📅 Fecha: ${
                    torneo.fecha_inicial_torneo?.toDate()?.toString()?.substring(0, 10)
                        ?: "Sin fecha"
                }",
                color = Color(0xFF6BF47F)
            )
        }
    }
}

/* ------------------------------ COMPONENTES AUXILIARES ------------------------------ */

@Composable
private fun SegmentOpcion(
    text: String,
    activo: Boolean,
    onClick: () -> Unit
) {
    val bg = if (activo) Color(0xFF00FF77) else Color.Transparent
    val fg = if (activo) Color.Black else Color.White

    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = bg,
            contentColor = fg
        ),
        border = if (activo)
            BorderStroke(1.dp, Color(0xFF00FF77))
        else
            BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
    ) {
        Text(text, fontWeight = FontWeight.SemiBold)
    }
}

/* --- Selector superior de Próximos / Pasados --- */
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
