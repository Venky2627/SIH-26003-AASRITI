# SmritiSetu (SIH26003) — AI Assistant Context & Developer Directive

> This document serves as the primary system context for AI pair-programming assistants (including Gemini, Claude, and Copilot) operating on the SmritiSetu repository.

---

## 🏛️ Project Identity & Mission
* **Project Code**: SIH26003
* **Repository**: [https://github.com/TeamNameSIH/AI-Based-Cognitive-Gaming-Platform-for-Elderly-Dementia-Patients-in-NER](https://github.com/TeamNameSIH/AI-Based-Cognitive-Gaming-Platform-for-Elderly-Dementia-Patients-in-NER)
* **Problem Statement**: AI-Based Cognitive Gaming and Memory Assistance Platform for Elderly Dementia Patients in North Eastern Region (NER) of India.
* **Sponsoring Ministry**: Ministry of Development of North Eastern Region (MDoNER), Government of India.
* **Target Demographics**: Elderly individuals aged 60+ exhibiting Mild Cognitive Impairment (MCI) and early dementia in rural Assam, Manipur, Mizoram, and Nagaland. Caregivers (family members).

---

## 🛑 Strict Engineering Directives for AI Assistants

### 1. Offline-First is Absolute Law ("ROOM IS KING")
* NEVER write code that assumes an active internet connection.
* Local SQLite via **Room Database** is the single source of truth.
* Firebase is used ONLY for background opportunistic backup, sharing, and sync.
* Adaptive difficulty uses local **Decision Tree Classifier** in Kotlin (`assets/ml/decision_tree_difficulty.json`). Zero cloud inference.

### 2. Strict Healthcare Privacy & DPDA 2023
* NEVER save raw microphone audio files (`.wav`, `.mp3`) to disk storage.
* Audio streams must be processed directly from volatile memory and explicitly zeroed out.
* Caregivers and Doctors use local 6-digit PINs (SHA-256 hashed in Room).
* Patients use **NO PIN** (direct photo/avatar card select). **NO OTP/SMS**.

### 3. Elderly & Dementia Accessibility (WCAG 2.2 AAA)
* All interactive touch targets must be at least `60x60 dp` (prefer `64x64 dp`).
* Contrast ratios between text and background must exceed `7:1`.
* Large, calm tactile buttons with visual icons and localized text.
* Provide audio prompts in Assamese (`as`) and English (`en`).

### 4. Codebase Patterns
* **Platform**: Native Android with Kotlin 1.9+ and Jetpack Compose.
* **Database**: Room 2.6.1 with KSP compiler.
* **Games**: 6 first-class playable games on a unified `BaseGameEngine`.

---

## 📂 Repository Directory Layout

```
SIH26003/
├── app/
│   ├── src/main/java/com/sih26003/smritisetu/
│   │   ├── feature/games/    # Common framework + 6 games
│   │   ├── feature/auth/     # Local PIN & Patient photo select
│   │   ├── feature/patient/  # Large-target patient dashboard
│   │   ├── feature/caregiver/# Profile & doctor access code management
│   │   ├── feature/doctor/   # Longitudinal game signal review
│   │   ├── feature/reminders/# Exact offline AlarmManager scheduling
│   │   ├── data/local/       # Room database, 7 entities, 7 DAOs
│   │   ├── ml/               # On-device Decision Tree engine
│   │   └── voice/            # Voice prompts & language packs
│   └── src/main/assets/      # Local ML tree JSON & language packs
├── assets/                   # Master ML model & language packs
├── docs/                     # Architecture, Room schema, game specs
└── scripts/                  # train_decision_tree.py
```

---

## ⚡ Routine Terminal Commands

```bash
# Export on-device ML model
python scripts/train_decision_tree.py

# Build Android APK
./gradlew assembleDebug

# Run Unit Tests
./gradlew testDebugUnitTest
```
