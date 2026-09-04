# SIH-26003 (SmritiSetu) — Project State

> **SINGLE SOURCE OF TRUTH FOR CURRENT PROJECT PROGRESS**  
> All team members and AI coding assistants must consult this document before starting work and update it immediately upon task completion.

---

## 🎯 CURRENT PHASE
**PHASE 1: Core Vertical Slice Implementation** (Phase 0 Architecture Freeze Complete)

---

## ⏰ CURRENT MILESTONE
**Core Vertical Slice Integration** — End-to-end loop: Caregiver setup $\rightarrow$ Patient game $\rightarrow$ Adaptive difficulty update $\rightarrow$ Caregiver Quick Log $\rightarrow$ Priority triage.

---

## 🎯 CURRENT OBJECTIVE
1. Implement pure Kotlin domain models (`domain/model/`) and explicit extension mappers (`data/mapper/`).
2. Connect `PatientHomeScreen` and `CaregiverDashboardScreen` to reactive Room data flows.
3. Validate deterministic `PriorityEngine` triage and on-device `DecisionTreeEngine` adaptation.
4. Maintain 100% offline autonomy and WCAG AAA accessibility compliance.

---

## 📅 LAST UPDATED
`2026-09-05T01:35:00+05:30` (Repository Consolidation & Phase 1 Active)

---

## ✅ COMPLETED (Verified in Repository)
* **Architecture Frozen**: Clean Architecture + Feature-First packaging consolidated into [`/docs/ARCHITECTURE.md`](ARCHITECTURE.md).
* **UI Design System Active**: `AasritiTheme`, 13 canonical color tokens, shapes, spacing, and typography verified in Compose.
* **Security & Privacy Governance**: Full-product RBAC, local SHA-256 PIN hashing, and DPDPA 2023 compliance frozen in [`/SECURITY.md`](../SECURITY.md).
* **Native Android Core**: Kotlin 1.9.23, Jetpack Compose Material 3, Android SDK 34 (`minSdk = 24`).
* **Local Persistence Engine**: Room SQLite database (`smritisetu.db`) with 7 DAOs and `sync_queue` table.
* **Authentication Subsystem**: Local 6-digit PIN for Caregiver/Doctor; direct photo/avatar tap for Patient (Zero PIN).
* **Six Cognitive Games (Engine & Logic)**:
  - Game 1: Family Trivia (Levels 1–5, Room relationship binding)
  - Game 2: Voice Cue Card (Levels 1–5, audio prompts, 5s speech fallback)
  - Game 3: Daily Sequencing (Levels 1–5, Assam tea & temple routines, distractors)
  - Game 4: Categorisation (Levels 1–5, fruits, vegetables, animals, traditional wear)
  - Game 5: Village Market (Levels 1–5, NER market shopping list recall)
  - Game 6: Pattern Recognition & Object Matching (Levels 1–5, color/shape motifs)
* **Adaptive Difficulty Engine**: On-device Decision Tree interpreter (`DecisionTreeEngine.kt`) reading local JSON (`<0.5ms` inference, levels 1–5).
* **Build & Tests Verified**: Local and CI validation passing (`./gradlew testDebugUnitTest` and `./gradlew assembleDebug` both PASS).

---

## 🚧 IN PROGRESS (Phase 1 Core Vertical Slice)
* **CORE-01**: Implement `domain/model/` data classes (`Patient`, `GameSession`, `CareLog`, `Reminder`).
* **CORE-02**: Implement `data/mapper/` extension functions (`toDomain()`, `toEntity()`).
* **CORE-03**: Connect `PatientHomeScreen` and `CaregiverDashboardScreen` to domain models.
* **CORE-06**: Build Caregiver Quick Log modal (Medication, Appetite, Sleep, Fall).
* **CORE-07**: Implement `PriorityEngine` deterministic evaluation (`Normal`, `Watch`, `Priority`, `Urgent`).

---

## 🛑 BLOCKED
* None. (Local Gradle wrapper restored, build and unit tests passing, all dependencies resolved).

---

## 🚀 NEXT 3 PRIORITIES (Vertical Slice Sprint)
1. **Domain Models & Mappers**: Implement `domain/model/` and `data/mapper/` to enforce clean model separation.
2. **Patient Gameplay Loop**: Wire Family Trivia and Sequencing directly to Room session persistence.
3. **Caregiver Triage Flow**: Wire Quick Log and Priority Engine for immediate bedside observations.

---

## 🔗 INTEGRATION STATUS
| Subsystem | Integration State | Primary Owner | Secondary Reviewer |
| :--- | :---: | :--- | :--- |
| **Data Layer (Room + SQLite)** | `STABLE` | Jasleen | Venkatesh |
| **Domain Layer & Mappers** | `SPECIFIED` | Jasleen | Krishna |
| **Common Game Framework** | `STABLE` | Krishna | Bhavya |
| **Adaptive Engine (Decision Tree)**| `STABLE` | Venkatesh | Jasleen |
| **Priority Engine** | `SPECIFIED` | Venkatesh | Shravani |
| **Caregiver / ASHA Workflows** | `PARTIAL` | Shravani | Kimaya |
| **Doctor Portal & Trends** | `PARTIAL` | Kimaya | Venkatesh |
| **Cultural Theme Engine** | `SPECIFIED` | Bhavya | Shravani |
| **Sync Engine (Queue Processor)** | `STABLE (OFFLINE)`| Venkatesh | Jasleen |

---

## ⚠️ KNOWN RISKS & MITIGATIONS
1. **Scope Creep Before PPT**:
   - *Risk*: Team attempts to code full UI for all 4 roles before completing the PPT.
   - *Mitigation*: STRICT ARCHITECTURE FREEZE. Coding resumes on Phase 1 vertical slice ONLY after September 5.
2. **Model Duplication**:
   - *Risk*: UI developers create duplicate data classes instead of using `domain/model/`.
   - *Mitigation*: Rigid rule: `data/local/entity/` = SQLite only; `domain/model/` = app domain only; `data/mapper/` bridges them.
3. **Medical Over-claiming**:
   - *Risk*: Presentation slides or docs claiming the app "diagnoses dementia".
   - *Mitigation*: Strictly enforce neutral functional terminology (*"cognitive engagement metrics"*, *"longitudinal behavioral trends"*, *"recommended next difficulty"*).
