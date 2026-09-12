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

data class LanguageOption(
    val code: String,
    val nativeLabel: String,
    val englishLabel: String,
    val sampleGreeting: String
)

/**
 * SCREEN 4: Language Selection.
 * Faithfully matches prototype screen4.html.
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
                        val currentOpt = languages.firstOrNull { it.code == activeLang } ?: languages.first()
                        voicePromptManager.speak(currentOpt.sampleGreeting)
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
                text = if (isAssamese) "ভাষা নিৰ্বাচন কৰক" else "Choose Language",
                fontSize = 28.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.TextDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isAssamese) "জ্যেষ্ঠজনে যি ভাষাত সকলো নিৰ্দেশনা আৰু উৎসাহজনক কথা শুনিবলৈ ভাল পায়।"
                else "Select the language in which all voice guidance and memory cues will be spoken.",
                fontSize = 14.sp,
                color = AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.85f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Language Cards (Matching screen4.html)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                languages.forEach { lang ->
                    val isSelected = (activeLang == lang.code)
                    Surface(
                        onClick = {
                            activeLang = lang.code
                            onLanguageSelected(lang.code)
                            DemoStateHolder.currentLanguage = lang.code
                            voicePromptManager.setLanguage(lang.code)
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) AasritiColorTokens.CardSurface else AasritiColorTokens.ParchmentSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.5.dp,
                            color = if (isSelected) AasritiColorTokens.CrimsonDeep else AasritiColorTokens.ParchmentBorder
                        ),
                        shadowElevation = if (isSelected) 3.dp else 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = lang.nativeLabel,
                                    fontSize = 20.sp,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) AasritiColorTokens.CrimsonDeep else AasritiColorTokens.TextDark
                                )
                                Text(
                                    text = lang.englishLabel,
                                    fontSize = 13.sp,
                                    color = AasritiColorTokens.WarmSlate,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Spoken Audio Preview Button
                                IconButton(
                                    onClick = {
                                        voicePromptManager.setLanguage(lang.code)
                                        voicePromptManager.speak(lang.sampleGreeting)
                                    },
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(AasritiColorTokens.WarmSunkenSurface)
                                        .border(1.dp, AasritiColorTokens.MugaGold.copy(alpha = 0.5f), CircleShape)
                                ) {
                                    Text("📢", fontSize = 18.sp)
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
                                        Text("✓", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Action Deck (Continue & Back)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    onLanguageSelected(activeLang)
                    DemoStateHolder.currentLanguage = activeLang
                    voicePromptManager.setLanguage(activeLang)
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
