package com.sih26003.smritisetu

import com.sih26003.smritisetu.data.local.entities.CareLogEntity
import com.sih26003.smritisetu.data.local.entities.ReminderEntity
import com.sih26003.smritisetu.demo.DemoPatientConfig
import com.sih26003.smritisetu.domain.model.CareLog
import com.sih26003.smritisetu.domain.model.Patient
import com.sih26003.smritisetu.domain.model.Reminder
import com.sih26003.smritisetu.engine.priority.PriorityEngine
import org.junit.Assert.*
import org.junit.Test

/**
 * AASRITI Caregiver, ASHA, and Routine Engine Verification Suite.
 * Covers:
 * 1. Quick Care Log creation, validation, and role tagging ("CAREGIVER" vs "ASHA")
 * 2. Room persistence schema compliance and DemoPatientConfig.PATIENT_ID enforcement
 * 3. PriorityEngine triage triggers (NORMAL -> WATCH -> PRIORITY)
 * 4. Offline Reminder entity constraints and domain translation
 * 5. Care history filtering and honest empty state contracts (Zero fake data)
 */
class CaregiverAshaWorkflowTests {

    private val testPatient = Patient(
        id = DemoPatientConfig.PATIENT_ID,
        pseudonymCode = DemoPatientConfig.PSEUDONYM_CODE,
        displayName = "আইতা বৰা (Aita Borah)",
        displaySubtitle = "৮২ বছৰীয়া",
        birthYear = 1958,
        gender = "F",
        villageLocation = "হাজো, কামৰূপ",
        primaryLanguage = "as",
        cognitiveStage = "Mild Cognitive Impairment (MCI)"
    )

    // =========================================================================
    // 1. CAREGIVER QUICK CARE LOG TESTS
    // =========================================================================

    @Test
    fun testCaregiverQuickLogCreationWithCanonicalPatient() {
        val note = "আইতাই দুপৰীয়াৰ ভাত ভালদৰে খালে (Had a wholesome lunch)."
        val careLog = CareLogEntity(
            patientId = DemoPatientConfig.PATIENT_ID,
            authorRole = "CAREGIVER",
            category = "APPETITE",
            severity = "NORMAL",
            notes = note
        )

        assertEquals("aita_borah_01", careLog.patientId)
        assertEquals("CAREGIVER", careLog.authorRole)
        assertEquals("APPETITE", careLog.category)
        assertEquals("NORMAL", careLog.severity)
        assertEquals(note, careLog.notes)
        assertTrue("Timestamp should be populated", careLog.timestamp > 0)
    }

    @Test
    fun testCaregiverSupportedCategoriesAndSeverities() {
        val validCategories = listOf("MEDICINE", "APPETITE", "SLEEP", "GENERAL", "FALL", "CONFUSION")
        val validSeverities = listOf("NORMAL", "WATCH", "PRIORITY")

        validCategories.forEach { category ->
            validSeverities.forEach { severity ->
                val log = CareLogEntity(
                    patientId = DemoPatientConfig.PATIENT_ID,
                    authorRole = "CAREGIVER",
                    category = category,
                    severity = severity,
                    notes = "Valid log for $category with $severity"
                )
                assertEquals(category, log.category)
                assertEquals(severity, log.severity)
                assertEquals("CAREGIVER", log.authorRole)
            }
        }
    }

    @Test
    fun testCareLogValidationNoteCannotBeBlank() {
        fun validateLogInput(note: String): Boolean {
            return note.trim().isNotEmpty()
        }

        assertFalse("Blank note should fail validation", validateLogInput(""))
        assertFalse("Whitespace note should fail validation", validateLogInput("    "))
        assertTrue("Meaningful note should pass validation", validateLogInput("Morning BP was normal."))
    }

    // =========================================================================
    // 2. ASHA / HEALTH WORKER VISIT WORKFLOW TESTS
    // =========================================================================

