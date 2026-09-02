package com.sih26003.smritisetu.feature.games.voicecuecard

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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class CueItem(val id: String, val name: String, val emoji: String)

class VoiceCueCardEngine(
    patientId: String,
    gameRepository: GameRepository,
    decisionTreeEngine: DecisionTreeEngine,
    voicePromptManager: VoicePromptManager,
    scope: CoroutineScope
) : BaseGameEngine(
    gameId = GameId.VOICE_CUE_CARD,
    patientId = patientId,
    gameRepository = gameRepository,
    decisionTreeEngine = decisionTreeEngine,
    voicePromptManager = voicePromptManager,
    scope = scope
) {
    override fun speakInstructions() {
        voicePromptManager.speakPromptKey(
            "voice_cue_card_instructions",
            "মাতটো শুনক আৰু সঠিক বস্তুটো স্পৰ্শ কৰক। (Listen to the voice and tap the matching item.)"
        )
    }

    fun playCue(text: String) {
        voicePromptManager.speak(text)
    }
}

@Composable
fun VoiceCueCardGameScreen(
    engine: VoiceCueCardEngine,
    onBack: () -> Unit
) {
    val phase by engine.gamePhase.collectAsState()
    val difficulty by engine.currentDifficulty.collectAsState()
    val feedbackMsg by engine.feedbackMessage.collectAsState()
    val roundCount by engine.roundCount.collectAsState()
    val scope = rememberCoroutineScope()

    val availableItems = remember {
        listOf(
            CueItem("tea", "চাহৰ কাপ (Tea Cup)", "☕"),
            CueItem("water", "পানীৰ গিলাচ (Water Glass)", "🥛"),
            CueItem("flower", "ফুল (Flower)", "🌸"),
            CueItem("book", "কিতাপ (Book)", "📖"),
            CueItem("banana", "কল (Banana)", "🍌"),
            CueItem("gamosa", "গামোচা (Gamosa)", "🧣")
        )
    }

    // Sequence target for current level
    val targetSequence = remember(difficulty, roundCount) {
        when (difficulty) {
            1 -> listOf(availableItems[0]) // single item
            2 -> listOf(availableItems[1]) // short instruction
            3 -> listOf(availableItems[2], availableItems[3]) // 2-step
            4 -> listOf(availableItems[4], availableItems[0]) // mixed
            else -> listOf(availableItems[1], availableItems[2], availableItems[5]) // 3-step sequence
        }
    }

    val spokenPromptText = remember(difficulty, targetSequence) {
        when (difficulty) {
            1 -> targetSequence[0].name
            2 -> "অনুগ্ৰহ কৰি ${targetSequence[0].name} স্পৰ্শ কৰক।"
            3 -> "প্ৰথমে ${targetSequence[0].name}, তাৰ পিছত ${targetSequence[1].name} স্পৰ্শ কৰক।"
            4 -> "বিচাৰি উলিয়াওক: ${targetSequence[0].name} আৰু ${targetSequence[1].name}।"
            else -> "ক্ৰম অনুসৰি স্পৰ্শ কৰক: ${targetSequence.joinToString(", ") { it.name }}।"
        }
    }

    var selectedItems by remember { mutableStateOf(listOf<String>()) }
    var isListeningVoice by remember { mutableStateOf(false) }
    var voiceSecondsLeft by remember { mutableStateOf(5) }
    var showVoiceFallbackQuestion by remember { mutableStateOf(false) }

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
                        "🔊 কণ্ঠ আৰু ছবি (Voice Cue Card)",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        when (difficulty) {
                            1 -> "মাতটো শুনক আৰু কোৱা বস্তুটো স্পৰ্শ কৰক।"
                            2 -> "নিৰ্দেশনাটো মনোযোগেৰে শুনক।"
                            3 -> "দুটাকৈ বস্তু ক্ৰম অনুসৰি স্পৰ্শ কৰক।"
                            4 -> "তালিকাখন শুনি বস্তুবোৰ বিচাৰি উলিয়াওক।"
                            else -> "ক্ৰম অনুসৰি তিনিটা বস্তু চিনাক্ত কৰক।"
                        },
                        fontSize = 18.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            selectedItems = emptyList()
                            showVoiceFallbackQuestion = false
                            engine.startRound()
                            engine.playCue(spokenPromptText)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    ) {
                        Text("মাত শুনক আৰু আৰম্ভ কৰক ▶", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            GamePhase.PLAYING -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Audio Replay Button
                    Button(
                        onClick = { engine.playCue(spokenPromptText) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .border(2.dp, Color(0xFF4CAF50), RoundedCornerShape(14.dp))
                    ) {
                        Text("🔊 পুনৰ শুনক (Listen Again)", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Optional Voice Answer path (5-second limit, non-blocking)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                if (!isListeningVoice) {
                                    isListeningVoice = true
                                    voiceSecondsLeft = 5
                                    scope.launch {
                                        for (sec in 5 downTo 1) {
                                            voiceSecondsLeft = sec
                                            delay(1000)
                                        }
                                        isListeningVoice = false
                                        showVoiceFallbackQuestion = true
                                        engine.playCue(spokenPromptText)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262626)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        ) {
                            Text(
                                if (isListeningVoice) "🎙️ শুনি আছোঁ ($voiceSecondsLeft s)..." else "🎙️ মুখেৰে কওক (Voice Input)",
                                fontSize = 14.sp,
                                color = if (isListeningVoice) Color(0xFF00E676) else Color(0xFFFFD700)
                            )
                        }

                        if (showVoiceFallbackQuestion) {
                            Text("❓ স্পৰ্শ কৰক", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFA000))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cards Layout: Number of choices scaled by level
                    val displayItems = when (difficulty) {
                        1 -> availableItems.take(2)
                        2 -> availableItems.take(3)
                        3 -> availableItems.take(4)
                        else -> availableItems
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        displayItems.chunked(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowItems.forEach { item ->
                                    val isSelected = selectedItems.contains(item.id)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(96.dp)
                                            .background(
                                                if (isSelected) Color(0xFF1B5E20) else Color(0xFF1E1E1E),
                                                RoundedCornerShape(14.dp)
                                            )
                                            .border(
                                                2.dp,
                                                if (isSelected) Color(0xFF00E676) else Color(0xFF424242),
                                                RoundedCornerShape(14.dp)
                                            )
                                            .clickable {
                                                val nextSelected = selectedItems + item.id
                                                selectedItems = nextSelected

                                                val expectedIndex = nextSelected.size - 1
                                                if (expectedIndex < targetSequence.size) {
                                                    val expectedItem = targetSequence[expectedIndex]
                                                    if (item.id == expectedItem.id) {
                                                        if (nextSelected.size == targetSequence.size) {
                                                            engine.onAnswerAttempt(isCorrect = true)
                                                        }
                                                    } else {
                                                        engine.onAnswerAttempt(isCorrect = false)
                                                        selectedItems = emptyList() // Allow retry without penalties
                                                    }
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(item.emoji, fontSize = 38.sp)
                                            Text(item.name, fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
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
