package com.maraloedev.golfmaster.view.eventos

<<<<<<< HEAD
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
=======
import androidx.compose.foundation.layout.padding
>>>>>>> parent of ff2be93 (EventosScreen + Amigos Screen Success)
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
<<<<<<< HEAD
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.maraloedev.golfmaster.model.Torneos
import com.google.firebase.Timestamp
=======
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maraloedev.golfmaster.model.Torneos
import com.maraloedev.golfmaster.viewmodel.EventosViewModel
import java.text.SimpleDateFormat
import java.util.Locale
>>>>>>> parent of ff2be93 (EventosScreen + Amigos Screen Success)

@Composable
<<<<<<< HEAD
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
=======
fun EventosScreen(vm: EventosViewModel = viewModel()) {
    val torneos by vm.proximos.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(Unit) { vm.cargar() }

    when {
        error != null -> Text("Error: ${error ?: "Desconocido"}")
        torneos.isEmpty() -> Text("No hay torneos próximos")
        else -> LazyColumn {
            items(torneos) { torneo ->
                TorneoCard(torneo)
>>>>>>> parent of ff2be93 (EventosScreen + Amigos Screen Success)
            }
        }
    }
}

/* ------------------------------ CARD TORNEO ------------------------------ */
@Composable
<<<<<<< HEAD
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
=======
fun TorneoCard(t: Torneos) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    ElevatedCard(Modifier.padding(8.dp)) {
        ListItem(
            headlineContent = { Text(t.nombre_torneo) },
            supportingContent = {
                Text(
                    "Tipo: ${t.tipo_torneo}\n" +
                            "Premio: ${t.premio_torneo}\n" +
                            "Desde: ${
                                t.fecha_inicial_torneo?.toDate()?.let { dateFormat.format(it) }
                            } " +
                            "hasta: ${
                                t.fecha_final_torneo?.toDate()?.let { dateFormat.format(it) }
                            }"
                )
>>>>>>> parent of ff2be93 (EventosScreen + Amigos Screen Success)
            }
        )
    }
}
