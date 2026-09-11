package com.sih26003.aasriti.feature.onboarding

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih26003.aasriti.R
import com.sih26003.aasriti.core.ui.components.CalmConnectivityPill
import com.sih26003.aasriti.core.ui.components.LanguageTogglePill
import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens
import com.sih26003.aasriti.demo.DemoStateHolder

/**
 * SCREEN 1: Open / Welcome Screen.
 * Authoritative entry point matching prototype screen1.html.
 * Welcomes the elder with authentic terracotta spiral branding,
 * language toggle, offline reassurance, and clear role portals.
 */
@Composable
fun WelcomeScreen(
    onEnterClicked: () -> Unit,
    onCaregiverClicked: () -> Unit,
    onDoctorClicked: () -> Unit,
    onDirectProfilesClicked: () -> Unit
) {
    val isAssamese = DemoStateHolder.currentLanguage == "as"
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AasritiColorTokens.WarmIvory)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Utility Bar: Language Selector & Calm Connectivity Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LanguageTogglePill(
                    currentLanguage = DemoStateHolder.currentLanguage,
                    onLanguageSelected = { DemoStateHolder.currentLanguage = it }
                )
                CalmConnectivityPill(isAssamese = isAssamese)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Authentic AASRITI Spiral Logo in Circular Cream Badge
            Box(
                modifier = Modifier
                    .size(116.dp)
                    .clip(CircleShape)
                    .background(AasritiColorTokens.SoftCream)
                    .border(2.5.dp, AasritiColorTokens.WarmStoneBorder, CircleShape)
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_aasriti_logo),
                    contentDescription = "AASRITI Terracotta Spiral Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // App Title & Tagline
            Text(
                text = "আশ্ৰীতি (AASRITI)",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.DeepCharcoal,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isAssamese) {
                    "উত্তৰ-পূব ভাৰতৰ জ্যেষ্ঠসকলৰ বাবে AI-ভিত্তিক স্মৃতি আৰু যত্ন মঞ্চ"
                } else {
                    "AI-Based Cognitive Gaming & Memory Assistance Platform"
                },
                fontSize = 13.sp,
                color = AasritiColorTokens.WarmSlate,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Focal Area: Elder / Patient Safe Portal (Dominant Interaction)
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = AasritiColorTokens.SoftCream),
                border = androidx.compose.foundation.BorderStroke(2.dp, AasritiColorTokens.WarmStoneBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isAssamese) "জ্যেষ্ঠ নাগৰিকৰ নিৰাপদ প্ৰৱেশ" else "Elder & Family Safe Portal",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AasritiColorTokens.WarmSlate
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onEnterClicked,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🌸", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = if (isAssamese) "আৰম্ভ কৰক (ENTER)" else "ENTER",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AasritiColorTokens.WarmIvory
                                )
                            }
                            Text("➔", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.WarmIvory)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Secondary Access Portals
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // ASHA / Caregiver Button
                Button(
                    onClick = onCaregiverClicked,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🤝", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isAssamese) "যত্ন লওঁতা / আশা কৰ্মী (ASHA / Caregiver)" else "ASHA / Family Caregiver",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AasritiColorTokens.DeepCharcoal
                            )
                        }
                        Text("➔", fontSize = 16.sp, color = AasritiColorTokens.WarmSlate)
                    }
                }

                // Doctor Button
                Button(
                    onClick = onDoctorClicked,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🩺", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isAssamese) "চিকিৎসকৰ পৰিদৰ্শন (Doctor Access)" else "Doctor / Clinician Portal",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AasritiColorTokens.DeepCharcoal
                            )
                        }
                        Text("➔", fontSize = 16.sp, color = AasritiColorTokens.WarmSlate)
                    }
                }
            }
        }

        // Footer: Existing profiles shortcut
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isAssamese) "বাছনি কৰা প্ৰ'ফাইললৈ যাওক (Role Select) ➔" else "Existing Elder Profiles & Roles ➔",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = AasritiColorTokens.DeepNortheastForest,
                modifier = Modifier
                    .clickable { onDirectProfilesClicked() }
                    .padding(8.dp)
            )

            Text(
                text = "AASRITI • SIH-26003 • 100% Offline Single Source of Truth",
                fontSize = 11.sp,
                color = AasritiColorTokens.WarmSlate.copy(alpha = 0.7f)
            )
        }
    }
}
