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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih26003.aasriti.core.ui.components.AasritiLogoBadge
import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens
import com.sih26003.aasriti.demo.DemoStateHolder
import com.sih26003.aasriti.voice.playback.VoicePromptManager

data class CulturalThemeOption(
    val id: String,
    val titleNative: String,
    val titleEnglish: String,
    val description: String,
    val iconEmoji: String,
    val languageCode: String
)

/**
 * SCREEN 2: Cultural Theme Selection.
 * Matches prototype screen2.html.
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
            titleNative = "অসম (Assam)",
            titleEnglish = "Assam Brahmaputra Valley",
            description = "Phulam Gamusa diamond motifs, Kaziranga flora, and Assamese voice guidance.",
            iconEmoji = "🦏",
            languageCode = "as"
        ),
        CulturalThemeOption(
            id = "MANIPUR",
            titleNative = "মণিপুৰ (Manipur)",
            titleEnglish = "Manipur Imphal Valley",
            description = "Moirang Phee temple motifs, Loktak water blooms, and Meitei voice guidance.",
            iconEmoji = "🌸",
            languageCode = "mn"
        ),
        CulturalThemeOption(
            id = "MEGHALAYA",
            titleNative = "মেঘালয় (Meghalaya)",
            titleEnglish = "Meghalaya Pine Hills",
            description = "Khasi woven bamboo patterns, living root bridges, and Khasi voice guidance.",
            iconEmoji = "🌲",
            languageCode = "kha"
        )
    )

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
            // Header with Back and Logo Badge
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
                        val prompt = if (activeRegion == "ASSAM") "অসমীয়া সাংস্কৃতিক পৰিমণ্ডল বাছনি কৰক"
                        else if (activeRegion == "MANIPUR") "Choose Manipur cultural theme"
                        else "Choose Meghalaya cultural theme"
                        voicePromptManager.speak(prompt)
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
                text = if (isAssamese) "সাংস্কৃতিক পৰিমণ্ডল বাছক" else "Choose Cultural Theme",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.DeepCharcoal
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isAssamese) "আপোনাৰ চিনাকি পৰিৱেশ অনুসৰি দৃশ্যপট আৰু মাত নিৰ্বাচন কৰক"
                else "Select authentic regional motifs and vocal warmth familiar to the elder.",
                fontSize = 14.sp,
                color = AasritiColorTokens.WarmSlate
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Theme Cards List
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                themeOptions.forEach { opt ->
                    val isSelected = (activeRegion == opt.id)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (isSelected) AasritiColorTokens.SoftCream else AasritiColorTokens.WarmIvory)
                            .border(
                                width = if (isSelected) 2.5.dp else 1.5.dp,
                                color = if (isSelected) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.WarmStoneBorder,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable {
                                activeRegion = opt.id
                                onRegionSelected(opt.id, opt.languageCode)
                            }
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.15f)
                                            else AasritiColorTokens.WarmSunkenSurface
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(opt.iconEmoji, fontSize = 28.sp)
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Text(
                                        text = opt.titleNative,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.DeepCharcoal
                                    )
                                    Text(
                                        text = opt.titleEnglish,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AasritiColorTokens.WarmSlate
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = opt.description,
                                        fontSize = 12.sp,
                                        color = AasritiColorTokens.DeepCharcoal.copy(alpha = 0.8f),
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) AasritiColorTokens.DeepNortheastForest else Color.Transparent
                                    )
                                    .border(
                                        2.dp,
                                        if (isSelected) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.WarmStoneBorder,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Text("✓", color = AasritiColorTokens.WarmIvory, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Action CTA
        Button(
            onClick = {
                val opt = themeOptions.firstOrNull { it.id == activeRegion } ?: themeOptions.first()
                onRegionSelected(opt.id, opt.languageCode)
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
                text = if (isAssamese) "আগবাঢ়ক (Continue) ➔" else "Continue to Language ➔",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.WarmIvory
            )
        }
    }
}
