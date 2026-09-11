package com.sih26003.aasriti.demo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Singleton reactive state container for the interactive SIH demonstration.
 * Maintains volatile interactive states and allows clean 1-touch resets.
 */
object DemoStateHolder {
    // 1. Language & Accessibility Mode
    var currentLanguage by mutableStateOf("as") // "as" for Assamese, "en" for English
    var largeTextMode by mutableStateOf(false)
    var highContrastMode by mutableStateOf(false)
    var activePatientId by mutableStateOf<String?>(null)
    var activePatientName by mutableStateOf<String?>(null)

    // 2. Daily Routine Checklist state
    val completedRoutineIds = mutableStateListOf<String>()

    fun toggleRoutine(routineId: String) {
        if (completedRoutineIds.contains(routineId)) {
            completedRoutineIds.remove(routineId)
        } else {
            completedRoutineIds.add(routineId)
        }
    }

    // 3. Flower Match Game State
    var flowerGameRound by mutableStateOf(1)
    val matchedFlowerIds = mutableStateListOf<Int>()
    var selectedFlowerId by mutableStateOf<Int?>(null)
    var flowerGameFeedback by mutableStateOf<String?>(null)
    var isFlowerGameComplete by mutableStateOf(false)

    fun onFlowerSelected(cardId: Int, targetMatchId: Int): Boolean {
        if (cardId == targetMatchId) {
            if (!matchedFlowerIds.contains(cardId)) {
                matchedFlowerIds.add(cardId)
            }
            flowerGameFeedback = if (currentLanguage == "as") "বৰ সুন্দৰ! শুদ্ধ উত্তৰ।" else "Wonderful! Correct match."
            if (matchedFlowerIds.size >= 2) {
                isFlowerGameComplete = true
            }
            return true
        } else {
            flowerGameFeedback = if (currentLanguage == "as") "একো কথা নাই, আকৌ এবাৰ চেষ্টা কৰোঁ আহক।" else "Take your time, let's try again gently."
            return false
        }
    }

    fun resetFlowerGame() {
        flowerGameRound = 1
        matchedFlowerIds.clear()
        selectedFlowerId = null
        flowerGameFeedback = null
        isFlowerGameComplete = false
    }

    // 4. Simulated Family Telephony
    var activeSimulatedCall by mutableStateOf<DemoFamilyContact?>(null)
    var callSecondsElapsed by mutableStateOf(0)
    var isCallConnected by mutableStateOf(false)

    fun startSimulatedCall(contact: DemoFamilyContact) {
        activeSimulatedCall = contact
        callSecondsElapsed = 0
        isCallConnected = true
    }

    fun endSimulatedCall() {
        activeSimulatedCall = null
        callSecondsElapsed = 0
        isCallConnected = false
    }

    // 5. Simulated SOS Emergency State
    var isSosActive by mutableStateOf(false)
    var sosAcknowledgedByCaregiver by mutableStateOf(false)

    fun triggerSos() {
        isSosActive = true
        sosAcknowledgedByCaregiver = true
    }

    fun dismissSos() {
        isSosActive = false
        sosAcknowledgedByCaregiver = false
    }

    // 6. Audio Voice Note Playing State
    var activePlayingMemoryId by mutableStateOf<String?>(null)

    fun toggleMemoryAudio(memoryId: String) {
        activePlayingMemoryId = if (activePlayingMemoryId == memoryId) null else memoryId
    }

    // 7. Caregiver Quick Log Submission state
    var lastLoggedIncidentTime by mutableStateOf<String?>(null)
    var lastLoggedIncidentText by mutableStateOf<String?>(null)

    fun recordCaregiverQuickLog(category: String, note: String) {
        lastLoggedIncidentTime = "Just now"
        lastLoggedIncidentText = "$category: $note"
    }

    // 8. Complete Demo State Reset (Restores initial conditions)
    fun resetAll() {
        currentLanguage = "as"
        largeTextMode = false
        highContrastMode = false
        completedRoutineIds.clear()
        resetFlowerGame()
        endSimulatedCall()
        dismissSos()
        activePlayingMemoryId = null
        lastLoggedIncidentTime = null
        lastLoggedIncidentText = null
    }
}
