package com.sih26003.smritisetu.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih26003.smritisetu.core.security.CryptoUtils
import com.sih26003.smritisetu.data.local.entities.PatientEntity
import com.sih26003.smritisetu.data.local.entities.UserEntity
import com.sih26003.smritisetu.data.repository.PatientRepository
import com.sih26003.smritisetu.data.repository.UserRepository
import kotlinx.coroutines.launch

@Composable
fun RoleAndModeSelectScreen(
    patientRepository: PatientRepository,
    onPatientSelected: (PatientEntity) -> Unit,
    onCaregiverLoginSelected: () -> Unit,
    onDoctorLoginSelected: () -> Unit
) {
    val patients by patientRepository.allPatients.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // App Header
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("🌿", fontSize = 48.sp)
            Text(
                "স্মৃতিসেতু (SmritiSetu)",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700)
            )
            Text(
                "জ্ঞানমূলক পুনৰুদ্ধাৰ আৰু স্মৃতি সহায়ক",
                fontSize = 15.sp,
                color = Color(0xFFE0E0E0)
            )
        }

        // Patient Photo Cards (NO PIN REQUIRED for Patient)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "খেলিবলৈ ফটো স্পৰ্শ কৰক: (Tap photo to play)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (patients.isEmpty()) {
                // Default starter profile if first run
                val demoPatient = remember {
                    PatientEntity(
                        id = "starter_patient",
                        pseudonymCode = "AS-DEMO-01",
                        birthYear = 1954,
                        gender = "M",
                        primaryLanguage = "as"
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
                        .border(2.dp, Color(0xFF00E676), RoundedCornerShape(16.dp))
                        .clickable { onPatientSelected(demoPatient) }
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🧓", fontSize = 48.sp, modifier = Modifier.padding(end = 16.dp))
                        Column {
                            Text("বোপা (Grandfather)", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                            Text("স্পৰ্শ কৰি খেল আৰম্ভ কৰক (Tap to play)", fontSize = 14.sp, color = Color(0xFF00E676))
                        }
                    }
                }
            } else {
                patients.forEach { patient ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
                            .border(2.dp, Color(0xFF00E676), RoundedCornerShape(16.dp))
                            .clickable { onPatientSelected(patient) }
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🧓", fontSize = 40.sp, modifier = Modifier.padding(end = 16.dp))
                            Column {
                                Text(patient.pseudonymCode, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                                Text("মাতৃভাষা: ${patient.primaryLanguage} • স্তৰ: ${patient.cognitiveStage}", fontSize = 13.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Caregiver & Doctor Administration Entry Points (PIN Protected)
        Column(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onCaregiverLoginSelected,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E1E)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(1.5.dp, Color(0xFF64B5F6), RoundedCornerShape(14.dp))
            ) {
                Text("🤝 যত্ন লওঁতাৰ প্ৰৱেশ (Caregiver PIN Mode)", fontSize = 16.sp, color = Color(0xFF64B5F6), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onDoctorLoginSelected,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E1E)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(1.5.dp, Color(0xFFFFB74D), RoundedCornerShape(14.dp))
            ) {
                Text("🩺 চিকিৎসক / আশা কৰ্মী (Doctor Access)", fontSize = 16.sp, color = Color(0xFFFFB74D), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PinAuthScreen(
    role: String, // "CAREGIVER" or "DOCTOR"
    userRepository: UserRepository,
    onSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isNewSetup by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(role) {
        val exists = userRepository.hasUser(role)
        isNewSetup = !exists
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262626)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("← উভতি যাওক", color = Color(0xFFFFD700), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                if (role == "CAREGIVER") "🤝 যত্ন লওঁতাৰ পিন (Caregiver PIN)" else "🩺 চিকিৎসকৰ পিন (Doctor PIN)",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                if (isNewSetup) "প্ৰথমবাৰৰ বাবে ৬-অংকৰ পিন এটা নিৰ্ধাৰণ কৰক।" else "প্ৰৱেশ কৰিবলৈ আপোনাৰ ৬-অংকৰ পিন দিয়ক।",
                fontSize = 16.sp,
                color = Color(0xFFE0E0E0),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = enteredPin,
                onValueChange = {
                    if (it.length <= 6 && it.all { ch -> ch.isDigit() }) {
                        enteredPin = it
                        errorMessage = null
                    }
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFFD700),
                    unfocusedBorderColor = Color(0xFF424242),
                    focusedTextColor = Color(0xFFFFD700),
                    unfocusedTextColor = Color(0xFFFFD700),
                    cursorColor = Color(0xFFFFD700)
                ),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 24.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold),
                singleLine = true,
                placeholder = { Text("••••••", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Color(0xFF757575)) }
            )

            errorMessage?.let {
                Spacer(modifier = Modifier.height(10.dp))
                Text("💡 $it", color = Color(0xFFFFA000), fontSize = 14.sp)
            }
        }

        Button(
            onClick = {
                if (enteredPin.length == 6) {
                    val hash = CryptoUtils.hashPin(enteredPin)
                    scope.launch {
                        if (isNewSetup) {
                            userRepository.registerUser(
                                UserEntity(
                                    role = role,
                                    pinHash = hash,
                                    name = if (role == "CAREGIVER") "মুখ্য যত্ন লওঁতা" else "পৰিদৰ্শক চিকিৎসক"
                                )
                            )
                            onSuccess()
                        } else {
                            val user = userRepository.authenticatePin(hash)
                            if (user != null) {
                                onSuccess()
                            } else {
                                errorMessage = "পিনটো ভুল হৈছে। পুনৰ চেষ্টা কৰক। (Invalid PIN)"
                            }
                        }
                    }
                } else {
                    errorMessage = "অনুগ্ৰহ কৰি ৬-অংকৰ পিন লিখক। (Enter 6 digits)"
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Text(if (isNewSetup) "পিন সংৰক্ষণ কৰক (Save PIN) ➔" else "প্ৰৱেশ কৰক (Enter) ➔", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF121212))
        }
    }
}
