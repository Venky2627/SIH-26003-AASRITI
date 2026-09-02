package com.sih26003.smritisetu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sih26003.smritisetu.data.local.entities.PatientEntity
import com.sih26003.smritisetu.data.local.entities.RelationshipEntity
import com.sih26003.smritisetu.feature.auth.PinAuthScreen
import com.sih26003.smritisetu.feature.auth.RoleAndModeSelectScreen
import com.sih26003.smritisetu.feature.caregiver.CaregiverDashboardScreen
import com.sih26003.smritisetu.feature.doctor.DoctorAccessScreen
import com.sih26003.smritisetu.feature.games.categorisation.CategorisationEngine
import com.sih26003.smritisetu.feature.games.categorisation.CategorisationGameScreen
import com.sih26003.smritisetu.feature.games.familytrivia.FamilyTriviaEngine
import com.sih26003.smritisetu.feature.games.familytrivia.FamilyTriviaGameScreen
import com.sih26003.smritisetu.feature.games.framework.GameId
import com.sih26003.smritisetu.feature.games.patternrecognition.PatternRecognitionEngine
import com.sih26003.smritisetu.feature.games.patternrecognition.PatternRecognitionGameScreen
import com.sih26003.smritisetu.feature.games.sequencing.SequencingEngine
import com.sih26003.smritisetu.feature.games.sequencing.SequencingGameScreen
import com.sih26003.smritisetu.feature.games.villagemarket.VillageMarketEngine
import com.sih26003.smritisetu.feature.games.villagemarket.VillageMarketGameScreen
import com.sih26003.smritisetu.feature.games.voicecuecard.VoiceCueCardEngine
import com.sih26003.smritisetu.feature.games.voicecuecard.VoiceCueCardGameScreen
import com.sih26003.smritisetu.feature.patient.PatientHomeScreen
import com.sih26003.smritisetu.feature.reminders.RemindersScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as SmritiSetuApplication

        setContent {
            val navController = rememberNavController()
            val scope = rememberCoroutineScope()
            var activePatient by remember { mutableStateOf<PatientEntity?>(null) }
            var activePatientRelationships by remember { mutableStateOf<List<RelationshipEntity>>(emptyList()) }

            NavHost(navController = navController, startDestination = "role_select") {
                // 1. Role Selection & Direct Patient Photo Mode
                composable("role_select") {
                    RoleAndModeSelectScreen(
                        patientRepository = app.patientRepository,
                        onPatientSelected = { patient ->
                            activePatient = patient
                            app.voicePromptManager.setLanguage(patient.primaryLanguage)
                            scope.launch {
                                activePatientRelationships = app.patientRepository.getRelationshipsList(patient.id)
                            }
                            navController.navigate("patient_home")
                        },
                        onCaregiverLoginSelected = {
                            navController.navigate("pin_auth_caregiver")
                        },
                        onDoctorLoginSelected = {
                            navController.navigate("pin_auth_doctor")
                        }
                    )
                }

                // 2. Caregiver Local PIN Authentication
                composable("pin_auth_caregiver") {
                    PinAuthScreen(
                        role = "CAREGIVER",
                        userRepository = app.userRepository,
                        onSuccess = { navController.navigate("caregiver_dashboard") },
                        onBack = { navController.popBackStack() }
                    )
                }

                // 3. Doctor Local PIN Authentication
                composable("pin_auth_doctor") {
                    PinAuthScreen(
                        role = "DOCTOR",
                        userRepository = app.userRepository,
                        onSuccess = { navController.navigate("doctor_access") },
                        onBack = { navController.popBackStack() }
                    )
                }

                // 4. Patient Home Screen (All 6 Games)
                composable("patient_home") {
                    val p = activePatient ?: PatientEntity(
                        id = "default_id",
                        pseudonymCode = "AS-DEMO-01",
                        birthYear = 1954,
                        gender = "M"
                    )
                    PatientHomeScreen(
                        patient = p,
                        voicePromptManager = app.voicePromptManager,
                        onSelectGame = { gameId ->
                            when (gameId) {
                                GameId.FAMILY_TRIVIA -> {
                                    scope.launch {
                                        activePatientRelationships = app.patientRepository.getRelationshipsList(p.id)
                                        navController.navigate("game_family_trivia")
                                    }
                                }
                                GameId.VOICE_CUE_CARD -> navController.navigate("game_voice_cue_card")
                                GameId.SEQUENCING -> navController.navigate("game_sequencing")
                                GameId.CATEGORISATION -> navController.navigate("game_categorisation")
                                GameId.VILLAGE_MARKET -> navController.navigate("game_village_market")
                                GameId.PATTERN_RECOGNITION -> navController.navigate("game_pattern_recognition")
                            }
                        },
                        onBackToProfiles = { navController.navigate("role_select") }
                    )
                }

                // GAME 1: Family Trivia
                composable("game_family_trivia") {
                    val p = activePatient ?: PatientEntity(id = "default", pseudonymCode = "AS-01", birthYear = 1950, gender = "M")
                    val engine = remember(p.id, activePatientRelationships) {
                        FamilyTriviaEngine(
                            patientId = p.id,
                            relationships = activePatientRelationships,
                            gameRepository = app.gameRepository,
                            decisionTreeEngine = app.decisionTreeEngine,
                            voicePromptManager = app.voicePromptManager,
                            scope = scope
                        )
                    }
                    FamilyTriviaGameScreen(engine = engine, onBack = { navController.popBackStack() })
                }

                // GAME 2: Voice Cue Card
                composable("game_voice_cue_card") {
                    val p = activePatient ?: PatientEntity(id = "default", pseudonymCode = "AS-01", birthYear = 1950, gender = "M")
                    val engine = remember(p.id) {
                        VoiceCueCardEngine(
                            patientId = p.id,
                            gameRepository = app.gameRepository,
                            decisionTreeEngine = app.decisionTreeEngine,
                            voicePromptManager = app.voicePromptManager,
                            scope = scope
                        )
                    }
                    VoiceCueCardGameScreen(engine = engine, onBack = { navController.popBackStack() })
                }

                // GAME 3: Sequencing
                composable("game_sequencing") {
                    val p = activePatient ?: PatientEntity(id = "default", pseudonymCode = "AS-01", birthYear = 1950, gender = "M")
                    val engine = remember(p.id) {
                        SequencingEngine(
                            patientId = p.id,
                            gameRepository = app.gameRepository,
                            decisionTreeEngine = app.decisionTreeEngine,
                            voicePromptManager = app.voicePromptManager,
                            scope = scope
                        )
                    }
                    SequencingGameScreen(engine = engine, onBack = { navController.popBackStack() })
                }

                // GAME 4: Categorisation
                composable("game_categorisation") {
                    val p = activePatient ?: PatientEntity(id = "default", pseudonymCode = "AS-01", birthYear = 1950, gender = "M")
                    val engine = remember(p.id) {
                        CategorisationEngine(
                            patientId = p.id,
                            gameRepository = app.gameRepository,
                            decisionTreeEngine = app.decisionTreeEngine,
                            voicePromptManager = app.voicePromptManager,
                            scope = scope
                        )
                    }
                    CategorisationGameScreen(engine = engine, onBack = { navController.popBackStack() })
                }

                // GAME 5: Village Market
                composable("game_village_market") {
                    val p = activePatient ?: PatientEntity(id = "default", pseudonymCode = "AS-01", birthYear = 1950, gender = "M")
                    val engine = remember(p.id) {
                        VillageMarketEngine(
                            patientId = p.id,
                            gameRepository = app.gameRepository,
                            decisionTreeEngine = app.decisionTreeEngine,
                            voicePromptManager = app.voicePromptManager,
                            scope = scope
                        )
                    }
                    VillageMarketGameScreen(engine = engine, onBack = { navController.popBackStack() })
                }

                // GAME 6: Pattern Recognition
                composable("game_pattern_recognition") {
                    val p = activePatient ?: PatientEntity(id = "default", pseudonymCode = "AS-01", birthYear = 1950, gender = "M")
                    val engine = remember(p.id) {
                        PatternRecognitionEngine(
                            patientId = p.id,
                            gameRepository = app.gameRepository,
                            decisionTreeEngine = app.decisionTreeEngine,
                            voicePromptManager = app.voicePromptManager,
                            scope = scope
                        )
                    }
                    PatternRecognitionGameScreen(engine = engine, onBack = { navController.popBackStack() })
                }

                // Caregiver Dashboard
                composable("caregiver_dashboard") {
                    CaregiverDashboardScreen(
                        patientRepository = app.patientRepository,
                        gameRepository = app.gameRepository,
                        doctorAccessRepository = app.doctorAccessRepository,
                        onOpenReminders = { patientId -> navController.navigate("reminders/$patientId") },
                        onLogout = { navController.navigate("role_select") }
                    )
                }

                // Doctor Access Screen
                composable("doctor_access") {
                    DoctorAccessScreen(
                        doctorAccessRepository = app.doctorAccessRepository,
                        patientRepository = app.patientRepository,
                        gameRepository = app.gameRepository,
                        onBack = { navController.popBackStack() }
                    )
                }

                // Reminders Screen
                composable("reminders/{patientId}") { backStackEntry ->
                    val pid = backStackEntry.arguments?.getString("patientId") ?: ""
                    RemindersScreen(
                        patientId = pid,
                        reminderRepository = app.reminderRepository,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
