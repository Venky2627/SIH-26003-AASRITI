# AASRITI — CONTRIBUTING WORKFLOW

## Repository Branch Model

- **`main`**:
  Stable/protected release branch.
  Never directly commit casually.

- **`develop`**:
  Shared integration branch.
  All contributors synchronize from here.

- **`feature/` branches**:
  Individual contributor work (`feature/<member>/<task-name>`).

---

## Starting Work

Every contributor must first check repository state:

```powershell
git status
git branch --show-current
```

Then synchronize local `develop`:

```powershell
git checkout develop
git pull origin develop
```

Then switch to your assigned feature branch:

```powershell
git checkout feature/YOUR-BRANCH
```

Merge the latest integration baseline into your branch:

```powershell
git merge develop
```

If conflicts exist:
**Do not blindly resolve.** Understand both sides of the conflict and preserve canonical contracts.

---

## Before Opening Antigravity

Every contributor must ensure the local repository is cleanly synchronized.

Upon session startup, Antigravity must read:

1. `PROJECT_CONTEXT.md`
2. `OWNERSHIP.md`
3. `CONTRIBUTING.md`

and inspect:

```powershell
git status
git branch --show-current
git log -10 --oneline
```

---

## Mandatory Antigravity Startup Protocol

> **"Do not modify code immediately. First inspect the repository state, read the collaboration documents, identify ownership boundaries, and report your understanding."**

---

## During Work

Follow these 10 core rules strictly:

1. **Work primarily inside assigned ownership** as defined in `OWNERSHIP.md`.
2. **Do not casually modify shared files** (such as `MainActivity.kt`, `AppDatabase.kt`, or `Entities.kt`).
3. **Do not redesign architecture** without explicit integration lead approval.
4. **Do not introduce external dependencies** casually into Gradle.
5. **Do not introduce cloud requirements** (Firebase, REST APIs, remote AI, or cloud TTS). AASRITI is 100% offline.
6. **Do not fabricate clinical data** or mock performance metrics.
7. **Do not change Room schema** without migration planning and integration signoff.
8. **Do not touch `main`**. Feature work is completed on feature branches and integrated via `develop`.
9. **Keep commits focused** on single tasks or fixes.
10. **Do not mix unrelated cleanup** or formatting with feature work.

---

## Before Commit

Run relevant verification. For integration-sensitive changes:

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
```

Then inspect the exact changes:

```powershell
git status
git diff
```

Review exactly what changed. Remove any accidental whitespace edits or debug logs.

---

## Commit Rules

Use focused conventional commit syntax:

- `feat(scope): description`
- `fix(scope): description`
- `docs(scope): description`
- `test(scope): description`
- `refactor(scope): description`

Do **NOT** use vague commit messages such as:
- `"changes"`
- `"update"`
- `"final"`
- `"fix stuff"`

---

## Push Workflow

Stage only the relevant files:

```powershell
git add specific/file1 specific/file2
git commit -m "feat(scope): meaningful description"
git push origin feature/YOUR-BRANCH
```

**Do not use `git add .`** unless the contributor has explicitly inspected every changed file via `git status` and `git diff`.

---

## Integration Workflow

1. Contributors push their tested feature branch to remote origin.
2. Integration-sensitive changes must be reviewed against:
   - `PROJECT_CONTEXT.md`
   - `OWNERSHIP.md`
   - Room database contracts & migrations
   - Canonical patient identity (`"aita_borah_01"`)
   - Existing `develop` baseline
3. Do not automatically merge unreviewed large changes into `main`.

---

## After Integration

Before starting another task:

1. Switch to `develop` and pull latest changes:
   ```powershell
   git checkout develop
   git pull origin develop
   ```
2. Never assume your local branch is up to date.

---

## AI Handoff Rule

The repository is the single source of truth and shared memory across the team.

When architecture or contracts change materially:

- Update `PROJECT_CONTEXT.md` as part of the same commit or coordinated integration commit.
- Do not rely on chat messages, informal notes, or previous AI conversation transcripts as the sole source of truth.
