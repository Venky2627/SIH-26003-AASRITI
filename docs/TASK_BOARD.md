# AASRITI (SIH-26003) — Task Board

> **STATUS CODES**: `NOT_STARTED` | `IN_PROGRESS` | `BLOCKED` | `REVIEW` | `DONE`  
> **PRIORITIZATION**: **P0** = Immediate / Core Vertical Slice • **P1** = Regional Differentiators • **P2** = Polish & Clinical Export

---

## 🎯 P0 — ARCHITECTURE FREEZE & PPT READINESS (Deadline: September 5)

| ID | Task Description | Owner | Reviewer | Status | Dependency |
| :--- | :--- | :--- | :--- | :---: | :--- |
| **ARCH-01** | Freeze Feature-First + Clean Architecture blueprint and directory structure | Venkatesh | Jasleen | `DONE` | None |
| **ARCH-02** | Formulate exact Entity vs Domain Model mapping to resolve model ambiguity | Jasleen | Venkatesh | `DONE` | ARCH-01 |
| **ARCH-03** | Specify 4 distinct role workflows (Patient, Caregiver, ASHA, Doctor) | Shravani | Kimaya | `DONE` | ARCH-01 |
| **ARCH-04** | Formalize 6-Game Framework contracts (`GameDefinition`, `GameSession`) | Krishna | Bhavya | `DONE` | ARCH-01 |
| **ARCH-05** | Document explainable Adaptive Engine (Decision Tree) and Priority Engine rules | Venkatesh | Shravani | `DONE` | ARCH-04 |
| **ARCH-06** | Specify Cultural Theme Engine (Assam, Manipur, Meghalaya) independent of language | Bhavya | Krishna | `DONE` | ARCH-01 |
| **ARCH-07** | Author PPT Technical Architecture slide deck and system diagrams | Venkatesh | All | `IN_PROGRESS` | ARCH-01–06 |
| **ARCH-08** | Write SIH-26003 Problem Statement Traceability Matrix (`docs/PS_TRACEABILITY.md`) | Kimaya | Venkatesh | `DONE` | ARCH-03 |

---

## 🚀 P0 — CORE VERTICAL SLICE (Post-September 5 First Milestone)

*Objective*: Connect the foundational loop end-to-end:
$$\text{Caregiver Creates Patient} \rightarrow \text{Patient Plays 1 Game} \rightarrow \text{Telemetry Saved} \rightarrow \text{Adaptive Engine Adjusts} \rightarrow \text{Caregiver Logs Event} \rightarrow \text{Priority Engine Evaluates} \rightarrow \text{Doctor Views Trend}$$

| ID | Task Description | Owner | Reviewer | Status | Dependency |
| :--- | :--- | :--- | :--- | :---: | :--- |
| **CORE-01** | Implement `domain/model/` data classes (`Patient`, `GameSession`, `CareLog`, `Reminder`) | Jasleen | Krishna | `NOT_STARTED` | ARCH-02 |
| **CORE-02** | Implement `data/mapper/` extension functions (`toDomain()`, `toEntity()`) | Jasleen | Venkatesh | `NOT_STARTED` | CORE-01 |
| **CORE-03** | Wire `feature/patient/` home screen directly to `domain/model/` | Bhavya | Jasleen | `NOT_STARTED` | CORE-01 |
| **CORE-04** | Execute Family Trivia game round loop and save `GameSession` via Repository | Krishna | Bhavya | `NOT_STARTED` | CORE-02 |
| **CORE-05** | Evaluate `DecisionTreeEngine` offline inference and update next round difficulty | Venkatesh | Jasleen | `NOT_STARTED` | CORE-04 |
| **CORE-06** | Build Caregiver Quick Log modal (Medication, Appetite, Sleep, Fall) | Shravani | Bhavya | `NOT_STARTED` | CORE-01 |
| **CORE-07** | Implement `PriorityEngine` deterministic evaluation (`Normal`, `Watch`, `Priority`, `Urgent`) | Venkatesh | Shravani | `NOT_STARTED` | CORE-06 |
| **CORE-08** | Build Doctor Patient Snapshot screen displaying 7-day reaction time and error trends | Kimaya | Venkatesh | `NOT_STARTED` | CORE-05, CORE-07 |
| **CORE-09** | Verify 100% offline functionality in Airplane Mode for entire vertical slice | All | Venkatesh | `NOT_STARTED` | CORE-01–08 |

---

## 🌟 P1 — DIFFERENTIATORS & REGIONAL DEPTH (Post-September 5 Second Milestone)

| ID | Task Description | Owner | Reviewer | Status | Dependency |
| :--- | :--- | :--- | :--- | :---: | :--- |
| **DIFF-01** | Deepen Games 2 & 3 (Voice Cue Card with 5s fallback & Daily Sequencing) | Krishna | Bhavya | `NOT_STARTED` | CORE-04 |
| **DIFF-02** | Deepen Games 4, 5 & 6 (Categorisation, Village Market, Pattern/Rotation) | Bhavya | Krishna | `NOT_STARTED` | CORE-04 |
| **DIFF-03** | Implement Cultural Theme Pack loader (`AssamTheme`, `ManipurTheme`, `MeghalayaTheme`) | Bhavya | Shravani | `NOT_STARTED` | ARCH-06 |
| **DIFF-04** | Implement ASHA Worker shared-device multi-patient switching workflow | Shravani | Kimaya | `NOT_STARTED` | CORE-01 |
| **DIFF-05** | Implement Memory Album photo and relationship viewer with local audio clips | Shravani | Bhavya | `NOT_STARTED` | CORE-01 |
| **DIFF-06** | Implement Emergency Toolkit (Emergency Call Intent, Patient Missing Card, Fall Triage) | Shravani | Kimaya | `NOT_STARTED` | CORE-06 |
| **DIFF-07** | Implement Doctor 30-day and 90-day longitudinal trend curves | Kimaya | Venkatesh | `NOT_STARTED` | CORE-08 |
| **DIFF-08** | Implement explainable Doctor Referral Prompt logic ("Consider clinical review") | Kimaya | Venkatesh | `NOT_STARTED` | CORE-08 |
| **DIFF-09** | Build durable `SyncQueueProcessor` with network reconnect listener | Venkatesh | Jasleen | `NOT_STARTED` | CORE-02 |

---

## 🎨 P2 — POLISH, MULTI-LANGUAGE & CLINICAL EXPORT (Pre-Grand Finale)

| ID | Task Description | Owner | Reviewer | Status | Dependency |
| :--- | :--- | :--- | :--- | :---: | :--- |
| **POL-01** | Multi-lingual audio prompt expansion (Assamese, Manipuri, Bodo, Hindi, English) | Krishna | Bhavya | `NOT_STARTED` | DIFF-01 |
| **POL-02** | Generate 1-page Clinician PDF Report using Android `PdfDocument` | Kimaya | Venkatesh | `NOT_STARTED` | DIFF-07 |
| **POL-03** | WCAG 2.2 AAA accessibility audit ($\ge 60\times 60\text{ dp}$ targets, $>7:1$ contrast) | Bhavya | All | `NOT_STARTED` | DIFF-02 |
| **POL-04** | Firebase Storage synchronization for Memory Album photo assets | Venkatesh | Shravani | `NOT_STARTED` | DIFF-05, DIFF-09 |
| **POL-05** | End-to-end integration and rehearsal of the 16-step SIH Grand Finale demo | All | Venkatesh | `NOT_STARTED` | All P0–P2 |
