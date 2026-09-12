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
import com.sih26003.aasriti.core.ui.components.AasritiHeroBadge
import com.sih26003.aasriti.core.ui.components.CalmConnectivityPill
import com.sih26003.aasriti.core.ui.components.LanguageTogglePill
import com.sih26003.aasriti.core.ui.components.PhulamGamusaWovenBand
import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens
import com.sih26003.aasriti.demo.DemoStateHolder

/**
 * SCREEN 1: Open / Welcome Screen.
 * Faithful native Android implementation of prototype screen1.html.
 *
 * Visual Features:
 * - Vertical parchment background gradient (#FAF4ED via #F6EDE0 to #EDDCC5)
 * - Top utility bar: Language toggle pill + Calm "Saved Locally" reassurance pill
 * - Traditional Phulam Gamusa woven motif bands (top and bottom of hero block)
 * - Animated AASRITI hero badge with breathing ambient glow
 * - Headline typography in Serif Literata styling
 * - Dominant Primary CTA button in Deep Rich Crimson (#720227, height 64dp, WCAG AAA)
 * - Compact secondary role cards (ASHA / Caregiver, Doctor) with circular icon avatars
 * - Zero vertical dead space or bloating
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

    AasritiAppBackground {
        // Main Content Container
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Utility Bar: Language Selector & Calm Connectivity Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LanguageTogglePill(
                    currentLanguage = DemoStateHolder.currentLanguage,
                    onLanguageSelected = { DemoStateHolder.currentLanguage = it }
                )
                CalmConnectivityPill(isAssamese = isAssamese)
            }

            // 2. Traditional Phulam Gamusa Geometric Woven Accent Band (Top)
            PhulamGamusaWovenBand(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)
                    .padding(vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 3. Hero Identity Block with Animated Breathing Badge
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                AasritiHeroBadge(size = 104.dp, animated = true)

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "AASRITI",
                    fontSize = 32.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = AasritiColorTokens.DeepCharcoal,
                    letterSpacing = 2.sp
                )

                if (isAssamese) {
                    Text(
                        text = "আশ্ৰীতি • উত্তৰ-পূব ভাৰতৰ জ্যেষ্ঠসকলৰ AI স্মৃতি মঞ্চ",
                        fontSize = 13.sp,
                        color = AasritiColorTokens.WarmSlate,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    )
                } else {
                    Text(
                        text = "AI-Based Cognitive Gaming & Memory Assistance Platform",
                        fontSize = 13.sp,
                        color = AasritiColorTokens.WarmSlate,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 4. Traditional Phulam Gamusa Geometric Woven Accent Band (Bottom)
            PhulamGamusaWovenBand(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)
                    .padding(vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Primary Focal Area: Elder / Patient Safe Portal (Dominant Interaction)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = AasritiColorTokens.ParchmentSurface.copy(alpha = 0.95f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.ParchmentBorder),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Ambient corner glow inside card
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 20.dp, y = (-20).dp)
                            .clip(CircleShape)
                            .background(AasritiColorTokens.MugaGold.copy(alpha = 0.15f))
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        // The Signature Crimson CTA (min-height 64dp >= 64dp WCAG AAA)
                        Button(
                            onClick = onEnterClicked,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AasritiColorTokens.CrimsonDeep,
                                contentColor = AasritiColorTokens.ParchmentSurface
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 1.dp
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AasritiColorTokens.CrimsonBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "🌸",
                                        fontSize = 24.sp
                                    )
                                    Text(
                                        text = if (isAssamese) "প্ৰৱেশ কৰক (ENTER)" else "ENTER",
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.5.sp,
                                        color = AasritiColorTokens.ParchmentSurface
                                    )
                                }
                                Text(
                                    text = "➔",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AasritiColorTokens.ParchmentSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 6. Secondary Role Access Cards (Compact, large touch height >= 60dp, chevrons)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: ASHA / Caregiver
                Surface(
                    onClick = onCaregiverClicked,
                    shape = RoundedCornerShape(14.dp),
                    color = AasritiColorTokens.ParchmentSurface,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.ParchmentBorder),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Circular Icon Avatar
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(AasritiColorTokens.WarmSunkenSurface)
                                    .border(1.dp, AasritiColorTokens.MugaGold.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👥", fontSize = 20.sp)
                            }

                            Text(
                                text = if (isAssamese) "যত্ন লওঁতা / আশা কৰ্মী (ASHA / Caregiver)" else "ASHA / Caregiver",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.TextDark
                            )
                        }

                        Text(
                            text = "›",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AasritiColorTokens.WarmSlate
                        )
                    }
                }

                // Card 2: Doctor
                Surface(
                    onClick = onDoctorClicked,
                    shape = RoundedCornerShape(14.dp),
                    color = AasritiColorTokens.ParchmentSurface,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.ParchmentBorder),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Circular Icon Avatar
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(AasritiColorTokens.WarmSunkenSurface)
                                    .border(1.dp, AasritiColorTokens.SoftClay.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🩺", fontSize = 20.sp)
                            }

                            Text(
                                text = if (isAssamese) "চিকিৎসক (Doctor)" else "Doctor",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.TextDark
                            )
                        }

                        Text(
                            text = "›",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AasritiColorTokens.WarmSlate
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7. Discreet Navigation Link to Existing Profiles & Direct Roles
            Text(
                text = if (isAssamese) "বিদ্যমান জ্যেষ্ঠ প্ৰ'ফাইল আৰু পিন প্ৰৱেশ ➔" else "Existing Elder Profiles & Direct Roles ➔",
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
                color = AasritiColorTokens.WarmSlate.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}
