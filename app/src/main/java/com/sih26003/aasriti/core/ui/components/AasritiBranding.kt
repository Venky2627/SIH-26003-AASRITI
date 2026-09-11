package com.sih26003.aasriti.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.sih26003.aasriti.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.animation.core.*
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
 * Authentic AASRITI Master Logo Image.
 * Directly renders the canonical brand terracotta spiral and golden sun emblem.
 */
@Composable
fun AasritiLogoImage(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp
) {
    Image(
        painter = painterResource(id = R.drawable.ic_aasriti_logo),
        contentDescription = "আশ্ৰীতি (AASRITI) Logo",
        modifier = modifier.size(size),
        contentScale = ContentScale.Fit
    )
}

/**
 * Traditional Phulam Gamusa Geometric Woven Accent Band.
 * Faithfully replicates the authentic Assamese Phulam Gamusa motif from the HTML prototype:
 * Repeating diamond weaves in SoftClay (#CB8067) with Crimson (#720227) inner diamonds
 * and Muga Gold (#CE9042) central accents.
 */
@Composable
fun PhulamGamusaWovenBand(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(20.dp),
    primaryCrimson: Color = AasritiColorTokens.CrimsonDeep,
    accentClay: Color = AasritiColorTokens.SoftClay,
    goldAccent: Color = AasritiColorTokens.MugaGold
) {
    Canvas(modifier = modifier) {
        val patternUnitWidth = 30.dp.toPx()
        val h = size.height
        val repeatCount = (size.width / patternUnitWidth).toInt() + 2

        for (i in -1..repeatCount) {
            val offsetX = i * patternUnitWidth
            val cy = h / 2f

            // 1. Outer Diamond Outline
            val outerPath = Path().apply {
                moveTo(offsetX + patternUnitWidth * 0.5f, 1.dp.toPx())
                lineTo(offsetX + patternUnitWidth * 0.9f, cy)
                lineTo(offsetX + patternUnitWidth * 0.5f, h - 1.dp.toPx())
                lineTo(offsetX + patternUnitWidth * 0.1f, cy)
                close()
            }
            drawPath(
                path = outerPath,
                color = accentClay,
                style = Stroke(width = 1.3.dp.toPx())
            )

            // 2. Inner Filled Diamond
            val innerPath = Path().apply {
                moveTo(offsetX + patternUnitWidth * 0.5f, 4.dp.toPx())
                lineTo(offsetX + patternUnitWidth * 0.73f, cy)
                lineTo(offsetX + patternUnitWidth * 0.5f, h - 4.dp.toPx())
                lineTo(offsetX + patternUnitWidth * 0.27f, cy)
                close()
            }
            drawPath(
                path = innerPath,
                color = primaryCrimson.copy(alpha = 0.88f)
            )

            // 3. Central Gold Core Circle
            drawCircle(
                color = goldAccent,
                radius = 2.dp.toPx(),
                center = Offset(offsetX + patternUnitWidth * 0.5f, cy)
            )

            // 4. Gold Connecting Ticks
            drawLine(
                color = goldAccent,
                start = Offset(offsetX + patternUnitWidth * 0.5f, 1.dp.toPx()),
                end = Offset(offsetX + patternUnitWidth * 0.5f, 4.dp.toPx()),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = goldAccent,
                start = Offset(offsetX + patternUnitWidth * 0.5f, h - 4.dp.toPx()),
                end = Offset(offsetX + patternUnitWidth * 0.5f, h - 1.dp.toPx()),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = goldAccent,
                start = Offset(offsetX + patternUnitWidth * 0.1f, cy),
                end = Offset(offsetX + patternUnitWidth * 0.27f, cy),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = goldAccent,
                start = Offset(offsetX + patternUnitWidth * 0.73f, cy),
                end = Offset(offsetX + patternUnitWidth * 0.9f, cy),
                strokeWidth = 1.dp.toPx()
            )

            // 5. Unit Boundary Circles
            drawCircle(
                color = accentClay,
                radius = 1.5.dp.toPx(),
                center = Offset(offsetX, cy)
            )
        }
    }
}

/**
 * Authentic AASRITI Hero Logo Badge with Ambient Breathing Halo.
 * Replicates the circular cream hero badge (w-28 h-28 / 112dp) from screen1.html
 * with a serene breathing gold/clay ambient glow for living dementia-friendly warmth.
 */
@Composable
fun AasritiHeroBadge(
    modifier: Modifier = Modifier,
    size: Dp = 112.dp,
    animated: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "AasritiHeroPulse")
    val pulseScale by if (animated) {
        infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
        )
    } else {
        remember { mutableStateOf(1.0f) }
    }
    val pulseAlpha by if (animated) {
        infiniteTransition.animateFloat(
            initialValue = 0.12f,
            targetValue = 0.32f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseAlpha"
        )
    } else {
        remember { mutableStateOf(0.2f) }
    }

    Box(
        modifier = modifier.size(size + 14.dp),
        contentAlignment = Alignment.Center
    ) {
        // Ambient breathing glow halo
        Box(
            modifier = Modifier
                .size((size.value * pulseScale).dp)
                .clip(CircleShape)
                .background(AasritiColorTokens.MugaGold.copy(alpha = pulseAlpha))
        )

        // Core Badge
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(AasritiColorTokens.ParchmentSurface)
                .border(2.5.dp, AasritiColorTokens.ParchmentBorder, CircleShape)
                .padding(size * 0.12f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_aasriti_logo),
                contentDescription = "AASRITI Terracotta Spiral Emblem",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

/**
 * Reusable AASRITI Brand Emblem Badge with container styling.
 * Uses the authentic brand logo inside a warm heritage circular badge.
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
            .border(1.5.dp, borderColor, CircleShape)
            .padding(size * 0.10f),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_aasriti_logo),
            contentDescription = "AASRITI Logo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

/**
 * Calm Connectivity Pill:
 * Signals 100% offline-first local security without anxiety.
 * Matches prototype: bg-[#FAF4ED]/90 border border-[#D4C3AC] text-[#274133] green dot [#5E8354]
 */
@Composable
fun CalmConnectivityPill(
    modifier: Modifier = Modifier,
    isAssamese: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(AasritiColorTokens.ParchmentSurface.copy(alpha = 0.95f))
            .border(1.dp, AasritiColorTokens.ParchmentBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(AasritiColorTokens.PeacefulGreen)
            )
            Text(
                text = if (isAssamese) "স্থানীয়ভাৱে সংৰক্ষিত" else "Saved Locally",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = AasritiColorTokens.DeepNortheastForest
            )
        }
    }
}

/**
 * Language Selector Pill (ENG / অসমীয়া)
 * Matches prototype: bg-[#EDE0D0] border border-[#D4C3AC] rounded-full
 * Selected: bg-[#720227] text-[#FAF4ED]
 * Unselected: text-[#574144]
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
            .border(1.dp, AasritiColorTokens.ParchmentBorder, RoundedCornerShape(20.dp))
            .padding(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (!isAssamese) AasritiColorTokens.CrimsonDeep else Color.Transparent)
                .clickable { onLanguageSelected("en") }
                .padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
            Text(
                "ENG",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (!isAssamese) AasritiColorTokens.ParchmentSurface else AasritiColorTokens.WarmSlate
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isAssamese) AasritiColorTokens.CrimsonDeep else Color.Transparent)
                .clickable { onLanguageSelected("as") }
                .padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
            Text(
                "অসমীয়া",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAssamese) AasritiColorTokens.ParchmentSurface else AasritiColorTokens.WarmSlate
            )
        }
    }
}
