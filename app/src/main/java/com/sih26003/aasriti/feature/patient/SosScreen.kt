package com.sih26003.aasriti.feature.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih26003.aasriti.core.ui.components.AasritiAppBackground
import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens
import com.sih26003.aasriti.demo.DemoStateHolder
import com.sih26003.aasriti.voice.playback.VoicePromptManager

/**
 * SCREEN_PATIENT_SOS:
 * Instantaneous, non-panicking emergency assistance and caregiver connection.
 * Follows UI_SCREEN_SPEC.md:
 * - Extremely clear contrast, non-punitive, safe offline simulation.
 * - Giant 120dp emergency action target in DeepCranberryEmergency.
 * - Clear cancellation option ("I pressed by mistake").
 */
@Composable
fun SosScreen(
    voicePromptManager: VoicePromptManager?,
    onOpenFollowUp: (() -> Unit)? = null,
    onBack: () -> Unit
) {
    val isAssamese = DemoStateHolder.currentLanguage == "as"
    val isSosActive = DemoStateHolder.isSosActive

    LaunchedEffect(Unit) {
        val prompt = if (isAssamese) {
            "সাহায্যৰ প্ৰয়োজন নেকি? জীয়ৰী মীৰাক মাতিবলৈ ডাঙৰ ৰঙা বুটামটো স্পৰ্শ কৰক।"
        } else {
            "Do you need help? Touch the large button to notify daughter Mira."
        }
        voicePromptManager?.speak(prompt)
    }

    AasritiAppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
        // 1. Safe Top Exit
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                modifier = Modifier.defaultMinSize(minHeight = 56.dp)
            ) {
                Text(
                    text = if (isAssamese) "← উভতি যাওক (মই ভালে আছোঁ)" else "← Back (I am okay)",
                    color = AasritiColorTokens.DeepCharcoal,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 2. Central Emergency Activation Container
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isAssamese) "সাহায্যৰ প্ৰয়োজন নেকি?" else "Do You Need Help?",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.DeepCharcoal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isAssamese) {
                    "এই বুটামটো টিপিলে লগে লগে জীয়ৰী মীৰালৈ সংকেত প্ৰেৰণ হ'ব।"
                } else {
                    "Pressing this notifies daughter Mira and your ASHA helper immediately."
                },
                fontSize = 17.sp,
                color = AasritiColorTokens.WarmSlate,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            if (!isSosActive) {
                // Giant 130dp Emergency Touch Target
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(AasritiColorTokens.DeepCranberryEmergency)
                        .clickable {
                            DemoStateHolder.triggerSos()
                            val prompt = if (isAssamese) {
                                "সতৰ্কবাৰ্তা প্ৰেৰণ কৰা হৈছে। মীৰা সোনকালেই উপস্থিত হ'ব।"
                            } else {
                                "Emergency alert sent. Help is on the way."
                            }
                            voicePromptManager?.speak(prompt)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🚨", fontSize = 38.sp)
                        Text(
                            text = if (isAssamese) "সহায় বিচাৰক" else "GET HELP",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AasritiColorTokens.WarmIvory
                        )
                    }
                }
            } else {
                // Activated Reassurance Confirmation Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AasritiColorTokens.SoftCream, RoundedCornerShape(20.dp))
                        .border(2.dp, AasritiColorTokens.DeepNortheastForest, RoundedCornerShape(20.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✓", fontSize = 36.sp, color = AasritiColorTokens.DeepNortheastForest, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isAssamese) "সতৰ্কবাৰ্তা প্ৰেৰণ কৰা হ'ল!" else "Emergency Alert Dispatched!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AasritiColorTokens.DeepNortheastForest
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isAssamese) {
                                "মীৰা বৰা (+91 98765 43210) আৰু আশা কৰ্মী হেমলতা ডেকাক অৱগত কৰা হৈছে। কোনো চিন্তা নকৰিব।"
                            } else {
                                "Mira Borah and ASHA Hemlata have been notified with your location. Please stay calm."
                            },
                            fontSize = 15.sp,
                            color = AasritiColorTokens.DeepCharcoal,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (onOpenFollowUp != null) {
                                Button(
                                    onClick = onOpenFollowUp,
                                    colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.height(52.dp)
                                ) {
                                    Text(
                                        text = if (isAssamese) "অনুসৰণ (Follow-up) ➔" else "Follow-up ➔",
                                        color = AasritiColorTokens.WarmIvory,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Button(
                                onClick = {
                                    DemoStateHolder.dismissSos()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.WarmSunkenSurface),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.height(52.dp)
                            ) {
                                Text(
                                    text = if (isAssamese) "বন্ধ কৰক (Dismiss)" else "Dismiss Alert",
                                    color = AasritiColorTokens.DeepCharcoal,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Secondary Actions & Cancel
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    DemoStateHolder.triggerSos()
                },
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                Text(
                    text = if (isAssamese) "🚑 এম্বুলেন্স / ১০৮ সহায় (Ambulance 108)" else "🚑 Emergency Helpline 108",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AasritiColorTokens.DeepCharcoal
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.WarmSunkenSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
            ) {
                Text(
                    text = if (isAssamese) "ভুলবশতঃ টিপিলো (I pressed by mistake)" else "I pressed by mistake (Cancel)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AasritiColorTokens.WarmSlate
                )
            }
        }
    }
    }
}
