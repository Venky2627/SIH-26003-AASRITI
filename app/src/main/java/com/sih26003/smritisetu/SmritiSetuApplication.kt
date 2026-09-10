package com.sih26003.smritisetu

import android.app.Application
import com.sih26003.smritisetu.data.local.database.AppDatabase
import com.sih26003.smritisetu.data.repository.CareLogRepository
import com.sih26003.smritisetu.data.repository.CarePlanRepository
import com.sih26003.smritisetu.data.repository.DoctorAccessRepository
import com.sih26003.smritisetu.data.repository.GameRepository
import com.sih26003.smritisetu.data.repository.MemoryRepository
import com.sih26003.smritisetu.data.repository.PatientRepository
import com.sih26003.smritisetu.data.repository.ReminderRepository
import com.sih26003.smritisetu.data.repository.UserRepository
import com.sih26003.smritisetu.data.sync.AndroidNetworkConnectivityMonitor
import com.sih26003.smritisetu.data.sync.FirestoreCloudSyncDispatcher
import com.sih26003.smritisetu.data.sync.SyncManager
import com.sih26003.smritisetu.engine.orchestrator.CognitiveInsightOrchestrator
import com.sih26003.smritisetu.ml.inference.DecisionTreeEngine
import com.sih26003.smritisetu.voice.playback.VoicePromptManager

class SmritiSetuApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var userRepository: UserRepository
        private set
    lateinit var patientRepository: PatientRepository
        private set
    lateinit var gameRepository: GameRepository
        private set
    lateinit var reminderRepository: ReminderRepository
        private set
    lateinit var doctorAccessRepository: DoctorAccessRepository
        private set
    lateinit var careLogRepository: CareLogRepository
        private set
    lateinit var carePlanRepository: CarePlanRepository
        private set
    lateinit var memoryRepository: MemoryRepository
        private set
    lateinit var syncManager: SyncManager
        private set

    lateinit var decisionTreeEngine: DecisionTreeEngine
        private set
    lateinit var voicePromptManager: VoicePromptManager
        private set
    lateinit var insightOrchestrator: CognitiveInsightOrchestrator
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)

        userRepository = UserRepository(database.userDao())
        patientRepository = PatientRepository(database.patientDao(), database.relationshipDao(), database.syncQueueDao())
        gameRepository = GameRepository(database.gameSessionDao(), database.syncQueueDao())
        reminderRepository = ReminderRepository(database.reminderDao(), database.syncQueueDao())
        doctorAccessRepository = DoctorAccessRepository(database.doctorAccessDao())
        careLogRepository = CareLogRepository(database.careLogDao(), database.syncQueueDao())
        carePlanRepository = CarePlanRepository(database.carePlanDao(), database.syncQueueDao())
        memoryRepository = MemoryRepository(database.memoryItemDao(), database.syncQueueDao())
        val connectivityMonitor = AndroidNetworkConnectivityMonitor(this)
        val cloudDispatcher = FirestoreCloudSyncDispatcher()
        syncManager = SyncManager(
            syncQueueDao = database.syncQueueDao(),
            connectivityMonitor = connectivityMonitor,
            cloudDispatcher = cloudDispatcher,
            patientDao = database.patientDao(),
            relationshipDao = database.relationshipDao(),
            gameSessionDao = database.gameSessionDao(),
            careLogDao = database.careLogDao(),
            reminderDao = database.reminderDao(),
            carePlanDao = database.carePlanDao(),
            memoryItemDao = database.memoryItemDao()
        )

        decisionTreeEngine = DecisionTreeEngine(this)
        voicePromptManager = VoicePromptManager(this)
        insightOrchestrator = CognitiveInsightOrchestrator(decisionTreeEngine)
    }
}
