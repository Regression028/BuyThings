package com.example.buythings.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
val CoralPink = Color(0xFFF08080)
private val DarkColorScheme = darkColorScheme(
    primary = CoralPink,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onBackground = Color(0xFFF5F5F5),
    onSurface = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF9E9E9E)
)

private val LightColorScheme = lightColorScheme(
    primary = CoralPink,
    background = Color(0xFFF7F7F7),
    surface = Color.White,
    onBackground = Color(0xFF171717),
    onSurface = Color(0xFF171717),
    onSurfaceVariant = Color(0xFF666666)
)
//these are all the colors

@Composable
fun BuyThingsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}