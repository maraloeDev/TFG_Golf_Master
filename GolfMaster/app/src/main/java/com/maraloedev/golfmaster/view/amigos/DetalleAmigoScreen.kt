package com.maraloedev.golfmaster.view.amigos

import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maraloedev.golfmaster.model.Jugadores

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleAmigoScreen(
    navController: NavController,
    jugador: Jugadores,
    vm: AmigosViewModel = viewModel()
) {
    val transition = rememberInfiniteTransition(label = "")
    val glow by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            tween(2000, easing = EaseInOutQuad),
            RepeatMode.Reverse
        ),
        label = ""
    )

    Scaffold(
        containerColor = Color(0xFF0D1B12),
        topBar = {
            TopAppBar(
                title = { Text("Detalles del jugador", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D1B12))
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🟢 Avatar animado
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF16361E))
                    .scale(glow)
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = Color(0xFF6BF47F),
                    modifier = Modifier.size(90.dp)
                )
            }

            Spacer(Modifier.height(24.dp))
            Text(
                "${jugador.nombre_jugador} ${jugador.apellido_jugador}".trim(),
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            if (jugador.numero_licencia_jugador.isNotBlank()) {
                Text(
                    "Licencia nº ${jugador.numero_licencia_jugador}",
                    color = Color(0xFF6BF47F),
                    fontSize = 15.sp
                )
            }

            Spacer(Modifier.height(32.dp))

            // 📋 Datos personales
            DetalleCampo("Correo", jugador.correo_jugador)
            DetalleCampo("Teléfono", jugador.telefono_jugador)
            DetalleCampo("Dirección", jugador.direccion_jugador)
            DetalleCampo("Provincia", jugador.provincia_jugador)
            DetalleCampo("Código postal", jugador.codigo_postal_jugador)
            DetalleCampo("Sexo", jugador.sexo_jugador)
            DetalleCampo(
                "Handicap",
                if (jugador.handicap_jugador != 0.0) jugador.handicap_jugador.toString() else "-"
            )
            DetalleCampo("Socio", if (jugador.socio_jugador) "Sí" else "No")
        }
    }
}

@Composable
fun DetalleCampo(label: String, valor: String) {
    if (valor.isNotBlank()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            Text(
                label,
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light
            )
            Text(
                valor,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Divider(
                color = Color(0x22FFFFFF),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
