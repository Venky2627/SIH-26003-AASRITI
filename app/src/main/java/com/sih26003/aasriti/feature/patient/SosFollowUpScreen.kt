package com.sih26003.aasriti.feature.patient

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih26003.aasriti.core.ui.components.AasritiLogoBadge
import com.sih26003.aasriti.core.ui.components.CalmConnectivityPill
import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens
import com.sih26003.aasriti.data.local.entities.CareLogEntity
import com.sih26003.aasriti.data.repository.CareLogRepository
import com.sih26003.aasriti.demo.DemoPatientConfig
import com.sih26003.aasriti.demo.DemoStateHolder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * SCREEN 25 — SOS Follow-up & Resolution Protocol.
 * Facilitates post-incident welfare verification, immediate phone dispatch,
 * and immutable Room SQLite event resolution logging.
 */
@Composable
fun SosFollowUpScreen(
    careLogRepository: CareLogRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isAssamese = DemoStateHolder.currentLanguage == "as"
    val scrollState = rememberScrollState()

    var isResolved by remember { mutableStateOf(true) }
    var notesText by remember { mutableStateOf("") }
    var isSaved by remember { mutableStateOf(false) }

    val formattedTime = remember {
        val sdf = SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault())
        sdf.format(Date())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AasritiColorTokens.WarmIvory)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder)
            ) {
                Text(
                    text = if (isAssamese) "← উভতি যাওক" else "← Back",
                    color = AasritiColorTokens.DeepCharcoal,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            CalmConnectivityPill(isAssamese = isAssamese)
        }

        // Main Scrollable Content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header card
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(AasritiColorTokens.SoftCream)
                    .border(1.5.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                AasritiLogoBadge(size = 50.dp)
                Column {
                    Text(
                        text = if (isAssamese) "জৰুৰীকালীন অনুসৰণ" else "SOS Incident Follow-up",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal
                    )
                    Text(
                        text = if (isAssamese) "ঘটনা পৰিদৰ্শন আৰু সমাধান" else "Incident Review & Resolution",
                        fontSize = 13.sp,
                        color = AasritiColorTokens.WarmSlate
                    )
                }
            }

            // Attempt Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AasritiColorTokens.SoftCream),
                border = BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AasritiColorTokens.DeepCranberryEmergency.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🚨", fontSize = 24.sp)
                    }
                    Column {
                        Text(
                            text = if (isAssamese) "সংকেত লিপিবদ্ধ কৰা হৈছে" else "Emergency Alert Logged",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AasritiColorTokens.DeepCharcoal
                        )
                        Text(
                            text = "$formattedTime • ${if (isAssamese) "স্থানীয় সংৰক্ষণ" else "Local SQLite"}",
                            fontSize = 13.sp,
                            color = AasritiColorTokens.WarmSlate
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isAssamese) "নিযুক্ত আশা: হেমলতা ডেকা (+91 98765 43210)" else "Assigned ASHA: Hemlata Deka (+91 98765 43210)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = AasritiColorTokens.DeepNortheastForest
                        )
                    }
                }
            }

            // Resolution Status Selector
            Text(
                text = if (isAssamese) "বৰ্তমান স্থিতি বাছনি কৰক:" else "Select Current Status:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.DeepCharcoal
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Resolved Button
                Card(
                    onClick = { isResolved = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isResolved) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.SoftCream
                    ),
                    border = BorderStroke(1.5.dp, if (isResolved) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.WarmStoneBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("✓", fontSize = 22.sp, color = if (isResolved) Color.White else AasritiColorTokens.DeepNortheastForest, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (isAssamese) "সমাধান হ'ল" else "Resolved",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isResolved) Color.White else AasritiColorTokens.DeepCharcoal
                        )
                    }
                }

                // Follow-up Needed Button
                Card(
                    onClick = { isResolved = false },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (!isResolved) AasritiColorTokens.WarmAmberWarning else AasritiColorTokens.SoftCream
                    ),
                    border = BorderStroke(1.5.dp, if (!isResolved) AasritiColorTokens.WarmAmberWarning else AasritiColorTokens.WarmStoneBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("⏳", fontSize = 22.sp)
                        Text(
                            text = if (isAssamese) "দৃষ্টিৰ প্ৰয়োজন" else "Needs Follow-up",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isResolved) Color.White else AasritiColorTokens.DeepCharcoal
                        )
                    }
                }
            }

            // Observations / Notes
            OutlinedTextField(
                value = notesText,
                onValueChange = { notesText = it },
                label = { Text(if (isAssamese) "টোকা / পৰ্যবেক্ষণ (বৈকল্পিক)" else "Caregiver / ASHA Observation Notes (Optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AasritiColorTokens.DeepNortheastForest,
                    unfocusedBorderColor = AasritiColorTokens.WarmStoneBorder,
                    focusedTextColor = AasritiColorTokens.DeepCharcoal,
                    unfocusedTextColor = AasritiColorTokens.DeepCharcoal
                )
            )

            // Direct Contact Call Button
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:+919876543210")
                    }
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                border = BorderStroke(1.5.dp, AasritiColorTokens.DeepNortheastForest),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📞", fontSize = 18.sp)
                    Text(
                        text = if (isAssamese) "আশা হেমলতালৈ কল কৰক (+91 98765 43210)" else "Call ASHA Hemlata Deka",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepNortheastForest
                    )
                }
            }
        }

        // Bottom Save Action
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    scope.launch {
                        val statusStr = if (isResolved) "RESOLVED" else "NEEDS_FOLLOW_UP"
                        val note = if (notesText.isNotBlank()) " [$notesText]" else ""
                        careLogRepository.saveLog(
                            CareLogEntity(
                                patientId = DemoPatientConfig.PATIENT_ID,
                                authorRole = "CAREGIVER",
                                category = "SOS_EVENT",
                                severity = if (isResolved) "NORMAL" else "WATCH",
                                notes = "SOS Follow-up: $statusStr$note"
                            )
                        )
                        DemoStateHolder.dismissSos()
                        isSaved = true
                        onBack()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(
                    text = if (isSaved) {
                        if (isAssamese) "সংৰক্ষণ হ'ল ✓" else "Saved & Verified ✓"
                    } else {
                        if (isAssamese) "স্থিতি নিশ্চিত কৰি সংৰক্ষণ কৰক ➔" else "Save & Confirm Status ➔"
                    },
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = AasritiColorTokens.WarmIvory
                )
            }
        }
    }
}
