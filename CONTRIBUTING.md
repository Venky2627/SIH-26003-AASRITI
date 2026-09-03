# Contributing to SmritiSetu (SIH-26003)

Welcome to the SmritiSetu development team! We are **6 equal contributors** building an AI-based cognitive gaming and memory assistance platform for rural North Eastern India.

---

## 🌿 1. Git Branching Model

To ensure seamless parallel work and prevent merge conflicts across the 6 members, we enforce a strict 3-tier branch hierarchy:

```
[feature/<member>/<feature-name>] ──(PR + Review + CI)──> [develop] ──(Release Sign-off)──> [main]
```

### Golden Git Directives:
1. **NO DIRECT PUSHES TO `main` OR `develop`**.
2. **NO FORCE PUSHES (`git push --force`) ON SHARED BRANCHES**.
3. All development must occur on your assigned feature branch:
   ```bash
   git checkout -b feature/<member>/<feature-name>
   ```

### Branch Naming Convention:
* `feature/venkatesh/<task-name>` (e.g., `feature/venkatesh/priority-engine`)
* `feature/jasleen/<task-name>` (e.g., `feature/jasleen/domain-mappers`)
* `feature/krishna/<task-name>` (e.g., `feature/krishna/game-framework`)
* `feature/bhavya/<task-name>` (e.g., `feature/bhavya/cultural-theme-engine`)
* `feature/shravani/<task-name>` (e.g., `feature/shravani/caregiver-quick-log`)
* `feature/kimaya/<task-name>` (e.g., `feature/kimaya/doctor-trends-screen`)

---

## 🧭 2. The 12-Step Contributor Lifecycle

Every feature or bugfix follows this exact path:

1. **Pull Latest `develop`**:
   ```bash
   git checkout develop
   git pull origin develop
   ```
2. **Create Feature Branch**:
   ```bash
   git checkout -b feature/<member>/<feature-name>
   ```
3. **Open Antigravity / Gemini**:
   - The AI assistant reads `AGENTS.md`, detects your branch, and outputs your execution contract.
4. **Code Within Module Scope**:
   - Stay strictly within your assigned safe directories (see [`TEAM_ALLOCATION.md`](file:///TEAM_ALLOCATION.md)).
5. **Inspect Diffs Before Staging**:
   ```bash
   git status
   git diff --stat
   git diff
   ```
   *Verify line-by-line. If unrelated files were touched, revert them.*
6. **Run Local Unit Tests**:
   ```bash
   ./gradlew testDebugUnitTest
   ```
7. **Commit with Conventional Messages**:
   ```bash
   git add <modified-files>
   git commit -m "feat(priority): implement deterministic triage rules"
   ```
8. **Push Feature Branch**:
   ```bash
   git push -u origin feature/<member>/<feature-name>
   ```
9. **Update Tracking**:
   - Update `TASK_BOARD.md` (set task status to `REVIEW`) and `PROJECT_STATE.md`.
10. **Open Pull Request Targeting `develop`**:
    - Complete all checklist items in `.github/pull_request_template.md`.
    - Tag your designated peer reviewer.
11. **Peer Review & CI Verification**:
    - Reviewer inspects code; GitHub Actions runs build and tests.
12. **Squash and Merge**:
    - Once approved, squash and merge into `develop`, then delete your feature branch.

---

## ✍️ 3. Commit Convention

We use standard Conventional Commits format:
* `feat:` A new feature, screen, or domain use case
* `fix:` A bug fix or crash resolution
* `test:` Adding or updating unit tests
* `refactor:` Code restructuring without functional change
* `docs:` Documentation or diagram updates
* `chore:` Gradle build configs, assets, or repository settings

---

## 🔒 4. Protected Files & 2-Approval Rule

Modifying integration-sensitive files requires **TWO approvals** (Primary Custodian + Secondary Reviewer):
* `AppNavigation.kt` (Jasleen + Venkatesh)
* `DementiaDatabase.kt` / `AppDatabase.kt` (Jasleen + Venkatesh)
* `build.gradle.kts` / `settings.gradle.kts` (Venkatesh + Jasleen)
* `AndroidManifest.xml` (Venkatesh + Shravani)
* `Theme.kt` / `colors.xml` (Bhavya + Krishna)
* `CommonGameFramework.kt` (Krishna + Bhavya)

---

## ⚔️ 5. Conflict Resolution Protocol

When merge conflicts occur:
1. **NEVER** use `checkout --ours` or `checkout --theirs` blindly.
2. **NEVER** ask an AI assistant to "auto-resolve all conflicts" without manual review.
3. Open conflicting files, review both changes, merge them logically, and verify with:
   ```bash
   ./gradlew testDebugUnitTest
   ```
4. Commit the resolution and notify your designated reviewer.
