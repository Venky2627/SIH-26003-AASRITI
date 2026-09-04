package com.sih26003.smritisetu.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * AASRITI Canonical Corner Radii.
 * Small: 8dp, Medium: 12dp, Large: 16dp, Very Large: 24dp, Pill: Fully rounded.
 */
val AasritiShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),      // Inputs, compact chips
    medium = RoundedCornerShape(12.dp),    // Secondary cards, summary tiles
    large = RoundedCornerShape(16.dp),     // Patient cards, choice tiles, buttons
    extraLarge = RoundedCornerShape(24.dp) // Hero activity cards, bottom sheets
)

val AasritiPillShape = RoundedCornerShape(999.dp) // Voice control pill, SOS trigger
