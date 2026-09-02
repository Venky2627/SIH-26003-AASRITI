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

    // High contrast pattern sequence: e.g. 🟡 🔷 🟡 🔷 [ ? ] -> Answer: 🟡
    val patternSymbols = when (difficulty) {
        1 -> listOf("🟡", "🔷", "🟡", "🔷", "❓")
        2 -> listOf("🟡", "🔷", "🟩", "🟡", "🔷", "❓")
        else -> listOf("🟡", "🔷", "🟩", "🔺", "🟡", "🔷", "❓")
    }
    val correctAnswer = when (difficulty) {
        1 -> "🟡"
        2 -> "🟩"
        else -> "🟩"
    }
    val choices = listOf("🟡", "🔷", "🟩", "🔺")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
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
            Text("স্তৰ (Level) $difficulty", color = Color(0xFFFFD700), fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
                        "🔷 আৰ্হি চিনাক্তকৰণ (Pattern)",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "আৰ্হিটো মন দি চাওক আৰু প্ৰশ্নবোধক (?) চিনৰ ঠাইত কি হ'ব বাছক।",
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
                        "ক্ৰম অনুসৰি পিছৰ বস্তুটো কি? (What comes next?)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Sequence Display Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
                            .border(2.dp, Color(0xFF424242), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        patternSymbols.forEach { symbol ->
                            Text(
                                symbol,
                                fontSize = if (symbol == "❓") 36.sp else 32.sp,
                                color = if (symbol == "❓") Color(0xFFFFD700) else Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Tappable Choices
                    Text(
                        "তলৰ এটা বাছক: (Tap your choice)",
                        fontSize = 18.sp,
                        color = Color(0xFFE0E0E0),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        choices.forEach { choice ->
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(Color(0xFF262626), RoundedCornerShape(16.dp))
                                    .border(2.dp, Color(0xFFFFD700), RoundedCornerShape(16.dp))
                                    .clickable {
                                        val isCorrect = (choice == correctAnswer)
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
