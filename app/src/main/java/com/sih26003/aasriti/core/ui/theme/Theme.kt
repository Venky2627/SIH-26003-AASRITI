package com.sih26003.aasriti.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * AASRITI Canonical Color Scheme.
 * Warm, natural, elderly-friendly palette anchored in North Eastern heritage.
 */
private val AasritiLightColorScheme = lightColorScheme(
    primary = AasritiColorTokens.DeepNortheastForest,
    onPrimary = AasritiColorTokens.WarmIvory,
    primaryContainer = AasritiColorTokens.SoftCream,
    onPrimaryContainer = AasritiColorTokens.DeepCharcoal,

    secondary = AasritiColorTokens.MutedHeritageTerracotta,
    onSecondary = AasritiColorTokens.WarmIvory,
    secondaryContainer = AasritiColorTokens.WarmSunkenSurface,
    onSecondaryContainer = AasritiColorTokens.DeepCharcoal,

    tertiary = AasritiColorTokens.MugaGold,
    onTertiary = AasritiColorTokens.DeepCharcoal,

    background = AasritiColorTokens.WarmIvory,
    onBackground = AasritiColorTokens.DeepCharcoal,

    surface = AasritiColorTokens.SoftCream,
    onSurface = AasritiColorTokens.DeepCharcoal,
    surfaceVariant = AasritiColorTokens.WarmSunkenSurface,
    onSurfaceVariant = AasritiColorTokens.WarmSlate,

    outline = AasritiColorTokens.WarmStoneBorder,
    error = AasritiColorTokens.DeepCranberryEmergency,
    onError = AasritiColorTokens.WarmIvory
)

/**
 * AASRITI Master Application Theme.
 * Wraps Jetpack Compose hierarchies with canonical colors, shapes, typography, and spacing.
 */
@Composable
fun AasritiTheme(
    content: @Composable () -> Unit
) {
    val spacing = AasritiSpacing()

    CompositionLocalProvider(
        LocalAasritiSpacing provides spacing
    ) {
        MaterialTheme(
            colorScheme = AasritiLightColorScheme,
            typography = AasritiTypography,
            shapes = AasritiShapes,
            content = content
        )
    }
}
