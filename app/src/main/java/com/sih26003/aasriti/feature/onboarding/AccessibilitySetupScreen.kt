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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih26003.aasriti.core.ui.components.AasritiAppBackground
import com.sih26003.aasriti.core.ui.components.AasritiLogoBadge
import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens
import com.sih26003.aasriti.demo.DemoStateHolder
import com.sih26003.aasriti.voice.playback.VoicePromptManager

/**
 * SCREEN 5: Accessibility and Visual Personalisation.
 * Faithfully matches prototype screen5.html.
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

    AasritiAppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 14.dp),
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
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(AasritiColorTokens.ParchmentSurface)
                        .border(1.5.dp, AasritiColorTokens.ParchmentBorder, CircleShape)
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
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(AasritiColorTokens.SoftClay)
                ) {
                    Text("🔊", fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isAssamese) "সহজ পঠন আৰু যত্নৰ সুবিধা" else "Accessibility & Comfort",
                fontSize = 28.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.TextDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isAssamese) "চকুৰ আৰাম আৰু সহজে বুজিব পৰাকৈ আখৰৰ আকাৰ নিৰ্ধাৰণ কৰক।"
                else "Tailor high-contrast reading comfort and spoken voice companion assistance.",
                fontSize = 14.sp,
                color = AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.85f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 1. Text Size Selection Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = AasritiColorTokens.CardSurface,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.ParchmentBorder),
                shadowElevation = 2.dp,
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
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.TextDark
                            )
                            Text(
                                text = if (isAssamese) "পঢ়াৰ সুবিধা অনুসৰি বাছক" else "Choose your reading comfort",
                                fontSize = 12.sp,
                                color = AasritiColorTokens.WarmSlate
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(AasritiColorTokens.WarmSunkenSurface)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (extraLarge) "Extra Large" else "Large",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.CrimsonDeep
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tactile Pills (Min 60px target height, matching screen5.html)
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
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!extraLarge) AasritiColorTokens.CrimsonDeep else AasritiColorTokens.WarmSunkenSurface
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = if (!extraLarge) 2.dp else 0.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(62.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "A",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!extraLarge) Color.White else AasritiColorTokens.TextDark
                                )
                                Text(
                                    text = "Large",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (!extraLarge) Color.White else AasritiColorTokens.TextDark
                                )
                                if (!extraLarge) {
                                    Text("✓", fontSize = 16.sp, color = Color.White)
                                }
                            }
                        }

                        // Extra Large Button
                        Button(
                            onClick = {
                                extraLarge = true
                                onPreferencesChanged(true, voiceEnabled)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (extraLarge) AasritiColorTokens.CrimsonDeep else AasritiColorTokens.WarmSunkenSurface
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = if (extraLarge) 2.dp else 0.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(62.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "AA",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (extraLarge) Color.White else AasritiColorTokens.TextDark
                                )
                                Text(
                                    text = "Extra Large",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (extraLarge) Color.White else AasritiColorTokens.TextDark
                                )
                                if (extraLarge) {
                                    Text("✓", fontSize = 16.sp, color = Color.White)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live Preview Strip
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(AasritiColorTokens.ParchmentSurface)
                            .border(1.dp, AasritiColorTokens.ParchmentBorder, RoundedCornerShape(10.dp))
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
                            color = AasritiColorTokens.TextDark,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Voice Assistance Switch Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = AasritiColorTokens.CardSurface,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.ParchmentBorder),
                shadowElevation = 2.dp,
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
                                .background(AasritiColorTokens.WarmSunkenSurface)
                                .border(1.dp, AasritiColorTokens.MugaGold.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🗣️", fontSize = 22.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = if (isAssamese) "মাতৰ সহায় (Voice Assistance)" else "Voice",
                                fontSize = 17.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.TextDark
                            )
                            Text(
                                text = if (isAssamese) "প্ৰতিটো নিৰ্দেশনা মুখেৰে ক'ব" else "Speaks every prompt aloud",
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
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AasritiColorTokens.CrimsonDeep,
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
                    .clip(RoundedCornerShape(12.dp))
                    .background(AasritiColorTokens.ParchmentSurface)
                    .border(1.dp, AasritiColorTokens.ParchmentBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🛡️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isAssamese) "WCAG AAA অনুৰূপ • জ্যেষ্ঠজনৰ চকুৰ কোনো ভাগৰ নপৰে।"
                        else "WCAG AAA Compliant • High Contrast • Non-glare Warm Parchment.",
                        fontSize = 12.sp,
                        color = AasritiColorTokens.DeepNortheastForest,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Bottom Navigation Action Deck
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    onPreferencesChanged(extraLarge, voiceEnabled)
                    onContinueClicked()
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.CrimsonDeep),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("✓", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isAssamese) "নিশ্চিত কৰক (Confirm)" else "Confirm",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color.White
                    )
                }
            }

            OutlinedButton(
                onClick = onBackClicked,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.ParchmentBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = if (isAssamese) "উভতি যাওক (Back)" else "Back",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AasritiColorTokens.WarmSlate
                )
            }
        }
    }
}
}
