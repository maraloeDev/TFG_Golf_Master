package com.maraloedev.golfmaster.view.reservas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.maraloedev.golfmaster.model.Reserva
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservasScreen(vm: ReservasViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val openSheet = remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("Próximas") }

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val reservas by vm.reservas.collectAsState()
    val loading by vm.loading.collectAsState()

    // Cargar reservas al iniciar
    LaunchedEffect(userId) {
        if (userId.isNotBlank()) vm.cargarReservas(userId)
    }

    val ahora = Timestamp.now()
    val proximas = reservas.filter { it.fecha?.seconds ?: 0 > ahora.seconds }
    val pasadas = reservas.filter { it.fecha?.seconds ?: 0 <= ahora.seconds }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openSheet.value = true },
                containerColor = Color(0xFF00FF77)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva reserva", tint = Color.Black)
            }
        },
        containerColor = Color(0xFF00281F)
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFF00281F))
        ) {
            SegmentedSelectorReservas(
                options = listOf("Próximas", "Pasadas"),
                selectedOption = selectedTab,
                onOptionSelected = { selectedTab = it }
            )

            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF00FF77))
                }
            } else {
                val listaMostrar = if (selectedTab == "Próximas") proximas else pasadas

                if (listaMostrar.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No hay reservas ${selectedTab.lowercase()}",
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(listaMostrar) { reserva ->
                            ReservaCard(reserva)
                        }
                    }
                }
            }
        }
    }

    if (openSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { openSheet.value = false },
            containerColor = Color(0xFF0D1B12)
        ) {
            BottomSheetNuevaReserva(
                vm = vm,
                creando = true,
                onClose = { openSheet.value = false }
            )
        }
    }
}

/* ------------------------------ CARD RESERVA ------------------------------ */

@Composable
private fun ReservaCard(reserva: Reserva) {
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val fechaTexto = reserva.fecha?.toDate()?.let { sdf.format(it) } ?: "Sin fecha"

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B12)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                "Reserva en curso",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text("📅 Fecha: $fechaTexto", color = Color(0xFF6BF47F))
            Text("⏰ Hora: ${reserva.hora}", color = Color.White.copy(alpha = 0.8f))
            Text("🏌️‍♂️ Hoyos: ${reserva.hoyos}", color = Color.White.copy(alpha = 0.8f))
            Text("👥 Jugadores: ${reserva.numJugadores}", color = Color.White.copy(alpha = 0.8f))
        }
    }
}

/* ------------------------------ BottomSheet ------------------------------ */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetNuevaReserva(
    vm: ReservasViewModel,
    creando: Boolean,
    onClose: () -> Unit
) {
    val ctx = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var hoyos by remember { mutableStateOf(9) }
    var numJugadores by remember { mutableStateOf(1) }

    var errorFecha by remember { mutableStateOf(false) }
    var errorHora by remember { mutableStateOf(false) }

    val cal = Calendar.getInstance()

    val datePicker = DatePickerDialog(
        ctx,
        { _, y, m, d ->
            val mm = (m + 1).toString().padStart(2, '0')
            val dd = d.toString().padStart(2, '0')
            fecha = "$y-$mm-$dd"
            errorFecha = false
        },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    )

    val timePicker = TimePickerDialog(
        ctx,
        { _, hh, mm ->
            hora = "${hh.toString().padStart(2, '0')}:${mm.toString().padStart(2, '0')}"
            errorHora = false
        },
        9, 0, true
    )

    Column(
        Modifier
            .fillMaxWidth()
            .background(Color(0xFF0D1B12))
            .padding(16.dp)
    ) {
        Text(
            text = if (creando) "Nueva reserva" else "Editar reserva",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = fecha,
            onValueChange = {},
            readOnly = true,
            label = { Text("Fecha", color = if (errorFecha) Color.Red else Color.White) },
            trailingIcon = { TextButton(onClick = { datePicker.show() }) { Text("Elegir") } },
            colors = reservasTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = hora,
            onValueChange = {},
            readOnly = true,
            label = { Text("Hora", color = if (errorHora) Color.Red else Color.White) },
            trailingIcon = { TextButton(onClick = { timePicker.show() }) { Text("Elegir") } },
            colors = reservasTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Text("Hoyos", color = Color.White, fontWeight = FontWeight.SemiBold)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SegmentOpcion("9", hoyos == 9) { hoyos = 9 }
            SegmentOpcion("18", hoyos == 18) { hoyos = 18 }
        }

        Spacer(Modifier.height(12.dp))

        Text("Número de jugadores", color = Color.White, fontWeight = FontWeight.SemiBold)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            (1..4).forEach { n ->
                SegmentOpcion(n.toString(), numJugadores == n) { numJugadores = n }
            }
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                errorFecha = fecha.isBlank()
                errorHora = hora.isBlank()

                if (errorFecha || errorHora) {
                    Toast.makeText(ctx, "Completa todos los campos.", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val reserva = Reserva(
                    id_jugador = userId,
                    fecha = Timestamp.now(),
                    hora = hora,
                    hoyos = hoyos,
                    numJugadores = numJugadores
                )

                vm.crearReservaConInvitados(
                    base = reserva,
                    invitadosIds = emptyList(),
                    onSuccess = {
                        Toast.makeText(ctx, "Reserva creada correctamente", Toast.LENGTH_SHORT)
                            .show()
                        onClose()
                    },
                    onError = { msg -> Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show() }
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF77)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("Guardar", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

/* ------------------------------ Componentes auxiliares ------------------------------ */




/* --- Selector superior de Próximas / Pasadas --- */
@Composable
fun SegmentedSelectorReservas(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
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
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = bg,
                    contentColor = fg
                ),
                // ✅ También BorderStroke aquí
                border = if (selected)
                    BorderStroke(1.dp, Color(0xFF00FF77))
                else
                    BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                modifier = Modifier.height(44.dp)
            ) {
                Text(option, fontWeight = FontWeight.Bold)
            }
        }
    }
}


/* --- Colores de los campos (Material 3 actual) --- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun reservasTextFieldColors(): TextFieldColors = TextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedLabelColor = Color(0xFF00FF77),
    unfocusedLabelColor = Color.White,
    focusedIndicatorColor = Color(0xFF00FF77),
    unfocusedIndicatorColor = Color.White.copy(alpha = 0.5f),
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    cursorColor = Color(0xFF00FF77)
)
