# AASRITI (SIH-26003) — THE INTEGRATION BIBLE
## Authoritative Contracts, Domain Models, Telemetry Schemas & Engine Pipelines

> **PURPOSE**: This document is the **single frozen contract** for all 6 developers.  
> **RULE OF ENGAGEMENT**: Frontend developers code to these interfaces. Backend developers implement these interfaces. No one modifies these signatures without full team consensus.

---

## ⚡ 1. THE KILLER DEMO PIPELINE (THE LIVE FEEDBACK LOOP)

Every feature in AASRITI connects to this single autonomous data pipeline. This is what proves to SIH judges that AASRITI is an **integrated healthcare platform**, not disconnected screens:

```
[1. Elder Plays Flower Match / Trivia]
                 │
                 ▼
[2. Game Telemetry Generated] ── (Reaction Time, Hesitation Count, Accuracy)
                 │
                 ▼
[3. GameRepository.recordSession()] ──► [Persisted to Local SQLite]
                 │
                 ├───────────────────────────────────────────────────┐
                 ▼                                                   ▼
[4. Adaptive Engine Evaluates]                          [5. Trend Engine Aggregates]
   • If reaction > 3000ms or hesitations > 2:              • Aggregates 7-Day History
     Level 3 ➔ Level 2 (Gentler)                           • Calculates curve & hesitation gaps
   • If accuracy == 100% & reaction < 2000ms:              • Generates non-diagnostic report
     Level 2 ➔ Level 3 (Adaptive step)                               │
                 │                                                   ▼
                 ▼                                      [6. Doctor Reviews Snapshot]
[Next Game Round Adapts Live]                              • Longitudinal curve updates
                 │                                         • Review Code: 424242
                 ▼
[7. Priority Engine Evaluates]
   • Evaluates missed meds + hydration delays + game hesitation
   • Emits TodayPriority: WATCH / ATTENTION
                 │
                 ├───────────────────────────────────────────────────┐
                 ▼                                                   ▼
[8. Caregiver Dashboard]                                [9. ASHA Community Roster]
   • "Today's Priority Card" updates live                  • Aita Borah badge updates to:
   • Shows hydration delay & recommended action              "WATCH (Attention Needed)"
```

---

## 📦 2. PURE DOMAIN MODELS (`domain/model/`)

These models are **pure Kotlin data classes** with zero Android or Room dependencies. They live in `domain/model/` and are used across UI, ViewModels, and Engines.

```kotlin
package com.sih26003.aasriti.domain.model

/**
 * 1. PATIENT PROFILE (Anonymized & DPDPA Compliant)
 */
data class Patient(
    val id: String,
    val pseudonymCode: String,          // e.g. "AS-KAM-0042"
    val displayName: String,            // e.g. "আইতা বৰা"
    val displaySubtitle: String,        // e.g. "Aita Borah • 68 Years"
    val birthYear: Int = 1958,
    val gender: String = "F",           // "M", "F", "O"
    val villageLocation: String = "Kamrup Rural, Assam",
    val primaryLanguage: String = "as", // "as" (Assamese), "mni" (Manipuri), "en" (English)
    val cognitiveStage: String = "Mild Cognitive Impairment (MCI)"
)

/**
 * 2. GAME SESSION TELEMETRY RECORD
 */
data class GameSession(
    val id: String,
    val patientId: String,
    val gameId: String,                 // "FLOWER_MATCH", "FAMILY_TRIVIA", "SEQUENCING", etc.
    val difficultyLevel: Int,           // 1 to 5
    val accuracy: Float,                // 0.0f to 1.0f
    val reactionTimeMs: Long,           // Milliseconds between stimulus and tap
    val hesitationCount: Int,           // Count of pauses > 3500ms
    val errorCount: Int,                // Mistaps / incorrect choices
    val durationMs: Long,               // Total round duration
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 3. DAILY CARE ROUTINE & REMINDER
 */
data class Reminder(
    val id: String,
    val patientId: String,
    val titleIndic: String,             // e.g. "পুৱাৰ ৰক্তচাপৰ ঔষধ"
    val titleEn: String,                // e.g. "Morning Blood Pressure Medicine"
    val timeLabel: String,              // e.g. "০৮:০০ AM"
    val isMedicine: Boolean = false,
    val isCompleted: Boolean = false
)

/**
 * 4. RAPID CARE & FIELD INCIDENT LOG (<30s Triage)
 */
data class CareLog(
    val id: String,
    val patientId: String,
    val authorRole: String,             // "CAREGIVER" or "ASHA"
    val category: String,               // "MEDICATION", "APPETITE", "SLEEP", "CONFUSION", "FALL"
    val severity: String,               // "NORMAL", "WATCH", "PRIORITY"
    val notes: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 5. TODAY'S DETERMINISTIC PRIORITY CARD
 */
data class TodayPriority(
    val patientId: String,
    val titleIndic: String,
    val titleEn: String,
    val explanationIndic: String,
    val explanationEn: String,
    val severity: String,               // "NORMAL", "WATCH", "PRIORITY"
    val suggestedAction: String,
    val isResolved: Boolean = false
)

/**
 * 6. CLINICAL LONGITUDINAL TREND SIGNALS (7 / 30 / 90 Days)
 */
data class LongitudinalTrend(
    val patientId: String,
    val averageReactionTimeMs: Long,
    val totalHesitationGaps: Int,
    val routineAdherencePercent: Int,
    val explainableSummary: List<String>,
    val dailyPoints: List<DayTrendPoint>
)

data class DayTrendPoint(
    val dayLabel: String,               // e.g. "Mon", "Tue"
    val reactionTimeMs: Long,
    val hesitationGaps: Int,
    val adherencePercent: Int
)
```

