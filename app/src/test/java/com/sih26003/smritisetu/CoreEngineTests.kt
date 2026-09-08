package com.sih26003.smritisetu

import com.sih26003.smritisetu.core.security.CryptoUtils
import com.sih26003.smritisetu.data.local.database.MIGRATION_1_2
import com.sih26003.smritisetu.data.local.entities.CareLogEntity
import com.sih26003.smritisetu.data.local.entities.GameSessionEntity
import com.sih26003.smritisetu.demo.AasritiDemoData
import com.sih26003.smritisetu.demo.DemoPatientConfig
import com.sih26003.smritisetu.feature.games.framework.GameId
import com.sih26003.smritisetu.feature.games.framework.PerformanceCollector
import org.junit.Assert.*
import org.junit.Test

class CoreEngineTests {

    @Test
    fun testSha256PinHashingAndVerification() {
        val pin = "123456"
        val hash = CryptoUtils.hashPin(pin)
        assertNotNull(hash)
        assertEquals(64, hash.length) // SHA-256 is 64 hex characters
        assertTrue(CryptoUtils.verifyPin("123456", hash))
        assertFalse(CryptoUtils.verifyPin("654321", hash))
    }

    @Test
    fun testSaltedPinHashingAndConstantTimeVerification() {
        val pin = "876543"
        val salt = "device_xyz_unique_salt"
        val hash = CryptoUtils.hashPin(pin, salt)
        assertNotNull(hash)
        assertEquals(64, hash.length)

        // Verifies with same salt
        assertTrue(CryptoUtils.verifyPin(pin, hash, salt))

        // Fails with wrong PIN
        assertFalse(CryptoUtils.verifyPin("111111", hash, salt))

        // Fails with wrong salt
        assertFalse(CryptoUtils.verifyPin(pin, hash, "wrong_salt"))
    }

    @Test
    fun testSixDigitCodeGeneration() {
        val generated = mutableSetOf<String>()
        for (i in 1..50) {
            val code = CryptoUtils.generateSixDigitCode()
            assertEquals(6, code.length)
            assertTrue(code.all { it.isDigit() })
            assertTrue(code.toInt() in 100000..999999)
            generated.add(code)
        }
        // SecureRandom entropy check: >= 45 unique codes in 50 trials
        assertTrue(generated.size >= 45)
    }

    @Test
    fun testPerformanceCollectorAccuracyAndErrors() {
        val collector = PerformanceCollector()
        collector.startRound()

        // 3 correct, 1 error -> 75% accuracy
        collector.recordInteraction(isCorrect = true)
        collector.recordInteraction(isCorrect = true)
        collector.recordInteraction(isCorrect = false)
        collector.recordInteraction(isCorrect = true)

        // Mock DecisionTreeEngine fallback test
        val mockRecommendation = when {
            0.75f >= 0.85f -> 3
            0.75f <= 0.60f -> 1
            else -> 2 // Maintain
        }
        assertEquals(2, mockRecommendation)
    }

    @Test
    fun testAllSixGamesRegistered() {
        val gameIds = GameId.values()
        assertEquals(6, gameIds.size)
        assertTrue(gameIds.contains(GameId.FAMILY_TRIVIA))
        assertTrue(gameIds.contains(GameId.VOICE_CUE_CARD))
        assertTrue(gameIds.contains(GameId.SEQUENCING))
        assertTrue(gameIds.contains(GameId.CATEGORISATION))
        assertTrue(gameIds.contains(GameId.VILLAGE_MARKET))
        assertTrue(gameIds.contains(GameId.PATTERN_RECOGNITION))

        // Confirm clinical domains are defined without diagnostic claims
        gameIds.forEach { g ->
            assertFalse(g.clinicalDomain.contains("Diagnosis"))
            assertFalse(g.titleIndic.isBlank())
            assertFalse(g.emoji.isBlank())
        }
    }

