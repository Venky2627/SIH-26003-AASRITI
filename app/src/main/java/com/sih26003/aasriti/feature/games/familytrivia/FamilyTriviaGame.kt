package com.sih26003.aasriti.feature.games.familytrivia

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
import androidx.compose.ui.draw.clip
import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens
import com.sih26003.aasriti.data.local.entities.RelationshipEntity
import com.sih26003.aasriti.data.repository.GameRepository
import com.sih26003.aasriti.feature.games.framework.BaseGameEngine
import com.sih26003.aasriti.feature.games.framework.GameId
import com.sih26003.aasriti.feature.games.framework.GamePhase
import com.sih26003.aasriti.ml.inference.DecisionTreeEngine
import com.sih26003.aasriti.voice.playback.VoicePromptManager
import kotlinx.coroutines.CoroutineScope

class FamilyTriviaEngine(
    patientId: String,
    val relationships: List<RelationshipEntity>,
    gameRepository: GameRepository,
    decisionTreeEngine: DecisionTreeEngine,
    voicePromptManager: VoicePromptManager,
    scope: CoroutineScope,
    initialDifficulty: Int = 1
) : BaseGameEngine(
    gameId = GameId.FAMILY_TRIVIA,
    patientId = patientId,
    gameRepository = gameRepository,
    decisionTreeEngine = decisionTreeEngine,
    voicePromptManager = voicePromptManager,
    scope = scope,
    initialDifficulty = initialDifficulty.coerceIn(1, 5)
) {
    override fun speakInstructions() {
        voicePromptManager.speakPromptKey(
            "family_trivia_instructions",
            "ফটোখন ভালদৰে চাওক আৰু চিনাকি মানুহজন বাছক। (Look at the photo and recognize your family member.)"
        )
    }

    fun speakClue(clue: String) {
        voicePromptManager.speak(clue)
    }
}

