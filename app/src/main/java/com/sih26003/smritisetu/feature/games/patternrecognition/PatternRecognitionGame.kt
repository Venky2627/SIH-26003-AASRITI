package com.sih26003.smritisetu.feature.games.patternrecognition

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
import com.sih26003.smritisetu.data.repository.GameRepository
import com.sih26003.smritisetu.feature.games.framework.BaseGameEngine
import com.sih26003.smritisetu.feature.games.framework.GameId
import com.sih26003.smritisetu.feature.games.framework.GamePhase
import com.sih26003.smritisetu.ml.inference.DecisionTreeEngine
import com.sih26003.smritisetu.voice.playback.VoicePromptManager
import kotlinx.coroutines.CoroutineScope

data class PatternChallenge(
    val sequence: List<String>,
    val correctAnswer: String,
    val choices: List<String>
)

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
            "আৰ্হিটো লক্ষ্য কৰক আৰু খালী ঠাইত কি বহিব বাছক। (Observe the pattern sequence and select what comes next.)"
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

    // Level-scaled pattern challenges
    val challenge = remember(difficulty, roundCount) {
        when (difficulty) {
            1 -> {
                // Simple 2-item alternating pattern
                if (roundCount % 2 == 1) {
                    PatternChallenge(listOf("🟡", "🔷", "🟡", "🔷", "❓"), "🟡", listOf("🟡", "🔷"))
                } else {
                    PatternChallenge(listOf("🔴", "🟢", "🔴", "🟢", "❓"), "🔴", listOf("🔴", "🟢"))
                }
            }
            2 -> {
                // 3-item pattern
                PatternChallenge(listOf("🟡", "🔷", "🟩", "🟡", "🔷", "❓"), "🟩", listOf("🟡", "🔷", "🟩"))
            }
            3 -> {
                // Longer sequence with step rhythm
                PatternChallenge(listOf("🔺", "🔺", "🔵", "🔺", "🔺", "❓"), "🔵", listOf("🔺", "🔵", "🟡"))
            }
            4 -> {
                // Complex pattern + distractors
                PatternChallenge(listOf("🔶", "🔷", "🔷", "🔶", "🔷", "❓"), "🔷", listOf("🔶", "🔷", "🔺", "🟩"))
            }
            else -> {
                // Level 5: Abstract sequence requiring fast evaluation
                PatternChallenge(listOf("🔴", "🔺", "🔵", "🔺", "🔴", "🔺", "❓"), "🔵", listOf("🔴", "🔵", "🔺", "🟡"))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262626)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.defaultMinSize(minHeight = 48.dp)
            ) {
                Text("← উভতি যাওক", color = Color(0xFFFFD700), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Text("স্তৰ $difficulty (Level $difficulty)", color = Color(0xFFFFD700), fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        when (phase) {
            GamePhase.INSTRUCTIONS -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "🔷 আৰ্হি চিনাক্তকৰণ (Pattern Recognition)",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        when (difficulty) {
                            1 -> "আৰ্হিটো চাওক আৰু প্ৰশ্নবোধক (?) চিনৰ ঠাইত কি হ'ব বাছক।"
                            2 -> "তিনিটা ৰঙৰ ক্ৰমটো মন দি বুজি লওক।"
                            3 -> "দীঘলীয়া আৰ্হিৰ পিছৰ বস্তুটো নিৰ্ণয় কৰক।"
                            4 -> "জটিল ক্ৰমৰ মাজৰ পৰা সঠিক উত্তৰ বিচাৰক।"
                            else -> "ক্ষিপ্ৰভাৱে আৰ্হিটো পৰীক্ষা কৰি বাছক।"
                        },
                        fontSize = 18.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { engine.startRound() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    ) {
                        Text("আৰ্হি মিলাওক (Start Round) ▶", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            GamePhase.PLAYING -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "প্ৰশ্নবোধক (?) চিনৰ ঠাইত কি বহিব? (What comes next?)",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Sequence Display Row with High Contrast
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
                            .border(2.dp, Color(0xFF424242), RoundedCornerShape(16.dp))
                            .padding(vertical = 16.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        challenge.sequence.forEach { symbol ->
                            Text(
                                symbol,
                                fontSize = if (symbol == "❓") 36.sp else 30.sp,
                                color = if (symbol == "❓") Color(0xFFFFD700) else Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        "তলৰ যিকোনো এটা স্পৰ্শ কৰক: (Tap choice)",
                        fontSize = 17.sp,
                        color = Color(0xFFE0E0E0),
                        modifier = Modifier.padding(bottom = 14.dp)
                    )

                    // Big Tappable Choice Targets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        challenge.choices.forEach { choice ->
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(Color(0xFF262626), RoundedCornerShape(16.dp))
                                    .border(2.5.dp, Color(0xFFFFD700), RoundedCornerShape(16.dp))
                                    .clickable {
                                        val isCorrect = (choice == challenge.correctAnswer)
                                        engine.onAnswerAttempt(isCorrect)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(choice, fontSize = 36.sp)
                            }
                        }
                    }
                }
            }

            GamePhase.FEEDBACK -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF152618), RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🌟", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        feedbackMsg,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E676),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { engine.finishRound() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    ) {
                        Text("পৰৱৰ্তী স্তৰ (Next Round) ➔", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF121212))
                    }
                }
            }

            GamePhase.ROUND_COMPLETE -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("✅ খেল সম্পন্ন হৈছে", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("পৰৱৰ্তী পৰামৰ্শিত স্তৰ: $difficulty", fontSize = 18.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { engine.proceedToNextRound() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    ) {
                        Text("আকৌ খেলক (Play Again)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
