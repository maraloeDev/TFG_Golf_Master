package com.maraloedev.golfmaster.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val DarkColors = darkColorScheme(
    primary = GolfPrimary,
    onPrimary = Color.Black,

    secondary = GolfPrimaryDark,
    onSecondary = Color.Black,

    secondaryContainer = GolfSecondaryContainer,
    onSecondaryContainer = GolfOnSecondaryContainer,

    background = GolfBackgroundDark,
    onBackground = GolfTextPrimary,

    surface = GolfSurfaceDark,
    onSurface = GolfTextPrimary,
    onSurfaceVariant = GolfTextSecondary,

    error = GolfError,
    onError = GolfErrorOn
)

// =============================
//  ESQUEMA DE COLOR CLARO (opcional)
// =============================
// (Se deja igual que dark para mantener identidad visual,
//  ya que tu diseño es totalmente dark-themed)

private val LightColors = DarkColors

// =============================
// THEME PRINCIPAL
// =============================

@Composable
fun GolfMasterTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
