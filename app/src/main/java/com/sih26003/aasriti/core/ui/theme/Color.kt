package com.sih26003.aasriti.core.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * AASRITI Canonical Semantic Color System.
 * Defined in /UI_RULES.md.
 * DO NOT add random hex colors to individual screens.
 */
object AasritiColorTokens {
    // Surface & Background Tones
    val WarmIvory = Color(0xFFFDFBF7)           // Primary background surface
    val SoftCream = Color(0xFFF5EFE6)           // Card / elevated surface background
    val WarmSunkenSurface = Color(0xFFEFE7DA)   // Inset / recessed container background

    // Typography & Ink Tones
    val DeepCharcoal = Color(0xFF1C2024)        // Primary typography, headers, icons
    val WarmSlate = Color(0xFF4A525A)           // Secondary text, subtitles, supporting metadata
    val WarmStoneBorder = Color(0xFFD6CBBB)     // Structural borders (1px-2px)

    // Primary & Brand Action Hues
    val DeepNortheastForest = Color(0xFF245C45) // PRIMARY ACTION (Normal, affirmative, save)
    val MutedHeritageTerracotta = Color(0xFF9E2A2B) // BRAND / REMINISCENCE (Memories, family, identity)
    val MugaGold = Color(0xFFC88D34)            // VOICE / ATTENTION (Audio indicators, highlights)

    // Supporting Natural Accents
    val SupportingSage = Color(0xFFA8B9A0)      // Secondary peaceful chips, ambient fills
    val SoftClay = Color(0xFFCB8067)            // Warm tactile secondary accents

    // Triage & Emergency Alerts
    val DeepCranberryEmergency = Color(0xFF8B183F) // EMERGENCY / SOS ONLY (Never for regular buttons)
    val WarmAmberWarning = Color(0xFFB45309)       // Warning, caution, pending sync
}
