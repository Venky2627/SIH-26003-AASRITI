package com.sih26003.aasriti.domain.model

/**
 * AASRITI Pure Domain Models.
 * Clean Architecture Contract: Zero Android/Room dependencies.
 */

data class Patient(
    val id: String,
    val pseudonymCode: String,
    val displayName: String,
    val displaySubtitle: String,
    val birthYear: Int = 1958,
    val gender: String = "F",
    val villageLocation: String = "Kamrup Rural, Assam",
    val primaryLanguage: String = "as",
    val cognitiveStage: String = "Mild Cognitive Impairment (MCI)"
)

data class GameSession(
    val id: String,
    val patientId: String,
    val gameId: String,
    val difficultyLevel: Int,
    val accuracy: Float,
    val reactionTimeMs: Long,
    val hesitationCount: Int,
    val errorCount: Int,
    val durationMs: Long,
    val timestamp: Long = System.currentTimeMillis()
)

data class Reminder(
    val id: String,
    val patientId: String,
    val titleIndic: String,
    val titleEn: String,
    val timeLabel: String,
    val isMedicine: Boolean = false,
    val isCompleted: Boolean = false
)

data class CareLog(
    val id: String,
    val patientId: String,
    val authorRole: String,
    val category: String,
    val severity: String,
    val notes: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class TodayPriority(
    val patientId: String,
    val titleIndic: String,
    val titleEn: String,
    val explanationIndic: String,
    val explanationEn: String,
    val severity: String,
    val suggestedAction: String,
    val isResolved: Boolean = false
)

data class LongitudinalTrend(
    val patientId: String,
    val averageReactionTimeMs: Long,
    val totalHesitationGaps: Int,
    val routineAdherencePercent: Int,
    val explainableSummary: List<String>,
    val dailyPoints: List<DayTrendPoint>
)

data class DayTrendPoint(
    val dayLabel: String,
    val reactionTimeMs: Long,
    val hesitationGaps: Int,
    val adherencePercent: Int
)
