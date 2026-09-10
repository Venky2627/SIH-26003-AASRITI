package com.sih26003.smritisetu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sih26003.smritisetu.data.local.entities.CareLogEntity
import com.sih26003.smritisetu.data.local.entities.CarePlanEntity
import com.sih26003.smritisetu.data.local.entities.DoctorAccessEntity
import com.sih26003.smritisetu.data.local.entities.GameSessionEntity
import com.sih26003.smritisetu.data.local.entities.MemoryItemEntity
import com.sih26003.smritisetu.data.local.entities.PatientEntity
import com.sih26003.smritisetu.data.local.entities.RelationshipEntity
import com.sih26003.smritisetu.data.local.entities.ReminderEntity
import com.sih26003.smritisetu.data.local.entities.SyncQueueEntity
import com.sih26003.smritisetu.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE role = :role LIMIT 1")
    suspend fun getUserByRole(role: String): UserEntity?

    @Query("SELECT * FROM users WHERE pinHash = :pinHash LIMIT 1")
    suspend fun authenticateWithPin(pinHash: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT COUNT(*) FROM users WHERE role = :role")
    suspend fun hasUserWithRole(role: String): Int
}

@Dao
interface PatientDao {
    @Query("SELECT * FROM patients ORDER BY createdAt DESC")
    fun getAllPatients(): Flow<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE id = :id LIMIT 1")
    suspend fun getPatientById(id: String): PatientEntity?

    @Query("SELECT * FROM patients WHERE linkCode = :code LIMIT 1")
    suspend fun getPatientByLinkCode(code: String): PatientEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: PatientEntity)

    @Update
    suspend fun updatePatient(patient: PatientEntity)

    @Query("UPDATE patients SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("DELETE FROM patients WHERE id = :id")
    suspend fun deletePatient(id: String)
}

@Dao
interface RelationshipDao {
    @Query("SELECT * FROM relationships WHERE patientId = :patientId")
    fun getRelationshipsForPatient(patientId: String): Flow<List<RelationshipEntity>>

    @Query("SELECT * FROM relationships WHERE patientId = :patientId")
    suspend fun getRelationshipsList(patientId: String): List<RelationshipEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelationship(relationship: RelationshipEntity)

    @Query("UPDATE relationships SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("DELETE FROM relationships WHERE id = :id")
    suspend fun deleteRelationship(id: String)
}

@Dao
interface GameSessionDao {
    @Query("SELECT * FROM game_sessions WHERE patientId = :patientId ORDER BY timestamp DESC")
    fun getSessionsForPatient(patientId: String): Flow<List<GameSessionEntity>>

    @Query("SELECT * FROM game_sessions WHERE patientId = :patientId AND gameId = :gameId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestSession(patientId: String, gameId: String): GameSessionEntity?

    @Query("SELECT * FROM game_sessions WHERE patientId = :patientId ORDER BY timestamp DESC")
    suspend fun getAllSessionsList(patientId: String): List<GameSessionEntity>

    @Query("SELECT * FROM game_sessions WHERE patientId = :patientId ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSessions(patientId: String, limit: Int = 10): Flow<List<GameSessionEntity>>

    @Query("SELECT COUNT(*) FROM game_sessions WHERE patientId = :patientId")
    suspend fun getTotalSessionsCount(patientId: String): Int

    @Query("SELECT * FROM game_sessions WHERE patientId = :patientId AND timestamp >= :sinceTimestamp ORDER BY timestamp DESC")
    suspend fun getSessionsSince(patientId: String, sinceTimestamp: Long): List<GameSessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: GameSessionEntity)

    @Query("UPDATE game_sessions SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("SELECT AVG(accuracy) FROM game_sessions WHERE patientId = :patientId")
    suspend fun getAverageAccuracy(patientId: String): Float?
}

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE patientId = :patientId AND isEnabled = 1")
    fun getActiveReminders(patientId: String): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE isEnabled = 1")
    suspend fun getAllActiveRemindersList(): List<ReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity)

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Query("UPDATE reminders SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminder(id: String)
}

@Dao
interface DoctorAccessDao {
    @Query("SELECT * FROM doctor_access WHERE doctorAccessCode = :code AND isRevoked = 0 LIMIT 1")
    suspend fun getActiveDoctorAccess(code: String): DoctorAccessEntity?

