package com.sih26003.aasriti.demo

import androidx.compose.ui.graphics.Color
import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens

/**
 * Authoritative in-memory fictional demo dataset for SIH presentation & walkthrough.
 * Strictly isolated from production Room SQLite persistence.
 */

data class DemoPatient(
    val id: String = "aita_borah_01",
    val pseudonymCode: String = "AS-KAM-0042",
    val displayName: String = "আইতা বৰা",
    val displaySubtitle: String = "Aita Borah • 68 Years",
    val villageLocation: String = "Kamrup Rural, Assam",
    val primaryLanguage: String = "as",
    val cognitiveStage: String = "Mild Cognitive Impairment (MCI)"
)

data class DemoRoutineItem(
    val id: String,
    val titleIndic: String,
    val titleEn: String,
    val timeLabel: String,
    val isMedicine: Boolean = false
)

data class DemoMemoryItem(
    val id: String,
    val titleIndic: String,
    val titleEn: String,
    val locationTag: String,
    val yearTag: String,
    val storyIndic: String,
    val storyEn: String,
    val relativeNarrator: String,
    val audioDurationSec: Int = 18
)

data class DemoFamilyContact(
    val id: String,
    val name: String,
    val relationIndic: String,
    val relationEn: String,
    val phoneMasked: String,
    val isPrimaryCaregiver: Boolean = false,
    val accentColor: Color = AasritiColorTokens.DeepNortheastForest
)

data class DemoFlowerCard(
    val id: Int,
    val nameIndic: String,
    val nameEn: String,
    val botanicalFamily: String,
    val primaryColor: Color,
    val secondaryColor: Color
)

data class DemoTrendPoint(
    val label: String,
    val reactionTimeMs: Int,
    val hesitationGaps: Int,
    val adherencePercent: Int
)

data class DemoAshaRosterItem(
    val id: String,
    val pseudonymCode: String,
    val nameIndic: String,
    val nameEn: String,
    val hamlet: String,
    val statusTier: String, // "NORMAL", "WATCH", "PRIORITY"
    val lastVisitedDate: String,
    val notes: String
)

object AasritiDemoData {
    val patient = DemoPatient()

    val initialRoutines = listOf(
        DemoRoutineItem(
            id = "routine_1",
            titleIndic = "পুৱাৰ ৰক্তচাপৰ ঔষধ",
            titleEn = "Morning Blood Pressure Medicine",
            timeLabel = "০৮:০০ AM",
            isMedicine = true
        ),
        DemoRoutineItem(
            id = "routine_2",
            titleIndic = "এগিলাচ কুহুমীয়া পানী",
            titleEn = "Warm Glass of Water",
            timeLabel = "১০:৩০ AM",
            isMedicine = false
        ),
        DemoRoutineItem(
            id = "routine_3",
            titleIndic = "ফুলনি বাৰীৰ ফুৰা",
            titleEn = "Gentle Garden Stroll",
            timeLabel = "০৪:৩০ PM",
            isMedicine = false
        )
    )

    val memories = listOf(
        DemoMemoryItem(
            id = "mem_tea_garden",
            titleIndic = "পৰিয়ালৰ চাহ বাগিচা",
            titleEn = "Our Family Tea Garden",
            locationTag = "সোণাৰি, অসম (Sonari, Assam)",
            yearTag = "২০২৩",
            storyIndic = "যোৱা বছৰ আমি সকলোৱে একেলগে চাহ বাৰীত গৈছিলো। আইতাই সেউজীয়া পাতবোৰ স্পৰ্শ কৰি বৰ আনন্দ পাইছিল।",
            storyEn = "Last year our entire family visited the lush tea estate. Mother found deep peace touching the tender green leaves.",
            relativeNarrator = "মীৰা বৰা (Mira — Daughter)"
        ),
        DemoMemoryItem(
            id = "mem_bihu_celebration",
            titleIndic = "নাতি-নাতিনীৰ সৈতে ব'হাগ বিহু",
            titleEn = "Rongali Bihu with Grandchildren",
            locationTag = "কামৰূপ (Kamrup)",
            yearTag = "২০২৪",
            storyIndic = "বিহুৰ দিনা নাতি অংকুৰে ঢোল বজাইছিল আৰু আইতাই পিঠা-পনা তৈয়াৰ কৰি সকলোকে খুৱাইছিল।",
            storyEn = "On Bihu day, grandson Ankur played the dhol, and mother delighted everyone by preparing fresh traditional pitha.",
            relativeNarrator = "ৰোহন বৰা (Rohan — Son)"
        ),
        DemoMemoryItem(
            id = "mem_namghar_prayer",
            titleIndic = "নামঘৰৰ সেৱা আৰু বৰগীত",
            titleEn = "Namghar Visit & Borgeet",
            locationTag = "মাজুলী (Majuli)",
            yearTag = "২০২২",
            storyIndic = "নামঘৰৰ শান্ত পৰিৱেশত শংকৰদেৱৰ বৰগীত শুনি আইতাৰ মন প্ৰশান্তিৰে ভৰি পৰিছিল।",
            storyEn = "Listening to melodic Borgeet prayers in the serene Namghar brought deep calm and spiritual reassurance.",
            relativeNarrator = "মীৰা বৰা (Mira — Daughter)"
        )
    )

