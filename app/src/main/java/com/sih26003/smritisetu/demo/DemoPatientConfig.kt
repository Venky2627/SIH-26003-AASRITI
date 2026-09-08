package com.sih26003.smritisetu.demo

import com.sih26003.smritisetu.data.local.entities.PatientEntity

/**
 * Single source of truth for prototype demo patient identity.
 * Unifies Room SQLite seed partition, UI selection, and clinical telemetry.
 */
object DemoPatientConfig {
    const val PATIENT_ID = "aita_borah_01"
    const val PSEUDONYM_CODE = "AS-KAM-0042"

    fun createCanonicalPatient(): PatientEntity = PatientEntity(
        id = PATIENT_ID,
        pseudonymCode = PSEUDONYM_CODE,
        birthYear = 1958,
        gender = "F",
        primaryLanguage = "as",
        cognitiveStage = "Mild Cognitive Impairment (MCI)"
    )
}
