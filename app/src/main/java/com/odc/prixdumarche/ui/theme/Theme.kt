package com.odc.prixdumarche.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = VertPrimaire,
    onPrimary = Surface,
    background = Background,
    onBackground = OnSurface,
    surface = Surface,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Bordure,
    error = Hausse,
    errorContainer = HausseFond
)

private val DarkColors = darkColorScheme(
    primary = VertClair,
    onPrimary = VertFonce,
    error = Hausse,
    errorContainer = HausseFond
)

@Composable
fun PrixDuMarcheTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}