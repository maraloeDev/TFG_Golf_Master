package com.maraloedev.golfmaster.view.amigos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maraloedev.golfmaster.model.Amigo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmigosScreen(
    navController: NavController, vm: AmigosViewModel = viewModel()
) {
    val colors = MaterialTheme.colorScheme
    val textMuted = colors.onBackground.copy(alpha = 0.7f)

    val amigos by vm.amigos.collectAsState()
    val loading by vm.loading.collectAsState()

    val scope = rememberCoroutineScope()
    var amigoAEliminar by remember { mutableStateOf<Amigo?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("amigosAgregar") },
                containerColor = colors.primary,
                contentColor = colors.onPrimary
            ) {
                Icon(
                    Icons.Default.Add, contentDescription = "Añadir amigo"
                )
            }
        }, containerColor = colors.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(colors.background)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {

            Text(
                text = "Tus amigos 👥",
                color = colors.onBackground,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Desliza a la izquierda para eliminar",
                color = textMuted,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            when {
                loading -> Box(
                    Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colors.primary)
                }

                //  Lista vacía
                amigos.isEmpty() -> Box(
                    Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.People,
                            contentDescription = null,
                            tint = textMuted,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Todavía no tienes amigos añadidos.", color = textMuted)
                        Text(
                            "Pulsa en el botón + para empezar 🟢",
                            color = textMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                //  Hay amigos
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(amigos, key = { it.id }) { amigo ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                // En lugar de borrar directamente, mostramos diálogo de confirmación
                                amigoAEliminar = amigo
                                false
                            })

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            enableDismissFromEndToStart = true,
                            backgroundContent = {
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(colors.error)
                                        .padding(horizontal = 18.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            "Eliminar",
                                            color = colors.onError,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = colors.onError
                                        )
                                    }
                                }
                            },
                            content = {
                                AmigoCardMini(amigo = amigo)
                            })
                    }
                }
            }
        }
    }

    //  Diálogo de confirmación de borrado
    amigoAEliminar?.let { amigo ->
        AlertDialog(
            onDismissRequest = { amigoAEliminar = null }, title = {
            Text(
                "Eliminar amigo", color = MaterialTheme.colorScheme.onSurface
            )
        }, text = {
            Text(
                "¿Seguro que quieres eliminar a ${amigo.nombre}?",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }, confirmButton = {
            TextButton(
                onClick = {
                    scope.launch {
                        vm.eliminarAmigo(amigo.id)
                        amigoAEliminar = null
                    }
                }) {
                Text("Eliminar", color = MaterialTheme.colorScheme.error)
            }
        }, dismissButton = {
            TextButton(onClick = { amigoAEliminar = null }) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
            }
        }, containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

/**
 * Tarjeta mini para cada amigo.
 */
@Composable
private fun AmigoCardMini(amigo: Amigo) {
    val colors = MaterialTheme.colorScheme
    val textMuted = colors.onSurface.copy(alpha = 0.7f)
    val inicial = amigo.nombre.trim().take(1).uppercase()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(colors.primary.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = inicial, color = colors.primary, fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "🏌️ ${amigo.nombre}",
                    color = colors.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Amigo en GolfMaster",
                    color = textMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
