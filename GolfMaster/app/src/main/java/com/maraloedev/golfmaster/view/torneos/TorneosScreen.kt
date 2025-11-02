package com.maraloedev.golfmaster.view.torneos

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
<<<<<<< HEAD
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
=======
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import com.maraloedev.golfmaster.viewmodel.EventosViewModel
>>>>>>> parent of ff2be93 (EventosScreen + Amigos Screen Success)
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TorneosScreen(
<<<<<<< HEAD
    vm: TorneosViewModel = viewModel(),
    onFinish: (com.maraloedev.golfmaster.model.Torneos) -> Unit
) {
    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var tipo by remember { mutableStateOf(TextFieldValue("")) }
    var premio by remember { mutableStateOf(TextFieldValue("")) }
    var lugar by remember { mutableStateOf(TextFieldValue("")) }
    var formato by remember { mutableStateOf(TextFieldValue("")) }
    var inicio: Timestamp? by remember { mutableStateOf(null) }
    var fin: Timestamp? by remember { mutableStateOf(null) }

    var errorNombre by remember { mutableStateOf(false) }
    var errorTipo by remember { mutableStateOf(false) }
    var errorPremio by remember { mutableStateOf(false) }
    var errorLugar by remember { mutableStateOf(false) }
    var errorFormato by remember { mutableStateOf(false) }
    var errorFecha by remember { mutableStateOf(false) }

    val ctx = LocalContext.current
    val df = remember { SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES")) }

    fun pickDate(onPicked: (Timestamp) -> Unit) {
        val cal = Calendar.getInstance()
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

    @Composable
    fun Campo(label: String, value: TextFieldValue, onChange: (TextFieldValue) -> Unit, isError: Boolean) {
        Column {
            OutlinedTextField(
                value = value,
                onValueChange = onChange,
                label = { Text(label, color = if (isError) Color.Red else Color(0xFF6BF47F)) },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                isError = isError,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = if (isError) Color.Red else Color(0xFF6BF47F),
                    unfocusedBorderColor = if (isError) Color.Red else Color.Gray,
                    focusedLabelColor = if (isError) Color.Red else Color(0xFF6BF47F),
                    unfocusedLabelColor = if (isError) Color.Red else Color.Gray
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (isError) Text("Campo obligatorio", color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }
    }

    Scaffold(
        containerColor = Color(0xFF0D1B12),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    errorNombre = nombre.text.isBlank()
                    errorTipo = tipo.text.isBlank()
                    errorPremio = premio.text.isBlank()
                    errorLugar = lugar.text.isBlank()
                    errorFormato = formato.text.isBlank()
                    errorFecha = (inicio == null || fin == null)

                    if (!errorNombre && !errorTipo && !errorPremio && !errorLugar && !errorFormato && !errorFecha) {
                        vm.crearTorneoCompleto(
                            nombre = nombre.text,
                            tipo = tipo.text,
                            premio = premio.text,
                            lugar = lugar.text,
                            formato = formato.text,
                            inicio = inicio!!,
                            fin = fin!!
                        ) { creado ->
                            onFinish(creado)
                        }
                    }
                },
                containerColor = Color(0xFF00C853)
            ) {
                Icon(Icons.Default.Check, contentDescription = "Guardar", tint = Color.White)
            }
        }
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Crear torneo", color = Color.White, style = MaterialTheme.typography.titleLarge)
            Campo("Nombre", nombre, { nombre = it }, errorNombre)
            Campo("Tipo", tipo, { tipo = it }, errorTipo)
            Campo("Premio", premio, { premio = it }, errorPremio)
            Campo("Lugar", lugar, { lugar = it }, errorLugar)
            Campo("Formato", formato, { formato = it }, errorFormato)

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { pickDate { inicio = it } }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))) {
                    Text(inicio?.toDate()?.let(df::format) ?: "Fecha inicio", color = Color.Black)
                }
                Button(onClick = { pickDate { fin = it } }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))) {
                    Text(fin?.toDate()?.let(df::format) ?: "Fecha fin", color = Color.Black)
                }
            }

            if (errorFecha) {
                Text("Selecciona ambas fechas", color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }
=======
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
>>>>>>> parent of ff2be93 (EventosScreen + Amigos Screen Success)
        }
    }
}
