# AASRITI AI AGENT MASTER OPERATING SYSTEM & GOVERNANCE DIRECTIVE
## `/AGENTS.md` — Authoritative Protocol for Antigravity & AI Coding Assistants

> **APPLICATION**: AASRITI (SIH-26003: AI-Based Cognitive Gaming & Memory Assistance Platform)  
> **SCOPE**: Mandatory for Google Antigravity, Cursor, Claude Code, Gemini CLI, and all AI agents.  
> **PRIMARY OPERATIONAL DIRECTIVE**: **"The repository is the single source of truth. Read before coding, enforce role-based UI rules, protect main, automate Git lifecycles, and never bypass repository safeguards."**

---

## 🛑 MANDATORY AI OPERATING PROTOCOL (THE 20 GOLDEN RULES)

Every AI coding assistant entering this repository **MUST** execute and adhere to these 20 commands strictly in sequence:

1. **Read `AGENTS.md` first** before generating any code or executing bash mutations.
2. **Read `README.md`** to understand the high-level architecture and current state.
3. **Inspect repository state**: Verify Git status, active branch, and uncommitted diffs.
4. **Inspect current Git branch**: Check `git branch --show-current`.
5. **Inspect current working tree**: Never silently discard or overwrite existing uncommitted work.
6. **Read architecture guidance** (`/docs/ARCHITECTURE.md` and `/ARCHITECTURE.md`) relevant to the task.
7. **Read UI rules** (`/UI_RULES.md`, `/UI_COMPONENT_RULES.md`, `/UI_SCREEN_SPEC.md`) before touching any UI composable.
8. **Read security rules** (`/SECURITY.md`) before touching authentication, session tokens, or patient data.
9. **Read database/API rules** (`/docs/DATA_MODEL.md`, `/docs/API_SYNC_CONTRACT.md`) before modifying schema or contracts.
10. **Reuse existing functionality**: Check for existing components, mappers, or utilities before creating new ones.
11. **Preserve existing working behavior**: Never rewrite working code merely for aesthetic or stylistic preference.
12. **Follow repository contribution rules** (`/CONTRIBUTING.md`).
13. **Run appropriate validation**: Execute unit tests (`./gradlew testDebugUnitTest`) before claiming completion.
14. **Self-correct failures**: Investigate and fix own build/test errors automatically where safe.
15. **Commit meaningful changes**: Use concise conventional commit syntax (`feat:`, `fix:`, `docs:`, `ui:`).
16. **Push the contributor branch**: `git push -u origin <branch-name>`.
17. **Create/update Pull Request**: Populate `.github/pull_request_template.md` where permissions allow.
18. **Report exactly what changed**: Output the canonical **AASRITI CHANGE REPORT** at task completion.
19. **Never directly modify `main`**: Normal feature development occurs strictly in `feature/<member>/<task>`.
20. **Never bypass security, testing, or repository rules** merely to make a feature compile or pass.

---

## ⚡ 1. ANTIGRAVITY STARTUP BEHAVIOR

Whenever Antigravity is opened in this repository, it must begin by determining:
* **Repository Name**: AASRITI (`AI-Based-Cognitive-Gaming-Platform-for-Elderly-Dementia-Patients-in-NER`)
* **Remote Origin**: `https://github.com/TeamNameSIH/...`
* **Current Branch & Git Status**: Branch name, clean/dirty working tree, uncommitted files.
* **Technology Stack**: Native Android (Kotlin 1.9+, Jetpack Compose, Room SQLite, Scikit-Learn Decision Tree JSON runtime).
* **Governance Status**: Verify that `/UI_RULES.md`, `/UI_SCREEN_SPEC.md`, `/SECURITY.md`, and `/docs/ARCHITECTURE.md` are loaded.

**It must NOT blindly start editing files. It must understand the repository state first.**

---

## 👤 2. CONTRIBUTOR IDENTITY & THE "I JUST CLONED IT" EXPERIENCE

