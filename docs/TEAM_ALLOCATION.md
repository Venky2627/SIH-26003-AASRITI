# AASRITI (SIH-26003) — Team Ownership & Allocation Matrix

> **Core Philosophy**: 6 Equal Contributors. Zero Hierarchy. Vertical Subsystem Ownership.  
> Every subsystem has **ONE Primary Owner** and **ONE Secondary Reviewer** to eliminate knowledge silos and guarantee code review quality.

---

## ðŸ‘¤ 1. VENKATESH

* **Primary Ownership**:
  - Architecture Integration & Repository Health
  - Adaptive Difficulty Engine (`engine/adaptive/`)
  - Priority Engine (`engine/priority/`)
  - AI & Machine Learning Pipeline (`scripts/train_decision_tree.py`, JSON model export)
* **Secondary Ownership / Reviewer Role**:
  - Secondary Reviewer for: **Firebase Sync & SyncQueue** (Jasleen) and **Doctor Workflows / Analytics** (Kimaya)
* **Safe Directories (Normal Working Scope)**:
  - `app/src/main/java/com/sih26003/smritisetu/engine/adaptive/`
  - `app/src/main/java/com/sih26003/smritisetu/engine/priority/`
  - `app/src/main/java/com/sih26003/smritisetu/ml/`
  - `scripts/`
* **Protected Files (Primary Custodian)**:
  - `build.gradle.kts`, `settings.gradle.kts`, `AndroidManifest.xml`
* **Current Task (Pre-Sept 5)**:
  - Finalize PPT System Architecture diagrams and Priority Engine decision matrix.
* **Dependencies**:
  - `domain/model/GameSession.kt`, `domain/model/CareLog.kt`
* **Definition of Done**:
  - Priority Engine rules evaluated deterministically without crashes; PPT architecture diagrams verified.
* **Review Responsibilities**:
  - Reviews PRs from: **Kimaya** (Doctor Workflows & Analytics).

---

## ðŸ‘¤ 2. JASLEEN

* **Primary Ownership**:
  - Core Application Foundation (`DementiaCareApp.kt`)
  - Data Layer & Persistence (`data/local/`, `data/remote/`, `data/mapper/`, `data/repository/`)
  - Room SQLite Database (`DementiaDatabase.kt`)
  - Domain Models & Use Cases (`domain/model/`, `domain/repository/`, `domain/usecase/`)
* **Secondary Ownership / Reviewer Role**:
  - Secondary Reviewer for: **Architecture Integration** (Venkatesh) and **Navigation Graph** (AppNavigation)
* **Safe Directories (Normal Working Scope)**:
  - `app/src/main/java/com/sih26003/smritisetu/data/`
  - `app/src/main/java/com/sih26003/smritisetu/domain/`
  - `backend/firebase/`
* **Protected Files (Primary Custodian)**:
  - `DementiaDatabase.kt` / `AppDatabase.kt`, `AppNavigation.kt`
* **Current Task (Pre-Sept 5)**:
  - Finalize Room-to-Domain mappers and Data Model documentation (`docs/DATA_MODEL.md`).
* **Dependencies**:
  - Android Architecture Components, Kotlin Coroutines, Gson/Serialization.
* **Definition of Done**:
  - Room entities and Domain models strictly separated; zero UI classes directly referencing Room entities.
* **Review Responsibilities**:
  - Reviews PRs from: **Venkatesh** (Engine & Architecture Integration).

---

## ðŸ‘¤ 3. KRISHNA

* **Primary Ownership**:
  - Reusable Game Framework (`feature/games/framework/`, `engine/game/`)
  - Game 1: Family Trivia (`feature/games/familytrivia/`)
  - Game 2: Voice Cue Card (`feature/games/voicecuecard/`)
  - Game 3: Daily Sequencing (`feature/games/sequencing/`)
* **Secondary Ownership / Reviewer Role**:
  - Secondary Reviewer for: **Game Telemetry** (Jasleen) and **Games 4â€“6** (Bhavya)
* **Safe Directories (Normal Working Scope)**:
  - `app/src/main/java/com/sih26003/smritisetu/feature/games/framework/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/games/familytrivia/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/games/voicecuecard/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/games/sequencing/`
  - `app/src/main/java/com/sih26003/smritisetu/voice/`
* **Protected Files**:
  - `CommonGameFramework.kt` / `BaseGameEngine`
* **Current Task (Pre-Sept 5)**:
  - Finalize Game 1â€“3 PPT mechanics slides and game lifecycle state machine specification.
* **Dependencies**:
  - `domain/model/GameSession.kt`, `domain/model/Relationship.kt`
* **Definition of Done**:
  - Unified `BaseGameEngine` round lifecycle (`Instructions` $\rightarrow$ `Playing` $\rightarrow$ `Feedback` $\rightarrow$ `Complete`) documented and verified.
* **Review Responsibilities**:
  - Reviews PRs from: **Jasleen** (Data Layer & Repositories).

---

## ðŸ‘¤ 4. BHAVYA

