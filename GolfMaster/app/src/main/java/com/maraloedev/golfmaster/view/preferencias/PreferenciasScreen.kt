package com.maraloedev.golfmaster.view.preferencias

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenciasScreen(vm: PreferenciasViewModel = viewModel()) {
    val ui by vm.ui.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    var temaOscuro by remember { mutableStateOf(ui.preferencias.temaOscuro) }
    var notificaciones by remember { mutableStateOf(ui.preferencias.notificaciones) }
    var idioma by remember { mutableStateOf(ui.preferencias.idioma) }
    var mostrarPerfilPublico by remember { mutableStateOf(ui.preferencias.mostrarPerfilPublico) }

    Scaffold(
        containerColor = Color(0xFF0B3D2E),
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("Preferencias", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B3D2E))
            )
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
                .background(Color(0xFF0B3D2E))
                .padding(16.dp)
        ) {
            if (ui.loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF00FF77))
                }
                return@Column
            }

            if (ui.error != null) {
                Text(ui.error ?: "Error desconocido", color = Color.Red)
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { vm.cargarPreferencias() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF77))
                ) {
                    Text("Reintentar", color = Color(0xFF0B3D2E))
                }
                return@Column
            }

            Text("Configuración general", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            PreferenciaSwitch(
                label = "Tema oscuro",
                checked = temaOscuro,
                onChange = { temaOscuro = it }
            )

            PreferenciaSwitch(
                label = "Notificaciones",
                checked = notificaciones,
                onChange = { notificaciones = it }
            )

            PreferenciaSwitch(
                label = "Perfil público",
                checked = mostrarPerfilPublico,
                onChange = { mostrarPerfilPublico = it }
            )

            Spacer(Modifier.height(16.dp))

            Text("Idioma", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            DropdownMenuIdioma(
                idiomaSeleccionado = idioma,
                onSeleccionar = { idioma = it }
            )

            Spacer(Modifier.height(30.dp))
            Button(
                onClick = {
                    val nuevasPrefs = Preferencias(
                        temaOscuro = temaOscuro,
                        notificaciones = notificaciones,
                        idioma = idioma,
                        mostrarPerfilPublico = mostrarPerfilPublico
                    )
                    vm.guardarPreferencias(
                        prefs = nuevasPrefs,
                        onSuccess = { scope.launch { snackbar.showSnackbar("Preferencias guardadas ✅") } },
                        onError = { e -> scope.launch { snackbar.showSnackbar("⚠️ $e") } }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF77)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF0B3D2E))
                Spacer(Modifier.width(8.dp))
                Text("Guardar cambios", color = Color(0xFF0B3D2E), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PreferenciaSwitch(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White, fontSize = 16.sp)
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF00FF77),
                checkedTrackColor = Color(0xFF1F4D3E)
            )
        )
    }
}

@Composable
fun DropdownMenuIdioma(idiomaSeleccionado: String, onSeleccionar: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val idiomas = listOf("Español", "Inglés", "Francés")

    Box {
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F4D3E))
        ) {
            Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF00FF77))
            Spacer(Modifier.width(6.dp))
            Text(idiomaSeleccionado, color = Color.White)
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            idiomas.forEach { idioma ->
                DropdownMenuItem(
                    text = { Text(idioma) },
                    onClick = {
                        expanded = false
                        onSeleccionar(idioma)
                    }
                )
            }
        }
    }
}