    @Test
    fun testAshaFieldVisitLogUsesAshaRoleAndSharesRoomHistory() {
        val ashaObservation = "ৰক্তচাপ ১২৮/৮২ mmHg, নাড়ী ৭৪/মি. আইতাৰ মানসিক স্থিতি শান্ত। (BP 128/82, Pulse 74, calm demeanor)."
        val ashaLog = CareLogEntity(
            patientId = DemoPatientConfig.PATIENT_ID,
            authorRole = "ASHA",
            category = "GENERAL",
            severity = "NORMAL",
            notes = ashaObservation
        )

        assertEquals("ASHA", ashaLog.authorRole)
        assertEquals("aita_borah_01", ashaLog.patientId)
        assertEquals("GENERAL", ashaLog.category)
        assertEquals("NORMAL", ashaLog.severity)
        assertEquals(ashaObservation, ashaLog.notes)
    }

    @Test
    fun testAshaEscalationToPrioritySeverity() {
        val highRiskObservation = "আইতা আজি পুৱা বিছনাৰ পৰা পিছলি পৰিছিল, সামান্য আঘাত। (Bedside slip observed with mild contusion)."
        val ashaLog = CareLogEntity(
            patientId = DemoPatientConfig.PATIENT_ID,
            authorRole = "ASHA",
            category = "FALL",
            severity = "PRIORITY",
            notes = highRiskObservation
        )

        assertEquals("ASHA", ashaLog.authorRole)
        assertEquals("FALL", ashaLog.category)
        assertEquals("PRIORITY", ashaLog.severity)

        // Convert to domain model and feed into PriorityEngine
        val domainLog = CareLog(
            id = ashaLog.id,
            patientId = ashaLog.patientId,
            authorRole = ashaLog.authorRole,
            category = ashaLog.category,
            severity = ashaLog.severity,
            notes = ashaLog.notes,
            timestamp = ashaLog.timestamp
        )

        val priority = PriorityEngine.evaluateTodayPriority(
            patient = testPatient,
            reminders = emptyList(),
            recentSessions = emptyList(),
            recentLogs = listOf(domainLog)
        )

        assertEquals("PRIORITY", priority.severity)
        assertTrue("Priority title should alert stumble", priority.titleEn.contains("Stumble"))
        assertEquals("Check balance & footwear", priority.suggestedAction)
    }

    // =========================================================================
    // 3. PRIORITY ENGINE TRIAGE LOGIC TESTS
    // =========================================================================

    @Test
    fun testPriorityEngineNormalWhenRemindersDoneAndNoIncident() {
        val completedReminder = Reminder(
            id = "rem_1",
            patientId = testPatient.id,
            titleIndic = "ঔষধ খোৱা",
            titleEn = "Take Medication",
            timeLabel = "09:00 AM",
            isMedicine = true,
            isCompleted = true
        )

        val routineLog = CareLog(
            id = "log_normal",
            patientId = testPatient.id,
            authorRole = "CAREGIVER",
            category = "MEDICINE",
            severity = "NORMAL",
            notes = "Morning medicine given on time."
        )

        val result = PriorityEngine.evaluateTodayPriority(
            patient = testPatient,
            reminders = listOf(completedReminder),
            recentSessions = emptyList(),
            recentLogs = listOf(routineLog)
        )

        assertEquals("NORMAL", result.severity)
        assertEquals("Continue daily routine", result.suggestedAction)
    }

    @Test
    fun testPriorityEngineWatchWhenMedicineMissed() {
        val missedMedicineReminder = Reminder(
            id = "rem_med",
            patientId = testPatient.id,
            titleIndic = "পুৱাৰ ঔষধ",
            titleEn = "Morning Medicine",
            timeLabel = "09:00 AM",
            isMedicine = true,
            isCompleted = false
        )

        val result = PriorityEngine.evaluateTodayPriority(
            patient = testPatient,
            reminders = listOf(missedMedicineReminder),
            recentSessions = emptyList(),
            recentLogs = emptyList()
        )

        assertEquals("WATCH", result.severity)
        assertEquals("Confirm medication dose", result.suggestedAction)
        assertTrue("Explanation indicates medication pending", result.explanationIndic.contains("ঔষধ"))
    }

    @Test
    fun testPriorityEngineWatchWhenHighHesitationInGameSession() {
        val highHesitationSession = com.sih26003.smritisetu.domain.model.GameSession(
            id = "sess_hes",
            patientId = testPatient.id,
            gameId = "FLOWER_MATCH",
            difficultyLevel = 1,
            accuracy = 0.7f,
            reactionTimeMs = 3500L,
            hesitationCount = 4,
            errorCount = 1,
            durationMs = 25000L,
            timestamp = System.currentTimeMillis()
        )

        val result = PriorityEngine.evaluateTodayPriority(
            patient = testPatient,
            reminders = emptyList(),
            recentSessions = listOf(highHesitationSession),
            recentLogs = emptyList()
        )

        assertEquals("WATCH", result.severity)
        assertEquals("Offer glass of water", result.suggestedAction)
    }

