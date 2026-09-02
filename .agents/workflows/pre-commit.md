# Workflow: /pre-commit

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
* **Verify**: Every single change must belong to your assigned feature scope.
* **Detect Unrelated Changes**: If the AI assistant reformatted or altered unrelated files, revert them:
  ```bash
  git checkout -- <unrelated-file>
  ```

### Step 3: Run Local Unit Tests
```bash
./gradlew testDebugUnitTest
```
Confirm all tests pass (`BUILD SUCCESSFUL`).

### Step 4: Commit with Conventional Format
```bash
git add <intended-files>
git commit -m "<type>(<scope>): <clear descriptive action>"
```
Valid types: `feat:`, `fix:`, `test:`, `refactor:`, `docs:`, `chore:`.

*(Do NOT push automatically; proceed to `/prepare-pr` when ready).*
