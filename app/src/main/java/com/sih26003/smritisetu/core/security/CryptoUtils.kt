package com.sih26003.smritisetu.core.security

import java.security.MessageDigest
import kotlin.random.Random

object CryptoUtils {
    /**
     * Hashes local PIN using SHA-256 for persistent Room storage.
     */
    fun hashPin(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Validates candidate PIN against stored SHA-256 hash.
     */
    fun verifyPin(candidatePin: String, storedHash: String): Boolean {
        return hashPin(candidatePin) == storedHash
    }

    /**
     * Generates a random 6-digit link code for Separate Device Mode or Doctor Access.
     */
    fun generateSixDigitCode(): String {
        val number = Random.nextInt(100000, 999999)
        return number.toString()
    }
}
