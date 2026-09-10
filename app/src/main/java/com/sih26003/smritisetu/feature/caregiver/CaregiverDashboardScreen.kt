package com.sih26003.smritisetu.feature.caregiver

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sih26003.smritisetu.SmritiSetuApplication
import com.sih26003.smritisetu.core.security.CryptoUtils
import com.sih26003.smritisetu.core.ui.theme.AasritiColorTokens
import com.sih26003.smritisetu.data.local.entities.CareLogEntity
import com.sih26003.smritisetu.data.local.entities.DoctorAccessEntity
import com.sih26003.smritisetu.data.repository.CareLogRepository
import com.sih26003.smritisetu.data.repository.DoctorAccessRepository
import com.sih26003.smritisetu.data.repository.GameRepository
import com.sih26003.smritisetu.data.repository.PatientRepository
import com.sih26003.smritisetu.data.repository.ReminderRepository
import com.sih26003.smritisetu.demo.AasritiDemoData
import com.sih26003.smritisetu.demo.DemoPatientConfig
import com.sih26003.smritisetu.demo.DemoStateHolder
import com.sih26003.smritisetu.domain.model.CareLog
import com.sih26003.smritisetu.domain.model.GameSession
import com.sih26003.smritisetu.domain.model.Reminder
import com.sih26003.smritisetu.engine.priority.PriorityEngine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * SCREEN_CAREGIVER_DASHBOARD, SCREEN_CAREGIVER_QUICK_LOG & SCREEN_CAREGIVER_CARE_HISTORY:
 * Room-backed family caregiver and health observer management center.
 * Follows UI_SCREEN_SPEC.md & UI_RULES.md:
 * - Patient status header with canonical DemoPatientConfig.PATIENT_ID ("aita_borah_01")
 * - 100% Offline Room SQLite Local Source of Truth
 * - Reactive Today's Priority Card evaluated by PriorityEngine
 * - Legitimate Quick Care Log (<30s entry) persisting into Room care_logs
 * - Full chronological Care History viewer with category filters and honest empty states
 * - Doctor Access Code generator
 */
