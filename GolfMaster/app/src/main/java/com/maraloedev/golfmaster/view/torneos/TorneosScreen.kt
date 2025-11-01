package com.maraloedev.golfmaster.view.torneos

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import com.maraloedev.golfmaster.viewmodel.EventosViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TorneosScreen(
    vm: EventosViewModel = viewModel(),
    onFinish: (nuevoTorneo: com.maraloedev.golfmaster.model.Torneos) -> Unit
) {
    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var tipo by remember { mutableStateOf(TextFieldValue("")) }
    var premio by remember { mutableStateOf(TextFieldValue("")) }
    var lugar by remember { mutableStateOf(TextFieldValue("")) }
    var formato by remember { mutableStateOf(TextFieldValue("")) }

    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    Scaffold(
        containerColor = Color(0xFF0D1B12),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (
                        nombre.text.isNotBlank() &&
                        tipo.text.isNotBlank() &&
                        premio.text.isNotBlank() &&
                        lugar.text.isNotBlank() &&
                        formato.text.isNotBlank()
                    ) {
                        val torneo = com.maraloedev.golfmaster.model.Torneos(
                            nombre_torneo = nombre.text.trim(),
                            tipo_torneo = tipo.text.trim(),
                            premio_torneo = premio.text.trim(),
                            lugar_torneo = lugar.text.trim(),
                            formato_torneo = formato.text.trim(),
                            fecha_inicial_torneo = Timestamp(Date()),
                            fecha_final_torneo = Timestamp(Date())
                        )
                        vm.crearTorneo(
                            nombre = torneo.nombre_torneo,
                            tipo = torneo.tipo_torneo,
                            premio = torneo.premio_torneo,
                            fechaInicio = torneo.fecha_inicial_torneo!!,
                            fechaFinal = torneo.fecha_final_torneo!!,
                            lugar = torneo.lugar_torneo,
                            formato = torneo.formato_torneo,
                            imagenUrl = torneo.imagen_url
                        )
                        onFinish(torneo)
                    }
                },
                containerColor = Color(0xFF00C853)
            ) {
                Icon(Icons.Default.Check, contentDescription = "Crear", tint = Color.White)
            }
        }
    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Crear nuevo evento",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall
            )

            // --- Campos con estilo unificado ---
            @Composable
            fun campo(label: String, value: TextFieldValue, onChange: (TextFieldValue) -> Unit) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onChange,
                    label = { Text(label, color = Color(0xFF6BF47F)) },
                    singleLine = true,
                    maxLines = 1,
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF6BF47F),
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color(0xFF6BF47F),
                        unfocusedLabelColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            campo("Nombre del evento", nombre) { nombre = it }
            campo("Tipo de evento", tipo) { tipo = it }
            campo("Premio", premio) { premio = it }
            campo("Lugar", lugar) { lugar = it }
            campo("Formato", formato) { formato = it }

            if (loading)
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF6BF47F)
                )

            if (error != null)
                Text("⚠️ ${error ?: ""}", color = Color.Red)
        }
    }
}
