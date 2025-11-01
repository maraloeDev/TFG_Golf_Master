package com.maraloedev.golfmaster.view.eventos

import androidx.compose.foundation.background
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
import com.maraloedev.golfmaster.model.Torneos
import com.maraloedev.golfmaster.viewmodel.EventosViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventosScreen(
    vm: EventosViewModel = viewModel(),
    onTorneoClick: (Torneos) -> Unit,
    onCrearTorneo: (() -> Unit)? = null,
    torneoRecienCreado: Torneos? = null
) {
    val torneos by vm.proximos.collectAsState()
    val error by vm.error.collectAsState()
    val loading by vm.loading.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { vm.cargar() }

    // SnackBar tras crear evento
    LaunchedEffect(torneoRecienCreado) {
        torneoRecienCreado?.let {
            scope.launch {
                val res = snackbarHostState.showSnackbar(
                    message = "✅ Evento creado correctamente",
                    actionLabel = "Ver evento"
                )
                if (res == SnackbarResult.ActionPerformed) onTorneoClick(it)
            }
            vm.cargar()
        }
    }

    Scaffold(
        containerColor = Color(0xFF0D1B12),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onCrearTorneo?.invoke() },
                containerColor = Color(0xFF00C853)
            ) { Icon(Icons.Default.Add, contentDescription = null, tint = Color.White) }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF0D1B12),
                contentColor = Color(0xFF6BF47F),
                indicator = {}
            ) {
                listOf("Próximas", "Pasadas").forEachIndexed { index, titulo ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                titulo,
                                color = if (selectedTab == index) Color(0xFF6BF47F) else Color.Gray
                            )
                        }
                    )
                }
            }

            // Contenido
            when {
                loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF6BF47F))
                }

                error != null -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("Error: $error", color = Color.Red)
                }

                torneos.isEmpty() -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("No hay eventos disponibles", color = Color.White)
                }

                else -> {
                    val ahora = Date()
                    val lista = if (selectedTab == 0)
                        torneos.filter { it.fecha_final_torneo?.toDate()?.after(ahora) == true }
                    else
                        torneos.filter { it.fecha_final_torneo?.toDate()?.before(ahora) == true }

                    LazyColumn(Modifier.background(Color(0xFF0D1B12))) {
                        items(lista) { torneo ->
                            TorneoCard(torneo) { onTorneoClick(torneo) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TorneoCard(t: Torneos, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF16361E))
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(t.nombre_torneo, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                if (t.lugar_torneo.isNotBlank())
                    Text(t.lugar_torneo, color = Color.LightGray, fontSize = 14.sp)
                Text(
                    text = t.fecha_inicial_torneo?.toDate()?.let { dateFormat.format(it) } ?: "",
                    color = Color(0xFF6BF47F),
                    fontSize = 13.sp
                )
            }
        }
    }
}