@Suppress("UNUSED_PARAMETER")
@Composable
fun CaregiverDashboardScreen(
    patientRepository: PatientRepository,
    gameRepository: GameRepository,
    doctorAccessRepository: DoctorAccessRepository,
    careLogRepository: CareLogRepository,
    onOpenReminders: (String) -> Unit,
    onLogout: () -> Unit,
    reminderRepository: ReminderRepository? = null
) {
    val context = LocalContext.current
    val app = context.applicationContext as? SmritiSetuApplication
    val resolvedReminderRepo = reminderRepository ?: app?.reminderRepository

    val patient = remember { AasritiDemoData.patient }
    val scope = rememberCoroutineScope()
    val isAssamese = DemoStateHolder.currentLanguage == "as"

    val realSessions by gameRepository.getSessionsForPatient(patient.id).collectAsState(initial = emptyList())
    val realCareLogs by careLogRepository.getLogsForPatient(patient.id).collectAsState(initial = emptyList())
    val roomReminders by (resolvedReminderRepo?.getActiveReminders(patient.id)?.collectAsState(initial = emptyList())
        ?: remember { mutableStateOf(emptyList()) })

    val reminders = remember(roomReminders, DemoStateHolder.completedRoutineIds.size) {
        roomReminders.map { entity ->
            Reminder(
                id = entity.id,
                patientId = entity.patientId,
                titleIndic = entity.title,
                titleEn = entity.title,
                timeLabel = "${entity.hour}:${if (entity.minute < 10) "0" else ""}${entity.minute}",
                isMedicine = entity.reminderType.equals("MEDICINE", ignoreCase = true),
                isCompleted = DemoStateHolder.completedRoutineIds.contains(entity.id)
            )
        }
    }

    val evaluatedPriority = remember(realSessions, realCareLogs, reminders) {
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
        val domainLogs = realCareLogs.map {
            CareLog(
                id = it.id,
                patientId = it.patientId,
                authorRole = it.authorRole,
                category = it.category,
                severity = it.severity,
                notes = it.notes,
                timestamp = it.timestamp
            )
        }
        val domainPatient = com.sih26003.smritisetu.domain.model.Patient(
            id = patient.id,
            pseudonymCode = patient.pseudonymCode,
            displayName = patient.displayName,
            displaySubtitle = patient.displaySubtitle,
            birthYear = 1958,
            gender = "F",
            villageLocation = patient.villageLocation,
            primaryLanguage = "as",
            cognitiveStage = patient.cognitiveStage
        )
        PriorityEngine.evaluateTodayPriority(
            patient = domainPatient,
            reminders = reminders,
            recentSessions = domainSessions,
            recentLogs = domainLogs
        )
    }

    var selectedTab by remember { mutableStateOf(0) } // 0: Overview, 1: Care History
    var showQuickLogDialog by remember { mutableStateOf(false) }
    var generatedDoctorCode by remember { mutableStateOf<String?>("424242") }
    var historyFilterCategory by remember { mutableStateOf("ALL") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AasritiColorTokens.WarmIvory)
            .padding(18.dp)
    ) {
        // 1. Top Header & Exit
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isAssamese) "যত্ন লওঁতাৰ ডেশ্ববৰ্ড" else "Caregiver Dashboard",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = AasritiColorTokens.DeepCharcoal
                )
                Text(
                    text = "মীৰা বৰা (Mira Borah • Daughter)",
                    fontSize = 13.sp,
                    color = AasritiColorTokens.DeepNortheastForest,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "100% Offline • Room SQLite Local Source of Truth",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepNortheastForest
                    )
                }
            }

            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text("প্ৰস্থান (Logout)", color = AasritiColorTokens.WarmAmberWarning, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Tab Navigation Switcher (Overview vs Care History)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(AasritiColorTokens.SoftCream)
                .border(1.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(9.dp))
                    .background(if (selectedTab == 0) AasritiColorTokens.DeepNortheastForest else Color.Transparent)
                    .clickable { selectedTab = 0 }
                    .heightIn(min = 48.dp)
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isAssamese) "আজিৰ অগ্ৰগতি (Overview)" else "Today Overview",
                    color = if (selectedTab == 0) AasritiColorTokens.WarmIvory else AasritiColorTokens.DeepCharcoal,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(9.dp))
                    .background(if (selectedTab == 1) AasritiColorTokens.DeepNortheastForest else Color.Transparent)
                    .clickable { selectedTab = 1 }
                    .heightIn(min = 48.dp)
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isAssamese) "যত্ন ইতিহাস (Care History)" else "Care History",
                        color = if (selectedTab == 1) AasritiColorTokens.WarmIvory else AasritiColorTokens.DeepCharcoal,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (realCareLogs.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (selectedTab == 1) AasritiColorTokens.WarmIvory else AasritiColorTokens.DeepNortheastForest)
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = realCareLogs.size.toString(),
                                color = if (selectedTab == 1) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.WarmIvory,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedTab == 0) {
            // ==========================================
            // TAB 0: TODAY OVERVIEW
            // ==========================================
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Patient Status Header Card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(AasritiColorTokens.SoftCream)
                            .border(1.5.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(AasritiColorTokens.MutedHeritageTerracotta.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("👵", fontSize = 30.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = patient.displayName,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AasritiColorTokens.DeepCharcoal
                                    )
                                    Text(
                                        text = "${patient.displaySubtitle} • ${patient.pseudonymCode}",
                                        fontSize = 12.sp,
                                        color = AasritiColorTokens.WarmSlate
                                    )
                                    Text(
                                        text = patient.villageLocation,
                                        fontSize = 12.sp,
                                        color = AasritiColorTokens.WarmSlate
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AasritiColorTokens.SupportingSage.copy(alpha = 0.25f))
                                    .border(1.dp, AasritiColorTokens.DeepNortheastForest, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "● স্থানীয় অফলাইন",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AasritiColorTokens.DeepNortheastForest
                                )
                            }
                        }
                    }
                }

                // 2. Today's Priority Card (Evaluated reactively by PriorityEngine)
                item {
                    val priorityColor = when (evaluatedPriority.severity) {
                        "URGENT" -> AasritiColorTokens.DeepCranberryEmergency
                        "PRIORITY" -> AasritiColorTokens.MutedHeritageTerracotta
                        "WATCH" -> AasritiColorTokens.MugaGold
                        else -> AasritiColorTokens.DeepNortheastForest
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(AasritiColorTokens.SoftCream)
                            .border(
                                width = 2.dp,
                                color = priorityColor,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(priorityColor)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isAssamese) evaluatedPriority.titleIndic else evaluatedPriority.titleEn,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = priorityColor
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(priorityColor.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = evaluatedPriority.severity,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = priorityColor
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (isAssamese) evaluatedPriority.explanationIndic else evaluatedPriority.explanationEn,
                                fontSize = 14.sp,
                                color = AasritiColorTokens.DeepCharcoal,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            val pendingReminder = reminders.firstOrNull { it.isMedicine && !it.isCompleted }
                                ?: reminders.firstOrNull { !it.isCompleted }

                            if (pendingReminder != null) {
                                Button(
                                    onClick = {
                                        DemoStateHolder.toggleRoutine(pendingReminder.id)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = priorityColor),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(64.dp)
                                ) {
                                    Text(
                                        text = "কৰণীয়: ${evaluatedPriority.suggestedAction}",
                                        color = AasritiColorTokens.WarmIvory,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else if (reminders.isEmpty()) {
                                Button(
                                    onClick = { onOpenReminders(patient.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = priorityColor),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(64.dp)
                                ) {
                                    Text(
                                        text = if (isAssamese) "⏰ সোঁৱৰণী নিৰ্ধাৰণ কৰক (Set Reminders)" else "⏰ Set Reminders",
                                        color = AasritiColorTokens.WarmIvory,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { /* All current reminders completed */ },
                                    enabled = false,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.6f),
                                        disabledContainerColor = AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.2f),
                                        disabledContentColor = AasritiColorTokens.DeepNortheastForest
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(64.dp)
                                ) {
                                    Text(
                                        text = if (isAssamese) "✓ সকলো নিয়ম সম্পন্ন (All Routines Done)" else "✓ All Routines Done",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Daily Routine Progress Strip
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
                                text = "দৈনন্দিন অগ্ৰগতি (Daily Care Progress)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.DeepCharcoal
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            if (reminders.isEmpty()) {
                                Text(
                                    text = if (isAssamese) "কোনো সক্ৰিয় সোঁৱৰণী নিৰ্ধাৰণ কৰা নাই (No active reminders scheduled)" else "No active reminders scheduled",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AasritiColorTokens.WarmSlate
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isAssamese)
                                        "দৈনন্দিন ঔষধ বা নিয়মৰ সময় নিৰ্ধাৰণ কৰিবলৈ তলৰ '⏰ সোঁৱৰণী' ব্যৱহাৰ কৰক।"
                                    else
                                        "Use '⏰ Reminders' below to schedule daily medicine and routine alarms.",
                                    fontSize = 12.sp,
                                    color = AasritiColorTokens.WarmSlate
                                )
                            } else {
                                val totalRoutines = reminders.size
                                val doneCount = reminders.count { it.isCompleted }
                                val percent = if (totalRoutines > 0) (doneCount * 100) / totalRoutines else 0

                                Text(
                                    text = "নিয়ম পালন: $doneCount / $totalRoutines সম্পন্ন ($percent%)",
                                    fontSize = 13.sp,
                                    color = AasritiColorTokens.WarmSlate
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                LinearProgressIndicator(
                                    progress = { if (totalRoutines > 0) doneCount.toFloat() / totalRoutines.toFloat() else 0f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(5.dp)),
                                    color = AasritiColorTokens.DeepNortheastForest,
                                    trackColor = AasritiColorTokens.WarmSunkenSurface
                                )
                            }
                        }
                    }
                }

                // 4. Quick Action Grid (Large Touch Targets >= 64dp)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showQuickLogDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp)
                        ) {
                            Text(
                                text = "+ খৰতকীয়া টোকা (Log)",
                                color = AasritiColorTokens.WarmIvory,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { onOpenReminders(patient.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp)
                        ) {
                            Text(
                                text = "⏰ সোঁৱৰণী (Reminders)",
                                color = AasritiColorTokens.DeepCharcoal,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 5. Cognitive Activity & Interaction Telemetry Summary Card (Room SQLite GameSession Source of Truth)
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🧠", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isAssamese) "খেল আৰু মানসিক সক্ৰিয়তা" else "Cognitive Game Activity",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AasritiColorTokens.DeepCharcoal
                                    )
                                }
                                if (realSessions.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.12f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${realSessions.size} খেল (Sessions)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AasritiColorTokens.DeepNortheastForest
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (realSessions.isEmpty()) {
                                Text(
                                    text = if (isAssamese) "কোনো খেলৰ তথ্য এতিয়ালৈকে উপলব্ধ নহয়" else "No game sessions recorded yet",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AasritiColorTokens.WarmSlate
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isAssamese)
                                        "আইতাই 'পৰিয়ালৰ স্মৃতি' (Family Trivia) বা আন খেলসমূহ খেলিলে ইয়াত প্ৰতিক্ৰিয়া সময় আৰু অগ্ৰগতি প্ৰদৰ্শিত হ'ব।"
                                    else
                                        "When elder plays cognitive games like Family Trivia, response latency and pacing metrics will appear here.",
                                    fontSize = 12.sp,
                                    color = AasritiColorTokens.WarmSlate
                                )
                            } else {
                                val latestSession = realSessions.maxByOrNull { it.timestamp } ?: realSessions.first()
                                val gameTitle = when (latestSession.gameId) {
                                    "FAMILY_TRIVIA" -> if (isAssamese) "পৰিয়ালৰ স্মৃতি (Family Trivia)" else "Family Trivia"
                                    "FLOWER_MATCH" -> if (isAssamese) "ফুলৰ খেল (Flower Match)" else "Flower Match"
                                    "SEQUENCING" -> if (isAssamese) "দৈনন্দিন ক্ৰম (Sequencing)" else "Daily Sequencing"
                                    "CATEGORISATION" -> if (isAssamese) "শ্ৰেণীবিভাজন (Categorisation)" else "Categorisation"
                                    "VILLAGE_MARKET" -> if (isAssamese) "গাঁওৰ বজাৰ (Village Market)" else "Village Market"
                                    "PATTERN_RECOGNITION" -> if (isAssamese) "আৰ্হি চিনাক্তকৰণ (Pattern Recognition)" else "Pattern Recognition"
                                    "VOICE_CUE_CARD" -> if (isAssamese) "কণ্ঠ আৰু ছবি (Voice Cue Card)" else "Voice Cue Card"
                                    else -> latestSession.gameId
                                }

                                Text(
                                    text = "শেহতীয়া খেল: $gameTitle",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AasritiColorTokens.DeepCharcoal
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "সময়: ${formatTimestamp(latestSession.timestamp)} • স্থিতি: ${if (latestSession.completed) "সম্পন্ন (Completed)" else "অসমাপ্ত (Incomplete)"}",
                                    fontSize = 12.sp,
                                    color = AasritiColorTokens.WarmSlate
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(AasritiColorTokens.WarmSunkenSurface)
                                            .padding(10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "স্তৰ ${latestSession.difficultyLevel}",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AasritiColorTokens.DeepNortheastForest
                                            )
                                            Text(
                                                text = "পৰৱৰ্তী: স্তৰ ${latestSession.adaptationDecision}",
                                                fontSize = 10.sp,
                                                color = AasritiColorTokens.WarmSlate
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(AasritiColorTokens.WarmSunkenSurface)
                                            .padding(10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "${(latestSession.accuracy * 100).toInt()}%",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AasritiColorTokens.DeepCharcoal
                                            )
                                            Text(
                                                text = "সঠিকতা (Accuracy)",
                                                fontSize = 10.sp,
                                                color = AasritiColorTokens.WarmSlate
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(AasritiColorTokens.WarmSunkenSurface)
                                            .padding(10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "${latestSession.reactionTimeMs}ms",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AasritiColorTokens.DeepCharcoal
                                            )
                                            Text(
                                                text = "প্ৰতিক্ৰিয়া (Latency)",
                                                fontSize = 10.sp,
                                                color = AasritiColorTokens.WarmSlate
                                            )
                                        }
                                    }
                                }

                                if (latestSession.hesitationCount > 0) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "⚠️ দ্বিধাবোধ বিৰতি (>3.5s): ${latestSession.hesitationCount} বাৰ পৰিলক্ষিত।",
                                        fontSize = 12.sp,
                                        color = AasritiColorTokens.WarmAmberWarning,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "এই তথ্য কেৱল দৈনন্দিন কাৰ্য্যক্ষমতাৰ পৰ্যবেক্ষণৰ বাবেহে, কোনো চিকিৎসা বা ৰোগ নিৰ্ণয় নহয়। (Functional interaction metrics only, not a clinical diagnosis)",
                                fontSize = 10.sp,
                                color = AasritiColorTokens.WarmSlate,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                // 6. Doctor Access Code Generator
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
                                text = "🩺 চিকিৎসকৰ প্ৰৱেশ সংকেত (Doctor Access Code)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.DeepCharcoal
                            )
                            Text(
                                text = "চিকিৎসকে ৰোগীৰ খেল আৰু অগ্ৰগতি চাবলৈ এই ৬-অংকৰ ক'ডটো ব্যৱহাৰ কৰিব পাৰে (৭২ ঘণ্টাৰ বাবে বৈধ)।",
                                fontSize = 12.sp,
                                color = AasritiColorTokens.WarmSlate
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        val code = CryptoUtils.generateSixDigitCode()
                                        generatedDoctorCode = code
                                        scope.launch {
                                            doctorAccessRepository.grantAccess(
                                                DoctorAccessEntity(
                                                    patientId = patient.id,
                                                    doctorAccessCode = code,
                                                    doctorName = "পৰিদৰ্শক চিকিৎসক"
                                                )
                                            )
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                                    modifier = Modifier.heightIn(min = 48.dp)
                                ) {
                                    Text("নতুন ক'ড সৃষ্টি কৰক", color = AasritiColorTokens.DeepCharcoal, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }

                                Text(
                                    text = generatedDoctorCode ?: "424242",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AasritiColorTokens.MutedHeritageTerracotta
                                )
                            }
                        }
                    }
                }

                // 6. Recent Care Observation Preview
                item {
                    val latestLog = realCareLogs.firstOrNull()
                    if (latestLog != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(AasritiColorTokens.SupportingSage.copy(alpha = 0.25f))
                                .border(1.dp, AasritiColorTokens.DeepNortheastForest, RoundedCornerShape(16.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "শেহতীয়া পৰ্যবেক্ষণ (${latestLog.authorRole}):",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AasritiColorTokens.DeepNortheastForest
                                    )
                                    Text(
                                        text = formatTimestamp(latestLog.timestamp),
                                        fontSize = 11.sp,
                                        color = AasritiColorTokens.WarmSlate
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "[${latestLog.category}] ${latestLog.notes}",
                                    fontSize = 14.sp,
                                    color = AasritiColorTokens.DeepCharcoal
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "সকলো ইতিহাস চাবলৈ ওপৰৰ 'যত্ন ইতিহাস' টেবত টিপক ➔",
                                    fontSize = 12.sp,
                                    color = AasritiColorTokens.DeepNortheastForest,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { selectedTab = 1 }
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(AasritiColorTokens.SoftCream)
                                .border(1.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(16.dp))
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "এতিয়ালৈকে কোনো টোকা নাই",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AasritiColorTokens.DeepCharcoal
                                    )
                                    Text(
                                        text = "প্ৰথম পৰ্যবেক্ষণ সংৰক্ষণ কৰিবলৈ '+ খৰতকীয়া টোকা' টিপক",
                                        fontSize = 12.sp,
                                        color = AasritiColorTokens.WarmSlate
                                    )
                                }
                                Button(
                                    onClick = { showQuickLogDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text("+ লিখক", fontSize = 12.sp, color = AasritiColorTokens.WarmIvory)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // ==========================================
            // TAB 1: CARE HISTORY (ROOM-BACKED)
            // ==========================================
            val filteredLogs = remember(realCareLogs, historyFilterCategory) {
                when (historyFilterCategory) {
                    "ALL" -> realCareLogs
                    "MEDICINE" -> realCareLogs.filter { it.category == "MEDICINE" }
                    "FALL" -> realCareLogs.filter { it.category == "FALL" }
                    "APPETITE" -> realCareLogs.filter { it.category == "APPETITE" }
                    "SLEEP" -> realCareLogs.filter { it.category == "SLEEP" }
                    "CONFUSION" -> realCareLogs.filter { it.category == "CONFUSION" }
                    "GENERAL" -> realCareLogs.filter { it.category == "GENERAL" }
                    "ASHA" -> realCareLogs.filter { it.authorRole == "ASHA" }
                    else -> realCareLogs
                }
            }

            Column(modifier = Modifier.fillMaxSize()) {
                // Horizontally scrollable filter chips row covering all categories
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = historyFilterCategory == "ALL",
                        onClick = { historyFilterCategory = "ALL" },
                        label = { Text("সকলো (${realCareLogs.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AasritiColorTokens.DeepNortheastForest,
                            selectedLabelColor = AasritiColorTokens.WarmIvory,
                            containerColor = AasritiColorTokens.SoftCream,
                            labelColor = AasritiColorTokens.DeepCharcoal
                        )
                    )
                    FilterChip(
                        selected = historyFilterCategory == "MEDICINE",
                        onClick = { historyFilterCategory = "MEDICINE" },
                        label = { Text("💊 ঔষধ") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AasritiColorTokens.DeepNortheastForest,
                            selectedLabelColor = AasritiColorTokens.WarmIvory,
                            containerColor = AasritiColorTokens.SoftCream,
                            labelColor = AasritiColorTokens.DeepCharcoal
                        )
                    )
                    FilterChip(
                        selected = historyFilterCategory == "FALL",
                        onClick = { historyFilterCategory = "FALL" },
                        label = { Text("⚠️ পতন") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AasritiColorTokens.DeepCranberryEmergency,
                            selectedLabelColor = AasritiColorTokens.WarmIvory,
                            containerColor = AasritiColorTokens.SoftCream,
                            labelColor = AasritiColorTokens.DeepCharcoal
                        )
                    )
                    FilterChip(
                        selected = historyFilterCategory == "APPETITE",
                        onClick = { historyFilterCategory = "APPETITE" },
                        label = { Text("🍲 আহাৰ") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AasritiColorTokens.DeepNortheastForest,
                            selectedLabelColor = AasritiColorTokens.WarmIvory,
                            containerColor = AasritiColorTokens.SoftCream,
                            labelColor = AasritiColorTokens.DeepCharcoal
                        )
                    )
                    FilterChip(
                        selected = historyFilterCategory == "SLEEP",
                        onClick = { historyFilterCategory = "SLEEP" },
                        label = { Text("🌙 টোপনি") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AasritiColorTokens.DeepNortheastForest,
                            selectedLabelColor = AasritiColorTokens.WarmIvory,
                            containerColor = AasritiColorTokens.SoftCream,
                            labelColor = AasritiColorTokens.DeepCharcoal
                        )
                    )
                    FilterChip(
                        selected = historyFilterCategory == "CONFUSION",
                        onClick = { historyFilterCategory = "CONFUSION" },
                        label = { Text("❓ বিভ্ৰান্তি") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AasritiColorTokens.WarmAmberWarning,
                            selectedLabelColor = AasritiColorTokens.WarmIvory,
                            containerColor = AasritiColorTokens.SoftCream,
                            labelColor = AasritiColorTokens.DeepCharcoal
                        )
                    )
                    FilterChip(
                        selected = historyFilterCategory == "ASHA",
                        onClick = { historyFilterCategory = "ASHA" },
                        label = { Text("👩‍⚕️ আশা") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AasritiColorTokens.MugaGold,
                            selectedLabelColor = AasritiColorTokens.WarmIvory,
                            containerColor = AasritiColorTokens.SoftCream,
                            labelColor = AasritiColorTokens.DeepCharcoal
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (filteredLogs.isEmpty()) {
                    // Honest Empty State
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(AasritiColorTokens.SoftCream)
                            .border(1.5.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(18.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("📋", fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "কোনো টোকা পোৱা নগ'ল (No records found)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.DeepCharcoal
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "এই শ্ৰেণীত এতিয়ালৈকে কোনো পৰ্যবেক্ষণ লিপিবদ্ধ কৰা হোৱা নাই।",
                                fontSize = 13.sp,
                                color = AasritiColorTokens.WarmSlate
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showQuickLogDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text(
                                    text = "+ প্ৰথম টোকা লিপিবদ্ধ কৰক",
                                    color = AasritiColorTokens.WarmIvory,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredLogs, key = { it.id }) { log ->
                            CareLogHistoryCard(log = log)
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // QUICK CARE LOG DIALOG (<30s ENTRY)
    // ==========================================
    if (showQuickLogDialog) {
        Dialog(onDismissRequest = { showQuickLogDialog = false }) {
            var selectedCategoryKey by remember { mutableStateOf("MEDICINE") }
            var selectedSeverity by remember { mutableStateOf("NORMAL") }
            var noteInput by remember { mutableStateOf("পুৱাৰ নিয়মীয়া ঔষধ আৰু আহাৰ সময়মতে গ্ৰহণ কৰিছে।") }
            var validationError by remember { mutableStateOf<String?>(null) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(AasritiColorTokens.WarmIvory)
                    .border(2.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "খৰতকীয়া পৰ্যবেক্ষণ টোকা (Quick Care Log)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal
                    )
                    Text(
                        text = "ৰোগী: ${patient.displayName} • Room SQLite সংৰক্ষণ",
                        fontSize = 12.sp,
                        color = AasritiColorTokens.WarmSlate
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. Category Selection Grid
                    Text(
                        text = "পৰ্যবেক্ষণ শ্ৰেণী (Category):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AasritiColorTokens.DeepCharcoal,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val categories = listOf(
                        "MEDICINE" to "💊 ঔষধ (Medicine)",
                        "APPETITE" to "🍲 আহাৰ (Appetite)",
                        "SLEEP" to "🌙 টোপনি (Sleep)",
                        "GENERAL" to "😊 মেজাজ (General)",
                        "FALL" to "⚠️ পতন / উজুটি (Fall)",
                        "CONFUSION" to "❓ বিভ্ৰান্তি (Confusion)"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        categories.chunked(2).forEach { rowPair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                rowPair.forEach { (key, label) ->
                                    val isSelected = selectedCategoryKey == key
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(9.dp))
                                            .background(
                                                if (isSelected) AasritiColorTokens.DeepNortheastForest
                                                else AasritiColorTokens.SoftCream
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.WarmStoneBorder,
                                                shape = RoundedCornerShape(9.dp)
                                            )
                                            .clickable {
                                                selectedCategoryKey = key
                                                if (key == "FALL") selectedSeverity = "PRIORITY"
                                                if (key == "CONFUSION") selectedSeverity = "WATCH"
                                            }
                                            .heightIn(min = 44.dp)
                                            .padding(vertical = 10.dp, horizontal = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            color = if (isSelected) AasritiColorTokens.WarmIvory else AasritiColorTokens.DeepCharcoal,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Severity Triage Selector
                    Text(
                        text = "অগ্ৰাধিকাৰ স্থিতি (Care Priority):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AasritiColorTokens.DeepCharcoal,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "NORMAL" to "স্বাভাৱিক (Normal)",
                            "WATCH" to "নজৰাধীন (Watch)",
                            "PRIORITY" to "প্ৰাথমিকতা (Priority)"
                        ).forEach { (sevKey, sevLabel) ->
                            val isSel = selectedSeverity == sevKey
                            val btnColor = when (sevKey) {
                                "PRIORITY" -> AasritiColorTokens.MutedHeritageTerracotta
                                "WATCH" -> AasritiColorTokens.MugaGold
                                else -> AasritiColorTokens.DeepNortheastForest
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) btnColor else AasritiColorTokens.SoftCream)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSel) btnColor else AasritiColorTokens.WarmStoneBorder,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedSeverity = sevKey }
                                    .heightIn(min = 44.dp)
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = sevLabel,
                                    color = if (isSel) AasritiColorTokens.WarmIvory else AasritiColorTokens.DeepCharcoal,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. Observation Notes
                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = {
                            noteInput = it
                            if (it.isNotBlank()) validationError = null
                        },
                        label = { Text("টোকা (Care Notes)") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = validationError != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = AasritiColorTokens.WarmIvory,
                            unfocusedContainerColor = AasritiColorTokens.WarmIvory,
                            focusedBorderColor = AasritiColorTokens.DeepNortheastForest,
                            unfocusedBorderColor = AasritiColorTokens.WarmStoneBorder
                        ),
                        supportingText = {
                            if (validationError != null) {
                                Text(validationError ?: "", color = AasritiColorTokens.DeepCranberryEmergency)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { showQuickLogDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                            modifier = Modifier.heightIn(min = 52.dp)
                        ) {
                            Text("বাতিল (Cancel)", color = AasritiColorTokens.DeepCharcoal, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                if (noteInput.trim().isBlank()) {
                                    validationError = "অনুগ্ৰহ কৰি পৰ্যবেক্ষণৰ টোকা লিখক (Notes cannot be empty)"
                                    return@Button
                                }

                                DemoStateHolder.recordCaregiverQuickLog(selectedCategoryKey, noteInput.trim())
                                scope.launch {
                                    careLogRepository.saveLog(
                                        CareLogEntity(
                                            patientId = DemoPatientConfig.PATIENT_ID,
                                            authorRole = "CAREGIVER",
                                            category = selectedCategoryKey,
                                            severity = selectedSeverity,
                                            notes = noteInput.trim(),
                                            timestamp = System.currentTimeMillis()
                                        )
                                    )
                                }
                                showQuickLogDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.heightIn(min = 52.dp)
                        ) {
                            Text("সংৰক্ষণ কৰক (Save)", color = AasritiColorTokens.WarmIvory, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CareLogHistoryCard(log: CareLogEntity) {
    val sevColor = when (log.severity) {
        "URGENT" -> AasritiColorTokens.DeepCranberryEmergency
        "PRIORITY" -> AasritiColorTokens.MutedHeritageTerracotta
        "WATCH" -> AasritiColorTokens.MugaGold
        else -> AasritiColorTokens.DeepNortheastForest
    }

    val categoryEmoji = when (log.category) {
        "MEDICINE" -> "💊"
        "APPETITE" -> "🍲"
        "SLEEP" -> "🌙"
        "FALL" -> "⚠️"
        "CONFUSION" -> "❓"
        else -> "📝"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AasritiColorTokens.SoftCream)
            .border(1.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(categoryEmoji, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = log.category,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (log.authorRole == "ASHA") AasritiColorTokens.MugaGold.copy(alpha = 0.2f)
                                else AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.15f)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (log.authorRole == "ASHA") "আশা কৰ্মী (ASHA)" else "যত্ন লওঁতা (Caregiver)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (log.authorRole == "ASHA") AasritiColorTokens.MugaGold else AasritiColorTokens.DeepNortheastForest
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(sevColor.copy(alpha = 0.15f))
                        .border(1.dp, sevColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = log.severity,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = sevColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = log.notes,
                fontSize = 14.sp,
                color = AasritiColorTokens.DeepCharcoal,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatTimestamp(log.timestamp),
                    fontSize = 11.sp,
                    color = AasritiColorTokens.WarmSlate
                )
                Text(
                    text = "Room SQLite Local",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AasritiColorTokens.DeepNortheastForest
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

