package com.sih26003.smritisetu.data.sync

import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sih26003.smritisetu.data.local.entities.SyncQueueEntity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

/**
 * Production CloudSyncDispatcher communicating with Firebase Firestore.
 * Strictly adheres to backend/firebase/firestore.rules hierarchy:
 * - /patients/{patientId}
 * - /patients/{patientId}/relationships/{relId}
 * - /patients/{patientId}/game_sessions/{sessionId}
 * - /patients/{patientId}/care_logs/{logId}
 * - /patients/{patientId}/reminders/{reminderId}
 * - /patients/{patientId}/doctor_access/{accessId}
 * - /patients/{patientId}/care_plans/{planId}
 * - /patients/{patientId}/memory_items/{memoryId}
 * - /sync_queue/{queueId}
 *
 * Enforces:
 * 1. Duplicate protection via idempotent set(..., SetOptions.merge()) keyed on Room record UUID.
 * 2. Honest completion reporting: returns Success ONLY upon verified Firestore Task completion.
 * 3. Safe unconfigured fallback: returns RetryableError if Firebase credentials are unconfigured,
 *    guaranteeing that local Room SQLite queue is NEVER falsely drained.
 */
class FirestoreCloudSyncDispatcher(
    private val firestoreInstanceProvider: (() -> FirebaseFirestore?)? = null
) : CloudSyncDispatcher {

    private fun getFirestore(): FirebaseFirestore? {
        if (firestoreInstanceProvider != null) {
            return firestoreInstanceProvider.invoke()
        }
        return try {
            if (FirebaseApp.getApps(FirebaseApp.getInstance().applicationContext).isNotEmpty()) {
                FirebaseFirestore.getInstance()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun dispatchMutation(item: SyncQueueEntity): SyncDispatchResult {
        val firestore = getFirestore() ?: return SyncDispatchResult.RetryableError(
            "Firebase Firestore is not initialized or credentials unconfigured on this device."
        )

        val mapType = object : TypeToken<Map<String, Any?>>() {}.type
        val payload: Map<String, Any?> = try {
            Gson().fromJson(item.payloadJson, mapType) ?: emptyMap()
        } catch (e: Exception) {
            return SyncDispatchResult.FatalError("Malformed JSON payload in SyncQueue record ${item.id}", e)
        }

        val patientId = (payload["patientId"] as? String) ?: item.recordId

        // Resolve Firestore document reference matching firestore.rules
        val docRef = when (item.tableName) {
            "patients" -> firestore.collection("patients").document(item.recordId)
            "relationships" -> firestore.collection("patients").document(patientId)
                .collection("relationships").document(item.recordId)
            "game_sessions" -> firestore.collection("patients").document(patientId)
                .collection("game_sessions").document(item.recordId)
            "care_logs" -> firestore.collection("patients").document(patientId)
                .collection("care_logs").document(item.recordId)
            "reminders" -> firestore.collection("patients").document(patientId)
                .collection("reminders").document(item.recordId)
            "doctor_access" -> firestore.collection("patients").document(patientId)
                .collection("doctor_access").document(item.recordId)
            "care_plans" -> firestore.collection("patients").document(patientId)
                .collection("care_plans").document(item.recordId)
            "memory_items" -> firestore.collection("patients").document(patientId)
                .collection("memory_items").document(item.recordId)
            else -> firestore.collection("sync_queue").document(item.recordId)
        }

        return try {
            when (item.operation.uppercase()) {
                "INSERT", "UPDATE" -> {
                    // Idempotent upsert with SetOptions.merge() ensures duplicate protection
                    docRef.set(payload, SetOptions.merge()).awaitTask()
                    SyncDispatchResult.Success
                }
                "DELETE" -> {
                    docRef.delete().awaitTask()
                    SyncDispatchResult.Success
                }
                else -> {
                    SyncDispatchResult.FatalError("Unsupported sync operation: ${item.operation}")
                }
            }
        } catch (e: Exception) {
            SyncDispatchResult.RetryableError("Firestore dispatch failed for ${item.tableName}/${item.recordId}: ${e.message}", e)
        }
    }

    private suspend fun <T> Task<T>.awaitTask(): T = suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            continuation.resume(result) {}
        }
        addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
    }
}
