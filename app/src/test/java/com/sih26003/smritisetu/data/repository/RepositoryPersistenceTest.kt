package com.sih26003.smritisetu.data.repository

import com.sih26003.smritisetu.data.local.dao.CareLogDao
import com.sih26003.smritisetu.data.local.dao.GameSessionDao
import com.sih26003.smritisetu.data.local.dao.PatientDao
import com.sih26003.smritisetu.data.local.dao.RelationshipDao
import com.sih26003.smritisetu.data.local.dao.ReminderDao
import com.sih26003.smritisetu.data.local.dao.SyncQueueDao
import com.sih26003.smritisetu.data.local.entities.CareLogEntity
import com.sih26003.smritisetu.data.local.entities.GameSessionEntity
import com.sih26003.smritisetu.data.local.entities.PatientEntity
import com.sih26003.smritisetu.data.local.entities.RelationshipEntity
import com.sih26003.smritisetu.data.local.entities.ReminderEntity
import com.sih26003.smritisetu.data.local.entities.SyncQueueEntity
import com.sih26003.smritisetu.demo.DemoPatientConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RepositoryPersistenceTest {

    private class FakeSyncQueueDao : SyncQueueDao {
        val queue = mutableListOf<SyncQueueEntity>()

        override suspend fun getPendingSyncBatches(): List<SyncQueueEntity> =
            queue.filter { it.status == "PENDING" }

        override suspend fun enqueue(syncItem: SyncQueueEntity) {
            queue.add(syncItem)
        }

        override suspend fun markSynced(id: Long) {
            val idx = queue.indexOfFirst { it.id == id }
            if (idx != -1) queue[idx] = queue[idx].copy(status = "SYNCED")
        }

        override suspend fun incrementRetry(id: Long) {
            val idx = queue.indexOfFirst { it.id == id }
            if (idx != -1) queue[idx] = queue[idx].copy(retryCount = queue[idx].retryCount + 1)
        }

        override suspend fun clearCompleted() {
            queue.removeAll { it.status == "SYNCED" }
        }
    }

    private class FakeGameSessionDao : GameSessionDao {
        val sessions = mutableListOf<GameSessionEntity>()

        override fun getSessionsForPatient(patientId: String): Flow<List<GameSessionEntity>> =
            flowOf(sessions.filter { it.patientId == patientId }.sortedByDescending { it.timestamp })

        override suspend fun getLatestSession(patientId: String, gameId: String): GameSessionEntity? =
            sessions.filter { it.patientId == patientId && it.gameId == gameId }
                .maxByOrNull { it.timestamp }

        override suspend fun getAllSessionsList(patientId: String): List<GameSessionEntity> =
            sessions.filter { it.patientId == patientId }.sortedByDescending { it.timestamp }

        override suspend fun insertSession(session: GameSessionEntity) {
            sessions.removeAll { it.id == session.id }
            sessions.add(session)
        }

        override suspend fun getAverageAccuracy(patientId: String): Float? {
            val pSessions = sessions.filter { it.patientId == patientId }
            if (pSessions.isEmpty()) return null
            return pSessions.map { it.accuracy }.average().toFloat()
        }
    }

    private class FakeReminderDao : ReminderDao {
        val reminders = mutableListOf<ReminderEntity>()

        override fun getActiveReminders(patientId: String): Flow<List<ReminderEntity>> =
            flowOf(reminders.filter { it.patientId == patientId && it.isEnabled })

        override suspend fun getAllActiveRemindersList(): List<ReminderEntity> =
            reminders.filter { it.isEnabled }

        override suspend fun insertReminder(reminder: ReminderEntity) {
            reminders.removeAll { it.id == reminder.id }
            reminders.add(reminder)
        }

        override suspend fun updateReminder(reminder: ReminderEntity) {
            val idx = reminders.indexOfFirst { it.id == reminder.id }
            if (idx != -1) reminders[idx] = reminder
        }

        override suspend fun deleteReminder(id: String) {
            reminders.removeAll { it.id == id }
        }
    }

    private class FakePatientDao : PatientDao {
        val patients = mutableListOf<PatientEntity>()

        override fun getAllPatients(): Flow<List<PatientEntity>> = flowOf(patients)

        override suspend fun getPatientById(id: String): PatientEntity? =
            patients.firstOrNull { it.id == id }

        override suspend fun getPatientByLinkCode(code: String): PatientEntity? =
            patients.firstOrNull { it.linkCode == code }

        override suspend fun insertPatient(patient: PatientEntity) {
            patients.removeAll { it.id == patient.id }
            patients.add(patient)
        }

        override suspend fun updatePatient(patient: PatientEntity) {
            val idx = patients.indexOfFirst { it.id == patient.id }
            if (idx != -1) patients[idx] = patient
        }

        override suspend fun deletePatient(id: String) {
            patients.removeAll { it.id == id }
        }
    }

    private class FakeRelationshipDao : RelationshipDao {
        val relationships = mutableListOf<RelationshipEntity>()

        override fun getRelationshipsForPatient(patientId: String): Flow<List<RelationshipEntity>> =
            flowOf(relationships.filter { it.patientId == patientId })

        override suspend fun getRelationshipsList(patientId: String): List<RelationshipEntity> =
            relationships.filter { it.patientId == patientId }

        override suspend fun insertRelationship(relationship: RelationshipEntity) {
            relationships.removeAll { it.id == relationship.id }
            relationships.add(relationship)
        }

        override suspend fun deleteRelationship(id: String) {
            relationships.removeAll { it.id == id }
        }
    }

    private class FakeCareLogDao : CareLogDao {
        val logs = mutableListOf<CareLogEntity>()

        override fun getLogsForPatient(patientId: String): Flow<List<CareLogEntity>> =
            flowOf(logs.filter { it.patientId == patientId }.sortedByDescending { it.timestamp })

        override suspend fun getRecentLogsList(patientId: String, limit: Int): List<CareLogEntity> =
            logs.filter { it.patientId == patientId }.sortedByDescending { it.timestamp }.take(limit)

        override suspend fun insertLog(log: CareLogEntity) {
            logs.removeAll { it.id == log.id }
            logs.add(log)
        }

        override suspend fun deleteLog(id: String) {
            logs.removeAll { it.id == id }
        }
    }

    private lateinit var syncQueueDao: FakeSyncQueueDao
    private lateinit var gameSessionDao: FakeGameSessionDao
    private lateinit var reminderDao: FakeReminderDao
    private lateinit var patientDao: FakePatientDao
    private lateinit var relationshipDao: FakeRelationshipDao
    private lateinit var careLogDao: FakeCareLogDao

    private lateinit var gameRepository: GameRepository
    private lateinit var reminderRepository: ReminderRepository
    private lateinit var patientRepository: PatientRepository
    private lateinit var careLogRepository: CareLogRepository

    @Before
    fun setUp() {
        syncQueueDao = FakeSyncQueueDao()
        gameSessionDao = FakeGameSessionDao()
        reminderDao = FakeReminderDao()
        patientDao = FakePatientDao()
        relationshipDao = FakeRelationshipDao()
        careLogDao = FakeCareLogDao()

        gameRepository = GameRepository(gameSessionDao, syncQueueDao)
        reminderRepository = ReminderRepository(reminderDao, syncQueueDao)
        patientRepository = PatientRepository(patientDao, relationshipDao, syncQueueDao)
        careLogRepository = CareLogRepository(careLogDao, syncQueueDao)
    }

    @Test
    fun testGameRepositorySaveSessionAndSyncQueueEnqueue() = runBlocking {
        val session = GameSessionEntity(
            patientId = DemoPatientConfig.PATIENT_ID,
            gameId = "FAMILY_TRIVIA",
            difficultyLevel = 2,
            durationMs = 15000L,
            accuracy = 0.9f,
            errors = 0,
            reactionTimeMs = 2100L,
            hesitationCount = 1,
            adaptationDecision = 3,
            completed = true
        )

        gameRepository.saveGameSession(session)

        // 1. Room persistence check
        val storedList = gameRepository.getAllSessionsList(DemoPatientConfig.PATIENT_ID)
        assertEquals(1, storedList.size)
        assertEquals(session.id, storedList[0].id)
        assertEquals(3, storedList[0].adaptationDecision)

        // 2. SyncQueue enqueue check
        val pendingQueue = syncQueueDao.getPendingSyncBatches()
        assertEquals(1, pendingQueue.size)
        val queueItem = pendingQueue[0]
        assertEquals("game_sessions", queueItem.tableName)
        assertEquals(session.id, queueItem.recordId)
        assertEquals("INSERT", queueItem.operation)
        assertTrue(queueItem.payloadJson.contains("FAMILY_TRIVIA"))
    }

    @Test
    fun testGameRepositoryGetLatestSessionCrossSessionAdaptation() = runBlocking {
        val oldSession = GameSessionEntity(
            patientId = DemoPatientConfig.PATIENT_ID,
            gameId = "VOICE_CUE_CARD",
            difficultyLevel = 1,
            durationMs = 12000L,
            accuracy = 0.8f,
            errors = 1,
            reactionTimeMs = 3000L,
            hesitationCount = 2,
            adaptationDecision = 2,
            completed = true,
            timestamp = 1000L
        )
        val newSession = GameSessionEntity(
            patientId = DemoPatientConfig.PATIENT_ID,
            gameId = "VOICE_CUE_CARD",
            difficultyLevel = 2,
            durationMs = 14000L,
            accuracy = 1.0f,
            errors = 0,
            reactionTimeMs = 1800L,
            hesitationCount = 0,
            adaptationDecision = 4,
            completed = true,
            timestamp = 2000L
        )

        gameRepository.saveGameSession(oldSession)
        gameRepository.saveGameSession(newSession)

        val latest = gameRepository.getLatestSession(DemoPatientConfig.PATIENT_ID, "VOICE_CUE_CARD")
        assertNotNull(latest)
        assertEquals(newSession.id, latest!!.id)
        assertEquals(4, latest.adaptationDecision)
    }

    @Test
    fun testReminderRepositorySaveToggleDeleteQueueOperations() = runBlocking {
        val reminder = ReminderEntity(
            id = "rem_test_01",
            patientId = DemoPatientConfig.PATIENT_ID,
            title = "ৰক্তচাপৰ ঔষধ",
            reminderType = "MEDICINE",
            hour = 8,
            minute = 0,
            isEnabled = true
        )

        // 1. Save (INSERT)
        reminderRepository.saveReminder(reminder)
        var activeReminders = reminderRepository.getAllActiveReminders()
        assertEquals(1, activeReminders.size)
        assertEquals("rem_test_01", activeReminders[0].id)

        var queue = syncQueueDao.getPendingSyncBatches()
        assertEquals(1, queue.size)
        assertEquals("INSERT", queue[0].operation)
        assertEquals("reminders", queue[0].tableName)

        // 2. Toggle (UPDATE)
        reminderRepository.toggleReminder(reminder, isEnabled = false)
        activeReminders = reminderRepository.getAllActiveReminders()
        assertEquals(0, activeReminders.size)

        queue = syncQueueDao.getPendingSyncBatches()
        assertEquals(2, queue.size)
        assertEquals("UPDATE", queue[1].operation)

        // 3. Delete (DELETE)
        reminderRepository.deleteReminder("rem_test_01")
        queue = syncQueueDao.getPendingSyncBatches()
        assertEquals(3, queue.size)
        assertEquals("DELETE", queue[2].operation)
        assertTrue(queue[2].payloadJson.contains("rem_test_01"))
    }

    @Test
    fun testPatientRepositorySaveAndRelationshipEnqueue() = runBlocking {
        val patient = PatientEntity(
            id = DemoPatientConfig.PATIENT_ID,
            pseudonymCode = "AS-KAM-0042",
            birthYear = 1958,
            gender = "F"
        )
        patientRepository.savePatient(patient)

        val retrieved = patientRepository.getPatientById(DemoPatientConfig.PATIENT_ID)
        assertNotNull(retrieved)
        assertEquals("AS-KAM-0042", retrieved!!.pseudonymCode)

        val rel = RelationshipEntity(
            patientId = DemoPatientConfig.PATIENT_ID,
            name = "ৰূপম বৰা",
            relationshipType = "পুত্ৰ"
        )
        patientRepository.addRelationship(rel)

        val relList = patientRepository.getRelationshipsList(DemoPatientConfig.PATIENT_ID)
        assertEquals(1, relList.size)
        assertEquals("ৰূপম বৰা", relList[0].name)

        val queue = syncQueueDao.getPendingSyncBatches()
        assertEquals(2, queue.size)
        assertEquals("patients", queue[0].tableName)
        assertEquals("relationships", queue[1].tableName)
    }

    @Test
    fun testCareLogRepositorySaveLogAndEnqueue() = runBlocking {
        val log = CareLogEntity(
            id = "log_001",
            patientId = DemoPatientConfig.PATIENT_ID,
            authorRole = "CAREGIVER",
            category = "HYDRATION",
            severity = "NORMAL",
            notes = "Full glass of warm water taken."
        )

        careLogRepository.saveLog(log)

        val recent = careLogRepository.getRecentLogs(DemoPatientConfig.PATIENT_ID, limit = 5)
        assertEquals(1, recent.size)
        assertEquals("HYDRATION", recent[0].category)

        val queue = syncQueueDao.getPendingSyncBatches()
        assertEquals(1, queue.size)
        assertEquals("care_logs", queue[0].tableName)
        assertEquals("INSERT", queue[0].operation)
    }

    @Test
    fun testGameRepositorySaveSessionIdempotencyAndReplace() = runBlocking {
        val sessionId = "session_idempotent_01"
        val initialSession = GameSessionEntity(
            id = sessionId,
            patientId = DemoPatientConfig.PATIENT_ID,
            gameId = "PATTERN_RECOGNITION",
            difficultyLevel = 1,
            durationMs = 20000L,
            accuracy = 0.5f,
            errors = 2,
            reactionTimeMs = 4000L,
            hesitationCount = 3,
            adaptationDecision = 1,
            completed = true,
            timestamp = 1000L
        )
        val updatedSession = GameSessionEntity(
            id = sessionId,
            patientId = DemoPatientConfig.PATIENT_ID,
            gameId = "PATTERN_RECOGNITION",
            difficultyLevel = 1,
            durationMs = 22000L,
            accuracy = 1.0f,
            errors = 0,
            reactionTimeMs = 1800L,
            hesitationCount = 0,
            adaptationDecision = 2,
            completed = true,
            timestamp = 2000L
        )

        // 1. Initial save
        gameRepository.saveGameSession(initialSession)
        var list = gameRepository.getAllSessionsList(DemoPatientConfig.PATIENT_ID)
        assertEquals(1, list.size)
        assertEquals(0.5f, list[0].accuracy)

        // 2. Re-save same session ID (REPLACE idempotency)
        gameRepository.saveGameSession(updatedSession)
        list = gameRepository.getAllSessionsList(DemoPatientConfig.PATIENT_ID)

        // Assert clean replacement: total count remains 1, updated session values present
        assertEquals(1, list.size)
        assertEquals(sessionId, list[0].id)
        assertEquals(1.0f, list[0].accuracy)
        assertEquals(2, list[0].adaptationDecision)

        // Assert latest session returns updated session
        val latest = gameRepository.getLatestSession(DemoPatientConfig.PATIENT_ID, "PATTERN_RECOGNITION")
        assertNotNull(latest)
        assertEquals(2, latest!!.adaptationDecision)
    }
}
