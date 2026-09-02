# Workflow: /start-work

Execute this workflow whenever starting a coding session:

### Step 1: Branch & Status Inspection
```bash
git branch --show-current
git status
```

### Step 2: Contributor Identity Detection
* If on `feature/<member>/<feature>`: Confirm active contributor, feature scope, and working directories.
* If on `develop`: Stop and instruct the developer to branch off `develop`:
  ```bash
  git checkout -b feature/<member>/<feature>
  ```
* If on `main`: Stop and warn that `main` is protected and cannot be modified.

### Step 3: Check `develop` Freshness
```bash
git fetch origin develop
git log HEAD..origin/develop --oneline
```
If your branch is behind `develop`, execute `/sync-with-develop`.

### Step 4: Confirm Working Scope
Identify the target files within your assigned module and ensure the working tree is clean before prompting your AI assistant.
