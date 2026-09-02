# Workflow: /prepare-pr

Execute this workflow when your feature is complete, tested, and ready for review:

### Step 1: Push Feature Branch to Remote
```bash
git push -u origin feature/<member>/<feature>
```

### Step 2: Open Pull Request Targeting `develop`
* **Base Branch**: `develop` (NEVER target `main`).
* **Compare Branch**: `feature/<member>/<feature>`.

### Step 3: Complete PR Template Checklist
Fill out all fields in `.github/pull_request_template.md`:
* Describe what changed and why.
* Disclose whether Room database, navigation, or shared architecture were affected.
* Confirm offline Airplane Mode verification.
* Attach UI screenshots showing large touch targets ($\ge 60\times 60\text{ dp}$).

### Step 4: Tag Assigned Default Reviewer
* Refer to `.agents/rules/01-team-map.md` for your designated reviewer.
* If shared infrastructure was touched, tag a second reviewer.

### Step 5: Wait for CI & Review Approval
Ensure GitHub Actions passes (`Build APK & Run Unit Tests`). Once approved, squash and merge into `develop`, then delete your feature branch.
