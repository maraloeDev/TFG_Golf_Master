package com.maraloedev.golfmaster.view.eventos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.maraloedev.golfmaster.model.Torneos
import java.util.*

@Composable
fun EventosTabs(vm: EventosViewModel, onTorneoClick: (Torneos) -> Unit) {
    val torneos by vm.torneos.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    val ahora = Date()

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF00281F))
            .padding(top = 12.dp)
    ) {
        // Encabezado Tabs
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TabButton("Próximos", selectedTab == 0) { selectedTab = 0 }
            TabButton("Pasados", selectedTab == 1) { selectedTab = 1 }
        }

        Spacer(Modifier.height(12.dp))

        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF00FF77))
            }
            error != null -> Text(
                text = "Error: $error",
                color = Color.Red,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            else -> {
                val lista = if (selectedTab == 0)
                    torneos.filter { it.fechaInicio?.toDate()?.after(ahora) == true }
                else
                    torneos.filter { it.fechaFin?.toDate()?.before(ahora) == true }

                LazyColumn(
                    Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(lista) { torneo ->
                        EventoCard(torneo, onClick = { onTorneoClick(torneo) })
                    }
                }
            }
        }
    }
}

@Composable
private fun TabButton(text: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) Color(0xFF00FF77) else Color.Transparent
    val fg = if (selected) Color.Black else Color.White

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 22.dp, vertical = 10.dp)
    ) {
        Text(text, color = fg, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun EventoCard(t: Torneos, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B12)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(t.nombre, color = Color.White, fontWeight = FontWeight.Bold)
            Text(t.lugar, color = Color.Gray)
        }
    }
}
