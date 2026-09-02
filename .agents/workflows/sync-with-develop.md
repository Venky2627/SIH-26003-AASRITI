# Workflow: /sync-with-develop

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
git checkout feature/<member>/<feature>
git merge develop
```

### Step 4: Handle Conflicts (If Any)
* **STOP** if git reports conflicts.
* **DO NOT** blindly run `checkout --ours` or `checkout --theirs`.
* Open conflicting files, understand both changes, resolve manually.
* If conflicts involve shared infrastructure (`AppDatabase.kt`, `MainActivity.kt`), consult your designated peer reviewer.

### Step 5: Verify Build Afterward
```bash
./gradlew testDebugUnitTest
```
If you stashed changes in Step 1, restore them:
```bash
git stash pop
```
