@file:OptIn(ExperimentalMaterial3Api::class)

package com.maraloedev.golfmaster.view.reservas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.maraloedev.golfmaster.model.Jugadores
import com.maraloedev.golfmaster.model.Reserva
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.UUID

/* ============================================================
   🎨 COLORES
   ============================================================ */
private val PillSelected = Color(0xFF1F4D3E)
private val PillUnselected = Color(0xFF00FF77)
private val ScreenBg = Color(0xFF00281F)
private val CardBg = Color(0xFF0D1B12)

/* ============================================================
   🟩 PANTALLA PRINCIPAL DE RESERVAS
   ============================================================ */
@Composable
fun ReservasScreen(vm: ReservasViewModel = viewModel()) {
    val reservas by vm.reservas.collectAsState()
    val loading by vm.loading.collectAsState()
    val snackbarHost = remember { SnackbarHostState() }

    var showForm by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("Próximas") }

    val ahora = remember { Timestamp.now() }

    val (proximas, pasadas) = remember(reservas) {
        val prox = reservas.filter { (it.fecha?.seconds ?: 0) > ahora.seconds }
        val pas = reservas.filter { (it.fecha?.seconds ?: 0) <= ahora.seconds }
        prox to pas
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHost) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showForm = true },
                containerColor = PillUnselected
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva reserva", tint = Color.Black)
            }
        },
        containerColor = ScreenBg
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .fillMaxSize()
                .background(ScreenBg)
        ) {
            BigPills(
                left = "Próximas",
                right = "Pasadas",
                selected = selectedTab,
                onSelect = { selectedTab = it }
            )

            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PillUnselected)
                }
            } else {
                Crossfade(targetState = selectedTab, label = "reservasCrossfade") { tab ->
                    val lista = if (tab == "Próximas") proximas else pasadas

                    // 🔧 Elimina duplicados por id + fecha.seconds
                    val listaUnica = lista.distinctBy { "${it.id}_${it.fecha?.seconds}" }

                    if (listaUnica.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "No hay reservas ${tab.lowercase()}",
                                color = Color.White.copy(alpha = .7f)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // 🔒 Claves únicas con índice (0 % duplicados)
                            itemsIndexed(
                                items = listaUnica,
                                key = { index, r ->
                                    val base = (r.id ?: "") +
                                            (r.usuarioId ?: "") +
                                            (r.fecha?.seconds?.toString() ?: "") +
                                            index.toString()
                                    base.ifBlank { UUID.randomUUID().toString() }
                                }
                            ) { _, r ->
                                ReservaCard(r)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showForm) {
        ModalBottomSheet(
            onDismissRequest = { showForm = false },
            containerColor = ScreenBg
        ) {
            NuevaReservaSheet(vm = vm, snackbarHostState = snackbarHost) {
                showForm = false
            }
        }
    }
}

/* ============================================================
   🟩 CARD DE RESERVA
   ============================================================ */
@Composable
private fun ReservaCard(r: Reserva) {
    val df = remember { SimpleDateFormat("dd MMMM yyyy - HH:mm", Locale("es", "ES")) }

    Surface(
        color = CardBg,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Text("⛳ ${r.hoyos ?: "--"} Hoyos", color = Color.White, fontWeight = FontWeight.SemiBold)
            Text(
                r.fecha?.toDate()?.let(df::format) ?: "Sin fecha",
                color = Color.White.copy(alpha = .8f)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Recorrido: ${r.recorrido ?: "--"}  ·  Jugadores: ${r.jugadores ?: "Solo"}",
                color = Color.White.copy(alpha = .8f)
            )
        }
    }
}

/* ============================================================
   🟩 PILLS
   ============================================================ */
@Composable
private fun BigPills(
    left: String,
    right: String,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        @Composable
        fun pill(text: String, isSelected: Boolean) =
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(100))
                    .background(if (isSelected) PillUnselected else PillSelected)
                    .clickable { onSelect(text) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = if (isSelected) Color.Black else Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

        pill(left, selected == left)
        pill(right, selected == right)
    }
}

