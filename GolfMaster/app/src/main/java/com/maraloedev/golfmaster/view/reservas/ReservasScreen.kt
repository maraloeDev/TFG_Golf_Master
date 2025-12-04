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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import java.util.Calendar
import java.util.Locale
import java.util.UUID

/* ============================================================
    PANTALLA PRINCIPAL DE RESERVAS
   - Muestra pestañas "Próximas" / "Pasadas"
   - Permite eliminar con swipe + confirmación
   - Desde el FAB se crea una nueva reserva (BottomSheet)
   ============================================================ */
@Composable
fun ReservasScreen(
    vm: ReservasViewModel = viewModel()
) {
    val colors = MaterialTheme.colorScheme

    // Estado expuesto por el ViewModel
    val reservas by vm.reservas.collectAsState()
    val loading by vm.loading.collectAsState()
    val invitacionesPendientes by vm.invitacionesPendientes.collectAsState()

    // Snackbar para mensajes de feedback
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Control del formulario (bottom sheet)
    var showForm by remember { mutableStateOf(false) }

    // Pestaña seleccionada: "Próximas" o "Pasadas"
    var selectedTab by remember { mutableStateOf("Próximas") }

    // Instante actual para separar próximas/pasadas
    val ahora = remember { Timestamp.now() }

    // Partición de reservas en próximas y pasadas
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
                containerColor = colors.primary,
                contentColor = colors.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nueva reserva"
                )
            }
        },
        containerColor = colors.background
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .fillMaxSize()
        ) {
            // Pills de pestañas (Próximas / Pasadas)
            BigPills(
                left = "Próximas",
                right = "Pasadas",
                selected = selectedTab,
                onSelect = { selectedTab = it }
            )

            if (loading) {
                // Estado de carga global
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colors.primary)
                }
            } else {
                // Crossfade suave al cambiar de pestaña
                Crossfade(targetState = selectedTab, label = "reservasCrossfade") { tab ->
                    val lista = if (tab == "Próximas") proximas else pasadas

                    // Evitamos duplicados por id + fecha (seguridad extra)
                    val listaUnica = lista.distinctBy { "${it.id}_${it.fecha?.seconds}" }

                    if (listaUnica.isEmpty()) {
                        // Mensaje de lista vacía para esa pestaña
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No hay reservas ${tab.lowercase()}",
                                color = colors.onBackground.copy(alpha = .7f)
                            )
                        }
                    } else {
                        // Lista de reservas con SwipeToDismissBox para eliminar
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            itemsIndexed(
                                items = listaUnica,
                                key = { index, r ->
                                    // Clave relativamente estable y única
                                    val base = (r.id ?: "") +
                                            (r.usuarioId ?: "") +
                                            (r.fecha?.seconds?.toString() ?: "") +
                                            index.toString()
                                    base.ifBlank { UUID.randomUUID().toString() }
                                }
                            ) { _, reserva ->

                                // Control del AlertDialog para esta reserva
                                var mostrarDialogo by remember { mutableStateOf(false) }

                                // Estado del deslizamiento (swipe to dismiss)
                                val dismissState = rememberSwipeToDismissBoxState(
                                    confirmValueChange = { value ->
                                        if (
                                            value == SwipeToDismissBoxValue.StartToEnd ||
                                            value == SwipeToDismissBoxValue.EndToStart
                                        ) {
                                            // Al completar el gesto, mostramos diálogo de confirmación
                                            mostrarDialogo = true
                                            false    // No eliminar automáticamente
                                        } else {
                                            false
                                        }
                                    }
                                )

                                // Diálogo de confirmación de borrado
                                if (mostrarDialogo) {
                                    AlertDialog(
                                        onDismissRequest = { mostrarDialogo = false },
                                        title = { Text("Eliminar reserva") },
                                        text = {
                                            Text("¿Seguro que quieres eliminar esta reserva?")
                                        },
                                        confirmButton = {
                                            TextButton(
                                                onClick = {
                                                    scope.launch {
                                                        reserva.id?.let { vm.eliminarReserva(it) }
                                                        snackbarHost.showSnackbar("Reserva eliminada")
                                                    }
                                                    mostrarDialogo = false
                                                }
                                            ) {
                                                Text("Sí, eliminar", color = colors.error)
                                            }
                                        },
                                        dismissButton = {
                                            TextButton(
                                                onClick = { mostrarDialogo = false }
                                            ) {
                                                Text("Cancelar")
                                            }
                                        }
                                    )
                                }

                                // SwipeToDismissBox con fondo rojo y iconos de borrar
                                SwipeToDismissBox(
                                    state = dismissState,
                                    enableDismissFromStartToEnd = true,
                                    enableDismissFromEndToStart = true,
                                    backgroundContent = {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(colors.error)
                                                .padding(horizontal = 20.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxSize(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.DeleteForever,
                                                    contentDescription = "Eliminar (izquierda)",
                                                    tint = colors.onError
                                                )
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Eliminar (derecha)",
                                                    tint = colors.onError
                                                )
                                            }
                                        }
                                    },
                                    content = {
                                        ReservaCard(reserva)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // (Por ahora solo se lee, si luego quieres mostrar un diálogo para invitaciones,
    //  ya tienes este "invitacionMostrada" disponible)
    val invitacionMostrada = invitacionesPendientes.firstOrNull()

    // BottomSheet para crear una nueva reserva
    if (showForm) {
        val colors = MaterialTheme.colorScheme
        ModalBottomSheet(
            onDismissRequest = { showForm = false },
            containerColor = colors.background
        ) {
            NuevaReservaSheet(
                vm = vm,
                snackbarHostState = snackbarHost
            ) {
                showForm = false
            }
        }
    }
}

/* ============================================================
   TARJETA DE RESERVA
   - Muestra hoyos, fecha, recorrido y nº de jugadores
   ============================================================ */
@Composable
private fun ReservaCard(
    r: Reserva
) {
    val colors = MaterialTheme.colorScheme
    val df = remember { SimpleDateFormat("dd MMMM yyyy - HH:mm", Locale("es", "ES")) }

    // Normalizamos el texto de hoyos:
    // - Si viene vacío → "-- hoyos"
    // - Si ya trae la palabra "hoyo" → se deja tal cual
    // - Si no, se añade "hoyos" al número
    val textoHoyosBruto = r.hoyos?.trim().orEmpty()
    val textoHoyos = when {
        textoHoyosBruto.isBlank() -> "-- hoyos"
        textoHoyosBruto.contains("hoyo", ignoreCase = true) -> textoHoyosBruto
        else -> "$textoHoyosBruto hoyos"
    }

    Surface(
        color = colors.surface,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Text("⛳ $textoHoyos", color = colors.onSurface, fontWeight = FontWeight.SemiBold)
            Text(
                text = r.fecha?.toDate()?.let(df::format) ?: "Sin fecha",
                color = colors.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Recorrido: ${r.recorrido ?: "--"}  ·  Jugadores: ${r.jugadores ?: "Solo"}",
                color = colors.onSurfaceVariant
            )
        }
    }
}

