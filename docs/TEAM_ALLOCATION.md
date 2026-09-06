# AASRITI (SIH-26003) — Team Ownership & Allocation Matrix
## Phase 1 Vertical Slice Execution

> **Core Philosophy**: 6 Equal Contributors. Zero Hierarchy. Vertical Subsystem Ownership.  
> Every subsystem has **ONE Primary Owner** and **ONE Secondary Reviewer** to eliminate knowledge silos and guarantee code review quality.  
> **Source of Truth**: All members code to `docs/INTEGRATION_BIBLE.md` and `docs/TEAM_VIBECODING_BRIEF.md`.

---

## 👤 1. VENKATESH

* **Primary Ownership**:
  - System Architecture & Intelligence Pipeline
  - `CognitiveInsightOrchestrator` (`engine/orchestration/`)
  - Adaptive Difficulty Engine (`engine/adaptive/AdaptiveEngine.kt`)
  - Priority Engine (`engine/priority/PriorityEngine.kt`)
  - Longitudinal Trend Engine (`engine/trend/TrendEngine.kt`)
* **Designated Branch**: `feature/venkatesh/backend-engines-security`
* **Safe Directories (Normal Working Scope)**:
  - `app/src/main/java/com/sih26003/smritisetu/engine/`
  - `app/src/main/java/com/sih26003/smritisetu/navigation/`
* **Current P0 Task (Vertical Slice V1)**:
  - Implement `CognitiveInsightOrchestrator` to unify `AdaptiveEngine`, `PriorityEngine`, and `TrendEngine` into a single invocation point.
* **Review Responsibilities**: Reviews PRs from: **Kimaya** (Elder & Doctor Workflows).

---

## 👤 2. JASLEEN

* **Primary Ownership**:
  - Data Layer & Room SQLite Persistence
  - Room Entities & 7 DAOs (`data/local/`)
  - Entity $\leftrightarrow$ Domain Mappers (`data/mapper/`)
  - Domain Repositories Implementation (`data/repository/`)
* **Designated Branch**: `feature/jasleen/backend-data-sync`
* **Safe Directories (Normal Working Scope)**:
  - `app/src/main/java/com/sih26003/smritisetu/data/local/`
  - `app/src/main/java/com/sih26003/smritisetu/data/mapper/`
  - `app/src/main/java/com/sih26003/smritisetu/data/repository/`
* **Current P0 Task (Vertical Slice V1)**:
  - Implement Room entities, DAOs, and Mappers for `Patient`, `GameSession`, and `CareLog`. Ensure Room entities never leak to UI.
* **Review Responsibilities**: Reviews PRs from: **Venkatesh** (Engine Orchestration).

---

## 👤 3. KRISHNA

* **Primary Ownership**:
  - Reusable Game Framework (`feature/games/framework/CommonGameFramework.kt`)
  - Flagship Flower Match Telemetry Integration (`feature/games/flowermatch/`)
  - Cognitive Games 1–3 (`familytrivia/`, `voicecuecard/`, `sequencing/`)
* **Designated Branch**: `feature/krishna/frontend-games-framework`
* **Safe Directories (Normal Working Scope)**:
  - `app/src/main/java/com/sih26003/smritisetu/feature/games/framework/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/games/flowermatch/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/games/familytrivia/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/games/voicecuecard/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/games/sequencing/`
* **Current P0 Task (Vertical Slice V1)**:
  - Wire Flower Match to emit real `GameSession` telemetry (reactionTimeMs, hesitationCount, accuracy) to `CognitiveInsightOrchestrator`.
* **Review Responsibilities**: Reviews PRs from: **Jasleen** (Data Layer).

---

## 👤 4. BHAVYA

* **Primary Ownership**:
  - Cultural Theme Engine & Design Tokens (`core/ui/theme/`, `cultural/`)
  - Cognitive Games 4–6 (`categorisation/`, `villagemarket/`, `patternrecognition/`)
  - WCAG AAA Contrast & Non-glare Surfaces (`WarmIvory`, `DeepNortheastForest`)
* **Designated Branch**: `feature/bhavya/frontend-cultural-games`
* **Safe Directories (Normal Working Scope)**:
  - `app/src/main/java/com/sih26003/smritisetu/feature/games/categorisation/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/games/villagemarket/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/games/patternrecognition/`
  - `app/src/main/java/com/sih26003/smritisetu/cultural/`
  - `app/src/main/java/com/sih26003/smritisetu/core/ui/theme/`
* **Current P0 Task (Vertical Slice V1)**:
  - Polish Game 4 (Categorisation) with `AasritiColorTokens` and ensure difficultyLevel scales with `AdaptiveEngine`.
* **Review Responsibilities**: Reviews PRs from: **Krishna** (Game Framework & Telemetry).

---

## 👤 5. SHRAVANI

* **Primary Ownership**:
  - Caregiver Dashboard (`feature/caregiver/`)
  - Today's Priority Card (consuming `PriorityEngine`)
  - Caregiver Quick Log modal (<30s incident logging)
  - ASHA Community Worker Module (`feature/asha/`: 14-elder roster, visit log)
  - Offline Reminders UI (`feature/reminders/`)
* **Designated Branch**: `feature/shravani/frontend-caregiver-asha`
* **Safe Directories (Normal Working Scope)**:
  - `app/src/main/java/com/sih26003/smritisetu/feature/caregiver/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/asha/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/reminders/`
* **Current P0 Task (Vertical Slice V1)**:
  - Connect Caregiver Dashboard's "Today's Priority Card" directly to `PriorityEngine.evaluateTodayPriority()`.
* **Review Responsibilities**: Reviews PRs from: **Bhavya** (Cultural Games & Theme).

---

## 👤 6. KIMAYA

* **Primary Ownership**:
  - Elder Companion Home Screen (`feature/patient/PatientHomeScreen.kt`)
  - Memory Garden Reminiscence Album (`feature/memoryalbum/`)
  - Care Circle Family Telephony (`feature/patient/CareCircleScreen.kt`)
  - Patient SOS Protocol (`feature/patient/SosScreen.kt`)
  - Doctor / Clinician Portal (`feature/doctor/DoctorAccessScreen.kt`)
* **Designated Branch**: `feature/kimaya/frontend-elder-doctor`
* **Safe Directories (Normal Working Scope)**:
  - `app/src/main/java/com/sih26003/smritisetu/feature/patient/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/memoryalbum/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/doctor/`
* **Current P0 Task (Vertical Slice V1)**:
  - Wire Doctor Portal to `TrendEngine` (with 7/30/90-day toggle) and ensure Elder Home seamlessly launches Flower Match.
* **Review Responsibilities**: Reviews PRs from: **Shravani** (Caregiver & ASHA Workflows).

---

## 🤖 AI AGENT ROUTING DIRECTIVE

```text
IF CURRENT DEVELOPER = <NAME>
THEN:
  1. Read docs/INTEGRATION_BIBLE.md and docs/TEAM_VIBECODING_BRIEF.md.
  2. Verify working on feature/<member>/<task-name>.
  3. Keep changes strictly inside <NAME>'s safe directories.
  4. Ensure all code consumes pure domain models from domain/model/.
  5. Never create duplicate models or leak Room entities into UI.
  6. Run testDebugUnitTest before claiming completion.
```
