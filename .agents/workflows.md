# AASRITI ANTIGRAVITY WORKFLOW CATALOG
## `.agents/workflows.md` — Authoritative Slash Command Workflows

> **USAGE**: Antigravity executes these workflows automatically or when requested by a contributor via `/start-work`, `/sync-with-develop`, `/pre-commit`, or `/prepare-pr`.

---

## 🚀 1. WORKFLOW: `/start-work`
Execute this workflow whenever starting a coding session:

### Step 1: Branch & Status Inspection
```bash
git branch --show-current
git status
```
- If on `feature/<member>/<task-name>`: Confirm active contributor, feature scope, and working directories from [`TEAM_ALLOCATION.md`](../docs/TEAM_ALLOCATION.md).
- If on `develop`: Stop and instruct the developer to branch off `develop`:
  ```bash
  git checkout -b feature/<member>/<task-name>
  ```
- If on `main`: Stop and warn that `main` is protected.

### Step 2: Check `develop` Freshness
```bash
git fetch origin develop
git log HEAD..origin/develop --oneline
```
If your branch is behind `develop`, execute `/sync-with-develop`.

### Step 3: Confirm Working Scope
Identify the target files within your assigned module and ensure the working tree is clean before beginning implementation.

---

## 🔄 2. WORKFLOW: `/sync-with-develop`
Execute this workflow to keep your feature branch aligned with the latest changes in `develop`:

### Step 1: Ensure Working Tree is Clean
```bash
git status
```
If you have uncommitted changes, either commit them or stash them:
```bash
git stash save "WIP before sync"
```

### Step 2: Fetch and Update Local `develop`
```bash
git checkout develop
git pull origin develop
```

### Step 3: Integrate `develop` into Feature Branch
```bash
git checkout feature/<member>/<task-name>
git merge develop
```

### Step 4: Handle Conflicts (If Any)
- **STOP** if git reports conflicts.
- **DO NOT** blindly run `checkout --ours` or `checkout --theirs`.
- Open conflicting files, understand both changes, and resolve manually.
- If conflicts involve shared infrastructure (`AppDatabase.kt`, `MainActivity.kt`), consult your designated peer reviewer from [`TEAM_ALLOCATION.md`](../docs/TEAM_ALLOCATION.md).

### Step 5: Verify Build Afterward
```bash
./gradlew testDebugUnitTest
```
If you stashed changes in Step 1, restore them:
```bash
git stash pop
```

---

## 🔍 3. WORKFLOW: `/pre-commit`
Execute this workflow before staging and committing any changes:

### Step 1: Inspect Status & Diff Stat
```bash
git status
git diff --stat
```

### Step 2: Detailed Line-by-Line Diff Review
```bash
git diff
```
- **Verify**: Every single change must belong to your assigned feature scope.
- **Detect Unrelated Changes**: If the AI assistant reformatted or altered unrelated files, revert them:
  ```bash
  git checkout -- <unrelated-file>
  ```

### Step 3: Run Local Validation
```bash
./gradlew testDebugUnitTest
```
Confirm all tests pass (`BUILD SUCCESSFUL`).

### Step 4: Commit with Conventional Format
```bash
git add <intended-files>
git commit -m "<type>(<scope>): <clear descriptive action>"
```
Valid types: `feat:`, `fix:`, `test:`, `docs:`, `ui:`, `chore:`.

---

## 📦 4. WORKFLOW: `/prepare-pr`
Execute this workflow when your feature is complete, tested, and ready for review:

### Step 1: Push Feature Branch to Remote
```bash
git push -u origin feature/<member>/<task-name>
```

### Step 2: Open Pull Request Targeting `develop`
- **Base Branch**: `develop` (NEVER target `main`).
- **Compare Branch**: `feature/<member>/<task-name>`.

### Step 3: Complete PR Template Checklist
Fill out all fields in [`.github/pull_request_template.md`](../.github/pull_request_template.md):
- Describe what changed and why.
- Disclose whether Room database, navigation, or shared architecture were affected.
- Confirm offline Airplane Mode verification.
- Verify UI compliance against [`UI_RULES.md`](../UI_RULES.md).

### Step 4: Tag Assigned Default Reviewer
- Refer to [`TEAM_ALLOCATION.md`](../docs/TEAM_ALLOCATION.md) for your designated reviewer:
  $$\text{Venkatesh} \rightarrow \text{Jasleen} \rightarrow \text{Krishna} \rightarrow \text{Bhavya} \rightarrow \text{Shravani} \rightarrow \text{Kimaya} \rightarrow \text{Venkatesh}$$
- If shared infrastructure was touched, tag a second reviewer.

### Step 5: Wait for CI & Review Approval
Ensure GitHub Actions passes (`AASRITI Continuous Integration`). Once approved, squash and merge into `develop`, then delete your feature branch.
