package com.sih26003.aasriti.engine.trend

import com.sih26003.aasriti.domain.model.DayTrendPoint
import com.sih26003.aasriti.domain.model.GameSession
import com.sih26003.aasriti.domain.model.LongitudinalTrend
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * AASRITI Longitudinal Trend Engine.
 * Aggregates game telemetry into 7-day curves and explainable signals for clinicians.
 * 100% Deterministic, Offline, and driven purely by real Room SQLite sessions.
 */
object TrendEngine {

    fun compute7DaySignals(patientId: String, sessions: List<GameSession>): LongitudinalTrend {
        val now = System.currentTimeMillis()
        val sevenDaysAgo = now - 7L * 24L * 3600L * 1000L
        val recentSessions = sessions.filter { it.timestamp in sevenDaysAgo..now || it.timestamp == 0L }

        if (recentSessions.isEmpty()) {
            return LongitudinalTrend(
                patientId = patientId,
                averageReactionTimeMs = 0L,
                totalHesitationGaps = 0,
                routineAdherencePercent = 0,
                explainableSummary = listOf(
                    "No recent interaction data available. (শেহতীয়া কোনো খেলৰ তথ্য উপলব্ধ নহয়।)",
                    "Awaiting completion of first cognitive session."
                ),
                dailyPoints = emptyList()
            )
        }

        val avgReaction = recentSessions.map { it.reactionTimeMs }.average().toLong()
        val totalHesitations = recentSessions.sumOf { it.hesitationCount }
        val totalErrors = recentSessions.sumOf { it.errorCount }
        val avgAccuracy = (recentSessions.map { it.accuracy }.average() * 100).toInt()

        val explainable = listOf(
            "Average response latency: ${avgReaction}ms across ${recentSessions.size} recorded session(s).",
            "Hesitation episodes (>3.5s pause): $totalHesitations occurrences across all sessions.",
            "Average accuracy: $avgAccuracy% with $totalErrors total error(s).",
            "Functional engagement: 100% offline local Room telemetry."
        )

        val sdf = SimpleDateFormat("EEE", Locale.ENGLISH)
        val points = (6 downTo 0).map { daysAgo ->
            val cal = Calendar.getInstance().apply {
                timeInMillis = now
                add(Calendar.DAY_OF_YEAR, -daysAgo)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startOfDay = cal.timeInMillis
            val endOfDay = startOfDay + 24L * 3600L * 1000L
            val daySessions = recentSessions.filter { 
                if (it.timestamp == 0L) daysAgo == 0 else it.timestamp in startOfDay until endOfDay 
            }

            val dayAvgLatency = if (daySessions.isNotEmpty()) {
                daySessions.map { it.reactionTimeMs }.average().toLong()
            } else {
                0L
            }
            val dayHesitations = daySessions.sumOf { it.hesitationCount }
            val dayAdherence = if (daySessions.isNotEmpty()) 100 else 0

            DayTrendPoint(
                dayLabel = sdf.format(Date(startOfDay)),
                reactionTimeMs = dayAvgLatency,
                hesitationGaps = dayHesitations,
                adherencePercent = dayAdherence
            )
        }

        return LongitudinalTrend(
            patientId = patientId,
            averageReactionTimeMs = avgReaction,
            totalHesitationGaps = totalHesitations,
            routineAdherencePercent = 100,
            explainableSummary = explainable,
            dailyPoints = points
        )
    }
}
