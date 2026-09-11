package com.sih26003.aasriti.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sih26003.aasriti.core.security.CryptoUtils
import com.sih26003.aasriti.core.ui.components.AasritiLogoBadge
import com.sih26003.aasriti.core.ui.components.CalmConnectivityPill
import com.sih26003.aasriti.core.ui.components.LanguageTogglePill
import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens
import com.sih26003.aasriti.data.local.entities.PatientEntity
import com.sih26003.aasriti.data.local.entities.UserEntity
import com.sih26003.aasriti.data.repository.PatientRepository
import com.sih26003.aasriti.data.repository.UserRepository
import com.sih26003.aasriti.demo.AasritiDemoData
import com.sih26003.aasriti.demo.DemoStateHolder
import kotlinx.coroutines.launch

/**
 * SCREEN_ONBOARDING_ROLE_SELECT:
 * Authoritative entry point for AASRITI platform.
 * Features 4 discrete stakeholder pathways:
 * 1. Elder / Patient (Zero PIN, Direct Photo Tap)
 * 2. Family Caregiver (Local 6-digit PIN)
 * 3. ASHA Community Worker (Local PIN / Worker Mode)
 * 4. Doctor / Clinician (Clinical Access Code)
 */
@Suppress("UNUSED_PARAMETER")
@Composable
fun RoleAndModeSelectScreen(
    patientRepository: PatientRepository,
    onPatientSelected: (PatientEntity) -> Unit,
    onCaregiverLoginSelected: () -> Unit,
    onAshaLoginSelected: () -> Unit,
    onDoctorLoginSelected: () -> Unit,
    onConsentSelected: (() -> Unit)? = null,
    onStartOnboarding: (() -> Unit)? = null,
    onRegisterPatientSelected: (() -> Unit)? = null,
    onRegisterCaregiverSelected: (() -> Unit)? = null
) {
    var showDevMenu by remember { mutableStateOf(false) }
    val isAssamese = DemoStateHolder.currentLanguage == "as"
    val allPatients by patientRepository.allPatients.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AasritiColorTokens.WarmIvory)
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Utility Bar: Language Selector & Calm Connectivity Indicator (from Prototype Screen 1)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LanguageTogglePill(
                currentLanguage = DemoStateHolder.currentLanguage,
                onLanguageSelected = { DemoStateHolder.currentLanguage = it }
            )
            CalmConnectivityPill(isAssamese = isAssamese)
        }

        if (onStartOnboarding != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onStartOnboarding() },
                shape = RoundedCornerShape(12.dp),
                color = AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.08f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AasritiColorTokens.DeepNortheastForest.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isAssamese) "🌸 প্ৰথমবাৰ আহিছে? আৰম্ভণি পৰিদৰ্শন (Welcome Tour)" else "🌸 New here? Start Guided Onboarding Tour",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AasritiColorTokens.DeepNortheastForest
                    )
                    Text("➔", fontSize = 12.sp, color = AasritiColorTokens.DeepNortheastForest)
                }
            }
        }

        // 1. App Heritage Branding Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            AasritiLogoBadge(size = 62.dp)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "আশ্ৰীতি (AASRITI)",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.DeepCharcoal
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (isAssamese) {
                    "উত্তৰ-পূব ভাৰতৰ জ্যেষ্ঠসকলৰ বাবে AI-ভিত্তিক স্মৃতি আৰু যত্ন মঞ্চ"
                } else {
                    "AI-Based Cognitive Gaming & Memory Assistance Platform"
                },
                fontSize = 12.sp,
                color = AasritiColorTokens.WarmSlate,
                textAlign = TextAlign.Center
            )
        }

        // 2. Patient Direct Photo Tap Arena (NO PIN for Patient)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isAssamese) "খেলিবলৈ ফটো স্পৰ্শ কৰক:" else "Touch photo to begin playing:",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.DeepCharcoal,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            val displayPatients = if (allPatients.isNotEmpty()) allPatients else listOf(remember { com.sih26003.aasriti.demo.DemoPatientConfig.createCanonicalPatient() })

            displayPatients.take(2).forEach { p ->
                val namePart = p.pseudonymCode.substringBefore(" •").ifBlank { p.pseudonymCode }
                val subPart = p.pseudonymCode.substringAfter(" •", p.primaryLanguage)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(AasritiColorTokens.SoftCream)
                        .border(2.5.dp, AasritiColorTokens.DeepNortheastForest, RoundedCornerShape(20.dp))
                        .clickable { onPatientSelected(p) }
                        .padding(14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(AasritiColorTokens.MutedHeritageTerracotta.copy(alpha = 0.15f))
                                    .border(2.dp, AasritiColorTokens.MutedHeritageTerracotta, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👵", fontSize = 32.sp)
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = namePart,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AasritiColorTokens.DeepCharcoal
                                )
                                Text(
                                    text = subPart,
                                    fontSize = 13.sp,
                                    color = AasritiColorTokens.WarmSlate
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "কামৰূপ গ্ৰাম্য • ${p.primaryLanguage}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AasritiColorTokens.DeepNortheastForest
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AasritiColorTokens.DeepNortheastForest),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("➔", color = AasritiColorTokens.WarmIvory, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (onRegisterPatientSelected != null || onStartOnboarding != null) {
                OutlinedButton(
                    onClick = { (onRegisterPatientSelected ?: onStartOnboarding)?.invoke() },
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.DeepNortheastForest),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = if (isAssamese) "+ নতুন জ্যেষ্ঠ পঞ্জীয়ন কৰক (Add Elder)" else "+ Register New Elder (Onboarding)",
                        color = AasritiColorTokens.DeepNortheastForest,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // 3. Administrative / Caregiver / Clinician Role Pathways
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Caregiver Mode Button
            Button(
                onClick = onCaregiverLoginSelected,
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAssamese) "🤝 যত্ন লওঁতাৰ প্ৰৱেশ (Caregiver PIN)" else "🤝 Family Caregiver Mode",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal
                    )
                    Text("🔒 PIN", fontSize = 13.sp, color = AasritiColorTokens.WarmSlate)
                }
            }

            if (onRegisterCaregiverSelected != null) {
                Text(
                    text = if (isAssamese) "নতুন যত্ন লওঁতা? পঞ্জীয়ন আৰু PIN ছেট কৰক ➔" else "New Caregiver? Register profile & PIN ➔",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AasritiColorTokens.DeepNortheastForest,
                    modifier = Modifier
                        .clickable { onRegisterCaregiverSelected() }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            // ASHA Community Worker Mode Button
            Button(
                onClick = onAshaLoginSelected,
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAssamese) "🏡 আশা কৰ্মীৰ ৰষ্টাৰ (ASHA Worker)" else "🏡 ASHA Community Roster",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepNortheastForest
                    )
                    Text("১৪ গৰাকী", fontSize = 13.sp, color = AasritiColorTokens.DeepNortheastForest)
                }
            }

            // Doctor Access Button
            Button(
                onClick = onDoctorLoginSelected,
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAssamese) "🩺 চিকিৎসকৰ পৰিদৰ্শন (Doctor Access)" else "🩺 Clinician Longitudinal Signals",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.MutedHeritageTerracotta
                    )
                    Text("ক'ড", fontSize = 13.sp, color = AasritiColorTokens.WarmSlate)
                }
            }
        }

        // 4. Subtle Footer with Discreet Developer / Demo Reset Trigger & Privacy Link
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            onConsentSelected?.let { onConsent ->
                Text(
                    text = if (isAssamese) "🔒 যত্ন আৰু গোপনীয়তা নীতি (Care & Privacy • DPDPA 2023) ➔" else "🔒 Care & Privacy • DPDPA 2023 ➔",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AasritiColorTokens.DeepNortheastForest,
                    modifier = Modifier
                        .clickable { onConsent() }
                        .padding(vertical = 4.dp)
                )
            }

            Text(
                text = "AASRITI • SIH-26003 • Kamrup Rural Pilot",
                fontSize = 12.sp,
                color = AasritiColorTokens.WarmSlate.copy(alpha = 0.7f),
                modifier = Modifier
                    .clickable { showDevMenu = true }
                    .padding(4.dp)
            )
        }
    }

    // Discreet Demo Controls Modal (Keeps video recording clean)
    if (showDevMenu) {
        Dialog(onDismissRequest = { showDevMenu = false }) {
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
                        text = "প্ৰদৰ্শনী আৰু ভাষা নিয়ন্ত্ৰণ (Demo Settings)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.DeepCharcoal
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Language Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ভাষা (Language):", fontSize = 15.sp, color = AasritiColorTokens.DeepCharcoal)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { DemoStateHolder.currentLanguage = "as" },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (DemoStateHolder.currentLanguage == "as") AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.WarmSunkenSurface
                                )
                            ) {
                                Text("অসমীয়া", color = if (DemoStateHolder.currentLanguage == "as") AasritiColorTokens.WarmIvory else AasritiColorTokens.DeepCharcoal)
                            }
                            Button(
                                onClick = { DemoStateHolder.currentLanguage = "en" },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (DemoStateHolder.currentLanguage == "en") AasritiColorTokens.DeepNortheastForest else AasritiColorTokens.WarmSunkenSurface
                                )
                            ) {
                                Text("English", color = if (DemoStateHolder.currentLanguage == "en") AasritiColorTokens.WarmIvory else AasritiColorTokens.DeepCharcoal)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reset Demo State Button
                    Button(
                        onClick = {
                            DemoStateHolder.resetAll()
                            showDevMenu = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepCranberryEmergency),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("সকলো প্ৰদৰ্শন ৰিছেট কৰক (Reset All Demo State)", color = AasritiColorTokens.WarmIvory, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showDevMenu = false },
                        colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.WarmSunkenSurface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("বন্ধ কৰক (Close)", color = AasritiColorTokens.DeepCharcoal)
                    }
                }
            }
        }
    }
}

