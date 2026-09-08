package com.sih26003.smritisetu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sih26003.smritisetu.core.ui.theme.AasritiTheme
import com.sih26003.smritisetu.data.local.entities.DoctorAccessEntity
import com.sih26003.smritisetu.data.local.entities.PatientEntity
import com.sih26003.smritisetu.data.local.entities.RelationshipEntity
import com.sih26003.smritisetu.feature.asha.AshaDashboardScreen
import com.sih26003.smritisetu.feature.auth.PinAuthScreen
import com.sih26003.smritisetu.feature.auth.RoleAndModeSelectScreen
import com.sih26003.smritisetu.feature.caregiver.CaregiverDashboardScreen
import com.sih26003.smritisetu.feature.doctor.DoctorAccessScreen
import com.sih26003.smritisetu.feature.games.categorisation.CategorisationEngine
import com.sih26003.smritisetu.feature.games.categorisation.CategorisationGameScreen
import com.sih26003.smritisetu.feature.games.familytrivia.FamilyTriviaEngine
import com.sih26003.smritisetu.feature.games.familytrivia.FamilyTriviaGameScreen
import com.sih26003.smritisetu.feature.games.flowermatch.FlowerMatchGameScreen
import com.sih26003.smritisetu.feature.games.framework.GameId
import com.sih26003.smritisetu.feature.games.patternrecognition.PatternRecognitionEngine
import com.sih26003.smritisetu.feature.games.patternrecognition.PatternRecognitionGameScreen
import com.sih26003.smritisetu.feature.games.sequencing.SequencingEngine
import com.sih26003.smritisetu.feature.games.sequencing.SequencingGameScreen
import com.sih26003.smritisetu.feature.games.villagemarket.VillageMarketEngine
import com.sih26003.smritisetu.feature.games.villagemarket.VillageMarketGameScreen
import com.sih26003.smritisetu.feature.games.voicecuecard.VoiceCueCardEngine
import com.sih26003.smritisetu.feature.games.voicecuecard.VoiceCueCardGameScreen
import com.sih26003.smritisetu.feature.memoryalbum.MemoryGardenScreen
import com.sih26003.smritisetu.feature.patient.CareCircleScreen
import com.sih26003.smritisetu.feature.patient.PatientHomeScreen
import com.sih26003.smritisetu.feature.patient.SosScreen
import com.sih26003.smritisetu.feature.reminders.RemindersScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as SmritiSetuApplication

        setContent {
            AasritiTheme {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()
                var activePatient by remember { mutableStateOf<PatientEntity?>(null) }
                var activePatientRelationships by remember { mutableStateOf<List<RelationshipEntity>>(emptyList()) }

                LaunchedEffect(Unit) {
                    val existing = app.patientRepository.getPatientById(com.sih26003.smritisetu.demo.DemoPatientConfig.PATIENT_ID)
                    if (existing == null) {
                        val aitaBorah = com.sih26003.smritisetu.demo.DemoPatientConfig.createCanonicalPatient()
                        app.patientRepository.savePatient(aitaBorah)

                        app.patientRepository.addRelationship(
                            RelationshipEntity(
                                patientId = com.sih26003.smritisetu.demo.DemoPatientConfig.PATIENT_ID,
                                name = "ৰূপম বৰা (Rupam Borah)",
                                relationshipType = "পুত্ৰ (Son)"
                            )
                        )
                        app.patientRepository.addRelationship(
                            RelationshipEntity(
                                patientId = com.sih26003.smritisetu.demo.DemoPatientConfig.PATIENT_ID,
                                name = "মীৰা বৰা (Mira Borah)",
                                relationshipType = "বোৱাৰী / প্ৰধান যত্ন লওঁতা (Daughter-in-law)"
                            )
                        )
                        app.patientRepository.addRelationship(
                            RelationshipEntity(
                                patientId = com.sih26003.smritisetu.demo.DemoPatientConfig.PATIENT_ID,
                                name = "প্ৰীতম (Pritam)",
                                relationshipType = "নাতি (Grandson)"
                            )
                        )

                        app.doctorAccessRepository.grantAccess(
                            DoctorAccessEntity(
                                patientId = com.sih26003.smritisetu.demo.DemoPatientConfig.PATIENT_ID,
                                doctorAccessCode = "424242",
                                doctorName = "ডাঃ হেমন্ত বৰুৱা (Dr. H. Baruah, Neurologist)"
                            )
                        )
                    }
                }

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
                            onAshaLoginSelected = {
                                navController.navigate("asha_dashboard")
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

                    // 4. Patient Home Screen (Focal Activity + Care Companions)
                    composable("patient_home") {
                        val p = activePatient ?: com.sih26003.smritisetu.demo.DemoPatientConfig.createCanonicalPatient()
                        PatientHomeScreen(
                            patient = p,
                            voicePromptManager = app.voicePromptManager,
                            onSelectGame = { gameId ->
                                when (gameId) {
                                    GameId.FAMILY_TRIVIA -> {
                                        scope.launch {
                                            activePatientRelationships = app.patientRepository.getRelationshipsList(p.id)
                                            val latestSession = app.gameRepository.getLatestSession(p.id, GameId.FAMILY_TRIVIA.name)
                                            val initialDiff = latestSession?.adaptationDecision?.coerceIn(1, 5) ?: 1
                                            navController.navigate("game_family_trivia/$initialDiff")
                                        }
                                    }
                                    GameId.VOICE_CUE_CARD -> navController.navigate("game_voice_cue_card")
                                    GameId.SEQUENCING -> navController.navigate("game_sequencing")
                                    GameId.CATEGORISATION -> navController.navigate("game_categorisation")
                                    GameId.VILLAGE_MARKET -> navController.navigate("game_village_market")
                                    GameId.PATTERN_RECOGNITION -> navController.navigate("game_pattern_recognition")
                                }
                            },
                            onOpenFlowerMatch = { navController.navigate("game_flower_match") },
                            onOpenMemoryGarden = { navController.navigate("memory_garden") },
                            onOpenCareCircle = { navController.navigate("care_circle") },
                            onOpenSos = { navController.navigate("sos_screen") },
                            onBackToProfiles = { navController.navigate("role_select") }
                        )
                    }

                    // 5. Flagship Game: Flower Match
                    composable("game_flower_match") {
                        val p = activePatient ?: com.sih26003.smritisetu.demo.DemoPatientConfig.createCanonicalPatient()
                        FlowerMatchGameScreen(
                            patientId = p.id,
                            gameRepository = app.gameRepository,
                            decisionTreeEngine = app.decisionTreeEngine,
                            insightOrchestrator = app.insightOrchestrator,
                            voicePromptManager = app.voicePromptManager,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 6. Memory Garden (Reminiscence Album)
                    composable("memory_garden") {
                        MemoryGardenScreen(
                            voicePromptManager = app.voicePromptManager,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 7. Care Circle (Family Telephony)
                    composable("care_circle") {
                        CareCircleScreen(
                            voicePromptManager = app.voicePromptManager,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 8. SOS & Help Dispatch
                    composable("sos_screen") {
                        SosScreen(
                            voicePromptManager = app.voicePromptManager,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 9. ASHA Multi-Patient Community Roster
                    composable("asha_dashboard") {
                        AshaDashboardScreen(
                            onOpenPatientView = { navController.navigate("patient_home") },
                            onBackToRoles = { navController.popBackStack() }
                        )
                    }

                    // 10. Caregiver Dashboard
                    composable("caregiver_dashboard") {
                        CaregiverDashboardScreen(
                            patientRepository = app.patientRepository,
                            gameRepository = app.gameRepository,
                            doctorAccessRepository = app.doctorAccessRepository,
                            careLogRepository = app.careLogRepository,
                            onOpenReminders = { patientId -> navController.navigate("reminders/$patientId") },
                            onLogout = { navController.navigate("role_select") }
                        )
                    }

                    // 11. Doctor Access Screen
                    composable("doctor_access") {
                        DoctorAccessScreen(
                            doctorAccessRepository = app.doctorAccessRepository,
                            patientRepository = app.patientRepository,
                            gameRepository = app.gameRepository,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 12. Reminders Screen
                    composable("reminders/{patientId}") { backStackEntry ->
                        val pid = backStackEntry.arguments?.getString("patientId") ?: ""
                        RemindersScreen(
                            patientId = pid,
                            reminderRepository = app.reminderRepository,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // GAME 1: Family Trivia (Cross-session adaptive difficulty route)
                    composable(
                        route = "game_family_trivia/{difficulty}",
                        arguments = listOf(androidx.navigation.navArgument("difficulty") {
                            type = androidx.navigation.NavType.IntType
                            defaultValue = 1
                        })
                    ) { backStackEntry ->
                        val diff = backStackEntry.arguments?.getInt("difficulty")?.coerceIn(1, 5) ?: 1
                        val p = activePatient ?: com.sih26003.smritisetu.demo.DemoPatientConfig.createCanonicalPatient()
                        val engine = remember(p.id, activePatientRelationships, diff) {
                            FamilyTriviaEngine(
                                patientId = p.id,
                                relationships = activePatientRelationships,
                                gameRepository = app.gameRepository,
                                decisionTreeEngine = app.decisionTreeEngine,
                                voicePromptManager = app.voicePromptManager,
                                scope = scope,
                                initialDifficulty = diff
                            )
                        }
                        FamilyTriviaGameScreen(engine = engine, onBack = { navController.popBackStack() })
                    }

                    // GAME 1: Family Trivia (Fallback route resolving difficulty from Room)
                    composable("game_family_trivia") {
                        val p = activePatient ?: com.sih26003.smritisetu.demo.DemoPatientConfig.createCanonicalPatient()
                        var engine by remember(p.id) { mutableStateOf<FamilyTriviaEngine?>(null) }
                        LaunchedEffect(p.id) {
                            val latestSession = app.gameRepository.getLatestSession(p.id, GameId.FAMILY_TRIVIA.name)
                            val initialDiff = latestSession?.adaptationDecision?.coerceIn(1, 5) ?: 1
                            engine = FamilyTriviaEngine(
                                patientId = p.id,
                                relationships = activePatientRelationships,
                                gameRepository = app.gameRepository,
                                decisionTreeEngine = app.decisionTreeEngine,
                                voicePromptManager = app.voicePromptManager,
                                scope = scope,
                                initialDifficulty = initialDiff
                            )
                        }
                        engine?.let { eng ->
                            FamilyTriviaGameScreen(engine = eng, onBack = { navController.popBackStack() })
                        }
                    }

                    // GAME 2: Voice Cue Card
                    composable("game_voice_cue_card") {
                        val p = activePatient ?: com.sih26003.smritisetu.demo.DemoPatientConfig.createCanonicalPatient()
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
                        val p = activePatient ?: com.sih26003.smritisetu.demo.DemoPatientConfig.createCanonicalPatient()
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
                        val p = activePatient ?: com.sih26003.smritisetu.demo.DemoPatientConfig.createCanonicalPatient()
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
                        val p = activePatient ?: com.sih26003.smritisetu.demo.DemoPatientConfig.createCanonicalPatient()
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
                        val p = activePatient ?: com.sih26003.smritisetu.demo.DemoPatientConfig.createCanonicalPatient()
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
                }
            }
        }
    }
}