When a teammate opens Antigravity and says:
> *"I'm Kimaya, set me up."* (or *"I'm Venkatesh"*, *"I'm Jasleen"*, *"I'm Krishna"*, *"I'm Bhavya"*, *"I'm Shravani"*)

Antigravity must execute the following setup sequence:
1. Identify the contributor from the message.
2. Verify that the working tree is clean.
3. Check out the contributor's assigned task branch:
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/<member>/<task-name>
   ```
4. Output the concise initialization summary:
   ```text
   AASRITI workspace initialized.

   Contributor:      <Member Name>
   Repository:       AASRITI
   Base branch:      develop
   Working branch:   feature/<member>/<task-name>
   Repository rules: Loaded
   UI governance:    Loaded (Warm Ivory / Forest Green / AAA Contrast)
   Security rules:   Loaded (DPDPA 2023 / Room SQLite Source of Truth)
   Architecture:     Loaded (Clean Feature-First / On-device Decision Tree)
   Working tree:     Clean

   Ready. What feature would you like to build?
   ```

---

## 🔄 3. THE 20-STEP TASK EXECUTION MODEL ("I AM KIMAYA")

When a developer requests a feature (e.g. *"I'm Kimaya. I need to build the Memory Garden."*), Antigravity executes these 20 steps internally:

```
[1. Load AGENTS.md] ──► [2. Inspect State] ──► [3. Scope Feature] ──► [4. Find Reusable Code]
                                                                                │
[8. Cultural Rules] ◄── [7. Access Rules] ◄── [6. Screen Spec] ◄── [5. UI Rules]
        │
        ▼
[9. Data / API Req] ──► [10. DB Check] ──► [11. Reuse Check] ──► [12. Switch/Create Branch]
                                                                            │
[16. Review Diff] ◄── [15. Auto-Fix] ◄── [14. Validate/Test] ◄── [13. Implement Feature]
        │
        ▼
[17. Commit] ──► [18. Push Branch] ──► [19. Create PR] ──► [20. Output Change Report]
```

1. **Step 1**: Load `AGENTS.md`.
2. **Step 2**: Inspect repository state and active Git branch.
3. **Step 3**: Determine feature scope and affected subsystems.
4. **Step 4**: Find existing code (e.g., check `feature/memoryalbum/` or Room DAOs).
5. **Step 5**: Read patient UI rules (`/UI_RULES.md` — Very Low Density, $\ge 64\text{dp}$ touch targets).
6. **Step 6**: Read screen specifications (`/UI_SCREEN_SPEC.md` — `SCREEN_PATIENT_MEMORY_GARDEN`).
7. **Step 7**: Read accessibility rules (`/UI_ACCESSIBILITY_RULES.md` — WCAG AAA, non-punitive language).
8. **Step 8**: Read cultural rules (`/UI_HERITAGE_GUIDE.md` — Assam/Manipur/Meghalaya authentic framing).
9. **Step 9**: Determine backend/API/data synchronization requirements.
10. **Step 10**: Check if database migration is required (Room entity modification requires 2 approvals).
11. **Step 11**: Ensure no duplicate models or components are being introduced.
12. **Step 12**: Ensure development is on `feature/<member>/<task-name>`.
13. **Step 13**: Implement the feature using approved design tokens (`AasritiColorTokens`, `AasritiSpacing`).
14. **Step 14**: Run relevant validation tests (`./gradlew testDebugUnitTest`).
15. **Step 15**: If a test fails, investigate root cause, fix, and re-run.
16. **Step 16**: Review complete Git diff (`git diff`) and remove accidental edits/whitespace changes.
17. **Step 17**: Create a descriptive commit (`git commit -m "feat(memory): ..."`).
18. **Step 18**: Push contributor branch (`git push -u origin feature/...`).
19. **Step 19**: Open or update the Pull Request using `.github/pull_request_template.md`.
20. **Step 20**: Output the canonical **AASRITI CHANGE REPORT**.

---

## 📋 4. CANONICAL AASRITI CHANGE REPORT FORMAT

Whenever Antigravity finishes a contributor task, its response must conclude with this exact structured report:

```text
============================================================
AASRITI CHANGE REPORT
============================================================