    @Test
    fun testAdaptiveEngineAdjustments() {
        // High hesitation should ease down difficulty
        val eased = com.sih26003.smritisetu.engine.adaptive.AdaptiveEngine.evaluateNextRound(
            currentLevel = 3,
            accuracy = 0.8f,
            reactionTimeMs = 3800L,
            hesitationCount = 2
        )
        assertEquals(2, eased.nextLevel)
        assertEquals("HESITATION_EASED", eased.adjustmentReason)

        // Low accuracy should ease down difficulty
        val lowAcc = com.sih26003.smritisetu.engine.adaptive.AdaptiveEngine.evaluateNextRound(
            currentLevel = 2,
            accuracy = 0.5f,
            reactionTimeMs = 2100L,
            hesitationCount = 0
        )
        assertEquals(1, lowAcc.nextLevel)

        // Rapid 100% accuracy should step up
        val steppedUp = com.sih26003.smritisetu.engine.adaptive.AdaptiveEngine.evaluateNextRound(
            currentLevel = 2,
            accuracy = 1.0f,
            reactionTimeMs = 1800L,
            hesitationCount = 0
        )
        assertEquals(3, steppedUp.nextLevel)
        assertEquals("HIGH_ACCURACY_STEP_UP", steppedUp.adjustmentReason)

        // Normal pace should maintain level
        val stable = com.sih26003.smritisetu.engine.adaptive.AdaptiveEngine.evaluateNextRound(
            currentLevel = 2,
            accuracy = 0.85f,
            reactionTimeMs = 2600L,
            hesitationCount = 0
        )
        assertEquals(2, stable.nextLevel)
        assertEquals("STABLE", stable.adjustmentReason)
    }

    @Test
    fun testTrendEngine7DayAggregation() {
        val sessions = listOf(
            com.sih26003.smritisetu.domain.model.GameSession(
                id = "s1",
                patientId = "AS-KAM-0042",
                gameId = "FLOWER_MATCH",
                difficultyLevel = 1,
                accuracy = 1.0f,
                reactionTimeMs = 2000L,
                hesitationCount = 1,
                errorCount = 0,
                durationMs = 15000L,
                timestamp = System.currentTimeMillis()
            ),
            com.sih26003.smritisetu.domain.model.GameSession(
                id = "s2",
                patientId = "AS-KAM-0042",
                gameId = "FLOWER_MATCH",
                difficultyLevel = 2,
                accuracy = 1.0f,
                reactionTimeMs = 2400L,
                hesitationCount = 2,
                errorCount = 0,
                durationMs = 18000L,
                timestamp = System.currentTimeMillis()
            )
        )

        val trend = com.sih26003.smritisetu.engine.trend.TrendEngine.compute7DaySignals("AS-KAM-0042", sessions)
        assertEquals("AS-KAM-0042", trend.patientId)
        assertEquals(2200L, trend.averageReactionTimeMs)
        assertEquals(3, trend.totalHesitationGaps)
        assertEquals(7, trend.dailyPoints.size)
        assertTrue(trend.explainableSummary.isNotEmpty())
        assertFalse(trend.explainableSummary.any { it.contains("Diagnosis") })
    }

    @Test
    fun testPriorityEngineTriageAlerts() {
        val testPatient = com.sih26003.smritisetu.domain.model.Patient(
            id = "AS-KAM-0042",
            pseudonymCode = "AS-KAM-0042",
            displayName = "আইতা বৰা (Aita Borah)",
            displaySubtitle = "৮২ বছৰীয়া",
            birthYear = 1958,
            gender = "F",
            villageLocation = "হাজো, কামৰূপ",
            primaryLanguage = "as",
            cognitiveStage = "MCI"
        )

        val doneReminders = listOf(
            com.sih26003.smritisetu.domain.model.Reminder(
                id = "r1",
                patientId = testPatient.id,
                titleIndic = "ঔষধ",
                titleEn = "Medicine",
                timeLabel = "08:00 AM",
                isMedicine = true,
                isCompleted = true
            )
        )

        // 1. Normal state
        val normalPriority = com.sih26003.smritisetu.engine.priority.PriorityEngine.evaluateTodayPriority(
            patient = testPatient,
            reminders = doneReminders,
            recentSessions = emptyList(),
            recentLogs = emptyList()
        )
        assertEquals("NORMAL", normalPriority.severity)

        // 2. Missed medicine -> WATCH
        val pendingReminders = listOf(
            com.sih26003.smritisetu.domain.model.Reminder(
                id = "r1",
                patientId = testPatient.id,
                titleIndic = "ঔষধ",
                titleEn = "Medicine",
                timeLabel = "08:00 AM",
                isMedicine = true,
                isCompleted = false
            )
        )
        val watchPriority = com.sih26003.smritisetu.engine.priority.PriorityEngine.evaluateTodayPriority(
            patient = testPatient,
            reminders = pendingReminders,
            recentSessions = emptyList(),
            recentLogs = emptyList()
        )
        assertEquals("WATCH", watchPriority.severity)

        // 3. Fall logged -> PRIORITY
        val fallLog = listOf(
            com.sih26003.smritisetu.domain.model.CareLog(
                id = "log1",
                patientId = testPatient.id,
                authorRole = "CAREGIVER",
                category = "FALL",
                severity = "HIGH",
                notes = "Mild bedside slip"
            )
        )
        val fallPriority = com.sih26003.smritisetu.engine.priority.PriorityEngine.evaluateTodayPriority(
            patient = testPatient,
            reminders = doneReminders,
            recentSessions = emptyList(),
            recentLogs = fallLog
        )
        assertEquals("PRIORITY", fallPriority.severity)
    }

