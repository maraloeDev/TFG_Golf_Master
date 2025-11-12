package com.maraloedev.golfmaster.view.amigos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarAmigoScreen(
    onFinish: () -> Unit,
    vm: AmigosViewModel = viewModel()
) {
    val resultados by vm.resultados.collectAsState()
    val buscando by vm.buscando.collectAsState()
    var searchText by remember { mutableStateOf("") }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir Amigo", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onFinish) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0C1A12))
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = Color(0xFF0C1A12)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF0C1A12))
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp)) // 🔹 Separación entre TopBar y contenido

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Buscar por nombre o licencia", color = Color.Gray) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF4CAF50),
                    focusedContainerColor = Color(0xFF0C1A12),
                    unfocusedContainerColor = Color(0xFF0C1A12),
                    focusedIndicatorColor = Color(0xFF4CAF50),
                    unfocusedIndicatorColor = Color.DarkGray
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { vm.buscarJugador(searchText.trim()) },
                enabled = searchText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Buscar", color = Color.White)
            }

            Spacer(Modifier.height(24.dp)) // 🔹 Más espacio antes de las cards

            if (buscando) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4CAF50))
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(resultados) { (id, nombre) ->
                        // 💬 Tarjeta más grande y visualmente destacada
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp) // 🔹 Más alta
                                .clickable {
                                    vm.enviarSolicitudAmistad(id, nombre) { msg ->
                                        scope.launch { snackbar.showSnackbar(msg) }
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1B372B)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = nombre,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
