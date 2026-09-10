package com.sih26003.aasriti.core.security

import java.security.MessageDigest
import java.security.SecureRandom

object CryptoUtils {

    private val secureRandom = SecureRandom()

    /**
     * Hashes local PIN using SHA-256 for persistent Room storage.
     * Optionally takes a device or user salt to prevent rainbow table attacks.
     */
    fun hashPin(pin: String, salt: String = ""): String {
        val input = if (salt.isNotEmpty()) "$salt:$pin" else pin
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Validates candidate PIN against stored SHA-256 hash using constant-time comparison
     * to protect against timing attacks.
     */
    fun verifyPin(candidatePin: String, storedHash: String, salt: String = ""): Boolean {
        val candidateHash = hashPin(candidatePin, salt)
        return MessageDigest.isEqual(
            candidateHash.toByteArray(Charsets.UTF_8),
            storedHash.toByteArray(Charsets.UTF_8)
        )
    }

    /**
     * Generates a cryptographically secure random 6-digit code for Separate Device Mode or Doctor Access.
     */
    fun generateSixDigitCode(): String {
        val number = secureRandom.nextInt(900000) + 100000
        return number.toString()
    }
}
