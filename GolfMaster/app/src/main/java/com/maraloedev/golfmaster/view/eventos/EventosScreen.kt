package com.maraloedev.golfmaster.view.eventos

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.maraloedev.golfmaster.model.Evento
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/* ===== 🎨 Colores ===== */
private val PillSelected = Color(0xFF1F4D3E)
private val PillUnselected = Color(0xFF00FF77)
private val ScreenBg = Color(0xFF00281F)
private val CardBg = Color(0xFF0D1B12)

/* ===== 🏆 Pantalla principal ===== */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventosScreen(vm: EventosViewModel = viewModel()) {
    val eventos by vm.eventos.collectAsState()
    val loading by vm.loading.collectAsState()
    val snackbarHost = remember { SnackbarHostState() }

    var showForm by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("Próximos") }

    val ahora = remember { Timestamp.now() }
    val (proximos, finalizados) = remember(eventos) {
        val prox = eventos.filter { (it.fechaFin?.seconds ?: 0) > ahora.seconds }
        val fin = eventos.filter { (it.fechaFin?.seconds ?: 0) <= ahora.seconds }
        prox to fin
    }

    val focusManager = LocalFocusManager.current

    // ✅ Solo cargar una vez, no en cada recomposición
    LaunchedEffect(Unit) {
        vm.cargarEventos()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHost) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showForm = true }, containerColor = PillUnselected) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo evento", tint = Color.Black)
            }
        },
        containerColor = ScreenBg
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
                .background(ScreenBg)
        ) {
            BigPillsEventos(
                left = "Próximos",
                right = "Finalizados",
                selected = selectedTab,
                onSelect = { selectedTab = it }
            )

            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PillUnselected)
                }
            } else {
                Crossfade(targetState = selectedTab, label = "eventosCrossfade") { tab ->
                    val lista = if (tab == "Próximos") proximos else finalizados
                    if (lista.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "No hay eventos ${tab.lowercase()}",
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
                            items(lista, key = { it.id ?: it.hashCode() }) { e ->
                                EventoCard(e, vm, snackbarHost)
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
            NuevoEventoSheet(vm = vm, snackbarHostState = snackbarHost) { showForm = false }
        }
    }
}

/* ===== 🟩 Pills ===== */
@Composable
fun BigPillsEventos(left: String, right: String, selected: String, onSelect: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(14.dp))
            .padding(10.dp),
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
                    text,
                    color = if (isSelected) Color.Black else Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

        pill(left, selected == left)
        pill(right, selected == right)
    }
}

