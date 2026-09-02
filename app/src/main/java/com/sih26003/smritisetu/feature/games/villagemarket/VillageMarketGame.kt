package com.sih26003.smritisetu.feature.games.villagemarket

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

data class MarketItem(val id: String, val name: String, val emoji: String)

class VillageMarketEngine(
    patientId: String,
    gameRepository: GameRepository,
    decisionTreeEngine: DecisionTreeEngine,
    voicePromptManager: VoicePromptManager,
    scope: CoroutineScope
) : BaseGameEngine(
    gameId = GameId.VILLAGE_MARKET,
    patientId = patientId,
    gameRepository = gameRepository,
    decisionTreeEngine = decisionTreeEngine,
    voicePromptManager = voicePromptManager,
    scope = scope
) {
    override fun speakInstructions() {
        voicePromptManager.speakPromptKey(
            "village_market_instructions",
            "বজাৰৰ তালিকাখন মনত ৰাখক আৰু বজাৰৰ পৰা বস্তুবোৰ বাছক। (Remember the shopping list and pick the items from the local market.)"
        )
    }

    fun speakList(itemsText: String) {
        voicePromptManager.speak("আপোনাৰ বজাৰৰ তালিকা: $itemsText")
    }
}

@Composable
fun VillageMarketGameScreen(
    engine: VillageMarketEngine,
    onBack: () -> Unit
) {
    val phase by engine.gamePhase.collectAsState()
    val difficulty by engine.currentDifficulty.collectAsState()
    val feedbackMsg by engine.feedbackMessage.collectAsState()
    val roundCount by engine.roundCount.collectAsState()

    // Authentic North Eastern Village Market inventory
    val allStallGoods = remember {
        listOf(
            MarketItem("rice", "জহা চাউল (Joha Rice)", "🌾"),
            MarketItem("lemon", "কাজি নেমু (Assam Lemon)", "🍋"),
            MarketItem("chilli", "ভূত জলকীয়া (Bhut Jolokia)", "🌶️"),
            MarketItem("betel", "তামোল-পান (Paan)", "🍃"),
            MarketItem("tea", "অসম চাহ (Assam Tea)", "🍵"),
            MarketItem("fish", "লোকেল মাছ (River Fish)", "🐟"),
            MarketItem("oil", "সৰিয়হ তেল (Mustard Oil)", "🫙"),
            MarketItem("bamboo", "বাঁহৰ গাজ (Bamboo Shoot)", "🎍"),
            MarketItem("ginger", "আদা (Ginger)", "🫚")
        )
    }

    val (shoppingList, activeStall) = remember(difficulty, roundCount) {
        val count = when (difficulty) {
            1 -> 2
            2 -> 3
            3 -> 3
            4 -> 4
            else -> 5
        }
        val targetList = allStallGoods.take(count)
        val stall = when (difficulty) {
            1 -> targetList + allStallGoods.drop(count).take(2) // 4 items total in stall
            2 -> targetList + allStallGoods.drop(count).take(3) // 6 items in stall
            else -> allStallGoods // full market stall
        }.shuffled()
        Pair(targetList, stall)
    }

    var collectedItems by remember { mutableStateOf(listOf<String>()) }
    var showListPhase by remember { mutableStateOf(true) }

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
                        "🛍️ গাঁওৰ বজাৰ (Village Market)",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        when (difficulty) {
                            1 -> "বজাৰৰ ২ টা বস্তু মনত ৰাখি বজাৰৰ পৰা বুটলক।"
                            2 -> "৩ টা বস্তুৰ তালিকাখন মনত ৰাখক।"
                            3 -> "তালিকা মনত ৰাখক, অনাহুত বস্তু নলব।"
                            4 -> "ডাঙৰ বজাৰৰ পৰা ৪ টা বস্তু সংগ্ৰহ কৰক।"
                            else -> "মনোযোগেৰে ৫ টা বস্তু মনত ৰাখি বাছক।"
                        },
                        fontSize = 18.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            collectedItems = emptyList()
                            showListPhase = true
                            engine.startRound()
                            engine.speakList(shoppingList.joinToString(", ") { it.name })
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    ) {
                        Text("তালিকা চাওক (View List) ▶", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            GamePhase.PLAYING -> {
                if (showListPhase) {
                    // Memorization Phase
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E1E1E), RoundedCornerShape(18.dp))
                            .border(2.dp, Color(0xFFFFD700), RoundedCornerShape(18.dp))
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "📝 মনত ৰাখিবলগীয়া তালিকা:",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        shoppingList.forEach { item ->
                            Text(
                                "• ${item.emoji} ${item.name}",
                                fontSize = 18.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { showListPhase = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                        ) {
                            Text("মনত ৰাখিলোঁ, বজাৰলৈ যাওক ➔", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                } else {
                    // Village Market Stall Selection Phase
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "বজাৰৰ পৰা বস্তুবোৰ স্পৰ্শ কৰক: (${collectedItems.size}/${shoppingList.size})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        activeStall.chunked(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowItems.forEach { item ->
                                    val isCollected = collectedItems.contains(item.id)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(90.dp)
                                            .background(
                                                if (isCollected) Color(0xFF1B5E20) else Color(0xFF1E1E1E),
                                                RoundedCornerShape(14.dp)
                                            )
                                            .border(
                                                2.dp,
                                                if (isCollected) Color(0xFF00E676) else Color(0xFF424242),
                                                RoundedCornerShape(14.dp)
                                            )
                                            .clickable(enabled = !isCollected) {
                                                val isTarget = shoppingList.any { it.id == item.id }
                                                if (isTarget) {
                                                    val next = collectedItems + item.id
                                                    collectedItems = next
                                                    if (next.size == shoppingList.size) {
                                                        engine.onAnswerAttempt(isCorrect = true)
                                                    }
                                                } else {
                                                    engine.onAnswerAttempt(isCorrect = false)
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(item.emoji, fontSize = 34.sp)
                                            Text(
                                                item.name,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isCollected) Color(0xFF00E676) else Color.White
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
