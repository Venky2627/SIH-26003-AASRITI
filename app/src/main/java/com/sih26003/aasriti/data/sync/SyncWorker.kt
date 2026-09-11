package com.sih26003.aasriti.data.sync

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.sih26003.aasriti.AasritiApplication
import java.util.concurrent.TimeUnit

/**
 * Idempotent WorkManager Worker for background Cloud Firestore synchronization.
 *
 * Constrained to run ONLY when network connection is available. Uses exponential backoff.
 */
class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("SyncWorker", "Executing background sync worker...")
        return try {
            val app = applicationContext as AasritiApplication
            val processed = app.syncManager.processPendingBatch()
            Log.d("SyncWorker", "SyncWorker finished processing $processed pending items.")
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "SyncWorker encountered transient failure: ${e.message}", e)
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "AasritiSyncWorker"

        fun enqueue(context: Context) {
            try {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

                val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                    .setConstraints(constraints)
                    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.SECONDS)
                    .build()

                WorkManager.getInstance(context).enqueueUniqueWork(
                    WORK_NAME,
                    ExistingWorkPolicy.KEEP,
                    syncRequest
                )
                Log.d("SyncWorker", "Enqueued unique background sync work request.")
            } catch (e: Exception) {
                Log.e("SyncWorker", "Failed to enqueue WorkManager sync request: ${e.message}")
            }
        }
    }
}
