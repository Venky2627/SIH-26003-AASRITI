# SIH26003 (SmritiSetu) — Problem Statement Coverage Matrix

> **Audit Standard**: A feature is classified as **REAL** only with working UI, logic, data flow, and local persistence. **PARTIAL** indicates some requirements exist. **MISSING** indicates unbuilt functionality. No feature is claimed without repository evidence.

---

## 📋 Comprehensive Feature Audit & Allocation Matrix

| Official PS Requirement | Existing State | Target Specification | Priority | Responsible Member | Dependencies |
| :--- | :---: | :--- | :---: | :--- | :--- |
| **Patient Authentication (No PIN)** | **REAL** | Photo/avatar tap card select; zero PIN barrier | P0 | Bhavya | Room `patients` table |
| **Caregiver Authentication (Local PIN)** | **REAL** | 6-digit local PIN; SHA-256 hashed in Room | P0 | Venkatesh | Room `users` table, `CryptoUtils` |
| **Doctor Access (Access Code)** | **REAL** | Caregiver-generated 6-digit code; revocable | P0 | Venkatesh | Room `doctor_access` table |
| **Offline-First Source of Truth** | **REAL** | Room SQLite `smritisetu.db`; 100% offline operational | P0 | Venkatesh | Android Architecture Components |
| **Secondary Cloud Sync (Firebase)** | **PARTIAL** | Opportunistic queue processor; Firestore push is stubbed | P2 | Venkatesh | Room `sync_queue`, ConnectivityManager |
| **Common Game Framework** | **REAL** | `BaseGameEngine` + `PerformanceCollector` | P0 | Venkatesh | Room `game_sessions` |
| **Game 1: Family Trivia** | **REAL** | Levels 1–5; binds to patient relationships | P0 | Jasleen | `BaseGameEngine`, `RelationshipEntity` |
| **Game 2: Voice Cue Card** | **REAL** | Levels 1–5; audio cue, 5-sec voice fallback | P0 | Krishna | `BaseGameEngine`, `VoicePromptManager` |
| **Game 3: Daily Sequencing** | **REAL** | Levels 1–5; Assam tea & Namghar routines | P0 | Kimaya | `BaseGameEngine` |
| **Game 4: Categorisation** | **REAL** | Levels 1–5; fruits, vegetables, animals, textiles | P0 | Kimaya | `BaseGameEngine` |
| **Game 5: Village Market** | **REAL** | Levels 1–5; authentic NER goods recall | P0 | Kimaya | `BaseGameEngine` |
| **Game 6: Pattern Recognition** | **REAL** | Levels 1–5; shape/color sequence completion | P0 | Kimaya | `BaseGameEngine` |
| **On-Device Adaptive ML** | **REAL** | Decision Tree classifier; local JSON runtime | P0 | Venkatesh | `scripts/train_decision_tree.py` |
| **Shared Voice System** | **REAL** | Offline TTS ($0.85\times$) + language packs (`as`, `en`) | P0 | Krishna | Android TextToSpeech |
| **Personalization Data Binding** | **REAL** | Family names/photos in Room loaded by Trivia | P0 | Jasleen | Room `relationships` |
| **Reminiscence M1: Memory Album** | **MISSING** | Photo album viewing with voice recordings | P1 | Jasleen | Room `MemoryItemEntity` |
| **Reminiscence M2: Familiar Audio** | **MISSING** | Traditional music & folk tunes audio player | P1 | Krishna | Android MediaPlayer / local assets |
| **Reminiscence M3: Festival/Food** | **MISSING** | Bihu & traditional food conversational prompts | P1 | Jasleen | Local cultural assets |
| **Caregiver: Today's Priority** | **MISSING** | Deterministic engine outputting max 3 priorities | P1 | Shravani | Room medications, appointments, logs |
| **Caregiver: Quick Log (<30s)** | **MISSING** | Fast logger for meds, appetite, sleep, falls | P1 | Shravani | Room `CareLogEntity` |
| **Caregiver: Weekly Overview** | **PARTIAL** | Basic metrics shown; aggregated trends missing | P1 | Shravani | Room `game_sessions`, `care_logs` |
| **Medication Management** | **PARTIAL** | Reminder exists; full dose/schedule entity missing | P1 | Shravani | Room `MedicationEntity`, AlarmManager |
| **Appointments Management** | **PARTIAL** | Reminder type exists; full calendar entity missing | P1 | Shravani | Room `AppointmentEntity` |
| **Caregiver Emergency Toolkit** | **MISSING** | Emergency call, missing card, fall triage tips | P1 | Bhavya | Android Intent / Contacts |
| **Caregiver Wellbeing Check-in** | **MISSING** | Weekly stress check-in & local trend | P2 | Bhavya | Room `CaregiverWellbeingEntity` |
| **Safety Screening & Routing** | **MISSING** | Screening flags routing to supervised mode | P1 | Bhavya | Room `SafetyScreeningEntity` |
| **Consent Management** | **MISSING** | Explicit checkboxes for participation, cloud, doctor | P1 | Bhavya | Room `ConsentEntity` |
| **Clinician Weekly Summary** | **PARTIAL** | Sessions viewable; longitudinal aggregation missing | P1 | Venkatesh | Room `game_sessions`, `care_logs` |
| **Clinician Referral Prompts** | **MISSING** | Explainable prompts ("Consider clinical review") | P1 | Venkatesh | Rule-based engine |
| **Clinician 1-Page PDF Export** | **MISSING** | Native `PdfDocument` generation stored locally | P2 | Venkatesh | Android `android.graphics.pdf` |

---

## 🎯 Audit Summary & Execution Priorities

1. **Foundational Core (REAL)**:
   - Native Android (Kotlin + Jetpack Compose) architecture is locked.
   - All 6 cognitive games are playable with physical metrics and on-device Decision Tree difficulty adaptation.
   - Room SQLite database (`smritisetu.db`) is operational as the single source of truth.
   - Local PIN authentication for Caregivers and Doctors is functional; Patient mode requires zero PIN.

2. **Phase 2 Expansion Targets (Documented for Implementation)**:
   - **Reminiscence Suite**: Memory Album, Familiar Music, Cultural Stories (Jasleen & Krishna).
   - **Caregiver Operations**: Today's Priority Engine, Quick Log, Emergency Toolkit, Medication Schedule (Shravani & Bhavya).
   - **Safety & Consent**: Pre-session safety screening and explicit consent management (Bhavya).
   - **Clinician Operations**: Explainable referral prompts and local 1-page PDF export (Venkatesh).
