package com.maraloedev.golfmaster.view.reservas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==================== BotonSegmento ====================
@Composable
fun BotonSegmento(
    texto: String,
    activo: Boolean,
    colorActivo: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(if (activo) colorActivo else Color.Transparent, RoundedCornerShape(50.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            color = if (activo) Color.Black else Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ==================== CampoReserva ====================
@Composable
fun CampoReserva(
    titulo: String,
    valor: String,
    icono: ImageVector? = null,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Text(titulo, color = Color.White, fontWeight = FontWeight.SemiBold)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .background(Color(0xFF111F1A), RoundedCornerShape(10.dp))
                .border(1.dp, Color(0xFF1F2D23), RoundedCornerShape(10.dp))
                .clickable(enabled = onClick != null) { onClick?.invoke() }
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(valor, color = Color.White)
                icono?.let {
                    Icon(it, contentDescription = null, tint = Color(0xFF2BD67B))
                }
            }
        }
    }
}

// ==================== CampoDropdown (CampoDD) ====================
@Composable
fun CampoDropdown(
    titulo: String,
    opciones: List<String>,
    valorSeleccionado: String,
    onSeleccionar: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Text(titulo, color = Color.White, fontWeight = FontWeight.SemiBold)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .background(Color(0xFF111F1A), RoundedCornerShape(10.dp))
                .border(1.dp, Color(0xFF1F2D23), RoundedCornerShape(10.dp))
                .clickable { expanded = true }
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            Text(valorSeleccionado, color = Color.White)
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            onSeleccionar(opcion)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

// ==================== JugadorCard (seleccionable) ====================
@Composable
fun JugadorCard(
    nombre: String,
    detalle: String = "",
    invitado: Boolean = false,
    seleccionado: Boolean = false,
    onClick: () -> Unit = {}
) {
    val verde = Color(0xFF2BD67B)
    val fondo = if (seleccionado) Color(0xFF163021) else Color(0xFF111F1A)
    val textoSecundario = Color(0xFF9CA3AF)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(fondo, RoundedCornerShape(12.dp))
            .padding(14.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color(0xFF1E2E23), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (invitado)
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            else
                Text(
                    text = (nombre.firstOrNull() ?: '?').uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(nombre, color = Color.White, fontWeight = FontWeight.SemiBold)
            if (detalle.isNotEmpty()) {
                Text(detalle, color = textoSecundario, fontSize = 13.sp)
            }
        }

        if (seleccionado) {
            Icon(Icons.Default.Check, contentDescription = null, tint = verde)
        }
    }
}

// ==================== JugadorCardAdd (tarjeta para añadir N amigos) ====================
@Composable
fun JugadorCardAdd(
    texto: String = "Añadir jugador",
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF111F1A), RoundedCornerShape(12.dp))
            .padding(14.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color(0xFF1E2E23), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(texto, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}
