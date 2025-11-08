package com.maraloedev.golfmaster.view.amigos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maraloedev.golfmaster.model.Amigo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmigosScreen(
    navController: NavController,
    vm: AmigosViewModel = viewModel()
) {
    val amigos by vm.amigos.collectAsState()
    val loading by vm.loading.collectAsState()

    val scope = rememberCoroutineScope()
    var amigoAEliminar by remember { mutableStateOf<Amigo?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("amigosAgregar") },
                containerColor = Color(0xFF4CAF50)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir amigo", tint = Color.White)
            }
        },
        containerColor = Color(0xFF0C1A12)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF0C1A12))
        ) {
            Text(
                "Amigos",
                modifier = Modifier.padding(16.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )

            when {
                loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4CAF50))
                }

                amigos.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes amigos añadidos aún.", color = Color.Gray)
                }

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(amigos, key = { it.id }) { amigo ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                amigoAEliminar = amigo
                                false // No eliminar directamente, solo mostrar diálogo
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            enableDismissFromEndToStart = true,
                            backgroundContent = {
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(Color.Red)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Text("Eliminar", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            },
                            content = {
                                // 💡 Tarjeta de amigo oscura
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF1B372B), shape = MaterialTheme.shapes.medium)
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = amigo.nombre,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // 🧾 Diálogo de confirmación de eliminación
    amigoAEliminar?.let { amigo ->
        AlertDialog(
            onDismissRequest = { amigoAEliminar = null },
            title = { Text("Eliminar amigo", color = Color.White) },
            text = { Text("¿Seguro que quieres eliminar a ${amigo.nombre}?", color = Color.White) },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            vm.eliminarAmigo(amigo.id)
                            amigoAEliminar = null
                        }
                    }
                ) { Text("Eliminar", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { amigoAEliminar = null }) {
                    Text("Cancelar", color = Color.White)
                }
            },
            containerColor = Color(0xFF1B1B1B)
        )
    }
}
