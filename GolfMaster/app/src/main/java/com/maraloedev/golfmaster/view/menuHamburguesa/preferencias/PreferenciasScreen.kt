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

/**
 * Pantalla de configuración de preferencias del jugador.
 *
 * Permite seleccionar:
 *  - Días de juego habituales.
 *  - Intereses relacionados con la actividad del club.
 *
 * La información se persiste en Firestore mediante PreferenciasViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenciasScreen(
    vm: PreferenciasViewModel = viewModel()
) {
    val context = LocalContext.current

    // Estado observado desde Firestore
    val preferencias by vm.preferencias.collectAsState()

    // Estados locales que se inicializan cada vez que cambian las preferencias remotas
    var diasJuego by remember(preferencias) { mutableStateOf(preferencias.dias_juego) }
    var intereses by remember(preferencias) { mutableStateOf(preferencias.intereses) }

    // Catálogos de opciones
    val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
    val interesesList = listOf(
        "Golf",
        "Torneos",
        "Pitch & Putt",
        "Escuela de golf",
        "Infantil",
        "Eventos"
    )

    Scaffold(
        containerColor = Color(0xFF0B3D2E),
        bottomBar = {
            // 🔘 Botón inferior fijo de guardado
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
                                Toast.makeText(
                                    context,
                                    "Preferencias guardadas correctamente ✅",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onError = { msg ->
                                Toast.makeText(
                                    context,
                                    "Error al guardar: $msg",
                                    Toast.LENGTH_LONG
                                ).show()
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

            // Título principal
            Text(
                "Preferencias del jugador",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(24.dp))

            // ============================================================
            // 🏌️‍♂️ PREFERENCIAS DE JUEGO
            // ============================================================
            Text(
                "PREFERENCIAS DE JUEGO",
                color = Color(0xFF00FF77),
                fontWeight = FontWeight.Bold
            )
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
                            onCheckedChange = { checked ->
                                diasJuego = if (checked) {
                                    diasJuego + dia
                                } else {
                                    diasJuego - dia
                                }
                            },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00FF77))
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // ============================================================
            // 🎯 INTERESES
            // ============================================================
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
                            onCheckedChange = { checked ->
                                intereses = if (checked) {
                                    intereses + interes
                                } else {
                                    intereses - interes
                                }
                            },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00FF77))
                        )
                    }
                }
            }

            // Espacio para que el contenido no quede tapado por el botón inferior
            Spacer(Modifier.height(80.dp))
        }
    }
}
