package com.maraloedev.golfmaster.view.reservas

import android.annotation.SuppressLint
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
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maraloedev.golfmaster.model.Reserva
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservasScreen(
    vm: ReservasViewModel = viewModel()
) {
    var tab by remember { mutableStateOf("Proximas") }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Estado en tiempo real
    val reservas by vm.reservas.collectAsState()

    // Derivados
    val proximas by remember(reservas) {
        derivedStateOf {
            val hoy = LocalDate.now()
            val f = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            reservas.filter {
                runCatching { LocalDate.parse(it.fecha, f) }.getOrNull()?.let { d ->
                    d.isAfter(hoy) || d.isEqual(hoy)
                } ?: false
            }.sortedBy { it.fecha }
        }
    }
    val pasadas by remember(reservas) {
        derivedStateOf {
            val hoy = LocalDate.now()
            val f = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            reservas.filter {
                runCatching { LocalDate.parse(it.fecha, f) }.getOrNull()?.isBefore(hoy) == true
            }.sortedByDescending { it.fecha }
        }
    }

    val background = Brush.verticalGradient(listOf(Color(0xFF0B3D2E), Color(0xFF173E34)))

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            if (tab == "Proximas") {
                FloatingActionButton(
                    onClick = { showSheet = true },
                    containerColor = Color(0xFF00FF77)
                ) { Icon(Icons.Default.Add, contentDescription = "Nueva reserva", tint = Color.Black) }
            }
        }
    ) { pv ->
        Column(
            Modifier
                .fillMaxSize()
                .background(background)
                .padding(pv)
        ) {
            Text(
                "Reservas",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            )

            Row(
                Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TabButton("Próximas", tab == "Proximas") { tab = "Proximas" }
                TabButton("Pasadas", tab == "Pasadas") { tab = "Pasadas" }
            }

            Spacer(Modifier.height(16.dp))

            val lista = if (tab == "Proximas") proximas else pasadas

            if (lista.isEmpty()) {
                Text(
                    "No hay reservas ${if (tab == "Proximas") "próximas" else "pasadas"}.",
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(
                    Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(lista) { r ->
                        ReservaCard(
                            reserva = r,
                            onClick = { vm.mostrarEditor(r) },
                            onEliminar = { vm.eliminarReserva(r.id) }
                        )
                    }
                }
            }
        }
    }

    // BottomSheet: crear/editar
    if (showSheet || vm.uiSheet.visible) {
        ModalBottomSheet(
            onDismissRequest = {
                showSheet = false
                vm.cerrarEditor()
            },
            sheetState = sheetState,
            containerColor = Color(0xFF0F4A3B),
            dragHandle = {
                // Drag handle personalizado (compatible con versiones antiguas)
                Box(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .width(32.dp)
                        .height(4.dp)
                        .background(Color(0xFFBBA864), shape = MaterialTheme.shapes.extraLarge)
                )
            }
        ) {
            BottomSheetNuevaReserva(
                vm = vm,
                // si showSheet==true estamos creando; si vm.uiSheet.editando es true, estamos editando
                creando = showSheet,
                onClose = {
                    showSheet = false
                    vm.cerrarEditor()
                }
            )
        }
    }
}

@Composable
private fun TabButton(titulo: String, activo: Boolean, onClick: () -> Unit) {
    val bg = if (activo) Color(0xFF00FF77) else Color.Transparent
    val fg = if (activo) Color.Black else Color.White
    Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = bg)) {
        Text(titulo, color = fg, fontWeight = FontWeight.Bold)
    }
}

@SuppressLint("NewApi")
@Composable
private fun ReservaCard(
    reserva: Reserva,
    onClick: () -> Unit,
    onEliminar: () -> Unit
) {
    val fechaBonita = runCatching {
        LocalDate.parse(reserva.fecha).format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
    }.getOrDefault(reserva.fecha)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F4A3B)),
        modifier = Modifier.fillMaxWidth().clickableNoIndication { onClick() }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("${reserva.hoyos} Hoyos", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("$fechaBonita - ${reserva.hora}", color = Color(0xFFBBA864), fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onEliminar) { Text("Eliminar", color = Color.Red) }
        }
    }
}

/* Utilidad simple para clicks sin ripple (opcional) */
@Composable
private fun Modifier.clickableNoIndication(onClick: () -> Unit) = this.then(
    Modifier
        .noRippleClickable(onClick)
)

@Composable
fun Modifier.noRippleClickable(onClick: () -> Unit) = composed {
    clickable(
        indication = null,
        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
        onClick = onClick
    )
}
