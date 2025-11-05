package com.maraloedev.golfmaster.view.menuHamburguesa.informacion.detalles

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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

data class CampoGolf(
    val nombre: String = "",
    val ubicacion: String = "",
    val telefono: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCamposScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var campos by remember { mutableStateOf<List<CampoGolf>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    // 🔹 Cargar datos desde Firestore
    LaunchedEffect(Unit) {
        db.collection("campos_golf")
            .get()
            .addOnSuccessListener { result ->
                campos = result.documents.mapNotNull { it.toObject(CampoGolf::class.java) }
                cargando = false
            }
            .addOnFailureListener {
                cargando = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Correspondencia de Campos", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF003122))
            )
        },
        containerColor = Color(0xFF00281F)
    ) { padding ->

        when {
            cargando -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF00FF77))
                }
            }
            campos.isEmpty() -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No hay correspondencias de campos registradas.",
                        color = Color.LightGray,
                        fontSize = 16.sp
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(campos) { campo ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C3C2C)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(campo.nombre, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text(campo.ubicacion, color = Color.LightGray, fontSize = 14.sp)
                                Spacer(Modifier.height(6.dp))
                                Text("Tel: ${campo.telefono}", color = Color(0xFF00FF77), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}
