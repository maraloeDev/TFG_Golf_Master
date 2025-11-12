package com.maraloedev.golfmaster.view.amigos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maraloedev.golfmaster.model.Amigo
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/* 🎨 Estilo GolfMaster */
private val ScreenBg = Color(0xFF00281F)
private val CardBg = Color(0xFF0D1B12)
private val Accent = Color(0xFF00FF77)

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
    var amigoSeleccionado by remember { mutableStateOf<Amigo?>(null) }

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
                .padding(top = 12.dp) // 🔹 Espacio entre TopAppBar y contenido
        ) {
            when {
                loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Accent)
                }

                amigos.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes amigos añadidos aún.", color = Color.Gray)
                }

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { amigoSeleccionado = amigo },
                                    colors = CardDefaults.cardColors(containerColor = CardBg),
                                    elevation = CardDefaults.elevatedCardElevation(6.dp)
                                ) {
                                    Column(Modifier.padding(18.dp)) {
                                        Text(
                                            amigo.nombre,
                                            color = Accent,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(Modifier.height(6.dp))
                                        Text(
                                            "Licencia: ${amigo.numero_licencia.ifBlank { "Sin licencia" }}",
                                            color = Color.White.copy(alpha = 0.85f),
                                            fontSize = 14.sp
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        amigo.fechaAmistad?.let {
                                            val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                                .format(it.toDate())
                                            Text(
                                                "Amigos desde: $fecha",
                                                color = Color.Gray,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    /* 🗑️ Diálogo de confirmación */
    amigoAEliminar?.let { amigo ->
        AlertDialog(
            onDismissRequest = { amigoAEliminar = null },
            title = { Text("Eliminar amigo", color = Color.White) },
            text = { Text("¿Seguro que quieres eliminar a ${amigo.nombre}?", color = Color.White) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        vm.eliminarAmigo(amigo.id)
                        amigoAEliminar = null
                    }
                }) { Text("Eliminar", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { amigoAEliminar = null }) { Text("Cancelar", color = Color.White) }
            },
            containerColor = CardBg
        )
    }

    /* 👤 Diálogo resumen del perfil */
    amigoSeleccionado?.let { amigo ->
        AlertDialog(
            onDismissRequest = { amigoSeleccionado = null },
            containerColor = CardBg,
            title = {
                Text("Perfil de ${amigo.nombre}", color = Accent, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("Licencia: ${amigo.numero_licencia.ifBlank { "No disponible" }}", color = Color.White)
                    Spacer(Modifier.height(8.dp))
                    amigo.fechaAmistad?.let {
                        val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it.toDate())
                        Text("Amigos desde: $fecha", color = Color.Gray)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("GolfMaster - Conectando jugadores ⛳", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { amigoSeleccionado = null }) {
                    Text("Cerrar", color = Accent, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
