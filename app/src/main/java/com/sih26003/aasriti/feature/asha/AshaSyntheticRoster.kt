package com.sih26003.aasriti.feature.asha

import com.sih26003.aasriti.demo.DemoAshaRosterItem
import com.sih26003.aasriti.demo.DemoPatientConfig

/**
 * =============================================================================
 * AASRITI EXP-03: SYNTHETIC DEMO ASHA ROSTER (14 ELDERS)
 * =============================================================================
 *
 * NOTICE:
 * This file contains strictly SYNTHETIC / FICTIONAL demo data created for
 * UI workflow demonstration, testing, and multi-patient roster visualization
 * in community health scenarios (Kamrup Rural, Assam).
 *
 * GUARANTEES & CONSTRAINTS:
 * 1. ZERO REAL PATIENT IDENTITIES: All names, hamlets, ages, and routine notes
 *    are fictional and do not represent real individuals or clinical subjects.
 * 2. ZERO CLINICAL DIAGNOSES: Notes contain only routine non-clinical community
 *    health observations (wellness checks, hydration prompts, family walks).
 * 3. STRICT ROOM DATABASE ISOLATION: These synthetic demo records are NEVER
 *    inserted into the local Room SQLite database (`patients`, `care_logs`, etc.).
 * 4. CANONICAL PATIENT PRESERVATION: Only the canonical demo elder (AS-KAM-0042 /
 *    Aita Borah) maps to the Room database identity (`DemoPatientConfig.PATIENT_ID`).
 *    The other 13 synthetic elders return null for Room patient ID and are clearly
 *    badged in the UI as synthetic demo entries without telemetry.
 * 5. SAFE PRIORITY ENGINE INTEGRATION: Live `PriorityEngine` evaluation is invoked
 *    ONLY for Room-backed patient data. Synthetic elders retain their explicitly
 *    labeled baseline demo triage tiers.
 * =============================================================================
 */
object AshaSyntheticRoster {

