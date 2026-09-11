package com.sih26003.aasriti.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens

/**
 * Standard visual and operational states for the AASRITI voice controller.
 * Defined in /docs/ui/UI_COMPONENT_RULES.md (Section 3).
 */
enum class AasritiVoicePillState {
    IDLE,
    PLAYING,
    LISTENING,
    PAUSED,
    ERROR
}

/**
 * Canonical Voice Controller Component for AASRITI.
 *
 * Requirements from UI_COMPONENT_RULES.md & UI_ACCESSIBILITY_RULES.md:
 * - Height: Exactly 56dp, horizontal padding >= 20dp, fully rounded pill shape (28dp radius).
 * - High contrast: MugaGold (#C88D34) with DeepCharcoal typography for WCAG AAA compliance.
 * - Playing state: Gentle animated pulse (opacity oscillates 100% <-> 75%).
 * - Listening state: DeepNortheastForest (#245C45) border highlight with active microphone.
 * - Error state: Subtle WarmAmberWarning (#B45309) border with retry guidance.
 * - Always renders text + icon together (never an unlabelled raw 24dp icon).
 */
@Composable
fun AasritiVoicePill(
    state: AasritiVoicePillState = AasritiVoicePillState.IDLE,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    customText: String? = null,
    enabled: Boolean = true
) {
    // Gentle breathing pulse animation for active audio playback
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "playback_pulse_alpha"
    )

    val currentAlpha = if (state == AasritiVoicePillState.PLAYING) pulseAlpha else 1.0f

    val backgroundColor = when (state) {
        AasritiVoicePillState.IDLE, AasritiVoicePillState.PLAYING -> AasritiColorTokens.MugaGold
        AasritiVoicePillState.LISTENING -> AasritiColorTokens.SoftCream
        AasritiVoicePillState.PAUSED -> AasritiColorTokens.SoftCream
        AasritiVoicePillState.ERROR -> AasritiColorTokens.SoftCream
    }

    val contentColor = when (state) {
        AasritiVoicePillState.IDLE, AasritiVoicePillState.PLAYING -> AasritiColorTokens.DeepCharcoal
        AasritiVoicePillState.LISTENING -> AasritiColorTokens.DeepNortheastForest
        AasritiVoicePillState.PAUSED -> AasritiColorTokens.DeepCharcoal
        AasritiVoicePillState.ERROR -> AasritiColorTokens.WarmAmberWarning
    }

    val borderStroke = when (state) {
        AasritiVoicePillState.IDLE, AasritiVoicePillState.PLAYING -> null
        AasritiVoicePillState.LISTENING -> BorderStroke(2.5.dp, AasritiColorTokens.DeepNortheastForest)
        AasritiVoicePillState.PAUSED -> BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder)
        AasritiVoicePillState.ERROR -> BorderStroke(2.dp, AasritiColorTokens.WarmAmberWarning)
    }

    val (defaultIcon, defaultLabel) = when (state) {
        AasritiVoicePillState.IDLE -> Pair("🔊", "শুনক (Listen)")
        AasritiVoicePillState.PLAYING -> Pair("🔊", "বজাই থকা হৈছে... (Playing...)")
        AasritiVoicePillState.LISTENING -> Pair("🎙️", "শুনি আছোঁ... (Listening...)")
        AasritiVoicePillState.PAUSED -> Pair("▶", "পুনৰ শুনক (Resume)")
        AasritiVoicePillState.ERROR -> Pair("⚠️", "পুনৰ চেষ্টা কৰক (Tap to retry)")
    }

    val displayText = customText ?: defaultLabel

    Box(
        modifier = modifier
            .height(56.dp)
            .alpha(currentAlpha)
            .then(
                if (borderStroke != null) {
                    Modifier.border(borderStroke, RoundedCornerShape(28.dp))
                } else Modifier
            )
            .background(backgroundColor, RoundedCornerShape(28.dp))
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            val alreadyHasIcon = displayText.startsWith("🔊") || displayText.startsWith("🎤")
            if (!alreadyHasIcon) {
                Text(
                    text = defaultIcon,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = displayText,
                color = contentColor,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
