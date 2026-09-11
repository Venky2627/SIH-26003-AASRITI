package com.sih26003.aasriti.data.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.sih26003.aasriti.data.local.entities.SyncQueueEntity
import com.sih26003.aasriti.demo.DemoPatientConfig
import kotlinx.coroutines.tasks.await

/**
 * Opportunistic Cloud Firestore Sync Adapter for AASRITI.
 *
 * Responsibilities:
 * - Deserializes pending SyncQueueEntity JSON payloads into Firestore-safe Map representations.
 * - Authenticates anonymously with Firebase Auth if currentUser is null (satisfying firestore.rules).
 * - Writes/deletes records in Cloud Firestore under /patients/{patientId}/...
 * - Awaits remote Firestore acknowledgement before returning success/failure.
 *
 * Supported Tables:
 * 1. "game_sessions"  -> /patients/{patientId}/game_sessions/{sessionId}
 * 2. "patients"       -> /patients/{patientId}
 * 3. "relationships"  -> /patients/{patientId}/relationships/{relId}
 * 4. "reminders"      -> /patients/{patientId}/reminders/{reminderId} (Supports DELETE)
 * 5. "care_logs"      -> /patients/{patientId}/care_logs/{logId}
 */
open class FirestoreSyncAdapter(
    private val firestoreProvider: () -> FirebaseFirestore = { FirebaseFirestore.getInstance() },
    private val authProvider: () -> FirebaseAuth = { FirebaseAuth.getInstance() },
    private val gson: Gson = Gson()
) {

    private val supportedTables = setOf("game_sessions", "patients", "relationships", "reminders", "care_logs")

    open suspend fun uploadSyncQueueItem(item: SyncQueueEntity): Boolean {
        if (!supportedTables.contains(item.tableName)) {
            Log.w("FirestoreSyncAdapter", "Skipping unsupported table: ${item.tableName}")
            return false
        }

        return try {
            val auth = authProvider()
            if (auth.currentUser == null) {
                Log.d("FirestoreSyncAdapter", "No current user found. Signing in anonymously...")
                try {
                    auth.signInAnonymously().await()
                    Log.d("FirestoreSyncAdapter", "Anonymous authentication successful. UID: ${auth.currentUser?.uid}")
                } catch (authEx: Exception) {
                    Log.e("FirestoreSyncAdapter", "Firebase Anonymous Auth failed: ${authEx.javaClass.simpleName} - ${authEx.message}", authEx)
                    return false
                }
            }

            @Suppress("UNCHECKED_CAST")
            val payloadMap: Map<String, Any?> = try {
                gson.fromJson(item.payloadJson, Map::class.java) as Map<String, Any?>
            } catch (gsonEx: Exception) {
                Log.e("FirestoreSyncAdapter", "Failed to parse JSON payload map for item id=${item.id}: ${gsonEx.message}", gsonEx)
                return false
            }

            val patientId: String = (payloadMap["patientId"] as? String)
                ?: (if (item.tableName == "patients") payloadMap["id"] as? String else null)
                ?: DemoPatientConfig.PATIENT_ID

            val recordId = (payloadMap["id"] as? String) ?: item.recordId

            val firestore = firestoreProvider()
            val docRef = when (item.tableName) {
                "patients" -> firestore.collection("patients").document(recordId)
                "relationships" -> firestore.collection("patients").document(patientId).collection("relationships").document(recordId)
                "game_sessions" -> firestore.collection("patients").document(patientId).collection("game_sessions").document(recordId)
                "reminders" -> firestore.collection("patients").document(patientId).collection("reminders").document(recordId)
                "care_logs" -> firestore.collection("patients").document(patientId).collection("care_logs").document(recordId)
                else -> return false
            }

            try {
                if (item.operation == "DELETE") {
                    docRef.delete().await()
                    Log.d("FirestoreSyncAdapter", "Successfully deleted remote document at ${docRef.path}")
                } else {
                    val cleanDataMap = payloadMap.filterValues { it != null }
                    docRef.set(cleanDataMap).await()
                    Log.d("FirestoreSyncAdapter", "Successfully set remote document at ${docRef.path}")
                }
                true
            } catch (fsEx: Exception) {
                Log.e("FirestoreSyncAdapter", "Firestore operation failed for path=${docRef.path}: ${fsEx.javaClass.simpleName} - ${fsEx.message}", fsEx)
                false
            }
        } catch (e: Exception) {
            Log.e("FirestoreSyncAdapter", "Failed to upload sync queue item id=${item.id}: ${e.javaClass.simpleName} - ${e.message}", e)
            false
        }
    }
}
