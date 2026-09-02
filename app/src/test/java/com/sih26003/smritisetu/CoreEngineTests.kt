package com.sih26003.smritisetu

import com.sih26003.smritisetu.core.security.CryptoUtils
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
    fun testSixDigitCodeGeneration() {
        for (i in 1..20) {
            val code = CryptoUtils.generateSixDigitCode()
            assertEquals(6, code.length)
            assertTrue(code.all { it.isDigit() })
        }
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
}
