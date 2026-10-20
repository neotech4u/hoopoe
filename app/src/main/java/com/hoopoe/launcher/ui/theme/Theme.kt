package com.hoopoe.launcher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.hoopoe.launcher.data.AppFont

val LocalDayMode = staticCompositionLocalOf { false }

@Composable
fun OpenLauncherTheme(
    accent: Color     = AccentCyan,
    background: Color = Black,
    textColor: Color  = Color.White,
    fontBold: Boolean = false,
    textScale: Float  = 1.0f,
    appFont: AppFont  = AppFont.JETBRAINS_MONO,
    isDayMode: Boolean = false,
    useCustomBg: Boolean = false,
    content: @Composable () -> Unit
) {
    // Contrast-aware: the accent is user-chosen and can be any brightness,
    // so a fixed onPrimary (white) goes invisible on light accents
    val onAccent = if (accent.luminance() > 0.5f) Color.Black else Color.White
    val colorScheme = if (isDayMode) lightColorScheme(
        primary          = accent,
        onPrimary        = onAccent,
        secondary        = accent.copy(alpha = 0.7f),
        onSecondary      = onAccent,
        tertiary         = accent.copy(alpha = 0.5f),
        background       = if (useCustomBg) background else Color(0xFFE2E8F0),
        surface          = Color(0xFFFFFFFF),
        onBackground     = Color(0xFF0F172A),
        onSurface        = Color(0xFF0F172A),
        surfaceVariant   = Color(0xFFF1F5F9),
        onSurfaceVariant = Color(0xFF475569),
        outline          = Color(0xFFCBD5E1)
    ) else darkColorScheme(
        primary          = accent,
        onPrimary        = onAccent,
        secondary        = accent.copy(alpha = 0.7f),
        onSecondary      = onAccent,
        tertiary         = accent.copy(alpha = 0.5f),
        background       = background,
        surface          = CardSurface,
        onBackground     = textColor,
        onSurface        = textColor,
        surfaceVariant   = DimSurface,
        onSurfaceVariant = TextMuted,
        outline          = BorderAcrylic
    )
    CompositionLocalProvider(LocalDayMode provides isDayMode) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = launcherTypography(fontBold, textScale, appFont.toFontFamily()),
            content     = content
        )
    }
}
