package com.sih26003.smritisetu

import android.app.Application
import com.sih26003.smritisetu.data.local.database.AppDatabase
import com.sih26003.smritisetu.data.repository.CareLogRepository
import com.sih26003.smritisetu.data.repository.DoctorAccessRepository
import com.sih26003.smritisetu.data.repository.GameRepository
import com.sih26003.smritisetu.data.repository.PatientRepository
import com.sih26003.smritisetu.data.repository.ReminderRepository
import com.sih26003.smritisetu.data.repository.UserRepository
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
        syncManager = SyncManager(database.syncQueueDao())

        decisionTreeEngine = DecisionTreeEngine(this)
        voicePromptManager = VoicePromptManager(this)
        insightOrchestrator = CognitiveInsightOrchestrator(decisionTreeEngine)
    }
}