CONTRIBUTOR:
<Member Name>

TASK:
<Task Title / Summary>

BRANCH:
<Working Branch Name>

FILES / AREAS CHANGED:
- <File path 1>
- <File path 2>

SYSTEMS AFFECTED:
[X] Frontend       [ ] Backend      [ ] Database
[ ] API            [ ] Auth         [ ] AI/ML
[X] UI             [X] Accessibility [ ] Security

RULES FOLLOWED:
- UI Governance: Followed /UI_RULES.md (Warm Ivory, Forest Green, Zero Dashboard)
- Accessibility: Followed /UI_ACCESSIBILITY_RULES.md (Touch target >= 64dp, Non-punitive copy)
- Architecture: Reused Clean Architecture / Feature-First patterns
- Security: Followed /SECURITY.md

TESTS:
PASS (Unit tests: <List passing test suites>)

BUILD:
PASS (Gradle debug APK assemble verified)

LINT / TYPE CHECK:
PASS (Kotlin compiler & ProGuard verified)

SECURITY:
PASS (Zero plaintext secret leaks detected)

DATABASE:
Not required (Existing Room schema preserved)

PULL REQUEST:
Created: PR #<Number> -> develop

CI:
Passing / Running on GitHub Actions

REMAINING ISSUES:
None

NEXT HUMAN ACTION:
Assign peer review to <Designated Reviewer> for approval.
============================================================
```

---

## 🛡️ 5. PROTECTED / INTEGRATION FILES (DO NOT CASUALLY TOUCH)

The following files are architectural integration points. Modifying them requires **2 designated reviewer approvals**:

| Protected File | Architectural Role | Designated Owner | Required Approvals |
| :--- | :--- | :--- | :---: |
| `AppNavigation.kt` | Top-level Jetpack Compose NavHost | **Jasleen** | 2 Approvals |
| `MainActivity.kt` | Application Activity entry point | **Jasleen** / **Venkatesh** | 2 Approvals |
| `AppDatabase.kt` | Room SQLite Database singleton & tables | **Jasleen** | 2 Approvals |
| `build.gradle.kts` / `settings.gradle.kts` | Root build configuration & dependencies | **Venkatesh** | 2 Approvals |
| `AndroidManifest.xml` | Application permissions & receivers | **Venkatesh** | 2 Approvals |
| `UI_RULES.md` / `core/ui/theme/` | Master UI governance & color tokens | **Bhavya** | 2 Approvals |
| `SECURITY.md` | Security policy & cryptographic rules | **Venkatesh** | 2 Approvals |
| `CommonGameFramework.kt` | Base engine contract for all 6 games | **Krishna** | 2 Approvals |

---

## 🚫 6. FORBIDDEN BEHAVIORS & REFACTORING GUARDRAILS

AI coding agents are strictly prohibited from:
1. **Introducing React Native, Flutter, or WebViews**: The stack is 100% Native Kotlin + Jetpack Compose.
2. **Introducing OTP / SMS / Cloud Email Auth**: Caregiver/Doctor/ASHA use local PIN; Patients use direct photo tap (Zero PIN).
3. **Renaming Folders or Moving Models Arbitrarily**: All structural paths must match `/docs/ARCHITECTURE.md`.
4. **Adding Arbitrary External Gradle Dependencies**: Every library must be justified and approved.
5. **Inventing Cloud ML or LLM Dependencies**: The adaptive engine uses an on-device Decision Tree (JSON runtime). Zero LLMs for core game logic.
6. **Making Dementia Diagnosis Claims**: The platform provides neutral functional signals; it **never** claims to diagnose dementia.
7. **Bypassing the Sync Queue**: Room SQLite is the single source of truth; direct UI-to-Firebase writes are forbidden.
8. **Introducing Rainbow UI or Floating Glassmorphism**: Colors must strictly use `AasritiColorTokens`.