    /**
     * Complete 14-elder community health roster for ASHA dashboard demonstration.
     * Contains 1 canonical Room-backed demo patient + 13 synthetic demo elders.
     */
    val roster: List<DemoAshaRosterItem> = listOf(
        // Elder 1: Canonical AASRITI demo patient (Room-backed, dynamic PriorityEngine)
        DemoAshaRosterItem(
            id = "asha_pat_1",
            pseudonymCode = DemoPatientConfig.PSEUDONYM_CODE, // "AS-KAM-0042"
            nameIndic = "আইতা বৰা",
            nameEn = "Aita Borah (Age 68)",
            hamlet = "উত্তৰ বৰকুছি (North Barkuchi)",
            statusTier = "WATCH",
            lastVisitedDate = "০২ চেপ্তেম্বৰ, ২০২৬",
            notes = "Mild hydration delay noted; stable orientation and mood."
        ),
        // Elder 2: Synthetic Demo Elder
        DemoAshaRosterItem(
            id = "asha_pat_2",
            pseudonymCode = "AS-KAM-0043",
            nameIndic = "বীৰেন দাস",
            nameEn = "Biren Das (Age 74)",
            hamlet = "দক্ষিণ বৰকুছি (South Barkuchi)",
            statusTier = "NORMAL",
            lastVisitedDate = "০১ চেপ্তেম্বৰ, ২০২৬",
            notes = "Routine wellness check; completed morning Namghar walk."
        ),
        // Elder 3: Synthetic Demo Elder
        DemoAshaRosterItem(
            id = "asha_pat_3",
            pseudonymCode = "AS-KAM-0044",
            nameIndic = "হেমলতা দেৱী",
            nameEn = "Hemolata Devi (Age 71)",
            hamlet = "বজাৰ চ'ক (Bazar Chowk)",
            statusTier = "PRIORITY",
            lastVisitedDate = "২৮ আগষ্ট, ২০২৬",
            notes = "Scheduled BP followup check; daughter assisting with hydration reminder."
        ),
        // Elder 4: Synthetic Demo Elder
        DemoAshaRosterItem(
            id = "asha_pat_4",
            pseudonymCode = "AS-KAM-0045",
            nameIndic = "তৰুণ শইকীয়া",
            nameEn = "Tarun Saikia (Age 76)",
            hamlet = "পূব পাৰ (Pub Par)",
            statusTier = "NORMAL",
            lastVisitedDate = "০৩ চেপ্তেম্বৰ, ২০২৬",
            notes = "Good energy; engaged in courtyard gardening with family."
        ),
        // Elder 5: Synthetic Demo Elder
        DemoAshaRosterItem(
            id = "asha_pat_5",
            pseudonymCode = "AS-KAM-0046",
            nameIndic = "প্ৰফুল্ল কলিতা",
            nameEn = "Prafulla Kalita (Age 70)",
            hamlet = "আমবাৰী (Ambari)",
            statusTier = "WATCH",
            lastVisitedDate = "২৯ আগষ্ট, ২০২৬",
            notes = "Mild seasonal joint stiffness; hydration routine advised."
        ),
        // Elder 6: Synthetic Demo Elder
        DemoAshaRosterItem(
            id = "asha_pat_6",
            pseudonymCode = "AS-KAM-0047",
            nameIndic = "চন্দ্ৰপ্ৰভা বৰ্মন",
            nameEn = "Chandraprabha Barman (Age 73)",
            hamlet = "চেনিকুঠী (Chenikuthi)",
            statusTier = "NORMAL",
            lastVisitedDate = "৩১ আগষ্ট, ২০২৬",
            notes = "Attended local community kirtan; cheerful orientation."
        ),
        // Elder 7: Synthetic Demo Elder
        DemoAshaRosterItem(
            id = "asha_pat_7",
            pseudonymCode = "AS-KAM-0048",
            nameIndic = "নগেন মেধি",
            nameEn = "Nagen Medhi (Age 78)",
            hamlet = "হাতীগাঁও (Hatigaon)",
            statusTier = "PRIORITY",
            lastVisitedDate = "২৭ আগষ্ট, ২০২৬",
            notes = "Pending weekly wellness check; family requested ASHA visit."
        ),
        // Elder 8: Synthetic Demo Elder
        DemoAshaRosterItem(
            id = "asha_pat_8",
            pseudonymCode = "AS-KAM-0049",
            nameIndic = "মিনতি হাজৰিকা",
            nameEn = "Minoti Hazarika (Age 69)",
            hamlet = "বেলতলা (Beltola)",
            statusTier = "NORMAL",
            lastVisitedDate = "০২ চেপ্তেম্বৰ, ২০২৬",
            notes = "Daily morning walk completed; calm and responsive."
        ),
        // Elder 9: Synthetic Demo Elder
        DemoAshaRosterItem(
            id = "asha_pat_9",
            pseudonymCode = "AS-KAM-0050",
            nameIndic = "গোলাপ শৰ্মা",
            nameEn = "Golap Sarma (Age 75)",
            hamlet = "জালুকবাৰী (Jalukbari)",
            statusTier = "WATCH",
            lastVisitedDate = "৩০ আগষ্ট, ২০২৬",
            notes = "Requested dietary hydration routine check; resting well."
        ),
        // Elder 10: Synthetic Demo Elder
        DemoAshaRosterItem(
            id = "asha_pat_10",
            pseudonymCode = "AS-KAM-0051",
            nameIndic = "ৰেণুকা ডেকা",
            nameEn = "Renuka Deka (Age 72)",
            hamlet = "মিৰ্জা (Mirza)",
            statusTier = "NORMAL",
            lastVisitedDate = "০১ চেপ্তেম্বৰ, ২০২৬",
            notes = "Traditional loom weaving activity observed; family present."
        ),
        // Elder 11: Synthetic Demo Elder
        DemoAshaRosterItem(
            id = "asha_pat_11",
            pseudonymCode = "AS-KAM-0052",
            nameIndic = "ভূপেন কাকতি",
            nameEn = "Bhupen Kakati (Age 77)",
            hamlet = "পলাশবাৰী (Palashbari)",
            statusTier = "WATCH",
            lastVisitedDate = "২৮ আগষ্ট, ২০২৬",
            notes = "Mild afternoon fatigue noted; caregiver ensuring quiet rest."
        ),
        // Elder 12: Synthetic Demo Elder
        DemoAshaRosterItem(
            id = "asha_pat_12",
            pseudonymCode = "AS-KAM-0053",
            nameIndic = "অনিমা গোস্বামী",
            nameEn = "Anima Goswami (Age 70)",
            hamlet = "সৰ্থেবাৰী (Sarthebari)",
            statusTier = "NORMAL",
            lastVisitedDate = "০৩ চেপ্তেম্বৰ, ২০২৬",
            notes = "Completed routine courtyard stroll; peaceful mood."
        ),
        // Elder 13: Synthetic Demo Elder
        DemoAshaRosterItem(
            id = "asha_pat_13",
            pseudonymCode = "AS-KAM-0054",
            nameIndic = "দিনেস্বৰ মহন্ত",
            nameEn = "Dineswar Mahanta (Age 81)",
            hamlet = "হাজো (Hajo)",
            statusTier = "PRIORITY",
            lastVisitedDate = "২৬ আগষ্ট, ২০২৬",
            notes = "Scheduled monthly vitals check-in; primary caregiver attentive."
        ),
        // Elder 14: Synthetic Demo Elder
        DemoAshaRosterItem(
            id = "asha_pat_14",
            pseudonymCode = "AS-KAM-0055",
            nameIndic = "জোনালী বৈশ্য",
            nameEn = "Jonali Baishya (Age 67)",
            hamlet = "শুৱালকুছি (Sualkuchi)",
            statusTier = "NORMAL",
            lastVisitedDate = "০২ চেপ্তেম্বৰ, ২০২৬",
            notes = "Participating in evening storytelling with grandchildren."
        )
    )

    /**
     * Resolves whether an ASHA roster item has a corresponding Room SQLite patient identity.
     * Only the canonical demo patient (AS-KAM-0042) is backed by Room SQLite.
     * All 13 other synthetic demo elders return null.
     */
    fun resolveRoomPatientId(rosterItem: DemoAshaRosterItem): String? {
        return if (rosterItem.pseudonymCode == DemoPatientConfig.PSEUDONYM_CODE) {
            DemoPatientConfig.PATIENT_ID
        } else {
            null
        }
    }
}
