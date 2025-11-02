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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarAmigoScreen(
    onFinish: () -> Unit,
    vm: AmigosViewModel = viewModel()
) {
    var searchText by remember { mutableStateOf("") }
    val resultados by vm.resultados.collectAsState()
    val buscando by vm.buscando.collectAsState()

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
        containerColor = Color(0xFF0C1A12)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0C1A12))
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Buscar por nombre de usuario o ID", color = Color.Gray) },
                singleLine = true,
                // ✅ Material3 usa TextFieldDefaults.colors(...)
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
                onClick = { vm.buscarPorNombre(searchText.trim()) },
                enabled = searchText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Buscar", color = Color.White)
            }

            Spacer(Modifier.height(16.dp))

            if (buscando) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4CAF50))
                }
            } else {
                LazyColumn {
                    items(resultados) { (id, nombre) ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF2E7D32), shape = MaterialTheme.shapes.medium)
                                .clickable { vm.addAmigo(id, nombre, onFinish) }
                                .padding(16.dp)
                        ) {
                            Text(nombre, color = Color.White, fontWeight = FontWeight.Medium)
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