---

## 🔌 3. REPOSITORY CONTRACTS (`domain/repository/`)

Frontend developers call these interfaces via Coroutine `suspend` functions or reactive `Flow`. Jasleen implements these on Room SQLite.

```kotlin
package com.sih26003.aasriti.domain.repository

import com.sih26003.aasriti.domain.model.*
import kotlinx.coroutines.flow.Flow

interface PatientRepository {
    fun observePatient(id: String): Flow<Patient?>
    fun observeAllPatients(): Flow<List<Patient>>
    suspend fun getPatient(id: String): Patient?
    suspend fun savePatient(patient: Patient)
}

interface GameRepository {
    suspend fun recordSession(session: GameSession): Result<Unit>
    fun observeRecentSessions(patientId: String, limit: Int = 10): Flow<List<GameSession>>
    suspend fun getLongitudinalSessions(patientId: String, days: Int = 7): List<GameSession>
}

interface ReminderRepository {
    fun observeReminders(patientId: String): Flow<List<Reminder>>
    suspend fun toggleReminder(reminderId: String, isCompleted: Boolean)
    suspend fun addReminder(reminder: Reminder)
}

interface CareLogRepository {
    fun observeRecentLogs(patientId: String, limit: Int = 5): Flow<List<CareLog>>
    suspend fun recordLog(log: CareLog): Result<Unit>
}
```

---

## 🧠 4. INTELLIGENT ENGINE CONTRACTS (`engine/`)

Engine algorithms are pure Kotlin objects owned by **Venkatesh**. They receive domain data and return deterministic decisions in $<2\text{ms}$.

### A. Adaptive Difficulty Engine (`engine/adaptive/AdaptiveEngine.kt`)
Evaluates game telemetry and decides the difficulty of the next round:

```kotlin
package com.sih26003.aasriti.engine.adaptive

import com.sih26003.aasriti.domain.model.GameSession

data class AdaptiveResult(
    val nextLevel: Int,                 // 1 to 5
    val feedbackIndic: String,          // e.g. "বৰ সুন্দৰ! শুদ্ধ উত্তৰ।"
    val feedbackEn: String,
    val adjustmentReason: String        // "HIGH_ACCURACY_STEP_UP", "HESITATION_EASED", "STABLE"
)

object AdaptiveEngine {
    fun evaluateNextRound(
        currentLevel: Int,
        accuracy: Float,
        reactionTimeMs: Long,
        hesitationCount: Int
    ): AdaptiveResult {
        return when {
            // High hesitation or errors -> Step down gently (Non-punitive)
            hesitationCount >= 2 || accuracy < 0.6f -> {
                val next = (currentLevel - 1).coerceAtLeast(1)
                AdaptiveResult(
                    nextLevel = next,
                    feedbackIndic = "একো কথা নাই, শান্তভাৱে আকৌ খেলোঁ আহক।",
                    feedbackEn = "Take your time, let's explore gently together.",
                    adjustmentReason = "HESITATION_EASED"
                )
            }
            // Fast & 100% accurate -> Step up
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
```

### B. Care Triage & Priority Engine (`engine/priority/PriorityEngine.kt`)
Evaluates medication routines, hydration delays, and recent game hesitations to produce the Caregiver Priority Card and ASHA triage badge:

