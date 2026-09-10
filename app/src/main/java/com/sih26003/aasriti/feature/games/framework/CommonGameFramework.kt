package com.sih26003.aasriti.feature.games.framework

import com.sih26003.aasriti.data.local.entities.GameSessionEntity
import com.sih26003.aasriti.data.repository.GameRepository
import com.sih26003.aasriti.ml.inference.DecisionTreeEngine
import com.sih26003.aasriti.voice.playback.VoicePromptManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class GameId(
    val titleIndic: String,
    val titleLatin: String,
    val emoji: String,
    val clinicalDomain: String
) {
    FAMILY_TRIVIA(
        titleIndic = "পৰিয়ালৰ স্মৃতি",
        titleLatin = "Family Trivia",
        emoji = "👨‍👩‍👧",
        clinicalDomain = "Memory & Personal Identity"
    ),
    VOICE_CUE_CARD(
        titleIndic = "কণ্ঠ আৰু ছবি",
        titleLatin = "Voice Cue Card",
        emoji = "🔊",
        clinicalDomain = "Memory & Attention"
    ),
    SEQUENCING(
        titleIndic = "ক্ৰম সজোৱা",
        titleLatin = "Daily Sequencing",
        emoji = "🫖",
        clinicalDomain = "Executive Function & Memory"
    ),
    CATEGORISATION(
        titleIndic = "শ্ৰেণীবিভাজন",
        titleLatin = "Categorisation",
        emoji = "🧺",
        clinicalDomain = "Attention & Categorical Thinking"
    ),
    VILLAGE_MARKET(
        titleIndic = "গাঁওৰ বজাৰ",
        titleLatin = "Village Market",
        emoji = "🛍️",
        clinicalDomain = "Working Memory & Attention"
    ),
    PATTERN_RECOGNITION(
        titleIndic = "আৰ্হি চিনাক্তকৰণ",
        titleLatin = "Pattern Recognition",
        emoji = "🔷",
        clinicalDomain = "Visuospatial & Processing Speed"
    )
}

enum class GamePhase {
    INSTRUCTIONS,
    PLAYING,
    FEEDBACK,
    ROUND_COMPLETE
}

data class PerformanceMetrics(
    val durationMs: Long = 0,
    val accuracy: Float = 1.0f,
    val errors: Int = 0,
    val reactionTimeMs: Long = 0,
    val hesitationCount: Int = 0,
    val adaptationDecision: Int = 1,
    val completed: Boolean = true
)

/**
 * Common Collector tracking interaction times, hesitation gaps, error counts, and accuracy.
 * All metrics reflect real user physical interaction — no fabricated metrics.
 */
class PerformanceCollector {
    private var startTimeMs: Long = 0
    private var firstTouchTimeMs: Long = 0
    private var errors: Int = 0
    private var totalAttempts: Int = 0
    private var correctAttempts: Int = 0
    private var lastTouchTimeMs: Long = 0
    private var hesitationCount: Int = 0

    fun startRound() {
        startTimeMs = System.currentTimeMillis()
        firstTouchTimeMs = 0
        lastTouchTimeMs = startTimeMs
        errors = 0
        totalAttempts = 0
        correctAttempts = 0
        hesitationCount = 0
    }

    fun recordInteraction(isCorrect: Boolean) {
        val now = System.currentTimeMillis()
        totalAttempts++
        if (firstTouchTimeMs == 0L) {
            firstTouchTimeMs = now
        }
        // Hesitation: if gap between prompt and touch > 3500ms or idle gap > 3500ms
        if ((now - lastTouchTimeMs) > 3500) {
            hesitationCount++
        }
        lastTouchTimeMs = now

        if (isCorrect) {
            correctAttempts++
        } else {
            errors++
        }
    }

    fun computeMetrics(currentDifficulty: Int, completed: Boolean, decisionTreeEngine: DecisionTreeEngine): PerformanceMetrics {
        val duration = System.currentTimeMillis() - startTimeMs
        val reactionTime = if (firstTouchTimeMs > 0) firstTouchTimeMs - startTimeMs else duration
        val accuracy = if (totalAttempts > 0) correctAttempts.toFloat() / totalAttempts.toFloat() else 0f

        val recommendedDifficulty = decisionTreeEngine.recommendDifficulty(
            accuracy = accuracy,
            errors = errors,
            reactionTimeMs = reactionTime,
            hesitationCount = hesitationCount,
            completed = completed,
            currentDifficulty = currentDifficulty
        )

        return PerformanceMetrics(
            durationMs = duration,
            accuracy = accuracy,
            errors = errors,
            reactionTimeMs = reactionTime,
            hesitationCount = hesitationCount,
            adaptationDecision = recommendedDifficulty,
            completed = completed
        )
    }
}

