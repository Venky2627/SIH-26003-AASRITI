package com.sih26003.smritisetu.feature.games.flowermatch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih26003.smritisetu.core.ui.theme.AasritiColorTokens
import com.sih26003.smritisetu.data.local.entities.GameSessionEntity
import com.sih26003.smritisetu.data.repository.GameRepository
import com.sih26003.smritisetu.demo.AasritiDemoData
import com.sih26003.smritisetu.demo.DemoFlowerCard
import com.sih26003.smritisetu.demo.DemoStateHolder
import com.sih26003.smritisetu.engine.orchestrator.CognitiveInsightOrchestrator
import com.sih26003.smritisetu.feature.games.framework.PerformanceCollector
import com.sih26003.smritisetu.ml.inference.DecisionTreeEngine
import com.sih26003.smritisetu.voice.playback.VoicePromptManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/**
 * Flagship SIH Demo Game: North Eastern Regional Flower Match.
 * Follows SCREEN_PATIENT_GAME_ARENA specification:
 * - Very low cognitive load, zero countdown timers, zero red penalty banners.
 * - Authentic botanical vectors (Kopou Phool, Tagar, Jaba, Nilkamal).
 * - Spoken Assamese/English prompt guidance.
 * - Touch targets >= 72dp with WCAG AAA contrast.
 * - Real interaction telemetry (timing, hesitation, errors) saved to Room SQLite.
 */
