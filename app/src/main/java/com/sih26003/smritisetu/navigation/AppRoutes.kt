package com.sih26003.smritisetu.navigation

/**
 * AASRITI Canonical Navigation Routes.
 * Frozen contract for screen navigation across all features.
 */
object AppRoutes {
    const val ROLE_SELECT = "role_select"
    const val PIN_AUTH_CAREGIVER = "pin_auth_caregiver"
    const val PIN_AUTH_DOCTOR = "pin_auth_doctor"
    const val PATIENT_HOME = "patient_home"
    const val GAME_FLOWER_MATCH = "game_flower_match"
    const val GAME_FAMILY_TRIVIA = "game_family_trivia"
    const val GAME_VOICE_CUE = "game_voice_cue_card"
    const val GAME_SEQUENCING = "game_sequencing"
    const val GAME_CATEGORISATION = "game_categorisation"
    const val GAME_VILLAGE_MARKET = "game_village_market"
    const val GAME_PATTERN = "game_pattern_recognition"
    const val MEMORY_GARDEN = "memory_garden"
    const val CARE_CIRCLE = "care_circle"
    const val SOS_SCREEN = "sos_screen"
    const val CAREGIVER_DASHBOARD = "caregiver_dashboard"
    const val ASHA_DASHBOARD = "asha_dashboard"
    const val DOCTOR_ACCESS = "doctor_access"
    const val REMINDERS = "reminders/{patientId}"

    fun buildRemindersRoute(patientId: String): String = "reminders/$patientId"
}
