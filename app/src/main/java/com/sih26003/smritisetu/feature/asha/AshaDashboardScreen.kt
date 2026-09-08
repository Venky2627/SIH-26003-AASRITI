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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sih26003.smritisetu.core.ui.theme.AasritiColorTokens
import com.sih26003.smritisetu.demo.AasritiDemoData
import com.sih26003.smritisetu.demo.DemoAshaRosterItem
import com.sih26003.smritisetu.demo.DemoStateHolder

/**
 * SCREEN_ASHA_PATIENT_ROSTER:
 * Community health worker multi-patient management roster.
 * Follows UI_SCREEN_SPEC.md:
 * - Medium information density
 * - Quick village elder triage (NORMAL, WATCH, PRIORITY)
 * - Offline home visit logging dialog
 */
@Composable
fun AshaDashboardScreen(
    onOpenPatientView: () -> Unit,
    onBackToRoles: () -> Unit
) {
    val roster = remember { AasritiDemoData.ashaRoster }
    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "WATCH", "PRIORITY"
    var activeVisitLogPatient by remember { mutableStateOf<DemoAshaRosterItem?>(null) }
    val isAssamese = DemoStateHolder.currentLanguage == "as"

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
                    onOpenCompanion = onOpenPatientView
                )
            }
        }
    }

    // 4. Quick Visit Log Dialog
    activeVisitLogPatient?.let { p ->
        AshaVisitLogDialog(
            patient = p,
            isAssamese = isAssamese,
            onDismiss = { activeVisitLogPatient = null }
        )
    }
}

@Composable
private fun AshaPatientCard(
    patient: DemoAshaRosterItem,
    isAssamese: Boolean,
    onLogVisit: () -> Unit,
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
                    onClick = onLogVisit,
                    colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(44.dp)
                ) {
                    Text(
                        text = if (isAssamese) "+ পৰিদৰ্শন লিখক" else "+ Log Visit",
                        fontSize = 13.sp,
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
                        text = if (isAssamese) "সঙ্গী খোলা ➔" else "Open Companion ➔",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal
                    )
                }
            }
        }
    }
}

/**
 * Rapid Field Visit Log Dialog (<30s entry).
 */
@Composable
private fun AshaVisitLogDialog(
    patient: DemoAshaRosterItem,
    isAssamese: Boolean,
    onDismiss: () -> Unit
) {
    var bpValue by remember { mutableStateOf("128/82") }
    var notesText by remember { mutableStateOf("ৰোগী সজাগ আৰু শান্ত। পুৱাৰ খাদ্য গ্ৰহণ স্বাভাৱিক।") }
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
                    text = "${patient.nameIndic} (${patient.pseudonymCode})",
                    fontSize = 14.sp,
                    color = AasritiColorTokens.WarmSlate
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!isSubmitted) {
                    OutlinedTextField(
                        value = bpValue,
                        onValueChange = { bpValue = it },
                        label = { Text("ৰক্তচাপ (Blood Pressure)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("পৰিদৰ্শনৰ টোকা (Observations)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("বাতিল", color = AasritiColorTokens.DeepCharcoal)
                        }

                        Button(
                            onClick = {
                                DemoStateHolder.recordCaregiverQuickLog("ASHA Visit", "$bpValue - $notesText")
                                isSubmitted = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                            shape = RoundedCornerShape(12.dp)
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
