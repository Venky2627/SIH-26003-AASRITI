package com.sih26003.smritisetu.feature.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih26003.smritisetu.data.local.entities.PatientEntity
import com.sih26003.smritisetu.feature.games.framework.GameId
import com.sih26003.smritisetu.voice.playback.VoicePromptManager

@Composable
fun PatientHomeScreen(
    patient: PatientEntity,
    voicePromptManager: VoicePromptManager,
    onSelectGame: (GameId) -> Unit,
    onBackToProfiles: () -> Unit
) {
    val games = listOf(
        Pair(GameId.FAMILY_TRIVIA, "👨‍👩‍👧 পৰিয়ালৰ স্মৃতি (Family Trivia)"),
        Pair(GameId.VOICE_CUE_CARD, "🔊 কণ্ঠ আৰু ছবি (Voice Cue Card)"),
        Pair(GameId.SEQUENCING, "🫖 ক্ৰম সজোৱা (Daily Sequencing)"),
        Pair(GameId.CATEGORISATION, "🧺 শ্ৰেণীবিভাজন (Categorisation)"),
        Pair(GameId.VILLAGE_MARKET, "🛍️ গাঁওৰ বজাৰ (Village Market)"),
        Pair(GameId.PATTERN_RECOGNITION, "🔷 আৰ্হি চিনাক্তকৰণ (Pattern)")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onBackToProfiles,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262626)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("← আন খেলুৱৈ", color = Color(0xFFFFD700), fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    voicePromptManager.speak("স্মৃতিসেতুলৈ স্বাগতম। তলৰ যিকোনো এটা খেল স্পৰ্শ কৰক।")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("🔊 শুনক", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Patient Greeting
        Text(
            "নমস্কাৰ, আপোনাৰ খেল বাছক",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFD700)
        )
        Text(
            "Choose a game to exercise your memory today",
            fontSize = 14.sp,
            color = Color(0xFFBDBDBD)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Big, Tappable Game Tiles
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(games.size) { index ->
                val (gameId, gameTitle) = games[index]
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                        .background(Color(0xFF1E1E1E), RoundedCornerShape(18.dp))
                        .border(2.dp, Color(0xFF424242), RoundedCornerShape(18.dp))
                        .clickable { onSelectGame(gameId) }
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            gameTitle,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                        Text("➔", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E676))
                    }
                }
            }
        }
    }
}
