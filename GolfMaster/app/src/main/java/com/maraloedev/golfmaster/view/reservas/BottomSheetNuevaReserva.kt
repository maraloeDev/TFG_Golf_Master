package com.maraloedev.golfmaster.view.reservas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.maraloedev.golfmaster.model.Jugadores
import com.maraloedev.golfmaster.model.Reserva
import com.maraloedev.golfmaster.view.auth.register.textFieldColors
import java.util.Calendar

@Composable
fun BottomSheetNuevaReserva(
    vm: ReservasViewModel,
    creando: Boolean,
    onClose: () -> Unit
) {
    val ctx = LocalContext.current

    // Jugadores disponibles (excluye actual desde el VM)
    val jugadores by vm.jugadores.collectAsState()

    // Estado de edición o creación
    val edit = vm.uiSheet.editando
    var hoyos by remember(edit) { mutableStateOf(if (edit) vm.uiSheet.hoyos else 9) }
    var fecha by remember(edit) { mutableStateOf(if (edit) vm.uiSheet.fecha else "") }
    var hora by remember(edit) { mutableStateOf(if (edit) vm.uiSheet.hora else "") }
    var numJugadores by remember(edit) { mutableStateOf(if (edit) vm.uiSheet.numJugadores else 1) }

    var search by remember { mutableStateOf("") }
    var seleccionados by remember(edit) {
        mutableStateOf(if (edit) vm.uiSheet.invitadosSeleccionados.toMutableList() else mutableListOf<Jugadores>())
    }

    // Filtros de búsqueda (maxLines 1)
    val filtrados = remember(search, jugadores) {
        if (search.isBlank()) jugadores
        else jugadores.filter {
            it.nombre_jugador.contains(search, ignoreCase = true) ||
                    it.apellido_jugador.contains(search, ignoreCase = true)
        }
    }

    /* ----- Cabecera ----- */
    Text(
        text = if (edit) "Editar reserva" else "Crear reserva",
        color = Color.White,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    )

    /* ----- Fecha ----- */
    val cal = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        ctx,
        { _, y, m, d ->
            val mm = (m + 1).toString().padStart(2, '0')
            val dd = d.toString().padStart(2, '0')
            fecha = "$y-$mm-$dd"
        },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    )

    OutlinedTextField(
        value = fecha,
        onValueChange = { /* solo mediante picker */ },
        readOnly = true,
        label = { Text("Fecha del juego", color = Color.White) },
        trailingIcon = { TextButton(onClick = { datePicker.show() }) { Text("Elegir") } },
        colors = textFieldColors(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    )

    /* ----- Recorrido (9 o 18) ----- */
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SegmentOpcion("9 hoyos", hoyos == 9) { hoyos = 9 }
        SegmentOpcion("18 hoyos", hoyos == 18) { hoyos = 18 }
    }

    /* ----- Número jugadores (1..4) ----- */
    NumberDropdown(
        label = "Número de jugadores",
        value = numJugadores,
        range = 1..4,
        onChange = { numJugadores = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    )

    /* ----- Hora ----- */
    val timePicker = TimePickerDialog(
        ctx,
        { _, hh, mm -> hora = "${hh.toString().padStart(2, '0')}:${mm.toString().padStart(2, '0')}" },
        9, 0, true
    )
    OutlinedTextField(
        value = hora,
        onValueChange = { /* solo picker */ },
        readOnly = true,
        label = { Text("Hora del juego", color = Color.White) },
        trailingIcon = { TextButton(onClick = { timePicker.show() }) { Text("Elegir") } },
        colors = textFieldColors(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    )

    /* ----- Botón Bloquear ----- */
    Button(
        onClick = {
            // Validaciones
            val totalMax = numJugadores
            val invitadosMax = (totalMax - 1).coerceAtLeast(0)
            if (fecha.isBlank() || hora.isBlank()) {
                Toast.makeText(ctx, "Selecciona fecha y hora.", Toast.LENGTH_SHORT).show(); return@Button
            }
            if (hoyos != 9 && hoyos != 18) {
                Toast.makeText(ctx, "Recorrido debe ser 9 o 18.", Toast.LENGTH_SHORT).show(); return@Button
            }
            if (seleccionados.size > invitadosMax) {
                Toast.makeText(ctx, "Máximo $invitadosMax invitado(s) para $totalMax jugadores.", Toast.LENGTH_SHORT).show(); return@Button
            }

            if (edit) {
                vm.actualizarReserva(
                    id = vm.uiSheet.idEdicion ?: return@Button,
                    nuevosDatos = mapOf(
                        "hoyos" to hoyos,
                        "fecha" to fecha,
                        "hora" to hora,
                        "numJugadores" to numJugadores
                    )
                ) { onClose() }
            } else {
                val base = Reserva(
                    hoyos = hoyos,
                    fecha = fecha,
                    hora = hora,
                    numJugadores = numJugadores
                )
                vm.crearReservaConInvitados(
                    base = base,
                    invitadosIds = seleccionados.map { it.id },
                    onSuccess = onClose,
                    onError = { msg -> Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show() }
                )
            }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF77)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .height(48.dp),
        shape = RoundedCornerShape(24.dp)
    ) { Text(if (edit) "Guardar cambios" else "Bloquear", color = Color.Black, fontWeight = FontWeight.Bold) }

    Divider(
        color = Color.White.copy(alpha = 0.15f),
        modifier = Modifier.padding(top = 6.dp, bottom = 8.dp)
    )

    /* ----- Seleccionar Jugadores ----- */
    Text("Seleccionar Jugadores", color = Color.White, modifier = Modifier.padding(horizontal = 16.dp))

    OutlinedTextField(
        value = search,
        onValueChange = { if (it.count { c -> c == '\n' } == 0) search = it }, // maxLines 1
        label = { Text("Buscar por nombre o apellidos", color = Color.White) },
        singleLine = true,
        maxLines = 1,
        colors = textFieldColors(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(filtrados) { j ->
            val selected = seleccionados.any { it.id == j.id }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (selected) Color(0xFF00FF77) else Color(0x33FFFFFF), RoundedCornerShape(12.dp))
                    .background(if (selected) Color(0x3316C37A) else Color.Transparent, RoundedCornerShape(12.dp))
                    .padding(12.dp)
                    .noRippleClickable {
                        if (selected) seleccionados.removeAll { it.id == j.id }
                        else {
                            val maxInv = (numJugadores - 1).coerceAtLeast(0)
                            if (seleccionados.size >= maxInv) {
                                Toast.makeText(ctx, "Máximo $maxInv invitado(s).", Toast.LENGTH_SHORT).show()
                            } else seleccionados.add(j)
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(36.dp).background(Color(0xFF1F4D3E), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(j.nombre_jugador.firstOrNull()?.uppercase() ?: "?", color = Color.White)
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("${j.nombre_jugador} ${j.apellido_jugador}", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text(j.correo_jugador, color = Color.White.copy(alpha = 0.7f))
                }
                Checkbox(checked = selected, onCheckedChange = null, colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00FF77)))
            }
        }
    }
}

/* --- Subcomponentes UI --- */
@Composable
private fun SegmentOpcion(text: String, active: Boolean, onClick: () -> Unit) {
    val bg = if (active) Color(0xFF00FF77) else Color.Transparent
    val fg = if (active) Color.Black else Color.White
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = fg),
        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
        shape = RoundedCornerShape(24.dp)
    ) { Text(text, color = fg, fontWeight = FontWeight.SemiBold) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NumberDropdown(
    label: String,
    value: Int,
    range: IntRange,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = modifier) {
        OutlinedTextField(
            value = value.toString(),
            onValueChange = { },
            readOnly = true,
            label = { Text(label, color = Color.White) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            colors = textFieldColors(),
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            range.forEach { n ->
                DropdownMenuItem(text = { Text(n.toString()) }, onClick = { onChange(n); expanded = false })
            }
        }
    }
}
