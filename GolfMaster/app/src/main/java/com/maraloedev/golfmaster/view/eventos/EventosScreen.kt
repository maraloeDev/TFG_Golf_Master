package com.maraloedev.golfmaster.view.eventos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventosScreen(vm: EventosViewModel = viewModel()) {
    val ui by vm.ui.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    Scaffold(
        containerColor = Color(0xFF0B3D2E),
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("Eventos", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B3D2E))
            )
        }
    ) { pv ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(pv)
                .background(Color(0xFF0B3D2E))
        ) {
            when {
                ui.loading -> {
                    CircularProgressIndicator(
                        color = Color(0xFF00FF77),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                ui.error != null -> {
                    Column(
                        Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(ui.error ?: "Error desconocido", color = Color.Red)
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { vm.cargarEventos() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF77))
                        ) {
                            Text("Reintentar", color = Color(0xFF0B3D2E))
                        }
                    }
                }

                ui.eventos.isEmpty() -> {
                    Text(
                        "No hay eventos disponibles 📅",
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        items(ui.eventos) { evento ->
                            EventoCard(
                                evento = evento,
                                onInscribirse = {
                                    vm.inscribirse(
                                        evento.id,
                                        onSuccess = { scope.launch { snackbar.showSnackbar("Inscripción realizada ✅") } },
                                        onError = { e -> scope.launch { snackbar.showSnackbar("⚠️ $e") } }
                                    )
                                },
                                onCancelar = {
                                    vm.cancelarInscripcion(
                                        evento.id,
                                        onSuccess = { scope.launch { snackbar.showSnackbar("Inscripción cancelada ❌") } },
                                        onError = { e -> scope.launch { snackbar.showSnackbar("⚠️ $e") } }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventoCard(
    evento: Evento,
    onInscribirse: () -> Unit,
    onCancelar: () -> Unit
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val inscrito = userId != null && userId in evento.inscritos

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF173E34))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    tint = Color(0xFF00FF77)
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(evento.titulo, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("${evento.fecha} • ${evento.hora}", color = Color.Gray, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(evento.descripcion, color = Color.White.copy(alpha = 0.9f))
            Spacer(Modifier.height(8.dp))
            Text("Lugar: ${evento.lugar}", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text("Inscritos: ${evento.inscritos.size}", color = Color.Gray, fontSize = 13.sp)
            Spacer(Modifier.height(12.dp))

            if (inscrito) {
                Button(
                    onClick = onCancelar,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f))
                ) {
                    Icon(Icons.Default.EventBusy, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(6.dp))
                    Text("Cancelar inscripción", color = Color.White)
                }
            } else {
                Button(
                    onClick = onInscribirse,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF77))
                ) {
                    Icon(Icons.Default.EventAvailable, contentDescription = null, tint = Color(0xFF0B3D2E))
                    Spacer(Modifier.width(6.dp))
                    Text("Inscribirme", color = Color(0xFF0B3D2E))
                }
            }
        }
    }
}