@Composable
fun FamilyTriviaGameScreen(
    engine: FamilyTriviaEngine,
    onBack: () -> Unit
) {
    val phase by engine.gamePhase.collectAsState()
    val difficulty by engine.currentDifficulty.collectAsState()
    val feedbackMsg by engine.feedbackMessage.collectAsState()
    val roundCount by engine.roundCount.collectAsState()
    val lastMetrics by engine.lastMetrics.collectAsState()

    // Real relationships from Room SQLite, with fallback if not yet configured
    val members = remember(engine.relationships) {
        if (engine.relationships.isNotEmpty()) {
            engine.relationships
        } else {
            listOf(
                RelationshipEntity(patientId = engine.patientId, name = "ৰূপম বৰা (Rupam)", relationshipType = "পুত্ৰ (Son)"),
                RelationshipEntity(patientId = engine.patientId, name = "মিনতি বৰা (Minati)", relationshipType = "বোৱাৰী (Daughter-in-law)"),
                RelationshipEntity(patientId = engine.patientId, name = "প্ৰীতম (Pritam)", relationshipType = "নাতি (Grandson)")
            )
        }
    }

    val currentTarget = members[(roundCount - 1) % members.size]

    // Formulate choices based on difficulty level
    val choices = remember(difficulty, currentTarget) {
        val otherNames = listOf("অৰুণ শৰ্মা", "দীপক ডেকা", "বিমল বৰুৱা", "নৱ কলিতা")
            .filter { it != currentTarget.name }
        when (difficulty) {
            1 -> listOf(currentTarget.name, otherNames[0]).shuffled()
            2 -> listOf(currentTarget.name, otherNames[0], otherNames[1]).shuffled()
            3, 4 -> listOf(currentTarget.name, otherNames[0], otherNames[1], otherNames[2]).shuffled()
            else -> emptyList() // Level 5 is direct recall without choices
        }
    }

    var level5Revealed by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AasritiColorTokens.WarmIvory)
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
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                modifier = Modifier.defaultMinSize(minHeight = 48.dp)
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
                        .clip(RoundedCornerShape(20.dp))
                        .background(AasritiColorTokens.SoftCream)
                        .border(1.5.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(20.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "👨‍👩‍👧 পৰিয়ালৰ স্মৃতি (Family Trivia)",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        when (difficulty) {
                            1 -> "ফটোখন চাওক আৰু সঠিক সদস্যজনৰ নাম বাছক।"
                            2 -> "সম্পৰ্কটো চাওক আৰু সদস্যজন চিনাক্ত কৰক।"
                            3 -> "চাৰিটা নামৰ পৰা পৰিয়ালৰ সদস্যজন বাছক।"
                            4 -> "কণ্ঠৰ ইংগিত শুনক আৰু পৰিয়ালৰ সদস্যজন চিনাক্ত কৰক।"
                            else -> "ফটোখন চাই নিজে মনত পেলাওক।"
                        },
                        fontSize = 18.sp,
                        color = AasritiColorTokens.DeepCharcoal,
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            level5Revealed = false
                            engine.startRound()
                            if (difficulty == 4) {
                                engine.speakClue("তেখেত আপোনাৰ ${currentTarget.relationshipType}। চিনাক্ত কৰক।")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    ) {
                        Text("খেল আৰম্ভ কৰক (Start) ▶", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.WarmIvory)
                    }
                }
            }

            GamePhase.PLAYING -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Photo Placeholder with High Contrast Frame
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(AasritiColorTokens.SoftCream)
                            .border(2.5.dp, AasritiColorTokens.DeepNortheastForest, RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 68.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Contextual Question based on Difficulty Level
                    val promptTitle = when (difficulty) {
                        1 -> "এইজন কোন হয়? (Who is this?)"
                        2 -> "আপোনাৰ ${currentTarget.relationshipType} কোন হয়?"
                        3 -> "পৰিয়ালৰ সদস্যজনক বাছক (Choose the member):"
                        4 -> "ইংগিত: আপোনাৰ ${currentTarget.relationshipType}"
                        else -> "এইজনক মনত পেলাওক (Recall who this is):"
                    }

                    Text(
                        promptTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (difficulty < 5) {
                        // Levels 1 to 4: Large Tactile Choices (>= 64dp touch target)
                        choices.forEach { choice ->
                            Button(
                                onClick = {
                                    val isCorrect = (choice == currentTarget.name)
                                    engine.onAnswerAttempt(isCorrect)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                                    .padding(vertical = 4.dp)
                                    .border(1.5.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(14.dp))
                            ) {
                                Text(choice, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.DeepCharcoal)
                            }
                        }
                    } else {
                        // Level 5: Recall without choices
                        if (!level5Revealed) {
                            Button(
                                onClick = {
                                    level5Revealed = true
                                    engine.onAnswerAttempt(isCorrect = true)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(68.dp)
                            ) {
                                Text("💡 মনত পৰিছে (I remember this person)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.WarmIvory)
                            }
                        } else {
                            Text(
                                "তেখেত হৈছে: ${currentTarget.name} (${currentTarget.relationshipType})",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.DeepNortheastForest,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            GamePhase.FEEDBACK -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(AasritiColorTokens.SoftCream)
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
                        colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    ) {
                        Text("পৰৱৰ্তী স্তৰ (Next Round) ➔", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.WarmIvory)
                    }
                }
            }

            GamePhase.ROUND_COMPLETE -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(AasritiColorTokens.SoftCream)
                        .border(2.dp, AasritiColorTokens.DeepNortheastForest, RoundedCornerShape(20.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("✅ খেল সম্পন্ন হৈছে", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.DeepNortheastForest)
                    Text("Round Complete", fontSize = 13.sp, color = AasritiColorTokens.WarmSlate)
                    Spacer(modifier = Modifier.height(12.dp))

                    lastMetrics?.let { metrics ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(AasritiColorTokens.WarmSunkenSurface)
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "প্ৰতিক্ৰিয়া সময়: ${metrics.reactionTimeMs} ms",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AasritiColorTokens.DeepCharcoal
                                )
                                Text(
                                    text = "দ্বিধা / অপেক্ষা (>3.5s): ${metrics.hesitationCount} বাৰ",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AasritiColorTokens.DeepCharcoal
                                )
                                Text(
                                    text = "ভুলৰ সংখ্যা: ${metrics.errors}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AasritiColorTokens.DeepCharcoal
                                )
                                Text(
                                    text = "সঠিকতা: ${(metrics.accuracy * 100).toInt()}%",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AasritiColorTokens.DeepNortheastForest
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Text(
                        "পৰৱৰ্তী পৰামৰ্শিত স্তৰ: স্তৰ $difficulty (Level $difficulty)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { engine.proceedToNextRound() },
                        colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    ) {
                        Text("আকৌ খেলক (Play Next Round)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.WarmIvory)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    ) {
                        Text("ঘৰলৈ উভতি যাওক (Finish & Return)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.DeepCharcoal)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
