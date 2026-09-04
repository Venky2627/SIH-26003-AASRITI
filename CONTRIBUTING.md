# AASRITI CONTRIBUTOR & ENGINEERING WORKFLOW GUIDE
## `/CONTRIBUTING.md` — The Standard Development Operating System

> **WELCOME TO AASRITI (SIH-26003)**  
> **CORE ENGINEERING PRINCIPLE**: **"Hard to break, simple to contribute. The Git repository is the single source of truth."**

---

## 🌟 1. THE 11-STAGE DEVELOPMENT LIFECYCLE

Every contribution to AASRITI — whether frontend UI, backend sync, database migration, ML logic, security rule, or documentation — follows this exact predictable lifecycle:

```
1. CLONE ──► 2. RUN ──► 3. BRANCH ──► 4. WORK ──► 5. TEST ──► 6. COMMIT
                                                                  │
11. MERGE ◄── 10. APPROVE ◄── 9. REVIEW ◄── 8. CI ◄── 7. PUSH & PR ◄┘
```

### Stage 1: CLONE
Clone the official repository to your machine:
```bash
git clone https://github.com/TeamNameSIH/AI-Based-Cognitive-Gaming-Platform-for-Elderly-Dementia-Patients-in-NER.git AASRITI
cd AASRITI
```

### Stage 2: RUN
Open the project in Android Studio (Jellyfish / Iguana / Hedgehog with JDK 17). Build and run on an Android device or emulator (API 34). Confirm that the default `AasritiTheme` loads with `Warm Ivory` backgrounds and large buttons.

### Stage 3: BRANCH
**NEVER WORK DIRECTLY ON `main` OR `develop`.**  
Create your feature branch off `develop` (or tell Antigravity who you are and what you want to build):
```bash
git checkout develop
git pull origin develop
git checkout -b feature/<member>/<task-name>
# Examples:
# git checkout -b feature/kimaya/memory-garden
# git checkout -b feature/venkatesh/priority-engine
# git checkout -b fix/shravani/reminder-alarm
```

### Stage 4: WORK (REUSE EXISTING PATTERNS)
- **UI Changes**: Read [`/UI_RULES.md`](file:///UI_RULES.md) and [`/UI_COMPONENT_RULES.md`](file:///UI_COMPONENT_RULES.md). Reuse `AasritiColorTokens`, `AasritiTypography`, and `AasritiSpacing`.
- **Backend / Database Changes**: Read [`/docs/ARCHITECTURE.md`](file:///docs/ARCHITECTURE.md) and [`/SECURITY.md`](file:///SECURITY.md). Room SQLite is the source of truth.
- **Patient Views**: Keep density VERY LOW. No scoreboards, no dashboards, touch targets $\ge 64\text{dp}$.

### Stage 5: TEST
Run local checks and tests before committing:
```bash
# Run unit tests
./gradlew testDebugUnitTest

# Test offline capability: Turn on Airplane Mode on your test device!
```

### Stage 6: COMMIT
Make meaningful, clean commits:
```bash
git add .
git commit -m "feat(memory): implement Memory Garden photo viewer with audio voice note"
```

### Stage 7: PUSH & OPEN PULL REQUEST
Push your branch to GitHub:
```bash
git push -u origin feature/<member>/<task-name>
```
Open a Pull Request targeting `develop`. The template in `.github/pull_request_template.md` will automatically load. Complete the checklist.

### Stage 8: CI (GITHUB ACTIONS)
Automated GitHub Actions will build the project, run unit tests, check for secret leaks, and verify UI governance file presence. If CI fails, inspect the log, fix locally, and push to the same branch.

### Stage 9: REVIEW
Your designated peer reviewer conducts code review:
* **Venkatesh** $\leftrightarrow$ **Jasleen**
* **Jasleen** $\leftrightarrow$ **Krishna**
* **Krishna** $\leftrightarrow$ **Bhavya**
* **Bhavya** $\leftrightarrow$ **Shravani**
* **Shravani** $\leftrightarrow$ **Kimaya**
* **Kimaya** $\leftrightarrow$ **Venkatesh**

### Stage 10: APPROVE
Address feedback. Once the reviewer gives explicit approval (LGTM), the PR is ready.

### Stage 11: MERGE
Merge via **Squash and Merge** into `develop`. Branch is deleted after successful merge.

---

## 🛑 2. PROTECTED BRANCH POLICY

* **`main` is protected**: Represents production-ready code. Direct pushes are blocked.
* **`develop` is the integration branch**: Features merge into `develop` via approved PRs.
* **Protected Files Rule**: Modifying integration files (`MainActivity.kt`, `DementiaDatabase.kt`, `build.gradle.kts`, `AndroidManifest.xml`, `UI_RULES.md`, `SECURITY.md`) strictly requires **2 reviewer approvals**.

---

## 🎯 3. PULL REQUEST-FIRST CULTURE

Every meaningful change must pass through a Pull Request. No exceptions for UI, backend, documentation, or security files. Every PR must clearly document:
1. **What changed**
2. **Why it changed**
3. **Systems affected** (Frontend, Database, API, ML, UI, etc.)
4. **Testing performed** (including offline Airplane Mode verification)