/**
 * SCREEN_AUTH_LOCAL_PIN:
 * Offline local PIN entry screen for Caregiver and Doctor roles.
 */
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
    var failedAttempts by remember { mutableIntStateOf(0) }
    var lockoutUntilTimestamp by remember { mutableLongStateOf(0L) }
    val scope = rememberCoroutineScope()
    val isAssamese = DemoStateHolder.currentLanguage == "as"
    val isLockedOut = remember(lockoutUntilTimestamp, enteredPin) {
        System.currentTimeMillis() < lockoutUntilTimestamp
    }

    LaunchedEffect(role) {
        val exists = userRepository.hasUser(role)
        isNewSetup = !exists
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AasritiColorTokens.WarmIvory)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder)
            ) {
                Text(
                    text = if (isAssamese) "← উভতি যাওক" else "← Back",
                    color = AasritiColorTokens.DeepCharcoal,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (role == "CAREGIVER") {
                    if (isAssamese) "🤝 যত্ন লওঁতাৰ পিন (Caregiver PIN)" else "🤝 Caregiver Local PIN"
                } else {
                    if (isAssamese) "🩺 চিকিৎসকৰ পিন (Doctor PIN)" else "🩺 Doctor Local PIN"
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AasritiColorTokens.DeepCharcoal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isNewSetup) {
                    if (isAssamese) "প্ৰথমবাৰৰ বাবে ৬-অংকৰ পিন এটা নিৰ্ধাৰণ কৰক (বা ১২৩৪৫৬ লিখক)।" else "Set a 6-digit local PIN (or enter 123456)."
                } else {
                    if (isAssamese) "প্ৰৱেশ কৰিবলৈ আপোনাৰ ৬-অংকৰ পিন দিয়ক।" else "Enter your 6-digit PIN to access care tools."
                },
                fontSize = 15.sp,
                color = AasritiColorTokens.WarmSlate,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

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
                    focusedBorderColor = AasritiColorTokens.DeepNortheastForest,
                    unfocusedBorderColor = AasritiColorTokens.WarmStoneBorder,
                    focusedTextColor = AasritiColorTokens.DeepCharcoal,
                    unfocusedTextColor = AasritiColorTokens.DeepCharcoal,
                    cursorColor = AasritiColorTokens.DeepNortheastForest
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 26.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 8.sp
                ),
                singleLine = true,
                placeholder = {
                    Text("••••••", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = AasritiColorTokens.WarmSlate)
                }
            )

            errorMessage?.let {
                Spacer(modifier = Modifier.height(10.dp))
                Text("💡 $it", color = AasritiColorTokens.WarmAmberWarning, fontSize = 14.sp)
            }
        }

        // Submit Button
        Button(
            onClick = {
                if (isLockedOut) {
                    errorMessage = if (isAssamese) {
                        "অত্যধিক ভুল প্ৰচেষ্টা। সুৰক্ষাৰ বাবে ৫ মিনিট লক কৰা হৈছে।"
                    } else {
                        "Too many failed attempts. Locked out for 5 minutes per security policy."
                    }
                    return@Button
                }

                // SIH demo shortcut: "123456" always succeeds
                if (enteredPin == "123456" || enteredPin.length == 6) {
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
                            failedAttempts = 0
                            onSuccess()
                        } else {
                            val user = userRepository.authenticatePin(hash)
                            if (user != null || enteredPin == "123456") {
                                failedAttempts = 0
                                onSuccess()
                            } else {
                                failedAttempts++
                                if (failedAttempts >= 5) {
                                    lockoutUntilTimestamp = System.currentTimeMillis() + 5 * 60 * 1000L
                                    errorMessage = if (isAssamese) {
                                        "অত্যধিক ভুল প্ৰচেষ্টা (৫/৫)। সুৰক্ষাৰ বাবে ৫ মিনিট লক কৰা হৈছে।"
                                    } else {
                                        "Too many failed attempts (5/5). Locked out for 5 minutes."
                                    }
                                } else {
                                    val remaining = 5 - failedAttempts
                                    errorMessage = if (isAssamese) {
                                        "পিনটো ভুল হৈছে। অৱশিষ্ট প্ৰচেষ্টা: $remaining"
                                    } else {
                                        "Invalid PIN. Remaining attempts: $remaining"
                                    }
                                }
                            }
                        }
                    }
                } else {
                    errorMessage = if (isAssamese) "অনুগ্ৰহ কৰি ৬-অংকৰ পিন লিখক।" else "Please enter 6 digits."
                }
            },
            enabled = !isLockedOut,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isLockedOut) AasritiColorTokens.WarmSunkenSurface else AasritiColorTokens.DeepNortheastForest,
                disabledContainerColor = AasritiColorTokens.WarmSunkenSurface
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Text(
                text = if (isLockedOut) {
                    if (isAssamese) "লক কৰা হৈছে (Locked)" else "Locked (5 min)"
                } else if (isNewSetup) {
                    "পিন সংৰক্ষণ কৰক (Save PIN) ➔"
                } else {
                    "প্ৰৱেশ কৰক (Enter) ➔"
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLockedOut) AasritiColorTokens.WarmSlate else AasritiColorTokens.WarmIvory
            )
        }
    }
}
