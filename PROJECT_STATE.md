# SIH-26003 (SmritiSetu) — Project State

> **SINGLE SOURCE OF TRUTH FOR CURRENT PROJECT PROGRESS**  
> All team members and AI coding assistants must consult this document before starting work and update it immediately upon task completion.

---

## 🎯 CURRENT PHASE
**PHASE 0: Architecture Freeze & PPT Preparation**

---

## ⏰ IMMEDIATE DEADLINE
**SEPTEMBER 5, 2026 — Smart India Hackathon PPT Submission**

---

## 🎯 CURRENT OBJECTIVE
1. Freeze technical architecture and eliminate all model/layer ambiguity.
2. Complete full team allocation with Primary Owners and Secondary Reviewers across all 6 members.
3. Establish a crystal-clear, technically credible architecture deck for the September 5 PPT submission.
4. Prepare the exact specifications for the post-PPT vertical slice milestone.

---

## 📅 LAST UPDATED
`2026-09-03T21:20:00+05:30` (Architecture Blueprint Freeze)

---

## ✅ COMPLETED (Verified in Repository)
* **Native Android Core**: Kotlin 1.9.23, Jetpack Compose Material 3, Android SDK 34 (`minSdk = 24`).
* **Local Persistence Engine**: Room SQLite database (`smritisetu.db`) with `users`, `patients`, `relationships`, `game_sessions`, `reminders`, `doctor_access`, `sync_queue`.
* **Authentication Subsystem**: Local 6-digit PIN with SHA-256 for Caregiver/Doctor; direct photo/avatar tap for Patient (Zero PIN / Zero OTP).
* **Six Cognitive Games (Engine & Logic)**:
  - Game 1: Family Trivia (Levels 1–5, Room relationship binding)
  - Game 2: Voice Cue Card (Levels 1–5, audio prompts, 5s speech fallback)
  - Game 3: Daily Sequencing (Levels 1–5, Assam tea & temple routines, distractors)
  - Game 4: Categorisation (Levels 1–5, fruits, vegetables, animals, traditional wear)
  - Game 5: Village Market (Levels 1–5, NER market shopping list recall)
  - Game 6: Pattern Recognition & Object Matching (Levels 1–5, color/shape motifs)
* **Adaptive Difficulty Engine**: On-device Decision Tree interpreter (`DecisionTreeEngine.kt`) reading local JSON (`<1ms` inference, levels 1–5).
* **Shared Voice Service**: Offline TTS ($0.85\times$ elderly calibration) + Assamese (`as`) and English (`en`) prompt packs.
* **Offline Reminder Scheduling**: Native Android `AlarmManager` scheduler registering exact alarms across reboots via `BootReceiver`.
* **CI Build Pipeline**: GitHub Actions workflow (`.github/workflows/android.yml` or `android-build.yml`) running Python model export validation, JDK 17, and Gradle unit tests.

---

## 🚧 IN PROGRESS (Immediate Focus for September 5 PPT)
* **PPT Technical Architecture Diagrams**:
  - High-level system architecture (Compose $\rightarrow$ ViewModel $\rightarrow$ UseCase $\rightarrow$ Repository $\rightarrow$ Room $\rightarrow$ SyncQueue $\rightarrow$ Firebase).
  - Multi-role UX workflow (Patient, Caregiver, ASHA, Doctor).
  - Explainable Adaptive Engine flow chart (Reaction Time, Errors, Hesitation $\rightarrow$ Difficulty 1–5).
  - Explainable Priority Engine matrix (Normal, Watch, Priority, Urgent).
* **Subsystem Interface Freezing**: Freezing data contracts between `data/local/entity/` and `domain/model/` with explicit mappers.
* **Cultural Theme Engine Specification**: Decoupling visual/content theme packs (Assam, Manipur, Meghalaya) from UI language.

---

## 🛑 BLOCKED
* None. (Architecture freeze and team allocation are actively unlocked).

---

## 🚀 NEXT 3 PRIORITIES (Pre-PPT Submission)
1. **PPT Slide Deck Finalization**: Synthesize technical architecture, data model, offline guarantee, and regional novelty into the official SIH PPT format.
2. **Subsystem Interface Review**: Each of the 6 members reviews their primary subsystem interfaces and signs off on domain models.
3. **Demo Script Rehearsal**: Dry run of the 16-step offline vertical slice demonstration script (`docs/DEMO_SCRIPT.md`).

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
