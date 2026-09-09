package com.sih26003.smritisetu.data.sync

import com.sih26003.smritisetu.data.local.dao.SyncQueueDao
import com.sih26003.smritisetu.data.local.entities.SyncQueueEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SyncManagerTest {

    private class FakeSyncQueueDao : SyncQueueDao {
        val items = mutableListOf<SyncQueueEntity>()

        override suspend fun getPendingSyncBatches(): List<SyncQueueEntity> {
            return items.filter { it.status == "PENDING" }.take(25)
        }

        override suspend fun enqueue(syncItem: SyncQueueEntity) {
            items.add(syncItem)
        }

        override suspend fun markSynced(id: Long) {
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
}
