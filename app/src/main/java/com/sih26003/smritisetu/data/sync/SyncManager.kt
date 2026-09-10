package com.sih26003.smritisetu.data.sync

import com.sih26003.smritisetu.data.local.dao.CareLogDao
import com.sih26003.smritisetu.data.local.dao.CarePlanDao
import com.sih26003.smritisetu.data.local.dao.GameSessionDao
import com.sih26003.smritisetu.data.local.dao.MemoryItemDao
import com.sih26003.smritisetu.data.local.dao.PatientDao
import com.sih26003.smritisetu.data.local.dao.RelationshipDao
import com.sih26003.smritisetu.data.local.dao.ReminderDao
import com.sih26003.smritisetu.data.local.dao.SyncQueueDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Summary of a processed sync batch.
 */
data class SyncBatchSummary(
    val totalAttempted: Int,
    val successful: Int,
    val failed: Int,
    val isOffline: Boolean
)

/**
 * Durable local-first SyncQueue processing and Firestore replication layer for AASRITI.
 *
 * ARCHITECTURAL CONTRACT:
 * 1. Room SQLite is the absolute single source of truth.
 * 2. Mutations are enqueued into `sync_queue` table inside local transactions.
 * 3. NetworkConnectivityMonitor detects real device connectivity or Airplane Mode.
 * 4. In Airplane Mode / Offline:
 *    - Queue processing halts immediately.
 *    - Pending mutations remain preserved in Room SQLite across process restarts.
 * 5. When Online:
 *    - Pending batches (up to 25 records) are dispatched via CloudSyncDispatcher to Firebase Firestore.
 *    - Duplicate protection is guaranteed via idempotent upserts keyed on Room UUID.
 *    - A record is marked SYNCED ONLY when remote Firestore dispatch succeeds.
 *    - Failed dispatches increment retry counters and remain PENDING for next reconnect.
 */
class SyncManager(
    private val syncQueueDao: SyncQueueDao,
    private val connectivityMonitor: NetworkConnectivityMonitor = TestableNetworkConnectivityMonitor(true),
    private val cloudDispatcher: CloudSyncDispatcher = FirestoreCloudSyncDispatcher(),
    private val patientDao: PatientDao? = null,
    private val relationshipDao: RelationshipDao? = null,
    private val gameSessionDao: GameSessionDao? = null,
    private val careLogDao: CareLogDao? = null,
    private val reminderDao: ReminderDao? = null,
    private val carePlanDao: CarePlanDao? = null,
    private val memoryItemDao: MemoryItemDao? = null
) {
    /**
     * Legacy constructor preserving backwards compatibility with existing Phase 1 call sites.
     */
    constructor(syncQueueDao: SyncQueueDao) : this(
        syncQueueDao = syncQueueDao,
        connectivityMonitor = TestableNetworkConnectivityMonitor(true),
        cloudDispatcher = FirestoreCloudSyncDispatcher()
    )

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _lastProcessedCount = MutableStateFlow(0)
    val lastProcessedCount: StateFlow<Int> = _lastProcessedCount.asStateFlow()

    private val _pendingQueueCount = MutableStateFlow(0)
    val pendingQueueCount: StateFlow<Int> = _pendingQueueCount.asStateFlow()

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    /**
     * Drains pending sync queue batches and dispatches to cloud storage.
     * Returns the count of successfully synchronized items (backwards compatible with tests).
     */
    suspend fun processPendingBatch(): Int {
        val summary = processSyncWithSummary()
        return summary.successful
    }

    /**
     * Full batch synchronization with comprehensive operational diagnostics.
     */
    suspend fun processSyncWithSummary(): SyncBatchSummary {
        if (_isProcessing.value) {
            return SyncBatchSummary(0, 0, 0, isOffline = false)
        }

        // 1. Connectivity Gate: Check real network state (or Airplane Mode)
        val online = connectivityMonitor.isOnline()
        _isOnline.value = online
        if (!online) {
            // Offline / Airplane Mode: do NOT drain queue. Keep all records safely in SQLite.
            refreshPendingCount()
            return SyncBatchSummary(totalAttempted = 0, successful = 0, failed = 0, isOffline = true)
        }

        _isProcessing.value = true
        var attemptedCount = 0
        var successfulCount = 0
        var failedCount = 0

        try {
            val pendingItems = syncQueueDao.getPendingSyncBatches()
            attemptedCount = pendingItems.size

            for (item in pendingItems) {
                try {
                    val result = cloudDispatcher.dispatchMutation(item)
                    when (result) {
                        is SyncDispatchResult.Success -> {
                            // Only mark SYNCED upon confirmed cloud write
                            syncQueueDao.markSynced(item.id)
                            markLocalEntitySynced(item.tableName, item.recordId)
                            successfulCount++
                        }
                        is SyncDispatchResult.RetryableError -> {
                            syncQueueDao.incrementRetry(item.id)
                            failedCount++
                        }
                        is SyncDispatchResult.FatalError -> {
                            syncQueueDao.incrementRetry(item.id)
                            failedCount++
                        }
                    }
                } catch (e: Exception) {
                    syncQueueDao.incrementRetry(item.id)
                    failedCount++
                }
            }

            if (successfulCount > 0) {
                // Prune verified acknowledged items from local SQLite queue
                syncQueueDao.clearCompleted()
            }

            _lastProcessedCount.value = successfulCount
            refreshPendingCount()

            return SyncBatchSummary(
                totalAttempted = attemptedCount,
                successful = successfulCount,
                failed = failedCount,
                isOffline = false
            )
        } finally {
            _isProcessing.value = false
        }
    }

    /**
     * Marks the corresponding local Room entity as isSynced = true.
     */
    private suspend fun markLocalEntitySynced(tableName: String, recordId: String) {
        try {
            when (tableName) {
                "patients" -> patientDao?.markSynced(recordId)
                "relationships" -> relationshipDao?.markSynced(recordId)
                "game_sessions" -> gameSessionDao?.markSynced(recordId)
                "care_logs" -> careLogDao?.markSynced(recordId)
                "reminders" -> reminderDao?.markSynced(recordId)
                "care_plans" -> carePlanDao?.markSynced(recordId)
                "memory_items" -> memoryItemDao?.markSynced(recordId)
            }
        } catch (_: Exception) {
            // Non-critical local flag update failure
        }
    }

    /**
     * Refreshes the pending queue count state.
     */
    suspend fun refreshPendingCount() {
        try {
            _pendingQueueCount.value = syncQueueDao.getPendingCount()
        } catch (_: Exception) {
            // Ignore in lightweight test contexts
        }
    }

    /**
     * Manually triggers cleanup of acknowledged sync records from Room SQLite.
     */
    suspend fun clearCompletedSyncs() {
        syncQueueDao.clearCompleted()
        refreshPendingCount()
    }
}
