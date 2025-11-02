package com.maraloedev.golfmaster.view.reservas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import com.maraloedev.golfmaster.model.Reserva
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservasScreen(vm: ReservasViewModel = viewModel()) {
    val reservas by vm.reservas.collectAsState()
    val loading by vm.loading.collectAsState()
    var showForm by remember { mutableStateOf(false) }
    var showEdit by remember { mutableStateOf<Reserva?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val ahora = Timestamp.now()

    val proximas = reservas.filter { it.fecha?.seconds ?: 0 > ahora.seconds }
    val pasadas = reservas.filter { it.fecha?.seconds ?: 0 <= ahora.seconds }
    var selectedTab by remember { mutableStateOf("Próximas") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showForm = true },
                containerColor = Color(0xFF00FF77)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva reserva", tint = Color.Black)
            }
        },
        containerColor = Color(0xFF00281F),
        topBar = {
            TopAppBar(
                title = { Text("Reservas", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0C1A12))
            )
        }
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .fillMaxSize()
                .background(Color(0xFF00281F))
        ) {
            SegmentedSelector(
                options = listOf("Próximas", "Pasadas"),
                selectedOption = selectedTab,
                onOptionSelected = { selectedTab = it }
            )

            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF00FF77))
                }
            } else {
                val lista = if (selectedTab == "Próximas") proximas else pasadas
                if (lista.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay reservas ${selectedTab.lowercase()}", color = Color.White.copy(alpha = 0.7f))
                    }
                } else {
                    LazyColumn(
                        Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(lista) { reserva ->
                            ReservaCard(reserva) { showEdit = reserva }
                        }
                    }
                }
            }
        }
    }

    // Modal nueva reserva
    if (showForm) {
        ModalBottomSheet(
            onDismissRequest = { showForm = false },
            containerColor = Color(0xFF00281F)
        ) {
            NuevaReservaSheet(vm, snackbarHostState)
        }
    }

    // Modal editar reserva
    showEdit?.let { reserva ->
        ModalBottomSheet(
            onDismissRequest = { showEdit = null },
            containerColor = Color(0xFF00281F)
        ) {
            EditarReservaSheet(vm, reserva, snackbarHostState) { showEdit = null }
        }
    }
}

/* ----------------------------- COMPONENTES ----------------------------- */

@Composable
private fun SegmentedSelector(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Row(
        Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .background(Color(0xFF0D1B12), RoundedCornerShape(50))
            .padding(6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        options.forEach { option ->
            val selected = option == selectedOption
            val bg = if (selected) Color(0xFF00FF77) else Color.Transparent
            val fg = if (selected) Color.Black else Color.White
            OutlinedButton(
                onClick = { onOptionSelected(option) },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = bg, contentColor = fg),
                border = null,
                modifier = Modifier.height(42.dp)
            ) { Text(option, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun ReservaCard(r: Reserva, onClick: () -> Unit) {
    val df = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "ES")) }
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B12)),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column(Modifier.padding(14.dp)) {
            Text("📅 ${r.fecha?.toDate()?.let(df::format) ?: "--"}", color = Color.White, fontWeight = FontWeight.Bold)
            Text("🏌️ ${r.recorrido ?: "Sin recorrido"}", color = Color.White.copy(alpha = 0.8f))
            Text("👥 ${r.jugadores ?: "0"} jugadores", color = Color.White.copy(alpha = 0.8f))
        }
    }
}

