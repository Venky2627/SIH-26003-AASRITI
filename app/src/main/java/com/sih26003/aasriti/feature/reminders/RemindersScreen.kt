package com.sih26003.aasriti.feature.reminders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih26003.aasriti.AasritiApplication
import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens
import com.sih26003.aasriti.data.local.entities.ReminderEntity
import com.sih26003.aasriti.data.repository.ReminderRepository
import com.sih26003.aasriti.demo.DemoPatientConfig
import kotlinx.coroutines.launch

/**
 * AASRITI Offline Reminders Screen.
 * Managed by Caregiver and Health Workers for elderly routine alarms (Medicine, Hydration, Movement).
 * 100% Offline via Android AlarmManager and Room SQLite.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    patientId: String,
    reminderRepository: ReminderRepository? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as? AasritiApplication
    val resolvedRepo = reminderRepository ?: app?.reminderRepository
        ?: error("ReminderRepository unavailable")

    val resolvedPatientId = if (patientId.isBlank()) DemoPatientConfig.PATIENT_ID else patientId
    val scheduler = remember { ReminderScheduler(context) }
    val reminders by resolvedRepo.getActiveReminders(resolvedPatientId).collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("পুৱাৰ ঔষধ (Morning Medicine)") }
    var hour by remember { mutableStateOf("09") }
    var minute by remember { mutableStateOf("00") }
    var reminderType by remember { mutableStateOf("MEDICINE") }
    var formError by remember { mutableStateOf<String?>(null) }

    val categories = listOf(
        "MEDICINE" to "💊 ঔষধ (Medicine)",
        "HYDRATION" to "🥛 পানী (Hydration)",
        "EXERCISE" to "🚶 খোজ কঢ়া (Exercise)",
        "APPOINTMENT" to "📅 সাক্ষাৎ (Appointment)"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AasritiColorTokens.WarmIvory)
            .padding(16.dp)
    ) {
        // Top App Bar / Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AasritiColorTokens.SoftCream,
                    contentColor = AasritiColorTokens.DeepCharcoal
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                modifier = Modifier.heightIn(min = 56.dp)
            ) {
                Text("← উভতি যাওক (Back)", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = {
                    showAddDialog = !showAddDialog
                    formError = null
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AasritiColorTokens.DeepNortheastForest,
                    contentColor = AasritiColorTokens.WarmIvory
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.heightIn(min = 56.dp)
            ) {
                Text(
                    text = if (showAddDialog) "বাতিল (Cancel)" else "+ সোঁৱৰণী যোগ কৰক",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title and Offline Status Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AasritiColorTokens.SoftCream),
            border = androidx.compose.foundation.BorderStroke(1.dp, AasritiColorTokens.WarmStoneBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⏰ দৈনন্দিন অফলাইন সোঁৱৰণী",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "১০০% অফলাইন: কোনো ইন্টাৰনেটৰ প্ৰয়োজন নাই। এলাৰ্ম পোনে পোনে ডিভাইচত বাজিব।",
                    fontSize = 13.sp,
                    color = AasritiColorTokens.WarmSlate
                )
                Text(
                    text = "ৰোগী পৰিচয়: $resolvedPatientId (আইতা বৰা)",
                    fontSize = 12.sp,
                    color = AasritiColorTokens.DeepNortheastForest,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Add Reminder Collapsible Dialog Card
        if (showAddDialog) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AasritiColorTokens.SoftCream),
                border = androidx.compose.foundation.BorderStroke(2.dp, AasritiColorTokens.MugaGold)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "নতুন সোঁৱৰণী নিৰ্ধাৰণ (Create Offline Reminder)",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Category Selector Chips
                    Text(
                        text = "প্ৰকাৰ বাছক (Select Category):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = AasritiColorTokens.WarmSlate
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.take(2).forEach { (catKey, label) ->
                            val isSelected = reminderType == catKey
                            OutlinedButton(
                                onClick = {
                                    reminderType = catKey
                                    if (catKey == "MEDICINE") title = "পুৱাৰ ঔষধ (Morning Medicine)"
                                    if (catKey == "HYDRATION") title = "পানী খোৱাৰ সময় (Hydration)"
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.WarmSunkenSurface,
                                    contentColor = if (isSelected) AasritiColorTokens.WarmIvory else AasritiColorTokens.DeepCharcoal
                                ),
                                modifier = Modifier.weight(1f).heightIn(min = 44.dp)
                            ) {
                                Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.drop(2).forEach { (catKey, label) ->
                            val isSelected = reminderType == catKey
                            OutlinedButton(
                                onClick = {
                                    reminderType = catKey
                                    if (catKey == "EXERCISE") title = "খোজ কঢ়া (Light Walk)"
                                    if (catKey == "APPOINTMENT") title = "স্বাস্থ্য পৰীক্ষা (Health Check)"
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.WarmSunkenSurface,
                                    contentColor = if (isSelected) AasritiColorTokens.WarmIvory else AasritiColorTokens.DeepCharcoal
                                ),
                                modifier = Modifier.weight(1f).heightIn(min = 44.dp)
                            ) {
                                Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            formError = null
                        },
                        label = { Text("বিষয় / কাৰ্য্য (Title)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = AasritiColorTokens.WarmIvory,
                            unfocusedContainerColor = AasritiColorTokens.WarmIvory,
                            focusedBorderColor = AasritiColorTokens.DeepNortheastForest,
                            unfocusedBorderColor = AasritiColorTokens.WarmStoneBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = hour,
                            onValueChange = {
                                hour = it.filter { char -> char.isDigit() }.take(2)
                                formError = null
                            },
                            label = { Text("ঘণ্টা (00-23)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = AasritiColorTokens.WarmIvory,
                                unfocusedContainerColor = AasritiColorTokens.WarmIvory,
                                focusedBorderColor = AasritiColorTokens.DeepNortheastForest,
                                unfocusedBorderColor = AasritiColorTokens.WarmStoneBorder
                            )
                        )
                        OutlinedTextField(
                            value = minute,
                            onValueChange = {
                                minute = it.filter { char -> char.isDigit() }.take(2)
                                formError = null
                            },
                            label = { Text("মিনিট (00-59)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = AasritiColorTokens.WarmIvory,
                                unfocusedContainerColor = AasritiColorTokens.WarmIvory,
                                focusedBorderColor = AasritiColorTokens.DeepNortheastForest,
                                unfocusedBorderColor = AasritiColorTokens.WarmStoneBorder
                            )
                        )
                    }

                    formError?.let { err ->
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(err, color = AasritiColorTokens.DeepCranberryEmergency, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val parsedHour = hour.toIntOrNull()
                            val parsedMinute = minute.toIntOrNull()
                            if (title.isBlank()) {
                                formError = "অনুগ্ৰহ কৰি বিষয় উল্লেখ কৰক (Title cannot be empty)"
                                return@Button
                            }
                            if (parsedHour == null || parsedHour !in 0..23) {
                                formError = "ঘণ্টা ০ৰ পৰা ২৩ৰ ভিতৰত হ'ব লাগিব (Hour must be 00-23)"
                                return@Button
                            }
                            if (parsedMinute == null || parsedMinute !in 0..59) {
                                formError = "মিনিট ০ৰ পৰা ৫৯ৰ ভিতৰত হ'ব লাগিব (Minute must be 00-59)"
                                return@Button
                            }

                            val newReminder = ReminderEntity(
                                patientId = resolvedPatientId,
                                title = title.trim(),
                                reminderType = reminderType,
                                hour = parsedHour,
                                minute = parsedMinute,
                                isEnabled = true
                            )

                            scope.launch {
                                resolvedRepo.saveReminder(newReminder)
                                scheduler.scheduleReminder(newReminder)
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AasritiColorTokens.DeepNortheastForest,
                            contentColor = AasritiColorTokens.WarmIvory
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 64.dp)
                    ) {
                        Text(
                            text = "এলাৰ্ম সক্ৰিয় কৰক (Set Offline Alarm)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Reminders List / Honest Empty State
        if (reminders.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AasritiColorTokens.SoftCream),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "⏰", fontSize = 42.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "কোনো সক্ৰিয় সোঁৱৰণী নিৰ্ধাৰণ কৰা হোৱা নাই",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "বয়োজ্যেষ্ঠ ব্যক্তিৰ বাবে ঔষধ, পানী খোৱা বা খোজ কঢ়াৰ বাবে অফলাইন এলাৰ্ম নিৰ্ধাৰণ কৰিবলৈ ওপৰৰ '+ সোঁৱৰণী যোগ কৰক' বুটামত স্পৰ্শ কৰক। ইন্টাৰনেট নোহোৱাকৈও সময়মতে এলাৰ্ম বাজি উঠিব।",
                        fontSize = 13.sp,
                        color = AasritiColorTokens.WarmSlate,
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(reminders.size) { index ->
                    val rem = reminders[index]
                    val formattedTime = String.format("%02d:%02d", rem.hour, rem.minute)
                    val typeLabel = when (rem.reminderType) {
                        "MEDICINE" -> "💊 ঔষধ (Medicine)"
                        "HYDRATION" -> "🥛 পানী (Hydration)"
                        "EXERCISE" -> "🚶 খোজ কঢ়া (Exercise)"
                        else -> "📅 সাক্ষাৎ (Appointment)"
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (rem.isEnabled) AasritiColorTokens.SoftCream else AasritiColorTokens.WarmSunkenSurface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (rem.isEnabled) AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.4f) else AasritiColorTokens.WarmStoneBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = rem.title,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (rem.isEnabled) AasritiColorTokens.DeepCharcoal else AasritiColorTokens.WarmSlate
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "সময়: $formattedTime",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AasritiColorTokens.DeepNortheastForest
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "•  $typeLabel",
                                        fontSize = 12.sp,
                                        color = AasritiColorTokens.WarmSlate
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Switch(
                                    checked = rem.isEnabled,
                                    onCheckedChange = { isChecked ->
                                        scope.launch {
                                            resolvedRepo.toggleReminder(rem, isChecked)
                                            if (isChecked) {
                                                scheduler.scheduleReminder(rem.copy(isEnabled = true))
                                            } else {
                                                scheduler.cancelReminder(rem.id)
                                            }
                                        }
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = AasritiColorTokens.DeepNortheastForest,
                                        checkedTrackColor = AasritiColorTokens.SupportingSage,
                                        uncheckedThumbColor = AasritiColorTokens.WarmSlate,
                                        uncheckedTrackColor = AasritiColorTokens.WarmStoneBorder
                                    )
                                )

                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            scheduler.cancelReminder(rem.id)
                                            resolvedRepo.deleteReminder(rem.id)
                                        }
                                    },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Text(text = "✕", color = AasritiColorTokens.DeepCranberryEmergency, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
