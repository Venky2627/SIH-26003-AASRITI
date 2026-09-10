# AASRITI — PROJECT CONTEXT

## Mandatory First Action

Every AI contributor must:

1. Read `PROJECT_CONTEXT.md` completely.
2. Read `OWNERSHIP.md`.
3. Read `CONTRIBUTING.md`.
4. Run `git status`.
5. Run `git branch --show-current`.
6. Inspect recent commits (`git log -10 --oneline`).
7. Pull current `develop` before starting work.
8. Understand ownership boundaries before modifying code.

## Project

AASRITI  
SIH Problem Statement 26003  

AI-Based Cognitive Gaming and Memory Assistance Platform for Elderly Dementia Patients in the North Eastern Region.

## Technology

- Native Android
- Kotlin
- Jetpack Compose
- Room SQLite
- Offline-first architecture
- On-device adaptive decision logic

## Core Architecture Principles

- Offline-first
- No mandatory internet dependency
- Real interaction metrics
- No fabricated clinical data
- Room SQLite is the persistence source of truth
- Explainable adaptive logic
- Accessibility-first elderly-friendly UI
- Non-punitive interaction design

## Canonical Patient Identity

The SINGLE canonical database patient identity is:

`DemoPatientConfig.PATIENT_ID`

Current value:

`"aita_borah_01"`

Rules:

- Never introduce another database patient ID.
- Never use `"default"`, `"default_id"`, or `"AS-01"` as persistence IDs.
- `"AS-KAM-0042"` may only exist as a display pseudonym/code where explicitly appropriate.
- All Room foreign-keyed records must use the canonical patient identity.

## Database Contract

Current Room database version:

`2`

Migration:

`MIGRATION_1_2`

Rules:

- Do not use `fallbackToDestructiveMigration()`.
- Do not casually modify schema.
- Do not add entities without integration approval.
- Do not alter foreign keys without approval.
- Do not bypass Room persistence with temporary UI-only state when persistent data is required.

Core pipelines:

### GAME Pipeline
User Interaction  
→ PerformanceCollector  
→ Metrics  
→ DecisionTreeEngine  
→ adaptationDecision  
→ GameSessionEntity  
→ GameRepository  
→ GameSessionDao  
→ Room SQLite  

### CAREGIVER Pipeline
Caregiver Input  
→ CareLogEntity  
→ CareLogRepository  
→ CareLogDao  
→ Room SQLite  
→ Flow  
→ PriorityEngine  

### DOCTOR Pipeline
Room GameSessions  
→ Repository  
→ Flow  
→ TrendEngine  
→ Doctor UI  

## Adaptive Difficulty Contract

Real interaction metrics include:

- response latency
- hesitation count
- errors
- accuracy

`DecisionTreeEngine` must produce difficulty recommendations bounded:

`1..5`

Cross-session flow:

Completed Game  
→ adaptationDecision persisted  
→ latest GameSession retrieved  
→ initialDifficulty  
→ next game launch  

Do not replace this with random, fake, or mock adaptive metrics.

## Honest Data Rule

Do NOT fabricate:

- patient performance metrics
- clinical trends
- longitudinal history
- AI recommendations
- random charts presented as real data

If no data exists:

Show an honest empty state.

## Offline Requirement

The prototype must remain offline-capable.

Do not introduce without explicit approval:

- Firebase dependency
- mandatory cloud backend
- mandatory external API
- remote AI inference
- internet-required core functionality

## Current Core Status

Core integration has passed unit-test and debug-build verification. Physical device/emulator verification status must be reported separately.

## Protected Core

Contributors should not casually modify:

- `core/`
- `engine/`
- `data/local/`
- `data/repository/`
- `DemoPatientConfig.kt`
- `AppDatabase.kt`
- `Entities.kt`
- `Daos.kt`
- core navigation in `MainActivity.kt`

These require integration awareness.

## Before Committing

Mandatory:

Run relevant tests.

At minimum for integration-sensitive changes:

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
```

Then inspect:

```powershell
git status
git diff
```

Never commit:

- `build/`
- `.idea/`
- `local.properties`
- generated APK files
- secrets
- credentials
- API keys

## Source of Truth

The repository state and these collaboration documents are the source of truth.

AI contributors must never assume project state from old conversations.

## UI Integration Status (Design Ingestion Baseline)

- **Prototype Source**: Ingested in `ui-reference/prototype/` with authoritative screen mapping in `ui-reference/SCREEN_MAPPING.md`.
- **Integrated Screens (P0 Core Pipelines)**:
  - Welcome & Role Pathways (`AuthScreens.kt`): Top utility bar with Language switch (`ENG` / `অসমীয়া`), "Saved Locally" reassurance pill, spiral logo badge, dominant elder patient touch target (>= 64dp), and PIN-protected caregiver/doctor cards.
  - Patient Home & 6 Cognitive Games (`PatientHomeScreen.kt`, `FamilyTriviaGame.kt`): Very low density layout, touch targets >= 64dp, real interaction latency & hesitation gap tracking, `GameSessionEntity` Room persistence, and cross-session adaptive difficulty recommendations (bounded 1..5).
  - Caregiver / ASHA Field Workflow (`CaregiverDashboardScreen.kt`, `AshaDashboardScreen.kt`): Patient switching, <30s Quick Care Log (Medication, Appetite, Sleep, Mood, Fall, Confusion) writing to Room SQLite `care_logs`, real-time `PriorityEngine` evaluation (WATCH / PRIORITY / NORMAL).
  - Clinician Snapshot & Longitudinal Trends (`DoctorAccessScreen.kt`): 6-digit access gate, 7/30-day reaction time curves via `TrendEngine`, clinical assessments review, caregiver observation table, clinician notes, and follow-up guidance.
  - Offline PDF Summary Export (`PdfReportGenerator.kt`): Native Android `PdfDocument` generating formal consultation dossier compiling real Room SQLite data (Patient profile, longitudinal trends, recent sessions, caregiver logs, priority tier, guidance notes, and statutory medical disclaimer).
- **Deferred Screens (P2)**: Cloud push sync animations, secondary print spooler protocols, decorative animations.
- **Asset & Token Policy**: Strictly enforced `AasritiColorTokens` (Warm Ivory, Soft Cream, Deep Northeast Forest, Muted Heritage Terracotta, Muga Gold) with AAA contrast and >= 64dp elderly touch targets. Zero web runtimes or WebViews.

