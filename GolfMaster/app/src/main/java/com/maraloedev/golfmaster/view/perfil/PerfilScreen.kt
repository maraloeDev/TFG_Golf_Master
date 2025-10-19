package com.maraloedev.golfmaster.view.perfil

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maraloedev.golfmaster.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(vm: PerfilViewModel = viewModel()) {
    val jugador by vm.jugador.collectAsState()
    val context = LocalContext.current
    var editMode by remember { mutableStateOf(false) }

    if (jugador == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    // Estados de los campos
    var nombre by remember { mutableStateOf(jugador?.nombre_jugador ?: "") }
    var apellido by remember { mutableStateOf(jugador?.apellido_jugador ?: "") }
    var correo by remember { mutableStateOf(jugador?.correo_jugador ?: "") }
    var telefono by remember { mutableStateOf(jugador?.telefono_jugador ?: "") }
    var direccion by remember { mutableStateOf(jugador?.direccion_jugador ?: "") }
    var codigoPostal by remember { mutableStateOf(jugador?.codigo_postal_jugador ?: "") }
    var sexo by remember { mutableStateOf(jugador?.sexo_jugador ?: "Masculino") }
    var handicap by remember { mutableStateOf(jugador?.handicap_jugador?.toString() ?: "") }
    var socio by remember { mutableStateOf(jugador?.socio_jugador ?: false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { editMode = !editMode },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Editar perfil", tint = Color.Black)
            }
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .padding(pv)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            // 👤 Avatar del usuario (sin botón de editar)
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_conacto),
                    contentDescription = "Avatar",
                    tint = Color.Gray,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = "$nombre $apellido",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = correo,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(Modifier.height(24.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(Modifier.height(16.dp))

            PerfilCampoEditable("Teléfono", telefono, editMode) { telefono = it }
            PerfilCampoEditable("Dirección", direccion, editMode) { direccion = it }
            PerfilCampoEditable("Código Postal", codigoPostal, editMode) { codigoPostal = it }
            PerfilCampoEditable("Sexo", sexo, editMode) { sexo = it }
            PerfilCampoEditable("Hándicap", handicap, editMode) { handicap = it }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Checkbox(checked = socio, onCheckedChange = { socio = it }, enabled = editMode)
                Text("¿Socio?", color = MaterialTheme.colorScheme.onBackground)
            }

            Spacer(Modifier.height(24.dp))

            if (editMode) {
                Button(
                    onClick = {
                        val newJugador = jugador!!.copy(
                            telefono_jugador = telefono,
                            direccion_jugador = direccion,
                            codigo_postal_jugador = codigoPostal,
                            sexo_jugador = sexo,
                            socio_jugador = socio,
                            handicap_jugador = handicap.toDoubleOrNull() ?: 0.0
                        )

                        vm.actualizarJugador(
                            jugador = newJugador,
                            onSuccess = {
                                Toast.makeText(context, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                                editMode = false
                            },
                            onError = {
                                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Guardar cambios", color = Color.Black)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilCampoEditable(
    label: String,
    value: String,
    editable: Boolean,
    onChange: (String) -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
        if (editable) {
            OutlinedTextField(
                value = value,
                onValueChange = onChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface
                )
            )
        } else {
            Text(
                value,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}