    val familyContacts = listOf(
        DemoFamilyContact(
            id = "contact_mira",
            name = "মীৰা বৰা (Mira Borah)",
            relationIndic = "জীয়াৰী (মুখ্য যত্ন লওঁতা)",
            relationEn = "Daughter • Primary Caregiver",
            phoneMasked = "+91 98765 43210",
            isPrimaryCaregiver = true,
            accentColor = AasritiColorTokens.DeepNortheastForest
        ),
        DemoFamilyContact(
            id = "contact_rohan",
            name = "ৰোহন বৰা (Rohan Borah)",
            relationIndic = "পুত্ৰ",
            relationEn = "Son",
            phoneMasked = "+91 94350 12345",
            isPrimaryCaregiver = false,
            accentColor = AasritiColorTokens.MugaGold
        ),
        DemoFamilyContact(
            id = "contact_asha",
            name = "হেমলতা ডেকা (Hemlata Deka)",
            relationIndic = "আশা কৰ্মী (স্বাস্থ্য সহায়ক)",
            relationEn = "ASHA Community Worker",
            phoneMasked = "+91 91012 67890",
            isPrimaryCaregiver = false,
            accentColor = AasritiColorTokens.WarmSlate
        )
    )

    // Regional NER Flowers for Flower Match
    val flowerCards = listOf(
        DemoFlowerCard(
            id = 1,
            nameIndic = "কপৌ ফুল",
            nameEn = "Kopou Phool (Foxtail Orchid)",
            botanicalFamily = "Rhynchostylis retusa • Assam State Flower",
            primaryColor = Color(0xFFC2185B),
            secondaryColor = Color(0xFFF8BBD0)
        ),
        DemoFlowerCard(
            id = 2,
            nameIndic = "তগৰ ফুল",
            nameEn = "Tagar Phool (Crape Jasmine)",
            botanicalFamily = "Tabernaemontana divaricata • Sacred White Flower",
            primaryColor = Color(0xFF245C45),
            secondaryColor = Color(0xFFE8F5E9)
        ),
        DemoFlowerCard(
            id = 3,
            nameIndic = "জবা ফুল",
            nameEn = "Jaba Phool (Hibiscus)",
            botanicalFamily = "Hibiscus rosa-sinensis • Traditional Courtyard Flower",
            primaryColor = Color(0xFFD32F2F),
            secondaryColor = Color(0xFFFFCDD2)
        ),
        DemoFlowerCard(
            id = 4,
            nameIndic = "নীলকমল",
            nameEn = "Nilkamal (Blue Water Lily)",
            botanicalFamily = "Nymphaea nouchali • Regional Wetland Flora",
            primaryColor = Color(0xFF1976D2),
            secondaryColor = Color(0xFFBBDEFB)
        )
    )

    // 7-day longitudinal telemetry data points for Doctor screen
    val longitudinalTrends = listOf(
        DemoTrendPoint(label = "সোম (Mon)", reactionTimeMs = 2800, hesitationGaps = 2, adherencePercent = 100),
        DemoTrendPoint(label = "মঙ্গল (Tue)", reactionTimeMs = 2650, hesitationGaps = 1, adherencePercent = 100),
        DemoTrendPoint(label = "বুধ (Wed)", reactionTimeMs = 2900, hesitationGaps = 2, adherencePercent = 100),
        DemoTrendPoint(label = "বৃহ (Thu)", reactionTimeMs = 3100, hesitationGaps = 3, adherencePercent = 85),
        DemoTrendPoint(label = "শুক্ৰ (Fri)", reactionTimeMs = 2750, hesitationGaps = 1, adherencePercent = 100),
        DemoTrendPoint(label = "শনি (Sat)", reactionTimeMs = 2600, hesitationGaps = 1, adherencePercent = 100),
        DemoTrendPoint(label = "দেও (Sun)", reactionTimeMs = 2500, hesitationGaps = 0, adherencePercent = 100)
    )

    // ASHA Multi-Patient Community Roster
    val ashaRoster = listOf(
        DemoAshaRosterItem(
            id = "asha_pat_1",
            pseudonymCode = "AS-KAM-0042",
            nameIndic = "আইতা বৰা",
            nameEn = "Aita Borah (Age 68)",
            hamlet = "উত্তৰ বৰকুছি (North Barkuchi)",
            statusTier = "WATCH",
            lastVisitedDate = "০২ চেপ্তেম্বৰ, ২০২৬",
            notes = "Mild hydration delay noted; stable orientation and mood."
        ),
        DemoAshaRosterItem(
            id = "asha_pat_2",
            pseudonymCode = "AS-KAM-0043",
            nameIndic = "বীৰেন দাস",
            nameEn = "Biren Das (Age 74)",
            hamlet = "দক্ষিণ বৰকুছি (South Barkuchi)",
            statusTier = "NORMAL",
            lastVisitedDate = "০১ চেপ্তেম্বৰ, ২০২৬",
            notes = "Adherence 100%; completed morning Namghar walk routine."
        ),
        DemoAshaRosterItem(
            id = "asha_pat_3",
            pseudonymCode = "AS-KAM-0044",
            nameIndic = "হেমলতা দেৱী",
            nameEn = "Hemolata Devi (Age 71)",
            hamlet = "বজাৰ চ'ক (Bazar Chowk)",
            statusTier = "PRIORITY",
            lastVisitedDate = "২৮ আগষ্ট, ২০২৬",
            notes = "Missed evening blood pressure dose; family contacted."
        )
    )
}
