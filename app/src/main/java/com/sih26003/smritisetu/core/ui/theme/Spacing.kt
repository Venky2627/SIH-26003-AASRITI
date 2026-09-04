package com.sih26003.smritisetu.core.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * AASRITI Canonical Spacing Rhythm (Multiples of 4dp).
 * Defined in /UI_RULES.md.
 */
data class AasritiSpacing(
    val space4: Dp = 4.dp,
    val space8: Dp = 8.dp,
    val space12: Dp = 12.dp,
    val space16: Dp = 16.dp,
    val space20: Dp = 20.dp,
    val space24: Dp = 24.dp,
    val space32: Dp = 32.dp,
    val space40: Dp = 40.dp,
    val space48: Dp = 48.dp,
    val space56: Dp = 56.dp,
    val space64: Dp = 64.dp,

    // Touch Targets
    val minPatientTouchTarget: Dp = 64.dp,
    val preferredPatientTouchTarget: Dp = 72.dp,
    val caregiverTouchTarget: Dp = 48.dp,
    val standardButtonHeight: Dp = 56.dp
)

val LocalAasritiSpacing = staticCompositionLocalOf { AasritiSpacing() }
