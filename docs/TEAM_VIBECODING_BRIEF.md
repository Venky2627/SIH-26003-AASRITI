# AASRITI — TEAM VIBECODING & DEVELOPMENT BRIEF

## SIH-26003 | Development Starts Today

### The one rule everyone must understand

**We are not six people building six separate apps.**

We are six developers building **one application with six subsystems**.

The goal is not:

> "My screen works."

The goal is:

> **"My subsystem works inside the complete AASRITI patient → intelligence → caregiver → doctor pipeline."**

---

# 1. SOURCE OF TRUTH

Before writing code, everyone reads:

`docs/INTEGRATION_BIBLE.md`

That document defines:

* domain models
* repository contracts
* navigation routes
* subsystem ownership
* integration expectations
* mock/demo data
* architectural boundaries

The architecture principle is:

**Room SQLite → Repository → Domain Models → Engines/UI**

Room entities must not leak into UI/ViewModels. The existing plan explicitly establishes pure Kotlin domain models and mapper boundaries.

### Absolute rule

**Do not invent a second version of something that already exists.**

---

# 2. VIBECODING RULE #1 — NEVER TRUST GENERATED CODE BLINDLY

AI can produce code extremely quickly. It can also produce obsolete APIs, duplicated classes, and broken scopes.

## AI writes code. Humans approve architecture.

Every developer must understand what their generated code does before committing it.

Do NOT paste 500 lines from an AI agent and immediately commit.

---

# 3. VIBECODING RULE #2 — SMALL PROMPTS BEAT GIANT PROMPTS

Do NOT tell the AI: *"Build the entire caregiver module."*  
Instead, prompt for single components, inspect the diff, test, and commit.

---

# 4. EVERY PERSON MUST STAY INSIDE THEIR OWN LANE

* **Jasleen**: Persistence / Room / repositories / data synchronization.
* **Venkatesh**: Adaptive + Priority + Trend engines, orchestration, infrastructure/build stability.
* **Krishna**: Game framework + Flower Match + Games 1–3.
* **Bhavya**: Games 4–6 + cultural theme system.
* **Shravani**: Caregiver + ASHA + reminder UI.
* **Kimaya**: Elder experience + Memory Garden + Care Circle + Doctor portal.

---

# 5. DO NOT EDIT SHARED CONTRACTS CASUALLY

High-risk files:
```text
domain/model/*
domain/repository/*
navigation/AppRoutes.kt
demo/AasritiMockProvider.kt
core/
build.gradle.kts
settings.gradle.kts
AndroidManifest.xml
```

Before changing: announce, explain, discuss, make the smallest change, and rebuild.

---

# 6. GIT RULES & BRANCHES

Everyone works on their assigned feature branch:
```text
feature/jasleen/backend-data-sync
feature/venkatesh/backend-engines-security
feature/krishna/frontend-games-framework
feature/bhavya/frontend-cultural-games
feature/shravani/frontend-caregiver-asha
feature/kimaya/frontend-elder-doctor
```

Commit frequently with conventional commit syntax (`feat:`, `fix:`, `test:`, `ui:`).

---

# 7. THE GOLDEN PATH (FIRST VERTICAL SLICE)

Our first milestone is NOT building all 6 games or complex sync. It is:

```text
Elder Home
    ↓
Flower Match
    ↓
Telemetry
    ↓
CognitiveInsightOrchestrator
    ↓
Adaptive Engine
    ↓
Trend Engine
    ↓
Priority Engine
    ↓
Caregiver Dashboard
    ↓
Doctor Trend View
```

---

# 8. WHAT EACH PERSON BUILDS FIRST

* **KRISHNA**: `CommonGameFramework` $\rightarrow$ Flower Match $\rightarrow$ `GameSession` telemetry $\rightarrow$ Orchestrator.
* **JASLEEN**: `Patient`, `GameSession`, `CareLog` Room DAOs and Mappers.
* **VENKATESH**: `CognitiveInsightOrchestrator` uniting `AdaptiveEngine`, `PriorityEngine`, and `TrendEngine`.
* **BHAVYA**: `AasritiColorTokens`, Typography, and Game 4 (Categorisation).
* **SHRAVANI**: Caregiver Dashboard $\rightarrow$ Today's Priority Card (consuming `PriorityEngine`).
* **KIMAYA**: Elder Home $\rightarrow$ Flower Match launch $\rightarrow$ Doctor Trend View.

---

# 9. INTEGRATION CHECKPOINTS

* **Checkpoint 1**: Feature branch compiles independently against `AasritiMockProvider`.
* **Checkpoint 2**: Merge Flower Match + Telemetry + Adaptive Engine.
* **Checkpoint 3**: Merge Caregiver Dashboard + Priority Engine.
* **Checkpoint 4**: Merge Doctor Portal + Trend Engine.
* **Checkpoint 5**: Full APK build & rehearsal.