/* ======================= NUEVA RESERVA ======================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaReservaSheet(vm: ReservasViewModel, snackbarHostState: SnackbarHostState) {
    var fecha by remember { mutableStateOf<Timestamp?>(null) }
    var hora by remember { mutableStateOf<Timestamp?>(null) }
    var recorrido by remember { mutableStateOf<String?>(null) }
    var numHoyos by remember { mutableStateOf<String?>(null) }
    var numJugadores by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val botonActivo = fecha != null && hora != null && recorrido != null && numJugadores != null

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            "Nueva Reserva",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = MaterialTheme.typography.titleLarge.fontSize
        )

        Spacer(Modifier.height(14.dp))

        ComboBoxFecha("Fecha del juego", fecha) { fecha = it }
        ComboBoxHora("Hora del juego", hora) { hora = it }
        ComboBox("Recorrido", listOf("Campo Norte", "Campo Sur"), recorrido) { recorrido = it }
        ComboBox("Número de hoyos", listOf("9", "18"), numHoyos) { numHoyos = it }
        ComboBox("Número de jugadores", listOf("1", "2", "3", "4"), numJugadores) { numJugadores = it }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                scope.launch {
                    vm.crearReserva(fecha, hora, recorrido, numHoyos, numJugadores)
                    snackbarHostState.showSnackbar("✅ Reserva creada con éxito")
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (botonActivo) Color(0xFF00FF77) else Color.Gray
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth(),
            enabled = botonActivo
        ) {
            Text(
                "Bloquear",
                color = if (botonActivo) Color.Black else Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/* ======================= EDITAR RESERVA ======================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarReservaSheet(
    vm: ReservasViewModel,
    reserva: Reserva,
    snackbarHostState: SnackbarHostState,
    onClose: () -> Unit
) {
    var fecha by remember { mutableStateOf(reserva.fecha) }
    var hora by remember { mutableStateOf(reserva.hora) }
    var recorrido by remember { mutableStateOf(reserva.recorrido) }
    var numHoyos by remember { mutableStateOf(reserva.hoyos) }
    var numJugadores by remember { mutableStateOf(reserva.jugadores) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val botonActivo = fecha != null && hora != null && !recorrido.isNullOrBlank() && !numJugadores.isNullOrBlank()

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text("Editar Reserva", color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(14.dp))

        ComboBoxFecha("Fecha", fecha) { fecha = it }
        ComboBoxHora("Hora", hora) { hora = it }
        ComboBox("Recorrido", listOf("Campo Norte", "Campo Sur"), recorrido) { recorrido = it }
        ComboBox("Número de hoyos", listOf("9", "18"), numHoyos) { numHoyos = it }
        ComboBox("Número de jugadores", listOf("1", "2", "3", "4"), numJugadores) { numJugadores = it }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                scope.launch {
                    vm.actualizarReserva(
                        id = reserva.id,
                        fecha = fecha,
                        hora = hora,
                        recorrido = recorrido,
                        hoyos = numHoyos,
                        jugadores = numJugadores
                    )
                    snackbarHostState.showSnackbar("✅ Reserva actualizada")
                    onClose()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (botonActivo) Color(0xFF00FF77) else Color.Gray
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth(),
            enabled = botonActivo
        ) {
            Icon(Icons.Filled.Edit, contentDescription = null, tint = Color.Black)
            Spacer(Modifier.width(6.dp))
            Text("Actualizar", color = if (botonActivo) Color.Black else Color.White)
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = { showConfirmDialog = true },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Delete, contentDescription = null, tint = Color.Red)
            Spacer(Modifier.width(6.dp))
            Text("Eliminar", color = Color.Red)
        }
    }

    /* ---- Confirmación al eliminar ---- */
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        vm.eliminarReserva(reserva.id)
                        snackbarHostState.showSnackbar("🗑️ Reserva eliminada")
                        onClose()
                    }
                    showConfirmDialog = false
                }) {
                    Text("Sí", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("No", color = Color.White)
                }
            },
            title = { Text("Eliminar reserva", color = Color.White) },
            text = { Text("¿Seguro que quieres eliminar esta reserva?", color = Color.White) },
            containerColor = Color(0xFF0C1A12)
        )
    }
}

/* ===================== COMPONENTES AUXILIARES ===================== */

@Composable
private fun ComboBoxFecha(label: String, value: Timestamp?, onPicked: (Timestamp) -> Unit) {
    val ctx = LocalContext.current
    val df = remember { SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES")) }
    val cal = Calendar.getInstance()

    ComboBoxBase(label, value?.toDate()?.let(df::format) ?: "") {
        DatePickerDialog(
            ctx,
            { _, y, m, d ->
                cal.set(y, m, d, 0, 0, 0)
                onPicked(Timestamp(cal.time))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}

@Composable
private fun ComboBoxHora(label: String, value: Timestamp?, onPicked: (Timestamp) -> Unit) {
    val ctx = LocalContext.current
    val cal = Calendar.getInstance()

    ComboBoxBase(label, value?.toDate()?.let { SimpleDateFormat("HH:mm", Locale("es", "ES")).format(it) } ?: "") {
        TimePickerDialog(
            ctx,
            { _, h, m ->
                cal.set(Calendar.HOUR_OF_DAY, h)
                cal.set(Calendar.MINUTE, m)
                onPicked(Timestamp(cal.time))
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }
}

@Composable
private fun ComboBoxBase(label: String, value: String, onClick: () -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label, color = Color.White) },
        readOnly = true,
        textStyle = LocalTextStyle.current.copy(color = Color.White),

        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    )
}

@Composable
private fun ComboBox(label: String, opciones: List<String>, seleccion: String?, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = seleccion ?: "",
            onValueChange = {},
            label = { Text(label, color = Color.White) },
            readOnly = true,
            textStyle = LocalTextStyle.current.copy(color = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },

        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF0D1B12))
        ) {
            opciones.forEach {
                DropdownMenuItem(
                    text = { Text(it, color = Color.White) },
                    onClick = {
                        onSelect(it)
                        expanded = false
                    }
                )
            }
        }
    }
}


