package com.maraloedev.golfmaster.view.reservas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservasScreen(
    viewModel: ReservasViewModel = viewModel()
)
 {
    val state = viewModel.state.value
    val context = LocalContext.current
    val fondo = Color(0xFF0A1A0E)
    val verde = Color(0xFF2BD67B)
    val verdeOscuro = Color(0xFF163021)
    val grisCampo = Color(0xFF111F1A)
    val textoSecundario = Color(0xFF9CA3AF)
    var nuevoJugador by remember { mutableStateOf("") }
    var amigoBusqueda by remember { mutableStateOf("") }

    // Alerta de confirmación
    if (state.confirmacionGuardada) {
        AlertDialog(
            onDismissRequest = { viewModel.cerrarConfirmacion() },
            confirmButton = {
                TextButton(onClick = { viewModel.cerrarConfirmacion() }) {
                    Text("Aceptar", color = verde)
                }
            },
            title = { Text("Reserva confirmada") },
            text = { Text("Tu reserva se ha confirmado correctamente.") },
            containerColor = grisCampo,
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    Scaffold(
        containerColor = fondo,
        topBar = {
            TopAppBar(
                title = { Text("Reservas", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = fondo,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Pestañas
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(grisCampo, RoundedCornerShape(50.dp))
                        .padding(4.dp)
                ) {
                    BotonSegmento(
                        texto = "Próximas",
                        activo = state.pestañaSeleccionada == "Proximas",
                        colorActivo = verde,
                        onClick = { viewModel.seleccionarPestaña("Proximas") },
                        modifier = Modifier.weight(1f)
                    )
                    BotonSegmento(
                        texto = "Pasadas",
                        activo = state.pestañaSeleccionada == "Pasadas",
                        colorActivo = verdeOscuro,
                        onClick = { viewModel.seleccionarPestaña("Pasadas") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (state.pestañaSeleccionada == "Pasadas") {
                item {
                    when {
                        state.loadingPasadas -> Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = verde) }
                        state.errorPasadas != null -> Text("Error: ${state.errorPasadas}", color = Color.Red)
                        state.reservasPasadas.isEmpty() -> Text("No hay reservas pasadas.", color = Color.White)
                        else -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            state.reservasPasadas.forEach { r ->
                                ElevatedCard(
                                    colors = CardDefaults.elevatedCardColors(containerColor = grisCampo)
                                ) {
                                    Column(Modifier.padding(12.dp)) {
                                        Text("${r.fecha} • ${r.hora}", color = Color.White, fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.height(2.dp))
                                        Text("${r.recorrido} • ${r.numJugadores} jugadores", color = textoSecundario, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Campos principales (para Próximas)
                item {
                    CampoReserva(
                        titulo = "Fecha del juego",
                        valor = state.fechaJuego,
                        icono = Icons.Default.CalendarToday,
                        onClick = {
                            val c = Calendar.getInstance()
                            DatePickerDialog(
                                context,
                                { _, y, m, d ->
                                    val fecha = String.format("%02d/%02d/%04d", d, m + 1, y)
                                    viewModel.seleccionarFecha(fecha)
                                },
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                    )

                    CampoDropdown(
                        titulo = "Recorrido",
                        opciones = listOf("9 hoyos", "18 hoyos"),
                        valorSeleccionado = state.recorrido,
                        onSeleccionar = viewModel::seleccionarRecorrido
                    )

                    CampoDropdown(
                        titulo = "Número de jugadores",
                        opciones = listOf("1", "2", "3", "4"),
                        valorSeleccionado = state.numJugadores,
                        onSeleccionar = viewModel::seleccionarNumeroJugadores
                    )

                    CampoReserva(
                        titulo = "Hora de juego",
                        valor = state.horaJuego,
                        onClick = {
                            val cal = Calendar.getInstance()
                            TimePickerDialog(
                                context,
                                { _, h, m -> viewModel.seleccionarHora(h, m) },
                                cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE),
                                true
                            ).show()
                        }
                    )

                    // --- BOTÓN PRINCIPAL ---
                    Button(
                        onClick = {
                            if (!state.bloqueada) viewModel.bloquearReserva()
                            else viewModel.confirmarReserva()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = verde),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        when {
                            state.bloqueada && state.enProgreso ->
                                Text("Confirmar reserva (${state.tiempoRestante}s)", color = Color.Black, fontWeight = FontWeight.Bold)
                            else ->
                                Text("Bloquear", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Buscador de amigo en base de datos
                item {
                    OutlinedTextField(
                        value = amigoBusqueda,
                        onValueChange = { amigoBusqueda = it },
                        label = { Text("Buscar amigo por nombre o correo") },
                        trailingIcon = {
                            IconButton(onClick = {
                                viewModel.buscarYAgregarAmigo(amigoBusqueda) { ok, msg ->
                                    if (!ok) Toast.makeText(context, msg ?: "No encontrado", Toast.LENGTH_SHORT).show()
                                    else Toast.makeText(context, "Amigo añadido", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Icon(Icons.Default.Search, contentDescription = "Buscar")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

                // Jugadores
                item { Text("Seleccionar Jugadores", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp) }

                items(state.jugadores.size) { index ->
                    val jugador = state.jugadores[index]
                    JugadorCard(
                        nombre = jugador.nombre,
                        detalle = jugador.detalle,
                        invitado = jugador.invitado,
                        seleccionado = jugador.seleccionado,
                        onClick = { viewModel.seleccionarJugador(jugador.nombre) }
                    )
                }

                // Añadir jugador
                item {
                    OutlinedTextField(
                        value = nuevoJugador,
                        onValueChange = { nuevoJugador = it },
                        placeholder = { Text("Añadir nuevo jugador...", color = textoSecundario) },
                        trailingIcon = {
                            IconButton(onClick = {
                                viewModel.añadirJugador(nuevoJugador)
                                nuevoJugador = ""
                            }) {
                                Icon(Icons.Default.Add, contentDescription = "Añadir", tint = verde)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(grisCampo, RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFF1F4D3E), RoundedCornerShape(10.dp)),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = verde
                        )
                    )
                }
            }
        }
    }
}

// ...resto de componentes (CampoReserva, CampoDropdown, JugadorCard, BotonSegmento) sin cambios...
