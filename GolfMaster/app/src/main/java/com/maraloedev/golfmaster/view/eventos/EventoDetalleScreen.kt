package com.maraloedev.golfmaster.view.eventos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.SportsGolf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.maraloedev.golfmaster.model.Torneos
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventoDetalleScreen(
    torneo: Torneos,
    navController: NavController? = null,
    vm: EventoDetalleViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val inscrito by vm.inscrito.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var modoDirecto by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale("es", "ES")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        torneo.nombre_torneo,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D1B12))
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFF0D1B12)
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color(0xFF0D1B12))
                .padding(16.dp)
        ) {
            Text(
                text = torneo.nombre_torneo,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))
            InfoRow(Icons.Default.Flag, "Tipo", torneo.tipo_torneo)
            InfoRow(Icons.Default.Place, "Lugar", torneo.lugar_torneo)
            InfoRow(Icons.Default.SportsGolf, "Formato", torneo.formato_torneo)

            Spacer(Modifier.height(16.dp))
            val inicio = torneo.fecha_inicial_torneo?.toDate()?.let { dateFormat.format(it) } ?: "Sin fecha"
            val fin = torneo.fecha_final_torneo?.toDate()?.let { dateFormat.format(it) } ?: "Sin fecha"
            Text("📅 Del $inicio al $fin", color = Color(0xFF6BF47F), fontWeight = FontWeight.Medium)

            Spacer(Modifier.height(16.dp))
            Divider(color = Color(0x33FFFFFF))
            Spacer(Modifier.height(16.dp))

            // Toggle entre modos
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Modo directo", color = Color.White)
                Switch(
                    checked = modoDirecto,
                    onCheckedChange = { modoDirecto = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00C853))
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val userId = vm.repo.currentUid ?: ""
                    if (userId.isNotBlank()) {
                        scope.launch {
                            if (modoDirecto) {
                                vm.inscribirse(userId)
                                snackbarHostState.showSnackbar("✅ Inscripción completada directamente")
                            } else {
                                vm.enviarSolicitudInscripcion(torneo.id, userId)
                                snackbarHostState.showSnackbar("📨 Solicitud enviada correctamente")
                            }
                        }
                    }
                },
                enabled = !loading && !inscrito,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (inscrito) Color.Gray else Color(0xFF00C853)
                )
            ) {
                when {
                    loading -> CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                    inscrito -> Text("Completado", color = Color.White, fontWeight = FontWeight.Bold)
                    else -> Text(
                        if (modoDirecto) "Inscribirse" else "Guardar solicitud",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (error != null) {
                Spacer(Modifier.height(12.dp))
                Text("⚠️ $error", color = Color.Red, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(icon, contentDescription = label, tint = Color(0xFF6BF47F))
        Spacer(Modifier.width(10.dp))
        Text("$label: ", color = Color.White, fontWeight = FontWeight.Medium)
        Text(value.ifBlank { "No especificado" }, color = Color.LightGray)
    }
}
