package com.sih26003.smritisetu.ml.model

import com.google.gson.annotations.SerializedName

/**
 * Model representing a node in the exported Scikit-Learn Decision Tree JSON.
 */
data class DecisionTreeNode(
    @SerializedName("is_leaf")
    val isLeaf: Boolean,
    
    @SerializedName("feature")
    val feature: String? = null, // "accuracy", "errors", "reaction_time_ms", "hesitation_count", "completion", "current_difficulty"
    
    @SerializedName("threshold")
    val threshold: Float? = null,
    
    @SerializedName("recommended_difficulty")
    val recommendedDifficulty: Int? = null, // 1 to 5
    
    @SerializedName("left")
    val left: DecisionTreeNode? = null,
    
    @SerializedName("right")
    val right: DecisionTreeNode? = null
)
