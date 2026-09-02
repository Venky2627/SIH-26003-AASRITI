# SmritiSetu (SIH26003) — Team Roles & RACI Responsibility Matrix

## 👥 1. The 6-Engineer Hackathon Team Structure

To eliminate role ambiguity and deliver a flawless offline healthcare platform within 14 days, the team is structured into 6 distinct specialized roles:

```
                                  +-----------------------------+
                                  |   MEMBER 1: TEAM LEAD &     |
                                  |     SOLUTIONS ARCHITECT     |
                                  +-----------------------------+
                                                 |
         +-----------------------+---------------+-----------------------+
         |                       |                               |       |
         v                       v                               v       v
+-----------------+     +-----------------+     +-----------------+     +-----------------+
|   MEMBER 2:     |     |   MEMBER 3:     |     |   MEMBER 4:     |     |   MEMBER 5:     |
| MOBILE ENGR 1   |     | MOBILE ENGR 2   |     | EDGE AI / SPEECH|     | BACKEND / CLOUD |
| (Games Engine)  |     | (Core & Storage)|     | (ONNX & ASR)    |     | (Sync & DB)     |
+-----------------+     +-----------------+     +-----------------+     +-----------------+
                                                 |
                                  +-----------------------------+
                                  |   MEMBER 6: CLINICAL UX,    |
                                  |   ACCESSIBILITY & QA        |
                                  +-----------------------------+
```

---

## 📊 2. Master RACI Matrix

* **R (Responsible)**: The person who does the work to achieve the task.
* **A (Accountable)**: The sole individual with final sign-off and ownership.
* **C (Consulted)**: Role whose feedback and expertise are sought.
* **I (Informed)**: Role kept updated on progress.

| Core Project Deliverable | Lead Architect (M1) | Mobile Games (M2) | Mobile Core (M3) | Edge AI (M4) | Backend Sync (M5) | Clinical UX / QA (M6) |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: |
| **System Architecture & DPDA Compliance** | **A / R** | C | C | C | C | C |
| **Speed Match Game Engine** | A | **R** | C | I | I | C |
| **Story Weaver Narrative & Voice UI** | A | **R** | C | **R** | I | C |
| **Picture Naming confrontation engine**| A | **R** | C | C | I | C |
| **SQLite Schema & SQLCipher Encryption**| A | C | **R** | I | C | I |
| **Redux Toolkit & Offline State** | A | C | **R** | I | I | I |
| **ONNX Runtime Mobile & IndicConformer**| A | C | I | **R** | I | I |
| **Raw Audio Buffer In-Memory Zeroer** | **A** | I | I | **R** | I | I |
| **FastAPI Gateway & Ingestion Schemas** | A | I | C | I | **R** | I |
| **PostgreSQL & Docker Infrastructure** | A | I | I | I | **R** | I |
| **Background Sync Queue & Backoff** | A | I | **R** | I | **R** | I |
| **WCAG 2.2 AAA Contrast & Tremor Damping**| A | C | C | I | I | **R** |
| **NER Cultural Asset Curation** | A | C | I | C | I | **R** |
| **Automated CI/CD & Android APK Build** | **A / R** | I | I | I | C | I |
| **Judge Demo Script & Pitch Rehearsal** | **A / R** | C | C | C | C | C |

---

## 🎯 3. Role Responsibilities Breakdown

### Member 1: Team Lead & Solutions Architect
* Enforces repository hygiene, branch protection, and daily standup check-ins.
* Guarantees statutory DPDA 2023 compliance and architectural consistency.
* Coordinates live presentation pitch and leads jury Q&A handling.

### Member 2: Mobile Engineer 1 (Gameplay Engine & Animations)
* Builds UI rendering loops for Speed Match, Story Weaver, and Picture Naming.
* Implements React Native Reanimated 3 animations and canvas elements.
* Tunes game audio playback triggers and interactive cards.

### Member 3: Mobile Engineer 2 (Core Architecture, Storage & State)
* Implements `expo-sqlite` and SQLCipher database initialization.
* Builds Redux Toolkit slices, offline persistence, and local repository queries.
* Implements `sync_queue` retry loops and network state listener hooks.

### Member 4: Edge AI & Speech Engineer
* Quantizes IndicConformer models to INT8 and compiles ONNX Mobile runtime sessions.
* Connects `expo-av` 16kHz PCM audio streamer directly to ONNX memory tensors.
* Ensures immediate memory wiping of raw audio buffers post-inference.

### Member 5: Backend & Cloud Synchronization Engineer
* Implements FastAPI ingestion routes (`/api/v1/sync/batch` and `/api/v1/health`).
* Sets up Docker Compose with PostgreSQL 16 (TimescaleDB) and Redis.
* Builds idempotent deduplication and migration scripts via Alembic.

### Member 6: Clinical UX, Accessibility & QA Evaluator
* Validates WCAG 2.2 AAA standards (contrast ratios $>7:1$, touch targets $\ge 64\text{ dp}$).
* Tests motor tremor damping filters and cataract-resilient typography.
* Curates indigenous cultural assets (Assam, Manipur, Mizoram, Nagaland) and leads Airplane Mode QA testing on physical test devices.
