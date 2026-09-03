# Antigravity & AI Agent Master Operating System — SIH-26003 (SmritiSetu)

> **PROJECT**: SIH-26003 — AI-Based Cognitive Gaming and Memory Assistance Platform for Elderly Dementia Patients in the North Eastern Region (NER)  
> **TEAM**: 6-Member Student Team (Smart India Hackathon)  
> **IMMEDIATE CRITICAL DEADLINE**: **SEPTEMBER 5 — PPT SUBMISSION**  
> **PRIMARY OPERATIONAL DIRECTIVE**: Freeze architecture, eliminate ambiguity, protect integration files, prevent duplicate work, and enforce disciplined feature-first clean architecture.

---

## 🛑 MANDATORY AI OPERATING PROTOCOL

Every AI coding assistant (Google Antigravity, Cursor, Claude Code, Gemini CLI) operating in this repository **MUST** execute the following protocol strictly in sequence. **DO NOT GENERATE CODE IMMEDIATELY.**

```
[Repository Opened]
       │
       ▼
[STEP 0: Read Before Coding] (AGENTS.md, PROJECT_STATE.md, TASK_BOARD.md, TEAM_ALLOCATION.md, ARCHITECTURE.md)
       │
       ▼
[STEP 1: Identify Developer] ("Which team member are you working as?")
       │
       ▼
[STEP 2: Output Task Execution Contract] (Task, Owner, Dependencies, Safe Files, Protected Files, DoD)
       │
       ▼
[STEP 3: Implement Within Assigned Module Boundary] (Zero touching of protected integration files without authorization)
       │
       ▼
[STEP 4: Post-Task Update] (Update PROJECT_STATE.md and TASK_BOARD.md)
```

---

## STEP 0 — READ BEFORE CODING

