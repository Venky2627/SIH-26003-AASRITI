package com.sih26003.smritisetu.feature.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih26003.smritisetu.core.ui.theme.AasritiColorTokens
import com.sih26003.smritisetu.data.local.entities.PatientEntity
import com.sih26003.smritisetu.demo.AasritiDemoData
import com.sih26003.smritisetu.demo.DemoStateHolder
import com.sih26003.smritisetu.feature.games.flowermatch.BotanicalFlowerEmblem
import com.sih26003.smritisetu.feature.games.framework.GameId
import com.sih26003.smritisetu.voice.playback.VoicePromptManager

/**
 * SCREEN_PATIENT_HOME:
 * Guided companion home screen for elderly patients.
 * Follows UI_RULES.md & UI_SCREEN_SPEC.md:
 * - Warm Ivory background, Soft Cream cards, Deep Charcoal text.
 * - Single primary focal action per view (Flower Match hero card).
 * - Gentle temporal orientation (Day, Time, Weather).
 * - Routine checklist with non-punitive toggle.
 * - Four direct access bottom items (Flower Match, Memory Garden, Care Circle, SOS).
 */
@Composable
fun PatientHomeScreen(
    patient: PatientEntity,
    voicePromptManager: VoicePromptManager?,
    onSelectGame: (GameId) -> Unit,
    onOpenFlowerMatch: () -> Unit,
    onOpenMemoryGarden: () -> Unit,
    onOpenCareCircle: () -> Unit,
    onOpenSos: () -> Unit,
    onBackToProfiles: () -> Unit
) {
    val routines = remember { AasritiDemoData.initialRoutines }
    val isAssamese = DemoStateHolder.currentLanguage == "as"
    val scrollState = rememberScrollState()

    var showOtherGamesModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AasritiColorTokens.WarmIvory)
            .padding(18.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Top Bar: Profile exit, Voice prompt, and discreet Emergency SOS Pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onBackToProfiles,
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                modifier = Modifier.defaultMinSize(minHeight = 52.dp)
            ) {
                Text(
                    text = if (isAssamese) "← আন খেলুৱৈ" else "← Switch",
                    color = AasritiColorTokens.DeepCharcoal,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Voice Read Button
                Button(
                    onClick = {
                        val prompt = if (isAssamese) {
                            "নমস্কাৰ আইতা। আজিৰ বিশেষ খেল হৈছে ফুল মেলোৱা। আৰম্ভ কৰিবলৈ সেউজীয়া বুটামটো স্পৰ্শ কৰক।"
                        } else {
                            "Welcome. Today's activity is Flower Match. Touch the green button to begin."
                        }
                        voicePromptManager?.speak(prompt)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.MugaGold),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.defaultMinSize(minHeight = 52.dp)
                ) {
                    Text(
                        text = "🔊 শুনক",
                        color = AasritiColorTokens.WarmIvory,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // SOS Emergency Pill Button
                Button(
                    onClick = onOpenSos,
                    colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepCranberryEmergency),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.defaultMinSize(minHeight = 52.dp)
                ) {
                    Text(
                        text = "🚨 সহায়",
                        color = AasritiColorTokens.WarmIvory,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Temporal Orientation Block
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isAssamese) "নমস্কাৰ, আইতা বৰা (${patient.pseudonymCode})" else "Welcome, Aita Borah (${patient.pseudonymCode})",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.DeepCharcoal
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .background(AasritiColorTokens.WarmSunkenSurface, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isAssamese) "বৃহস্পতিবাৰ, পুৱা • শান্ত আৰু পৰিষ্কাৰ বতৰ" else "Thursday Morning • Calm & Pleasant Weather",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AasritiColorTokens.WarmSlate
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "100% Offline • Room SQLite Local Source of Truth",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AasritiColorTokens.DeepNortheastForest
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. Hero Focal Activity Card: Regional Flower Match
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(AasritiColorTokens.SoftCream)
                .border(2.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(22.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BotanicalFlowerEmblem(
                        primaryColor = Color(0xFFC2185B),
                        secondaryColor = Color(0xFFF8BBD0),
                        id = 1,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = if (isAssamese) "আজিৰ বিশেষ খেল" else "Today's Guided Activity",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AasritiColorTokens.MutedHeritageTerracotta
                        )
                        Text(
                            text = if (isAssamese) "ফুল মেলোৱা (Flower Match)" else "Flower Match",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AasritiColorTokens.DeepCharcoal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isAssamese) "অসমৰ চিনাকি কাপৌ আৰু তগৰ ফুল চিনি উলিয়াওঁ আহক।" else "Match familiar blooms of Assam together gently.",
                    fontSize = 15.sp,
                    color = AasritiColorTokens.WarmSlate,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Giant Action Button (Height >= 72dp)
                Button(
                    onClick = onOpenFlowerMatch,
                    colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                ) {
                    Text(
                        text = if (isAssamese) "🌸 খেল আৰম্ভ কৰক (Start)" else "🌸 Start Activity",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.WarmIvory
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 4. Gentle Routine Checklist
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = if (isAssamese) "আজিৰ দৈনন্দিন নিয়ম (Today's Routine)" else "Today's Gentle Routine",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.DeepCharcoal,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            routines.forEach { item ->
                val isCompleted = DemoStateHolder.completedRoutineIds.contains(item.id)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isCompleted) AasritiColorTokens.SupportingSage.copy(alpha = 0.2f) else AasritiColorTokens.SoftCream)
                        .border(1.5.dp, if (isCompleted) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(16.dp))
                        .clickable { DemoStateHolder.toggleRoutine(item.id) }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isCompleted) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.WarmSunkenSurface)
                                    .border(1.5.dp, AasritiColorTokens.WarmStoneBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCompleted) {
                                    Text("✓", color = AasritiColorTokens.WarmIvory, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isAssamese) item.titleIndic else item.titleEn,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AasritiColorTokens.DeepCharcoal
                                )
                                Text(
                                    text = item.timeLabel,
                                    fontSize = 12.sp,
                                    color = AasritiColorTokens.WarmSlate
                                )
                            }
                        }

                        Text(
                            text = if (isCompleted) "সম্পূৰ্ণ" else "বাকি",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCompleted) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.WarmAmberWarning
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 5. Four Elder Companion Quick Bar (>= 64dp touch targets)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Memory Garden
            Button(
                onClick = onOpenMemoryGarden,
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
            ) {
                Text(
                    text = if (isAssamese) "🖼️ স্মৃতি বাৰী" else "🖼️ Memories",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AasritiColorTokens.MutedHeritageTerracotta
                )
            }

            // Family Circle
            Button(
                onClick = onOpenCareCircle,
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
            ) {
                Text(
                    text = if (isAssamese) "👨‍👩‍👧 পৰিয়াল" else "👨‍👩‍👧 Family",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AasritiColorTokens.DeepNortheastForest
                )
            }

            // Other Games Arena
            Button(
                onClick = { showOtherGamesModal = true },
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
            ) {
                Text(
                    text = if (isAssamese) "🧩 অন্য খেল" else "🧩 Games",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AasritiColorTokens.DeepCharcoal
                )
            }
        }
    }

    // Modal to access the other 5 cognitive games
    if (showOtherGamesModal) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showOtherGamesModal = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(AasritiColorTokens.WarmIvory)
                    .border(2.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(22.dp))
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isAssamese) "অন্য জ্ঞানমূলক খেলসমূহ" else "Other Cognitive Games",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val otherGames = listOf(
                        Triple(GameId.FAMILY_TRIVIA, "👨‍👩‍👧 পৰিয়ালৰ স্মৃতি (Family Trivia)", "চিনাকি মানুহ চিনাক্তকৰণ"),
                        Triple(GameId.VOICE_CUE_CARD, "🔊 কণ্ঠ আৰু ছবি (Voice Cue Card)", "মাত আৰু ছবি মেলোৱা"),
                        Triple(GameId.SEQUENCING, "🫖 দৈনন্দিন ক্ৰম (Sequencing)", "চাহ বনোৱাৰ ক্ৰম"),
                        Triple(GameId.CATEGORISATION, "🧺 শ্ৰেণীবিভাজন (Categorisation)", "ফল আৰু পাচলি ভাগ কৰক"),
                        Triple(GameId.VILLAGE_MARKET, "🛍️ গাঁওৰ বজাৰ (Village Market)", "বজাৰৰ সামগ্ৰী গণনা")
                    )

                    otherGames.forEach { (gameId, title, desc) ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(AasritiColorTokens.SoftCream)
                                .border(1.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(14.dp))
                                .clickable {
                                    showOtherGamesModal = false
                                    onSelectGame(gameId)
                                }
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.DeepCharcoal)
                                Text(desc, fontSize = 12.sp, color = AasritiColorTokens.WarmSlate)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { showOtherGamesModal = false },
                        colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.WarmSunkenSurface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("বন্ধ কৰক (Close)", color = AasritiColorTokens.DeepCharcoal, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