/* ===== 🟩 Card de evento (ACTUALIZADA) ===== */
@Composable
fun EventoCard(e: Evento, vm: EventosViewModel, snackbarHost: SnackbarHostState) {
    val df = remember { SimpleDateFormat("dd MMMM, yyyy - HH:mm", Locale("es", "ES")) }
    val scope = rememberCoroutineScope()

    val numInscritos = e.inscritos.size
    val plazasTotales = e.plazas ?: 0
    val completo = plazasTotales > 0 && numInscritos >= plazasTotales

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val yaInscrito = uid != null && e.inscritos.contains(uid)

    // 🔹 Evento pasado (usamos fechaFin, y si no tiene, fechaInicio)
    val ahora = remember { Timestamp.now() }
    val baseSeconds = e.fechaFin?.seconds ?: e.fechaInicio?.seconds ?: 0
    val eventoPasado = baseSeconds < ahora.seconds

    // 🔹 Estado para el diálogo de confirmación
    var showConfirmDialog by remember { mutableStateOf(false) }

    Card(colors = CardDefaults.cardColors(containerColor = CardBg)) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {

            // Cabecera con nombre y botón eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "🏌️ ${e.nombre ?: "--"}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                if (e.id != null) {
                    IconButton(
                        onClick = {
                            // 👉 Primero mostramos el diálogo
                            showConfirmDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar evento",
                            tint = Color(0xFFFF5555)
                        )
                    }
                }
            }

            Text(
                "${e.fechaInicio?.toDate()?.let(df::format)} → ${e.fechaFin?.toDate()?.let(df::format)}",
                color = Color.White.copy(alpha = .8f)
            )
            Spacer(Modifier.height(6.dp))
            Text("Tipo: ${e.tipo ?: "--"}", color = Color.White.copy(alpha = .8f))
            Text(
                "💰 Socio: ${e.precioSocio ?: "--"}€ · No socio: ${e.precioNoSocio ?: "--"}€",
                color = Color.White.copy(alpha = .8f)
            )

            Spacer(Modifier.height(6.dp))

            val plazasTexto = if (plazasTotales > 0) " / $plazasTotales plazas" else ""
            Text(
                text = "👥 $numInscritos inscritos$plazasTexto",
                color = Color.White.copy(alpha = .8f),
                fontWeight = FontWeight.SemiBold
            )

            if (eventoPasado) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "⏰ Evento finalizado",
                    color = Color(0xFFFFC107),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(10.dp))

            val botonHabilitado = uid != null && !yaInscrito && !completo && !eventoPasado

            Button(
                onClick = {
                    scope.launch {
                        vm.inscribirseEnEvento(e)
                        snackbarHost.showSnackbar("✅ Inscripción completada")
                    }
                },
                enabled = botonHabilitado,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (botonHabilitado) PillUnselected else Color.Gray
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    when {
                        eventoPasado -> "Evento finalizado"
                        yaInscrito -> "Ya estás inscrito"
                        completo -> "Evento completo"
                        else -> "Inscribirse"
                    },
                    color = if (botonHabilitado) Color.Black else Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // 🛑 AlertDialog de confirmación
    if (showConfirmDialog && e.id != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Eliminar evento") },
            text = { Text("¿Desea eliminar el evento?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        scope.launch {
                            vm.eliminarEvento(e.id)
                            // 🔄 Esto dispara cargarEventos() en el ViewModel
                            // y la lista se actualiza "en tiempo real"
                            snackbarHost.showSnackbar("🗑️ Evento eliminado")
                        }
                    }
                ) {
                    Text("Eliminar", color = Color(0xFFFF5555))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}


/* ===== 🟩 Nuevo evento ===== */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoEventoSheet(
    vm: EventosViewModel,
    snackbarHostState: SnackbarHostState,
    onClose: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf<String?>(null) }

    // 🔹 Solo precios, sin plazas (por ahora)
    val precioSocio = 5
    val precioNoSocio = 22

    var fechaInicio by remember { mutableStateOf<Timestamp?>(null) }
    var fechaFin by remember { mutableStateOf<Timestamp?>(null) }

    val scope = rememberCoroutineScope()
    val botonActivo = nombre.isNotBlank() && tipo != null && fechaInicio != null && fechaFin != null

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text("Nuevo Evento", color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(14.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del torneo", color = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PillUnselected,
                unfocusedBorderColor = Color.DarkGray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))
        DateTimePickerFieldEvento("Inicio del torneo", fechaInicio) { fechaInicio = it }
        DateTimePickerFieldEvento("Fin del torneo", fechaFin) { fechaFin = it }

        Spacer(Modifier.height(10.dp))
        SelectFieldEvento(
            label = "Tipo de torneo",
            value = tipo,
            options = listOf("Stableford", "Stroke Play", "Match Play", "Scramble", "Medal Play")
        ) { tipo = it }

        Spacer(Modifier.height(20.dp))
        PreciosSection(precioSocio = precioSocio, precioNoSocio = precioNoSocio)

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                scope.launch {
                    vm.crearEvento(
                        nombre = nombre,
                        tipo = tipo,
                        precioSocio = precioSocio.toString(),
                        precioNoSocio = precioNoSocio.toString(),
                        fechaInicio = fechaInicio,
                        fechaFin = fechaFin
                    )
                    snackbarHostState.showSnackbar("✅ Evento creado con éxito")
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
                "Crear Evento",
                color = if (botonActivo) Color.Black else Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/* ===== 💰 Solo precios ===== */
@Composable
fun PreciosSection(precioSocio: Int, precioNoSocio: Int) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedCard(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, Color(0xFFBBA864))
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Socio (€): $precioSocio", color = Color.White, fontSize = 16.sp)
            }
        }
        OutlinedCard(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, Color(0xFFBBA864))
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No socio (€): $precioNoSocio", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

/* ===== 🕓 DateTime Picker ===== */
@Composable
fun DateTimePickerFieldEvento(
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

/* ===== 🎯 Select tipo ===== */
@Composable
fun SelectFieldEvento(
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
                    text = value ?: "Seleccionar tipo",
                    color = if (value == null) Color.Gray else Color.White,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(CardBg)
        ) {
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
                        .background(if (isSelected) PillUnselected else PillSelected)
                )
            }
        }
    }
}