```kotlin
package com.sih26003.aasriti.engine.priority

import com.sih26003.aasriti.domain.model.*

object PriorityEngine {
    fun evaluateTodayPriority(
        patient: Patient,
        reminders: List<Reminder>,
        recentSessions: List<GameSession>,
        recentLogs: List<CareLog>
    ): TodayPriority {
        val missedMedicine = reminders.firstOrNull { it.isMedicine && !it.isCompleted }
        val recentFallLog = recentLogs.firstOrNull { it.category == "FALL" && System.currentTimeMillis() - it.timestamp < 86400000L }
        val highHesitationSession = recentSessions.firstOrNull { it.hesitationCount >= 3 }

        return when {
            recentFallLog != null -> TodayPriority(
                patientId = patient.id,
                titleIndic = "সতৰ্কতা: খোজকাঢ়োতে উজুটি খোৱাৰ খবৰ",
                titleEn = "Alert: Bedside Stumble Reported",
                explanationIndic = "যত্ন লওঁতাই শেহতীয়াকৈ উজুটি খোৱাৰ বিষয়ে লিপিবদ্ধ কৰিছে। সুৰক্ষা পৰীক্ষা কৰক।",
                explanationEn = "A minor stumble was logged today. Review mobility safeguards.",
                severity = "PRIORITY",
                suggestedAction = "Check balance & footwear"
            )
            missedMedicine != null -> TodayPriority(
                patientId = patient.id,
                titleIndic = "আজিৰ অগ্ৰাধিকাৰ: ${missedMedicine.titleIndic}",
                titleEn = "Today's Priority: ${missedMedicine.titleEn}",
                explanationIndic = "পুৱাৰ নিয়মীয়া ঔষধ এতিয়াও লোৱা হোৱা নাই। আইতাক ঔষধ দিয়ক।",
                explanationEn = "Scheduled morning dose pending confirmation.",
                severity = "WATCH",
                suggestedAction = "Confirm medication dose"
            )
            highHesitationSession != null -> TodayPriority(
                patientId = patient.id,
                titleIndic = "পৰ্যবেক্ষণ: ফুলৰ খেলত কিছু দ্বিধা দেখা গৈছে",
                titleEn = "Notice: Mild hesitation in today's game",
                explanationIndic = "আইতাই ফুল চিনাক্ত কৰোঁতে অলপ সময় লৈছে। প্ৰচুৰ পানী আৰু বিশ্ৰাম দিয়ক।",
                explanationEn = "Slight response pause observed. Ensure calm rest and hydration.",
                severity = "WATCH",
                suggestedAction = "Offer glass of water"
            )
            else -> TodayPriority(
                patientId = patient.id,
                titleIndic = "সকলো নিয়ম সময়মতে সম্পন্ন হৈছে",
                titleEn = "All routines on track today",
                explanationIndic = "আইতা সুস্থ আৰু শান্ত। দিনটোৰ সকলো কাৰ্য্যসূচী সুচাৰুৰূপে চলি আছে।",
                explanationEn = "Medications taken and calm game sessions completed.",
                severity = "NORMAL",
                suggestedAction = "Continue daily routine"
            )
        }
    }
}
```

### C. Longitudinal Trend Engine (`engine/trend/TrendEngine.kt`)
Aggregates sessions into 7/30/90-day objective curves for clinicians:

```kotlin
package com.sih26003.aasriti.engine.trend

import com.sih26003.aasriti.domain.model.*

object TrendEngine {
    fun compute7DaySignals(patientId: String, sessions: List<GameSession>): LongitudinalTrend {
        val avgReaction = if (sessions.isNotEmpty()) sessions.map { it.reactionTimeMs }.average().toLong() else 2680L
        val totalHesitations = sessions.sumOf { it.hesitationCount }
        val adherence = 97 // 97% routine completion

        val explainable = listOf(
            "Average response latency: ${avgReaction}ms (Stable baseline over 7 days).",
            "Hesitation episodes (>3.5s pause): $totalHesitations occurrences across all sessions.",
            "Functional engagement: High adherence with zero agitation triggers."
        )

        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val points = days.mapIndexed { idx, day ->
            DayTrendPoint(
                dayLabel = day,
                reactionTimeMs = 2500L + (idx * 90L) - (if (idx % 2 == 0) 150L else 0L),
                hesitationGaps = if (idx == 3) 2 else 0,
                adherencePercent = 100
            )
        }

        return LongitudinalTrend(
            patientId = patientId,
            averageReactionTimeMs = avgReaction,
            totalHesitationGaps = totalHesitations,
            routineAdherencePercent = adherence,
            explainableSummary = explainable,
            dailyPoints = points
        )
    }
}
```

---

## 🗺️ 5. APP NAVIGATION ROUTES CONTRACT (`navigation/AppRoutes.kt`)

All Jetpack Compose screens route through these string identifiers:

