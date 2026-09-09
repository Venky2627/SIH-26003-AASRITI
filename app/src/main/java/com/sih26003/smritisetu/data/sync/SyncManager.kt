package com.sih26003.smritisetu.data.sync

import com.sih26003.smritisetu.data.local.dao.SyncQueueDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Durable local SyncQueue processing and state management layer for AASRITI.
 * Processes pending local mutations from Room SQLite sync_queue table.
 * Pure local offline processor with zero external Firebase/cloud dependencies.
 */
class SyncManager(
    private val syncQueueDao: SyncQueueDao
) {
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _lastProcessedCount = MutableStateFlow(0)
    val lastProcessedCount: StateFlow<Int> = _lastProcessedCount.asStateFlow()

    /**
     * Drains pending items from the durable local sync queue in Room.
     * Marks pending items as SYNCED in Room SQLite.
     * Increments retry count on processing errors.
     * Prunes completed items and returns total count of processed records.
     */
    suspend fun processPendingBatch(): Int {
        if (_isProcessing.value) return 0
        _isProcessing.value = true
        var processedCount = 0

        try {
            val pendingItems = syncQueueDao.getPendingSyncBatches()
            for (item in pendingItems) {
                try {
                    // Local durable state transition: mark item as SYNCED
                    syncQueueDao.markSynced(item.id)
                    processedCount++
                } catch (e: Exception) {
                    // Increment retry counter on error
                    syncQueueDao.incrementRetry(item.id)
                }
            }
            if (processedCount > 0) {
                // Prune successfully synced entries from queue
                syncQueueDao.clearCompleted()
            }
            _lastProcessedCount.value = processedCount
            return processedCount
        } finally {
            _isProcessing.value = false
        }
    }

    /**
     * Manually triggers cleanup of completed sync records from Room SQLite.
     */
    suspend fun clearCompletedSyncs() {
        syncQueueDao.clearCompleted()
    }
}
