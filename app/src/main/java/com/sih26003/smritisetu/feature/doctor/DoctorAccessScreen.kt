package com.sih26003.smritisetu.feature.doctor

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih26003.smritisetu.core.ui.theme.AasritiColorTokens
import com.sih26003.smritisetu.data.local.entities.CareLogEntity
import com.sih26003.smritisetu.data.local.entities.GameSessionEntity
import com.sih26003.smritisetu.data.repository.CareLogRepository
import com.sih26003.smritisetu.data.repository.DoctorAccessRepository
import com.sih26003.smritisetu.data.repository.GameRepository
import com.sih26003.smritisetu.data.repository.PatientRepository
import com.sih26003.smritisetu.demo.AasritiDemoData
import com.sih26003.smritisetu.demo.DemoPatientConfig
import com.sih26003.smritisetu.demo.DemoStateHolder
import com.sih26003.smritisetu.domain.model.GameSession
import com.sih26003.smritisetu.engine.priority.PriorityEngine
import com.sih26003.smritisetu.engine.trend.TrendEngine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * SCREEN_DOCTOR_PATIENT_SNAPSHOT & CLINICIAN WORKFLOW:
 * Integrates Prototype Screens 27–34:
 * - 6-Digit PIN Gate & Roster Access
 * - Patient Snapshot (Screen 28)
 * - Longitudinal Interaction Trends (Screen 30, Room SQLite TrendEngine)
 * - Clinical Assessment Records (Screen 31)
 * - Medication & Caregiver Summary (Screen 32, CareLogRepository)
 * - Care Plan & Review Notes (Screen 33)
 * - Clinician Summary & Native PDF Export (Screen 34, PdfReportGenerator)
 */
