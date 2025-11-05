package com.maraloedev.golfmaster.view.menuHamburguesa.preferencias

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenciasScreen(vm: PreferenciasViewModel = viewModel()) {
    val context = LocalContext.current
    val preferencias by vm.preferencias.collectAsState()

    var idioma by remember { mutableStateOf(preferencias.idioma) }
    var diasJuego by remember { mutableStateOf(preferencias.dias_juego.toMutableList()) }
    var intereses by remember { mutableStateOf(preferencias.intereses.toMutableList()) }

    val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
    val interesesList = listOf("Golf", "Torneos", "P&P", "Escuela de golf", "Escuela infantil", "Eventos")

    Scaffold(containerColor = Color(0xFF0B3D2E)) { pv ->
        Column(
            modifier = Modifier
                .padding(pv)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Preferencias", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))

            // 🈸 Idioma
            Text("Idioma de preferencia", color = Color.White, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            IdiomaRadioItem("Español", idioma) { idioma = it }
            IdiomaRadioItem("Inglés", idioma) { idioma = it }

            Spacer(Modifier.height(16.dp))
            Divider(color = Color.Gray.copy(alpha = 0.4f))
            Spacer(Modifier.height(16.dp))

            // 🏌️‍♂️ Preferencias de juego
            Text("PREFERENCIAS DE JUEGO", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            diasSemana.forEach { dia ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(dia, color = Color.White)
                    Checkbox(
                        checked = dia in diasJuego,
                        onCheckedChange = {
                            diasJuego = (if (it) diasJuego + dia else diasJuego - dia) as MutableList<String>
                        },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00FF77))
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    vm.guardarPreferencias(
                        idioma = idioma,
                        dias = diasJuego,
                        intereses = intereses,
                        onSuccess = {
                            Toast.makeText(context, "Preferencias guardadas correctamente", Toast.LENGTH_SHORT).show()
                        },
                        onError = {
                            Toast.makeText(context, "Error al guardar: $it", Toast.LENGTH_LONG).show()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF77))
            ) {
                Text("Guardar", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))
            Divider(color = Color.Gray.copy(alpha = 0.4f))
            Spacer(Modifier.height(16.dp))

            // 🎯 Intereses
            Text("INTERESES", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            interesesList.forEach { interes ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(interes, color = Color.White)
                    Checkbox(
                        checked = interes in intereses,
                        onCheckedChange = {
                            intereses = (if (it) intereses + interes else intereses - interes) as MutableList<String>
                        },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00FF77))
                    )
                }
            }
        }
    }
}

@Composable
fun IdiomaRadioItem(
    text: String,
    selected: String,
    onSelect: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        color = Color(0xFF173E34)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { onSelect(text) },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, color = Color.White)
            RadioButton(
                selected = selected == text,
                onClick = { onSelect(text) },
                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00FF77))
            )
        }
    }
}
