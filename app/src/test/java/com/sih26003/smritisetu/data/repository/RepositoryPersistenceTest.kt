package com.sih26003.smritisetu.data.repository

import com.sih26003.smritisetu.data.local.dao.CareLogDao
import com.sih26003.smritisetu.data.local.dao.CarePlanDao
import com.sih26003.smritisetu.data.local.dao.GameSessionDao
import com.sih26003.smritisetu.data.local.dao.MemoryItemDao
import com.sih26003.smritisetu.data.local.dao.PatientDao
import com.sih26003.smritisetu.data.local.dao.RelationshipDao
import com.sih26003.smritisetu.data.local.dao.ReminderDao
import com.sih26003.smritisetu.data.local.dao.SyncQueueDao
import com.sih26003.smritisetu.data.local.entities.CareLogEntity
import com.sih26003.smritisetu.data.local.entities.CarePlanEntity
import com.sih26003.smritisetu.data.local.entities.GameSessionEntity
import com.sih26003.smritisetu.data.local.entities.MemoryItemEntity
import com.sih26003.smritisetu.data.local.entities.PatientEntity
import com.sih26003.smritisetu.data.local.entities.RelationshipEntity
import com.sih26003.smritisetu.data.local.entities.ReminderEntity
import com.sih26003.smritisetu.data.local.entities.SyncQueueEntity
import com.sih26003.smritisetu.demo.DemoPatientConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

        override suspend fun getPendingCount(): Int =
            queue.count { it.status == "PENDING" }

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

        override fun getRecentSessions(patientId: String, limit: Int): Flow<List<GameSessionEntity>> =
            flowOf(sessions.filter { it.patientId == patientId }.sortedByDescending { it.timestamp }.take(limit))

        override suspend fun getTotalSessionsCount(patientId: String): Int =
            sessions.count { it.patientId == patientId }

        override suspend fun getSessionsSince(patientId: String, sinceTimestamp: Long): List<GameSessionEntity> =
            sessions.filter { it.patientId == patientId && it.timestamp >= sinceTimestamp }.sortedByDescending { it.timestamp }

        override suspend fun insertSession(session: GameSessionEntity) {
            sessions.removeAll { it.id == session.id }
            sessions.add(session)
        }

        override suspend fun markSynced(id: String) {
            val idx = sessions.indexOfFirst { it.id == id }
            if (idx != -1) sessions[idx] = sessions[idx].copy(isSynced = true)
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

        override suspend fun markSynced(id: String) {
            val idx = reminders.indexOfFirst { it.id == id }
            if (idx != -1) reminders[idx] = reminders[idx].copy(isSynced = true)
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

        override suspend fun markSynced(id: String) {
            val idx = patients.indexOfFirst { it.id == id }
            if (idx != -1) patients[idx] = patients[idx].copy(isSynced = true)
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

        override suspend fun markSynced(id: String) {
            val idx = relationships.indexOfFirst { it.id == id }
            if (idx != -1) relationships[idx] = relationships[idx].copy(isSynced = true)
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

        override suspend fun getLogsSince(patientId: String, sinceTimestamp: Long): List<CareLogEntity> =
            logs.filter { it.patientId == patientId && it.timestamp >= sinceTimestamp }.sortedByDescending { it.timestamp }

        override suspend fun getLogsByCategory(patientId: String, category: String): List<CareLogEntity> =
            logs.filter { it.patientId == patientId && it.category == category }.sortedByDescending { it.timestamp }

        override suspend fun getUrgentLogsCount(patientId: String): Int =
            logs.count { it.patientId == patientId && (it.severity == "PRIORITY" || it.severity == "URGENT") }

        override suspend fun insertLog(log: CareLogEntity) {
            logs.removeAll { it.id == log.id }
            logs.add(log)
        }

        override suspend fun markSynced(id: String) {
            val idx = logs.indexOfFirst { it.id == id }
            if (idx != -1) logs[idx] = logs[idx].copy(isSynced = true)
        }

        override suspend fun deleteLog(id: String) {
            logs.removeAll { it.id == id }
        }
    }

    private class FakeCarePlanDao : CarePlanDao {
        val plans = mutableListOf<CarePlanEntity>()

        override fun getLatestCarePlanFlow(patientId: String): Flow<CarePlanEntity?> =
            flowOf(plans.filter { it.patientId == patientId }.maxByOrNull { it.updatedAt })

        override suspend fun getLatestCarePlan(patientId: String): CarePlanEntity? =
            plans.filter { it.patientId == patientId }.maxByOrNull { it.updatedAt }

        override fun getAllCarePlansForPatient(patientId: String): Flow<List<CarePlanEntity>> =
            flowOf(plans.filter { it.patientId == patientId }.sortedByDescending { it.updatedAt })

        override suspend fun insertCarePlan(carePlan: CarePlanEntity) {
            plans.removeAll { it.id == carePlan.id }
            plans.add(carePlan)
        }

        override suspend fun markSynced(id: String) {
            val idx = plans.indexOfFirst { it.id == id }
            if (idx != -1) plans[idx] = plans[idx].copy(isSynced = true)
        }

        override suspend fun deleteCarePlan(id: String) {
            plans.removeAll { it.id == id }
        }
    }

    private class FakeMemoryItemDao : MemoryItemDao {
        val memories = mutableListOf<MemoryItemEntity>()

        override fun getMemoriesForPatient(patientId: String): Flow<List<MemoryItemEntity>> =
            flowOf(memories.filter { it.patientId == patientId }.sortedByDescending { it.createdAt })

        override suspend fun getMemoriesList(patientId: String): List<MemoryItemEntity> =
            memories.filter { it.patientId == patientId }.sortedByDescending { it.createdAt }

        override suspend fun getMemoryById(id: String): MemoryItemEntity? =
            memories.firstOrNull { it.id == id }

        override suspend fun insertMemory(memory: MemoryItemEntity) {
            memories.removeAll { it.id == memory.id }
            memories.add(memory)
        }

        override suspend fun markSynced(id: String) {
            val idx = memories.indexOfFirst { it.id == id }
            if (idx != -1) memories[idx] = memories[idx].copy(isSynced = true)
        }

        override suspend fun deleteMemory(id: String) {
            memories.removeAll { it.id == id }
        }
    }

    private lateinit var syncQueueDao: FakeSyncQueueDao
    private lateinit var gameSessionDao: FakeGameSessionDao
    private lateinit var reminderDao: FakeReminderDao
    private lateinit var patientDao: FakePatientDao
    private lateinit var relationshipDao: FakeRelationshipDao
    private lateinit var careLogDao: FakeCareLogDao
    private lateinit var carePlanDao: FakeCarePlanDao
    private lateinit var memoryItemDao: FakeMemoryItemDao

    private lateinit var gameRepository: GameRepository
    private lateinit var reminderRepository: ReminderRepository
    private lateinit var patientRepository: PatientRepository
    private lateinit var careLogRepository: CareLogRepository
    private lateinit var carePlanRepository: CarePlanRepository
    private lateinit var memoryRepository: MemoryRepository

    @Before
    fun setUp() {
        syncQueueDao = FakeSyncQueueDao()
        gameSessionDao = FakeGameSessionDao()
        reminderDao = FakeReminderDao()
        patientDao = FakePatientDao()
        relationshipDao = FakeRelationshipDao()
        careLogDao = FakeCareLogDao()
        carePlanDao = FakeCarePlanDao()
        memoryItemDao = FakeMemoryItemDao()

        gameRepository = GameRepository(gameSessionDao, syncQueueDao)
        reminderRepository = ReminderRepository(reminderDao, syncQueueDao)
        patientRepository = PatientRepository(patientDao, relationshipDao, syncQueueDao)
        careLogRepository = CareLogRepository(careLogDao, syncQueueDao)
        carePlanRepository = CarePlanRepository(carePlanDao, syncQueueDao)
        memoryRepository = MemoryRepository(memoryItemDao, syncQueueDao)
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
    fun testGameRepositoryAnalyticsQueries() = runBlocking {
        val s1 = GameSessionEntity(
            patientId = DemoPatientConfig.PATIENT_ID,
            gameId = "SEQUENCING",
            difficultyLevel = 1,
            durationMs = 10000L,
            accuracy = 0.8f,
            errors = 1,
            reactionTimeMs = 2500L,
            hesitationCount = 1,
            adaptationDecision = 2,
            completed = true,
            timestamp = 1000L
        )
        val s2 = GameSessionEntity(
            patientId = DemoPatientConfig.PATIENT_ID,
            gameId = "SEQUENCING",
            difficultyLevel = 2,
            durationMs = 12000L,
            accuracy = 1.0f,
            errors = 0,
            reactionTimeMs = 1900L,
            hesitationCount = 0,
            adaptationDecision = 3,
            completed = true,
            timestamp = 5000L
        )

        gameRepository.saveGameSession(s1)
        gameRepository.saveGameSession(s2)

        assertEquals(2, gameRepository.getTotalSessionsCount(DemoPatientConfig.PATIENT_ID))
        val recentSince = gameRepository.getSessionsSince(DemoPatientConfig.PATIENT_ID, sinceTimestamp = 3000L)
        assertEquals(1, recentSince.size)
        assertEquals(s2.id, recentSince[0].id)
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
    fun testCareLogRepositoryAnalyticsQueries() = runBlocking {
        val l1 = CareLogEntity(
            id = "log_002",
            patientId = DemoPatientConfig.PATIENT_ID,
            category = "FALL",
            severity = "URGENT",
            notes = "Slipped near bathroom.",
            timestamp = 1000L
        )
        val l2 = CareLogEntity(
            id = "log_003",
            patientId = DemoPatientConfig.PATIENT_ID,
            category = "SLEEP",
            severity = "NORMAL",
            notes = "Slept 7 hours peacefully.",
            timestamp = 4000L
        )

        careLogRepository.saveLog(l1)
        careLogRepository.saveLog(l2)

        assertEquals(1, careLogRepository.getUrgentLogsCount(DemoPatientConfig.PATIENT_ID))
        val sleepLogs = careLogRepository.getLogsByCategory(DemoPatientConfig.PATIENT_ID, "SLEEP")
        assertEquals(1, sleepLogs.size)
        assertEquals("log_003", sleepLogs[0].id)
    }

    @Test
    fun testCarePlanRepositoryPersistenceAndSyncQueue() = runBlocking {
        val plan = CarePlanEntity(
            id = "plan_test_01",
            patientId = DemoPatientConfig.PATIENT_ID,
            doctorId = "doc_01",
            doctorName = "Dr. S. Sharma",
            clinicalStatus = "STABLE",
            reviewScheduleWeeks = 4,
            guidanceNotes = "Daily morning hydration routine and gentle garden walk recommended."
        )

        carePlanRepository.saveCarePlan(plan)

        val retrieved = carePlanRepository.getLatestCarePlanDirect(DemoPatientConfig.PATIENT_ID)
        assertNotNull(retrieved)
        assertEquals("plan_test_01", retrieved!!.id)
        assertEquals("STABLE", retrieved.clinicalStatus)
        assertEquals(4, retrieved.reviewScheduleWeeks)

        val queue = syncQueueDao.getPendingSyncBatches()
        assertEquals(1, queue.size)
        assertEquals("care_plans", queue[0].tableName)
        assertEquals("plan_test_01", queue[0].recordId)
        assertEquals("INSERT", queue[0].operation)
        assertTrue(queue[0].payloadJson.contains("Dr. S. Sharma"))
    }

    @Test
    fun testMemoryRepositoryPersistenceAndSyncQueue() = runBlocking {
        val memory = MemoryItemEntity(
            id = "mem_test_01",
            patientId = DemoPatientConfig.PATIENT_ID,
            titleIndic = "বিহু নৃত্য",
            titleEn = "Bihu Dance Celebration",
            locationTag = "Sibsagar, Assam",
            yearTag = "1974",
            storyIndic = "বৰ পথাৰত বিহু নাচিছিলো।",
            storyEn = "Dancing Bihu in the wide paddy field.",
            relativeNarrator = "পুত্ৰ ৰূপম"
        )

        memoryRepository.saveMemory(memory)

        val list = memoryRepository.getMemoriesList(DemoPatientConfig.PATIENT_ID)
        assertEquals(1, list.size)
        assertEquals("বিহু নৃত্য", list[0].titleIndic)

        val queue = syncQueueDao.getPendingSyncBatches()
        assertEquals(1, queue.size)
        assertEquals("memory_items", queue[0].tableName)
        assertEquals("mem_test_01", queue[0].recordId)

        // Delete test
        memoryRepository.deleteMemory("mem_test_01")
        assertEquals(0, memoryRepository.getMemoriesList(DemoPatientConfig.PATIENT_ID).size)
        assertEquals(2, syncQueueDao.getPendingSyncBatches().size)
        assertEquals("DELETE", syncQueueDao.getPendingSyncBatches()[1].operation)
    }

    @Test
    fun testMultiRoleUnifiedPatientDataIsolation() = runBlocking {
        val patientA = "patient_alpha"
        val patientB = "patient_beta"

        // Alpha logs
        careLogRepository.saveLog(CareLogEntity(id = "log_a", patientId = patientA, category = "MEDICINE", severity = "NORMAL", notes = "A dose"))
        gameRepository.saveGameSession(GameSessionEntity(id = "game_a", patientId = patientA, gameId = "SEQUENCING", difficultyLevel = 1, durationMs = 1000L, accuracy = 1.0f, errors = 0, reactionTimeMs = 2000L, hesitationCount = 0, adaptationDecision = 2, completed = true))

        // Beta logs
        careLogRepository.saveLog(CareLogEntity(id = "log_b", patientId = patientB, category = "FALL", severity = "PRIORITY", notes = "B fall"))

        // Assert strict isolation: Patient A sees only A's data
        val logsA = careLogRepository.getRecentLogs(patientA)
        assertEquals(1, logsA.size)
        assertEquals("log_a", logsA[0].id)

        val logsB = careLogRepository.getRecentLogs(patientB)
        assertEquals(1, logsB.size)
        assertEquals("log_b", logsB[0].id)

        val sessionsA = gameRepository.getAllSessionsList(patientA)
        assertEquals(1, sessionsA.size)
        assertEquals("game_a", sessionsA[0].id)

        val sessionsB = gameRepository.getAllSessionsList(patientB)
        assertEquals(0, sessionsB.size)
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
