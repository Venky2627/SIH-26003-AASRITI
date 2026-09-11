package com.sih26003.aasriti.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih26003.aasriti.core.security.CryptoUtils
import com.sih26003.aasriti.core.ui.components.AasritiLogoBadge
import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens
import com.sih26003.aasriti.data.local.entities.PatientEntity
import com.sih26003.aasriti.data.local.entities.UserEntity
import com.sih26003.aasriti.data.repository.PatientRepository
import com.sih26003.aasriti.data.repository.UserRepository
import com.sih26003.aasriti.demo.DemoStateHolder
import kotlinx.coroutines.launch

/**
 * SCREEN: Caregiver Registration & Onboarding.
 * Captures caregiver identity, relationship to elder, offline 6-digit PIN,
 * and associated patient.
 */
@Composable
fun CaregiverRegistrationScreen(
    userRepository: UserRepository,
    patientRepository: PatientRepository,
    onCaregiverRegistered: (String) -> Unit, // passes patientId to next step
    onBackClicked: () -> Unit
) {
    val isAssamese = DemoStateHolder.currentLanguage == "as"
    val scope = rememberCoroutineScope()
    val patients by patientRepository.allPatients.collectAsState(initial = emptyList())

    var nameInput by remember { mutableStateOf("") }
    var selectedRelation by remember { mutableStateOf("বোৱাৰী / প্ৰধান যত্ন লওঁতা (Daughter-in-law)") }
    var phoneInput by remember { mutableStateOf("") }
    var pinInput by remember { mutableStateOf("") }
    var pinConfirmInput by remember { mutableStateOf("") }
    var selectedPatientId by remember { mutableStateOf<String?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val relations = listOf(
        "বোৱাৰী / প্ৰধান যত্ন লওঁতা (Daughter-in-law)",
        "কন্যা (Daughter)",
        "পুত্ৰ (Son)",
        "স্বামী/পত্নী (Spouse)",
        "নাতি/নাতিনী (Grandchild)",
        "বন্ধু/প্ৰতিবেশী (Friend/Neighbor)"
    )

    LaunchedEffect(patients) {
        if (selectedPatientId == null && patients.isNotEmpty()) {
            selectedPatientId = patients.first().id
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AasritiColorTokens.WarmIvory)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClicked,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AasritiColorTokens.SoftCream)
                        .border(1.dp, AasritiColorTokens.WarmStoneBorder, CircleShape)
                ) {
                    Text("←", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AasritiColorTokens.DeepCharcoal)
                }

                AasritiLogoBadge(size = 46.dp)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Caregiver Setup",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepNortheastForest
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isAssamese) "যত্ন লওঁতাৰ পঞ্জীয়ন (Caregiver Setup)" else "Caregiver Registration",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.DeepCharcoal
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isAssamese) "জ্যেষ্ঠজনৰ কাষত থকা প্ৰধান ব্যক্তিৰ নাম আৰু নিৰাপদ পিন নিৰ্ধাৰণ কৰক।"
                else "Set up your caregiver profile, relationship to the elder, and local offline PIN.",
                fontSize = 13.sp,
                color = AasritiColorTokens.WarmSlate
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Caregiver Name
            OutlinedTextField(
                value = nameInput,
                onValueChange = {
                    nameInput = it
                    validationError = null
                },
                label = { Text(if (isAssamese) "যত্ন লওঁতাৰ সম্পূৰ্ণ নাম (Caregiver Name)" else "Caregiver Full Name") },
                placeholder = { Text("e.g. Mira Borah (মীৰা বৰা)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AasritiColorTokens.DeepNortheastForest,
                    unfocusedBorderColor = AasritiColorTokens.WarmStoneBorder
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Phone Contact
            OutlinedTextField(
                value = phoneInput,
                onValueChange = {
                    if (it.length <= 15) phoneInput = it
                },
                label = { Text(if (isAssamese) "যোগাযোগ নম্বৰ (Phone Number)" else "Emergency Contact Phone") },
                placeholder = { Text("+91 98640 12345") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AasritiColorTokens.DeepNortheastForest,
                    unfocusedBorderColor = AasritiColorTokens.WarmStoneBorder
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Relationship to Elder
            Text(
                text = if (isAssamese) "জ্যেষ্ঠজনৰ সৈতে সম্পৰ্ক (Relationship):" else "Relationship to Elder:",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = AasritiColorTokens.DeepCharcoal
            )

            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                relations.forEach { rel ->
                    val isSel = selectedRelation == rel
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.SoftCream)
                            .border(1.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(10.dp))
                            .clickable { selectedRelation = rel }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = rel,
                            fontSize = 13.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSel) AasritiColorTokens.WarmIvory else AasritiColorTokens.DeepCharcoal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Associated Patient Selector
            Text(
                text = if (isAssamese) "যত্ন লোৱা জ্যেষ্ঠ ব্যক্তি (Associated Elder):" else "Associated Elder Patient:",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = AasritiColorTokens.DeepCharcoal
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (patients.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AasritiColorTokens.SoftCream)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "আইতা বৰা (Aita Borah • AS-KAM-0042) [Default Demo Elder]",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepNortheastForest
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    patients.forEach { p ->
                        val isSel = selectedPatientId == p.id
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) AasritiColorTokens.SoftCream else AasritiColorTokens.WarmIvory)
                                .border(
                                    width = if (isSel) 2.dp else 1.dp,
                                    color = if (isSel) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.WarmStoneBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedPatientId = p.id }
                                .padding(12.dp)
                        ) {
                            Text(
                                text = p.pseudonymCode,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.DeepCharcoal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // PIN Inputs
            Text(
                text = if (isAssamese) "৬-অংকৰ স্থানীয় সুৰক্ষা পিন (6-digit Offline PIN):" else "6-Digit Local Offline PIN:",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = AasritiColorTokens.DeepCharcoal
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = {
                        if (it.length <= 6 && it.all { ch -> ch.isDigit() }) pinInput = it
                    },
                    label = { Text("PIN") },
                    placeholder = { Text("••••••") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = pinConfirmInput,
                    onValueChange = {
                        if (it.length <= 6 && it.all { ch -> ch.isDigit() }) pinConfirmInput = it
                    },
                    label = { Text("Confirm PIN") },
                    placeholder = { Text("••••••") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            if (validationError != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "💡 $validationError",
                    color = AasritiColorTokens.WarmAmberWarning,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Action CTA
        Button(
            onClick = {
                val trimmed = nameInput.trim()
                if (trimmed.isBlank()) {
                    validationError = "অনুগ্ৰহ কৰি যত্ন লওঁতাৰ নাম লিখক (Please enter caregiver name)."
                    return@Button
                }
                if (pinInput.length != 6) {
                    validationError = "অনুগ্ৰহ কৰি ৬-অংকৰ পিন লিখক (PIN must be 6 digits)."
                    return@Button
                }
                if (pinInput != pinConfirmInput) {
                    validationError = "দুয়োটা পিন একে হোৱা নাই (PINs do not match)."
                    return@Button
                }

                val patientId = selectedPatientId ?: com.sih26003.aasriti.demo.DemoPatientConfig.PATIENT_ID
                val pinHash = CryptoUtils.hashPin(pinInput)

                isSaving = true
                scope.launch {
                    userRepository.registerUser(
                        UserEntity(
                            role = "CAREGIVER",
                            pinHash = pinHash,
                            name = trimmed,
                            phoneHash = phoneInput.trim()
                        )
                    )
                    isSaving = false
                    onCaregiverRegistered(patientId)
                }
            },
            enabled = !isSaving,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(top = 10.dp)
        ) {
            Text(
                text = if (isSaving) "সংৰক্ষণ কৰি থকা হৈছে..."
                else if (isAssamese) "আগবাঢ়ক (Continue to Care Circle) ➔"
                else "Continue to Care Circle Setup ➔",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.WarmIvory
            )
        }
    }
}