* **Primary Ownership**:
  - Game 4: Categorisation (`feature/games/categorisation/`)
  - Game 5: Village Market (`feature/games/villagemarket/`)
  - Game 6: Pattern/Object Matching + Mental Rotation (`feature/games/patternrecognition/`)
  - Cultural Theme Engine (`cultural/`: Assam, Manipur, Meghalaya theme packs)
* **Secondary Ownership / Reviewer Role**:
  - Secondary Reviewer for: **Reusable UI Components & Accessibility** (Jasleen/Shravani)
* **Safe Directories (Normal Working Scope)**:
  - `app/src/main/java/com/sih26003/smritisetu/feature/games/categorisation/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/games/villagemarket/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/games/patternrecognition/`
  - `app/src/main/java/com/sih26003/smritisetu/cultural/`
  - `assets/cultural/`
* **Protected Files**:
  - `Theme.kt`, `colors.xml`
* **Current Task (Pre-Sept 5)**:
  - Document Cultural Theme Engine architecture and NER asset specifications for PPT.
* **Dependencies**:
  - `CommonGameFramework.kt`, `assets/cultural/`
* **Definition of Done**:
  - Cultural theme architecture decoupled from UI language strings; Games 4â€“6 mechanics verified.
* **Review Responsibilities**:
  - Reviews PRs from: **Krishna** (Game Framework & Games 1â€“3).

---

## ðŸ‘¤ 5. SHRAVANI

* **Primary Ownership**:
  - Caregiver Workflows (`feature/caregiver/`: Patient Overview, Today's Priorities, Quick Log)
  - ASHA / Community Health Worker Workflows (`feature/asha/`: Shared device, Multi-patient switch)
  - Offline Reminder Engine (`engine/reminder/`, `feature/reminders/`)
  - Emergency Toolkit & SOS Protocol (`feature/caregiver/sos/`)
  - Memory Album Feature (`feature/memoryalbum/`)
* **Secondary Ownership / Reviewer Role**:
  - Secondary Reviewer for: **Cultural Content Integration** (Bhavya)
* **Safe Directories (Normal Working Scope)**:
  - `app/src/main/java/com/sih26003/smritisetu/feature/caregiver/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/asha/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/reminders/`
  - `app/src/main/java/com/sih26003/smritisetu/engine/reminder/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/memoryalbum/`
* **Protected Files**:
  - `ReminderScheduler.kt`, `BootReceiver.kt`
* **Current Task (Pre-Sept 5)**:
  - Finalize Caregiver & ASHA workflow wireframes and SOS triage flow for PPT presentation.
* **Dependencies**:
  - `domain/model/Patient.kt`, `domain/model/Reminder.kt`, `domain/model/CareLog.kt`
* **Definition of Done**:
  - ASHA shared-device flow clearly separated from individual Caregiver workflow; offline reminder state diagram frozen.
* **Review Responsibilities**:
  - Reviews PRs from: **Bhavya** (Games 4â€“6 & Cultural Theme Engine).

---

## ðŸ‘¤ 6. KIMAYA

* **Primary Ownership**:
  - Doctor / Clinician Workflows (`feature/doctor/`: Patient Snapshot, Since Last Visit)
  - Longitudinal Analytics & Trend Engine (`engine/trend/`, `feature/analytics/`)
  - 7 / 30 / 90 Day Trend Signal Visualization (Reaction time, errors, adherence)
  - Explainable Referral Prompt Logic ("Consider clinical review")
  - Clinician PDF Summary Generation (`docs/`, `android.graphics.pdf`)
* **Secondary Ownership / Reviewer Role**:
  - Secondary Reviewer for: **Testing & Documentation** (All)
* **Safe Directories (Normal Working Scope)**:
  - `app/src/main/java/com/sih26003/smritisetu/feature/doctor/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/analytics/`
  - `app/src/main/java/com/sih26003/smritisetu/engine/trend/`
  - `docs/`
* **Protected Files**:
  - `DoctorAccessScreen.kt`
* **Current Task (Pre-Sept 5)**:
  - Finalize Doctor Dashboard wireframes and 7/30/90 day longitudinal trend charts for PPT presentation.
* **Dependencies**:
  - `domain/model/GameSession.kt`, `domain/model/CareLog.kt`, `domain/model/DoctorAccess.kt`
* **Definition of Done**:
  - Neutral functional signal reporting defined without medical diagnosis claims; 1-page clinician PDF format approved.
* **Review Responsibilities**:
  - Reviews PRs from: **Shravani** (Caregiver & ASHA Workflows).

---

## ðŸ¤– AI AGENT ROUTING DIRECTIVE

```text
IF CURRENT DEVELOPER = <NAME>
THEN:
  1. Read docs/PROJECT_STATE.md to verify current project phase and blockers.
  2. Read <NAME>'s section in docs/TEAM_ALLOCATION.md.
  3. Search docs/TASK_BOARD.md for highest priority unfinished task assigned to <NAME>.
  4. Verify all dependencies for that task are satisfied.
  5. Recommend EXACTLY ONE primary task (and optionally one secondary task).
  6. DO NOT modify protected files without explicit justification and warning.
  7. Enforce that all modified code remains strictly within <NAME>'s safe directories.
  8. Update docs/TASK_BOARD.md and docs/PROJECT_STATE.md upon completion.
```

