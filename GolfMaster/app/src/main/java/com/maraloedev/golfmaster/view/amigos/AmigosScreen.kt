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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maraloedev.golfmaster.model.Amigo
import kotlinx.coroutines.launch

public val ScreenBg = Color(0xFF02140D)
public val CardBg   = Color(0xFF11261B)
public val Accent   = Color(0xFF00FF77)
public val Danger   = Color(0xFFE53935)
public val TextMuted = Color.White.copy(alpha = 0.7f)

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
                containerColor = Accent
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir amigo", tint = Color.Black)
            }
        },
        containerColor = ScreenBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(ScreenBg)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {

            Text(
                text = "Tus amigos 👥",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Desliza a la izquierda para eliminar",
                color = TextMuted,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            when {
                loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Accent)
                }

                amigos.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.People,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Todavía no tienes amigos añadidos.", color = TextMuted)
                        Text("Pulsa en el botón + para empezar 🟢", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                    }
                }

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(amigos, key = { it.id }) { amigo ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                amigoAEliminar = amigo
                                false
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
                                        .background(Danger)
                                        .padding(horizontal = 18.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Eliminar", color = Color.White, fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.width(6.dp))
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                }
                            },
                            content = {
                                AmigoCardMini(amigo = amigo)
                            }
                        )
                    }
                }
            }
        }
    }

    amigoAEliminar?.let { amigo ->
        AlertDialog(
            onDismissRequest = { amigoAEliminar = null },
            title = { Text("Eliminar amigo", color = Color.White) },
            text  = { Text("¿Seguro que quieres eliminar a ${amigo.nombre}?", color = TextMuted) },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            vm.eliminarAmigo(amigo.id)
                            amigoAEliminar = null
                        }
                    }
                ) { Text("Eliminar", color = Danger) }
            },
            dismissButton = {
                TextButton(onClick = { amigoAEliminar = null }) {
                    Text("Cancelar", color = Color.White)
                }
            },
            containerColor = CardBg
        )
    }
}

@Composable
private fun AmigoCardMini(amigo: Amigo) {
    val inicial = amigo.nombre.trim().take(1).uppercase()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),         // 🔹 MÁS PEQUEÑA
        colors = CardDefaults.cardColors(containerColor = CardBg),
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
                    .size(36.dp)       // 🔹 AVATAR MÁS PEQUEÑO
                    .clip(CircleShape)
                    .background(Accent.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = inicial,
                    color = Accent,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "🏌️ ${amigo.nombre}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Amigo en GolfMaster",
                    color = TextMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
