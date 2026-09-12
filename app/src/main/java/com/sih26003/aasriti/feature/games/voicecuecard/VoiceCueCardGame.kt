package com.sih26003.aasriti.feature.games.voicecuecard

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
import com.sih26003.aasriti.data.repository.GameRepository
import com.sih26003.aasriti.feature.games.framework.BaseGameEngine
import com.sih26003.aasriti.feature.games.framework.GameId
import com.sih26003.aasriti.feature.games.framework.GamePhase
import com.sih26003.aasriti.ml.inference.DecisionTreeEngine
import com.sih26003.aasriti.voice.playback.VoicePromptManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.sih26003.aasriti.core.ui.components.AasritiAppBackground
import com.sih26003.aasriti.core.ui.components.AasritiVoicePill
import com.sih26003.aasriti.core.ui.components.AasritiVoicePillState
import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens

data class CueItem(val id: String, val name: String, val emoji: String)

object VoiceCueCardMatcher {
    /**
     * Multilingual speech keyword matcher for spoken voice cue answers.
     * Supports Assamese, English, Manipuri, and Khasi vocabulary.
     */
    fun matchesSpokenCue(spokenText: String, expectedItem: CueItem): Boolean {
        val clean = spokenText.lowercase().trim()
        val targetId = expectedItem.id.lowercase()
        if (clean.contains(targetId)) return true

        val aliases = when (targetId) {
            "tea" -> listOf("tea", "চাহ", "চা", "chai", "cup")
            "water" -> listOf("water", "পানী", "পানি", "pani", "glass", "ishi", "um")
            "flower" -> listOf("flower", "ফুল", "phool", "kopou", "leikham")
            "book" -> listOf("book", "কিতাপ", "বই", "kitap", "lairik", "kot")
            "banana" -> listOf("banana", "কল", "kol", "laphoi", "kait")
            "gamosa" -> listOf("gamosa", "গামোচা", "gamusa", "towel", "scarf")
            "jaapi" -> listOf("jaapi", "জাপি", "japi", "hat")
            "duitara" -> listOf("duitara", "দুতৰা", "dotara", "lute", "instrument")
            "pena" -> listOf("pena", "পেনা", "lute", "fiddle")
            "jolpan" -> listOf("jolpan", "জলপান", "snack", "bowl", "curd")
            else -> listOf(targetId)
        }

        return aliases.any { clean.contains(it) }
    }
}