Before modifying or creating any code, the AI agent must read:
1. [`AGENTS.md`](file:///AGENTS.md) (This operational directive)
2. [`PROJECT_STATE.md`](file:///PROJECT_STATE.md) (Single source of truth for phase, status, and active blockers)
3. [`TASK_BOARD.md`](file:///TASK_BOARD.md) (Prioritized backlog, ownership, and dependencies)
4. [`TEAM_ALLOCATION.md`](file:///TEAM_ALLOCATION.md) (Subsystem ownership, primary owners, and secondary reviewers)
5. [`ARCHITECTURE.md`](file:///ARCHITECTURE.md) (Authoritative architectural rules and data flow)

Then determine:
* Current project phase (**Phase 0: Architecture Freeze & PPT Readiness**)
* Active task and dependencies
* Assigned team member boundary
* Integration risk level

---

## STEP 1 — IDENTIFY THE DEVELOPER

The AI must check the current git branch:
```bash
git branch --show-current
```

### Branch-to-Member Mapping:
| Branch Pattern | Developer | Primary Subsystem Ownership | Secondary Reviewer |
| :--- | :--- | :--- | :--- |
| `feature/venkatesh/*` | **Venkatesh** | Architecture Integration, Adaptive Engine, Priority Engine | Jasleen |
| `feature/jasleen/*` | **Jasleen** | Core Application, Data Layer, Room, Repositories, Domain Models | Krishna |
| `feature/krishna/*` | **Krishna** | Game Framework, Games 1–3 (Trivia, Cue Card, Sequencing) | Bhavya |
| `feature/bhavya/*` | **Bhavya** | Games 4–6 (Categorisation, Market, Pattern/Rotation), Cultural Theme Engine | Shravani |
| `feature/shravani/*` | **Shravani** | Caregiver Workflows, ASHA Workflows, Offline Reminders, SOS | Kimaya |
| `feature/kimaya/*` | **Kimaya** | Doctor Workflows, Analytics, Longitudinal Trends, Reports | Venkatesh |

* If on `main` or `develop`: Ask the developer:
  > *"Which team member are you working as? (1. Venkatesh, 2. Jasleen, 3. Krishna, 4. Bhavya, 5. Shravani, 6. Kimaya). Please create your feature branch: `git checkout -b feature/<member>/<task-name>` before coding."*
* Once identified: Consult [`TEAM_ALLOCATION.md`](file:///TEAM_ALLOCATION.md) and present **EXACTLY ONE PRIMARY TASK** and optionally **ONE SECONDARY TASK**. Do not overwhelm the developer.

---

## STEP 2 — TASK EXECUTION PROTOCOL

Before writing any code, the AI must output the following execution contract to the chat:

```text
======================================================================
AI EXECUTION CONTRACT
======================================================================
TASK:               <Task ID and Title from TASK_BOARD.md>
DEVELOPER:          <Active Team Member>
REVIEWER:           <Designated Peer Reviewer>
DEPENDENCIES:       <Required models/interfaces that must be present>
SAFE FILES:         <Files/folders within the developer's assigned module>
PROTECTED FILES:    <DO NOT TOUCH: AppNavigation.kt, DementiaDatabase.kt, etc.>
DEFINITION OF DONE: <Exact completion and verification criteria>
======================================================================
```

---

## STEP 3 — AFTER TASK COMPLETION

Upon completing a task and verifying with unit tests, the AI **MUST** instruct the developer to update both [`PROJECT_STATE.md`](file:///PROJECT_STATE.md) and [`TASK_BOARD.md`](file:///TASK_BOARD.md) with:

```text
STATUS:               DONE
OWNER:                <Member Name>
DATE:                 <YYYY-MM-DD>
FILES CREATED:        <List of new files>
FILES MODIFIED:       <List of modified files>
DEPENDENCIES RESOLVED:<List of completed interfaces/models>
NEXT RECOMMENDED TASK:<Single next priority>
INTEGRATION NOTES:    <Notes for secondary reviewer or shared modules>
```

---

## STEP 4 — PROTECTED / INTEGRATION FILES (DO NOT CASUALLY TOUCH)

The following files are architectural integration points. Modifying them can break parallel work across the team.

| Protected File | Architectural Role | Designated Owner | Required Approvals |
| :--- | :--- | :--- | :--- |
| `AppNavigation.kt` | Top-level Jetpack Compose NavHost | **Jasleen** | 2 Approvals |
| `DementiaDatabase.kt` / `AppDatabase.kt` | Room SQLite Database singleton & tables | **Jasleen** | 2 Approvals |
| `RepositoryModule.kt` / DI containers | Dependency injection wiring | **Jasleen** / **Venkatesh** | 2 Approvals |
| `build.gradle.kts` / `settings.gradle.kts` | Build configuration & dependencies | **Venkatesh** | 2 Approvals |
| `AndroidManifest.xml` | Application permissions & receivers | **Venkatesh** | 2 Approvals |
| `Theme.kt` / `colors.xml` | Global UI styling & WCAG AAA contrast | **Bhavya** | 2 Approvals |
| `CommonGameFramework.kt` | Base engine contract for all 6 games | **Krishna** | 2 Approvals |

### AI Guardrail for Protected Files:
If a task requires modifying a protected file:
1. **STOP** and inform the user.
2. Explain specifically why the modification is necessary.
3. Keep the diff minimal (e.g., adding one line to a navigation route enum or registering one Room DAO).
4. Disclose the modification in the PR template.

---

## STEP 5 — FORBIDDEN BEHAVIORS & NO RANDOM REFACTORING

AI coding agents are strictly prohibited from:
1. **Introducing React Native, Expo, or WebViews**: The stack is 100% Native Kotlin + Jetpack Compose.
2. **Introducing OTP / SMS / Email-Password Auth**: Caregiver/Doctor/ASHA use local PIN; Patients use direct photo/avatar tap (zero PIN).
3. **Renaming Folders or Moving Models Arbitrarily**: All structural paths must match [`ARCHITECTURE.md`](file:///ARCHITECTURE.md).
4. **Adding Arbitrary Gradle Dependencies**: Every library must be justified and approved.
5. **Inventing Cloud ML or LLM Dependencies**: The adaptive engine uses an explainable on-device Decision Tree (JSON runtime) in Kotlin. Zero LLMs for core game logic.
6. **Making Dementia Diagnosis Claims**: The platform provides neutral functional signals (reaction time, error frequency, hesitation); it **never** claims to diagnose dementia or score dementia progression.
7. **Bypassing the Sync Queue**: Room is the single source of truth; Firebase is exclusively a background replication layer. Direct UI-to-Firebase writes are forbidden.
