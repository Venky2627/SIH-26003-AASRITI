package com.sih26003.aasriti.data.sync

import com.sih26003.aasriti.data.firebase.FirestoreSyncAdapter
import com.sih26003.aasriti.data.local.dao.SyncQueueDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Durable local SyncQueue processing and state management layer for AASRITI.
 *
 * Drives opportunistic remote cloud synchronization to Cloud Firestore via FirestoreSyncAdapter.
 *
 * Room SQLite remains the King and single source of truth.
 * Items in `sync_queue` are marked `SYNCED` ONLY after successful Cloud Firestore acknowledgement.
 */
class SyncManager(
    private val syncQueueDao: SyncQueueDao,
    private val firestoreSyncAdapter: FirestoreSyncAdapter? = null
) {
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _lastProcessedCount = MutableStateFlow(0)
    val lastProcessedCount: StateFlow<Int> = _lastProcessedCount.asStateFlow()

    /**
     * Processes pending mutation items in the local Room sync queue.
     *
     * For each pending item:
     * - If `tableName == "game_sessions"` and `firestoreSyncAdapter` is provided:
     *   Calls `firestoreSyncAdapter.uploadSyncQueueItem(item)`.
     *   On Remote ACK success: calls `syncQueueDao.markSynced(item.id)`.
     *   On Remote failure/error: calls `syncQueueDao.incrementRetry(item.id)`.
     * - Unsupported table names or missing adapter: does NOT mark `SYNCED` (preserves PENDING state).
     *
     * Prunes acknowledged (`SYNCED`) items from the local queue upon batch completion.
     */
    suspend fun processPendingBatch(): Int {
        if (_isProcessing.value) return 0
        _isProcessing.value = true
        var processedCount = 0

        val supportedTables = setOf("game_sessions", "patients", "relationships", "reminders", "care_logs")

        try {
            val pendingItems = syncQueueDao.getPendingSyncBatches()
            for (item in pendingItems) {
                val success = if (supportedTables.contains(item.tableName)) {
                    firestoreSyncAdapter?.uploadSyncQueueItem(item) ?: false
                } else {
                    false
                }

                if (success) {
                    syncQueueDao.markSynced(item.id)
                    processedCount++
                } else {
                    syncQueueDao.incrementRetry(item.id)
                }
            }
            if (processedCount > 0) {
                // Prune remotely acknowledged items from queue
                syncQueueDao.clearCompleted()
            }
            _lastProcessedCount.value = processedCount
            return processedCount
        } finally {
            _isProcessing.value = false
        }
    }

    /**
     * Manually triggers cleanup of acknowledged sync records from Room SQLite.
     */
    suspend fun clearCompletedSyncs() {
        syncQueueDao.clearCompleted()
    }
}