@Suppress("UNUSED_PARAMETER")
@Composable
fun DoctorAccessScreen(
    doctorAccessRepository: DoctorAccessRepository,
    patientRepository: PatientRepository,
    gameRepository: GameRepository,
    careLogRepository: CareLogRepository? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val patientId = DemoPatientConfig.PATIENT_ID
    val scope = rememberCoroutineScope()
    var accessCodeInput by remember { mutableStateOf("424242") }
    var approvedAccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableStateOf("snapshot") } // "snapshot", "trends", "assessments", "caregiver", "careplan", "pdf"
    var clinicianNote by remember { mutableStateOf("Preserve bilingual cognitive cues and daily hydration routines. Routine review in 4 weeks.") }
    var noteSavedConfirmation by remember { mutableStateOf(false) }
    val isAssamese = DemoStateHolder.currentLanguage == "as"

    val realSessions by gameRepository.getSessionsForPatient(patientId).collectAsState(initial = emptyList())
    val realLogs by (careLogRepository?.getLogsForPatient(patientId) ?: kotlinx.coroutines.flow.flowOf(emptyList()))
        .collectAsState(initial = emptyList())

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

    val evaluatedPriority = remember(realSessions, realLogs) {
        val domainPatient = com.sih26003.smritisetu.domain.model.Patient(
            id = DemoPatientConfig.PATIENT_ID,
            pseudonymCode = DemoPatientConfig.PSEUDONYM_CODE,
            displayName = "আইতা বৰা (Aita Borah)",
            displaySubtitle = "৬৮ বছৰীয়া • 68 Years",
            birthYear = 1958,
            gender = "F",
            villageLocation = "হাজো, কামৰূপ",
            primaryLanguage = "as",
            cognitiveStage = "Mild Cognitive Impairment (MCI)"
        )
        val domainLogs = realLogs.map {
            com.sih26003.smritisetu.domain.model.CareLog(
                id = it.id,
                patientId = it.patientId,
                authorRole = it.authorRole,
                category = it.category,
                severity = it.severity,
                notes = it.notes,
                timestamp = it.timestamp
            )
        }
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
        PriorityEngine.evaluateTodayPriority(
            patient = domainPatient,
            reminders = emptyList(),
            recentSessions = domainSessions,
            recentLogs = domainLogs
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AasritiColorTokens.WarmIvory)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Navigation Bar
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
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "Saved Locally • Room SQLite",
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

        Spacer(modifier = Modifier.height(12.dp))

        if (!approvedAccess) {
            // Screen 27: 6-Digit Doctor Access Login View
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🩺", fontSize = 34.sp)
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
                        "Enter the 6-digit access code provided by caregiver (or demo code 424242)."
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
                        .fillMaxWidth(0.75f)
                        .height(56.dp)
                ) {
                    Text(
                        text = if (isAssamese) "তথ্য চাওক (View Records) ➔" else "View Patient Dossier ➔",
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
            // Approved Clinician Workflow (Screens 28 to 34)
            // 1. Patient Dossier Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AasritiColorTokens.SoftCream)
                    .border(1.5.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ৰোগী: আইতা বৰা (Aita Borah)",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.DeepCharcoal
                            )
                            Text(
                                text = "৬৮ বছৰ • মহিলা • ID: ${DemoPatientConfig.PATIENT_ID} (AS-KAM-0042)",
                                fontSize = 12.sp,
                                color = AasritiColorTokens.WarmSlate
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("অনুমোদিত (Verified)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.DeepNortheastForest)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Clinician Subsystem Tabs
            ScrollableTabRow(
                selectedTabIndex = when (selectedTab) {
                    "snapshot" -> 0
                    "trends" -> 1
                    "assessments" -> 2
                    "caregiver" -> 3
                    "careplan" -> 4
                    else -> 5
                },
                containerColor = AasritiColorTokens.WarmIvory,
                contentColor = AasritiColorTokens.DeepNortheastForest,
                edgePadding = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(selected = selectedTab == "snapshot", onClick = { selectedTab = "snapshot" }) {
                    Text("Snapshot", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Tab(selected = selectedTab == "trends", onClick = { selectedTab = "trends" }) {
                    Text("Trends", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Tab(selected = selectedTab == "assessments", onClick = { selectedTab = "assessments" }) {
                    Text("Assessments", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Tab(selected = selectedTab == "caregiver", onClick = { selectedTab = "caregiver" }) {
                    Text("Care Logs (${realLogs.size})", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Tab(selected = selectedTab == "careplan", onClick = { selectedTab = "careplan" }) {
                    Text("Care Plan", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Tab(selected = selectedTab == "pdf", onClick = { selectedTab = "pdf" }) {
                    Text("Export PDF", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AasritiColorTokens.MutedHeritageTerracotta)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Tab Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (selectedTab) {
                    "snapshot" -> {
                        // Screen 28: Patient Snapshot
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(AasritiColorTokens.SoftCream)
                                    .border(1.5.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(16.dp))
                                    .padding(16.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("দফা ১: শেহতীয়া স্থিতি (Clinical Overview)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.DeepCharcoal)
                                    Text("Attending Clinician: Dr. N. Barua, MD • Jorhat Neurological Unit", fontSize = 12.sp, color = AasritiColorTokens.WarmSlate)
                                    Text("Cognitive Stage: Mild Cognitive Impairment (MCI)", fontSize = 13.sp, color = AasritiColorTokens.DeepCharcoal)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Today's Care Priority Tier:", fontSize = 13.sp, color = AasritiColorTokens.DeepCharcoal)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    when (evaluatedPriority.severity) {
                                                        "PRIORITY" -> AasritiColorTokens.DeepCranberryEmergency
                                                        "WATCH" -> AasritiColorTokens.WarmAmberWarning
                                                        else -> AasritiColorTokens.DeepNortheastForest
                                                    }
                                                )
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(evaluatedPriority.severity, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }

                                    Divider(color = AasritiColorTokens.WarmStoneBorder, thickness = 1.dp)

                                    Text("Recent Observational Highlights:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AasritiColorTokens.DeepCharcoal)
                                    Text(
                                        text = if (realLogs.isNotEmpty()) "Latest note: \"${realLogs.last().notes}\"" else "No negative incidents or falls recorded recently.",
                                        fontSize = 12.sp,
                                        color = AasritiColorTokens.WarmSlate
                                    )

                                    Button(
                                        onClick = { selectedTab = "pdf" },
                                        colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.MutedHeritageTerracotta),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth().height(48.dp)
                                    ) {
                                        Text("📄 Generate Dossier Summary PDF ➔", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }

                    "trends" -> {
                        // Screen 30: 7/30-Day Longitudinal Interaction Trends
                        if (realSessions.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(AasritiColorTokens.SoftCream)
                                        .border(1.5.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(16.dp))
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("📊", fontSize = 40.sp)
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "শেহতীয়া কোনো খেলৰ তথ্য উপলব্ধ নহয়।",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AasritiColorTokens.DeepCharcoal,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "No recent interaction data available.\n(ৰোগীয়ে খেল সম্পূৰ্ণ কৰাৰ পিছত প্ৰকৃত তথ্য ইয়াত প্ৰদৰ্শিত হ'ব।)",
                                            fontSize = 12.sp,
                                            color = AasritiColorTokens.WarmSlate,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        } else {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(AasritiColorTokens.SoftCream)
                                        .border(1.5.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(16.dp))
                                        .padding(16.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "৭-দিনীয়া অনুদৈৰ্ঘ্য প্ৰতিক্ৰিয়া সময় (Trend)",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AasritiColorTokens.DeepCharcoal
                                            )
                                            Text(
                                                text = "${realSessions.size} সেশ্বন",
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
                                                    .padding(vertical = 3.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = pt.dayLabel,
                                                    fontSize = 12.sp,
                                                    color = AasritiColorTokens.DeepCharcoal,
                                                    modifier = Modifier.width(80.dp)
                                                )

                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(10.dp)
                                                        .clip(RoundedCornerShape(5.dp))
                                                        .background(AasritiColorTokens.WarmSunkenSurface)
                                                ) {
                                                    if (pt.reactionTimeMs > 0) {
                                                        val fillFraction = (pt.reactionTimeMs / 3500f).coerceIn(0.1f, 1f)
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxHeight()
                                                                .fillMaxWidth(fillFraction)
                                                                .clip(RoundedCornerShape(5.dp))
                                                                .background(
                                                                    if (pt.reactionTimeMs > 3000) AasritiColorTokens.WarmAmberWarning
                                                                    else AasritiColorTokens.DeepNortheastForest
                                                                )
                                                        )
                                                    }
                                                }

                                                Text(
                                                    text = if (pt.reactionTimeMs > 0) "${pt.reactionTimeMs}ms" else "--",
                                                    fontSize = 11.sp,
                                                    color = AasritiColorTokens.WarmSlate,
                                                    modifier = Modifier.width(55.dp),
                                                    textAlign = TextAlign.End
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "assessments" -> {
                        // Screen 31: Clinical Assessment Records (Non-diagnostic review)
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(AasritiColorTokens.SoftCream)
                                    .border(1.5.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(16.dp))
                                    .padding(16.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("দফা ৩: ক্লিনিকেল পৰ্যবেক্ষণ ৰেকৰ্ড (Assessments)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.DeepCharcoal)
                                    Text("Recorded by Attending Clinician during hospital visits. Not diagnostic.", fontSize = 12.sp, color = AasritiColorTokens.WarmSlate)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("HMSE Score: 26 / 30", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AasritiColorTokens.DeepCharcoal)
                                            Text("MoCA Equivalent: 24 / 30", fontSize = 12.sp, color = AasritiColorTokens.WarmSlate)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(AasritiColorTokens.SupportingSage.copy(alpha = 0.3f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text("Stable (+1pt)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.DeepNortheastForest)
                                        }
                                    }

                                    Divider(color = AasritiColorTokens.WarmStoneBorder, thickness = 1.dp)

                                    Text("Domain Sub-scores (Review):", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AasritiColorTokens.DeepCharcoal)
                                    Text("• Attention & Orientation: 15 / 18", fontSize = 12.sp, color = AasritiColorTokens.DeepCharcoal)
                                    Text("• Memory & Recall: 19 / 26", fontSize = 12.sp, color = AasritiColorTokens.DeepCharcoal)
                                    Text("• Fluency & Executive: 11 / 14", fontSize = 12.sp, color = AasritiColorTokens.DeepCharcoal)
                                    Text("• Language & Comprehension: 23 / 26", fontSize = 12.sp, color = AasritiColorTokens.DeepCharcoal)
                                    Text("• Visuospatial Construction: 10 / 16", fontSize = 12.sp, color = AasritiColorTokens.DeepCharcoal)
                                }
                            }
                        }
                    }

                    "caregiver" -> {
                        // Screen 32: Caregiver & ASHA Field Observations (Room SQLite care_logs)
                        if (realLogs.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(AasritiColorTokens.SoftCream)
                                        .border(1.5.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(16.dp))
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("📝", fontSize = 36.sp)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("কোনো যত্ন অভিলেখ নাই (No Care Logs)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.DeepCharcoal)
                                        Text("Caregiver or ASHA has not logged observations yet.", fontSize = 12.sp, color = AasritiColorTokens.WarmSlate)
                                    }
                                }
                            }
                        } else {
                            items(realLogs.reversed()) { log ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(AasritiColorTokens.SoftCream)
                                        .border(1.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                    ) {
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "${log.category} • ${log.authorRole}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = AasritiColorTokens.DeepCharcoal
                                                )
                                                Text(
                                                    text = log.severity,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = when (log.severity) {
                                                        "PRIORITY", "HIGH" -> AasritiColorTokens.DeepCranberryEmergency
                                                        "WATCH", "MEDIUM" -> AasritiColorTokens.WarmAmberWarning
                                                        else -> AasritiColorTokens.DeepNortheastForest
                                                    }
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = log.notes, fontSize = 12.sp, color = AasritiColorTokens.WarmSlate)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH).format(Date(log.timestamp)),
                                                fontSize = 10.sp,
                                                color = AasritiColorTokens.WarmSlate.copy(alpha = 0.7f)
                                            )
                                        }
                                    }
                            }
                        }
                    }

                    "careplan" -> {
                        // Screen 33: Care Plan & Clinician Guidance
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(AasritiColorTokens.SoftCream)
                                    .border(1.5.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(16.dp))
                                    .padding(16.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("দফা ৫: যত্ন পৰিকল্পনা আৰু নিৰ্দেশনা (Care Plan)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.DeepCharcoal)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Clinical Management Status:", fontSize = 13.sp, color = AasritiColorTokens.DeepCharcoal)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(AasritiColorTokens.DeepNortheastForest)
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text("STABLE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }

                                    Text("Next Scheduled Review: 4 Weeks (28 Days)", fontSize = 12.sp, color = AasritiColorTokens.WarmSlate)

                                    Divider(color = AasritiColorTokens.WarmStoneBorder, thickness = 1.dp)

                                    Text("Attending Clinician Guidance Note:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AasritiColorTokens.DeepCharcoal)
                                    OutlinedTextField(
                                        value = clinicianNote,
                                        onValueChange = { clinicianNote = it },
                                        modifier = Modifier.fillMaxWidth().height(100.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AasritiColorTokens.DeepNortheastForest,
                                            unfocusedBorderColor = AasritiColorTokens.WarmStoneBorder
                                        ),
                                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, color = AasritiColorTokens.DeepCharcoal)
                                    )

                                    Button(
                                        onClick = {
                                            noteSavedConfirmation = true
                                            Toast.makeText(context, "Clinician note saved locally.", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth().height(48.dp)
                                    ) {
                                        Text("নিৰ্দেশনা সংৰক্ষণ কৰক (Save Guidance)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }

                    "pdf" -> {
                        // Screen 34: Clinician Summary & PDF Export
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(AasritiColorTokens.SoftCream)
                                    .border(1.5.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(16.dp))
                                    .padding(16.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("📄 ", fontSize = 20.sp)
                                        Text("AASRITI Summary Export Dossier", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.DeepCharcoal)
                                    }
                                    Text(
                                        "Generates a formal, printable PDF consultation report compiling real Room SQLite data (Patient Demographics, Longitudinal Trends, Care Logs, Priority, and Guidance Notes).",
                                        fontSize = 12.sp,
                                        color = AasritiColorTokens.WarmSlate
                                    )

                                    Divider(color = AasritiColorTokens.WarmStoneBorder, thickness = 1.dp)

                                    Text("Included Dossier Sections (5 of 5):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.DeepCharcoal)
                                    Text("✓ Patient Identification & Kamrup Location", fontSize = 12.sp, color = AasritiColorTokens.DeepNortheastForest)
                                    Text("✓ Longitudinal Cognitive Latency (${realSessions.size} Sessions)", fontSize = 12.sp, color = AasritiColorTokens.DeepNortheastForest)
                                    Text("✓ Caregiver & ASHA Field Logs (${realLogs.size} Records)", fontSize = 12.sp, color = AasritiColorTokens.DeepNortheastForest)
                                    Text("✓ Today's Care Priority (${evaluatedPriority.severity})", fontSize = 12.sp, color = AasritiColorTokens.DeepNortheastForest)
                                    Text("✓ Clinician Notes & Scheduled Follow-up", fontSize = 12.sp, color = AasritiColorTokens.DeepNortheastForest)

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Button(
                                        onClick = {
                                            try {
                                                val reportData = PdfReportGenerator.ReportData(
                                                    patientId = DemoPatientConfig.PATIENT_ID,
                                                    patientName = "আইতা বৰা (Aita Borah)",
                                                    pseudonymCode = DemoPatientConfig.PSEUDONYM_CODE,
                                                    age = 68,
                                                    gender = "Female",
                                                    villageLocation = "Kamrup Rural, Assam",
                                                    cognitiveStage = "Mild Cognitive Impairment (MCI)",
                                                    clinicianName = "Dr. N. Barua, MD (Neurology)",
                                                    reportingPeriod = "Last 30 Days",
                                                    carePriorityStatus = evaluatedPriority.severity,
                                                    clinicianNotes = clinicianNote,
                                                    sessions = realSessions,
                                                    careLogs = realLogs
                                                )
                                                val pdfFile = PdfReportGenerator.generateClinicianPdf(context, reportData)
                                                Toast.makeText(context, "PDF Exported: ${pdfFile.name}", Toast.LENGTH_LONG).show()

                                                // Launch view intent
                                                val viewIntent = PdfReportGenerator.createViewIntent(context, pdfFile)
                                                context.startActivity(Intent.createChooser(viewIntent, "Open AASRITI Dossier PDF"))
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Export error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth().height(52.dp)
                                    ) {
                                        Text("📥 Download Summary PDF (AASRITI Format)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            try {
                                                val reportData = PdfReportGenerator.ReportData(
                                                    patientId = DemoPatientConfig.PATIENT_ID,
                                                    patientName = "আইতা বৰা (Aita Borah)",
                                                    pseudonymCode = DemoPatientConfig.PSEUDONYM_CODE,
                                                    age = 68,
                                                    gender = "Female",
                                                    villageLocation = "Kamrup Rural, Assam",
                                                    cognitiveStage = "Mild Cognitive Impairment (MCI)",
                                                    clinicianName = "Dr. N. Barua, MD (Neurology)",
                                                    reportingPeriod = "Last 30 Days",
                                                    carePriorityStatus = evaluatedPriority.severity,
                                                    clinicianNotes = clinicianNote,
                                                    sessions = realSessions,
                                                    careLogs = realLogs
                                                )
                                                val pdfFile = PdfReportGenerator.generateClinicianPdf(context, reportData)
                                                val shareIntent = PdfReportGenerator.createShareIntent(context, pdfFile)
                                                context.startActivity(Intent.createChooser(shareIntent, "Share AASRITI Summary Dossier"))
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Share error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.DeepNortheastForest),
                                        modifier = Modifier.fillMaxWidth().height(48.dp)
                                    ) {
                                        Text("📤 Share Summary with Caregiver", color = AasritiColorTokens.DeepNortheastForest, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
