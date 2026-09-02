package com.sih26003.smritisetu.feature.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih26003.smritisetu.data.local.entities.DoctorAccessEntity
import com.sih26003.smritisetu.data.local.entities.GameSessionEntity
import com.sih26003.smritisetu.data.repository.DoctorAccessRepository
import com.sih26003.smritisetu.data.repository.GameRepository
import com.sih26003.smritisetu.data.repository.PatientRepository
import kotlinx.coroutines.launch

@Composable
fun DoctorAccessScreen(
    doctorAccessRepository: DoctorAccessRepository,
    patientRepository: PatientRepository,
    gameRepository: GameRepository,
    onBack: () -> Unit
) {
    var accessCodeInput by remember { mutableStateOf("") }
    var approvedAccess by remember { mutableStateOf<DoctorAccessEntity?>(null) }
    var sessions by remember { mutableStateOf<List<GameSessionEntity>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262626)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("← উভতি যাওক", color = Color(0xFFFFD700), fontSize = 15.sp)
            }
            if (approvedAccess != null) {
                Button(
                    onClick = {
                        scope.launch {
                            approvedAccess?.let { doctorAccessRepository.revokeAccess(it.id) }
                            approvedAccess = null
                            sessions = emptyList()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E1010)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("প্ৰৱেশ বাতিল (Revoke Access)", color = Color(0xFFFF8A80), fontSize = 13.sp)
                }
            }
        }

        if (approvedAccess == null) {
            // Code Entry
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "🩺 চিকিৎসকৰ পৰিদৰ্শন (Doctor Access)",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "যত্ন লওঁতাই প্ৰদান কৰা ৬-অংকৰ প্ৰৱেশ সংকেত লিখক।",
                    fontSize = 15.sp,
                    color = Color(0xFFE0E0E0),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = accessCodeInput,
                    onValueChange = {
                        if (it.length <= 6) {
                            accessCodeInput = it
                            errorMessage = null
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 24.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700)),
                    placeholder = { Text("123456", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Color(0xFF757575)) }
                )

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("💡 $it", color = Color(0xFFFFA000), fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        scope.launch {
                            val access = doctorAccessRepository.verifyDoctorAccess(accessCodeInput)
                            if (access != null) {
                                approvedAccess = access
                                sessions = gameRepository.getAllSessionsList(access.patientId)
                            } else {
                                errorMessage = "সংকেতটো অবৈধ বা বাতিল কৰা হৈছে। (Invalid or revoked code)"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("তথ্য চাওক (View Approved Records) ➔", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF121212))
                }
            }
        } else {
            // Approved Clinical Game Signals View
            Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Text("ৰোগীৰ সংকেত: ${approvedAccess?.patientId?.take(10)}...", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                Text("লক্ষণীয়: এই তথ্য কেৱল খেলৰ কাৰ্যক্ষমতা আৰু প্ৰতিক্ৰিয়াৰ সময়। কোনো চিকিৎসা নিদান নহয়।", fontSize = 13.sp, color = Color(0xFFBDBDBD))
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(sessions.size) { i ->
                        val s = sessions[i]
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFF424242), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("খেল: ${s.gameId}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("স্তৰ: ${s.difficultyLevel} • শুদ্ধতা: ${(s.accuracy * 100).toInt()}% • প্ৰতিক্ৰিয়া সময়: ${s.reactionTimeMs}ms", fontSize = 13.sp, color = Color(0xFF64B5F6))
                                Text("অনিশ্চয়তা (Hesitations): ${s.hesitationCount} • ভুল: ${s.errors}", fontSize = 12.sp, color = Color(0xFFBDBDBD))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}
