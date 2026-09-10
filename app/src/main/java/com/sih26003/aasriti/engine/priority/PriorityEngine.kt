package com.sih26003.aasriti.engine.priority

import com.sih26003.aasriti.domain.model.CareLog
import com.sih26003.aasriti.domain.model.GameSession
import com.sih26003.aasriti.domain.model.Patient
import com.sih26003.aasriti.domain.model.Reminder
import com.sih26003.aasriti.domain.model.TodayPriority

/**
 * AASRITI Care Triage & Priority Engine.
 * Evaluates patient status, missed reminders, and game performance to emit deterministic priority signals.
 */
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