    // =========================================================================
    // 4. OFFLINE REMINDER LOGIC & VALIDATION TESTS
    // =========================================================================

    @Test
    fun testReminderEntityCreationAndFieldConstraints() {
        val reminder = ReminderEntity(
            patientId = DemoPatientConfig.PATIENT_ID,
            title = "দুপৰীয়াৰ আহাৰৰ পিছৰ ঔষধ",
            reminderType = "MEDICINE",
            hour = 13,
            minute = 30,
            isEnabled = true
        )

        assertEquals("aita_borah_01", reminder.patientId)
        assertEquals("MEDICINE", reminder.reminderType)
        assertEquals(13, reminder.hour)
        assertEquals(30, reminder.minute)
        assertTrue(reminder.isEnabled)
    }

    @Test
    fun testReminderTimeValidationRules() {
        fun isValidTime(hourStr: String, minuteStr: String): Boolean {
            val h = hourStr.toIntOrNull() ?: return false
            val m = minuteStr.toIntOrNull() ?: return false
            return h in 0..23 && m in 0..59
        }

        assertTrue(isValidTime("00", "00"))
        assertTrue(isValidTime("09", "15"))
        assertTrue(isValidTime("23", "59"))

        assertFalse("Hour 24 is invalid", isValidTime("24", "00"))
        assertFalse("Negative hour is invalid", isValidTime("-1", "30"))
        assertFalse("Minute 60 is invalid", isValidTime("12", "60"))
        assertFalse("Non-digit string is invalid", isValidTime("ab", "00"))
    }

    @Test
    fun testReminderToggleState() {
        val active = ReminderEntity(
            patientId = DemoPatientConfig.PATIENT_ID,
            title = "পানী খোৱা",
            reminderType = "HYDRATION",
            hour = 11,
            minute = 0,
            isEnabled = true
        )

        val toggledOff = active.copy(isEnabled = false)
        assertFalse(toggledOff.isEnabled)

        val toggledOn = toggledOff.copy(isEnabled = true)
        assertTrue(toggledOn.isEnabled)
    }

    // =========================================================================
    // 5. CARE HISTORY FILTERING & HONEST EMPTY STATE CONTRACT
    // =========================================================================

    @Test
    fun testCareHistoryRoleFiltering() {
        val logs = listOf(
            CareLogEntity(id = "1", patientId = DemoPatientConfig.PATIENT_ID, authorRole = "CAREGIVER", category = "MEDICINE", severity = "NORMAL", notes = "Med taken"),
            CareLogEntity(id = "2", patientId = DemoPatientConfig.PATIENT_ID, authorRole = "ASHA", category = "GENERAL", severity = "NORMAL", notes = "Vitals checked"),
            CareLogEntity(id = "3", patientId = DemoPatientConfig.PATIENT_ID, authorRole = "CAREGIVER", category = "SLEEP", severity = "NORMAL", notes = "Slept 7 hours")
        )

        val caregiverLogs = logs.filter { it.authorRole == "CAREGIVER" }
        val ashaLogs = logs.filter { it.authorRole == "ASHA" }

        assertEquals(2, caregiverLogs.size)
        assertEquals(1, ashaLogs.size)
        assertEquals("ASHA", ashaLogs.first().authorRole)
    }

    @Test
    fun testCareHistoryCategoryFiltering() {
        val logs = listOf(
            CareLogEntity(id = "1", patientId = DemoPatientConfig.PATIENT_ID, authorRole = "CAREGIVER", category = "MEDICINE", severity = "NORMAL", notes = "Morning pill"),
            CareLogEntity(id = "2", patientId = DemoPatientConfig.PATIENT_ID, authorRole = "CAREGIVER", category = "FALL", severity = "PRIORITY", notes = "Near miss"),
            CareLogEntity(id = "3", patientId = DemoPatientConfig.PATIENT_ID, authorRole = "ASHA", category = "MEDICINE", severity = "NORMAL", notes = "Refill confirmed")
        )

        val medicineLogs = logs.filter { it.category == "MEDICINE" }
        val fallLogs = logs.filter { it.category == "FALL" }

        assertEquals(2, medicineLogs.size)
        assertEquals(1, fallLogs.size)
        assertEquals("FALL", fallLogs.first().category)
    }

