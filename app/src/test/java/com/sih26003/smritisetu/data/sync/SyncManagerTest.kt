package com.sih26003.smritisetu.data.sync

import com.sih26003.smritisetu.data.local.dao.SyncQueueDao
import com.sih26003.smritisetu.data.local.entities.SyncQueueEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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

    @Test
    fun testProcessPendingBatchDrainsQueue() = runBlocking {
        val fakeDao = FakeSyncQueueDao()
        fakeDao.enqueue(SyncQueueEntity(id = 1L, tableName = "reminders", recordId = "rem_1", operation = "INSERT", payloadJson = "{}"))
        fakeDao.enqueue(SyncQueueEntity(id = 2L, tableName = "reminders", recordId = "rem_2", operation = "UPDATE", payloadJson = "{}"))

        val syncManager = SyncManager(fakeDao)
        assertEquals(0, syncManager.lastProcessedCount.value)
        assertFalse(syncManager.isProcessing.value)

        val processedCount = syncManager.processPendingBatch()

        assertEquals(2, processedCount)
        assertEquals(2, syncManager.lastProcessedCount.value)
        assertEquals(0, fakeDao.items.size)
    }

    @Test
    fun testProcessPendingBatchHandlesEmptyQueue() = runBlocking {
        val fakeDao = FakeSyncQueueDao()
        val syncManager = SyncManager(fakeDao)

        val processedCount = syncManager.processPendingBatch()

        assertEquals(0, processedCount)
        assertEquals(0, syncManager.lastProcessedCount.value)
    }

    @Test
    fun testProcessPendingBatchIncrementsRetryOnFailure() = runBlocking {
        val fakeDao = FakeSyncQueueDao(failOnIds = setOf(1L))
        fakeDao.enqueue(SyncQueueEntity(id = 1L, tableName = "reminders", recordId = "rem_1", operation = "INSERT", payloadJson = "{}"))
        fakeDao.enqueue(SyncQueueEntity(id = 2L, tableName = "reminders", recordId = "rem_2", operation = "UPDATE", payloadJson = "{}"))

        val syncManager = SyncManager(fakeDao)
        val processedCount = syncManager.processPendingBatch()

        assertEquals(1, processedCount)
        assertEquals(1, syncManager.lastProcessedCount.value)

        // Item 1 failed markSynced -> retryCount incremented, status remains PENDING
        val item1 = fakeDao.items.firstOrNull { it.id == 1L }
        assertNotNull(item1)
        assertEquals(1, item1!!.retryCount)
        assertEquals("PENDING", item1.status)

        // Item 2 succeeded -> marked SYNCED and pruned by clearCompleted()
        val item2 = fakeDao.items.firstOrNull { it.id == 2L }
        assertEquals(null, item2)
    }

    @Test
    fun testProcessPendingBatchReentrancyGuard() = runBlocking {
        var nestedResult = -1
        lateinit var syncManager: SyncManager

        val fakeDao = FakeSyncQueueDao(
            markSyncedCallback = { _ ->
                // Attempt re-entrant call while isProcessing is true
                nestedResult = syncManager.processPendingBatch()
            }
        )
        fakeDao.enqueue(SyncQueueEntity(id = 1L, tableName = "care_logs", recordId = "log_1", operation = "INSERT", payloadJson = "{}"))

        syncManager = SyncManager(fakeDao)
        val outerResult = syncManager.processPendingBatch()

        assertEquals(0, nestedResult) // Re-entrant call returned 0 immediately
        assertEquals(1, outerResult)
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
