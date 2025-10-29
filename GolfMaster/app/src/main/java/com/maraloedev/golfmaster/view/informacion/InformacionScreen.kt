package com.maraloedev.golfmaster.view.informacion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformacionScreen(vm: InformacionViewModel = viewModel()) {
    val ui by vm.ui.collectAsState()
    val scroll = rememberScrollState()

    Scaffold(
        containerColor = Color(0xFF0B3D2E),
        topBar = {
            TopAppBar(
                title = { Text("Información", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B3D2E))
            )
        }
    ) { pv ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(pv)
                .background(Color(0xFF0B3D2E))
        ) {
            when {
                ui.loading -> {
                    CircularProgressIndicator(
                        color = Color(0xFF00FF77),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                ui.error != null -> {
                    Column(
                        Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(ui.error ?: "Error desconocido", color = Color.Red)
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { vm.cargarInformacion() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF77))
                        ) {
                            Text("Reintentar", color = Color(0xFF0B3D2E))
                        }
                    }
                }

                else -> {
                    val info = ui.info
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                            .verticalScroll(scroll)
                    ) {
                        Text(
                            info.nombreClub,
                            color = Color(0xFF00FF77),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            info.descripcion,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.height(20.dp))

                        Text("📍 Dirección", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(info.direccion.ifBlank { "No especificada" }, color = Color.White.copy(alpha = 0.9f))
                        Spacer(Modifier.height(12.dp))

                        ContactoInfo(icono = Icons.Default.Phone, texto = info.telefono.ifBlank { "No disponible" })
                        ContactoInfo(icono = Icons.Default.Email, texto = info.email.ifBlank { "No disponible" })
                        ContactoInfo(icono = Icons.Default.Language, texto = info.web.ifBlank { "No disponible" })

                        Spacer(Modifier.height(20.dp))
                        Divider(color = Color(0xFF1F4D3E))
                        Spacer(Modifier.height(16.dp))

                        Text("Política de privacidad", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(info.politicaPrivacidad, color = Color.White.copy(alpha = 0.9f))
                        Spacer(Modifier.height(20.dp))
                        Divider(color = Color(0xFF1F4D3E))
                        Spacer(Modifier.height(20.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF00FF77))
                                Spacer(Modifier.width(6.dp))
                                Text("Versión de la app", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Text(info.versionApp, color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactoInfo(icono: androidx.compose.ui.graphics.vector.ImageVector, texto: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, tint = Color(0xFF00FF77))
        Spacer(Modifier.width(8.dp))
        Text(texto, color = Color.White)
    }
}
