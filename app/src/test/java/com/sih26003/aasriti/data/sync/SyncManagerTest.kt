package com.sih26003.aasriti.data.sync

import com.google.gson.Gson
import com.sih26003.aasriti.data.firebase.FirestoreSyncAdapter
import com.sih26003.aasriti.data.local.dao.SyncQueueDao
import com.sih26003.aasriti.data.local.entities.CareLogEntity
import com.sih26003.aasriti.data.local.entities.GameSessionEntity
import com.sih26003.aasriti.data.local.entities.PatientEntity
import com.sih26003.aasriti.data.local.entities.RelationshipEntity
import com.sih26003.aasriti.data.local.entities.ReminderEntity
import com.sih26003.aasriti.data.local.entities.SyncQueueEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class SyncManagerTest {

    private class FakeSyncQueueDao(
        private val failOnIds: Set<Long> = emptySet(),
        private val markSyncedCallback: (suspend (Long) -> Unit)? = null
    ) : SyncQueueDao {
        val items = mutableListOf<SyncQueueEntity>()

        override suspend fun getPendingSyncBatches(): List<SyncQueueEntity> {
            return items.filter { it.status == "PENDING" }.take(25)
        }

        override suspend fun enqueue(syncItem: SyncQueueEntity) {
            items.add(syncItem)
        }

        override suspend fun markSynced(id: Long) {
            markSyncedCallback?.invoke(id)
            if (failOnIds.contains(id)) {
                throw RuntimeException("Simulated DAO markSynced failure for id $id")
            }
            val idx = items.indexOfFirst { it.id == id }
            if (idx != -1) {
                items[idx] = items[idx].copy(status = "SYNCED")
            }
        }

        override suspend fun incrementRetry(id: Long) {
            val idx = items.indexOfFirst { it.id == id }
            if (idx != -1) {
                items[idx] = items[idx].copy(retryCount = items[idx].retryCount + 1)
            }
        }

        override suspend fun clearCompleted() {
            items.removeAll { it.status == "SYNCED" }
        }
    }

    private class FakeFirestoreSyncAdapter(
        private val shouldSucceed: Boolean = true
    ) : FirestoreSyncAdapter() {
        val uploadedItems = mutableListOf<SyncQueueEntity>()

        override suspend fun uploadSyncQueueItem(item: SyncQueueEntity): Boolean {
            uploadedItems.add(item)
            return shouldSucceed
        }
    }

    @Test
    fun testProcessPendingBatchAllSupportedEntitiesSuccess() = runBlocking {
        val fakeDao = FakeSyncQueueDao()
        val gson = Gson()

        val session = GameSessionEntity(id = "sess_1", patientId = "aita_borah_01", gameId = "FLOWER_MATCH", difficultyLevel = 1, durationMs = 12000L, accuracy = 1.0f, errors = 0, reactionTimeMs = 1500L, hesitationCount = 0, adaptationDecision = 2, completed = true)
        val patient = PatientEntity(id = "aita_borah_01", pseudonymCode = "AS-KAM-0042", birthYear = 1958, gender = "F", primaryLanguage = "as", cognitiveStage = "MCI")
        val rel = RelationshipEntity(id = "rel_1", patientId = "aita_borah_01", name = "Rupam", relationshipType = "SON")
        val reminder = ReminderEntity(id = "rem_1", patientId = "aita_borah_01", title = "Water", reminderType = "HYDRATION", hour = 11, minute = 0)
        val careLog = CareLogEntity(id = "log_1", patientId = "aita_borah_01", category = "MEDICINE", severity = "NORMAL", notes = "Taken")

        fakeDao.enqueue(SyncQueueEntity(id = 1L, tableName = "game_sessions", recordId = session.id, operation = "INSERT", payloadJson = gson.toJson(session)))
        fakeDao.enqueue(SyncQueueEntity(id = 2L, tableName = "patients", recordId = patient.id, operation = "INSERT", payloadJson = gson.toJson(patient)))
        fakeDao.enqueue(SyncQueueEntity(id = 3L, tableName = "relationships", recordId = rel.id, operation = "INSERT", payloadJson = gson.toJson(rel)))
        fakeDao.enqueue(SyncQueueEntity(id = 4L, tableName = "reminders", recordId = reminder.id, operation = "INSERT", payloadJson = gson.toJson(reminder)))
        fakeDao.enqueue(SyncQueueEntity(id = 5L, tableName = "care_logs", recordId = careLog.id, operation = "INSERT", payloadJson = gson.toJson(careLog)))

        val fakeAdapter = FakeFirestoreSyncAdapter(shouldSucceed = true)
        val syncManager = SyncManager(fakeDao, fakeAdapter)

        val processedCount = syncManager.processPendingBatch()

        assertEquals(5, processedCount)
        assertEquals(5, syncManager.lastProcessedCount.value)
        assertEquals(5, fakeAdapter.uploadedItems.size)
        assertEquals(0, fakeDao.items.size) // All 5 SYNCED & cleared
    }

    @Test
    fun testProcessPendingBatchGameSessionFailureIncrementsRetry() = runBlocking {
        val fakeDao = FakeSyncQueueDao()
        fakeDao.enqueue(
            SyncQueueEntity(
                id = 1L,
                tableName = "game_sessions",
                recordId = "sess_999",
                operation = "INSERT",
                payloadJson = "{}"
            )
        )

        val fakeAdapter = FakeFirestoreSyncAdapter(shouldSucceed = false)
        val syncManager = SyncManager(fakeDao, fakeAdapter)

        val processedCount = syncManager.processPendingBatch()

        assertEquals(0, processedCount)
        assertEquals(0, syncManager.lastProcessedCount.value)

        val item = fakeDao.items.firstOrNull { it.id == 1L }
        assertNotNull(item)
        assertEquals(1, item!!.retryCount)
        assertEquals("PENDING", item.status)
    }

    @Test
    fun testProcessPendingBatchUnsupportedTableNotSynced() = runBlocking {
        val fakeDao = FakeSyncQueueDao()
        fakeDao.enqueue(
            SyncQueueEntity(
                id = 1L,
                tableName = "unsupported_table",
                recordId = "id_1",
                operation = "INSERT",
                payloadJson = "{}"
            )
        )

        val syncManager = SyncManager(fakeDao)
        val processedCount = syncManager.processPendingBatch()

        assertEquals(0, processedCount)
        val item = fakeDao.items.firstOrNull { it.id == 1L }
        assertNotNull(item)
        assertEquals(1, item!!.retryCount)
        assertEquals("PENDING", item.status)
    }

    @Test
    fun testClearCompletedSyncs() = runBlocking {
        val fakeDao = FakeSyncQueueDao()
        fakeDao.enqueue(SyncQueueEntity(id = 1L, tableName = "patients", recordId = "p_1", operation = "INSERT", payloadJson = "{}", status = "SYNCED"))
        fakeDao.enqueue(SyncQueueEntity(id = 2L, tableName = "patients", recordId = "p_2", operation = "INSERT", payloadJson = "{}", status = "PENDING"))

        val syncManager = SyncManager(fakeDao)
        syncManager.clearCompletedSyncs()

        assertEquals(1, fakeDao.items.size)
        assertEquals(2L, fakeDao.items[0].id)
        assertEquals("PENDING", fakeDao.items[0].status)
    }
}