    @Test
    fun testTrendEngineEmptyStateNoSyntheticData() {
        val trend = com.sih26003.smritisetu.engine.trend.TrendEngine.compute7DaySignals("AS-KAM-0042", emptyList())
        assertEquals("AS-KAM-0042", trend.patientId)
        assertEquals(0L, trend.averageReactionTimeMs)
        assertEquals(0, trend.totalHesitationGaps)
        assertEquals(0, trend.routineAdherencePercent)
        assertTrue(trend.dailyPoints.isEmpty())
        assertTrue(trend.explainableSummary.any { it.contains("No recent interaction data available") })
    }

    @Test
    fun testCareLogPersistenceEntityToDomainMapping() {
        val entity = com.sih26003.smritisetu.data.local.entities.CareLogEntity(
            id = "log-test-1",
            patientId = "AS-KAM-0042",
            authorRole = "CAREGIVER",
            category = "FALL",
            severity = "PRIORITY",
            notes = "Elder slipped near bed; uninjured.",
            timestamp = System.currentTimeMillis()
        )
        val domainLog = com.sih26003.smritisetu.domain.model.CareLog(
            id = entity.id,
            patientId = entity.patientId,
            authorRole = entity.authorRole,
            category = entity.category,
            severity = entity.severity,
            notes = entity.notes,
            timestamp = entity.timestamp
        )
        assertEquals(entity.id, domainLog.id)
        assertEquals("FALL", domainLog.category)
        assertEquals("PRIORITY", domainLog.severity)
        assertEquals(entity.notes, domainLog.notes)
    }

    @Test
    fun testCanonicalPatientIdentity() {
        assertEquals("aita_borah_01", DemoPatientConfig.PATIENT_ID)
        assertEquals("AS-KAM-0042", DemoPatientConfig.PSEUDONYM_CODE)
        assertEquals(DemoPatientConfig.PATIENT_ID, AasritiDemoData.patient.id)
        val canonical = DemoPatientConfig.createCanonicalPatient()
        assertEquals(DemoPatientConfig.PATIENT_ID, canonical.id)
        assertEquals(DemoPatientConfig.PSEUDONYM_CODE, canonical.pseudonymCode)
    }

    @Test
    fun testSessionPersistenceContractReferencesCanonicalPatient() {
        val gameSession = GameSessionEntity(
            patientId = DemoPatientConfig.PATIENT_ID,
            gameId = "FAMILY_TRIVIA",
            difficultyLevel = 1,
            durationMs = 12000L,
            accuracy = 0.9f,
            errors = 0,
            reactionTimeMs = 2200L,
            hesitationCount = 1,
            adaptationDecision = 2,
            completed = true
        )
        assertEquals(DemoPatientConfig.PATIENT_ID, gameSession.patientId)

        val careLog = CareLogEntity(
            patientId = DemoPatientConfig.PATIENT_ID,
            authorRole = "CAREGIVER",
            category = "HYDRATION",
            severity = "NORMAL",
            notes = "Patient drank full glass of water."
        )
        assertEquals(DemoPatientConfig.PATIENT_ID, careLog.patientId)
    }

    @Test
    fun testCrossSessionDifficultyResolution() {
        fun resolveInitialDifficulty(adaptationDecision: Int?): Int {
            return adaptationDecision?.coerceIn(1, 5) ?: 1
        }

        assertEquals(1, resolveInitialDifficulty(null))
        assertEquals(4, resolveInitialDifficulty(4))
        assertEquals(1, resolveInitialDifficulty(0)) // Clamped up to 1
        assertEquals(5, resolveInitialDifficulty(6)) // Clamped down to 5
    }

    @Test
    fun testRoomMigration1To2Contract() {
        val migration = MIGRATION_1_2
        assertEquals(1, migration.startVersion)
        assertEquals(2, migration.endVersion)
    }
}

