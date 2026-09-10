package com.sih26003.aasriti.engine.adaptive

/**
 * Adaptive Difficulty Result.
 */
data class AdaptiveResult(
    val nextLevel: Int,
    val feedbackIndic: String,
    val feedbackEn: String,
    val adjustmentReason: String
)

/**
 * AASRITI Adaptive Difficulty Engine.
 * Evaluates game interaction telemetry to scale difficulty levels (1–5) non-punitively.
 */
object AdaptiveEngine {

    fun evaluateNextRound(
        currentLevel: Int,
        accuracy: Float,
        reactionTimeMs: Long,
        hesitationCount: Int
    ): AdaptiveResult {
        return when {
            // High hesitation (>2 pauses >3.5s) or low accuracy -> Step down gently
            hesitationCount >= 2 || accuracy < 0.6f -> {
                val next = (currentLevel - 1).coerceAtLeast(1)
                AdaptiveResult(
                    nextLevel = next,
                    feedbackIndic = "একো কথা নাই, শান্তভাৱে আকৌ খেলোঁ আহক।",
                    feedbackEn = "Take your time, let's explore gently together.",
                    adjustmentReason = "HESITATION_EASED"
                )
            }
            // Fast & 100% accurate -> Step up to next level
            accuracy >= 0.99f && reactionTimeMs < 2400L -> {
                val next = (currentLevel + 1).coerceAtMost(5)
                AdaptiveResult(
                    nextLevel = next,
                    feedbackIndic = "বৰ সুন্দৰ! আপুনি বহুত ভাল খেলিছে।",
                    feedbackEn = "Wonderful! Excellent recognition.",
                    adjustmentReason = "HIGH_ACCURACY_STEP_UP"
                )
            }
            // Stable performance -> Maintain level
            else -> {
                AdaptiveResult(
                    nextLevel = currentLevel,
                    feedbackIndic = "বৰ ধুনীয়া! আগবাঢ়ি যাওক।",
                    feedbackEn = "Lovely! Let's continue.",
                    adjustmentReason = "STABLE"
                )
            }
        }
    }
}
