package com.maraloedev.golfmaster.view.amigos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maraloedev.golfmaster.model.Jugadores
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmigosScreen(
    navController: NavController,
    vm: AmigosViewModel = viewModel()
) {
    var query by remember { mutableStateOf("") }
    val resultados by vm.resultados.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val mensaje by vm.mensaje.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(mensaje) {
        mensaje?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("amigosAgregar") },
                containerColor = Color(0xFF00C853)
            ) { Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White) }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFF0D1B12)
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it; vm.buscarJugadores(it) },
                label = { Text("Buscar por nombre o nº licencia") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF6BF47F),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFF6BF47F),
                    unfocusedLabelColor = Color.Gray
                )
            )

            when {
                loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF6BF47F))
                }

                error != null -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("Error: $error", color = Color.Red)
                }

                resultados.isEmpty() && query.isNotBlank() -> Box(
                    Modifier.fillMaxSize(),
                    Alignment.Center
                ) {
                    Text("No se encontraron jugadores", color = Color.White)
                }

                else -> LazyColumn(
                    Modifier.fillMaxSize()
                ) {
                    items(resultados) { j: Jugadores ->
                        ListItem(
                            headlineContent = { Text(j.nombre_jugador, color = Color.White) },
                            supportingContent = {
                                Text("Licencia: ${j.numero_licencia_jugador}", color = Color.Gray)
                            },
                            modifier = Modifier.clickable {
                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("jugadorDetalle", j)
                                navController.navigate("amigoDetalle")
                            }
                        )
                        Divider(color = Color(0x22FFFFFF))
                    }

                }
            }
        }
    }
}