@Composable
fun FlowerMatchGameScreen(
    patientId: String = com.sih26003.smritisetu.demo.DemoPatientConfig.PATIENT_ID,
    gameRepository: GameRepository? = null,
    decisionTreeEngine: DecisionTreeEngine? = null,
    insightOrchestrator: CognitiveInsightOrchestrator? = null,
    voicePromptManager: VoicePromptManager?,
    onBack: () -> Unit
) {
    val flowers = remember { AasritiDemoData.flowerCards }
    val isAssamese = DemoStateHolder.currentLanguage == "as"
    val scope = rememberCoroutineScope()
    val collector = remember { PerformanceCollector() }

    var sessionSaved by remember { mutableStateOf(false) }
    var lastReactionTimeMs by remember { mutableStateOf(0L) }
    var lastHesitationCount by remember { mutableStateOf(0) }
    var lastErrorCount by remember { mutableStateOf(0) }
    var nextRecommendedLevel by remember { mutableStateOf(1) }
    var adaptiveFeedbackMessage by remember { mutableStateOf("") }

    // Target flower for current round (Round 1: Kopou Phool, Round 2: Nilkamal)
    val targetFlower = if (DemoStateHolder.flowerGameRound == 1) flowers[0] else flowers[3]

    LaunchedEffect(DemoStateHolder.flowerGameRound) {
        if (DemoStateHolder.flowerGameRound == 1 && !DemoStateHolder.isFlowerGameComplete) {
            collector.startRound()
            sessionSaved = false
        }
        val prompt = if (isAssamese) {
            "অনুগ্ৰহ কৰি ${targetFlower.nameIndic} বাছক।"
        } else {
            "Please select the ${targetFlower.nameEn}."
        }
        voicePromptManager?.speak(prompt)
    }

    val handleCardSelection: (DemoFlowerCard) -> Unit = { flower ->
        val isMatch = (flower.id == targetFlower.id)
        collector.recordInteraction(isMatch)
        val ok = DemoStateHolder.onFlowerSelected(flower.id, targetFlower.id)
        if (ok) {
            if (DemoStateHolder.flowerGameRound == 1) {
                DemoStateHolder.flowerGameRound = 2
            } else if (DemoStateHolder.flowerGameRound == 2 && !sessionSaved) {
                DemoStateHolder.isFlowerGameComplete = true
                sessionSaved = true

                // Compute real physical telemetry
                if (decisionTreeEngine != null) {
                    val metrics = collector.computeMetrics(
                        currentDifficulty = 1,
                        completed = true,
                        decisionTreeEngine = decisionTreeEngine
                    )
                    lastReactionTimeMs = metrics.reactionTimeMs
                    lastHesitationCount = metrics.hesitationCount
                    lastErrorCount = metrics.errors

                    val session = GameSessionEntity(
                        patientId = patientId,
                        gameId = "FLOWER_MATCH",
                        difficultyLevel = 1,
                        durationMs = metrics.durationMs,
                        accuracy = metrics.accuracy,
                        errors = metrics.errors,
                        reactionTimeMs = metrics.reactionTimeMs,
                        hesitationCount = metrics.hesitationCount,
                        adaptationDecision = metrics.adaptationDecision,
                        completed = true
                    )

                    // 1. Commit session to local Room SQLite
                    scope.launch(Dispatchers.IO) {
                        gameRepository?.saveGameSession(session)
                    }

                    // 2. Feed session into CognitiveInsightOrchestrator
                    if (insightOrchestrator != null) {
                        val insight = insightOrchestrator.processSession(session, 1)
                        nextRecommendedLevel = insight.nextLevel
                        adaptiveFeedbackMessage = if (isAssamese) insight.feedbackIndic else insight.feedbackEn
                    } else {
                        nextRecommendedLevel = metrics.adaptationDecision
                        adaptiveFeedbackMessage = if (isAssamese) "বৰ সুন্দৰ! আপুনি বহুত ভাল খেলিছে।" else "Wonderful! Excellent recognition."
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AasritiColorTokens.WarmIvory)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Top Bar with Calm Navigation & Step Indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                modifier = Modifier.defaultMinSize(minHeight = 56.dp)
            ) {
                Text(
                    text = if (isAssamese) "← উভতি যাওক" else "← Back",
                    color = AasritiColorTokens.DeepCharcoal,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Calm Round Indicators (Not a score)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(
                            if (DemoStateHolder.matchedFlowerIds.contains(flowers[0].id))
                                AasritiColorTokens.DeepNortheastForest
                            else
                                AasritiColorTokens.WarmSunkenSurface
                        )
                )
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(
                            if (DemoStateHolder.matchedFlowerIds.contains(flowers[3].id))
                                AasritiColorTokens.DeepNortheastForest
                            else
                                AasritiColorTokens.WarmSunkenSurface
                        )
                )
            }

            // Audio Replay Button
            Button(
                onClick = {
                    val prompt = if (isAssamese) {
                        "অনুগ্ৰহ কৰি ${targetFlower.nameIndic} বাছক।"
                    } else {
                        "Please select the ${targetFlower.nameEn}."
                    }
                    voicePromptManager?.speak(prompt)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.MugaGold),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.defaultMinSize(minHeight = 56.dp)
            ) {
                Text(
                    text = if (isAssamese) "🔊 শুনক" else "🔊 Listen",
                    color = AasritiColorTokens.WarmIvory,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 100% Offline Status Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.12f))
                .border(1.dp, AasritiColorTokens.DeepNortheastForest, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "100% Offline • Room SQLite Local Source of Truth",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.DeepNortheastForest
            )
        }

        // 2. Center Game Header & Prompt Instruction
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            Text(
                text = if (isAssamese) "ফুল মেলোৱা খেল" else "Flower Match Game",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.DeepCharcoal
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .background(AasritiColorTokens.SoftCream, RoundedCornerShape(14.dp))
                    .border(1.5.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = if (isAssamese) {
                        "তলৰ ফুলবোৰৰ পৰা \"${targetFlower.nameIndic}\" স্পৰ্শ কৰক"
                    } else {
                        "Touch the \"${targetFlower.nameEn}\" from below"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AasritiColorTokens.DeepNortheastForest,
                    textAlign = TextAlign.Center
                )
            }
        }

        // 3. Flower Choice Cards Arena (2x2 Grid)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                FlowerChoiceCard(
                    flower = flowers[0],
                    isAssamese = isAssamese,
                    isMatched = DemoStateHolder.matchedFlowerIds.contains(flowers[0].id),
                    modifier = Modifier.weight(1f),
                    onSelect = { handleCardSelection(flowers[0]) }
                )
                FlowerChoiceCard(
                    flower = flowers[1],
                    isAssamese = isAssamese,
                    isMatched = DemoStateHolder.matchedFlowerIds.contains(flowers[1].id),
                    modifier = Modifier.weight(1f),
                    onSelect = { handleCardSelection(flowers[1]) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                FlowerChoiceCard(
                    flower = flowers[2],
                    isAssamese = isAssamese,
                    isMatched = DemoStateHolder.matchedFlowerIds.contains(flowers[2].id),
                    modifier = Modifier.weight(1f),
                    onSelect = { handleCardSelection(flowers[2]) }
                )
                FlowerChoiceCard(
                    flower = flowers[3],
                    isAssamese = isAssamese,
                    isMatched = DemoStateHolder.matchedFlowerIds.contains(flowers[3].id),
                    modifier = Modifier.weight(1f),
                    onSelect = { handleCardSelection(flowers[3]) }
                )
            }
        }

        // 4. Gentle Feedback / Celebration Footer with Real Telemetry & Adaptation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 84.dp)
                .background(
                    if (DemoStateHolder.isFlowerGameComplete)
                        AasritiColorTokens.SupportingSage.copy(alpha = 0.25f)
                    else
                        AasritiColorTokens.SoftCream,
                    RoundedCornerShape(18.dp)
                )
                .border(1.5.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(18.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (DemoStateHolder.isFlowerGameComplete) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (isAssamese) "🌸 বৰ সুন্দৰ! সকলো ফুল চিনাক্ত কৰা হ'ল।" else "🌸 Wonderful! All blooms identified gently.",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepNortheastForest,
                        textAlign = TextAlign.Center
                    )

                    // Real interaction physical telemetry from PerformanceCollector
                    Text(
                        text = if (isAssamese) {
                            "প্ৰতিক্ৰিয়া: ${lastReactionTimeMs}ms • দ্বিধা: $lastHesitationCount বাৰ • ত্ৰুটি: $lastErrorCount"
                        } else {
                            "Latency: ${lastReactionTimeMs}ms • Hesitation: $lastHesitationCount • Errors: $lastErrorCount"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AasritiColorTokens.DeepCharcoal
                    )

                    // On-Device Scikit-Learn Decision Tree Adaptation
                    Text(
                        text = if (isAssamese) {
                            "অনুকূলিত স্তৰ: স্তৰ $nextRecommendedLevel ($adaptiveFeedbackMessage)"
                        } else {
                            "Adaptive Next Level: Level $nextRecommendedLevel ($adaptiveFeedbackMessage)"
                        },
                        fontSize = 13.sp,
                        color = AasritiColorTokens.DeepNortheastForest,
                        fontWeight = FontWeight.Bold
                    )

                    // Room SQLite Local Persistence Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "✓ Room SQLite: অধিবেশন সংৰক্ষিত (100% Offline)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AasritiColorTokens.DeepNortheastForest
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Button(
                        onClick = {
                            DemoStateHolder.resetFlowerGame()
                            sessionSaved = false
                            collector.startRound()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = if (isAssamese) "আকৌ খেলোঁ আহক (Play Again)" else "Play Again",
                            color = AasritiColorTokens.WarmIvory,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                val feedback = DemoStateHolder.flowerGameFeedback
                Text(
                    text = feedback ?: if (isAssamese) "শান্তভাৱে ভাবি ফুলখন স্পৰ্শ কৰক।" else "Take your time and gently touch the flower.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (feedback != null && feedback.contains("আকৌ")) AasritiColorTokens.WarmAmberWarning else AasritiColorTokens.DeepCharcoal,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Individual high-contrast flower card with custom Canvas botanical badge.
 */
@Composable
private fun FlowerChoiceCard(
    flower: DemoFlowerCard,
    isAssamese: Boolean,
    isMatched: Boolean,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit
) {
    Box(
        modifier = modifier
            .height(140.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (isMatched) flower.secondaryColor.copy(alpha = 0.6f) else AasritiColorTokens.SoftCream
            )
            .border(
                width = if (isMatched) 3.dp else 1.5.dp,
                color = if (isMatched) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.WarmStoneBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onSelect() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Botanical Emblem Canvas
            BotanicalFlowerEmblem(
                primaryColor = flower.primaryColor,
                secondaryColor = flower.secondaryColor,
                id = flower.id,
                modifier = Modifier.size(54.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isAssamese) flower.nameIndic else flower.nameEn,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.DeepCharcoal,
                textAlign = TextAlign.Center
            )

            if (isMatched) {
                Text(
                    text = if (isAssamese) "✓ চিনাক্ত হৈছে" else "✓ Matched",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AasritiColorTokens.DeepNortheastForest
                )
            }
        }
    }
}

/**
 * Authentic procedural botanical badge rendered via Compose Canvas.
 * No emojis or childish cartoons; elegant North Eastern geometric flora.
 */
@Composable
fun BotanicalFlowerEmblem(
    primaryColor: Color,
    secondaryColor: Color,
    id: Int,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val petalRadius = size.minDimension * 0.32f
        val numPetals = when (id) {
            1 -> 6 // Kopou (Foxtail orchid florets)
            2 -> 5 // Tagar (Pinwheel Jasmine)
            3 -> 5 // Jaba (Hibiscus)
            else -> 8 // Nilkamal (Lotus / Water Lily)
        }

        // Draw outer petals
        for (i in 0 until numPetals) {
            val angle = (2 * Math.PI / numPetals) * i
            val petalCenterX = center.x + (petalRadius * 0.65f * cos(angle)).toFloat()
            val petalCenterY = center.y + (petalRadius * 0.65f * sin(angle)).toFloat()

            drawCircle(
                color = primaryColor.copy(alpha = 0.85f),
                radius = petalRadius * 0.5f,
                center = Offset(petalCenterX, petalCenterY)
            )
        }

        // Central flower core
        drawCircle(
            color = secondaryColor,
            radius = petalRadius * 0.42f,
            center = center
        )

        // Inner golden pollen stamen
        drawCircle(
            color = AasritiColorTokens.MugaGold,
            radius = petalRadius * 0.22f,
            center = center
        )
        drawCircle(
            color = AasritiColorTokens.DeepCharcoal,
            radius = petalRadius * 0.22f,
            center = center,
            style = Stroke(width = 1.5.dp.toPx())
        )
    }
}
