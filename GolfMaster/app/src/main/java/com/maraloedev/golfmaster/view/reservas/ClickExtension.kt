package com.maraloedev.golfmaster.view.reservas

import android.annotation.SuppressLint

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

/**
 * Extensión para hacer clics sin animación de ripple.
 * Ideal para filas, tarjetas o elementos personalizados.
 */
@SuppressLint("RememberInComposition")
fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    role: Role? = null,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = MutableInteractionSource()
    clickable(
        interactionSource = interactionSource,
        indication = null, // 🔹 sin efecto visual
        enabled = enabled,
        role = role,
        onClick = onClick
    )
}