    @Query("SELECT * FROM doctor_access WHERE patientId = :patientId")
    fun getAccessForPatient(patientId: String): Flow<List<DoctorAccessEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccess(access: DoctorAccessEntity)

    @Query("UPDATE doctor_access SET isRevoked = 1 WHERE id = :id")
    suspend fun revokeAccess(id: String)
}

@Dao
interface SyncQueueDao {
    @Query("SELECT * FROM sync_queue WHERE status = 'PENDING' ORDER BY timestamp ASC LIMIT 25")
    suspend fun getPendingSyncBatches(): List<SyncQueueEntity>

    @Query("SELECT COUNT(*) FROM sync_queue WHERE status = 'PENDING'")
    suspend fun getPendingCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueue(syncItem: SyncQueueEntity)

    @Query("UPDATE sync_queue SET status = 'SYNCED' WHERE id = :id")
    suspend fun markSynced(id: Long)

    @Query("UPDATE sync_queue SET retryCount = retryCount + 1, status = 'PENDING' WHERE id = :id")
    suspend fun incrementRetry(id: Long)

    @Query("DELETE FROM sync_queue WHERE status = 'SYNCED'")
    suspend fun clearCompleted()
}

@Dao
interface CareLogDao {
    @Query("SELECT * FROM care_logs WHERE patientId = :patientId ORDER BY timestamp DESC")
    fun getLogsForPatient(patientId: String): Flow<List<CareLogEntity>>

    @Query("SELECT * FROM care_logs WHERE patientId = :patientId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentLogsList(patientId: String, limit: Int = 10): List<CareLogEntity>

    @Query("SELECT * FROM care_logs WHERE patientId = :patientId AND timestamp >= :sinceTimestamp ORDER BY timestamp DESC")
    suspend fun getLogsSince(patientId: String, sinceTimestamp: Long): List<CareLogEntity>

    @Query("SELECT * FROM care_logs WHERE patientId = :patientId AND category = :category ORDER BY timestamp DESC")
    suspend fun getLogsByCategory(patientId: String, category: String): List<CareLogEntity>

    @Query("SELECT COUNT(*) FROM care_logs WHERE patientId = :patientId AND (severity = 'PRIORITY' OR severity = 'URGENT')")
    suspend fun getUrgentLogsCount(patientId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: CareLogEntity)

    @Query("UPDATE care_logs SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("DELETE FROM care_logs WHERE id = :id")
    suspend fun deleteLog(id: String)
}

@Dao
interface CarePlanDao {
    @Query("SELECT * FROM care_plans WHERE patientId = :patientId ORDER BY updatedAt DESC LIMIT 1")
    fun getLatestCarePlanFlow(patientId: String): Flow<CarePlanEntity?>

    @Query("SELECT * FROM care_plans WHERE patientId = :patientId ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getLatestCarePlan(patientId: String): CarePlanEntity?

    @Query("SELECT * FROM care_plans WHERE patientId = :patientId ORDER BY updatedAt DESC")
    fun getAllCarePlansForPatient(patientId: String): Flow<List<CarePlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCarePlan(carePlan: CarePlanEntity)

    @Query("UPDATE care_plans SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("DELETE FROM care_plans WHERE id = :id")
    suspend fun deleteCarePlan(id: String)
}

@Dao
interface MemoryItemDao {
    @Query("SELECT * FROM memory_items WHERE patientId = :patientId ORDER BY createdAt DESC")
    fun getMemoriesForPatient(patientId: String): Flow<List<MemoryItemEntity>>

    @Query("SELECT * FROM memory_items WHERE patientId = :patientId ORDER BY createdAt DESC")
    suspend fun getMemoriesList(patientId: String): List<MemoryItemEntity>

    @Query("SELECT * FROM memory_items WHERE id = :id LIMIT 1")
    suspend fun getMemoryById(id: String): MemoryItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryItemEntity)

    @Query("UPDATE memory_items SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("DELETE FROM memory_items WHERE id = :id")
    suspend fun deleteMemory(id: String)
}
