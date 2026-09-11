package com.sih26003.aasriti.feature.games.patternrecognition

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih26003.aasriti.core.ui.components.AasritiVoicePill
import com.sih26003.aasriti.core.ui.components.AasritiVoicePillState
import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens
import com.sih26003.aasriti.data.repository.GameRepository
import com.sih26003.aasriti.feature.games.framework.BaseGameEngine
import com.sih26003.aasriti.feature.games.framework.GameId
import com.sih26003.aasriti.feature.games.framework.GamePhase
import com.sih26003.aasriti.ml.inference.DecisionTreeEngine
import com.sih26003.aasriti.voice.playback.VoicePromptManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class PatternItem(
    val id: String,
    val symbol: String,
    val nameIndic: String,
    val originRegion: String
)

data class PatternChallenge(
    val sequence: List<String>,
    val correctAnswer: String,
    val choices: List<String>,
    val descriptionIndic: String = ""
)

object PatternGameData {
    val culturalMotifs = listOf(
        PatternItem("kopou", "🌸", "কপৌ ফুল (Kopou Orchid)", "Assam"),
        PatternItem("jaapi", "👒", "বাঁহৰ জাপি (Bamboo Jaapi)", "Assam"),
        PatternItem("dhol", "🪕", "লোকবাদ্য (Folk Instrument)", "North East"),
        PatternItem("tea", "🍵", "অসম চাহ (Assam Tea Leaf)", "Assam"),
        PatternItem("gamosa", "🧣", "ফুলাম গামোচা (Gamosa)", "Assam"),
        PatternItem("lemon", "🍋", "কাজি নেমু (Assam Lemon)", "Assam"),
        PatternItem("lotus", "🪷", "নীলকমল (Blue Lily)", "Assam"),
        PatternItem("mandarin", "🍊", "খাচী সুমথিৰা (Khasi Mandarin)", "Meghalaya")
    )

    fun getChallengeForLevel(difficulty: Int, roundCount: Int): PatternChallenge {
        return when (difficulty) {
            1 -> {
                // Alternating pair of authentic regional items (A B A B ?)
                if (roundCount % 2 == 1) {
                    PatternChallenge(
                        sequence = listOf("🌸", "👒", "🌸", "👒", "❓"),
                        correctAnswer = "🌸",
                        choices = listOf("🌸", "👒"),
                        descriptionIndic = "কপৌ ফুল (🌸) আৰু বাঁহৰ জাপি (👒)"
                    )
                } else {
                    PatternChallenge(
                        sequence = listOf("🍵", "🧣", "🍵", "🧣", "❓"),
                        correctAnswer = "🍵",
                        choices = listOf("🍵", "🧣"),
                        descriptionIndic = "অসম চাহ (🍵) আৰু ফুলাম গামোচা (🧣)"
                    )
                }
            }
            2 -> {
                // 3-item regional rotation (A B C A B ?)
                PatternChallenge(
                    sequence = listOf("🌸", "👒", "🪕", "🌸", "👒", "❓"),
                    correctAnswer = "🪕",
                    choices = listOf("🌸", "👒", "🪕"),
                    descriptionIndic = "তিনিটা সাংস্কৃতিক সামগ্ৰীৰ ক্ৰম: ফুল, জাপি, আৰু লোকবাদ্য"
                )
            }
            3 -> {
                // Step / rhythmic handloom repetition (A A B A A ?)
                PatternChallenge(
                    sequence = listOf("🧣", "🧣", "🪷", "🧣", "🧣", "❓"),
                    correctAnswer = "🪷",
                    choices = listOf("🧣", "🪷", "🍋"),
                    descriptionIndic = "তাঁতশালৰ পাৰিৰ আৰ্হি: দুটা গামোচা আৰু এটা নীলকমল"
                )
            }
            4 -> {
                // Multi-item sequence with distractors (A B B A B ?)
                PatternChallenge(
                    sequence = listOf("🍋", "🍊", "🍊", "🍋", "🍊", "❓"),
                    correctAnswer = "🍊",
                    choices = listOf("🍋", "🍊", "🌸", "👒"),
                    descriptionIndic = "উত্তৰ-পূবৰ টেঙা ফলৰ ক্ৰম: কাজি নেমু আৰু খাচী সুমথিৰা"
                )
            }
            else -> {
                // Level 5: Interleaved sequence (A B A C A B ?)
                PatternChallenge(
                    sequence = listOf("🌸", "👒", "🌸", "🪕", "🌸", "👒", "❓"),
                    correctAnswer = "🌸",
                    choices = listOf("🌸", "👒", "🪕", "🍵"),
                    descriptionIndic = "সাংস্কৃতিক আৰ্হিৰ বিশেষ চক্ৰ"
                )
            }
        }
    }
}

class PatternRecognitionEngine(
    patientId: String,
    gameRepository: GameRepository,
    decisionTreeEngine: DecisionTreeEngine,
    voicePromptManager: VoicePromptManager,
    scope: CoroutineScope
) : BaseGameEngine(
    gameId = GameId.PATTERN_RECOGNITION,
    patientId = patientId,
    gameRepository = gameRepository,
    decisionTreeEngine = decisionTreeEngine,
    voicePromptManager = voicePromptManager,
    scope = scope
) {
    override fun speakInstructions() {
        voicePromptManager.speakPromptKey(
            "pattern_instructions",
            "আৰ্হিটো লক্ষ্য কৰক আৰু খালী ঠাইত কি বহিব বাছক। (Observe the cultural pattern sequence and select what comes next.)"
        )
    }
}

