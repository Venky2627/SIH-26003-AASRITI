package com.sih26003.smritisetu.data.repository

import com.google.gson.Gson
import com.sih26003.smritisetu.data.local.dao.DoctorAccessDao
import com.sih26003.smritisetu.data.local.dao.GameSessionDao
import com.sih26003.smritisetu.data.local.dao.PatientDao
import com.sih26003.smritisetu.data.local.dao.RelationshipDao
import com.sih26003.smritisetu.data.local.dao.ReminderDao
import com.sih26003.smritisetu.data.local.dao.SyncQueueDao
import com.sih26003.smritisetu.data.local.dao.UserDao
import com.sih26003.smritisetu.data.local.entities.DoctorAccessEntity
import com.sih26003.smritisetu.data.local.entities.GameSessionEntity
import com.sih26003.smritisetu.data.local.entities.PatientEntity
import com.sih26003.smritisetu.data.local.entities.RelationshipEntity
import com.sih26003.smritisetu.data.local.entities.ReminderEntity
import com.sih26003.smritisetu.data.local.entities.SyncQueueEntity
import com.sih26003.smritisetu.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    suspend fun hasUser(role: String): Boolean = userDao.hasUserWithRole(role) > 0
    suspend fun authenticatePin(pinHash: String): UserEntity? = userDao.authenticateWithPin(pinHash)
    suspend fun registerUser(user: UserEntity) = userDao.insertUser(user)
}

class PatientRepository(
    private val patientDao: PatientDao,
    private val relationshipDao: RelationshipDao,
    private val syncQueueDao: SyncQueueDao
) {
    val allPatients: Flow<List<PatientEntity>> = patientDao.getAllPatients()

    suspend fun getPatientById(id: String): PatientEntity? = patientDao.getPatientById(id)
    suspend fun getPatientByLinkCode(code: String): PatientEntity? = patientDao.getPatientByLinkCode(code)

    suspend fun savePatient(patient: PatientEntity) {
        patientDao.insertPatient(patient)
        syncQueueDao.enqueue(
            SyncQueueEntity(
                tableName = "patients",
                recordId = patient.id,
                operation = "INSERT",
                payloadJson = Gson().toJson(patient)
            )
        )
    }

    fun getRelationships(patientId: String): Flow<List<RelationshipEntity>> =
        relationshipDao.getRelationshipsForPatient(patientId)

    suspend fun getRelationshipsList(patientId: String): List<RelationshipEntity> =
        relationshipDao.getRelationshipsList(patientId)

    suspend fun addRelationship(rel: RelationshipEntity) {
        relationshipDao.insertRelationship(rel)
        syncQueueDao.enqueue(
            SyncQueueEntity(
                tableName = "relationships",
                recordId = rel.id,
                operation = "INSERT",
                payloadJson = Gson().toJson(rel)
            )
        )
    }
}

class GameRepository(
    private val gameSessionDao: GameSessionDao,
    private val syncQueueDao: SyncQueueDao
) {
    fun getSessionsForPatient(patientId: String): Flow<List<GameSessionEntity>> =
        gameSessionDao.getSessionsForPatient(patientId)

    suspend fun getLatestSession(patientId: String, gameId: String): GameSessionEntity? =
        gameSessionDao.getLatestSession(patientId, gameId)

    suspend fun getAllSessionsList(patientId: String): List<GameSessionEntity> =
        gameSessionDao.getAllSessionsList(patientId)

    suspend fun saveGameSession(session: GameSessionEntity) {
        // 1. Room is KING: Write to local SQLite first
        gameSessionDao.insertSession(session)
        // 2. Enqueue for opportunistic background sync
        syncQueueDao.enqueue(
            SyncQueueEntity(
                tableName = "game_sessions",
                recordId = session.id,
                operation = "INSERT",
                payloadJson = Gson().toJson(session)
            )
        )
    }

    suspend fun getAverageAccuracy(patientId: String): Float =
        gameSessionDao.getAverageAccuracy(patientId) ?: 0.0f
}

class ReminderRepository(
    private val reminderDao: ReminderDao,
    private val syncQueueDao: SyncQueueDao
) {
    fun getActiveReminders(patientId: String): Flow<List<ReminderEntity>> =
        reminderDao.getActiveReminders(patientId)

    suspend fun getAllActiveReminders(): List<ReminderEntity> =
        reminderDao.getAllActiveRemindersList()

    suspend fun saveReminder(reminder: ReminderEntity) {
        reminderDao.insertReminder(reminder)
        syncQueueDao.enqueue(
            SyncQueueEntity(
                tableName = "reminders",
                recordId = reminder.id,
                operation = "INSERT",
                payloadJson = Gson().toJson(reminder)
            )
        )
    }

    suspend fun toggleReminder(reminder: ReminderEntity, isEnabled: Boolean) {
        val updated = reminder.copy(isEnabled = isEnabled)
        reminderDao.updateReminder(updated)
    }

    suspend fun deleteReminder(id: String) {
        reminderDao.deleteReminder(id)
    }
}

class DoctorAccessRepository(
    private val doctorAccessDao: DoctorAccessDao
) {
    suspend fun verifyDoctorAccess(code: String): DoctorAccessEntity? =
        doctorAccessDao.getActiveDoctorAccess(code)

    fun getAccessListForPatient(patientId: String): Flow<List<DoctorAccessEntity>> =
        doctorAccessDao.getAccessForPatient(patientId)

    suspend fun grantAccess(access: DoctorAccessEntity) =
        doctorAccessDao.insertAccess(access)

    suspend fun revokeAccess(id: String) =
        doctorAccessDao.revokeAccess(id)
}
