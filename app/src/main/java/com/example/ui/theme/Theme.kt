package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightKidsColorScheme = lightColorScheme(
    primary = SkyBlue,
    onPrimary = Color.White,
    primaryContainer = SkyBlue.copy(alpha = 0.2f),
    onPrimaryContainer = OceanBlue,
    secondary = SunnyYellow,
    onSecondary = DarkText,
    secondaryContainer = SunnyYellow.copy(alpha = 0.3f),
    tertiary = BubblegumPink,
    onTertiary = Color.White,
    background = WarmCream,
    surface = Color.White,
    surfaceVariant = SoftCloud,
    onBackground = DarkText,
    onSurface = DarkText,
    onSurfaceVariant = MediumText,
    error = CoralRed
)

private val DarkKidsColorScheme = darkColorScheme(
    primary = SkyBlue,
    onPrimary = Color.White,
    primaryContainer = OceanBlue,
    onPrimaryContainer = Color.White,
    secondary = SunnyYellow,
    onSecondary = DarkText,
    secondaryContainer = SunshineDark.copy(alpha = 0.4f),
    tertiary = BubblegumPink,
    onTertiary = Color.White,
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    surfaceVariant = Color(0xFF334155),
    onBackground = LightText,
    onSurface = LightText,
    onSurfaceVariant = Color(0xFFCBD5E1),
    error = CoralRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkKidsColorScheme else LightKidsColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
