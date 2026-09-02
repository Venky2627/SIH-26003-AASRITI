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
        Triple(GameId.FAMILY_TRIVIA, "👨‍👩‍👧 পৰিয়ালৰ স্মৃতি", "Family Trivia • চিনাকি মানুহ"),
        Triple(GameId.VOICE_CUE_CARD, "🔊 কণ্ঠ আৰু ছবি", "Voice Cue Card • মাত আৰু ছবি"),
        Triple(GameId.SEQUENCING, "🫖 দৈনন্দিন ক্ৰম", "Daily Sequencing • চাহ বনোৱা"),
        Triple(GameId.CATEGORISATION, "🧺 শ্ৰেণীবিভাজন", "Categorisation • ফল আৰু পাচলি"),
        Triple(GameId.VILLAGE_MARKET, "🛍️ গাঁওৰ বজাৰ", "Village Market • বজাৰৰ মোনা"),
        Triple(GameId.PATTERN_RECOGNITION, "🔷 আৰ্হি চিনাক্তকৰণ", "Pattern Recognition • ৰং আৰু আৰ্হি")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onBackToProfiles,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262626)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.defaultMinSize(minHeight = 48.dp)
            ) {
                Text("← আন খেলুৱৈ", color = Color(0xFFFFD700), fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    voicePromptManager.speak("স্মৃতিসেতুলৈ স্বাগতম। আপোনাৰ খেল বাছক।")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.defaultMinSize(minHeight = 48.dp)
            ) {
                Text("🔊 নিৰ্দেশনা শুনক", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Gentle, Calm Header
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "নমস্কাৰ! এটা খেল বাছক",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "খেলুৱৈ সংকেত: ${patient.pseudonymCode}",
                fontSize = 14.sp,
                color = Color(0xFFBDBDBD)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large Tappable Game Cards (WCAG AAA Touch Targets)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(games.size) { index ->
                val (gameId, indicTitle, subTitle) = games[index]
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp)
                        .background(Color(0xFF1E1E1E), RoundedCornerShape(18.dp))
                        .border(2.dp, Color(0xFF424242), RoundedCornerShape(18.dp))
                        .clickable { onSelectGame(gameId) }
                        .padding(horizontal = 18.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                indicTitle,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD700)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                subTitle,
                                fontSize = 13.sp,
                                color = Color(0xFFE0E0E0)
                            )
                        }
                        Text("➔", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E676))
                    }
                }
            }
        }
    }
}
