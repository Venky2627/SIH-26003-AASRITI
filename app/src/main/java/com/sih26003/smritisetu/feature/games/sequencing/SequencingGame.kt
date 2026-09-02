package com.sih26003.smritisetu.feature.games.sequencing

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

data class SequenceStep(val stepIndex: Int, val description: String, val emoji: String)

class SequencingEngine(
    patientId: String,
    gameRepository: GameRepository,
    decisionTreeEngine: DecisionTreeEngine,
    voicePromptManager: VoicePromptManager,
    scope: CoroutineScope
) : BaseGameEngine(
    gameId = GameId.SEQUENCING,
    patientId = patientId,
    gameRepository = gameRepository,
    decisionTreeEngine = decisionTreeEngine,
    voicePromptManager = voicePromptManager,
    scope = scope
) {
    override fun speakInstructions() {
        voicePromptManager.speakPromptKey(
            "sequencing_instructions",
            "দৈনন্দিন কামবোৰ ক্ৰম অনুসৰি সজাওক। (Arrange daily activities in the correct order.)"
        )
    }
}

@Composable
fun SequencingGameScreen(
    engine: SequencingEngine,
    onBack: () -> Unit
) {
    val phase by engine.gamePhase.collectAsState()
    val difficulty by engine.currentDifficulty.collectAsState()
    val feedbackMsg by engine.feedbackMessage.collectAsState()

    // Example Scenario: Making Traditional Assam Milk Tea (চাহ তৈয়াৰ কৰা)
    val fullSteps = remember {
        listOf(
            SequenceStep(1, "১. পানীত চাহপাত উতলোৱা (Boil water & tea)", "🫖"),
            SequenceStep(2, "২. গাখীৰ আৰু চেনি দিয়া (Add milk & sugar)", "🥛"),
            SequenceStep(3, "৩. কাপত চাহ বাকি খোৱা (Pour into cup & enjoy)", "☕")
        )
    }

    val currentSteps = when (difficulty) {
        1 -> fullSteps.take(2)
        else -> fullSteps
    }

    var selectedOrder by remember { mutableStateOf(listOf<SequenceStep>()) }

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
                        "🫖 দৈনন্দিন ক্ৰম (Daily Sequencing)",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "চাহ তৈয়াৰ কৰাৰ সঠিক ক্ৰমটো বাছক। প্ৰথম কামটো প্ৰথমে স্পৰ্শ কৰক।",
                        fontSize = 18.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            selectedOrder = emptyList()
                            engine.startRound()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    ) {
                        Text("ক্ৰম সজাওক (Start Sequencing) ▶", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            GamePhase.PLAYING -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "প্ৰথম কামটো বাছক: (Pick the next step)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Display choices
                    currentSteps.shuffled().forEach { step ->
                        val isChosen = selectedOrder.contains(step)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .background(
                                    if (isChosen) Color(0xFF1B5E20) else Color(0xFF1E1E1E),
                                    RoundedCornerShape(14.dp)
                                )
                                .border(2.dp, if (isChosen) Color(0xFF00E676) else Color(0xFF424242), RoundedCornerShape(14.dp))
                                .clickable(enabled = !isChosen) {
                                    val nextOrder = selectedOrder + step
                                    selectedOrder = nextOrder

                                    // Validate sequence
                                    val expectedStep = currentSteps[selectedOrder.size - 1]
                                    if (step.stepIndex == expectedStep.stepIndex) {
                                        if (nextOrder.size == currentSteps.size) {
                                            engine.onAnswerAttempt(isCorrect = true)
                                        }
                                    } else {
                                        engine.onAnswerAttempt(isCorrect = false)
                                        selectedOrder = emptyList() // Reset for retry
                                    }
                                }
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(step.emoji, fontSize = 32.sp, modifier = Modifier.padding(right = 12.dp))
                                Text(
                                    step.description,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isChosen) Color.White else Color(0xFFFFD700)
                                )
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
