package com.bestmakina.depotakip.presentation.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    background = BackgroundColor,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextColor,
    onSurface = TextColor,
    error = ErrorColor,
    onError = Color.White,
    primaryContainer = LightBlue,
    secondaryContainer = VeryLightBlue,
    tertiary = InfoColor,
    onTertiary = Color.White
)

@Composable
fun DepoTakipTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}