package com.sih26003.smritisetu.feature.asha

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
import androidx.compose.ui.window.Dialog
import com.sih26003.smritisetu.SmritiSetuApplication
import com.sih26003.smritisetu.core.ui.theme.AasritiColorTokens
import com.sih26003.smritisetu.data.local.entities.CareLogEntity
import com.sih26003.smritisetu.data.repository.CareLogRepository
import com.sih26003.smritisetu.data.repository.PatientRepository
import com.sih26003.smritisetu.demo.AasritiDemoData
import com.sih26003.smritisetu.demo.DemoAshaRosterItem
import com.sih26003.smritisetu.demo.DemoPatientConfig
import com.sih26003.smritisetu.demo.DemoStateHolder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * SCREEN_ASHA_PATIENT_ROSTER:
 * Community health worker multi-patient management roster.
 * Follows UI_SCREEN_SPEC.md:
 * - Medium information density
 * - Quick village elder triage (NORMAL, WATCH, PRIORITY)
 * - Offline home visit logging dialog persisting into Room care_logs
 * - Recent observations and care history inspector
 */
@Composable
fun AshaDashboardScreen(
    onOpenPatientView: () -> Unit,
    onBackToRoles: () -> Unit,
    careLogRepository: CareLogRepository? = null,
    patientRepository: PatientRepository? = null
) {
    val context = LocalContext.current
    val app = context.applicationContext as? SmritiSetuApplication
    val resolvedCareLogRepo = careLogRepository ?: app?.careLogRepository
    val scope = rememberCoroutineScope()

    val roster = remember { AasritiDemoData.ashaRoster }
    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "WATCH", "PRIORITY"
    var activeVisitLogPatient by remember { mutableStateOf<DemoAshaRosterItem?>(null) }
    var activeHistoryPatient by remember { mutableStateOf<DemoAshaRosterItem?>(null) }
    val isAssamese = DemoStateHolder.currentLanguage == "as"

    val realCareLogs by (resolvedCareLogRepo?.getLogsForPatient(DemoPatientConfig.PATIENT_ID)?.collectAsState(initial = emptyList())
        ?: remember { mutableStateOf(emptyList()) })

    val filteredRoster = remember(selectedFilter) {
        when (selectedFilter) {
            "WATCH" -> roster.filter { it.statusTier == "WATCH" }
            "PRIORITY" -> roster.filter { it.statusTier == "PRIORITY" }
            else -> roster
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AasritiColorTokens.WarmIvory)
            .padding(18.dp)
    ) {
        // 1. Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onBackToRoles,
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    text = if (isAssamese) "← প্ৰস্থান" else "← Exit",
                    color = AasritiColorTokens.DeepCharcoal,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isAssamese) "আশা কৰ্মীৰ কমিউনিটি ৰষ্টাৰ" else "ASHA Community Roster",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AasritiColorTokens.DeepCharcoal
                )
                Text(
                    text = "কামৰূপ গ্ৰাম্য (Kamrup Rural) • ১৪ গৰাকী জ্যেষ্ঠ",
                    fontSize = 12.sp,
                    color = AasritiColorTokens.WarmSlate
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
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilter == "ALL",
                onClick = { selectedFilter = "ALL" },
                label = { Text("সকলো (${roster.size})") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AasritiColorTokens.DeepNortheastForest,
                    selectedLabelColor = AasritiColorTokens.WarmIvory,
                    containerColor = AasritiColorTokens.SoftCream,
                    labelColor = AasritiColorTokens.DeepCharcoal
                )
            )
            FilterChip(
                selected = selectedFilter == "WATCH",
                onClick = { selectedFilter = "WATCH" },
                label = { Text("নজৰাধীন (Watch)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AasritiColorTokens.WarmAmberWarning,
                    selectedLabelColor = AasritiColorTokens.WarmIvory,
                    containerColor = AasritiColorTokens.SoftCream,
                    labelColor = AasritiColorTokens.DeepCharcoal
                )
            )
            FilterChip(
                selected = selectedFilter == "PRIORITY",
                onClick = { selectedFilter = "PRIORITY" },
                label = { Text("প্ৰাথমিকতা (Priority)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AasritiColorTokens.DeepCranberryEmergency,
                    selectedLabelColor = AasritiColorTokens.WarmIvory,
                    containerColor = AasritiColorTokens.SoftCream,
                    labelColor = AasritiColorTokens.DeepCharcoal
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Multi-Patient Roster List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredRoster) { item ->
                AshaPatientCard(
                    patient = item,
                    isAssamese = isAssamese,
                    onLogVisit = { activeVisitLogPatient = item },
                    onViewHistory = { activeHistoryPatient = item },
                    onOpenCompanion = onOpenPatientView
                )
            }
        }
    }

    // 4. Quick Visit Log Dialog (Room SQLite-backed)
    activeVisitLogPatient?.let { p ->
        AshaVisitLogDialog(
            patient = p,
            isAssamese = isAssamese,
            onDismiss = { activeVisitLogPatient = null },
            onSave = { category, severity, bp, notes ->
                DemoStateHolder.recordCaregiverQuickLog("ASHA Visit", "$bp - $notes")
                scope.launch {
                    resolvedCareLogRepo?.saveLog(
                        CareLogEntity(
                            patientId = DemoPatientConfig.PATIENT_ID,
                            authorRole = "ASHA",
                            category = category,
                            severity = severity,
                            notes = "ৰক্তচাপ (BP): $bp • $notes",
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }
        )
    }

    // 5. Patient Care & Visit History Dialog (Room SQLite-backed)
    activeHistoryPatient?.let { p ->
        AshaPatientHistoryDialog(
            patient = p,
            logs = realCareLogs,
            isAssamese = isAssamese,
            onDismiss = { activeHistoryPatient = null }
        )
    }
}

@Composable
private fun AshaPatientCard(
    patient: DemoAshaRosterItem,
    isAssamese: Boolean,
    onLogVisit: () -> Unit,
    onViewHistory: () -> Unit,
    onOpenCompanion: () -> Unit
) {
    val statusColor = when (patient.statusTier) {
        "PRIORITY" -> AasritiColorTokens.DeepCranberryEmergency
        "WATCH" -> AasritiColorTokens.WarmAmberWarning
        else -> AasritiColorTokens.DeepNortheastForest
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AasritiColorTokens.SoftCream)
            .border(1.5.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isAssamese) patient.nameIndic else patient.nameEn,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AasritiColorTokens.DeepCharcoal
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "[${patient.pseudonymCode}]",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AasritiColorTokens.WarmSlate
                        )
                    }
                    Text(
                        text = "চুবুৰী: ${patient.hamlet}",
                        fontSize = 13.sp,
                        color = AasritiColorTokens.WarmSlate
                    )
                }

                // Triage Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(1.dp, statusColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = patient.statusTier,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "শেহতীয়া পৰিদৰ্শন: ${patient.lastVisitedDate}",
                fontSize = 12.sp,
                color = AasritiColorTokens.WarmSlate
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "মন্তব্য: ${patient.notes}",
                fontSize = 13.sp,
                color = AasritiColorTokens.DeepCharcoal
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onViewHistory,
                    colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                    modifier = Modifier.height(44.dp)
                ) {
                    Text(
                        text = if (isAssamese) "ইতিহাস (History)" else "History",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onLogVisit,
                    colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(44.dp)
                ) {
                    Text(
                        text = if (isAssamese) "+ পৰিদৰ্শন লিখক" else "+ Log Visit",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.WarmIvory
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onOpenCompanion,
                    colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.WarmSunkenSurface),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(44.dp)
                ) {
                    Text(
                        text = "➔",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal
                    )
                }
            }
        }
    }
}