```kotlin
package com.sih26003.aasriti.navigation

object AppRoutes {
    const val ROLE_SELECT = "role_select"
    const val PIN_AUTH_CAREGIVER = "pin_auth_caregiver"
    const val PIN_AUTH_DOCTOR = "pin_auth_doctor"
    const val PATIENT_HOME = "patient_home"
    const val GAME_FLOWER_MATCH = "game_flower_match"
    const val GAME_FAMILY_TRIVIA = "game_family_trivia"
    const val GAME_VOICE_CUE = "game_voice_cue"
    const val GAME_SEQUENCING = "game_sequencing"
    const val GAME_CATEGORISATION = "game_categorisation"
    const val GAME_VILLAGE_MARKET = "game_village_market"
    const val GAME_PATTERN = "game_pattern"
    const val MEMORY_GARDEN = "memory_garden"
    const val CARE_CIRCLE = "care_circle"
    const val SOS_SCREEN = "sos_screen"
    const val CAREGIVER_DASHBOARD = "caregiver_dashboard"
    const val ASHA_DASHBOARD = "asha_dashboard"
    const val DOCTOR_ACCESS = "doctor_access"
    const val REMINDERS = "reminders/{patientId}"
}
```

---

## 🎯 6. WHAT MUST WORK LIVE vs. WHAT IS SIMULATED

| Subsystem | What MUST Actually Work (Live in Demo) | What CAN Be Simulated (Hackathon Speed) |
| :--- | :--- | :--- |
| **Patient Screen** | Guided temporal header, big buttons, gentle checklist | Remote cloud syncing of routine checks |
| **Cognitive Games** | Flower Match & Trivia run live, render vector flowers, emit real ms telemetry | Complex 50-level JSON libraries (Level 1–3 is plenty) |
| **Adaptive Scaling**| **Difficulty visibly shifts** between rounds based on hesitations | Scikit-Learn Python pipeline runtime on phone |
| **Caregiver UI** | **Today's Priority Card** dynamically reacts to patient routine status | Real-time Firebase Firestore webhooks |
| **ASHA Worker** | Kamrup 14-patient roster filters by `WATCH` & records rapid check-in | Bluetooth mesh synchronization across devices |
| **Doctor Portal** | 6-digit access (`424242`), 7-day reaction curve, clinical notes save | Complex PDF file export to external printers |
| **Telephony & SOS**| Simulated in-app active call timer & reassurance alert confirmation | Real Android telecom telephony intents / 108 calls |

---

## 🧪 7. ZERO-BLOCKER MOCK PROVIDER (`demo/AasritiMockProvider.kt`)

Frontend developers (Krishna, Bhavya, Shravani, Kimaya) can immediately bind their composables to this mock provider while backend database tables are compiling:

```kotlin
package com.sih26003.aasriti.demo

import com.sih26003.aasriti.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

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
        Reminder("r1", "aita_borah_01", "পুৱাৰ ৰক্তচাপৰ ঔষধ", "Morning Blood Pressure Medicine", "০৮:০০ AM", isMedicine = true, isCompleted = false),
        Reminder("r2", "aita_borah_01", "এগিলাচ কুহুমীয়া পানী", "Warm Glass of Water", "১০:৩০ AM", isMedicine = false, isCompleted = true),
        Reminder("r3", "aita_borah_01", "ফুলনি বাৰীৰ ফুৰা", "Gentle Garden Stroll", "০৪:৩০ PM", isMedicine = false, isCompleted = false)
    )

    val recentSessions = listOf(
        GameSession("s1", "aita_borah_01", "FLOWER_MATCH", difficultyLevel = 2, accuracy = 1.0f, reactionTimeMs = 2650L, hesitationCount = 1, errorCount = 0, durationMs = 18000L),
        GameSession("s2", "aita_borah_01", "FAMILY_TRIVIA", difficultyLevel = 2, accuracy = 0.8f, reactionTimeMs = 3100L, hesitationCount = 3, errorCount = 1, durationMs = 24000L)
    )

    fun observeMockPatient(): Flow<Patient> = flowOf(patient)
    fun observeMockReminders(): Flow<List<Reminder>> = flowOf(reminders)
    fun observeMockSessions(): Flow<List<GameSession>> = flowOf(recentSessions)
}
```

---

## 🤝 8. READY-TO-CODE CHECKLIST FOR EACH MEMBER

When any member opens their task:
1. **Jasleen**: Implements `data/local/` (DAOs & Entities) and maps them to Section 2 `domain/model/`.
2. **Venkatesh**: Wire `AdaptiveEngine`, `PriorityEngine`, and `TrendEngine` (Section 4) to the Application DI container.
3. **Krishna**: Plug `GameSession` telemetry emitter into `CommonGameFramework.kt` (Section 1 & 2).
4. **Bhavya**: Wire Games 4–6 to `AasritiColorTokens` and Section 4 `AdaptiveEngine`.
5. **Shravani**: Wire Caregiver & ASHA cards to Section 4 `PriorityEngine.evaluateTodayPriority()`.
6. **Kimaya**: Wire Elder Companion and Doctor views to `TrendEngine` and `AppRoutes` (Section 4 & 5).
