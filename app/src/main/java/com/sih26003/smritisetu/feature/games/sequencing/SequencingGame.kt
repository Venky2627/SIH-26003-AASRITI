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

data class SequenceStep(
    val stepOrder: Int, // 1, 2, 3... or -1 for distractor
    val description: String,
    val emoji: String,
    val isDistractor: Boolean = false
)

data class ActivityScenario(
    val id: String,
    val title: String,
    val steps: List<SequenceStep>
)

class SequencingEngine(
    patientId: String,
    gameRepository: GameRepository,
    decisionTreeEngine: DecisionTreeEngine,
    voicePromptManager: VoicePromptManager,
    scope: CoroutineScope,
    initialDifficulty: Int = 1
) : BaseGameEngine(
    gameId = GameId.SEQUENCING,
    patientId = patientId,
    gameRepository = gameRepository,
    decisionTreeEngine = decisionTreeEngine,
    voicePromptManager = voicePromptManager,
    scope = scope,
    initialDifficulty = initialDifficulty
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
    val roundCount by engine.roundCount.collectAsState()
    val lastMetrics by engine.lastMetrics.collectAsState()

    // Cultural activities: Assam Tea Making and Namghar Morning Routine
    val teaScenario = remember {
        ActivityScenario(
            id = "tea",
            title = "চাহ তৈয়াৰ কৰা (Making Assam Tea)",
            steps = listOf(
                SequenceStep(1, "১. পানীত চাহপাত উতলোৱা (Boil water & tea)", "🫖"),
                SequenceStep(2, "২. গাখীৰ আৰু চেনি দিয়া (Add milk & sugar)", "🥛"),
                SequenceStep(3, "৩. ছাকনিৰে চাহ ছকা (Strain the tea)", "☕"),
                SequenceStep(4, "৪. কাপত বাকি আনন্দ লোৱা (Pour & drink)", "🍵"),
                SequenceStep(-1, "ছাতি খোলা (Open umbrella)", "☂️", isDistractor = true)
            )
        )
    }

    val morningScenario = remember {
        ActivityScenario(
            id = "morning",
            title = "নামঘৰলৈ যোৱাৰ প্ৰস্তুতি (Morning Routine)",
            steps = listOf(
                SequenceStep(1, "১. হাত-মুখ ধোৱা (Freshen up)", "🚰"),
                SequenceStep(2, "২. পৰিষ্কাৰ কাপোৰ পিন্ধা (Wear clean clothes)", "👕"),
                SequenceStep(3, "৩. ফুলাম গামোচা লোৱা (Take Gamosa)", "🧣"),
                SequenceStep(4, "৪. নামঘৰলৈ খোজ কঢ়া (Walk to Namghar)", "🚶"),
                SequenceStep(5, "৫. সেৱা জনোৱা (Offer prayers)", "🙏"),
                SequenceStep(-1, "বজাৰৰ মোনা লোৱা (Take market bag)", "🛍️", isDistractor = true)
            )
        )
    }

    val activeScenario = if (roundCount % 2 == 1) teaScenario else morningScenario

    // Select step items and distractors according to difficulty level
    val (displayedSteps, validStepsCount) = remember(difficulty, activeScenario) {
        when (difficulty) {
            1 -> Pair(activeScenario.steps.filter { !it.isDistractor }.take(2), 2)
            2 -> Pair(activeScenario.steps.filter { !it.isDistractor }.take(3), 3)
            3 -> Pair(activeScenario.steps.filter { !it.isDistractor }.take(4), 4)
            4 -> {
                // 5 items: 4 valid steps + 1 distractor
                val valid = activeScenario.steps.filter { !it.isDistractor }.take(4)
                val distractor = activeScenario.steps.filter { it.isDistractor }
                Pair((valid + distractor).shuffled(), 4)
            }
            else -> {
                // Level 5: 5-6 items with distractor
                val valid = activeScenario.steps.filter { !it.isDistractor }
                val distractor = activeScenario.steps.filter { it.isDistractor }
                Pair((valid + distractor).shuffled(), valid.size)
            }
        }
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
                        "🫖 দৈনন্দিন ক্ৰম: ${activeScenario.title}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        when (difficulty) {
                            1 -> "কাম দুটাৰ মাজৰ পৰা প্ৰথম কামটো প্ৰথমে বাছক।"
                            2 -> "কাম তিনিটা সঠিক ক্ৰম অনুসৰি সজাওক।"
                            3 -> "চাৰিটা কাম একাদিক্ৰমে সজাওক।"
                            4 -> "অদৰকাৰী কামটো বাদ দি ৪ টা কাম ক্ৰমত সজাওক।"
                            else -> "মনোযোগেৰে সম্পূৰ্ণ ক্ৰমটো সজাওক।"
                        },
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
                        "পৰৱৰ্তী কামটো বাছক: (${selectedOrder.size + 1}/$validStepsCount)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700),
                        modifier = Modifier.padding(bottom = 14.dp)
                    )

                    // Step cards
                    displayedSteps.forEach { step ->
                        val isChosen = selectedOrder.contains(step)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .background(
                                    if (isChosen) Color(0xFF1B5E20) else Color(0xFF1E1E1E),
                                    RoundedCornerShape(14.dp)
                                )
                                .border(
                                    2.dp,
                                    if (isChosen) Color(0xFF00E676) else Color(0xFF424242),
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable(enabled = !isChosen) {
                                    if (step.isDistractor) {
                                        // Picked an irrelevant distractor step
                                        engine.onAnswerAttempt(isCorrect = false)
                                    } else {
                                        val expectedOrder = selectedOrder.size + 1
                                        if (step.stepOrder == expectedOrder) {
                                            val nextOrder = selectedOrder + step
                                            selectedOrder = nextOrder
                                            if (nextOrder.size == validStepsCount) {
                                                engine.onAnswerAttempt(isCorrect = true)
                                            }
                                        } else {
                                            engine.onAnswerAttempt(isCorrect = false)
                                            selectedOrder = emptyList() // Soft reset for gentle retry
                                        }
                                    }
                                }
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(step.emoji, fontSize = 28.sp, modifier = Modifier.padding(end = 12.dp))
                                Text(
                                    step.description,
                                    fontSize = 16.sp,
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
                    Text("Round Complete", fontSize = 13.sp, color = Color(0xFFBDBDBD))
                    Spacer(modifier = Modifier.height(12.dp))

                    lastMetrics?.let { metrics ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF262626), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "প্ৰতিক্ৰিয়া সময়: ${metrics.reactionTimeMs} ms",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "দ্বিধা / অপেক্ষা (>3.5s): ${metrics.hesitationCount} বাৰ",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "ভুলৰ সংখ্যা: ${metrics.errors}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "সঠিকতা: ${(metrics.accuracy * 100).toInt()}%",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00E676)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Text(
                        "পৰৱৰ্তী পৰামৰ্শিত স্তৰ: স্তৰ $difficulty (Level $difficulty)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { engine.proceedToNextRound() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    ) {
                        Text("আকৌ খেলক (Play Next Round)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262626)),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF424242)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    ) {
                        Text("ঘৰলৈ উভতি যাওক (Finish & Return)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