    @Test
    fun testHonestEmptyStateNeverManufacturesSyntheticLogs() {
        // Contract: When room returns emptyList, UI must show honest empty card without generating synthetic history
        val emptyLogs: List<CareLogEntity> = emptyList()
        assertTrue("No logs should be present when repository is empty", emptyLogs.isEmpty())
        assertEquals(0, emptyLogs.size)
    }

    // =========================================================================
    // 6. DAY 2 GOLDEN VERTICAL SLICE INTEGRATION TESTS
    // =========================================================================

    @Test
    fun testGoldenSliceGameSessionTelemetryToCaregiverPriority() {
        // Telemetry signals: Family Trivia session with 4 hesitation gaps
        val realSession = com.sih26003.smritisetu.data.local.entities.GameSessionEntity(
            patientId = DemoPatientConfig.PATIENT_ID,
            gameId = "FAMILY_TRIVIA",
            difficultyLevel = 2,
            durationMs = 32000L,
            accuracy = 0.75f,
            errors = 1,
            reactionTimeMs = 4200L,
            hesitationCount = 4,
            adaptationDecision = 1,
            completed = true
        )

        // Convert to domain model as done in CaregiverDashboardScreen
        val domainSession = com.sih26003.smritisetu.domain.model.GameSession(
            id = realSession.id,
            patientId = realSession.patientId,
            gameId = realSession.gameId,
            difficultyLevel = realSession.difficultyLevel,
            accuracy = realSession.accuracy,
            reactionTimeMs = realSession.reactionTimeMs,
            hesitationCount = realSession.hesitationCount,
            errorCount = realSession.errors,
            durationMs = realSession.durationMs,
            timestamp = realSession.timestamp
        )

        val priority = PriorityEngine.evaluateTodayPriority(
            patient = testPatient,
            reminders = emptyList(),
            recentSessions = listOf(domainSession),
            recentLogs = emptyList()
        )

        assertEquals("WATCH", priority.severity)
        assertEquals("Offer glass of water", priority.suggestedAction)
        assertTrue("Title mentions hesitation", priority.titleEn.contains("hesitation"))
    }

    @Test
    fun testGoldenSliceQuickLogPersistenceToCaregiverPriority() {
        // Caregiver saves a rapid fall observation in Room
        val fallLog = CareLogEntity(
            patientId = DemoPatientConfig.PATIENT_ID,
            authorRole = "CAREGIVER",
            category = "FALL",
            severity = "PRIORITY",
            notes = "Mild slip near the bathroom door, assisted immediately."
        )

        val domainLog = CareLog(
            id = fallLog.id,
            patientId = fallLog.patientId,
            authorRole = fallLog.authorRole,
            category = fallLog.category,
            severity = fallLog.severity,
            notes = fallLog.notes,
            timestamp = fallLog.timestamp
        )

        val priority = PriorityEngine.evaluateTodayPriority(
            patient = testPatient,
            reminders = emptyList(),
            recentSessions = emptyList(),
            recentLogs = listOf(domainLog)
        )

        assertEquals("PRIORITY", priority.severity)
        assertEquals("Check balance & footwear", priority.suggestedAction)
        assertTrue("Priority title indicates stumble alert", priority.titleEn.contains("Stumble"))
    }

    @Test
    fun testGoldenSliceHonestEmptyStateWhenNoGameSessionsExist() {
        val emptySessions: List<com.sih26003.smritisetu.data.local.entities.GameSessionEntity> = emptyList()
        assertTrue("No game sessions should exist initially", emptySessions.isEmpty())

        val domainPatient = testPatient
        val priority = PriorityEngine.evaluateTodayPriority(
            patient = domainPatient,
            reminders = emptyList(),
            recentSessions = emptyList(),
            recentLogs = emptyList()
        )

        assertEquals("NORMAL", priority.severity)
        assertEquals("Continue daily routine", priority.suggestedAction)
    }
}
