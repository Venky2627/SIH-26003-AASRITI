package com.sih26003.aasriti.feature.caregiver

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import com.sih26003.aasriti.AasritiApplication
import com.sih26003.aasriti.core.security.CryptoUtils
import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens
import com.sih26003.aasriti.data.local.entities.CareLogEntity
import com.sih26003.aasriti.data.local.entities.DoctorAccessEntity
import com.sih26003.aasriti.data.local.entities.PatientEntity
import com.sih26003.aasriti.data.local.entities.RelationshipEntity
import com.sih26003.aasriti.data.repository.CareLogRepository
import com.sih26003.aasriti.data.repository.DoctorAccessRepository
import com.sih26003.aasriti.data.repository.GameRepository
import com.sih26003.aasriti.data.repository.PatientRepository
import com.sih26003.aasriti.data.repository.ReminderRepository
import com.sih26003.aasriti.demo.AasritiDemoData
import com.sih26003.aasriti.demo.DemoPatient
import com.sih26003.aasriti.demo.DemoPatientConfig
import com.sih26003.aasriti.demo.DemoStateHolder
import com.sih26003.aasriti.domain.model.CareLog
import com.sih26003.aasriti.domain.model.GameSession
import com.sih26003.aasriti.domain.model.Reminder
import com.sih26003.aasriti.engine.priority.PriorityEngine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * SCREEN_CAREGIVER_DASHBOARD, SCREEN_CAREGIVER_QUICK_LOG & SCREEN_CAREGIVER_CARE_HISTORY:
 * Room-backed family caregiver and health observer management center.
 * Follows UI_SCREEN_SPEC.md & UI_RULES.md:
 * - Patient status header with reactive patient selection
 * - 100% Offline Local Encrypted Storage
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
    reminderRepository: ReminderRepository? = null,
    activePatient: PatientEntity? = null
) {
    val context = LocalContext.current
    val app = context.applicationContext as? AasritiApplication
    val resolvedReminderRepo = reminderRepository ?: app?.reminderRepository

    val isAssamese = DemoStateHolder.currentLanguage == "as"
    val patientId = activePatient?.id ?: DemoStateHolder.activePatientId ?: AasritiDemoData.patient.id
    val patientDisplayName = activePatient?.let {
        it.pseudonymCode.substringBefore(" •").ifBlank { it.pseudonymCode }
    } ?: DemoStateHolder.activePatientName ?: if (isAssamese) AasritiDemoData.patient.displayName else AasritiDemoData.patient.displaySubtitle
    val patientSubtitle = activePatient?.let {
        "${it.pseudonymCode} • ${it.cognitiveStage}"
    } ?: AasritiDemoData.patient.displaySubtitle

    val patient = remember(activePatient, DemoStateHolder.activePatientId, DemoStateHolder.activePatientName, isAssamese) {
        DemoPatient(
            id = patientId,
            pseudonymCode = activePatient?.pseudonymCode ?: AasritiDemoData.patient.pseudonymCode,
            displayName = patientDisplayName,
            displaySubtitle = patientSubtitle,
            villageLocation = AasritiDemoData.patient.villageLocation,
            primaryLanguage = if (isAssamese) "as" else "en",
            cognitiveStage = activePatient?.cognitiveStage ?: AasritiDemoData.patient.cognitiveStage
        )
    }
    val scope = rememberCoroutineScope()

    val realSessions by gameRepository.getSessionsForPatient(patient.id).collectAsState(initial = emptyList())
    val realCareLogs by careLogRepository.getLogsForPatient(patient.id).collectAsState(initial = emptyList())
    val realRelationships by patientRepository.getRelationships(patient.id).collectAsState(initial = emptyList())
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
        val domainPatient = com.sih26003.aasriti.domain.model.Patient(
            id = patient.id,
            pseudonymCode = patient.pseudonymCode,
            displayName = patient.displayName,
            displaySubtitle = patient.displaySubtitle,
            birthYear = 1958,
            gender = "F",
            villageLocation = patient.villageLocation,
            primaryLanguage = if (isAssamese) "as" else "en",
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
    var showAddFamilyDialog by remember { mutableStateOf(false) }
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
                    text = if (isAssamese) "মীৰা বৰা (Mira Borah • Daughter)" else "Mira Borah (Primary Caregiver)",
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
                        text = if (isAssamese) "১০০% অফলাইন • স্থানীয় সংৰক্ষণ" else "100% Offline • Local Secure Storage",
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
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder)
            ) {
                Text(
                    text = if (isAssamese) "প্ৰস্থান (Logout)" else "Logout",
                    color = AasritiColorTokens.WarmAmberWarning,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
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
                                    text = if (isAssamese) "● স্থানীয় অফলাইন" else "● Local Offline",
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
                                    Text("⚡", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
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
                                        text = if (isAssamese) "কৰণীয়: ${evaluatedPriority.suggestedAction}" else "Action: ${evaluatedPriority.suggestedAction}",
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📋", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAssamese) "দৈনন্দিন অগ্ৰগতি (Daily Care Progress)" else "Daily Care Progress",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AasritiColorTokens.DeepCharcoal
                                )
                            }
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
                                    text = if (isAssamese) "নিয়ম পালন: $doneCount / $totalRoutines সম্পন্ন ($percent%)" else "Routines Completed: $doneCount / $totalRoutines ($percent%)",
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
                                text = if (isAssamese) "+ খৰতকীয়া টোকা (Log)" else "+ Quick Log",
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
                                text = if (isAssamese) "⏰ সোঁৱৰণী (Reminders)" else "⏰ Reminders",
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
                                            text = if (isAssamese) "${realSessions.size} খেল (Sessions)" else "${realSessions.size} Sessions",
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
                                    text = if (isAssamese) "শেহতীয়া খেল: $gameTitle" else "Latest Game: $gameTitle",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AasritiColorTokens.DeepCharcoal
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isAssamese) "সময়: ${formatTimestamp(latestSession.timestamp)} • স্থিতি: ${if (latestSession.completed) "সম্পন্ন (Completed)" else "অসমাপ্ত (Incomplete)"}"
                                    else "Time: ${formatTimestamp(latestSession.timestamp)} • Status: ${if (latestSession.completed) "Completed" else "Incomplete"}",
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
                                                text = if (isAssamese) "স্তৰ ${latestSession.difficultyLevel}" else "Level ${latestSession.difficultyLevel}",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AasritiColorTokens.DeepNortheastForest
                                            )
                                            Text(
                                                text = if (isAssamese) "পৰৱৰ্তী: স্তৰ ${latestSession.adaptationDecision}" else "Next: Level ${latestSession.adaptationDecision}",
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
                                                text = if (isAssamese) "সঠিকতা (Accuracy)" else "Accuracy",
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
                                                text = if (isAssamese) "প্ৰতিক্ৰিয়া (Latency)" else "Latency",
                                                fontSize = 10.sp,
                                                color = AasritiColorTokens.WarmSlate
                                            )
                                        }
                                    }
                                }

                                if (latestSession.hesitationCount > 0) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (isAssamese) "⚠️ দ্বিধাবোধ বিৰতি (>3.5s): ${latestSession.hesitationCount} বাৰ পৰিলক্ষিত।"
                                        else "⚠️ Hesitation pause (>3.5s): ${latestSession.hesitationCount} times.",
                                        fontSize = 12.sp,
                                        color = AasritiColorTokens.WarmAmberWarning,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isAssamese) "এই তথ্য কেৱল দৈনন্দিন কাৰ্য্যক্ষমতাৰ পৰ্যবেক্ষণৰ বাবেহে, কোনো চিকিৎসা বা ৰোগ নিৰ্ণয় নহয়। (Functional interaction metrics only, not a clinical diagnosis)"
                                else "Functional interaction metrics only, not a clinical diagnosis.",
                                fontSize = 10.sp,
                                color = AasritiColorTokens.WarmSlate,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                // 6. Family Circle for Trivia & Reminiscence Card
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("👨‍👩‍👧", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = if (isAssamese) "পৰিয়ালৰ সদস্য আৰু স্মৃতি (Family Circle)" else "Family Circle & Trivia Setup",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AasritiColorTokens.DeepCharcoal
                                        )
                                        Text(
                                            text = if (isAssamese) "স্মৃতি খেলৰ বাবে সদস্য তালিকা" else "Configured members for Family Trivia",
                                            fontSize = 11.sp,
                                            color = AasritiColorTokens.WarmSlate
                                        )
                                    }
                                }

                                if (realRelationships.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AasritiColorTokens.MutedHeritageTerracotta.copy(alpha = 0.12f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (isAssamese) "${realRelationships.size} সদস্য" else "${realRelationships.size} Members",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AasritiColorTokens.MutedHeritageTerracotta
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (realRelationships.isEmpty()) {
                                Text(
                                    text = if (isAssamese) "কোনো পৰিয়ালৰ সদস্য যোগ কৰা হোৱা নাই।" else "No family members added yet.",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AasritiColorTokens.WarmSlate
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isAssamese)
                                        "'পৰিয়ালৰ স্মৃতি' খেলত আইতাই চিনি পাবলৈ তলৰ বুটাম টিপি পৰিয়ালৰ সদস্যৰ নাম আৰু সম্পৰ্ক নিৰ্ধাৰণ কৰক।"
                                    else
                                        "Add family members below so elder can recognize them in Family Trivia game rounds.",
                                    fontSize = 12.sp,
                                    color = AasritiColorTokens.WarmSlate
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    realRelationships.forEach { member ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(AasritiColorTokens.WarmSunkenSurface)
                                                .border(1.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(10.dp))
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                val avatarEmoji = when {
                                                    member.relationshipType.contains("নাতি", ignoreCase = true) || member.relationshipType.contains("Grandson", ignoreCase = true) -> "👦"
                                                    member.relationshipType.contains("নাতিনী", ignoreCase = true) || member.relationshipType.contains("Granddaughter", ignoreCase = true) -> "👧"
                                                    member.relationshipType.contains("পুত্ৰ", ignoreCase = true) || member.relationshipType.contains("Son", ignoreCase = true) -> "👨"
                                                    member.relationshipType.contains("কন্যা", ignoreCase = true) || member.relationshipType.contains("Daughter", ignoreCase = true) -> "👩"
                                                    member.relationshipType.contains("বোৱাৰী", ignoreCase = true) -> "👩"
                                                    member.relationshipType.contains("স্বামী", ignoreCase = true) || member.relationshipType.contains("পত্নী", ignoreCase = true) -> "👵"
                                                    else -> "👤"
                                                }
                                                Text(avatarEmoji, fontSize = 20.sp)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = member.name,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = AasritiColorTokens.DeepCharcoal
                                                    )
                                                    Text(
                                                        text = member.relationshipType,
                                                        fontSize = 12.sp,
                                                        color = AasritiColorTokens.MutedHeritageTerracotta,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                            }

                                            IconButton(
                                                onClick = {
                                                    scope.launch {
                                                        patientRepository.deleteRelationship(member.id)
                                                    }
                                                }
                                            ) {
                                                Text("✕", color = AasritiColorTokens.WarmAmberWarning, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { showAddFamilyDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.MutedHeritageTerracotta),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                            ) {
                                Text(
                                    text = if (isAssamese) "+ পৰিয়ালৰ সদস্য যোগ কৰক (Add Member)" else "+ Add Family Member",
                                    color = AasritiColorTokens.WarmIvory,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 7. Doctor Access Code Generator
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
                                text = if (isAssamese) "🩺 চিকিৎসকৰ প্ৰৱেশ সংকেত (Doctor Access Code)" else "🩺 Doctor Access Code",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.DeepCharcoal
                            )
                            Text(
                                text = if (isAssamese) "চিকিৎসকে ৰোগীৰ খেল আৰু অগ্ৰগতি চাবলৈ এই ৬-অংকৰ ক'ডটো ব্যৱহাৰ কৰিব পাৰে (৭২ ঘণ্টাৰ বাবে বৈধ)।"
                                else "Doctor can use this 6-digit access code to view longitudinal trends and session telemetry (valid for 72h).",
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
                                    modifier = Modifier.height(44.dp)
                                ) {
                                    Text(
                                        text = if (isAssamese) "নতুন ক'ড সৃষ্টি কৰক" else "Generate Code",
                                        color = AasritiColorTokens.DeepCharcoal,
                                        fontSize = 13.sp
                                    )
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
                                        text = if (isAssamese) "শেহতীয়া পৰ্যবেক্ষণ (${latestLog.authorRole}):" else "Recent Observation (${latestLog.authorRole}):",
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
                                    text = if (isAssamese) "সকলো ইতিহাস চাবলৈ ওপৰৰ 'যত্ন ইতিহাস' টেবত টিপক ➔" else "View complete care logs in 'Care History' tab ➔",
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isAssamese) "এতিয়ালৈকে কোনো টোকা নাই" else "No care notes yet",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AasritiColorTokens.DeepCharcoal
                                    )
                                    Text(
                                        text = if (isAssamese) "প্ৰথম পৰ্যবেক্ষণ সংৰক্ষণ কৰিবলৈ '+ খৰতকীয়া টোকা' টিপক" else "Tap '+ Log' to record first observation",
                                        fontSize = 12.sp,
                                        color = AasritiColorTokens.WarmSlate
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { showQuickLogDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(40.dp)
                                ) {
                                    Text(if (isAssamese) "+ লিখক" else "+ Log", fontSize = 12.sp, color = AasritiColorTokens.WarmIvory)
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
                    "ASHA" -> realCareLogs.filter { it.authorRole == "ASHA" }
                    else -> realCareLogs
                }
            }

            Column(modifier = Modifier.fillMaxSize()) {
                // Filter chips row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = historyFilterCategory == "ALL",
                        onClick = { historyFilterCategory = "ALL" },
                        label = { Text(if (isAssamese) "সকলো (${realCareLogs.size})" else "All (${realCareLogs.size})") },
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
                        label = { Text(if (isAssamese) "ঔষধ" else "Medication") },
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
                        label = { Text(if (isAssamese) "পতন/উজুটি" else "Fall") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AasritiColorTokens.DeepCranberryEmergency,
                            selectedLabelColor = AasritiColorTokens.WarmIvory,
                            containerColor = AasritiColorTokens.SoftCream,
                            labelColor = AasritiColorTokens.DeepCharcoal
                        )
                    )
                    FilterChip(
                        selected = historyFilterCategory == "ASHA",
                        onClick = { historyFilterCategory = "ASHA" },
                        label = { Text(if (isAssamese) "আশা পৰিদৰ্শন" else "ASHA Visits") },
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
                                text = if (isAssamese) "কোনো টোকা পোৱা নগ'ল (No records found)" else "No records found",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.DeepCharcoal
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isAssamese) "এই শ্ৰেণীত এতিয়ালৈকে কোনো পৰ্যবেক্ষণ লিপিবদ্ধ কৰা হোৱা নাই।"
                                else "No observations recorded in this category yet.",
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
                                    text = if (isAssamese) "+ প্ৰথম টোকা লিপিবদ্ধ কৰক" else "+ Record First Care Log",
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
                            CareLogHistoryCard(log = log, isAssamese = isAssamese)
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
            var noteInput by remember {
                mutableStateOf(
                    if (isAssamese) "পুৱাৰ নিয়মীয়া ঔষধ আৰু আহাৰ সময়মতে গ্ৰহণ কৰিছে।"
                    else "Regular medicine and meal taken on schedule."
                )
            }
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
                        text = if (isAssamese) "খৰতকীয়া পৰ্যবেক্ষণ টোকা (Quick Care Log)" else "Quick Care Log",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal
                    )
                    Text(
                        text = if (isAssamese) "ৰোগী: ${patient.displayName} • স্থানীয় সংৰক্ষণ" else "Patient: ${patient.displayName} • Local Storage",
                        fontSize = 12.sp,
                        color = AasritiColorTokens.WarmSlate
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. Category Selection Grid
                    Text(
                        text = if (isAssamese) "পৰ্যবেক্ষণ শ্ৰেণী (Category):" else "Observation Category:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AasritiColorTokens.DeepCharcoal,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val categories = if (isAssamese) listOf(
                        "MEDICINE" to "💊 ঔষধ (Medicine)",
                        "HYDRATION" to "💧 পানী (Hydration)",
                        "MEAL" to "🍲 আহাৰ (Meal)",
                        "MOOD" to "😊 মেজাজ (Mood)",
                        "ACTIVITY" to "🚶 কাৰ্য্যকলাপ (Activity)",
                        "SLEEP" to "🌙 টোপনি (Sleep)",
                        "SYMPTOM" to "🩺 লক্ষণ (Symptom)",
                        "FALL" to "⚠️ পতন / আঘাত (Fall)",
                        "CONFUSION" to "❓ বিভ্ৰান্তি (Confusion)",
                        "OTHER" to "📝 অন্যান্য (Other)"
                    ) else listOf(
                        "MEDICINE" to "💊 Medication",
                        "HYDRATION" to "💧 Hydration",
                        "MEAL" to "🍲 Meal",
                        "MOOD" to "😊 Mood",
                        "ACTIVITY" to "🚶 Activity",
                        "SLEEP" to "🌙 Sleep",
                        "SYMPTOM" to "🩺 Symptom",
                        "FALL" to "⚠️ Fall / Injury",
                        "CONFUSION" to "❓ Confusion",
                        "OTHER" to "📝 Other"
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
                                            .clickable {
                                                selectedCategoryKey = key
                                                if (key == "FALL" || key == "SYMPTOM") selectedSeverity = "PRIORITY"
                                                else if (key == "CONFUSION") selectedSeverity = "WATCH"
                                                else if (key == "MEDICINE" || key == "HYDRATION" || key == "MEAL") selectedSeverity = "NORMAL"
                                            }
                                            .padding(vertical = 8.dp, horizontal = 6.dp),
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
                        text = if (isAssamese) "অগ্ৰাধিকাৰ স্থিতি (Care Priority):" else "Care Priority:",
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
                        (if (isAssamese) listOf(
                            "NORMAL" to "স্বাভাৱিক (Normal)",
                            "WATCH" to "নজৰাধীন (Watch)",
                            "PRIORITY" to "প্ৰাথমিকতা (Priority)"
                        ) else listOf(
                            "NORMAL" to "Normal",
                            "WATCH" to "Watch",
                            "PRIORITY" to "Priority"
                        )).forEach { (sevKey, sevLabel) ->
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
                                    .clickable { selectedSeverity = sevKey }
                                    .padding(vertical = 7.dp),
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
                        label = { Text(if (isAssamese) "টোকা (Care Notes)" else "Care Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = validationError != null,
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
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (isAssamese) "বাতিল (Cancel)" else "Cancel", color = AasritiColorTokens.DeepCharcoal)
                        }

                        Button(
                            onClick = {
                                if (noteInput.trim().isBlank()) {
                                    validationError = if (isAssamese) "অনুগ্ৰহ কৰি পৰ্যবেক্ষণৰ টোকা লিখক (Notes cannot be empty)"
                                    else "Care notes cannot be empty"
                                    return@Button
                                }

                                DemoStateHolder.recordCaregiverQuickLog(selectedCategoryKey, noteInput.trim())
                                scope.launch {
                                    careLogRepository.saveLog(
                                        CareLogEntity(
                                            patientId = patient.id,
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
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (isAssamese) "সংৰক্ষণ কৰক (Save)" else "Save", color = AasritiColorTokens.WarmIvory, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showAddFamilyDialog) {
        AddFamilyMemberDialog(
            patientId = patient.id,
            onDismiss = { showAddFamilyDialog = false },
            onSave = { newMember ->
                scope.launch {
                    patientRepository.addRelationship(newMember)
                }
            }
        )
    }
}

@Composable
private fun CareLogHistoryCard(log: CareLogEntity, isAssamese: Boolean = false) {
    val sevColor = when (log.severity) {
        "URGENT" -> AasritiColorTokens.DeepCranberryEmergency
        "PRIORITY" -> AasritiColorTokens.MutedHeritageTerracotta
        "WATCH" -> AasritiColorTokens.MugaGold
        else -> AasritiColorTokens.DeepNortheastForest
    }

    val categoryEmoji = when (log.category) {
        "MEDICINE" -> "💊"
        "HYDRATION" -> "💧"
        "MEAL", "APPETITE" -> "🍲"
        "MOOD", "GENERAL" -> "😊"
        "ACTIVITY" -> "🚶"
        "SLEEP" -> "🌙"
        "SYMPTOM" -> "🩺"
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
                            text = if (log.authorRole == "ASHA") (if (isAssamese) "আশা কৰ্মী (ASHA)" else "ASHA Worker")
                            else (if (isAssamese) "যত্ন লওঁতা (Caregiver)" else "Caregiver"),
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
                    text = if (isAssamese) "স্থানীয় সংৰক্ষণ" else "Local Storage",
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

@Composable
fun AddFamilyMemberDialog(
    patientId: String,
    onDismiss: () -> Unit,
    onSave: (RelationshipEntity) -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var selectedRelation by remember { mutableStateOf("পুত্ৰ (Son)") }
    var validationError by remember { mutableStateOf<String?>(null) }

    val relationsList = listOf(
        "পুত্ৰ (Son)",
        "কন্যা (Daughter)",
        "নাতি (Grandson)",
        "নাতিনী (Granddaughter)",
        "বোৱাৰী (Daughter-in-law)",
        "জোঁৱাই (Son-in-law)",
        "স্বামী/পত্নী (Spouse)",
        "ভনী/বায়েক (Sister)",
        "ভাই/ককাই (Brother)",
        "বন্ধু (Friend)"
    )

    Dialog(onDismissRequest = onDismiss) {
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
                    text = "👨‍👩‍👧 পৰিয়ালৰ সদস্য যোগ কৰক",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AasritiColorTokens.DeepCharcoal
                )
                Text(
                    text = "Add Family Member for Trivia & Reminiscence",
                    fontSize = 12.sp,
                    color = AasritiColorTokens.WarmSlate
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = {
                        nameInput = it
                        validationError = null
                    },
                    label = { Text("সদস্যৰ নাম (Full Name)") },
                    placeholder = { Text("যেনে: ৰূপম বৰা (Rupam Borah)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AasritiColorTokens.DeepNortheastForest,
                        unfocusedBorderColor = AasritiColorTokens.WarmStoneBorder,
                        focusedLabelColor = AasritiColorTokens.DeepNortheastForest
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "আইতাৰ সৈতে সম্পৰ্ক (Relationship):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AasritiColorTokens.DeepCharcoal,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 140.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    relationsList.chunked(2).forEach { rowChips ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowChips.forEach { rel ->
                                val isSelected = (selectedRelation == rel)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSelected) AasritiColorTokens.DeepNortheastForest
                                            else AasritiColorTokens.SoftCream
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) AasritiColorTokens.DeepNortheastForest
                                            else AasritiColorTokens.WarmStoneBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedRelation = rel }
                                        .padding(vertical = 8.dp, horizontal = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = rel,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) AasritiColorTokens.WarmIvory else AasritiColorTokens.DeepCharcoal,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                if (validationError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = validationError ?: "",
                        color = AasritiColorTokens.WarmAmberWarning,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AasritiColorTokens.WarmStoneBorder),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text("বাতিল (Cancel)", color = AasritiColorTokens.WarmSlate, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val trimmed = nameInput.trim()
                            if (trimmed.isBlank()) {
                                validationError = "অনুগ্ৰহ কৰি সদস্যৰ নাম লিখক (Please enter member name)"
                            } else {
                                onSave(
                                    RelationshipEntity(
                                        patientId = patientId,
                                        name = trimmed,
                                        relationshipType = selectedRelation
                                    )
                                )
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text("সংৰক্ষণ (Save)", color = AasritiColorTokens.WarmIvory, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
