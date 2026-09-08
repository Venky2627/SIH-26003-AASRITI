package com.sih26003.smritisetu.feature.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih26003.smritisetu.core.ui.theme.AasritiColorTokens
import com.sih26003.smritisetu.data.local.entities.DoctorAccessEntity
import com.sih26003.smritisetu.data.local.entities.GameSessionEntity
import com.sih26003.smritisetu.data.repository.DoctorAccessRepository
import com.sih26003.smritisetu.data.repository.GameRepository
import com.sih26003.smritisetu.data.repository.PatientRepository
import com.sih26003.smritisetu.demo.AasritiDemoData
import com.sih26003.smritisetu.demo.DemoStateHolder
import com.sih26003.smritisetu.domain.model.GameSession
import com.sih26003.smritisetu.engine.trend.TrendEngine
import kotlinx.coroutines.launch

/**
 * SCREEN_DOCTOR_PATIENT_SNAPSHOT:
 * Clinical longitudinal functional signals review.
 * Follows UI_SCREEN_SPEC.md:
 * - Medium-high information density for clinicians
 * - 7-day longitudinal reaction times and hesitation trends computed via TrendEngine from Room SQLite
 * - Explainable functional triage signals
 * - Strictly NEVER claims to diagnose dementia
 */
@Suppress("UNUSED_PARAMETER")
@Composable
fun DoctorAccessScreen(
    doctorAccessRepository: DoctorAccessRepository,
    patientRepository: PatientRepository,
    gameRepository: GameRepository,
    onBack: () -> Unit
) {
    val patientId = AasritiDemoData.patient.id
    val scope = rememberCoroutineScope()
    var accessCodeInput by remember { mutableStateOf("424242") }
    var approvedAccess by remember { mutableStateOf<Boolean>(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var clinicalNote by remember { mutableStateOf("Cognitive engagement stable; encourage daily reminiscence audio sessions.") }
    var noteSavedConfirmation by remember { mutableStateOf(false) }
    val isAssamese = DemoStateHolder.currentLanguage == "as"

    val realSessions by gameRepository.getSessionsForPatient(patientId).collectAsState(initial = emptyList())
    val computedTrend = remember(realSessions) {
        val domainSessions = realSessions.map {
            GameSession(
                id = it.id,
                patientId = it.patientId,
                gameId = it.gameId,
                difficultyLevel = it.difficultyLevel,
                accuracy = it.accuracy,
                reactionTimeMs = it.reactionTimeMs,
                hesitationCount = it.hesitationCount,
                errorCount = it.errors,
                durationMs = it.durationMs,
                timestamp = it.timestamp
            )
        }
        TrendEngine.compute7DaySignals(patientId, domainSessions)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AasritiColorTokens.WarmIvory)
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder)
            ) {
                Text(
                    text = if (isAssamese) "← উভতি যাওক" else "← Back",
                    color = AasritiColorTokens.DeepCharcoal,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "100% Offline • Room SQLite",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AasritiColorTokens.DeepNortheastForest
                )
            }

            if (approvedAccess) {
                Button(
                    onClick = { approvedAccess = false },
                    colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AasritiColorTokens.DeepCranberryEmergency)
                ) {
                    Text("প্ৰৱেশ সমাপ্ত (Exit)", color = AasritiColorTokens.DeepCranberryEmergency, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (!approvedAccess) {
            // 2. Doctor Access Code Entry View
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🩺", fontSize = 32.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isAssamese) "চিকিৎসকৰ নিৰীক্ষণ প্ৰৱেশ" else "Clinician Portal Access",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = AasritiColorTokens.DeepCharcoal,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isAssamese) {
                        "যত্ন লওঁতাই প্ৰদান কৰা ৬-অংকৰ প্ৰৱেশ সংকেত লিখক (বা ৪২৪২৪২ লিখক)।"
                    } else {
                        "Enter the 6-digit access code provided by caregiver (or 424242)."
                    },
                    fontSize = 14.sp,
                    color = AasritiColorTokens.WarmSlate,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = accessCodeInput,
                    onValueChange = {
                        if (it.length <= 6 && it.all { ch -> ch.isDigit() }) accessCodeInput = it
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(64.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal,
                        letterSpacing = 6.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AasritiColorTokens.DeepNortheastForest,
                        unfocusedBorderColor = AasritiColorTokens.WarmStoneBorder
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (accessCodeInput.length == 6) {
                            scope.launch {
                                val verify = doctorAccessRepository.verifyDoctorAccess(accessCodeInput)
                                if (verify != null || accessCodeInput == "424242") {
                                    approvedAccess = true
                                    errorMessage = null
                                } else {
                                    errorMessage = if (isAssamese) {
                                        "ভুল বা ম্যাদ উকলি যোৱা প্ৰৱেশ সংকেত। যত্ন লওঁতাৰ পৰা নতুন ক'ড লওক।"
                                    } else {
                                        "Invalid or expired access code. Request a new code from caregiver."
                                    }
                                }
                            }
                        } else {
                            errorMessage = if (isAssamese) "অনুগ্ৰহ কৰি ৬-অংকৰ ক'ড লিখক।" else "Please enter a 6-digit code."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(56.dp)
                ) {
                    Text(
                        text = if (isAssamese) "তথ্য চাওক (View Records) ➔" else "View Telemetry ➔",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.WarmIvory
                    )
                }

                errorMessage?.let { msg ->
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AasritiColorTokens.DeepCranberryEmergency.copy(alpha = 0.12f))
                            .border(1.dp, AasritiColorTokens.DeepCranberryEmergency, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "⚠️ $msg",
                            color = AasritiColorTokens.DeepCranberryEmergency,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        } else {
            // 3. Approved Longitudinal Clinical View
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Banner
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(AasritiColorTokens.SoftCream)
                            .border(1.5.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ৰোগী: আইতা বৰা (${AasritiDemoData.patient.pseudonymCode})",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AasritiColorTokens.DeepCharcoal
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("অনুমোদিত (Verified)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.DeepNortheastForest)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "লক্ষণীয়: এই তথ্য কেৱল জ্ঞানমূলক প্ৰতিক্ৰিয়া সময় আৰু পালনৰ হাৰ। কোনো চিকিৎসা নিদান নহয়।",
                                fontSize = 12.sp,
                                color = AasritiColorTokens.WarmSlate
                            )
                        }
                    }
                }

                if (realSessions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(AasritiColorTokens.SoftCream)
                                .border(1.5.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(18.dp))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📊", fontSize = 40.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "শেহতীয়া কোনো খেলৰ তথ্য উপলব্ধ নহয়।",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AasritiColorTokens.DeepCharcoal,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "No recent interaction data available.\n(ৰোগীয়ে খেল সম্পূৰ্ণ কৰাৰ পিছত প্ৰকৃত তথ্য ইয়াত প্ৰদৰ্শিত হ'ব।)",
                                    fontSize = 13.sp,
                                    color = AasritiColorTokens.WarmSlate,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                } else {
                    // 7-Day Longitudinal Signal Bars (Computed dynamically from Room SQLite sessions via TrendEngine)
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(AasritiColorTokens.SoftCream)
                                .border(1.5.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(18.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "৭-দিনীয়া অনুদৈৰ্ঘ্য প্ৰতিক্ৰিয়াৰ সময় (7-Day Trend)",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AasritiColorTokens.DeepCharcoal
                                    )
                                    Text(
                                        text = "${realSessions.size} সেশ্বন সংৰক্ষিত",
                                        fontSize = 12.sp,
                                        color = AasritiColorTokens.DeepNortheastForest,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))

                                computedTrend.dailyPoints.forEach { pt ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = pt.dayLabel,
                                            fontSize = 13.sp,
                                            color = AasritiColorTokens.DeepCharcoal,
                                            modifier = Modifier.width(90.dp)
                                        )

                                        // Bar indicating response time
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(12.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(AasritiColorTokens.WarmSunkenSurface)
                                        ) {
                                            if (pt.reactionTimeMs > 0) {
                                                val fillFraction = (pt.reactionTimeMs / 3500f).coerceIn(0.1f, 1f)
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxHeight()
                                                        .fillMaxWidth(fillFraction)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(
                                                        if (pt.hesitationGaps > 2) AasritiColorTokens.WarmAmberWarning else AasritiColorTokens.DeepNortheastForest
                                                    )
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Text(
                                            text = if (pt.reactionTimeMs > 0) "${pt.reactionTimeMs}ms" else "—",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (pt.reactionTimeMs > 0) AasritiColorTokens.WarmSlate else AasritiColorTokens.WarmSlate.copy(alpha = 0.5f),
                                            modifier = Modifier.width(60.dp),
                                            textAlign = TextAlign.End
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Explainable Triage Summary (Derived directly from TrendEngine computation)
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(AasritiColorTokens.SoftCream)
                                .border(1.5.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(18.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = "ব্যখ্যামূলক সংকেত (Explainable Functional Signal):",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AasritiColorTokens.DeepCharcoal
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                computedTrend.explainableSummary.forEach { summaryLine ->
                                    Text(
                                        text = "• $summaryLine",
                                        fontSize = 13.sp,
                                        color = AasritiColorTokens.DeepCharcoal,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Clinical Recommendation Notes Input
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(AasritiColorTokens.SoftCream)
                            .border(1.5.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "চিকিৎসকৰ পৰামৰ্শ (Clinical Recommendation Note):",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.DeepCharcoal
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = clinicalNote,
                                onValueChange = {
                                    clinicalNote = it
                                    noteSavedConfirmation = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3,
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = AasritiColorTokens.DeepCharcoal)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (noteSavedConfirmation) {
                                    Text("✓ সংৰক্ষিত হ'ল", color = AasritiColorTokens.DeepNortheastForest, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                } else {
                                    Spacer(modifier = Modifier.width(1.dp))
                                }

                                Button(
                                    onClick = { noteSavedConfirmation = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("পৰামৰ্শ সংৰক্ষণ (Save Note)", color = AasritiColorTokens.WarmIvory, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
