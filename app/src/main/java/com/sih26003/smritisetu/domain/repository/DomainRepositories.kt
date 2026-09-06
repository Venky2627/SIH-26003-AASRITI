package com.sih26003.smritisetu.domain.repository

import com.sih26003.smritisetu.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * AASRITI Domain Repository Interfaces.
 * Frozen contract between frontend consumers and Room SQLite / Data layer.
 */

interface DomainPatientRepository {
    fun observePatient(id: String): Flow<Patient?>
    fun observeAllPatients(): Flow<List<Patient>>
    suspend fun getPatient(id: String): Patient?
    suspend fun savePatient(patient: Patient)
}

interface DomainGameRepository {
    suspend fun recordSession(session: GameSession): Result<Unit>
    fun observeRecentSessions(patientId: String, limit: Int = 10): Flow<List<GameSession>>
    suspend fun getLongitudinalSessions(patientId: String, days: Int = 7): List<GameSession>
}

interface DomainReminderRepository {
    fun observeReminders(patientId: String): Flow<List<Reminder>>
    suspend fun toggleReminder(reminderId: String, isCompleted: Boolean)
    suspend fun addReminder(reminder: Reminder)
}

interface DomainCareLogRepository {
    fun observeRecentLogs(patientId: String, limit: Int = 5): Flow<List<CareLog>>
    suspend fun recordLog(log: CareLog): Result<Unit>
}