/* ============================================================
    PILLS DE PESTAÑAS (PRÓXIMAS / PASADAS)
   ============================================================ */
@Composable
private fun BigPills(
    left: String,
    right: String,
    selected: String,
    onSelect: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme

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
                    .background(
                        if (isSelected) colors.primary
                        else colors.secondaryContainer
                    )
                    .clickable { onSelect(text) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = if (isSelected) colors.onPrimary else colors.onSecondaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }

        pill(left, selected == left)
        pill(right, selected == right)
    }
}

/* ============================================================
   BOTTOMSHEET: NUEVA RESERVA
   - Fecha/hora
   - Recorrido (9/18 hoyos)
   - Selección de jugadores invitados
   - Valida que no haya otra reserva el mismo día
   ============================================================ */
@Composable
fun NuevaReservaSheet(
    vm: ReservasViewModel,
    snackbarHostState: SnackbarHostState,
    onClose: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    // Estado de jugadores e info de reservas desde el VM
    val jugadores by vm.jugadores.collectAsState()
    val loadingJugadores by vm.loadingJugadores.collectAsState()
    val reservas by vm.reservas.collectAsState()

    // Estado local del formulario
    var fecha by remember { mutableStateOf<Timestamp?>(null) }
    var hoyos by remember { mutableStateOf<String?>(null) }
    var jugadoresSeleccionados by remember { mutableStateOf<List<Jugadores>>(emptyList()) }
    var search by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val currentUser = FirebaseAuth.getInstance().currentUser

    // Botón habilitado si hay fecha + recorrido
    val botonActivo = fecha != null && hoyos != null

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(colors.background)
    ) {
        Text(
            "Nueva Reserva",
            color = colors.onBackground,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(14.dp))

        // Selector Fecha/Hora
        DateTimePickerField(
            label = "Fecha y hora del juego",
            value = fecha
        ) { fecha = it }

        Spacer(Modifier.height(12.dp))

        // Selector de recorrido (9/18 hoyos)
        SelectField(
            label = "Recorrido",
            value = hoyos,
            options = listOf("9 hoyos", "18 hoyos")
        ) { hoyos = it }

        Spacer(Modifier.height(20.dp))

        // Título de bloque de jugadores
        Text(
            "Seleccionar jugadores",
            color = colors.onBackground,
            fontWeight = FontWeight.SemiBold
        )

        // Cuadro de búsqueda de jugadores
        OutlinedTextField(
            value = search,
            onValueChange = {
                search = it
                vm.buscarJugadores(it)
            },
            placeholder = {
                Text(
                    "Buscar jugador...",
                    color = colors.onBackground.copy(alpha = 0.5f)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.outline,
                cursorColor = colors.primary,
                focusedTextColor = colors.onBackground,
                unfocusedTextColor = colors.onBackground
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        // Lista de jugadores o spinner
        if (loadingJugadores) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colors.primary)
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
                    JugadorCard(
                        jugador = jugador,
                        seleccionado = seleccionado
                    ) {
                        jugadoresSeleccionados =
                            if (seleccionado) jugadoresSeleccionados - jugador
                            else jugadoresSeleccionados + jugador
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Botón principal: crea la reserva + invitaciones
        Button(
            onClick = {
                scope.launch {
                    // Regla de negocio: solo una reserva por día
                    val yaReservadoEseDia = reservas.any { existente ->
                        mismaFecha(existente.fecha, fecha)
                    }

                    if (yaReservadoEseDia) {
                        snackbarHostState.showSnackbar(
                            "⚠️ Ya tienes una reserva ese día. No puedes reservar dos veces el mismo día."
                        )
                        return@launch
                    }

                    // Crea reserva + invitaciones en el ViewModel
                    vm.crearReservaConInvitaciones(
                        fecha = fecha,
                        hoyos = hoyos,
                        jugadores = jugadoresSeleccionados
                    )

                    // Mensaje según haya o no invitados
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
                containerColor = if (botonActivo) colors.primary else colors.outline,
                contentColor = if (botonActivo) colors.onPrimary else colors.onSurface
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Bloquear reserva",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

/* ============================================================
    COMPONENTE: DATE + TIME PICKER
   ============================================================ */
@Composable
fun DateTimePickerField(
    label: String,
    value: Timestamp?,
    onPicked: (Timestamp) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val ctx = LocalContext.current
    val cal = remember { Calendar.getInstance() }
    val df = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "ES")) }

    Column(Modifier.fillMaxWidth()) {
        Text(label, color = colors.onBackground, modifier = Modifier.padding(bottom = 6.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    // 1️⃣ Primero mostramos DatePicker
                    DatePickerDialog(
                        ctx,
                        { _, y, m, d ->
                            cal.set(y, m, d)
                            // 2️⃣ Después de elegir fecha, mostramos TimePicker
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
            color = colors.surface
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value?.toDate()?.let(df::format) ?: "Seleccionar fecha y hora",
                    color = if (value == null) colors.onSurfaceVariant else colors.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.Event, contentDescription = null, tint = colors.onSurface)
            }
        }
    }
}

/* ============================================================
    COMPONENTE: DROPDOWN GENÉRICO
   ============================================================ */
@Composable
fun SelectField(
    label: String,
    value: String?,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    var expanded by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth()) {
        Text(label, color = colors.onBackground, modifier = Modifier.padding(bottom = 6.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { expanded = true },
            color = colors.surface
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value ?: "Seleccionar",
                    color = if (value == null) colors.onSurfaceVariant else colors.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = colors.onSurface
                )
            }
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                val isSelected = opt == value
                DropdownMenuItem(
                    text = {
                        Text(
                            text = opt,
                            color = if (isSelected) colors.onPrimary else colors.onSurface,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = { onSelect(opt); expanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) colors.primary
                            else colors.surfaceVariant
                        )
                        .padding(vertical = 4.dp, horizontal = 6.dp)
                )
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

/* ============================================================
    CARD DE JUGADOR PARA INVITAR
   ============================================================ */
@Composable
fun JugadorCard(
    jugador: Jugadores,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val bg = if (seleccionado) colors.primary else colors.secondaryContainer
    val fg = if (seleccionado) colors.onPrimary else colors.onSecondaryContainer

    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Person,
            contentDescription = null,
            tint = fg
        )
        Spacer(Modifier.width(10.dp))
        Text(
            jugador.nombre_jugador,
            color = fg,
            fontWeight = FontWeight.Bold
        )
    }
}

/* ============================================================
    FUNCIÓN AUXILIAR: MISMA FECHA (AÑO + DÍA DEL AÑO)
   - Ignora horas, minutos, etc.
   - Se usa para impedir reservas duplicadas el mismo día.
   ============================================================ */
private fun mismaFecha(a: Timestamp?, b: Timestamp?): Boolean {
    if (a == null || b == null) return false
    val c1 = Calendar.getInstance().apply { time = a.toDate() }
    val c2 = Calendar.getInstance().apply { time = b.toDate() }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
}
