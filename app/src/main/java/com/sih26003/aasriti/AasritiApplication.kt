package com.sih26003.aasriti

import android.app.Application
import com.sih26003.aasriti.data.local.database.AppDatabase
import com.sih26003.aasriti.data.repository.CareLogRepository
import com.sih26003.aasriti.data.repository.DoctorAccessRepository
import com.sih26003.aasriti.data.repository.GameRepository
import com.sih26003.aasriti.data.repository.PatientRepository
import com.sih26003.aasriti.data.repository.ReminderRepository
import com.sih26003.aasriti.data.repository.UserRepository
import com.sih26003.aasriti.data.sync.SyncManager
import com.sih26003.aasriti.engine.orchestrator.CognitiveInsightOrchestrator
import com.sih26003.aasriti.ml.inference.DecisionTreeEngine
import com.sih26003.aasriti.voice.playback.VoicePromptManager

class AasritiApplication : Application() {

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
