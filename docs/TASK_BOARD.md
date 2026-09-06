# AASRITI (SIH-26003) — Task Board
## Phase 1: Vertical Slice Execution

> **STATUS CODES**: `NOT_STARTED` | `IN_PROGRESS` | `BLOCKED` | `REVIEW` | `DONE`  
> **PRIORITIZATION**: **P0** = Vertical Slice V1 (The Golden Path) • **P1** = Regional Depth & Multi-Game • **P2** = Polish & Release Tag

---

## 🎯 PHASE 0 — ARCHITECTURE FREEZE & CONTRACTS (COMPLETE ✅)

| ID | Task Description | Owner | Reviewer | Status | Deliverable |
| :--- | :--- | :--- | :--- | :---: | :--- |
| **P0-01** | Pure Domain Models (`Patient`, `GameSession`, `Reminder`, `CareLog`, `TodayPriority`) | Jasleen / Venkatesh | All | `DONE` | `domain/model/DomainModels.kt` |
| **P0-02** | Domain Repository Interfaces (`DomainPatientRepository`, `DomainGameRepository`, etc.) | Jasleen | Krishna | `DONE` | `domain/repository/DomainRepositories.kt` |
| **P0-03** | Core Intelligence Engines (`AdaptiveEngine`, `PriorityEngine`, `TrendEngine`) | Venkatesh | Jasleen | `DONE` | `engine/adaptive/`, `engine/priority/`, `engine/trend/` |
| **P0-04** | Central Navigation Routes Contract | Venkatesh | All | `DONE` | `navigation/AppRoutes.kt` |
| **P0-05** | Shared Mock Provider for Unblocked UI | Venkatesh | All | `DONE` | `demo/AasritiMockProvider.kt` |
| **P0-06** | Authoritative Integration Bible & Vibecoding Brief | Venkatesh | All | `DONE` | `docs/INTEGRATION_BIBLE.md`, `docs/TEAM_VIBECODING_BRIEF.md` |

---

## 🚀 PHASE 1 — THE GOLDEN PATH (VERTICAL SLICE V1)

*Goal*: Complete the single end-to-end feedback loop:
$$\text{Elder Plays Flower Match} \rightarrow \text{Telemetry} \rightarrow \text{CognitiveInsightOrchestrator} \rightarrow \text{Adaptive Scaling} \rightarrow \text{Caregiver Priority Card} \rightarrow \text{Doctor Trend Curve}$$

| ID | Task Description | Owner | Reviewer | Status | Dependency |
| :--- | :--- | :--- | :--- | :---: | :--- |
| **SLICE-01** | Implement `CognitiveInsightOrchestrator` uniting Adaptive, Priority, and Trend engines | Venkatesh | Jasleen | `IN_PROGRESS` | P0-03 |
| **SLICE-02** | Wire Flower Match to emit `GameSession` telemetry to Orchestrator | Krishna | Bhavya | `NOT_STARTED` | SLICE-01 |
| **SLICE-03** | Implement Room SQLite entities, DAOs, and Mappers for `Patient`, `GameSession`, `CareLog` | Jasleen | Venkatesh | `NOT_STARTED` | P0-01 |
| **SLICE-04** | Connect Caregiver Dashboard Today's Priority Card directly to `PriorityEngine` output | Shravani | Kimaya | `NOT_STARTED` | SLICE-01 |
| **SLICE-05** | Connect Doctor Portal to `TrendEngine` output with 7 / 30 / 90-day window selector | Kimaya | Venkatesh | `NOT_STARTED` | SLICE-01 |
| **SLICE-06** | Align Game 4 (Categorisation) with `AasritiColorTokens` and `AdaptiveEngine` difficulty level | Bhavya | Krishna | `NOT_STARTED` | SLICE-01 |
| **SLICE-07** | Checkpoint 1 & 2 integration verification (`./gradlew testDebugUnitTest` + APK) | All | Venkatesh | `NOT_STARTED` | SLICE-01–06 |

---

## 🌟 PHASE 2 — EXPANSION & MULTI-GAME DEPTH (Post-Vertical Slice)

| ID | Task Description | Owner | Reviewer | Status | Dependency |
| :--- | :--- | :--- | :--- | :---: | :--- |
| **EXP-01** | Deepen Games 1–3 (Family Trivia, Voice Cue Card, Daily Sequencing) | Krishna | Bhavya | `NOT_STARTED` | SLICE-02 |
| **EXP-02** | Deepen Games 5 & 6 (Village Market, Pattern Recognition) | Bhavya | Krishna | `NOT_STARTED` | SLICE-06 |
| **EXP-03** | Connect ASHA 14-elder community roster to `PriorityEngine` triage status | Shravani | Kimaya | `NOT_STARTED` | SLICE-04 |
| **EXP-04** | Connect Memory Garden and Care Circle telephony to domain repositories | Kimaya | Venkatesh | `NOT_STARTED` | SLICE-05 |
| **EXP-05** | Wire Room SQLite persistence to `DomainRepositories` | Jasleen | Venkatesh | `NOT_STARTED` | SLICE-03 |