/* ============================================================
   🟩 NUEVA RESERVA (Sheet)
   ============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaReservaSheet(
    vm: ReservasViewModel,
    snackbarHostState: SnackbarHostState,
    onClose: () -> Unit
) {
    val jugadores by vm.jugadores.collectAsState()
    val loadingJugadores by vm.loadingJugadores.collectAsState()

    var fecha by remember { mutableStateOf<Timestamp?>(null) }
    var hoyos by remember { mutableStateOf<String?>(null) }
    var jugadoresSeleccionados by remember { mutableStateOf<List<Jugadores>>(emptyList()) }
    var search by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val botonActivo = fecha != null && hoyos != null

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(ScreenBg)
    ) {
        Text(
            "Nueva Reserva",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(14.dp))

        DateTimePickerField("Fecha y hora del juego", fecha) { fecha = it }
        Spacer(Modifier.height(12.dp))

        SelectField("Recorrido", hoyos, listOf("9 hoyos", "18 hoyos")) { hoyos = it }
        Spacer(Modifier.height(20.dp))

        Text("Seleccionar jugadores", color = Color.White, fontWeight = FontWeight.SemiBold)

        OutlinedTextField(
            value = search,
            onValueChange = {
                search = it
                vm.buscarJugadores(it)
            },
            placeholder = { Text("Buscar jugador...", color = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PillUnselected,
                unfocusedBorderColor = Color.DarkGray,
                cursorColor = PillUnselected,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        if (loadingJugadores) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PillUnselected)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 250.dp)
                    .padding(vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(
                    jugadores.filter { it.id != currentUser?.uid },
                    key = { it.id.ifBlank { UUID.randomUUID().toString() } }
                ) { jugador ->
                    val seleccionado = jugadoresSeleccionados.contains(jugador)
                    JugadorCard(jugador = jugador, seleccionado = seleccionado) {
                        jugadoresSeleccionados =
                            if (seleccionado) jugadoresSeleccionados - jugador else jugadoresSeleccionados + jugador
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                scope.launch {
                    vm.crearReservaConInvitaciones(
                        fecha = fecha,
                        hoyos = hoyos,
                        jugadores = jugadoresSeleccionados
                    )
                    snackbarHostState.showSnackbar(
                        if (jugadoresSeleccionados.isEmpty())
                            "✅ Reserva creada correctamente"
                        else
                            "✅ Reserva creada e invitaciones enviadas"
                    )
                    onClose()
                }
            },
            enabled = botonActivo,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (botonActivo) PillUnselected else Color.Gray
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Bloquear reserva",
                color = if (botonActivo) Color.Black else Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

/* ============================================================
   🟩 PICKER DE FECHA Y HORA
   ============================================================ */
@Composable
fun DateTimePickerField(
    label: String,
    value: Timestamp?,
    onPicked: (Timestamp) -> Unit
) {
    val ctx = LocalContext.current
    val cal = remember { Calendar.getInstance() }
    val df = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "ES")) }

    Column(Modifier.fillMaxWidth()) {
        Text(label, color = Color.White, modifier = Modifier.padding(bottom = 6.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    DatePickerDialog(
                        ctx,
                        { _, y, m, d ->
                            cal.set(y, m, d)
                            TimePickerDialog(
                                ctx,
                                { _, h, min ->
                                    cal.set(Calendar.HOUR_OF_DAY, h)
                                    cal.set(Calendar.MINUTE, min)
                                    onPicked(Timestamp(cal.time))
                                },
                                cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE),
                                true
                            ).show()
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
            color = CardBg
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value?.toDate()?.let(df::format) ?: "Seleccionar fecha y hora",
                    color = if (value == null) Color.Gray else Color.White,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.Event, contentDescription = null, tint = Color.White)
            }
        }
    }
}

/* ============================================================
   🟩 SELECT FIELD
   ============================================================ */
@Composable
fun SelectField(
    label: String,
    value: String?,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth()) {
        Text(label, color = Color.White, modifier = Modifier.padding(bottom = 6.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { expanded = true },
            color = CardBg
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value ?: "Seleccionar",
                    color = if (value == null) Color.Gray else Color.White,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
            }
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                val isSelected = opt == value
                DropdownMenuItem(
                    text = {
                        Text(
                            text = opt,
                            color = if (isSelected) Color.Black else Color.White,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = { onSelect(opt); expanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) PillUnselected else PillSelected)
                        .padding(vertical = 4.dp, horizontal = 6.dp)
                )
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

/* ============================================================
   🟩 JUGADOR CARD
   ============================================================ */
@Composable
fun JugadorCard(jugador: Jugadores, seleccionado: Boolean, onClick: () -> Unit) {
    val bg = if (seleccionado) PillUnselected else PillSelected
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Person, contentDescription = null, tint = if (seleccionado) Color.Black else Color.White)
        Spacer(Modifier.width(10.dp))
        Text(jugador.nombre_jugador, color = if (seleccionado) Color.Black else Color.White, fontWeight = FontWeight.Bold)
    }
}
