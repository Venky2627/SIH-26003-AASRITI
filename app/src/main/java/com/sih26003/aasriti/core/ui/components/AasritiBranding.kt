package com.sih26003.aasriti.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens
import kotlin.math.cos
import kotlin.math.sin

/**
 * AASRITI Canonical Procedural Emblem.
 * Draws the peaceful spiral coil and botanical seed motif via pure Compose Canvas.
 */
@Composable
fun AasritiEmblem(
    modifier: Modifier = Modifier.size(54.dp),
    primaryColor: Color = AasritiColorTokens.DeepNortheastForest,
    accentColor: Color = AasritiColorTokens.MugaGold
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f

        // 1. Subtle dotted boundary halo
        drawCircle(
            color = accentColor.copy(alpha = 0.35f),
            radius = radius * 0.88f,
            center = center,
            style = Stroke(
                width = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
            )
        )

        // 2. Harmonic spiral coil
        val spiralPath = Path()
        val turns = 2.2f
        val points = 60
        val maxAngle = (turns * 2 * Math.PI).toFloat()

        for (i in 0..points) {
            val progress = i / points.toFloat()
            val angle = progress * maxAngle
            val r = progress * (radius * 0.72f)
            val x = center.x + r * cos(angle)
            val y = center.y + r * sin(angle)

            if (i == 0) {
                spiralPath.moveTo(x, y)
            } else {
                spiralPath.lineTo(x, y)
            }
        }

        drawPath(
            path = spiralPath,
            color = primaryColor,
            style = Stroke(
                width = 2.8.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        // 3. Central Seed Core
        drawCircle(
            color = accentColor,
            radius = radius * 0.12f,
            center = center
        )

        // 4. Botanical Spark Accents
        val sparkRadius = radius * 0.78f
        for (k in 0..3) {
            val sparkAngle = (k * Math.PI / 2.0 + Math.PI / 4.0).toFloat()
            val sx1 = center.x + (sparkRadius - 6.dp.toPx()) * cos(sparkAngle)
            val sy1 = center.y + (sparkRadius - 6.dp.toPx()) * sin(sparkAngle)
            val sx2 = center.x + sparkRadius * cos(sparkAngle)
            val sy2 = center.y + sparkRadius * sin(sparkAngle)

            drawLine(
                color = accentColor,
                start = Offset(sx1, sy1),
                end = Offset(sx2, sy2),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * Reusable AASRITI Brand Emblem Badge with container styling.
 */
@Composable
fun AasritiLogoBadge(
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    containerColor: Color = AasritiColorTokens.SoftCream,
    borderColor: Color = AasritiColorTokens.WarmStoneBorder
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(containerColor)
            .border(1.5.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        AasritiEmblem(modifier = Modifier.size(size * 0.75f))
    }
}

/**
 * Calm Connectivity Pill:
 * Signals 100% offline-first local security without anxiety.
 */
@Composable
fun CalmConnectivityPill(
    modifier: Modifier = Modifier,
    isAssamese: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AasritiColorTokens.SoftCream)
            .border(1.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(AasritiColorTokens.DeepNortheastForest)
            )
            Text(
                text = if (isAssamese) "স্থানীয়ভাৱে সংৰক্ষিত (Saved Locally)" else "Saved Locally",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.DeepNortheastForest
            )
        }
    }
}

/**
 * Language Selector Pill (ENG / অসমীয়া)
 */
@Composable
fun LanguageTogglePill(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isAssamese = currentLanguage == "as"

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(AasritiColorTokens.WarmSunkenSurface)
            .border(1.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(20.dp))
            .padding(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (!isAssamese) AasritiColorTokens.MutedHeritageTerracotta else Color.Transparent)
                .clickable { onLanguageSelected("en") }
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                "ENG",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (!isAssamese) Color.White else AasritiColorTokens.WarmSlate
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isAssamese) AasritiColorTokens.MutedHeritageTerracotta else Color.Transparent)
                .clickable { onLanguageSelected("as") }
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                "অসমীয়া",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAssamese) Color.White else AasritiColorTokens.WarmSlate
            )
        }
    }
}