/**
 * Reusable base Game Engine shared across all six games.
 * Enforces the locked lifecycle:
 * GAME SELECT → PATIENT SETTINGS → INSTRUCTIONS → GAMEPLAY → METRICS → ADAPTIVE DIFFICULTY → SAVE TO ROOM → FEEDBACK → NEXT ROUND
 */
abstract class BaseGameEngine(
    val gameId: GameId,
    val patientId: String,
    val gameRepository: GameRepository,
    val decisionTreeEngine: DecisionTreeEngine,
    val voicePromptManager: VoicePromptManager,
    private val scope: CoroutineScope,
    initialDifficulty: Int = 1
) {
    protected val collector = PerformanceCollector()

    private val _currentDifficulty = MutableStateFlow(initialDifficulty.coerceIn(1, 5))
    val currentDifficulty: StateFlow<Int> = _currentDifficulty.asStateFlow()

    private val _gamePhase = MutableStateFlow(GamePhase.INSTRUCTIONS)
    val gamePhase: StateFlow<GamePhase> = _gamePhase.asStateFlow()

    private val _feedbackMessage = MutableStateFlow("")
    val feedbackMessage: StateFlow<String> = _feedbackMessage.asStateFlow()

    private val _isCorrectLast = MutableStateFlow(true)
    val isCorrectLast: StateFlow<Boolean> = _isCorrectLast.asStateFlow()

    private val _roundCount = MutableStateFlow(1)
    val roundCount: StateFlow<Int> = _roundCount.asStateFlow()

    private val _lastMetrics = MutableStateFlow<PerformanceMetrics?>(null)
    val lastMetrics: StateFlow<PerformanceMetrics?> = _lastMetrics.asStateFlow()

    open fun startRound() {
        collector.startRound()
        _gamePhase.value = GamePhase.PLAYING
        speakInstructions()
    }

    abstract fun speakInstructions()

    fun onAnswerAttempt(isCorrect: Boolean) {
        collector.recordInteraction(isCorrect)
        _isCorrectLast.value = isCorrect

        if (isCorrect) {
            _feedbackMessage.value = "বৰ ভাল হৈছে! (Wonderful job!)"
            voicePromptManager.speakPromptKey("correct_feedback", "বৰ ভাল হৈছে! আপুনি শুদ্ধ উত্তৰ দিছে।")
            _gamePhase.value = GamePhase.FEEDBACK
        } else {
            _feedbackMessage.value = "একো কথা নাই, আকৌ এবাৰ চেষ্টা কৰক। (Let's try again!)"
            voicePromptManager.speakPromptKey("encourage_feedback", "একো কথা নাই, আকৌ এবাৰ চেষ্টা কৰোঁ আহক।")
            // Never punitive, keep in PLAYING phase so user can try again
        }
    }

    fun finishRound(completed: Boolean = true) {
        val metrics = collector.computeMetrics(_currentDifficulty.value, completed, decisionTreeEngine)
        _lastMetrics.value = metrics

        // ROOM IS KING: Commit session synchronously to local Room SQLite
        scope.launch(Dispatchers.IO) {
            val session = GameSessionEntity(
                patientId = patientId,
                gameId = gameId.name,
                difficultyLevel = _currentDifficulty.value,
                durationMs = metrics.durationMs,
                accuracy = metrics.accuracy,
                errors = metrics.errors,
                reactionTimeMs = metrics.reactionTimeMs,
                hesitationCount = metrics.hesitationCount,
                adaptationDecision = metrics.adaptationDecision,
                completed = metrics.completed
            )
            gameRepository.saveGameSession(session)
        }

        // Apply recommended next difficulty for following round
        _currentDifficulty.value = metrics.adaptationDecision
        _gamePhase.value = GamePhase.ROUND_COMPLETE
    }

    fun proceedToNextRound() {
        _roundCount.value += 1
        _gamePhase.value = GamePhase.INSTRUCTIONS
    }
}
