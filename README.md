# SmritiSetu (SIH26003) — AI-Based Cognitive Gaming & Memory Assistance Platform

[![Platform](https://img.shields.io/badge/Platform-Android%20Native-brightgreen.svg)](https://developer.android.com/)
[![Language](https://img.shields.io/badge/Language-Kotlin%201.9-blue.svg)](https://kotlinlang.org/)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-purple.svg)](https://developer.android.com/jetpack/compose)
[![Database](https://img.shields.io/badge/Database-Room%20%2F%20SQLite-orange.svg)](https://developer.android.com/training/data-storage/room)
[![Edge ML](https://img.shields.io/badge/Adaptive%20ML-On--Device%20Decision%20Tree-yellow.svg)](https://scikit-learn.org/)
[![Accessibility](https://img.shields.io/badge/Accessibility-WCAG%202.2%20AAA-success.svg)](https://www.w3.org/WAI/standards-guidelines/wcag/)
[![Offline](https://img.shields.io/badge/Offline--First-100%25%20Autonomous-blueviolet.svg)](#offline-architecture)

> **Sponsoring Ministry**: Ministry of Development of North Eastern Region (MDoNER), Government of India.  
> **Problem Statement**: AI-Based Cognitive Gaming and Memory Assistance Platform for Elderly Dementia Patients in North Eastern Region (NER) of India.  
> **Repository**: [https://github.com/TeamNameSIH/AI-Based-Cognitive-Gaming-Platform-for-Elderly-Dementia-Patients-in-NER](https://github.com/TeamNameSIH/AI-Based-Cognitive-Gaming-Platform-for-Elderly-Dementia-Patients-in-NER)

---

## 🏛️ 1. Locked Architecture: Local First, Cloud Second

> **"ROOM IS KING. FIREBASE IS THE MESSENGER."**

SmritiSetu is engineered specifically for elderly patients with Mild Cognitive Impairment (MCI) and early dementia living in rural North Eastern India (Assam, Manipur, Mizoram, Nagaland). The architecture is strictly locked:

1. **Android Native**: Built with Kotlin and Jetpack Compose for low-end Android hardware.
2. **Room Database (Source of Truth)**: All patient records, family relationships, game performance signals, and reminders are committed to local SQLite via Room immediately.
3. **Firebase (Secondary Messenger Only)**: Used exclusively for optional background backup, cross-device sharing, and doctor telemetry. Gameplay **never** waits for network round-trips.
4. **Local PIN Authentication**:
   - **Caregiver & Doctor**: Local 6-digit PIN hashed with SHA-256 in Room.
   - **Patient**: **NO PIN**. Elderly patients enter Patient Mode by tapping their photo card.
   - **NO OTP**: SMS/OTP login flows are entirely excluded.
5. **On-Device Decision Tree Adaptive ML**: Scikit-Learn trained Decision Tree classifier exported to JSON and interpreted locally in Kotlin. Evaluates accuracy, errors, reaction time, and hesitation to recommend difficulty levels (1–5). **Zero cloud inference**.
6. **Shared Voice Application Layer**: Pre-recorded local language packs (Assamese, Manipuri, Bodo, Hindi, English) with on-device TextToSpeech. Voice is a common horizontal service, **NOT** a seventh game.

---

## 🎮 2. The Six Playable Games

All six games inherit from the unified `BaseGameEngine` framework with the exact lifecycle:  
`GAME SELECT` $\rightarrow$ `LOAD PATIENT SETTINGS` $\rightarrow$ `VOICE + VISUAL INSTRUCTIONS` $\rightarrow$ `GAMEPLAY` $\rightarrow$ `COLLECT PERFORMANCE METRICS` $\rightarrow$ `RUN ADAPTIVE DIFFICULTY` $\rightarrow$ `SAVE TO ROOM` $\rightarrow$ `FEEDBACK` $\rightarrow$ `NEXT ROUND`.

| Game # | Name | Clinical Focus | Interaction & Content |
| :---: | :--- | :--- | :--- |
| **1** | **Family Trivia** | Memory & Personal Identity | Personalised family photographs, names, and relationships loaded from Room. |
| **2** | **Voice Cue Card** | Memory & Attention | Local voice prompt plays item; patient taps matching visual card. |
| **3** | **Daily Sequencing** | Executive Function & Memory | Step-by-step familiar routines (e.g. preparing traditional Assam milk tea). |
| **4** | **Categorisation** | Attention & Executive Function | Grouping items into large tactile categories (fruits, vegetables, handlooms). |
| **5** | **Village Market** | Working Memory & Attention | Shopping memory recall featuring authentic NER market goods (Joha rice, Kaji nemu). |
| **6** | **Pattern Recognition** | Visuospatial Processing & Speed | Color and shape sequence prediction with high contrast visual motifs. |

---

## 📂 3. Repository Directory Blueprint

```text
SIH26003/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── assets/
│       │   │   ├── ml/decision_tree_difficulty.json
│       │   │   └── language-packs/
│       │   ├── java/com/sih26003/smritisetu/
│       │   │   ├── SmritiSetuApplication.kt
│       │   │   ├── MainActivity.kt
│       │   │   ├── core/
│       │   │   │   ├── security/CryptoUtils.kt
│       │   │   ├── data/
│       │   │   │   ├── local/
│       │   │   │   │   ├── entities/Entities.kt
│       │   │   │   │   ├── dao/Daos.kt
│       │   │   │   │   └── database/AppDatabase.kt
│       │   │   │   ├── repository/Repositories.kt
│       │   │   │   └── firebase/FirebaseSyncMessenger.kt
│       │   │   ├── feature/
│       │   │   │   ├── auth/AuthScreens.kt
│       │   │   │   ├── patient/PatientHomeScreen.kt
│       │   │   │   ├── caregiver/CaregiverDashboardScreen.kt
│       │   │   │   ├── doctor/DoctorAccessScreen.kt
│       │   │   │   ├── reminders/
│       │   │   │   │   ├── ReminderScheduler.kt
│       │   │   │   │   └── RemindersScreen.kt
│       │   │   │   └── games/
│       │   │   │       ├── framework/CommonGameFramework.kt
│       │   │   │       ├── familytrivia/FamilyTriviaGame.kt
│       │   │   │       ├── voicecuecard/VoiceCueCardGame.kt
│       │   │   │       ├── sequencing/SequencingGame.kt
│       │   │   │       ├── categorisation/CategorisationGame.kt
│       │   │   │       ├── villagemarket/VillageMarketGame.kt
│       │   │   │       └── patternrecognition/PatternRecognitionGame.kt
│       │   │   ├── ml/
│       │   │   │   ├── model/DecisionTreeNode.kt
│       │   │   │   └── inference/DecisionTreeEngine.kt
│       │   │   └── voice/
│       │   │       ├── playback/VoicePromptManager.kt
│       │   │       └── packs/LanguagePack.kt
│       │   └── res/
│       └── test/java/com/sih26003/smritisetu/CoreEngineTests.kt
├── assets/
│   ├── ml/decision_tree_difficulty.json
│   └── language-packs/
├── docs/
│   ├── architecture/LOCKED_ARCHITECTURE.md
│   ├── games/SIX_GAMES_SPECIFICATION.md
│   ├── data-model/ROOM_SCHEMA.md
│   └── testing/OFFLINE_VERIFICATION_CHECKLIST.md
├── scripts/
│   └── train_decision_tree.py
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## ⚡ 4. Build & Verification Commands

```bash
# 1. Train and export the on-device Decision Tree JSON
python scripts/train_decision_tree.py

# 2. Build Android Debug APK
./gradlew assembleDebug

# 3. Execute Core Unit Tests
./gradlew testDebugUnitTest

# 4. Install onto connected Android device or emulator
./gradlew installDebug
```

---

## 🔒 5. Healthcare Compliance & Ethical Safety
- **Non-Diagnostic Policy**: Game performance metrics measure reaction times and game accuracy. The application **never** generates a dementia diagnosis or disease severity verdict.
- **DPDA 2023 Compliant**: Patient identities use non-PII pseudonyms (e.g., `AS-KAM-0042`). Raw audio is never written to disk storage.
