package com.maraloedev.golfmaster.view.amigos

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarAmigoScreen(
    vm: AmigosViewModel = viewModel(),
    onFinish: (() -> Unit)? = null
) {
    var nombre by remember { mutableStateOf("") }
    var licencia by remember { mutableStateOf("") }

    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val mensaje by vm.mensaje.collectAsState()

    Scaffold(
        containerColor = Color(0xFF0D1B12),
        topBar = {
            TopAppBar(
                title = { Text("Agregar amigo", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D1B12))
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del jugador") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF6BF47F), unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFF6BF47F), unfocusedLabelColor = Color.Gray
                )
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = licencia,
                onValueChange = { licencia = it },
                label = { Text("Número de licencia") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF6BF47F), unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFF6BF47F), unfocusedLabelColor = Color.Gray
                )
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    vm.agregarAmigo(nombre, licencia)
                    if (error == null) onFinish?.invoke()
                },
                enabled = nombre.isNotBlank() && licencia.isNotBlank() && !loading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (loading)
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                else
                    Text("Guardar", color = Color.White)
            }

            Spacer(Modifier.height(12.dp))
            if (error != null) Text("Error: $error", color = Color.Red)
            if (mensaje != null) Text(mensaje!!, color = Color(0xFF6BF47F))
        }
    }
}
