package com.sih26003.smritisetu.ml.inference

import android.content.Context
import com.google.gson.Gson
import com.sih26003.smritisetu.ml.model.DecisionTreeNode
import java.io.InputStreamReader

/**
 * Lightweight on-device Decision Tree interpreter in Kotlin.
 * Evaluates game performance signals locally to recommend next difficulty level (1-5).
 * 100% offline, zero cloud inference, zero medical diagnostic claims.
 */
class DecisionTreeEngine(private val context: Context) {

    private var rootNode: DecisionTreeNode? = null

    init {
        loadModel()
    }

    private fun loadModel() {
        try {
            context.assets.open("ml/decision_tree_difficulty.json").use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    rootNode = Gson().fromJson(reader, DecisionTreeNode::class.java)
                }
            }
        } catch (e: Exception) {
            // Fallback rule if asset load encounters an I/O issue
            rootNode = null
        }
    }

    /**
     * Recommends the next game difficulty level (1 to 5) based on the session's performance signals.
     */
    fun recommendDifficulty(
        accuracy: Float,
        errors: Int,
        reactionTimeMs: Long,
        hesitationCount: Int,
        completed: Boolean,
        currentDifficulty: Int
    ): Int {
        val root = rootNode
        if (root == null) {
            // Safe clinical fallback heuristic if tree is unavailable
            return when {
                !completed -> maxOf(1, currentDifficulty - 1)
                accuracy >= 0.85f && reactionTimeMs < 2500 && errors <= 1 -> minOf(5, currentDifficulty + 1)
                accuracy <= 0.60f || errors >= 3 -> maxOf(1, currentDifficulty - 1)
                else -> currentDifficulty
            }
        }

        val features = mapOf(
            "accuracy" to accuracy,
            "errors" to errors.toFloat(),
            "reaction_time_ms" to reactionTimeMs.toFloat(),
            "hesitation_count" to hesitationCount.toFloat(),
            "completion" to if (completed) 1.0f else 0.0f,
            "current_difficulty" to currentDifficulty.toFloat()
        )

        var current: DecisionTreeNode = root
        var depthGuard = 0

        while (!current.isLeaf && depthGuard < 20) {
            depthGuard++
            val featureName = current.feature ?: break
            val threshold = current.threshold ?: break
            val value = features[featureName] ?: 0f

            current = if (value <= threshold) {
                current.left ?: break
            } else {
                current.right ?: break
            }
        }

        val recommended = current.recommendedDifficulty ?: currentDifficulty
        return recommended.coerceIn(1, 5)
    }
}
