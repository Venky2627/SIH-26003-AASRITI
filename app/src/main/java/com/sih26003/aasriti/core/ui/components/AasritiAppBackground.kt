package com.sih26003.aasriti.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens

/**
 * AASRITI Canonical Global Application Background.
 * 
 * Shared foundational visual atmosphere across all AASRITI screens:
 * - 3-step vertical parchment gradient (#FAF4ED -> #F6EDE0 -> #EDDCC5)
 * - Soft organic ambient radial glows (Soft Clay, Muga Gold, Supporting Sage)
 * - Warm, non-glare, elderly-accessible tactile depth
 */
@Composable
fun AasritiAppBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val backgroundGradient = remember {
        Brush.verticalGradient(
            colors = listOf(
                AasritiColorTokens.ParchmentSurface,
                AasritiColorTokens.ParchmentBase,
                AasritiColorTokens.ParchmentDeep
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        // 1. Subtle top-start Soft Clay ambient glow
        Box(
            modifier = Modifier
                .size(240.dp)
                .offset(x = (-60).dp, y = (-40).dp)
                .clip(CircleShape)
                .background(AasritiColorTokens.SoftClay.copy(alpha = 0.12f))
        )

        // 2. Subtle center-end Muga Gold ambient glow
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 70.dp, y = (-20).dp)
                .clip(CircleShape)
                .background(AasritiColorTokens.MugaGold.copy(alpha = 0.10f))
        )

        // 3. Subtle bottom-start Supporting Sage ambient glow
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-40).dp, y = 50.dp)
                .clip(CircleShape)
                .background(AasritiColorTokens.SupportingSage.copy(alpha = 0.12f))
        )

        // Screen content layer
        content()
    }
}
