package com.sih26003.aasriti.feature.auth

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
import com.sih26003.aasriti.core.ui.components.AasritiLogoBadge
import com.sih26003.aasriti.core.ui.components.CalmConnectivityPill
import com.sih26003.aasriti.core.ui.components.LanguageTogglePill
import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens
import com.sih26003.aasriti.demo.DemoStateHolder

/**
 * SCREEN 6 — Informed Consent & Privacy Assent.
 * Faithfully matches prototype screen6.html.
 * Implements DPDPA 2023 compliant plain-language privacy transparency.
 * Allows either Direct Elder Assent or Proxy Caregiver Assent with offline local storage.
 */
@Composable
fun InformedConsentScreen(
    onConsentAccepted: () -> Unit,
    onConsentDeclined: () -> Unit
) {
    var isElderAssent by remember { mutableStateOf(true) }
    val isAssamese = DemoStateHolder.currentLanguage == "as"
    val scrollState = rememberScrollState()

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            AasritiColorTokens.ParchmentSurface,
            AasritiColorTokens.ParchmentBase,
            AasritiColorTokens.ParchmentDeep
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Utility Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LanguageTogglePill(
                currentLanguage = DemoStateHolder.currentLanguage,
                onLanguageSelected = { DemoStateHolder.currentLanguage = it }
            )
            CalmConnectivityPill(isAssamese = isAssamese)
        }

        // Scrollable Consent Content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AasritiColorTokens.CardSurface)
                    .border(1.5.dp, AasritiColorTokens.ParchmentBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                AasritiLogoBadge(size = 52.dp)
                Column {
                    Text(
                        text = if (isAssamese) "যত্ন আৰু গোপনীয়তা" else "Care & Privacy",
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.CrimsonDeep
                    )
                    Text(
                        text = if (isAssamese) "কেৱল ফোনত সংৰক্ষিত (DPDPA 2023)" else "Safe On Phone • DPDPA 2023",
                        fontSize = 13.sp,
                        color = AasritiColorTokens.DeepNortheastForest,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Plain Language Commitments Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = AasritiColorTokens.CardSurface,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.ParchmentBorder),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Commitment 1
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("🛡️", fontSize = 24.sp)
                        Column {
                            Text(
                                text = if (isAssamese) "আপোনাৰ নিয়ন্ত্ৰণ সদায় অটুট" else "You Stay in Control",
                                fontSize = 16.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.TextDark
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = if (isAssamese) {
                                    "আপোনাৰ স্মৃতি, কণ্ঠস্বৰৰ টোকা আৰু দৈনন্দিন স্বাস্থ্য তথ্য এই ডিভাইচতে সুৰক্ষিত থাকে। কোনো অনাহুত ব্যক্তিয়ে ইয়াক চাব নোৱাৰে।"
                                } else {
                                    "Your memories, voice notes, and cognitive game history stay strictly on this device. Only your family and chosen care team can see updates."
                                },
                                fontSize = 13.sp,
                                color = AasritiColorTokens.WarmSlate,
                                lineHeight = 19.sp
                            )
                        }
                    }

                    HorizontalDivider(color = AasritiColorTokens.ParchmentBorder.copy(alpha = 0.5f))

                    // Commitment 2
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("🏡", fontSize = 24.sp)
                        Column {
                            Text(
                                text = if (isAssamese) "মৰমৰ পৰিয়ালৰ যত্ন চক্ৰ" else "Loving Family Loop",
                                fontSize = 16.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.TextDark
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = if (isAssamese) {
                                    "দৈনন্দিন ঔষধ, পুষ্টি আৰু খেলৰ ফলাফল কেৱল মনোনীত যত্ন লওঁতা আৰু আশা কৰ্মীৰ সৈতে সমন্বয় কৰা হয়।"
                                } else {
                                    "Daily tea times, walks, and medication reminders are shared with your loved ones to preserve peace of mind."
                                },
                                fontSize = 13.sp,
                                color = AasritiColorTokens.WarmSlate,
                                lineHeight = 19.sp
                            )
                        }
                    }

                    HorizontalDivider(color = AasritiColorTokens.ParchmentBorder.copy(alpha = 0.5f))

                    // Commitment 3: Security & Diagnostic Neutrality
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("🩺", fontSize = 24.sp)
                        Column {
                            Text(
                                text = if (isAssamese) "চিকিৎসাগত নিৰপেক্ষতা" else "Non-Diagnostic Guidance",
                                fontSize = 16.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.TextDark
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = if (isAssamese) {
                                    "আশ্ৰীতিয়ে ডিমেনচিয়া নিদান নকৰে। ই স্মৃতিৰ সহায় আৰু আচৰণৰ প্ৰৱণতা বুজিবলৈ সহায়কহে।"
                                } else {
                                    "AASRITI provides neutral functional trends to assist clinicians; it does not generate automated dementia diagnoses."
                                },
                                fontSize = 13.sp,
                                color = AasritiColorTokens.WarmSlate,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Assent Pathway Selection (Direct Elder vs Proxy Caregiver)
            Text(
                text = if (isAssamese) "সন্মতিৰ পদ্ধতি বাছনি কৰক:" else "Select Assent Mode:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.TextDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Option 1: Elder Direct Assent
            Surface(
                onClick = { isElderAssent = true },
                shape = RoundedCornerShape(14.dp),
                color = AasritiColorTokens.CardSurface,
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isElderAssent) 1.5.dp else 1.dp,
                    color = if (isElderAssent) AasritiColorTokens.CrimsonDeep else AasritiColorTokens.ParchmentBorder
                ),
                shadowElevation = if (isElderAssent) 2.dp else 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(if (isElderAssent) AasritiColorTokens.CrimsonDeep else Color.Transparent)
                            .border(
                                1.5.dp,
                                if (isElderAssent) AasritiColorTokens.CrimsonDeep else AasritiColorTokens.ParchmentBorder,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isElderAssent) {
                            Text("✓", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isAssamese) "জ্যেষ্ঠ নাগৰিকৰ ব্যক্তিগত সন্মতি (Personal Assent)" else "Personal Assent",
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = if (isElderAssent) AasritiColorTokens.CrimsonDeep else AasritiColorTokens.TextDark
                        )
                        Text(
                            text = if (isAssamese) "মই স্বেচ্ছাই খেলিবলৈ আৰু ব্যৱহাৰ কৰিবলৈ সম্মত হৈছোঁ।" else "Direct elder assent with voluntary participation.",
                            fontSize = 12.sp,
                            color = AasritiColorTokens.WarmSlate
                        )
                    }
                    Text("❤️", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Option 2: Caregiver / Nominee Assent
            Surface(
                onClick = { isElderAssent = false },
                shape = RoundedCornerShape(14.dp),
                color = AasritiColorTokens.CardSurface,
                border = androidx.compose.foundation.BorderStroke(
                    width = if (!isElderAssent) 1.5.dp else 1.dp,
                    color = if (!isElderAssent) AasritiColorTokens.CrimsonDeep else AasritiColorTokens.ParchmentBorder
                ),
                shadowElevation = if (!isElderAssent) 2.dp else 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(if (!isElderAssent) AasritiColorTokens.CrimsonDeep else Color.Transparent)
                            .border(
                                1.5.dp,
                                if (!isElderAssent) AasritiColorTokens.CrimsonDeep else AasritiColorTokens.ParchmentBorder,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!isElderAssent) {
                            Text("✓", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isAssamese) "অভিভাৱক / যত্ন লওঁতাৰ সন্মতি (Caregiver Assent)" else "Caregiver Assent",
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = if (!isElderAssent) AasritiColorTokens.CrimsonDeep else AasritiColorTokens.TextDark
                        )
                        Text(
                            text = if (isAssamese) "পৰিয়ালৰ হৈ মই সুৰক্ষিতভাৱে যত্ন ল'বলৈ অনুমতি দিছোঁ।" else "Authorized family caregiver consenting on elder's behalf.",
                            fontSize = 12.sp,
                            color = AasritiColorTokens.WarmSlate
                        )
                    }
                    Text("🤝", fontSize = 18.sp)
                }
            }
        }

        // Action Deck (Continue vs Decline)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onConsentAccepted,
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.CrimsonDeep),
                shape = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAssamese) "সন্মত হৈ আগবাঢ়ক (Continue)" else "Continue",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("➔", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            OutlinedButton(
                onClick = onConsentDeclined,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.ParchmentBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = if (isAssamese) "উভতি যাওক (Back)" else "I don't wish to continue (Back)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AasritiColorTokens.WarmSlate
                )
            }
        }
    }
}
