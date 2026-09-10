package com.sih26003.smritisetu.data.sync

import com.sih26003.smritisetu.data.local.dao.SyncQueueDao
import com.sih26003.smritisetu.data.local.entities.SyncQueueEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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

        override suspend fun getPendingCount(): Int {
            return items.count { it.status == "PENDING" }
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

    private class RecordingCloudDispatcher(
        var shouldSucceed: Boolean = true,
        var failOnIds: Set<Long> = emptySet()
    ) : CloudSyncDispatcher {
        val dispatchedItems = mutableListOf<SyncQueueEntity>()

        override suspend fun dispatchMutation(item: SyncQueueEntity): SyncDispatchResult {
            dispatchedItems.add(item)
            return if (!shouldSucceed || failOnIds.contains(item.id)) {
                SyncDispatchResult.RetryableError("Simulated remote cloud dispatch failure")
            } else {
                SyncDispatchResult.Success
            }
        }
    }

    @Test
    fun testProcessPendingBatchDrainsQueue() = runBlocking {
        val fakeDao = FakeSyncQueueDao()
        fakeDao.enqueue(SyncQueueEntity(id = 1L, tableName = "reminders", recordId = "rem_1", operation = "INSERT", payloadJson = "{}"))
        fakeDao.enqueue(SyncQueueEntity(id = 2L, tableName = "reminders", recordId = "rem_2", operation = "UPDATE", payloadJson = "{}"))

        val syncManager = SyncManager(
            syncQueueDao = fakeDao,
            connectivityMonitor = TestableNetworkConnectivityMonitor(true),
            cloudDispatcher = RecordingCloudDispatcher(shouldSucceed = true)
        )
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
        val fakeDao = FakeSyncQueueDao()
        fakeDao.enqueue(SyncQueueEntity(id = 1L, tableName = "reminders", recordId = "rem_1", operation = "INSERT", payloadJson = "{}"))
        fakeDao.enqueue(SyncQueueEntity(id = 2L, tableName = "reminders", recordId = "rem_2", operation = "UPDATE", payloadJson = "{}"))

        val dispatcher = RecordingCloudDispatcher(failOnIds = setOf(1L))
        val syncManager = SyncManager(
            syncQueueDao = fakeDao,
            connectivityMonitor = TestableNetworkConnectivityMonitor(true),
            cloudDispatcher = dispatcher
        )
        val processedCount = syncManager.processPendingBatch()

        assertEquals(1, processedCount)
        assertEquals(1, syncManager.lastProcessedCount.value)

        // Item 1 failed -> retryCount incremented, status remains PENDING in Room
        val item1 = fakeDao.items.firstOrNull { it.id == 1L }
        assertNotNull(item1)
        assertEquals(1, item1!!.retryCount)
        assertEquals("PENDING", item1.status)

        // Item 2 succeeded -> marked SYNCED and pruned by clearCompleted()
        val item2 = fakeDao.items.firstOrNull { it.id == 2L }
        assertEquals(null, item2)
    }

    @Test
    fun testAirplaneModePreservesQueueUntouched() = runBlocking {
        val fakeDao = FakeSyncQueueDao()
        fakeDao.enqueue(SyncQueueEntity(id = 10L, tableName = "care_logs", recordId = "log_10", operation = "INSERT", payloadJson = "{\"note\":\"test\"}"))
        fakeDao.enqueue(SyncQueueEntity(id = 11L, tableName = "game_sessions", recordId = "ses_11", operation = "INSERT", payloadJson = "{\"accuracy\":0.9}"))

        val connectivity = TestableNetworkConnectivityMonitor(onlineState = false) // Airplane mode ON
        val dispatcher = RecordingCloudDispatcher(shouldSucceed = true)

        val syncManager = SyncManager(
            syncQueueDao = fakeDao,
            connectivityMonitor = connectivity,
            cloudDispatcher = dispatcher
        )

        val summary = syncManager.processSyncWithSummary()

        assertTrue(summary.isOffline)
        assertEquals(0, summary.totalAttempted)
        assertEquals(0, summary.successful)
        assertEquals(0, dispatcher.dispatchedItems.size) // No network calls attempted
        assertEquals(2, fakeDao.items.size) // Both records safely preserved in SQLite
        assertEquals(2, fakeDao.getPendingCount())
    }

    @Test
    fun testEndToEndReconnectionLifecycle() = runBlocking {
        val fakeDao = FakeSyncQueueDao()
        val connectivity = TestableNetworkConnectivityMonitor(onlineState = false) // Start offline
        val dispatcher = RecordingCloudDispatcher(shouldSucceed = true)

        val syncManager = SyncManager(
            syncQueueDao = fakeDao,
            connectivityMonitor = connectivity,
            cloudDispatcher = dispatcher
        )

        // 1. User performs offline activities (e.g. playing game, quick care log)
        fakeDao.enqueue(SyncQueueEntity(id = 101L, tableName = "game_sessions", recordId = "game_01", operation = "INSERT", payloadJson = "{\"score\":100}"))
        fakeDao.enqueue(SyncQueueEntity(id = 102L, tableName = "care_logs", recordId = "care_01", operation = "INSERT", payloadJson = "{\"category\":\"MEDICINE\"}"))

        // 2. Offline sync attempt
        val offlineSummary = syncManager.processSyncWithSummary()
        assertTrue(offlineSummary.isOffline)
        assertEquals(2, fakeDao.getPendingCount())
        assertEquals(0, dispatcher.dispatchedItems.size)

        // 3. Airplane mode toggled OFF (Network restored)
        connectivity.setOnline(true)

        // 4. Background SyncManager triggers
        val onlineSummary = syncManager.processSyncWithSummary()
        assertFalse(onlineSummary.isOffline)
        assertEquals(2, onlineSummary.totalAttempted)
        assertEquals(2, onlineSummary.successful)
        assertEquals(2, dispatcher.dispatchedItems.size)
        assertEquals(0, fakeDao.items.size) // Completed items cleared from SQLite queue
    }

    @Test
    fun testDuplicateProtectionIdempotency() = runBlocking {
        val fakeDao = FakeSyncQueueDao()
        val dispatcher = RecordingCloudDispatcher(shouldSucceed = true)
        val syncManager = SyncManager(
            syncQueueDao = fakeDao,
            connectivityMonitor = TestableNetworkConnectivityMonitor(true),
            cloudDispatcher = dispatcher
        )

        // Enqueue duplicate records for same entity ID
        fakeDao.enqueue(SyncQueueEntity(id = 201L, tableName = "care_plans", recordId = "plan_01", operation = "INSERT", payloadJson = "{\"status\":\"STABLE\"}"))
        fakeDao.enqueue(SyncQueueEntity(id = 202L, tableName = "care_plans", recordId = "plan_01", operation = "UPDATE", payloadJson = "{\"status\":\"MONITORING\"}"))

        val summary = syncManager.processSyncWithSummary()

        assertEquals(2, summary.totalAttempted)
        assertEquals(2, summary.successful)
        assertEquals(2, dispatcher.dispatchedItems.size)
        // Both items reference the exact same document ID ("plan_01")
        assertEquals("plan_01", dispatcher.dispatchedItems[0].recordId)
        assertEquals("plan_01", dispatcher.dispatchedItems[1].recordId)
    }

    @Test
    fun testProcessPendingBatchReentrancyGuard() = runBlocking {
        var nestedResult = -1
        lateinit var syncManager: SyncManager

        val fakeDao = FakeSyncQueueDao(
            markSyncedCallback = { _ ->
                nestedResult = syncManager.processPendingBatch()
            }
        )
        fakeDao.enqueue(SyncQueueEntity(id = 1L, tableName = "care_logs", recordId = "log_1", operation = "INSERT", payloadJson = "{}"))

        syncManager = SyncManager(
            syncQueueDao = fakeDao,
            connectivityMonitor = TestableNetworkConnectivityMonitor(true),
            cloudDispatcher = RecordingCloudDispatcher(shouldSucceed = true)
        )
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
