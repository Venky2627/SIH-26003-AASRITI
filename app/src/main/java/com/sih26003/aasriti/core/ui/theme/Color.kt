package com.sih26003.aasriti.core.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * AASRITI Canonical Semantic Color System.
 * Defined in /UI_RULES.md.
 * DO NOT add random hex colors to individual screens.
 */
object AasritiColorTokens {
    // Surface & Background Tones (Authentic Prototype Parchment Gradient)
    val ParchmentSurface = Color(0xFFFAF4ED)      // Top of parchment gradient (#FAF4ED)
    val ParchmentBase = Color(0xFFF6EDE0)         // Mid parchment (#F6EDE0)
    val ParchmentDeep = Color(0xFFEDDCC5)         // Deep parchment base (#EDDCC5)
    val ParchmentBorder = Color(0xFFD4C3AC)       // Canonical border (#D4C3AC)
    val CardSurface = Color(0xFFFFFDF9)           // Elevated clean card surface (#FFFDF9)

    // Legacy surface names for backwards compatibility
    val WarmIvory = Color(0xFFFAF4ED)
    val SoftCream = Color(0xFFF6EDE0)
    val WarmSunkenSurface = Color(0xFFEDE0D0)
    val WarmStoneBorder = Color(0xFFD4C3AC)

    // Typography & Ink Tones
    val DeepCharcoal = Color(0xFF2A1D15)          // Primary typography / Dark Ink (#2A1D15)
    val TextDark = Color(0xFF231B15)              // Contrast text (#231B15)
    val WarmSlate = Color(0xFF574144)             // Secondary text / subtitles (#574144)
    val TextMuted = Color(0xFF6B584D)             // Warm muted (#6B584D)

    // Primary & Brand Action Hues (From Prototype: Signature Wine / Deep Crimson)
    val CrimsonDeep = Color(0xFF720227)           // CANONICAL PRIMARY CTA (#720227)
    val CrimsonHover = Color(0xFF58001C)          // Active / hover state (#58001C)
    val CrimsonBorder = Color(0x66A9324C)         // Button border stroke rgba(169, 50, 76, 0.4)

    // Cultural & Heritage Accents
    val SoftClay = Color(0xFFCB8067)              // Terracotta accent (#CB8067)
    val TerracottaHover = Color(0xFFB86E56)       // Terracotta hover (#B86E56)
    val MugaGold = Color(0xFFCE9042)              // Muga Golden Silk accent (#CE9042)
    val DeepNortheastForest = Color(0xFF274133)   // Deep forest green accent (#274133)
    val SupportingSage = Color(0xFFA8B9A0)        // Calm sage accent (#A8B9A0)
    val PeacefulGreen = Color(0xFF5E8354)         // "Saved Locally" dot (#5E8354)
    val MutedHeritageTerracotta = Color(0xFF720227) // Re-map to CrimsonDeep for cohesive branding

    // Triage & Emergency Alerts
    val DeepCranberryEmergency = Color(0xFF8B183F) // EMERGENCY / SOS ONLY (Never for regular buttons)
    val WarmAmberWarning = Color(0xFFB45309)       // Warning, caution, pending sync
}
