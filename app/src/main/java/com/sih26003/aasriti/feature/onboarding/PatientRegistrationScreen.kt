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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih26003.aasriti.core.ui.components.AasritiLogoBadge
import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens
import com.sih26003.aasriti.data.local.entities.PatientEntity
import com.sih26003.aasriti.data.local.entities.RelationshipEntity
import com.sih26003.aasriti.data.repository.PatientRepository
import com.sih26003.aasriti.demo.DemoStateHolder
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Dynamic Patient Registration & Elder Identity Screen.
 * Saves real PatientEntity into Room database (Source of Truth).
 */
@Composable
fun PatientRegistrationScreen(
    patientRepository: PatientRepository,
    initialRegion: String = "ASSAM",
    initialLanguage: String = "as",
    onPatientRegistered: (PatientEntity) -> Unit,
    onBackClicked: () -> Unit
) {
    val isAssamese = DemoStateHolder.currentLanguage == "as" && initialLanguage != "en"
    val scope = rememberCoroutineScope()

    var nameInput by remember { mutableStateOf("") }
    var ageInput by remember { mutableStateOf("72") }
    var selectedGender by remember { mutableStateOf("F") }
    var selectedAvatar by remember { mutableStateOf("👵") }
    var selectedStage by remember { mutableStateOf("Early Stage Care") }
    var validationError by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val avatars = listOf("👵", "👴", "🌸", "🌿", "🦚", "🏡")

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

                Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isAssamese) "জ্যেষ্ঠজনৰ পৰিচয় নিৰ্ধাৰণ" else "Elder Profile & Identity",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.DeepCharcoal
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isAssamese) "ঘৰুৱাভাৱে মতা নাম আৰু প্ৰাথমিক যত্নৰ বিৱৰণ দিয়ক।"
                else "Enter the elder's familiar name, age, and comfort preferences.",
                fontSize = 14.sp,
                color = AasritiColorTokens.WarmSlate
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Avatar Picker
            Text(
                text = if (isAssamese) "প্ৰ'ফাইল প্ৰতীক বাছক:" else "Choose Profile Avatar:",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AasritiColorTokens.DeepCharcoal
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                avatars.forEach { av ->
                    val isSelected = selectedAvatar == av
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.2f)
                                else AasritiColorTokens.SoftCream
                            )
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.WarmStoneBorder,
                                shape = CircleShape
                            )
                            .clickable { selectedAvatar = av },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(av, fontSize = 26.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Name Input Field
            OutlinedTextField(
                value = nameInput,
                onValueChange = {
                    nameInput = it
                    validationError = null
                },
                label = { Text(if (isAssamese) "জ্যেষ্ঠজনৰ নাম (Elder's Name)" else "Elder's Preferred Name") },
                placeholder = { Text(if (isAssamese) "যেনে: আইতা বৰা (Aita Borah)" else "e.g. Aita Borah, Kaka Sharma") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AasritiColorTokens.DeepNortheastForest,
                    unfocusedBorderColor = AasritiColorTokens.WarmStoneBorder,
                    focusedLabelColor = AasritiColorTokens.DeepNortheastForest
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Age and Gender Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = ageInput,
                    onValueChange = {
                        if (it.length <= 3 && it.all { ch -> ch.isDigit() }) {
                            ageInput = it
                        }
                    },
                    label = { Text(if (isAssamese) "বয়স (Age)" else "Age") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AasritiColorTokens.DeepNortheastForest,
                        unfocusedBorderColor = AasritiColorTokens.WarmStoneBorder
                    ),
                    singleLine = true
                )

                Column(modifier = Modifier.weight(1.5f)) {
                    Text(
                        text = if (isAssamese) "লিংগ (Gender)" else "Gender",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AasritiColorTokens.WarmSlate
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val genderOptions = if (isAssamese) listOf("F" to "মহিলা", "M" to "পুৰুষ") else listOf("F" to "Female", "M" to "Male")
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        genderOptions.forEach { (code, label) ->
                            val isSel = selectedGender == code
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSel) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.SoftCream)
                                    .border(1.dp, AasritiColorTokens.WarmStoneBorder, RoundedCornerShape(12.dp))
                                    .clickable { selectedGender = code },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) AasritiColorTokens.WarmIvory else AasritiColorTokens.DeepCharcoal
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Doctor Diagnosis / Cognitive Stage
            Text(
                text = if (isAssamese) "চিকিৎসকৰ নিদান (Doctor Diagnosis):" else "Doctor Diagnosis:",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = AasritiColorTokens.DeepCharcoal
            )

            Spacer(modifier = Modifier.height(6.dp))

            val stageOptions = if (isAssamese) {
                listOf(
                    "Early Stage Care" to "প্ৰাৰম্ভিক যত্ন • দৈনন্দিন নিয়ম আৰু পৰিয়ালৰ মাত (Daily Routine & Voice)",
                    "Mild Dementia Stage" to "মৃদু স্তৰ • স্মৃতি উদ্দীপনা আৰু চিনাকি সংগী (Gentle Memory Support)",
                    "Severe Stage" to "উচ্চ স্তৰ • শান্ত পৰিৱেশ আৰু নিৰন্তৰ সংগী (Compassionate Comfort)"
                )
            } else {
                listOf(
                    "Early Stage Care" to "Daily Routine & Family Voice Guidance",
                    "Mild Dementia Stage" to "Gentle Memory Stimulation & Familiar Face Support",
                    "Severe Stage" to "Compassionate Comfort, Calming Audio & Constant Companion"
                )
            }

            stageOptions.forEach { (stage, desc) ->
                val isSel = selectedStage == stage
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSel) AasritiColorTokens.SoftCream else AasritiColorTokens.WarmIvory)
                        .border(
                            width = if (isSel) 2.dp else 1.dp,
                            color = if (isSel) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.WarmStoneBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedStage = stage }
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = stage,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.DeepCharcoal
                        )
                        Text(
                            text = desc,
                            fontSize = 11.sp,
                            color = AasritiColorTokens.WarmSlate
                        )
                    }
                }
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

        // Save Button
        Button(
            onClick = {
                val trimmedName = nameInput.trim()
                if (trimmedName.isBlank()) {
                    validationError = if (isAssamese) "অনুগ্ৰহ কৰি জ্যেষ্ঠজনৰ নাম লিখক।" else "Please enter the elder's name."
                    return@Button
                }

                val ageNum = ageInput.toIntOrNull() ?: 70
                val birthYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) - ageNum

                val randomSuffix = (1000..9999).random()
                val codePrefix = when (initialRegion) {
                    "MANIPUR" -> "MN-IMP"
                    "MEGHALAYA" -> "ML-SHI"
                    else -> "AS-KAM"
                }
                val generatedPseudonym = "$trimmedName • $codePrefix-$randomSuffix"

                val newPatient = PatientEntity(
                    id = UUID.randomUUID().toString(),
                    pseudonymCode = generatedPseudonym,
                    birthYear = birthYear,
                    gender = selectedGender,
                    primaryLanguage = initialLanguage,
                    cognitiveStage = selectedStage
                )

                isSaving = true
                scope.launch {
                    patientRepository.savePatient(newPatient)

                    // Seed default affectionate family relationships for Family Trivia & Care Circle
                    patientRepository.addRelationship(
                        RelationshipEntity(
                            patientId = newPatient.id,
                            name = "ৰূপম (Rupam)",
                            relationshipType = "পুত্ৰ (Son)"
                        )
                    )
                    patientRepository.addRelationship(
                        RelationshipEntity(
                            patientId = newPatient.id,
                            name = "মীৰা (Mira)",
                            relationshipType = "বোৱাৰী / যত্ন লওঁতা (Daughter-in-law)"
                        )
                    )
                    patientRepository.addRelationship(
                        RelationshipEntity(
                            patientId = newPatient.id,
                            name = "প্ৰীতম (Pritam)",
                            relationshipType = "নাতি (Grandson)"
                        )
                    )

                    isSaving = false
                    onPatientRegistered(newPatient)
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
                text = if (isSaving) (if (isAssamese) "সংৰক্ষণ কৰি থকা হৈছে..." else "Saving...")
                else if (isAssamese) "প্ৰ'ফাইল সংৰক্ষণ কৰক (Save & Continue) ➔"
                else "Save Profile & Continue ➔",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.WarmIvory
            )
        }
    }
}
