package com.sih26003.aasriti.feature.memoryalbum

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih26003.aasriti.core.ui.components.AasritiAppBackground
import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens
import com.sih26003.aasriti.demo.AasritiDemoData
import com.sih26003.aasriti.demo.DemoMemoryItem
import com.sih26003.aasriti.demo.DemoStateHolder
import com.sih26003.aasriti.voice.playback.VoicePromptManager

/**
 * SCREEN_PATIENT_MEMORY_GARDEN:
 * Autobiographical memory reminiscence album for elderly patients.
 * Follows UI_SCREEN_SPEC.md:
 * - Handcrafted framed album aesthetic with WarmStoneBorder
 * - Zero EXIF metadata or camera file names
 * - Large font sizes, calming tones, non-punitive exploration
 * - Audio voice note playback simulation with Muga Gold pulse
 */
@Composable
fun MemoryGardenScreen(
    voicePromptManager: VoicePromptManager?,
    onBack: () -> Unit
) {
    val memories = remember { AasritiDemoData.memories }
    var currentIndex by remember { mutableStateOf(0) }
    val currentMemory = memories[currentIndex]
    val isAssamese = DemoStateHolder.currentLanguage == "as"
    val isPlayingAudio = DemoStateHolder.activePlayingMemoryId == currentMemory.id

    val scrollState = rememberScrollState()

    AasritiAppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
        // 1. Top Navigation Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                modifier = Modifier.defaultMinSize(minHeight = 56.dp)
            ) {
                Text(
                    text = if (isAssamese) "← মূল পৃষ্ঠা" else "← Home",
                    color = AasritiColorTokens.DeepCharcoal,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = if (isAssamese) "স্মৃতি বাৰী" else "Memory Garden",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.MutedHeritageTerracotta
            )

            Button(
                onClick = {
                    val prompt = if (isAssamese) {
                        "${currentMemory.titleIndic}। ${currentMemory.storyIndic}"
                    } else {
                        "${currentMemory.titleEn}. ${currentMemory.storyEn}"
                    }
                    voicePromptManager?.speak(prompt)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.MugaGold),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.defaultMinSize(minHeight = 56.dp)
            ) {
                Text(
                    text = if (isAssamese) "🔊 পঢ়ক" else "🔊 Read",
                    color = AasritiColorTokens.WarmIvory,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Framed Album Photo Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AasritiColorTokens.SoftCream, RoundedCornerShape(22.dp))
                .border(2.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(22.dp))
                .padding(18.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Procedural Heritage Canvas Visual Frame
                HeritageMemoryArtFrame(
                    memoryId = currentMemory.id,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Location & Year Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(AasritiColorTokens.WarmSunkenSurface, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = currentMemory.locationTag,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AasritiColorTokens.WarmSlate
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(AasritiColorTokens.MutedHeritageTerracotta.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "বছৰ ${currentMemory.yearTag}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AasritiColorTokens.MutedHeritageTerracotta
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title
                Text(
                    text = if (isAssamese) currentMemory.titleIndic else currentMemory.titleEn,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = AasritiColorTokens.DeepCharcoal,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Story Narrative
                Text(
                    text = if (isAssamese) currentMemory.storyIndic else currentMemory.storyEn,
                    fontSize = 17.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Normal,
                    color = AasritiColorTokens.DeepCharcoal,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Voice Note Pill Player
                AudioVoiceNotePill(
                    isPlaying = isPlayingAudio,
                    narrator = currentMemory.relativeNarrator,
                    durationSec = currentMemory.audioDurationSec,
                    isAssamese = isAssamese,
                    onToggle = {
                        DemoStateHolder.toggleMemoryAudio(currentMemory.id)
                        if (!isPlayingAudio) {
                            val prompt = if (isAssamese) {
                                "${currentMemory.relativeNarrator}ৰ কণ্ঠ: ${currentMemory.storyIndic}"
                            } else {
                                "Voice of ${currentMemory.relativeNarrator}: ${currentMemory.storyEn}"
                            }
                            voicePromptManager?.speak(prompt)
                        } else {
                            voicePromptManager?.stop()
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. Navigation Controls (Previous / Next)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (currentIndex > 0) currentIndex--
                },
                enabled = currentIndex > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AasritiColorTokens.SoftCream,
                    disabledContainerColor = AasritiColorTokens.WarmSunkenSurface
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
            ) {
                Text(
                    text = if (isAssamese) "◀ পূৰ্বৰ স্মৃতি" else "◀ Previous",
                    color = if (currentIndex > 0) AasritiColorTokens.DeepCharcoal else AasritiColorTokens.WarmSlate,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Step dots
            Text(
                text = "${currentIndex + 1} / ${memories.size}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.WarmSlate
            )

            Spacer(modifier = Modifier.width(14.dp))

            Button(
                onClick = {
                    if (currentIndex < memories.size - 1) currentIndex++
                },
                enabled = currentIndex < memories.size - 1,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AasritiColorTokens.DeepNortheastForest,
                    disabledContainerColor = AasritiColorTokens.WarmSunkenSurface
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
            ) {
                Text(
                    text = if (isAssamese) "পৰৱৰ্তী স্মৃতি ▶" else "Next ▶",
                    color = if (currentIndex < memories.size - 1) AasritiColorTokens.WarmIvory else AasritiColorTokens.WarmSlate,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
    }
}

/**
 * Animated audio pill player with Muga Gold pulse.
 */
@Composable
private fun AudioVoiceNotePill(
    isPlaying: Boolean,
    narrator: String,
    durationSec: Int,
    isAssamese: Boolean,
    onToggle: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (isPlaying) AasritiColorTokens.MugaGold.copy(alpha = pulseAlpha * 0.3f) else AasritiColorTokens.WarmSunkenSurface
            )
            .border(
                width = if (isPlaying) 2.dp else 1.dp,
                color = if (isPlaying) AasritiColorTokens.MugaGold else AasritiColorTokens.WarmStoneBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onToggle() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isPlaying) AasritiColorTokens.MugaGold else AasritiColorTokens.DeepNortheastForest),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isPlaying) "⏸" else "▶",
                        color = AasritiColorTokens.WarmIvory,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (isPlaying) {
                            if (isAssamese) "বৰ্তমান বাজি আছে..." else "Playing voice note..."
                        } else {
                            if (isAssamese) "কণ্ঠস্বৰ শুনক" else "Listen to voice note"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal
                    )
                    Text(
                        text = "$narrator • ${durationSec}s",
                        fontSize = 13.sp,
                        color = AasritiColorTokens.WarmSlate
                    )
                }
            }

            if (isPlaying) {
                Text(
                    text = "🔊",
                    fontSize = 22.sp,
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
        }
    }
}

/**
 * Procedural heritage landscape canvas art for memory framing.
 * Draws symbolic Assam tea hills, traditional Bihu courtyard motifs, or serene river Namghar.
 */
@Composable
private fun HeritageMemoryArtFrame(
    memoryId: String,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        when (memoryId) {
            "mem_tea_garden" -> {
                // Lush rolling tea hills & golden morning sun
                drawRect(color = Color(0xFFE8F5E9)) // Pale morning sky

                // Golden morning sun
                drawCircle(
                    color = AasritiColorTokens.MugaGold.copy(alpha = 0.8f),
                    radius = h * 0.28f,
                    center = Offset(w * 0.75f, h * 0.35f)
                )

                // Background hills
                val hillPath1 = Path().apply {
                    moveTo(0f, h * 0.6f)
                    quadraticBezierTo(w * 0.3f, h * 0.35f, w * 0.65f, h * 0.55f)
                    quadraticBezierTo(w * 0.85f, h * 0.65f, w, h * 0.5f)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(hillPath1, color = Color(0xFF81C784))

                // Foreground tea bushes hill
                val hillPath2 = Path().apply {
                    moveTo(0f, h * 0.72f)
                    quadraticBezierTo(w * 0.4f, h * 0.6f, w * 0.7f, h * 0.75f)
                    quadraticBezierTo(w * 0.9f, h * 0.8f, w, h * 0.68f)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(hillPath2, color = AasritiColorTokens.DeepNortheastForest)
            }

            "mem_bihu_celebration" -> {
                // Warm festive courtyard with heritage motifs
                drawRect(color = Color(0xFFFFF3E0)) // Warm festive sky

                // Traditional terracotta courtyard base
                drawRect(
                    color = AasritiColorTokens.MutedHeritageTerracotta.copy(alpha = 0.85f),
                    topLeft = Offset(0f, h * 0.65f),
                    size = Size(w, h * 0.35f)
                )

                // Traditional Jaapi / sun emblem
                drawCircle(
                    color = AasritiColorTokens.MugaGold,
                    radius = h * 0.26f,
                    center = Offset(w * 0.5f, h * 0.4f)
                )
                drawCircle(
                    color = AasritiColorTokens.MutedHeritageTerracotta,
                    radius = h * 0.12f,
                    center = Offset(w * 0.5f, h * 0.4f)
                )
            }

            else -> {
                // Majestic Brahmaputra river & Majuli Namghar silhouette
                drawRect(color = Color(0xFFE1F5FE)) // River dawn sky

                // Serene river water
                drawRect(
                    color = Color(0xFF81D4FA),
                    topLeft = Offset(0f, h * 0.55f),
                    size = Size(w, h * 0.45f)
                )

                // Namghar roof silhouette
                val namgharRoof = Path().apply {
                    moveTo(w * 0.35f, h * 0.55f)
                    lineTo(w * 0.5f, h * 0.25f)
                    lineTo(w * 0.65f, h * 0.55f)
                    close()
                }
                drawPath(namgharRoof, color = AasritiColorTokens.DeepNortheastForest)
            }
        }
    }
}
