package com.maraloedev.golfmaster.view.torneos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maraloedev.golfmaster.model.Torneos
import java.text.SimpleDateFormat
import java.util.*

/**
 * Tarjeta reutilizable para mostrar la información de un torneo.
 * Estilo GolfMaster: fondo verde oscuro, texto blanco y animación suave.
 */
@Composable
fun TorneoCard(
    torneo: Torneos,
    onClick: () -> Unit
) {
    val formatoFecha = remember { SimpleDateFormat("dd MMM yyyy", Locale("es", "ES")) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B12)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {


            // 🔹 Contenido textual
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = torneo.nombre_torneo.ifBlank { "Torneo sin nombre" },
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = torneo.lugar_torneo.ifBlank { "Lugar no especificado" },
                    color = Color(0xFFB0B0B0),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(6.dp))

                val fechaInicio = torneo.fecha_inicial_torneo?.toDate()?.let { formatoFecha.format(it) }
                val fechaFin = torneo.fecha_final_torneo?.toDate()?.let { formatoFecha.format(it) }

                if (fechaInicio != null && fechaFin != null) {
                    Text(
                        text = "📅 $fechaInicio → $fechaFin",
                        color = Color(0xFF6BF47F),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (!torneo.premio_torneo.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "🏆 ${torneo.premio_torneo}",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
