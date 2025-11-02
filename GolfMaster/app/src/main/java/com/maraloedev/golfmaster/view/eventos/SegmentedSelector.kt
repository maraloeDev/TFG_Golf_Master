package com.maraloedev.golfmaster.view.eventos

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Componente reutilizable de botones tipo "segmento" (Próximas / Pasadas)
 */
@Composable
fun SegmentedSelector(
    selectedIndex: Int,
    options: List<String>,
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            val bgColor by animateColorAsState(
                targetValue = if (selected) Color(0xFF00FF77) else Color.Transparent,
                label = ""
            )
            val textColor by animateColorAsState(
                targetValue = if (selected) Color.Black else Color.White,
                label = ""
            )
            val height by animateDpAsState(
                targetValue = if (selected) 48.dp else 42.dp,
                label = ""
            )

            Box(
                modifier = Modifier
                    .height(height)
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .background(bgColor, CircleShape)
                    .clickable { onSelect(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = textColor,
                    fontSize = 16.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }

    @Composable
    fun SegmentOpcion(text: String, activo: Boolean, onClick: () -> Unit) {
        val bg = if (activo) Color(0xFF00FF77) else Color.Transparent
        val fg = if (activo) Color.Black else Color.White
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(bg)
                .clickable { onClick() }
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(text, color = fg, fontWeight = FontWeight.SemiBold)
        }
    }

}
