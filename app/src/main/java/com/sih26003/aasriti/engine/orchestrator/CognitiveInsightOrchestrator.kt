package com.sih26003.aasriti.engine.orchestrator

import com.sih26003.aasriti.data.local.entities.GameSessionEntity
import com.sih26003.aasriti.domain.model.CareLog
import com.sih26003.aasriti.domain.model.GameSession
import com.sih26003.aasriti.domain.model.LongitudinalTrend
import com.sih26003.aasriti.domain.model.Patient
import com.sih26003.aasriti.domain.model.Reminder
import com.sih26003.aasriti.domain.model.TodayPriority
import com.sih26003.aasriti.engine.adaptive.AdaptiveEngine
import com.sih26003.aasriti.engine.adaptive.AdaptiveResult
import com.sih26003.aasriti.engine.priority.PriorityEngine
import com.sih26003.aasriti.engine.trend.TrendEngine
import com.sih26003.aasriti.ml.inference.DecisionTreeEngine

/**
 * AASRITI Cognitive Insight Orchestrator.
 * Connects the three core intelligence engines:
 *   1. Adaptive Difficulty (On-device Decision Tree JSON interpreter + AdaptiveEngine)
 *   2. Longitudinal Trend Signals (TrendEngine)
 *   3. Care Triage & Priority Evaluation (PriorityEngine)
 *
 * 100% OFFLINE. Room SQLite is the sole source of truth. Zero cloud dependencies.
 */
class CognitiveInsightOrchestrator(
    private val decisionTreeEngine: DecisionTreeEngine
) {

    /**
     * Evaluates a completed game session to calculate next difficulty level and non-punitive feedback.
     */
    fun processSession(
        session: GameSessionEntity,
        currentDifficulty: Int
    ): AdaptiveResult {
        // 1. Evaluate on-device scikit-learn Decision Tree model
        val recommendedLevel = decisionTreeEngine.recommendDifficulty(
            accuracy = session.accuracy,
            errors = session.errors,
            reactionTimeMs = session.reactionTimeMs,
            hesitationCount = session.hesitationCount,
            completed = session.completed,
            currentDifficulty = currentDifficulty
        )

        // 2. Generate explainable clinical feedback copy
        val heuristic = AdaptiveEngine.evaluateNextRound(
            currentLevel = currentDifficulty,
            accuracy = session.accuracy,
            reactionTimeMs = session.reactionTimeMs,
            hesitationCount = session.hesitationCount
        )

        return AdaptiveResult(
            nextLevel = recommendedLevel,
            feedbackIndic = heuristic.feedbackIndic,
            feedbackEn = heuristic.feedbackEn,
            adjustmentReason = heuristic.adjustmentReason
        )
    }

    /**
     * Computes 7-day longitudinal response latency curves and hesitation trends for the clinician portal.
     */
    fun computeLongitudinalTrends(
        patientId: String,
        sessions: List<GameSessionEntity>
    ): LongitudinalTrend {
        val domainSessions = sessions.map { entity ->
            GameSession(
                id = entity.id,
                patientId = entity.patientId,
                gameId = entity.gameId,
                difficultyLevel = entity.difficultyLevel,
                accuracy = entity.accuracy,
                reactionTimeMs = entity.reactionTimeMs,
                hesitationCount = entity.hesitationCount,
                errorCount = entity.errors,
                durationMs = entity.durationMs,
                timestamp = entity.timestamp
            )
        }
        return TrendEngine.compute7DaySignals(patientId, domainSessions)
    }

    /**
     * Evaluates patient care triage level (NORMAL, WATCH, PRIORITY, URGENT) from routine adherence,
     * recent game hesitation, and logged incidents.
     */
    fun evaluateCarePriority(
        patient: Patient,
        reminders: List<Reminder>,
        sessions: List<GameSessionEntity>,
        logs: List<CareLog>
    ): TodayPriority {
        val domainSessions = sessions.map { entity ->
            GameSession(
                id = entity.id,
                patientId = entity.patientId,
                gameId = entity.gameId,
                difficultyLevel = entity.difficultyLevel,
                accuracy = entity.accuracy,
                reactionTimeMs = entity.reactionTimeMs,
                hesitationCount = entity.hesitationCount,
                errorCount = entity.errors,
                durationMs = entity.durationMs,
                timestamp = entity.timestamp
            )
        }
        return PriorityEngine.evaluateTodayPriority(patient, reminders, domainSessions, logs)
    }
}
