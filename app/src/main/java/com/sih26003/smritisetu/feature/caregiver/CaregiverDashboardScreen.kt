package com.sih26003.smritisetu.feature.caregiver

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sih26003.smritisetu.core.security.CryptoUtils
import com.sih26003.smritisetu.core.ui.theme.AasritiColorTokens
import com.sih26003.smritisetu.data.local.entities.DoctorAccessEntity
import com.sih26003.smritisetu.data.repository.DoctorAccessRepository
import com.sih26003.smritisetu.data.repository.GameRepository
import com.sih26003.smritisetu.data.repository.PatientRepository
import com.sih26003.smritisetu.demo.AasritiDemoData
import com.sih26003.smritisetu.demo.DemoStateHolder
import kotlinx.coroutines.launch

/**
 * SCREEN_CAREGIVER_DASHBOARD & SCREEN_CAREGIVER_QUICK_LOG:
 * Low-medium density family caregiver overview.
 * Follows UI_SCREEN_SPEC.md:
 * - Patient status header with local SQLite sync chip
 * - Today's Priority Card with Muga Gold accent
 * - Daily Routine progress connected to reactive DemoStateHolder
 * - <30s Quick Log triage dialog
 * - Doctor Access Code generator
 */
@Suppress("UNUSED_PARAMETER")
@Composable
fun CaregiverDashboardScreen(
    patientRepository: PatientRepository,
    gameRepository: GameRepository,
    doctorAccessRepository: DoctorAccessRepository,
    onOpenReminders: (String) -> Unit,
    onLogout: () -> Unit
) {
    val patient = remember { AasritiDemoData.patient }
    val scope = rememberCoroutineScope()
    val isAssamese = DemoStateHolder.currentLanguage == "as"

    var showQuickLogDialog by remember { mutableStateOf(false) }
    var generatedDoctorCode by remember { mutableStateOf<String?>("424242") }

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
            }

            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder)
            ) {
                Text("প্ৰস্থান (Logout)", color = AasritiColorTokens.WarmAmberWarning, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 2. Patient Status Header Card
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

                        // Local Sync Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AasritiColorTokens.SupportingSage.copy(alpha = 0.25f))
                                .border(1.dp, AasritiColorTokens.DeepNortheastForest, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("● স্থানীয় অফলাইন", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.DeepNortheastForest)
                        }
                    }
                }
            }

            // 3. Today's Priority Card (Muga Gold Accent)
            item {
                val hasPendingMeds = !DemoStateHolder.completedRoutineIds.contains("routine_1")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(AasritiColorTokens.SoftCream)
                        .border(
                            width = 2.dp,
                            color = if (hasPendingMeds) AasritiColorTokens.MugaGold else AasritiColorTokens.DeepNortheastForest,
                            shape = RoundedCornerShape(18.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (hasPendingMeds) AasritiColorTokens.MugaGold else AasritiColorTokens.DeepNortheastForest)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (hasPendingMeds) "আজিৰ সৰ্বোচ্চ অগ্ৰাধিকাৰ (Today's Priority)" else "দৈনন্দিন অগ্ৰাধিকাৰ সম্পন্ন (Priority Completed)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (hasPendingMeds) AasritiColorTokens.MugaGold else AasritiColorTokens.DeepNortheastForest
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (hasPendingMeds) {
                                "পুৱাৰ ৰক্তচাপৰ ঔষধ নিশ্চিত কৰক। আইতাই ঔষধ সেৱন কৰিলে ক্লিক কৰি চিহ্নিত কৰক।"
                            } else {
                                "পুৱাৰ ৰক্তচাপৰ ঔষধ সম্পন্ন কৰা হৈছে। পৰৱৰ্তী: এগিলাচ কুহুমীয়া পানী।"
                            },
                            fontSize = 14.sp,
                            color = AasritiColorTokens.DeepCharcoal
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { DemoStateHolder.toggleRoutine("routine_1") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (hasPendingMeds) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.WarmSunkenSurface
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Text(
                                text = if (hasPendingMeds) "✓ ঔষধ লোৱা হ'ল বুলি চিহ্নিত কৰক" else "সম্পন্ন (Undo)",
                                color = if (hasPendingMeds) AasritiColorTokens.WarmIvory else AasritiColorTokens.DeepCharcoal,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 4. Daily Routine Progress Strip
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

                        val totalRoutines = AasritiDemoData.initialRoutines.size
                        val doneCount = DemoStateHolder.completedRoutineIds.size
                        Text(
                            text = "নিয়ম পালন: $doneCount / $totalRoutines সম্পন্ন (${(doneCount * 100) / totalRoutines}%)",
                            fontSize = 13.sp,
                            color = AasritiColorTokens.WarmSlate
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { doneCount.toFloat() / totalRoutines.toFloat() },
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

            // 5. Quick Action Grid
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
                            .height(56.dp)
                    ) {
                        Text("+ খৰতকীয়া টোকা (Log)", color = AasritiColorTokens.WarmIvory, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onOpenReminders(patient.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text("⏰ সোঁৱৰণী", color = AasritiColorTokens.DeepCharcoal, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
                            text = "চিকিৎসকে ৰোগীৰ খেল আৰু অগ্ৰগতি চাবলৈ এই ৬-অংকৰ ক'ডটো ব্যৱহাৰ কৰিব পাৰে।",
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
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder)
                            ) {
                                Text("নতুন ক'ড সৃষ্টি কৰক", color = AasritiColorTokens.DeepCharcoal, fontSize = 13.sp)
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

            // 7. Recent Logged Incident
            DemoStateHolder.lastLoggedIncidentText?.let { incident ->
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(AasritiColorTokens.SupportingSage.copy(alpha = 0.25f))
                            .border(1.dp, AasritiColorTokens.DeepNortheastForest, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text("শেহতীয়া টোকা (Recent Note):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.DeepNortheastForest)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(incident, fontSize = 14.sp, color = AasritiColorTokens.DeepCharcoal)
                        }
                    }
                }
            }
        }
    }

    // Quick Log Dialog (<30s Entry)
    if (showQuickLogDialog) {
        Dialog(onDismissRequest = { showQuickLogDialog = false }) {
            var selectedCategory by remember { mutableStateOf("ঔষধ (Medication)") }
            var noteInput by remember { mutableStateOf("পুৱাৰ আহাৰ আৰু ঔষধ সময়মতে লোৱা হ'ল।") }

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
                        text = "খৰতকীয়া পৰ্যবেক্ষণ টোকা (Quick Log)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val categories = listOf("ঔষধ (Medication)", "আহাৰ (Appetite)", "টোপনি (Sleep)", "মেজাজ (Mood)", "বিভ্ৰান্তি (Confusion)")
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        categories.forEach { cat ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selectedCategory == cat) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.SoftCream)
                                    .clickable { selectedCategory = cat }
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = cat,
                                    color = if (selectedCategory == cat) AasritiColorTokens.WarmIvory else AasritiColorTokens.DeepCharcoal,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        label = { Text("টোকা (Note)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { showQuickLogDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream)
                        ) {
                            Text("বাতিল", color = AasritiColorTokens.DeepCharcoal)
                        }

                        Button(
                            onClick = {
                                DemoStateHolder.recordCaregiverQuickLog(selectedCategory, noteInput)
                                showQuickLogDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest)
                        ) {
                            Text("সংৰক্ষণ কৰক", color = AasritiColorTokens.WarmIvory, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
