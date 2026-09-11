package com.sih26003.aasriti.navigation

/**
 * AASRITI Canonical Navigation Routes.
 * Frozen contract for screen navigation across all features.
 */
object AppRoutes {
    const val ROLE_SELECT = "role_select"
    const val ONBOARDING_WELCOME = "onboarding_welcome"
    const val ONBOARDING_THEME = "onboarding_theme"
    const val ONBOARDING_LANGUAGE = "onboarding_language"
    const val ONBOARDING_ACCESSIBILITY = "onboarding_accessibility"
    const val ONBOARDING_PATIENT_REGISTER = "onboarding_patient_register"
    const val ONBOARDING_CAREGIVER_REGISTER = "onboarding_caregiver_register"
    const val ONBOARDING_CARE_CIRCLE_SETUP = "onboarding_care_circle_setup"
    const val ONBOARDING_CARE_CIRCLE_SETUP_PARAM = "onboarding_care_circle_setup/{patientId}"
    const val ONBOARDING_CONSENT = "onboarding_consent"
    const val INFORMED_CONSENT = "informed_consent"
    const val PIN_AUTH_CAREGIVER = "pin_auth_caregiver"
    const val PIN_AUTH_DOCTOR = "pin_auth_doctor"
    const val PATIENT_HOME = "patient_home"
    const val GAME_FLOWER_MATCH = "game_flower_match"
    const val GAME_FAMILY_TRIVIA = "game_family_trivia"
    const val GAME_FAMILY_TRIVIA_DIFF = "game_family_trivia/{difficulty}"
    const val GAME_VOICE_CUE = "game_voice_cue_card"
    const val GAME_SEQUENCING = "game_sequencing"
    const val GAME_CATEGORISATION = "game_categorisation"
    const val GAME_VILLAGE_MARKET = "game_village_market"
    const val GAME_PATTERN = "game_pattern_recognition"
    const val MEMORY_GARDEN = "memory_garden"
    const val CARE_CIRCLE = "care_circle"
    const val SOS_SCREEN = "sos_screen"
    const val SOS_FOLLOW_UP = "sos_follow_up"
    const val CAREGIVER_DASHBOARD = "caregiver_dashboard"
    const val ASHA_DASHBOARD = "asha_dashboard"
    const val DOCTOR_ACCESS = "doctor_access"
    const val REMINDERS = "reminders/{patientId}"

    fun buildRemindersRoute(patientId: String): String = "reminders/$patientId"
    fun buildFamilyTriviaRoute(difficulty: Int): String = "game_family_trivia/$difficulty"
    fun buildCareCircleSetupRoute(patientId: String): String = "onboarding_care_circle_setup/$patientId"
}
