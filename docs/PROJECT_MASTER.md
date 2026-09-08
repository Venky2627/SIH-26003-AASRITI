# AASRITI (SIH-26003) — PROJECT MASTER DOCUMENT
## Authoritative Single Source of Truth & Operational Registry

> **CORE PRINCIPLE**:  
> **"DO NOT BUILD WHAT ALREADY EXISTS. INSPECT FIRST, CONNECT SECOND, IMPROVE THIRD."**

---

## 1. PROJECT IDENTITY & CURRENT STATE

* **Problem Statement**: SIH-26003 — AI-Based Cognitive Gaming & Memory Assistance Platform for Elderly Dementia Patients in North-East Region (NER).
* **Application Name**: **AASRITI** (আশ্ৰিতি)
* **Architecture State**: **100% OFFLINE-FIRST**
  * **Room SQLite Local Database** is the single authority.
  * Zero Cloud Firestore/Firebase dependencies.
  * Zero remote LLMs / cloud AI inference.
  * Zero external Cloud TTS / REST APIs.
  * On-device Scikit-Learn Decision Tree JSON interpreter (`DecisionTreeEngine`).
  * On-device Android TTS + local Assamese voice prompt packs.
* **Build Health**: Verified passing (`./gradlew testDebugUnitTest`, `./gradlew assembleDebug`).

---

## 2. THE AASRITI GOLDEN PATH (VERTICAL SLICE)

The entire SIH demo and 3-day sprint centers on this single end-to-end verified pipeline:

```text
                   AASRITI GOLDEN PATH

                      AITA BORAH
                          │ (Direct Photo Tap - Zero PIN)
                          ▼
                     PATIENT HOME
                          │
                          ▼
                    FLOWER MATCH (Flagship Game)
                          │
                 real interaction
                 (latency, errors, hesitation >3.5s)
                          │
                          ▼
                    GAME TELEMETRY
                          │
                          ▼
                    ROOM SQLITE (smritisetu.db)
                          │
                          ▼
             COGNITIVE INSIGHT ORCHESTRATOR
                    ╱          │          ╲
                   ▼           ▼           ▼
              ADAPTIVE       TREND      PRIORITY
            (DecisionTree)  (7-Day)   (Triage Alert)
                   │           │           │
                   ▼           ▼           ▼
              NEXT ROUND    DOCTOR     CAREGIVER
                             PORTAL      DASHBOARD
```

* **Offline Indicator**: All screens display `100% Offline • Room SQLite Local Source of Truth`.
* **Persistence Guarantee**: Data survives application force-kill and device restart.

---

## 3. MODULAR DOCUMENTATION SITEMAP

To avoid bloated mega-documents, refer to these focused authoritative guides:

1. **[INTEGRATION_BIBLE.md](file:///d:/Project/SIH/docs/INTEGRATION_BIBLE.md)**:
   Pure domain models, repository contracts, navigation route names, and architectural boundaries.
2. **[TEAM_VIBECODING_BRIEF.md](file:///d:/Project/SIH/docs/TEAM_VIBECODING_BRIEF.md)**:
   Coding rules, Git lifecycle, and guidelines for AI pair-programming.
3. **[TEAM_ALLOCATION.md](file:///d:/Project/SIH/docs/TEAM_ALLOCATION.md)**:
   Subsystem ownership, member roles, and assigned branch protocols.
4. **[TASK_BOARD.md](file:///d:/Project/SIH/docs/TASK_BOARD.md)**:
   Daily actionable checklist from September 7 to September 11.
5. **UI Governance**:
   - Accessibility Standards: [`docs/ui/UI_ACCESSIBILITY_RULES.md`](file:///d:/Project/SIH/docs/ui/UI_ACCESSIBILITY_RULES.md) (WCAG AAA, $\ge 64$dp targets, non-punitive language).
   - Component Specifications: [`docs/ui/UI_COMPONENT_RULES.md`](file:///d:/Project/SIH/docs/ui/UI_COMPONENT_RULES.md) (Warm Ivory, Northeast Forest, Muga Gold).
   - Cultural & Heritage Guide: [`docs/ui/UI_HERITAGE_GUIDE.md`](file:///d:/Project/SIH/docs/ui/UI_HERITAGE_GUIDE.md) (Authentic Assam, Manipur, Meghalaya botanical and cultural cues).

---

## 4. TEAM SUBSYSTEM ALLOCATION

| Member | Subsystem Responsibility | Active Git Branch | Primary Deliverable |
| :--- | :--- | :--- | :--- |
| **Venkatesh** | Integration Lead, Security, Engines | `feature/venkatesh/backend-engines-security` | `CognitiveInsightOrchestrator`, build stability, zero security leaks |
| **Jasleen** | Database, Room SQLite, Persistence | `feature/jasleen/backend-data-sync` | Default seeding of Aita Borah, Room entity relationships, offline persistence |
| **Krishna** | Game Framework, Flagship Games | `feature/krishna/frontend-games-framework` | Flower Match physical telemetry, `CommonGameFramework` integration |
| **Shravani** | Caregiver Portal, ASHA Dashboard | `feature/shravani/frontend-caregiver-asha` | Reactive Caregiver Dashboard, <30s Quick Log connected to `PriorityEngine` |
| **Kimaya** | Elder Experience, Doctor Access | `feature/kimaya/frontend-elder-doctor` | Doctor Portal consuming real Room sessions via `TrendEngine`, Care Circle |
| **Bhavya** | Cultural Polish, Accessibility | `feature/bhavya/frontend-cultural-games` | Design tokens, tactile feedback, secondary games refinement |

---

## 5. REPOSITORY VERIFICATION COMMANDS

```bash
# Set Java 21 environment (Android Studio JBR)
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"

# Run all unit tests
.\gradlew testDebugUnitTest

# Assemble debug APK
.\gradlew assembleDebug

# Output APK path:
# app/build/outputs/apk/debug/app-debug.apk
```
