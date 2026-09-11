package com.sih26003.aasriti

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sih26003.aasriti.core.ui.theme.AasritiTheme
import com.sih26003.aasriti.data.local.entities.DoctorAccessEntity
import com.sih26003.aasriti.data.local.entities.PatientEntity
import com.sih26003.aasriti.data.local.entities.RelationshipEntity
import com.sih26003.aasriti.feature.asha.AshaDashboardScreen
import com.sih26003.aasriti.feature.auth.InformedConsentScreen
import com.sih26003.aasriti.feature.auth.PinAuthScreen
import com.sih26003.aasriti.feature.auth.RoleAndModeSelectScreen
import com.sih26003.aasriti.feature.caregiver.CaregiverDashboardScreen
import com.sih26003.aasriti.feature.doctor.DoctorAccessScreen
import com.sih26003.aasriti.feature.patient.SosFollowUpScreen
import com.sih26003.aasriti.navigation.AppRoutes
import com.sih26003.aasriti.feature.games.categorisation.CategorisationEngine
import com.sih26003.aasriti.feature.games.categorisation.CategorisationGameScreen
import com.sih26003.aasriti.feature.games.familytrivia.FamilyTriviaEngine
import com.sih26003.aasriti.feature.games.familytrivia.FamilyTriviaGameScreen
import com.sih26003.aasriti.feature.games.flowermatch.FlowerMatchGameScreen
import com.sih26003.aasriti.feature.games.framework.GameId
import com.sih26003.aasriti.feature.games.patternrecognition.PatternRecognitionEngine
import com.sih26003.aasriti.feature.games.patternrecognition.PatternRecognitionGameScreen
import com.sih26003.aasriti.feature.games.sequencing.SequencingEngine
import com.sih26003.aasriti.feature.games.sequencing.SequencingGameScreen
import com.sih26003.aasriti.feature.games.villagemarket.VillageMarketEngine
import com.sih26003.aasriti.feature.games.villagemarket.VillageMarketGameScreen
import com.sih26003.aasriti.feature.games.voicecuecard.VoiceCueCardEngine
import com.sih26003.aasriti.feature.games.voicecuecard.VoiceCueCardGameScreen
import com.sih26003.aasriti.feature.memoryalbum.MemoryGardenScreen
import com.sih26003.aasriti.feature.patient.CareCircleScreen
import com.sih26003.aasriti.feature.patient.PatientHomeScreen
import com.sih26003.aasriti.feature.patient.SosScreen
import com.sih26003.aasriti.feature.reminders.RemindersScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as AasritiApplication

        setContent {
            AasritiTheme {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()
                var activePatient by remember { mutableStateOf<PatientEntity?>(null) }
                var activePatientRelationships by remember { mutableStateOf<List<RelationshipEntity>>(emptyList()) }

                LaunchedEffect(Unit) {
                    val existing = app.patientRepository.getPatientById(com.sih26003.aasriti.demo.DemoPatientConfig.PATIENT_ID)
                    if (existing == null) {
                        val aitaBorah = com.sih26003.aasriti.demo.DemoPatientConfig.createCanonicalPatient()
                        app.patientRepository.savePatient(aitaBorah)

                        app.patientRepository.addRelationship(
                            RelationshipEntity(
                                patientId = com.sih26003.aasriti.demo.DemoPatientConfig.PATIENT_ID,
                                name = "ৰূপম বৰা (Rupam Borah)",
                                relationshipType = "পুত্ৰ (Son)"
                            )
                        )
                        app.patientRepository.addRelationship(
                            RelationshipEntity(
                                patientId = com.sih26003.aasriti.demo.DemoPatientConfig.PATIENT_ID,
                                name = "মীৰা বৰা (Mira Borah)",
                                relationshipType = "বোৱাৰী / প্ৰধান যত্ন লওঁতা (Daughter-in-law)"
                            )
                        )
                        app.patientRepository.addRelationship(
                            RelationshipEntity(
                                patientId = com.sih26003.aasriti.demo.DemoPatientConfig.PATIENT_ID,
                                name = "প্ৰীতম (Pritam)",
                                relationshipType = "নাতি (Grandson)"
                            )
                        )

                        app.doctorAccessRepository.grantAccess(
                            DoctorAccessEntity(
                                patientId = com.sih26003.aasriti.demo.DemoPatientConfig.PATIENT_ID,
                                doctorAccessCode = "424242",
                                doctorName = "ডাঃ হেমন্ত বৰুৱা (Dr. H. Baruah, Neurologist)"
                            )
                        )
                    }
                }

                NavHost(navController = navController, startDestination = AppRoutes.ROLE_SELECT) {
                    // 1. Role Selection & Direct Patient Photo Mode
                    composable(AppRoutes.ROLE_SELECT) {
                        RoleAndModeSelectScreen(
                            patientRepository = app.patientRepository,
                            onPatientSelected = { patient ->
                                activePatient = patient
                                app.voicePromptManager.setLanguage(patient.primaryLanguage)
                                scope.launch {
                                    activePatientRelationships = app.patientRepository.getRelationshipsList(patient.id)
                                }
                                navController.navigate(AppRoutes.PATIENT_HOME)
                            },
                            onCaregiverLoginSelected = {
                                navController.navigate(AppRoutes.PIN_AUTH_CAREGIVER)
                            },
                            onAshaLoginSelected = {
                                navController.navigate(AppRoutes.ASHA_DASHBOARD)
                            },
                            onDoctorLoginSelected = {
                                navController.navigate(AppRoutes.PIN_AUTH_DOCTOR)
                            },
                            onConsentSelected = {
                                navController.navigate(AppRoutes.INFORMED_CONSENT)
                            }
                        )
                    }

                    // SCREEN 6: Informed Consent & Privacy Assent (DPDPA 2023)
                    composable(AppRoutes.INFORMED_CONSENT) {
                        InformedConsentScreen(
                            onConsentAccepted = { navController.popBackStack() },
                            onConsentDeclined = { navController.popBackStack() }
                        )
                    }

                    // 2. Caregiver Local PIN Authentication
                    composable(AppRoutes.PIN_AUTH_CAREGIVER) {
                        PinAuthScreen(
                            role = "CAREGIVER",
                            userRepository = app.userRepository,
                            onSuccess = { navController.navigate(AppRoutes.CAREGIVER_DASHBOARD) },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 3. Doctor Local PIN Authentication
                    composable(AppRoutes.PIN_AUTH_DOCTOR) {
                        PinAuthScreen(
                            role = "DOCTOR",
                            userRepository = app.userRepository,
                            onSuccess = { navController.navigate(AppRoutes.DOCTOR_ACCESS) },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 4. Patient Home Screen (Focal Activity + Care Companions)
                    composable(AppRoutes.PATIENT_HOME) {
                        val p = activePatient ?: com.sih26003.aasriti.demo.DemoPatientConfig.createCanonicalPatient()
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
                                            navController.navigate(AppRoutes.buildFamilyTriviaRoute(initialDiff))
                                        }
                                    }
                                    GameId.VOICE_CUE_CARD -> navController.navigate(AppRoutes.GAME_VOICE_CUE)
                                    GameId.SEQUENCING -> navController.navigate(AppRoutes.GAME_SEQUENCING)
                                    GameId.CATEGORISATION -> navController.navigate(AppRoutes.GAME_CATEGORISATION)
                                    GameId.VILLAGE_MARKET -> navController.navigate(AppRoutes.GAME_VILLAGE_MARKET)
                                    GameId.PATTERN_RECOGNITION -> navController.navigate(AppRoutes.GAME_PATTERN)
                                }
                            },
                            onOpenFlowerMatch = { navController.navigate(AppRoutes.GAME_FLOWER_MATCH) },
                            onOpenMemoryGarden = { navController.navigate(AppRoutes.MEMORY_GARDEN) },
                            onOpenCareCircle = { navController.navigate(AppRoutes.CARE_CIRCLE) },
                            onOpenSos = { navController.navigate(AppRoutes.SOS_SCREEN) },
                            onBackToProfiles = { navController.navigate(AppRoutes.ROLE_SELECT) }
                        )
                    }

                    // 5. Flagship Game: Flower Match
                    composable(AppRoutes.GAME_FLOWER_MATCH) {
                        val p = activePatient ?: com.sih26003.aasriti.demo.DemoPatientConfig.createCanonicalPatient()
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
                    composable(AppRoutes.MEMORY_GARDEN) {
                        MemoryGardenScreen(
                            voicePromptManager = app.voicePromptManager,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 7. Care Circle (Family Telephony)
                    composable(AppRoutes.CARE_CIRCLE) {
                        CareCircleScreen(
                            voicePromptManager = app.voicePromptManager,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 8. SOS & Help Dispatch (SCREEN 16)
                    composable(AppRoutes.SOS_SCREEN) {
                        SosScreen(
                            voicePromptManager = app.voicePromptManager,
                            onOpenFollowUp = { navController.navigate(AppRoutes.SOS_FOLLOW_UP) },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // SCREEN 25: SOS Incident Follow-up & Resolution Protocol
                    composable(AppRoutes.SOS_FOLLOW_UP) {
                        SosFollowUpScreen(
                            careLogRepository = app.careLogRepository,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 9. ASHA Multi-Patient Community Roster
                    composable(AppRoutes.ASHA_DASHBOARD) {
                        AshaDashboardScreen(
                            onOpenPatientView = { navController.navigate(AppRoutes.PATIENT_HOME) },
                            onBackToRoles = { navController.popBackStack() }
                        )
                    }

                    // 10. Caregiver Dashboard
                    composable(AppRoutes.CAREGIVER_DASHBOARD) {
                        CaregiverDashboardScreen(
                            patientRepository = app.patientRepository,
                            gameRepository = app.gameRepository,
                            doctorAccessRepository = app.doctorAccessRepository,
                            careLogRepository = app.careLogRepository,
                            onOpenReminders = { patientId -> navController.navigate(AppRoutes.buildRemindersRoute(patientId)) },
                            onLogout = { navController.navigate(AppRoutes.ROLE_SELECT) }
                        )
                    }

                    // 11. Doctor Access Screen
                    composable(AppRoutes.DOCTOR_ACCESS) {
                        DoctorAccessScreen(
                            doctorAccessRepository = app.doctorAccessRepository,
                            patientRepository = app.patientRepository,
                            gameRepository = app.gameRepository,
                            careLogRepository = app.careLogRepository,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 12. Reminders Screen
                    composable(AppRoutes.REMINDERS) { backStackEntry ->
                        val pid = backStackEntry.arguments?.getString("patientId") ?: ""
                        RemindersScreen(
                            patientId = pid,
                            reminderRepository = app.reminderRepository,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // GAME 1: Family Trivia (Cross-session adaptive difficulty route)
                    composable(
                        route = AppRoutes.GAME_FAMILY_TRIVIA_DIFF,
                        arguments = listOf(androidx.navigation.navArgument("difficulty") {
                            type = androidx.navigation.NavType.IntType
                            defaultValue = 1
                        })
                    ) { backStackEntry ->
                        val diff = backStackEntry.arguments?.getInt("difficulty")?.coerceIn(1, 5) ?: 1
                        val p = activePatient ?: com.sih26003.aasriti.demo.DemoPatientConfig.createCanonicalPatient()
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
                        FamilyTriviaGameScreen(
                            engine = engine,
                            patientRepository = app.patientRepository,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // GAME 1: Family Trivia (Fallback route resolving difficulty from Room)
                    composable(AppRoutes.GAME_FAMILY_TRIVIA) {
                        val p = activePatient ?: com.sih26003.aasriti.demo.DemoPatientConfig.createCanonicalPatient()
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
                            FamilyTriviaGameScreen(
                                engine = eng,
                                patientRepository = app.patientRepository,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }

                    // GAME 2: Voice Cue Card
                    composable(AppRoutes.GAME_VOICE_CUE) {
                        val p = activePatient ?: com.sih26003.aasriti.demo.DemoPatientConfig.createCanonicalPatient()
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
                    composable(AppRoutes.GAME_SEQUENCING) {
                        val p = activePatient ?: com.sih26003.aasriti.demo.DemoPatientConfig.createCanonicalPatient()
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
                    composable(AppRoutes.GAME_CATEGORISATION) {
                        val p = activePatient ?: com.sih26003.aasriti.demo.DemoPatientConfig.createCanonicalPatient()
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
                    composable(AppRoutes.GAME_VILLAGE_MARKET) {
                        val p = activePatient ?: com.sih26003.aasriti.demo.DemoPatientConfig.createCanonicalPatient()
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
                    composable(AppRoutes.GAME_PATTERN) {
                        val p = activePatient ?: com.sih26003.aasriti.demo.DemoPatientConfig.createCanonicalPatient()
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