object VoiceCueCardData {
    val defaultItems = listOf(
        CueItem("tea", "চাহৰ কাপ (Tea Cup)", "☕"),
        CueItem("water", "পানীৰ গিলাচ (Water Glass)", "🥛"),
        CueItem("flower", "কপৌ ফুল (Kopou Orchid)", "🌸"),
        CueItem("book", "কিতাপ (Book)", "📖"),
        CueItem("banana", "মালভোগ কল (Banana)", "🍌"),
        CueItem("gamosa", "ফুলাম গামোচা (Phulam Gamosa)", "🧣"),
        CueItem("jaapi", "বাঁহৰ জাপি (Bamboo Jaapi)", "👒"),
        CueItem("duitara", "দুতৰা বাদ্য (Duitara)", "🪕"),
        CueItem("pena", "পেনা বাদ্য (Pena)", "🎻"),
        CueItem("jolpan", "জলপান বাটি (Jolpan Bowl)", "🥣")
    )
}

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

    val availableItems = remember { VoiceCueCardData.defaultItems }

    // Sequence target for current level with round progression
    val targetSequence = remember(difficulty, roundCount) {
        val offset = (roundCount - 1) % availableItems.size
        val shifted = availableItems.drop(offset) + availableItems.take(offset)
        when (difficulty) {
            1 -> listOf(shifted[0]) // single item
            2 -> listOf(shifted[0]) // short instruction
            3 -> listOf(shifted[0], shifted[1]) // 2-step
            4 -> listOf(shifted[0], shifted[1]) // 2-step mixed
            else -> listOf(shifted[0], shifted[1], shifted[2]) // 3-step sequence
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
    var isPlayingAudio by remember { mutableStateOf(false) }
    var voiceSecondsLeft by remember { mutableStateOf(5) }
    var showVoiceFallbackQuestion by remember { mutableStateOf(false) }

    AasritiAppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                modifier = Modifier.defaultMinSize(minHeight = 52.dp)
            ) {
                Text("← উভতি যাওক", color = AasritiColorTokens.DeepCharcoal, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                "স্তৰ $difficulty (Level $difficulty)",
                color = AasritiColorTokens.DeepNortheastForest,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
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
                        "🔊 কণ্ঠ আৰু ছবি (Voice Cue Card)",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal,
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
                        color = AasritiColorTokens.WarmSlate,
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
                        colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                        shape = RoundedCornerShape(16.dp),
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
                    // Canonical Voice Pill for Audio Replay
                    AasritiVoicePill(
                        state = if (isPlayingAudio) AasritiVoicePillState.PLAYING else AasritiVoicePillState.IDLE,
                        onClick = {
                            isPlayingAudio = true
                            engine.playCue(spokenPromptText)
                            scope.launch {
                                delay(2500)
                                isPlayingAudio = false
                            }
                        },
                        customText = if (isPlayingAudio) "🔊 বজাই থকা হৈছে... (Playing...)" else "🔊 পুনৰ শুনক (Listen Again)",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Optional Voice Answer Path using AasritiVoicePill (5-second limit, non-punitive fallback)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AasritiVoicePill(
                            state = if (isListeningVoice) AasritiVoicePillState.LISTENING else AasritiVoicePillState.PAUSED,
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
                            customText = if (isListeningVoice) "🎙️ শুনি আছোঁ ($voiceSecondsLeft s)..." else "🎙️ মুখেৰে কওক (Voice Answer)",
                            modifier = Modifier.weight(1f)
                        )

                        if (showVoiceFallbackQuestion) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("❓ স্পৰ্শ কৰক", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.MugaGold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cards Layout: Number of choices scaled by level, ensuring targets are always present
                    val displayItems = remember(difficulty, targetSequence, availableItems) {
                        val distractors = availableItems.filter { item -> !targetSequence.any { it.id == item.id } }
                        val distractorCount = when (difficulty) {
                            1 -> 1 // 1 target + 1 distractor = 2
                            2 -> 2 // 1 target + 2 distractors = 3
                            3 -> 2 // 2 targets + 2 distractors = 4
                            4 -> 4 // 2 targets + 4 distractors = 6
                            else -> 5 // 3 targets + 5 distractors = 8
                        }
                        (targetSequence + distractors.take(distractorCount)).distinctBy { it.id }.sortedBy { it.id }
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
                                            .height(98.dp)
                                            .background(
                                                if (isSelected) AasritiColorTokens.SupportingSage.copy(alpha = 0.4f) else AasritiColorTokens.SoftCream,
                                                RoundedCornerShape(16.dp)
                                            )
                                            .border(
                                                2.dp,
                                                if (isSelected) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.WarmStoneBorder,
                                                RoundedCornerShape(16.dp)
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
                                            Text(
                                                item.name,
                                                fontSize = 13.sp,
                                                color = if (isSelected) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.DeepCharcoal,
                                                fontWeight = FontWeight.Bold
                                            )
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
                        .background(AasritiColorTokens.SoftCream, RoundedCornerShape(20.dp))
                        .border(2.dp, AasritiColorTokens.DeepNortheastForest, RoundedCornerShape(20.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🌟", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        feedbackMsg,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepNortheastForest,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { engine.finishRound() },
                        colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.MugaGold),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    ) {
                        Text("পৰৱৰ্তী স্তৰ (Next Round) ➔", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.DeepCharcoal)
                    }
                }
            }

            GamePhase.ROUND_COMPLETE -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AasritiColorTokens.SoftCream, RoundedCornerShape(20.dp))
                        .border(1.5.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(20.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("✅ খেল সম্পন্ন হৈছে", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.DeepNortheastForest)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("পৰৱৰ্তী পৰামৰ্শিত স্তৰ: $difficulty", fontSize = 18.sp, color = AasritiColorTokens.DeepCharcoal)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { engine.proceedToNextRound() },
                        colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                        shape = RoundedCornerShape(16.dp),
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
}