@Composable
fun PatternRecognitionGameScreen(
    engine: PatternRecognitionEngine,
    onBack: () -> Unit
) {
    val phase by engine.gamePhase.collectAsState()
    val difficulty by engine.currentDifficulty.collectAsState()
    val feedbackMsg by engine.feedbackMessage.collectAsState()
    val roundCount by engine.roundCount.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var isVoicePlaying by remember { mutableStateOf(false) }

    // Level-scaled cultural pattern challenge
    val challenge = remember(difficulty, roundCount) {
        PatternGameData.getChallengeForLevel(difficulty, roundCount)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AasritiColorTokens.WarmIvory)
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header Row with Navigation, Level Indicator, and Canonical Voice Pill
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                modifier = Modifier.defaultMinSize(minHeight = 44.dp)
            ) {
                Text(
                    text = "← উভতি যাওক",
                    color = AasritiColorTokens.DeepCharcoal,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .background(AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                    .border(1.5.dp, AasritiColorTokens.DeepNortheastForest, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "স্তৰ $difficulty (Level $difficulty)",
                    color = AasritiColorTokens.DeepNortheastForest,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            AasritiVoicePill(
                state = if (isVoicePlaying) AasritiVoicePillState.PLAYING else AasritiVoicePillState.IDLE,
                onClick = {
                    isVoicePlaying = true
                    engine.speakInstructions()
                    coroutineScope.launch {
                        delay(2500)
                        isVoicePlaying = false
                    }
                },
                modifier = Modifier.height(44.dp)
            )
        }

        when (phase) {
            GamePhase.INSTRUCTIONS -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AasritiColorTokens.SoftCream, RoundedCornerShape(20.dp))
                        .border(1.5.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(20.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🌸 সাংস্কৃতিক আৰ্হি চিনাক্তকৰণ",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepNortheastForest,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "(Cultural Pattern Recognition)",
                        fontSize = 14.sp,
                        color = AasritiColorTokens.WarmSlate,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = when (difficulty) {
                            1 -> "সাংস্কৃতিক আৰ্হিটো মন দি চাওক আৰু প্ৰশ্নবোধক (?) চিনৰ ঠাইত কি বহিব বাছক।"
                            2 -> "তিনিটা সাংস্কৃতিক সামগ্ৰীৰ ক্ৰমটো মন দি বুজি লওক।"
                            3 -> "তাঁতশালৰ পাৰিৰ দৰে আৰ্হিৰ পিছৰ বস্তুটো নিৰ্ণয় কৰক।"
                            4 -> "জটিল ক্ৰমৰ মাজৰ পৰা সঠিক উত্তৰ বাছক।"
                            else -> "ক্ষিপ্ৰভাৱে আৰ্হিটো পৰীক্ষা কৰি সঠিক চিন বাছক।"
                        },
                        fontSize = 17.sp,
                        color = AasritiColorTokens.DeepCharcoal,
                        textAlign = TextAlign.Center,
                        lineHeight = 25.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { engine.startRound() },
                        colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                    ) {
                        Text(
                            text = "আৰ্হি মিলাওক (Start Round) ▶",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            GamePhase.PLAYING -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "প্ৰশ্নবোধক (?) ঠাইত কি বহিব?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "What comes next in the sequence?",
                        fontSize = 13.sp,
                        color = AasritiColorTokens.WarmSlate,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Sequence Display Row with Muga Gold Accent
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AasritiColorTokens.SoftCream, RoundedCornerShape(20.dp))
                            .border(2.dp, AasritiColorTokens.MugaGold, RoundedCornerShape(20.dp))
                            .padding(vertical = 18.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        challenge.sequence.forEach { symbol ->
                            Text(
                                text = symbol,
                                fontSize = if (symbol == "❓") 38.sp else 32.sp,
                                color = if (symbol == "❓") AasritiColorTokens.MutedHeritageTerracotta else AasritiColorTokens.DeepCharcoal,
                                fontWeight = if (symbol == "❓") FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = "তলৰ পৰা শুদ্ধ ছবিখন স্পৰ্শ কৰক:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Big Tappable Choice Targets for Elderly Usability (>= 72dp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        challenge.choices.forEach { choice ->
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .background(AasritiColorTokens.SoftCream, RoundedCornerShape(18.dp))
                                    .border(2.5.dp, AasritiColorTokens.DeepNortheastForest, RoundedCornerShape(18.dp))
                                    .clickable {
                                        val isCorrect = (choice == challenge.correctAnswer)
                                        engine.onAnswerAttempt(isCorrect)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = choice, fontSize = 38.sp)
                            }
                        }
                    }
                }
            }

            GamePhase.FEEDBACK -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AasritiColorTokens.SoftCream, RoundedCornerShape(20.dp))
                        .border(2.dp, AasritiColorTokens.DeepNortheastForest, RoundedCornerShape(20.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🌟", fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = feedbackMsg,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepNortheastForest,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { engine.finishRound() },
                        colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                    ) {
                        Text(
                            text = "পৰৱৰ্তী স্তৰ (Next Round) ➔",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            GamePhase.ROUND_COMPLETE -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AasritiColorTokens.SoftCream, RoundedCornerShape(20.dp))
                        .border(2.dp, AasritiColorTokens.MugaGold, RoundedCornerShape(20.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "✅ আৰ্হি খেল সম্পন্ন হৈছে",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepNortheastForest
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "পৰৱৰ্তী পৰামৰ্শিত স্তৰ: $difficulty",
                        fontSize = 17.sp,
                        color = AasritiColorTokens.DeepCharcoal
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { engine.proceedToNextRound() },
                        colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                    ) {
                        Text(
                            text = "আকৌ খেলক (Play Again)",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}
