package com.sih26003.smritisetu.data.sync

import com.sih26003.smritisetu.data.local.entities.SyncQueueEntity

/**
 * Result of dispatching a single mutation to the cloud backend.
 */
sealed class SyncDispatchResult {
    data object Success : SyncDispatchResult()
    data class RetryableError(val reason: String, val throwable: Throwable? = null) : SyncDispatchResult()
    data class FatalError(val reason: String, val throwable: Throwable? = null) : SyncDispatchResult()
}

/**
 * Contract for dispatching Room offline mutations to cloud storage (e.g. Firebase Firestore).
 */
interface CloudSyncDispatcher {
    suspend fun dispatchMutation(item: SyncQueueEntity): SyncDispatchResult
}
