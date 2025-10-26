package com.maraloedev.golfmaster.view.campos.terminos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
fun TerminosScreen(
    navController: NavController,
    vm: TerminosViewModel = viewModel()
) {
    val state = vm.state.collectAsState().value
    val fondo = Color(0xFF0A1A0E)
    val verde = Color(0xFF2BD67B)
    val textoSec = Color(0xFF9CA3AF)

    Scaffold(
        containerColor = fondo,
        topBar = {
            TopAppBar(
                title = { Text("Términos y Condiciones", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = fondo, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        when {
            state.cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = verde)
            }
            state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("❌ ${state.error}", color = Color.Red)
            }
            else -> Column(
                Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                state.terminos.forEach { item ->
                    Text(item.titulo, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    Text(item.descripcion, color = textoSec, fontSize = 15.sp)
                    Divider(color = textoSec.copy(alpha = 0.3f))
                }
            }
        }
    }
}
