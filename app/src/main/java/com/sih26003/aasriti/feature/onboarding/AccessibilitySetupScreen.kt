package com.sih26003.aasriti.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih26003.aasriti.core.ui.components.AasritiLogoBadge
import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens
import com.sih26003.aasriti.demo.DemoStateHolder
import com.sih26003.aasriti.voice.playback.VoicePromptManager

/**
 * SCREEN 5: Accessibility and Visual Personalisation.
 * Matches prototype screen5.html.
 * Allows tailoring text scaling, live preview, and voice assistance switches
 * calibrated for elderly cognition.
 */
@Composable
fun AccessibilitySetupScreen(
    voicePromptManager: VoicePromptManager,
    isExtraLargeFont: Boolean,
    isVoiceAssistanceEnabled: Boolean,
    onPreferencesChanged: (Boolean, Boolean) -> Unit, // (isExtraLarge, isVoiceEnabled)
    onContinueClicked: () -> Unit,
    onBackClicked: () -> Unit
) {
    val isAssamese = DemoStateHolder.currentLanguage == "as"
    var extraLarge by remember { mutableStateOf(isExtraLargeFont) }
    var voiceEnabled by remember { mutableStateOf(isVoiceAssistanceEnabled) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AasritiColorTokens.WarmIvory)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClicked,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AasritiColorTokens.SoftCream)
                        .border(1.dp, AasritiColorTokens.WarmStoneBorder, CircleShape)
                ) {
                    Text("←", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.DeepCharcoal)
                }

                AasritiLogoBadge(size = 46.dp)

                IconButton(
                    onClick = {
                        val text = if (isAssamese) {
                            "সহজ পঠন আৰু মাতৰ সহায় নিৰ্বাচন কৰক।"
                        } else {
                            "Select text size and voice assistance comfort."
                        }
                        voicePromptManager.speak(text)
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AasritiColorTokens.SoftCream)
                        .border(1.dp, AasritiColorTokens.WarmStoneBorder, CircleShape)
                ) {
                    Text("🔊", fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isAssamese) "সহজ পঠন আৰু যত্নৰ সুবিধা" else "Accessibility & Comfort",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.DeepCharcoal
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isAssamese) "চকুৰ আৰাম আৰু সহজে বুজিব পৰাকৈ আখৰৰ আকাৰ নিৰ্ধাৰণ কৰক।"
                else "Tailor high-contrast reading comfort and spoken voice companion assistance.",
                fontSize = 14.sp,
                color = AasritiColorTokens.WarmSlate
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Text Size Selection Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AasritiColorTokens.SoftCream),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isAssamese) "আখৰৰ আকাৰ (Text Size)" else "Text Size",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.DeepCharcoal
                            )
                            Text(
                                text = if (isAssamese) "পঢ়াৰ সুবিধা অনুসৰি বাছক" else "Choose reading comfort",
                                fontSize = 12.sp,
                                color = AasritiColorTokens.WarmSlate
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.12f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (extraLarge) "Extra Large (অতি ডাঙৰ)" else "Large (ডাঙৰ)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.DeepNortheastForest
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Large Button
                        Button(
                            onClick = {
                                extraLarge = false
                                onPreferencesChanged(false, voiceEnabled)
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!extraLarge) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.WarmSunkenSurface
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(62.dp)
                        ) {
                            Text(
                                text = "A  Large",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!extraLarge) AasritiColorTokens.WarmIvory else AasritiColorTokens.DeepCharcoal
                            )
                        }

                        // Extra Large Button
                        Button(
                            onClick = {
                                extraLarge = true
                                onPreferencesChanged(true, voiceEnabled)
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (extraLarge) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.WarmSunkenSurface
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(62.dp)
                        ) {
                            Text(
                                text = "AA  Extra Large",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (extraLarge) AasritiColorTokens.WarmIvory else AasritiColorTokens.DeepCharcoal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live Preview Strip
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(AasritiColorTokens.WarmIvory)
                            .border(1.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isAssamese) {
                                "“নমস্কাৰ। আপোনাৰ দিনটো আৰামদায়ক আৰু শান্তিময় হওক।”"
                            } else {
                                "“Namaskar. Your routine is warm, clear, and ready.”"
                            },
                            fontSize = if (extraLarge) 20.sp else 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AasritiColorTokens.DeepCharcoal,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Voice Assistance Switch Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AasritiColorTokens.SoftCream),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🗣️", fontSize = 24.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = if (isAssamese) "মাতৰ সহায় (Voice Assistance)" else "Voice Assistance",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.DeepCharcoal
                            )
                            Text(
                                text = if (isAssamese) "প্ৰতিটো নিৰ্দেশনা মুখেৰে ক'ব" else "Speaks every prompt and encouragement aloud",
                                fontSize = 12.sp,
                                color = AasritiColorTokens.WarmSlate
                            )
                        }
                    }

                    Switch(
                        checked = voiceEnabled,
                        onCheckedChange = {
                            voiceEnabled = it
                            onPreferencesChanged(extraLarge, it)
                            if (it) {
                                voicePromptManager.speak("Voice assistance enabled.")
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AasritiColorTokens.WarmIvory,
                            checkedTrackColor = AasritiColorTokens.DeepNortheastForest,
                            uncheckedThumbColor = AasritiColorTokens.WarmSlate,
                            uncheckedTrackColor = AasritiColorTokens.WarmSunkenSurface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Calm Reassurance Note
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AasritiColorTokens.SoftCream.copy(alpha = 0.6f))
                    .border(1.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🛡️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isAssamese) "WCAG AAA অনুৰূপ • জ্যেষ্ঠজনৰ চকুৰ কোনো ভাগৰ নপৰে।"
                        else "WCAG AAA Compliant • High Contrast • Non-glare Warm Ivory.",
                        fontSize = 12.sp,
                        color = AasritiColorTokens.WarmSlate
                    )
                }
            }
        }

        // Action CTA
        Button(
            onClick = {
                onPreferencesChanged(extraLarge, voiceEnabled)
                onContinueClicked()
            },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(top = 10.dp)
        ) {
            Text(
                text = if (isAssamese) "আগবাঢ়ক (Continue) ➔" else "Continue to Elder Profile ➔",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.WarmIvory
            )
        }
    }
}
