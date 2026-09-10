package com.sih26003.aasriti.demo

import com.sih26003.aasriti.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * AASRITI Mock Data & Contract Provider.
 * Allows frontend engineers to develop and preview UI in isolation without database dependency.
 */
object AasritiMockProvider {

    val patient = Patient(
        id = "aita_borah_01",
        pseudonymCode = "AS-KAM-0042",
        displayName = "আইতা বৰা",
        displaySubtitle = "Aita Borah • 68 Years",
        villageLocation = "Kamrup Rural, Assam",
        primaryLanguage = "as",
        cognitiveStage = "Mild Cognitive Impairment (MCI)"
    )

    val reminders = listOf(
        Reminder(
            id = "r1",
            patientId = "aita_borah_01",
            titleIndic = "পুৱাৰ ৰক্তচাপৰ ঔষধ",
            titleEn = "Morning Blood Pressure Medicine",
            timeLabel = "০৮:০০ AM",
            isMedicine = true,
            isCompleted = false
        ),
        Reminder(
            id = "r2",
            patientId = "aita_borah_01",
            titleIndic = "এগিলাচ কুহুমীয়া পানী",
            titleEn = "Warm Glass of Water",
            timeLabel = "১০:৩০ AM",
            isMedicine = false,
            isCompleted = true
        ),
        Reminder(
            id = "r3",
            patientId = "aita_borah_01",
            titleIndic = "ফুলনি বাৰীৰ ফুৰা",
            titleEn = "Gentle Garden Stroll",
            timeLabel = "০৪:৩০ PM",
            isMedicine = false,
            isCompleted = false
        )
    )

    val recentSessions = listOf(
        GameSession(
            id = "s1",
            patientId = "aita_borah_01",
            gameId = "FLOWER_MATCH",
            difficultyLevel = 2,
            accuracy = 1.0f,
            reactionTimeMs = 2650L,
            hesitationCount = 1,
            errorCount = 0,
            durationMs = 18000L
        ),
        GameSession(
            id = "s2",
            patientId = "aita_borah_01",
            gameId = "FAMILY_TRIVIA",
            difficultyLevel = 2,
            accuracy = 0.8f,
            reactionTimeMs = 3100L,
            hesitationCount = 3,
            errorCount = 1,
            durationMs = 24000L
        )
    )

    fun observeMockPatient(): Flow<Patient> = flowOf(patient)
    fun observeMockReminders(): Flow<List<Reminder>> = flowOf(reminders)
    fun observeMockSessions(): Flow<List<GameSession>> = flowOf(recentSessions)
}
