# AASRITI — CODE OWNERSHIP

## Global Rule

Each contributor primarily works inside their assigned ownership boundaries.

Before editing shared/core files:

STOP.

Inspect current `develop`.

Determine whether the change is genuinely required.

Do not casually modify shared files.

---

## Venkatesh

Role: Lead Integrator / Core Architecture  
Assigned Branches: `feature/venkatesh/backend-engines-security`, `feature/venkatesh/integration`

Primary ownership:

- Core architecture & pipeline integrity
- Engine integration (`engine/`)
- Room SQLite database integration & contracts
- Repository contracts & interfaces (`data/repository/`)
- Integration testing (`src/test/`)
- Core navigation routing in `MainActivity.kt`
- Release verification & governance adherence

Protected and shared integration files:

- `app/src/main/java/com/sih26003/smritisetu/MainActivity.kt`
- `app/src/main/java/com/sih26003/smritisetu/demo/DemoPatientConfig.kt`
- `app/src/main/java/com/sih26003/smritisetu/data/local/database/AppDatabase.kt`
- `app/src/main/java/com/sih26003/smritisetu/data/local/entities/Entities.kt`
- `app/src/main/java/com/sih26003/smritisetu/data/local/dao/Daos.kt`
- `app/src/main/java/com/sih26003/smritisetu/data/repository/Repositories.kt`
- `app/src/main/java/com/sih26003/smritisetu/engine/adaptive/AdaptiveEngine.kt`
- `app/src/main/java/com/sih26003/smritisetu/engine/trend/TrendEngine.kt`
- `app/src/main/java/com/sih26003/smritisetu/engine/priority/PriorityEngine.kt`
- `app/src/main/java/com/sih26003/smritisetu/engine/orchestrator/CognitiveInsightOrchestrator.kt`
- `app/src/main/java/com/sih26003/smritisetu/core/security/CryptoUtils.kt`

---

## Jasleen

Role: Backend Data, Sync & Persistence Engineer  
Assigned Branch: `feature/jasleen/backend-data-sync`

Primary ownership:

- Room SQLite DAOs and query implementations:
  `app/src/main/java/com/sih26003/smritisetu/data/local/dao/Daos.kt`
- Sync queue contracts and data persistence logic:
  `app/src/main/java/com/sih26003/smritisetu/data/repository/Repositories.kt`
- Background offline reminder scheduling:
  `app/src/main/java/com/sih26003/smritisetu/feature/reminders/ReminderScheduler.kt`
- Room schema migrations and SQLite performance in coordination with Lead Integrator

---

## Krishna

Role: Game Framework & Core Cognitive Game Engineer  
Assigned Branch: `feature/krishna/frontend-games-framework`

Primary ownership:

- Base game engine lifecycle and contracts:
  `app/src/main/java/com/sih26003/smritisetu/feature/games/framework/CommonGameFramework.kt`
- Physical telemetry and hesitation tracking:
  `app/src/main/java/com/sih26003/smritisetu/feature/games/framework/PerformanceCollector.kt`
- Game definitions and metadata:
  `app/src/main/java/com/sih26003/smritisetu/feature/games/framework/GameId.kt`
- Core cognitive game implementations:
  - Family Trivia: `app/src/main/java/com/sih26003/smritisetu/feature/games/familytrivia/FamilyTriviaGame.kt`
  - Sequencing: `app/src/main/java/com/sih26003/smritisetu/feature/games/sequencing/SequencingGame.kt`
  - Pattern Recognition: `app/src/main/java/com/sih26003/smritisetu/feature/games/patternrecognition/PatternRecognitionGame.kt`

---

## Bhavya

Role: Cultural Adaptation & Heritage Game Engineer  
Assigned Branch: `feature/bhavya/frontend-cultural-games`

Primary ownership:

- Culturally grounded Northeast Indian game mechanics:
  - Flower Match: `app/src/main/java/com/sih26003/smritisetu/feature/games/flowermatch/FlowerMatchGame.kt`
  - Village Market: `app/src/main/java/com/sih26003/smritisetu/feature/games/villagemarket/VillageMarketGame.kt`
  - Categorisation: `app/src/main/java/com/sih26003/smritisetu/feature/games/categorisation/CategorisationGame.kt`
  - Voice Cue Card: `app/src/main/java/com/sih26003/smritisetu/feature/games/voicecuecard/VoiceCueCardGame.kt`
