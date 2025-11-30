@file:OptIn(ExperimentalMaterial3Api::class)

package com.maraloedev.golfmaster.view.eventos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.maraloedev.golfmaster.model.Evento
import kotlinx.coroutines.launch

/* ============================================================
   🎨 COLORES
   ============================================================ */
private val ScreenBg = Color(0xFF00281F)
private val CardBg = Color(0xFF0D1B12)
private val Accent = Color(0xFF00FF77)
private val DeleteBg = Color(0xFF8B0000)

/* ============================================================
   🟩 PANTALLA DE EVENTOS
   ============================================================ */
@Composable
fun EventosScreen(
    vm: EventosViewModel = viewModel()
) {
    val eventos by vm.eventos.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid

    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        vm.cargarEventos()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHost) },
        containerColor = ScreenBg,
        topBar = {
            TopAppBar(
                title = { Text("Mis eventos", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ScreenBg
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Accent
                )
            } else {
                if (eventos.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No tienes eventos creados",
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = eventos,
                            key = { it.id ?: it.nombre }
                        ) { evento ->

                            // (Por cómo está el repo, aquí solo llegan eventos del usuario)
                            val esCreador = evento.creadorId == currentUid

                            var mostrarDialogo by remember { mutableStateOf(false) }

                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { value ->
                                    if (!esCreador) return@rememberSwipeToDismissBoxState false

                                    if (value == SwipeToDismissBoxValue.StartToEnd ||
                                        value == SwipeToDismissBoxValue.EndToStart
                                    ) {
                                        // Muestra diálogo y no completes aún el swipe
                                        mostrarDialogo = true
                                        false
                                    } else {
                                        false
                                    }
                                }
                            )

                            if (mostrarDialogo) {
                                AlertDialog(
                                    onDismissRequest = { mostrarDialogo = false },
                                    title = { Text("Eliminar evento") },
                                    text = { Text("¿Seguro que quieres eliminar este evento?") },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                scope.launch {
                                                    evento.id?.let { vm.eliminarEvento(it) }
                                                    snackbarHost.showSnackbar("Evento eliminado")
                                                }
                                                mostrarDialogo = false
                                            }
                                        ) {
                                            Text("Sí, eliminar", color = Color.Red)
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { mostrarDialogo = false }) {
                                            Text("Cancelar")
                                        }
                                    }
                                )
                            }

                            if (esCreador) {
                                // Igual que en Reservas: swipe con iconos rojos
                                SwipeToDismissBox(
                                    state = dismissState,
                                    enableDismissFromStartToEnd = true,
                                    enableDismissFromEndToStart = true,
                                    backgroundContent = {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(DeleteBg)
                                                .padding(horizontal = 20.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxSize(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Eliminar",
                                                    tint = Color.White
                                                )
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Eliminar",
                                                    tint = Color.White
                                                )
                                            }
                                        }
                                    },
                                    content = {
                                        EventoCard(
                                            evento = evento,
                                            onInscribirse = { vm.inscribirseEnEvento(evento) }
                                        )
                                    }
                                )
                            } else {
                                // Por si en un futuro muestras eventos de otros usuarios: sin swipe
                                EventoCard(
                                    evento = evento,
                                    onInscribirse = { vm.inscribirseEnEvento(evento) }
                                )
                            }
                        }
                    }
                }
            }

            error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
                )
            }
        }
    }
}

/* ============================================================
   🟩 CARD DE EVENTO
   ============================================================ */
@Composable
private fun EventoCard(
    evento: Evento,
    onInscribirse: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Event,
                contentDescription = null,
                tint = Accent,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = evento.nombre,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = evento.tipo,
                    color = Color.LightGray
                )
                if (evento.precioSocio != null || evento.precioNoSocio != null) {
                    Text(
                        text = buildString {
                            append("Socio: ${evento.precioSocio ?: "-"}€  ")
                            append("No socio: ${evento.precioNoSocio ?: "-"}€")
                        },
                        color = Color(0xFFCCCCCC)
                    )
                }
            }

            Button(
                onClick = onInscribirse,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Accent,
                    contentColor = Color.Black
                )
            ) {
                Text("Apuntarme")
            }
        }
    }
}
