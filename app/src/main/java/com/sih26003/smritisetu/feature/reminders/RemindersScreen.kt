package com.sih26003.smritisetu.feature.reminders

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
import com.sih26003.smritisetu.data.local.entities.ReminderEntity
import com.sih26003.smritisetu.data.repository.ReminderRepository
import kotlinx.coroutines.launch

@Composable
fun RemindersScreen(
    patientId: String,
    reminderRepository: ReminderRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scheduler = remember { ReminderScheduler(context) }
    val reminders by reminderRepository.getActiveReminders(patientId).collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("পুৱাৰ ঔষধ (Morning Medicine)") }
    var hour by remember { mutableStateOf("09") }
    var minute by remember { mutableStateOf("00") }
    var reminderType by remember { mutableStateOf("MEDICINE") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262626)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("← উভতি যাওক", color = Color(0xFFFFD700), fontSize = 15.sp)
            }
            Button(
                onClick = { showAddDialog = !showAddDialog },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("+ সোঁৱৰণী যোগ কৰক", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("⏰ দৈনন্দিন অফলাইন সোঁৱৰণী", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
        Text("১০০% অফলাইন: কোনো ইন্টাৰনেটৰ প্ৰয়োজন নাই। সময় হ'লে এলাৰ্ম বাজি উঠিব।", fontSize = 13.sp, color = Color(0xFFBDBDBD))
        Spacer(modifier = Modifier.height(16.dp))

        if (showAddDialog) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
                    .border(2.dp, Color(0xFFFFD700), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text("নতুন সোঁৱৰণী (New Reminder)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("বিষয় (Title)", color = Color(0xFFBDBDBD)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = hour,
                            onValueChange = { hour = it },
                            label = { Text("ঘণ্টা (0-23)", color = Color(0xFFBDBDBD)) },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = minute,
                            onValueChange = { minute = it },
                            label = { Text("মিনিট (0-59)", color = Color(0xFFBDBDBD)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val r = ReminderEntity(
                                patientId = patientId,
                                title = title,
                                reminderType = reminderType,
                                hour = hour.toIntOrNull() ?: 9,
                                minute = minute.toIntOrNull() ?: 0,
                                isEnabled = true
                            )
                            scope.launch {
                                reminderRepository.saveReminder(r)
                                scheduler.scheduleReminder(r)
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("এলাৰ্ম সক্ৰিয় কৰক (Set Offline Alarm)", color = Color(0xFF121212), fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(reminders.size) { index ->
                val rem = reminders[index]
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1E1E), RoundedCornerShape(14.dp))
                        .border(1.5.dp, Color(0xFF424242), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(rem.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("সময়: ${rem.hour}:${if (rem.minute < 10) "0" else ""}${rem.minute} • ${rem.reminderType}", fontSize = 13.sp, color = Color(0xFF64B5F6))
                        }
                        Switch(
                            checked = rem.isEnabled,
                            onCheckedChange = { isChecked ->
                                scope.launch {
                                    reminderRepository.toggleReminder(rem, isChecked)
                                    if (isChecked) {
                                        scheduler.scheduleReminder(rem.copy(isEnabled = true))
                                    } else {
                                        scheduler.cancelReminder(rem.id)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