/**
 * Rapid Field Visit Log Dialog (<30s entry) persisting into Room SQLite.
 */
@Composable
private fun AshaVisitLogDialog(
    patient: DemoAshaRosterItem,
    isAssamese: Boolean,
    onDismiss: () -> Unit,
    onSave: (category: String, severity: String, bp: String, notes: String) -> Unit
) {
    var bpValue by remember { mutableStateOf("128/82") }
    var selectedCategoryKey by remember { mutableStateOf("GENERAL") }
    var selectedSeverity by remember { mutableStateOf("NORMAL") }
    var notesText by remember { mutableStateOf("ৰোগী সজাগ আৰু শান্ত। পুৱাৰ খাদ্য গ্ৰহণ স্বাভাৱিক।") }
    var validationError by remember { mutableStateOf<String?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }

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
                    text = if (isAssamese) "আশা ঘৰুৱা পৰিদৰ্শন পত্ৰ" else "ASHA Home Visit Check-in",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AasritiColorTokens.DeepCharcoal
                )
                Text(
                    text = "${patient.nameIndic} (${patient.pseudonymCode}) • Room SQLite",
                    fontSize = 12.sp,
                    color = AasritiColorTokens.WarmSlate
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (!isSubmitted) {
                    // Blood Pressure input
                    OutlinedTextField(
                        value = bpValue,
                        onValueChange = { bpValue = it },
                        label = { Text("ৰক্তচাপ (Blood Pressure)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category Selector
                    Text(
                        text = "পৰ্যবেক্ষণ শ্ৰেণী (Category):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AasritiColorTokens.DeepCharcoal,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    val categories = listOf(
                        "GENERAL" to "সাধাৰণ (General)",
                        "MEDICINE" to "ঔষধ (Medicine)",
                        "APPETITE" to "আহাৰ (Appetite)",
                        "FALL" to "পতন (Fall)"
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        categories.forEach { (catKey, catLabel) ->
                            val isSel = selectedCategoryKey == catKey
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.SoftCream)
                                    .clickable {
                                        selectedCategoryKey = catKey
                                        if (catKey == "FALL") selectedSeverity = "PRIORITY"
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = catLabel,
                                    color = if (isSel) AasritiColorTokens.WarmIvory else AasritiColorTokens.DeepCharcoal,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Severity Selector
                    Text(
                        text = "অগ্ৰাধিকাৰ স্থিতি (Triage):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AasritiColorTokens.DeepCharcoal,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
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
                                    .clickable { selectedSeverity = sevKey }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = sevLabel,
                                    color = if (isSel) AasritiColorTokens.WarmIvory else AasritiColorTokens.DeepCharcoal,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = notesText,
                        onValueChange = {
                            notesText = it
                            if (it.isNotBlank()) validationError = null
                        },
                        label = { Text("পৰিদৰ্শনৰ টোকা (Field Observations)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        isError = validationError != null,
                        supportingText = {
                            if (validationError != null) {
                                Text(validationError ?: "", color = AasritiColorTokens.DeepCranberryEmergency)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("বাতিল (Cancel)", color = AasritiColorTokens.DeepCharcoal)
                        }

                        Button(
                            onClick = {
                                if (notesText.trim().isBlank()) {
                                    validationError = "অনুগ্ৰহ কৰি টোকা লিখক (Notes cannot be empty)"
                                    return@Button
                                }
                                onSave(selectedCategoryKey, selectedSeverity, bpValue.trim(), notesText.trim())
                                isSubmitted = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("সংৰক্ষণ কৰক (Save)", color = AasritiColorTokens.WarmIvory, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✓", fontSize = 36.sp, color = AasritiColorTokens.DeepNortheastForest, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "পৰিদৰ্শন সফলভাৱে লিপিবদ্ধ কৰা হ'ল!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AasritiColorTokens.DeepNortheastForest
                        )
                        Text(
                            text = "Room SQLite local care_logs table updated",
                            fontSize = 12.sp,
                            color = AasritiColorTokens.WarmSlate
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("সম্পূৰ্ণ হ'ল", color = AasritiColorTokens.WarmIvory)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dialog displaying Room SQLite-backed care logs and field observations for the elder.
 */
@Composable
private fun AshaPatientHistoryDialog(
    patient: DemoAshaRosterItem,
    logs: List<CareLogEntity>,
    isAssamese: Boolean,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .clip(RoundedCornerShape(20.dp))
                .background(AasritiColorTokens.WarmIvory)
                .border(2.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "পৰ্যবেক্ষণ ইতিহাস (Care History)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AasritiColorTokens.DeepCharcoal
                        )
                        Text(
                            text = "${patient.nameIndic} (${patient.pseudonymCode})",
                            fontSize = 12.sp,
                            color = AasritiColorTokens.WarmSlate
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Room SQLite",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AasritiColorTokens.DeepNortheastForest
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (logs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📝", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "কোনো পূৰ্বৰ পৰ্যবেক্ষণ পোৱা নগ'ল",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.DeepCharcoal
                            )
                            Text(
                                text = "প্ৰথম পৰিদৰ্শন লিখিবলৈ '+ পৰিদৰ্শন লিখক' টিপক",
                                fontSize = 12.sp,
                                color = AasritiColorTokens.WarmSlate
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(logs, key = { it.id }) { log ->
                            val sevColor = when (log.severity) {
                                "URGENT" -> AasritiColorTokens.DeepCranberryEmergency
                                "PRIORITY" -> AasritiColorTokens.MutedHeritageTerracotta
                                "WATCH" -> AasritiColorTokens.MugaGold
                                else -> AasritiColorTokens.DeepNortheastForest
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AasritiColorTokens.SoftCream)
                                    .border(1.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(12.dp))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(
                                                        if (log.authorRole == "ASHA") AasritiColorTokens.MugaGold.copy(alpha = 0.2f)
                                                        else AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.15f)
                                                    )
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = if (log.authorRole == "ASHA") "আশা (ASHA)" else "যত্ন লওঁতা (Caregiver)",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (log.authorRole == "ASHA") AasritiColorTokens.MugaGold else AasritiColorTokens.DeepNortheastForest
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = log.category,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AasritiColorTokens.DeepCharcoal
                                            )
                                        }

                                        Text(
                                            text = log.severity,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = sevColor
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = log.notes,
                                        fontSize = 13.sp,
                                        color = AasritiColorTokens.DeepCharcoal
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                                    Text(
                                        text = sdf.format(Date(log.timestamp)),
                                        fontSize = 10.sp,
                                        color = AasritiColorTokens.WarmSlate
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Text("বন্ধ কৰক (Close)", color = AasritiColorTokens.WarmIvory, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
