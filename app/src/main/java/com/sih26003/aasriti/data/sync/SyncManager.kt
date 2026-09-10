package com.sih26003.aasriti.data.sync

import com.sih26003.aasriti.data.local.dao.SyncQueueDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Durable local SyncQueue processing and state management layer for AASRITI.
 *
 * PHASE 1 LOCAL QUEUE ACKNOWLEDGEMENT STUB:
 * This class provides local queue buffer processing and state management for offline mutation tracking
 * in Room SQLite (`sync_queue` table).
 *
 * It uses the existing Room schema status semantics ("PENDING" -> "SYNCED") to process and drain
 * locally acknowledged mutation batches.
 *
 * NOTE: This processor operates 100% locally. It does NOT perform remote Firebase or cloud network
 * synchronization, nor does it represent remote cloud delivery. Actual remote transport dispatching
 * is reserved for Phase 2 integration adapters built on top of this local queue.
 */
class SyncManager(
    private val syncQueueDao: SyncQueueDao
) {
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _lastProcessedCount = MutableStateFlow(0)
    val lastProcessedCount: StateFlow<Int> = _lastProcessedCount.asStateFlow()

    /**
     * Processes pending mutation items in the local Room sync queue.
     *
     * In Phase 1 local offline mode:
     * - Drains pending batch items from `SyncQueueDao.getPendingSyncBatches()`.
     * - Marks items as locally acknowledged (`SYNCED`) in Room SQLite.
     * - Increments retry counter if local DAO update encounters an error.
     * - Prunes acknowledged items from the local queue and returns total processed count.
     *
     * This does NOT represent remote cloud synchronization.
     */
    suspend fun processPendingBatch(): Int {
        if (_isProcessing.value) return 0
        _isProcessing.value = true
        var processedCount = 0

        try {
            val pendingItems = syncQueueDao.getPendingSyncBatches()
            for (item in pendingItems) {
                try {
                    // Local queue state transition: mark item as locally acknowledged (SYNCED)
                    syncQueueDao.markSynced(item.id)
                    processedCount++
                } catch (e: Exception) {
                    // Increment retry counter on local processing error
                    syncQueueDao.incrementRetry(item.id)
                }
            }
            if (processedCount > 0) {
                // Prune locally acknowledged items from queue
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
