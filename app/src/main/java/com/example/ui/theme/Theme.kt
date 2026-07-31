package com.example.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ApexRed,
    onPrimary = Color.White,
    primaryContainer = ApexRedLight,
    onPrimaryContainer = Color.White,
    secondary = ApexCyan,
    onSecondary = ApexDark,
    tertiary = ApexAmber,
    onTertiary = ApexDark,
    background = ApexDark,
    onBackground = ApexTextPrimary,
    surface = ApexDark,
    onSurface = ApexTextPrimary,
    surfaceVariant = ApexCard,
    onSurfaceVariant = ApexTextSecondary,
    outline = ApexCardBorder
)

private val LightColorScheme = darkColorScheme( // Keep high contrast dark motorsport theme
    primary = ApexRed,
    onPrimary = Color.White,
    primaryContainer = ApexRedLight,
    onPrimaryContainer = Color.White,
    secondary = ApexCyan,
    onSecondary = ApexDark,
    tertiary = ApexAmber,
    onTertiary = ApexDark,
    background = ApexDark,
    onBackground = ApexTextPrimary,
    surface = ApexDark,
    onSurface = ApexTextPrimary,
    surfaceVariant = ApexCard,
    onSurfaceVariant = ApexTextSecondary,
    outline = ApexCardBorder
)

@Composable
fun MotoGridTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
