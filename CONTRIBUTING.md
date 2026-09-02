# Contributing to SmritiSetu (SIH26003)

Welcome to the SmritiSetu development workflow. This project is built by a team of **six equal contributors** working concurrently with AI-assisted workflows (vibecoding).

---

## 🧭 The 12-Step Development Lifecycle

Every contributor follows this exact path for every feature and fix:

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/TeamNameSIH/AI-Based-Cognitive-Gaming-Platform-for-Elderly-Dementia-Patients-in-NER.git
   cd AI-Based-Cognitive-Gaming-Platform-for-Elderly-Dementia-Patients-in-NER
   ```

2. **Update `develop`**:
   ```bash
   git checkout develop
   git pull origin develop
   ```

3. **Create Feature Branch**:
   ```bash
   git checkout -b feature/<member>/<feature-name>
   ```

4. **Code with AI Coding Assistant**:
   - Guide your AI agent (Antigravity, Cursor, etc.) within your specific feature boundary.
   - Do not instruct the AI to rewrite shared architectures.

5. **Review Working Tree & Diffs**:
   ```bash
   git status
   git diff --stat
   git diff
   ```
   *Inspect every line modified. If the AI touched unrelated files, revert them immediately.*

6. **Build & Test Locally**:
   ```bash
   ./gradlew testDebugUnitTest
   ./gradlew assembleDebug
   ```

7. **Commit Meaningful Work**:
   ```bash
   git add <modified-files>
   git commit -m "feat(reminders): implement offline alarm scheduling"
   ```

8. **Push Feature Branch**:
   ```bash
   git push -u origin feature/<member>/<feature-name>
   ```

9. **Create Pull Request**:
   - Open PR targeting `develop` (NEVER directly to `main`).
   - Fill out the PR template completely, disclosing any shared-file impacts.

10. **Peer Review**:
    - Tag your designated peer reviewer (see [Rotation](#peer-review-rotation)).
    - Address comments collaboratively.

11. **Merge**:
    - Merge only after approval and green CI.
    - Prefer **Squash and Merge** to keep the `develop` history clean and understandable.

12. **Delete Merged Branch**:
    - Delete the remote and local feature branch after merging to prevent stale branch clutter.

---

## 🌿 Branch Naming Convention

Always use: `feature/<member>/<feature>`

Valid Examples:
- `feature/venkatesh/game-framework`
- `feature/jasleen/family-trivia`
- `feature/krishna/voice-cue-card`
- `feature/shravani/reminders`
- `feature/bhavya/accessibility`
- `feature/kimaya/sequencing-categorisation`

Invalid Examples (Forbidden):
- `testbranch`, `newfinal`, `working`, `temp`, `mycode`, `venky-branch`

---

## ✍️ Commit Conventions

Follow Conventional Commits format:
* `feat:` A new feature or screen
* `fix:` A bug fix
* `test:` Adding or updating unit tests
* `refactor:` Code change that neither fixes a bug nor adds a feature
* `docs:` Documentation updates
* `chore:` Build configs, dependencies, or tool settings

---

## 🔄 Peer Review Rotation

Review responsibility is distributed circularly so no single member is overburdened:

| PR Author | Assigned Default Reviewer |
| :--- | :--- |
| **Venkatesh** | Jasleen |
| **Jasleen** | Krishna |
| **Krishna** | Shravani |
| **Shravani** | Bhavya |
| **Bhavya** | Kimaya |
| **Kimaya** | Venkatesh |

*Rule for Shared Files*: Any PR touching `AppDatabase.kt`, `Entities.kt`, `MainActivity.kt`, `build.gradle.kts`, or `CommonGameFramework.kt` requires **TWO approvals**.

---

## 🤖 AI Coding Agent Safety Contract

If you use Antigravity, Cursor, Claude Code, Gemini, Copilot, or any AI assistant:

1. **NO Direct Pushes**: AI is never allowed to run `git push origin main` or `git push origin develop`.
2. **NO Force Pushes**: `git push -f` is completely disabled on shared branches.
3. **NO Scope Creep**: If the AI attempts to rewrite Room, navigation, or another member's game, stop the agent and revert the unauthorized changes.
4. **Inspect Before Commit**: Always run `git diff` and verify every changed line yourself.

---

## ⚔️ Merge Conflict Resolution Protocol

When merge conflicts occur between your feature branch and `develop`:

1. Update your local `develop`:
   ```bash
   git checkout develop
   git pull origin develop
   ```
2. Rebase or merge `develop` into your feature branch:
   ```bash
   git checkout feature/<member>/<feature>
   git merge develop
   ```
3. **DO NOT** use `git checkout --ours` or `git checkout --theirs` blindly.
4. **DO NOT** tell the AI to "auto-resolve all conflicts" without inspection.
5. Manually review conflicting chunks, preserve both features cleanly, run `./gradlew testDebugUnitTest`, and commit the resolution.

---

## 🔐 Secrets & Privacy Policy

* **NEVER** commit API keys, signing keystores (`.jks`, `.keystore`), or credentials.
* **NEVER** write raw patient audio files (`.wav`, `.mp3`) to disk storage.
* Non-PII pseudonyms (e.g. `AS-DEMO-01`) must always be used for patient profiles.