- Master UI theme styling, color tokens, and accessibility contrast tokens:
  `app/src/main/java/com/sih26003/smritisetu/core/ui/theme/`

---

## Shravani

Role: Caregiver, ASHA & Routine UI Engineer  
Assigned Branch: `feature/shravani/frontend-caregiver-asha`

Primary ownership:

- Caregiver dashboard, quick log dialogs, and triage actions:
  `app/src/main/java/com/sih26003/smritisetu/feature/caregiver/CaregiverDashboardScreen.kt`
- ASHA community health worker roster and patient tracking UI:
  `app/src/main/java/com/sih26003/smritisetu/feature/asha/AshaDashboardScreen.kt`
- Daily routines and reminders UI:
  `app/src/main/java/com/sih26003/smritisetu/feature/reminders/RemindersScreen.kt`
- Reactive bindings to `CareLogRepository` and `PriorityEngine`

---

## Kimaya

Role: Patient Interface & Clinical Doctor View Engineer  
Assigned Branch: `feature/kimaya/frontend-elder-doctor`

Primary ownership:

- Elder-friendly patient home and launcher UI:
  `app/src/main/java/com/sih26003/smritisetu/feature/patient/PatientHomeScreen.kt`
- Care Circle family touch contacts:
  `app/src/main/java/com/sih26003/smritisetu/feature/patient/CareCircleScreen.kt`
- Emergency one-touch SOS screen:
  `app/src/main/java/com/sih26003/smritisetu/feature/patient/SosScreen.kt`
- Memory Garden reminiscence and photo voice viewer:
  `app/src/main/java/com/sih26003/smritisetu/feature/memoryalbum/MemoryGardenScreens.kt`
- Doctor 6-digit access screen and longitudinal clinical trend viewer:
  `app/src/main/java/com/sih26003/smritisetu/feature/doctor/DoctorAccessScreen.kt`

---

## Shared Files

The following files are architectural integration points and require coordination:

- `app/src/main/java/com/sih26003/smritisetu/MainActivity.kt` (Global NavHost & Top-level dependency wiring)
- `app/src/main/java/com/sih26003/smritisetu/data/local/database/AppDatabase.kt` (Room database singleton & migrations)
- `app/src/main/java/com/sih26003/smritisetu/data/local/entities/Entities.kt` (Room SQLite table definitions)
- `app/src/main/java/com/sih26003/smritisetu/data/local/dao/Daos.kt` (Persistence DAOs)
- `app/src/main/java/com/sih26003/smritisetu/data/repository/Repositories.kt` (Data repository contracts)
- `app/src/main/java/com/sih26003/smritisetu/demo/DemoPatientConfig.kt` (Canonical patient identity)
- `app/src/main/java/com/sih26003/smritisetu/feature/games/framework/CommonGameFramework.kt` (Base game engine contract)
- `app/src/main/java/com/sih26003/smritisetu/engine/` (Shared intelligence engines)

### Shared File Coordination Rule:
Contributors should **not** independently edit shared files simply because Antigravity suggests it. If a feature requires a shared-file modification:

1. **Stop.**
2. **Document** exactly what interface, parameter, or schema modification is required.
3. **Notify** the Lead Integrator (Venkatesh).
4. **Make** the smallest possible coordinated change.

---

## Merge Conflict Rule

Never resolve a merge conflict by blindly accepting all incoming changes.

Understand both changes in depth. Always preserve:

- Canonical patient identity (`DemoPatientConfig.PATIENT_ID = "aita_borah_01"`)
- Room database schema integrity and non-destructive migrations
- Offline-first architecture (zero mandatory cloud/network calls)
- Adaptive engine telemetry contracts (`1..5` difficulty bounds)
- Elderly accessibility rules (touch targets $\ge 64$dp, Warm Ivory theme, non-punitive messaging)
