package com.sih26003.smritisetu.feature.caregiver

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih26003.smritisetu.core.security.CryptoUtils
import com.sih26003.smritisetu.data.local.entities.DoctorAccessEntity
import com.sih26003.smritisetu.data.local.entities.PatientEntity
import com.sih26003.smritisetu.data.local.entities.RelationshipEntity
import com.sih26003.smritisetu.data.repository.DoctorAccessRepository
import com.sih26003.smritisetu.data.repository.GameRepository
import com.sih26003.smritisetu.data.repository.PatientRepository
import kotlinx.coroutines.launch

@Composable
fun CaregiverDashboardScreen(
    patientRepository: PatientRepository,
    gameRepository: GameRepository,
    doctorAccessRepository: DoctorAccessRepository,
    onOpenReminders: (String) -> Unit,
    onLogout: () -> Unit
) {
    val patients by patientRepository.allPatients.collectAsState(initial = emptyList())
    var selectedPatientId by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Form states for creating patient
    var showAddPatientDialog by remember { mutableStateOf(false) }
    var newPseudonym by remember { mutableStateOf("AS-PAT-00" + (10..99).random()) }
    var newBirthYear by remember { mutableStateOf("1954") }
    var newLanguage by remember { mutableStateOf("as") }

    // Relationship states
    var newRelativeName by remember { mutableStateOf("") }
    var newRelativeType by remember { mutableStateOf("SON") }

    // Generated Doctor Access Code state
    var generatedDoctorCode by remember { mutableStateOf<String?>(null) }

    val activePatient = patients.firstOrNull { it.id == selectedPatientId } ?: patients.firstOrNull()
    val relationships by patientRepository.getRelationships(activePatient?.id ?: "").collectAsState(initial = emptyList())
    val sessions by gameRepository.getSessionsForPatient(activePatient?.id ?: "").collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(18.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🤝 যত্ন লওঁতাৰ ডেশ্ববৰ্ড", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262626)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("প্ৰস্থান (Logout)", color = Color(0xFFFFA000), fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // 1. Patient Profile Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
                        .border(1.5.dp, Color(0xFF424242), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("সক্ৰিয় খেলুৱৈ: ${activePatient?.pseudonymCode ?: "নাই"}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Button(
                                onClick = { showAddPatientDialog = !showAddPatientDialog },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("+ নতুন ৰোগী", color = Color.White, fontSize = 13.sp)
                            }
                        }

                        if (showAddPatientDialog) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = newPseudonym,
                                onValueChange = { newPseudonym = it },
                                label = { Text("গোপন সংকেত (Pseudonym)", color = Color(0xFFBDBDBD)) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = newBirthYear,
                                onValueChange = { newBirthYear = it },
                                label = { Text("জন্মৰ বছৰ (Birth Year)", color = Color(0xFFBDBDBD)) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        val p = PatientEntity(
                                            pseudonymCode = newPseudonym,
                                            birthYear = newBirthYear.toIntOrNull() ?: 1954,
                                            gender = "M",
                                            primaryLanguage = newLanguage,
                                            linkCode = CryptoUtils.generateSixDigitCode()
                                        )
                                        patientRepository.savePatient(p)
                                        showAddPatientDialog = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("সংৰক্ষণ কৰক (Save Patient)", color = Color(0xFF121212), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 2. Add Family Member for Family Trivia
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
                        .border(1.5.dp, Color(0xFF424242), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text("👨‍👩‍👧 পৰিয়ালৰ সদস্য যোগ কৰক (Add Family)", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                        Text("Family Trivia খেলৰ বাবে চিনাকি নাম আৰু সম্পৰ্ক লিখক।", fontSize = 13.sp, color = Color(0xFFBDBDBD))
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = newRelativeName,
                            onValueChange = { newRelativeName = it },
                            label = { Text("সদস্যৰ নাম (Name, e.g. ৰূপম বৰা)", color = Color(0xFFBDBDBD)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                if (newRelativeName.isNotBlank() && activePatient != null) {
                                    scope.launch {
                                        patientRepository.addRelationship(
                                            RelationshipEntity(
                                                patientId = activePatient.id,
                                                name = newRelativeName,
                                                relationshipType = newRelativeType
                                            )
                                        )
                                        newRelativeName = ""
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("+ পৰিয়ালৰ সদস্য সংৰক্ষণ কৰক", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        if (relationships.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("সংৰক্ষিত সদস্যসকল: ${relationships.joinToString { it.name }}", fontSize = 13.sp, color = Color(0xFF00E676))
                        }
                    }
                }
            }

            // 3. Reminders Shortcut
            item {
                Button(
                    onClick = {
                        activePatient?.let { onOpenReminders(it.id) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("⏰ দৈনন্দিন সোঁৱৰণী পৰিচালনা (Manage Reminders)", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            // 4. Doctor Access Generator
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
                        .border(1.5.dp, Color(0xFF424242), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text("🩺 চিকিৎসকৰ প্ৰৱেশ সংকেত (Doctor Access Code)", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                        Text("চিকিৎসক বা আশা কৰ্মীয়ে ৰোগীৰ খেলৰ অগ্ৰগতি চাবলৈ এই ৬-অংকৰ ক'ডটো ব্যৱহাৰ কৰিব পাৰে।", fontSize = 13.sp, color = Color(0xFFBDBDBD))
                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                if (activePatient != null) {
                                    val code = CryptoUtils.generateSixDigitCode()
                                    generatedDoctorCode = code
                                    scope.launch {
                                        doctorAccessRepository.grantAccess(
                                            DoctorAccessEntity(
                                                patientId = activePatient.id,
                                                doctorAccessCode = code,
                                                doctorName = "পৰিদৰ্শক চিকিৎসক"
                                            )
                                        )
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("৬-অংকৰ ক'ড সৃষ্টি কৰক (Generate 6-Digit Code)", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        generatedDoctorCode?.let {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("চিকিৎসকৰ সংকেত: $it", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                        }
                    }
                }
            }

            // 5. Longitudinal Game Performance (NOT a dementia diagnosis)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
                        .border(1.5.dp, Color(0xFF424242), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text("📊 খেলৰ প্ৰদৰ্শন সংকেত (Game Performance Metrics)", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                        Text("This measures game interaction metrics, NOT a medical dementia diagnosis.", fontSize = 12.sp, color = Color(0xFFBDBDBD))
                        Spacer(modifier = Modifier.height(10.dp))

                        if (sessions.isEmpty()) {
                            Text("বৰ্তমানলৈকে কোনো খেলৰ তথ্য সংৰক্ষিত হোৱা নাই। (No game sessions recorded yet)", fontSize = 14.sp, color = Color(0xFF757575))
                        } else {
                            sessions.take(5).forEach { s ->
                                Text(
                                    "• ${s.gameId} | স্তৰ: ${s.difficultyLevel} | শুদ্ধতা: ${(s.accuracy * 100).toInt()}% | সময়: ${s.durationMs / 1000}s",
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
