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

data class LanguageOption(
    val code: String,
    val nativeLabel: String,
    val englishLabel: String,
    val sampleGreeting: String
)

/**
 * SCREEN 4: Language Selection.
 * Matches prototype screen4.html.
 * Allows choosing regional audio packs (Assamese, English, Khasi, Meitei)
 * with instant audio previews.
 */
@Composable
fun LanguageSelectScreen(
    voicePromptManager: VoicePromptManager,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onContinueClicked: () -> Unit,
    onBackClicked: () -> Unit
) {
    var activeLang by remember { mutableStateOf(selectedLanguage.ifBlank { "as" }) }
    val isAssamese = activeLang == "as"

    val languages = listOf(
        LanguageOption(
            code = "as",
            nativeLabel = "অসমীয়া",
            englishLabel = "Assamese",
            sampleGreeting = "নমস্কাৰ, আপুনি কেনে আছে?"
        ),
        LanguageOption(
            code = "en",
            nativeLabel = "English",
            englishLabel = "Indian English",
            sampleGreeting = "Namaskar, welcome to AASRITI."
        ),
        LanguageOption(
            code = "kha",
            nativeLabel = "Ka Ktien Khasi",
            englishLabel = "Khasi (Meghalaya)",
            sampleGreeting = "Khublei shibun, welcome."
        ),
        LanguageOption(
            code = "mn",
            nativeLabel = "ꯃꯩꯇꯩꯂꯣꯟ",
            englishLabel = "Meitei (Manipur)",
            sampleGreeting = "ꯈꯨꯔꯨꯝꯖꯔꯤ, AASRITI-দা ꯇꯔꯥꯝꯅꯥ ꯑꯣꯛꯆꯔꯤ।"
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
                        val currentOpt = languages.firstOrNull { it.code == activeLang } ?: languages.first()
                        voicePromptManager.speak(currentOpt.sampleGreeting)
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
                text = if (isAssamese) "ভাষা নিৰ্বাচন কৰক" else "Choose Language",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.DeepCharcoal
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isAssamese) "জ্যেষ্ঠজনে যি ভাষাত সকলো নিৰ্দেশনা আৰু উৎসাহজনক কথা শুনিবলৈ ভাল পায়।"
                else "Select the language in which all voice guidance and memory cues will be spoken.",
                fontSize = 14.sp,
                color = AasritiColorTokens.WarmSlate
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Language Cards
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                languages.forEach { lang ->
                    val isSelected = (activeLang == lang.code)
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
                                activeLang = lang.code
                                onLanguageSelected(lang.code)
                                DemoStateHolder.currentLanguage = lang.code
                                voicePromptManager.setLanguage(lang.code)
                            }
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = lang.nativeLabel,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.DeepCharcoal
                                )
                                Text(
                                    text = lang.englishLabel,
                                    fontSize = 13.sp,
                                    color = AasritiColorTokens.WarmSlate
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Spoken Audio Preview Button
                                IconButton(
                                    onClick = {
                                        voicePromptManager.setLanguage(lang.code)
                                        voicePromptManager.speak(lang.sampleGreeting)
                                    },
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(AasritiColorTokens.WarmSunkenSurface)
                                ) {
                                    Text("📢", fontSize = 18.sp)
                                }

                                Spacer(modifier = Modifier.width(12.dp))

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
        }

        // Action CTA
        Button(
            onClick = {
                onLanguageSelected(activeLang)
                DemoStateHolder.currentLanguage = activeLang
                voicePromptManager.setLanguage(activeLang)
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
                text = if (isAssamese) "আগবাঢ়ক (Continue) ➔" else "Continue to Accessibility ➔",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.WarmIvory
            )
        }
    }
}
