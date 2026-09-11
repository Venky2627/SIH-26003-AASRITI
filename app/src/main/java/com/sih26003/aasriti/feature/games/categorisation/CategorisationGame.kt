package com.sih26003.aasriti.feature.games.categorisation

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

import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens

data class CategorisationItem(val name: String, val categoryId: String, val emoji: String)

data class CategoryDefinition(val id: String, val labelIndic: String, val emoji: String)

class CategorisationEngine(
    patientId: String,
    gameRepository: GameRepository,
    decisionTreeEngine: DecisionTreeEngine,
    voicePromptManager: VoicePromptManager,
    scope: CoroutineScope
) : BaseGameEngine(
    gameId = GameId.CATEGORISATION,
    patientId = patientId,
    gameRepository = gameRepository,
    decisionTreeEngine = decisionTreeEngine,
    voicePromptManager = voicePromptManager,
    scope = scope
) {
    override fun speakInstructions() {
        voicePromptManager.speakPromptKey(
            "categorisation_instructions",
            "বস্তুটো চাই সঠিক দলটো বাছক। (Look at the item and pick which group it belongs to.)"
        )
    }
}

@Composable
fun CategorisationGameScreen(
    engine: CategorisationEngine,
    onBack: () -> Unit
) {
    val phase by engine.gamePhase.collectAsState()
    val difficulty by engine.currentDifficulty.collectAsState()
    val feedbackMsg by engine.feedbackMessage.collectAsState()
    val roundCount by engine.roundCount.collectAsState()

    val categories = remember {
        listOf(
            CategoryDefinition("FRUIT", "ফল-মূল (Fruits)", "🍎"),
            CategoryDefinition("VEGETABLE", "শাক-পাচলি (Vegetables)", "🥬"),
            CategoryDefinition("ANIMAL", "পোহনীয়া জীৱ (Animals)", "🐄"),
            CategoryDefinition("CLOTH", "কাপোৰ (Traditional Wear)", "🧣")
        )
    }

    val itemPool = remember {
        listOf(
            CategorisationItem("পকা আম (Ripe Mango)", "FRUIT", "🥭"),
            CategorisationItem("মালভোগ কল (Banana)", "FRUIT", "🍌"),
            CategorisationItem("তিতা কেৰেলা (Bitter Gourd)", "VEGETABLE", "🥒"),
            CategorisationItem("জাতি লাউ (Bottle Gourd)", "VEGETABLE", "🥬"),
            CategorisationItem("ঘৰচীয়া গাই (Cow)", "ANIMAL", "🐄"),
            CategorisationItem("ফুলাম গামোচা (Gamosa)", "CLOTH", "🧣")
        )
    }

    val activeCategoryList = remember(difficulty) {
        when (difficulty) {
            1 -> categories.take(2)
            2 -> categories.take(3)
            else -> categories
        }
    }

    val currentItem = remember(difficulty, roundCount) {
        itemPool[(roundCount - 1) % itemPool.size]
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AasritiColorTokens.WarmIvory)
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
                        "🧺 শ্ৰেণীবিভাজন (Categorisation)",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        when (difficulty) {
                            1 -> "বস্তুটো চাই ২ টা দলৰ পৰা সঠিক দলটো বাছক।"
                            2 -> "৩ টা দলৰ মাজৰ পৰা সঠিক দলটো চিনাক্ত কৰক।"
                            3 -> "কম পৰিচিত বস্তুৰ সঠিক শ্ৰেণী নিৰ্ধাৰণ কৰক।"
                            4 -> "৪ টা সুকীয়া দলৰ মাজৰ পৰা বাছক।"
                            else -> "মনোযোগেৰে আৰু ক্ষিপ্ৰভাৱে শ্ৰেণী নিৰ্বাচন কৰক।"
                        },
                        fontSize = 18.sp,
                        color = AasritiColorTokens.WarmSlate,
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { engine.startRound() },
                        colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    ) {
                        Text("বাছনি আৰম্ভ কৰক ▶", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            GamePhase.PLAYING -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Item to classify with high contrast border
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .background(AasritiColorTokens.SoftCream, RoundedCornerShape(24.dp))
                            .border(2.5.dp, AasritiColorTokens.MugaGold, RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(currentItem.emoji, fontSize = 60.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                currentItem.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.DeepCharcoal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        "এইটো কিহৰ দলত পৰে? (Which group?)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Large Category Target Buttons (>= 64dp touch targets)
                    activeCategoryList.forEach { category ->
                        Button(
                            onClick = {
                                val isCorrect = (category.id == currentItem.categoryId)
                                engine.onAnswerAttempt(isCorrect)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                "${category.emoji}  ${category.labelIndic}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.DeepCharcoal
                            )
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

