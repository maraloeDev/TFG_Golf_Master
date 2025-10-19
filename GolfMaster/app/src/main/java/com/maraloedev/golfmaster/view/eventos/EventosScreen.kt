package com.maraloedev.golfmaster.view.eventos

import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maraloedev.golfmaster.model.Torneos
import com.maraloedev.golfmaster.viewmodel.EventosViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
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
            }
        }
    }
}

@Composable
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
            }
        )
    }
}
