package com.maraloedev.golfmaster.view.torneos

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import com.maraloedev.golfmaster.viewmodel.EventosViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TorneosScreen(
    vm: EventosViewModel = viewModel(),
    onFinish: (() -> Unit)? = null
) {
    var nombre by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var premio by remember { mutableStateOf("") }
    var fechaInicio by remember { mutableStateOf<Timestamp?>(null) }
    var fechaFinal by remember { mutableStateOf<Timestamp?>(null) }

    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    // Función para mostrar un selector de fecha
    fun pickDate(onPicked: (Timestamp) -> Unit) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, day)
                onPicked(Timestamp(cal.time))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Crear Torneo", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del Torneo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = tipo,
            onValueChange = { tipo = it },
            label = { Text("Tipo de Torneo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = premio,
            onValueChange = { premio = it },
            label = { Text("Premio del Torneo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { pickDate { fechaInicio = it } }) {
                Text(fechaInicio?.toDate()?.let(dateFormat::format) ?: "Fecha Inicio")
            }
            Button(onClick = { pickDate { fechaFinal = it } }) {
                Text(fechaFinal?.toDate()?.let(dateFormat::format) ?: "Fecha Final")
            }
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                if (fechaInicio != null && fechaFinal != null) {
                    vm.crearTorneo(
                        nombre = nombre,
                        tipo = tipo,
                        premio = premio,
                        fechaInicio = fechaInicio!!,
                        fechaFinal = fechaFinal!!
                    )
                    onFinish?.invoke()
                }
            },
            enabled = nombre.isNotBlank() && fechaInicio != null && fechaFinal != null
        ) {
            Text("Guardar Torneo")
        }
    }
}
