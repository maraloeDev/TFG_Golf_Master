package com.maraloedev.golfmaster.view.informacion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.maraloedev.golfmaster.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformacionScreen(navController: NavController) {

    val background = Brush.verticalGradient(listOf(Color(0xFF0B3D2E), Color(0xFF173E34)))

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Información",
                        color = Color.White,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B3D2E))
            )
        },
        containerColor = Color.Transparent
    ) { pv ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .padding(pv)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // 🔹 Sección Reservas
            item {
                SectionTitle("Reservas")
                InfoCard(
                    icon = R.drawable.ic_reservas,
                    title = "Reservas de Equipamiento",
                    description = "Reserva de buggies, palos y carros",
                    onClick = { /* navController.navigate("reservasEquipamiento") */ }
                )
                Spacer(Modifier.height(12.dp))
            }

            // 🔹 Sección Campos
            item {
                SectionTitle("Campos")
                InfoCard(
                    icon = R.drawable.ic_correspondencia,
                    title = "Correspondencia de Campos",
                    description = "Información de contacto y ubicación",
                    onClick = { /* navController.navigate("correspondenciaCampos") */ }
                )
                Spacer(Modifier.height(8.dp))
                InfoCard(
                    icon = R.drawable.ic_reglas,
                    title = "Reglas Locales",
                    description = "Reglas locales de los campos de golf",
                    onClick = { /* navController.navigate("reglasLocales") */ }
                )
                Spacer(Modifier.height(12.dp))
            }

            // 🔹 Sección Torneos
            item {
                SectionTitle("Torneos")
                InfoCard(
                    icon = R.drawable.ic_terminos,
                    title = "Términos y Condiciones",
                    description = "Términos y condiciones de los torneos",
                    onClick = { /* navController.navigate("terminosCondiciones") */ }
                )
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color(0xFFBBA864),
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun InfoCard(
    icon: Int,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF122C22)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                tint = Color(0xFF00FF77),
                modifier = Modifier.size(28.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(description, color = Color.LightGray, fontSize = 13.sp)
            }

            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = "Ver más",
                tint = Color(0xFFBBA864),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
