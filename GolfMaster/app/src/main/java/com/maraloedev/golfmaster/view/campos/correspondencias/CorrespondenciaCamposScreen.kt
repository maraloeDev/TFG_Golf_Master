package com.maraloedev.golfmaster.view.campos.correspondencias

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CorrespondenciaCamposScreen(
    navController: NavController,
    vm: CorrespondenciaCamposViewModel = viewModel()
) {
    val state = vm.state.collectAsState().value
    val fondo = Color(0xFF0A1A0E)
    val card = Color(0xFF111F1A)
    val verde = Color(0xFF2BD67B)
    val textoSec = Color(0xFF9CA3AF)

    Scaffold(
        containerColor = fondo,
        topBar = {
            TopAppBar(
                title = { Text("Correspondencia de Campos", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = fondo, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        when {
            state.cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = verde)
            }
            state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("❌ Error: ${state.error}", color = Color.Red)
            }
            else -> Column(
                Modifier.padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                state.campos.forEach { campo ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = card),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(campo.nombre, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Map, null, tint = verde)
                                Spacer(Modifier.width(6.dp))
                                Text(campo.direccion, color = textoSec)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                                Icon(Icons.Default.Call, null, tint = verde)
                                Spacer(Modifier.width(6.dp))
                                Text(campo.telefono, color = textoSec)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                                Icon(Icons.Default.Email, null, tint = verde)
                                Spacer(Modifier.width(6.dp))
                                Text(campo.email, color = textoSec)
                            }
                        }
                    }
                }
            }
        }
    }
}
