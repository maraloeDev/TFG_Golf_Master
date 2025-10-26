package com.maraloedev.golfmaster.view.campos.reglasLocales

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
fun ReglasLocalesScreen(
    navController: NavController,
    vm: ReglasLocalesViewModel = viewModel()
) {
    val state = vm.state.collectAsState().value
    val fondo = Color(0xFF0A1A0E)
    val card = Color(0xFF111F1A)
    val verde = Color(0xFF2BD67B)

    Scaffold(
        containerColor = fondo,
        topBar = {
            TopAppBar(
                title = { Text("Reglas Locales", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = fondo,
                    titleContentColor = Color.White
                )
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
            else -> LazyColumn(
                Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.reglas) { regla ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = card),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            regla.texto,
                            color = Color.White,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }
        }
    }
}
