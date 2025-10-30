// ui/amigos.kt
package com.maraloedev.golfmaster.view.amigos

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maraloedev.golfmaster.model.Jugadores
import com.maraloedev.golfmaster.viewmodel.AmigosViewModel
@Preview
@Composable
fun AmigosScreen(vm: AmigosViewModel = viewModel()) {
    var q by remember { mutableStateOf("") }
    Column {
        OutlinedTextField(q, { q = it; vm.buscarJugadores(query = it) }, label = { Text("Buscar por nombre o apellido") },
            modifier = Modifier.padding(12.dp))
        val res by vm.resultados.collectAsState()
        LazyColumn {
            items(res) { j: Jugadores ->
                ListItem(headlineContent = { Text("${j.nombre_jugador} ${j.apellido_jugador}") }, supportingContent = { Text(j.correo_jugador) })
                Divider()
            }
        }
    }
}
