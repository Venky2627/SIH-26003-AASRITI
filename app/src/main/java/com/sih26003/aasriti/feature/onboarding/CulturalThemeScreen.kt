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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih26003.aasriti.core.ui.components.AasritiAppBackground
import com.sih26003.aasriti.core.ui.components.AasritiLogoBadge
import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens
import com.sih26003.aasriti.demo.DemoStateHolder
import com.sih26003.aasriti.voice.playback.VoicePromptManager

data class CulturalThemeOption(
    val id: String,
    val regionSubtitle: String,
    val titleNative: String,
    val titleEnglish: String,
    val motifDescription: String,
    val accentColor: Color,
    val iconEmoji: String,
    val languageCode: String
)

/**
 * SCREEN 2: Cultural Theme Selection.
 * Faithfully matches prototype screen2.html.
 * Allows elders or caregivers to select authentic Northeast regional motifs
 * (Assam, Manipur, Meghalaya) to calibrate voice guidance and visual motifs.
 */
@Composable
fun CulturalThemeScreen(
    voicePromptManager: VoicePromptManager,
    selectedRegion: String,
    onRegionSelected: (String, String) -> Unit, // (regionId, languageCode)
    onContinueClicked: () -> Unit,
    onBackClicked: () -> Unit
) {
    val isAssamese = DemoStateHolder.currentLanguage == "as"
    var activeRegion by remember { mutableStateOf(selectedRegion.ifBlank { "ASSAM" }) }

    val themeOptions = listOf(
        CulturalThemeOption(
            id = "ASSAM",
            regionSubtitle = "Brahmaputra Valley",
            titleNative = "অসম (Assam)",
            titleEnglish = "Assam",
            motifDescription = "Phulam Gamusa & Jaapi motif",
            accentColor = AasritiColorTokens.CrimsonDeep,
            iconEmoji = "🦏",
            languageCode = "as"
        ),
        CulturalThemeOption(
            id = "MANIPUR",
            regionSubtitle = "Loktak Lakeside",
            titleNative = "মণিপুৰ (Manipur)",
            titleEnglish = "Manipur",
            motifDescription = "Moirang Phee temple motif",
            accentColor = AasritiColorTokens.SoftClay,
            iconEmoji = "🌸",
            languageCode = "mn"
        ),
        CulturalThemeOption(
            id = "MEGHALAYA",
            regionSubtitle = "Khasi Highlands",
            titleNative = "মেঘালয় (Meghalaya)",
            titleEnglish = "Meghalaya",
            motifDescription = "Khasi tribal banded weave",
            accentColor = AasritiColorTokens.SoftClay,
            iconEmoji = "🌲",
            languageCode = "kha"
        )
    )

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
            // Header with Back, Logo Badge, and Spoken Listen Button
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
                        val prompt = if (activeRegion == "ASSAM") "অসমীয়া সাংস্কৃতিক পৰিমণ্ডল বাছনি কৰক"
                        else if (activeRegion == "MANIPUR") "Choose Manipur cultural theme"
                        else "Choose Meghalaya cultural theme"
                        voicePromptManager.speak(prompt)
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
                text = if (isAssamese) "সাংস্কৃতিক পৰিমণ্ডল বাছক" else "Choose Theme",
                fontSize = 28.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.TextDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isAssamese) "আপোনাৰ চিনাকি পৰিৱেশ অনুসৰি দৃশ্যপট আৰু মাত নিৰ্বাচন কৰক"
                else "Select theme / language familiar to the elder",
                fontSize = 14.sp,
                color = AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.85f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Cultural Motif Cards (Matching screen2.html)
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                themeOptions.forEach { opt ->
                    val isSelected = (activeRegion == opt.id)
                    Surface(
                        onClick = {
                            activeRegion = opt.id
                            onRegionSelected(opt.id, opt.languageCode)
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = AasritiColorTokens.CardSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.5.dp,
                            color = if (isSelected) AasritiColorTokens.CrimsonDeep else AasritiColorTokens.ParchmentBorder
                        ),
                        shadowElevation = if (isSelected) 3.dp else 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            // Top Woven Motif Accent Stripe
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .background(
                                        if (isSelected) AasritiColorTokens.CrimsonDeep
                                        else opt.accentColor.copy(alpha = 0.25f)
                                    )
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Thumbnail Container
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(AasritiColorTokens.ParchmentSurface)
                                            .border(1.dp, AasritiColorTokens.ParchmentBorder, RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(opt.iconEmoji, fontSize = 28.sp)
                                    }

                                    Column {
                                        Text(
                                            text = opt.regionSubtitle.uppercase(),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                            color = if (isSelected) AasritiColorTokens.CrimsonDeep else opt.accentColor
                                        )
                                        Text(
                                            text = opt.titleEnglish,
                                            fontSize = 20.sp,
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            color = AasritiColorTokens.TextDark
                                        )
                                        Text(
                                            text = opt.motifDescription,
                                            fontSize = 12.sp,
                                            color = AasritiColorTokens.WarmSlate,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                // Selection Indicator Stamp
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) AasritiColorTokens.CrimsonDeep else Color.Transparent
                                        )
                                        .border(
                                            2.dp,
                                            if (isSelected) AasritiColorTokens.CrimsonDeep else AasritiColorTokens.ParchmentBorder,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Text(
                                            text = "✓",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Primary 64dp Elder-Friendly Touch Anchor (CONTINUE in CrimsonDeep)
        Button(
            onClick = {
                val opt = themeOptions.firstOrNull { it.id == activeRegion } ?: themeOptions.first()
                onRegionSelected(opt.id, opt.languageCode)
                onContinueClicked()
            },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.CrimsonDeep),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(top = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAssamese) "আগবাঢ়ক (CONTINUE)" else "CONTINUE",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("➔", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
}
