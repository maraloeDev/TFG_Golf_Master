package com.maraloedev.golfmaster.view.menuHamburguesa.preferencias

import android.widget.Toast
import androidx.compose.foundation.background
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

    var diasJuego: List<String> by remember { mutableStateOf(preferencias.dias_juego.toMutableList()) }
    var intereses: List<String> by remember { mutableStateOf(preferencias.intereses.toMutableList()) }

    val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
    val interesesList = listOf("Golf", "Torneos", "Pitch & Putt", "Escuela de golf", "Infantil", "Eventos")

    Scaffold(
        containerColor = Color(0xFF0B3D2E),
        bottomBar = {
            // 🔘 Botón inferior fijo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0B3D2E))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        vm.guardarPreferencias(
                            dias = diasJuego,
                            intereses = intereses,
                            onSuccess = {
                                Toast.makeText(context, "Preferencias guardadas correctamente ✅", Toast.LENGTH_SHORT).show()
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
            }
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .padding(pv)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Preferencias del jugador",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(24.dp))

            // 🏌️‍♂️ PREFERENCIAS DE JUEGO
            Text("PREFERENCIAS DE JUEGO", color = Color(0xFF00FF77), fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF173E34), shape = MaterialTheme.shapes.medium)
                    .padding(12.dp)
            ) {
                diasSemana.forEach { dia ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(dia, color = Color.White, fontSize = 16.sp)
                        Checkbox(
                            checked = dia in diasJuego,
                            onCheckedChange = {
                                diasJuego = if (it) diasJuego + dia else diasJuego - dia
                            },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00FF77))
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // 🎯 INTERESES
            Text("INTERESES", color = Color(0xFF00FF77), fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF173E34), shape = MaterialTheme.shapes.medium)
                    .padding(12.dp)
            ) {
                interesesList.forEach { interes ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(interes, color = Color.White, fontSize = 16.sp)
                        Checkbox(
                            checked = interes in intereses,
                            onCheckedChange = {
                                intereses = if (it) intereses + interes else intereses - interes
                            },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00FF77))
                        )
                    }
                }
            }

            Spacer(Modifier.height(80.dp)) // espacio para que no tape el botón inferior
        }
    }
}
