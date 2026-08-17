package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ParchmentGold,
    onPrimary = IndigoNavyDark,
    primaryContainer = GoldenAmberLight,
    onPrimaryContainer = AncientGoldDark,
    secondary = LapisCyan,
    onSecondary = IndigoNavyDark,
    secondaryContainer = CyanContainer,
    onSecondaryContainer = CyanGlow,
    tertiary = FallbackAmethyst,
    onTertiary = IndigoNavyDark,
    background = IndigoNavyDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceCardBorder,
    error = ScribalErrorRed,
    errorContainer = ErrorContainer
)

@Composable
fun MyApplicationTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
