package com.sih26003.smritisetu.feature.games.familytrivia

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
import com.sih26003.smritisetu.data.local.entities.RelationshipEntity
import com.sih26003.smritisetu.data.repository.GameRepository
import com.sih26003.smritisetu.feature.games.framework.BaseGameEngine
import com.sih26003.smritisetu.feature.games.framework.GameId
import com.sih26003.smritisetu.feature.games.framework.GamePhase
import com.sih26003.smritisetu.ml.inference.DecisionTreeEngine
import com.sih26003.smritisetu.voice.playback.VoicePromptManager
import kotlinx.coroutines.CoroutineScope

class FamilyTriviaEngine(
    patientId: String,
    val relationships: List<RelationshipEntity>,
    gameRepository: GameRepository,
    decisionTreeEngine: DecisionTreeEngine,
    voicePromptManager: VoicePromptManager,
    scope: CoroutineScope
) : BaseGameEngine(
    gameId = GameId.FAMILY_TRIVIA,
    patientId = patientId,
    gameRepository = gameRepository,
    decisionTreeEngine = decisionTreeEngine,
    voicePromptManager = voicePromptManager,
    scope = scope
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
                        "👨‍👩‍👧 পৰিয়ালৰ স্মৃতি (Family Trivia)",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700),
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
                        color = Color.White,
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    ) {
                        Text("খেল আৰম্ভ কৰক (Start) ▶", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
                            .background(Color(0xFF1E1E1E), RoundedCornerShape(20.dp))
                            .border(3.dp, Color(0xFFFFD700), RoundedCornerShape(20.dp)),
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
                        color = Color(0xFFFFD700),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (difficulty < 5) {
                        // Levels 1 to 4: Large Tactile Choices
                        choices.forEach { choice ->
                            Button(
                                onClick = {
                                    val isCorrect = (choice == currentTarget.name)
                                    engine.onAnswerAttempt(isCorrect)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E1E)),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                                    .padding(vertical = 4.dp)
                                    .border(2.dp, Color(0xFF424242), RoundedCornerShape(14.dp))
                            ) {
                                Text(choice, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(68.dp)
                                    .border(2.dp, Color(0xFF4CAF50), RoundedCornerShape(16.dp))
                            ) {
                                Text("💡 মনত পৰিছে (I remember this person)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        } else {
                            Text(
                                "তেখেত হৈছে: ${currentTarget.name} (${currentTarget.relationshipType})",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00E676),
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
                        Text("আকৌ খেলক (Play Next Round)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
