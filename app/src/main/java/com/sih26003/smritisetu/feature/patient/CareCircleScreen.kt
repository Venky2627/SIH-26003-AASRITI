package com.sih26003.smritisetu.feature.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sih26003.smritisetu.core.ui.theme.AasritiColorTokens
import com.sih26003.smritisetu.demo.AasritiDemoData
import com.sih26003.smritisetu.demo.DemoFamilyContact
import com.sih26003.smritisetu.demo.DemoStateHolder
import com.sih26003.smritisetu.voice.playback.VoicePromptManager
import kotlinx.coroutines.delay

/**
 * SCREEN_PATIENT_CARE_CIRCLE:
 * Family circle contacts screen for one-touch simulated in-app calling.
 * Complies with UI_RULES.md and zero-permission demo safety rules.
 */
@Composable
fun CareCircleScreen(
    voicePromptManager: VoicePromptManager?,
    onBack: () -> Unit
) {
    val contacts = remember { AasritiDemoData.familyContacts }
    val isAssamese = DemoStateHolder.currentLanguage == "as"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AasritiColorTokens.WarmIvory)
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
                text = if (isAssamese) "পৰিয়ালৰ চক্ৰ" else "Care Circle",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.DeepNortheastForest
            )

            Button(
                onClick = {
                    val prompt = if (isAssamese) {
                        "আপোনাৰ পৰিয়ালৰ সদস্যসকল। কথা পাতিবলৈ বুটামত স্পৰ্শ কৰক।"
                    } else {
                        "Your family contacts. Tap call to speak with them."
                    }
                    voicePromptManager?.speak(prompt)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.MugaGold),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.defaultMinSize(minHeight = 56.dp)
            ) {
                Text(
                    text = if (isAssamese) "🔊 শুনক" else "🔊 Listen",
                    color = AasritiColorTokens.WarmIvory,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Subtitle Guidance
        Text(
            text = if (isAssamese) "পৰিয়ালৰ সদস্যৰ লগত কথা পাতিবলৈ 'কল কৰক' টিপক" else "Touch 'Call' to speak gently with your loved ones",
            fontSize = 16.sp,
            color = AasritiColorTokens.WarmSlate,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Family Contacts List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(contacts) { contact ->
                FamilyContactCard(
                    contact = contact,
                    isAssamese = isAssamese,
                    onCall = {
                        DemoStateHolder.startSimulatedCall(contact)
                    }
                )
            }
        }
    }

    // 3. Simulated Telephony Modal Dialog (Safe In-App Experience)
    val activeCall = DemoStateHolder.activeSimulatedCall
    if (activeCall != null) {
        SimulatedCallDialog(
            contact = activeCall,
            isAssamese = isAssamese,
            onDismiss = {
                DemoStateHolder.endSimulatedCall()
            }
        )
    }
}

/**
 * Large tactile family contact card.
 */
@Composable
private fun FamilyContactCard(
    contact: DemoFamilyContact,
    isAssamese: Boolean,
    onCall: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(AasritiColorTokens.SoftCream)
            .border(2.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Calm Avatar Circle with initials
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(contact.accentColor.copy(alpha = 0.15f))
                        .border(2.dp, contact.accentColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contact.name.take(1),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = contact.accentColor
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = contact.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isAssamese) contact.relationIndic else contact.relationEn,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = contact.accentColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = contact.phoneMasked,
                        fontSize = 13.sp,
                        color = AasritiColorTokens.WarmSlate
                    )
                }
            }

            // Call Action Button (Touch target >= 64dp)
            Button(
                onClick = onCall,
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .defaultMinSize(minWidth = 100.dp, minHeight = 64.dp)
            ) {
                Text(
                    text = if (isAssamese) "📞 কল" else "📞 Call",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AasritiColorTokens.WarmIvory
                )
            }
        }
    }
}

/**
 * Safe in-app simulated call dialog with realistic timer and end call action.
 */
@Composable
private fun SimulatedCallDialog(
    contact: DemoFamilyContact,
    isAssamese: Boolean,
    onDismiss: () -> Unit
) {
    var seconds by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            seconds++
            DemoStateHolder.callSecondsElapsed = seconds
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AasritiColorTokens.WarmIvory.copy(alpha = 0.96f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight(0.85f)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isAssamese) "কল চলি আছে..." else "In Call...",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = AasritiColorTokens.DeepNortheastForest
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Contact Avatar
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(contact.accentColor.copy(alpha = 0.15f))
                            .border(3.dp, contact.accentColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = contact.name.take(1),
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Bold,
                            color = contact.accentColor
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = contact.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal
                    )

                    Text(
                        text = if (isAssamese) contact.relationIndic else contact.relationEn,
                        fontSize = 16.sp,
                        color = AasritiColorTokens.WarmSlate
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val mins = seconds / 60
                    val secs = seconds % 60
                    Text(
                        text = String.format("%02d:%02d", mins, secs),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal
                    )
                }

                // Call Controls
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isAssamese) "অসমীয়া ভাষা সমৰ্থিত ইন-এপ ভইচ সংযোগ" else "In-App Simulated Voice Connection Active",
                        fontSize = 13.sp,
                        color = AasritiColorTokens.WarmSlate,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Big End Call Button
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepCranberryEmergency),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(72.dp)
                    ) {
                        Text(
                            text = if (isAssamese) "কল সমাপ্ত কৰক (End Call)" else "End Call",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AasritiColorTokens.WarmIvory
                        )
                    }
                }
            }
        }
    }
}
