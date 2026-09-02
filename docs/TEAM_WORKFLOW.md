# SIH26003 — Team Workflow & Ownership Board

> **Core Philosophy**: All 6 members are **EQUAL CONTRIBUTORS**. Everyone writes code, everyone tests, everyone reviews PRs, and everyone is responsible for product quality. No member is superior, junior, or backup.

---

## 👥 1. Final Team Feature Allocation

| Member | Feature Area | Designated Branch | Default Reviewer | Current Status |
| :--- | :--- | :--- | :--- | :---: |
| **Venkatesh** | Game Framework + Session/Metrics | `feature/venkatesh/game-framework` | Jasleen | `IN PROGRESS` |
| **Jasleen** | Family Trivia + Personalization | `feature/jasleen/family-trivia` | Krishna | `IN PROGRESS` |
| **Krishna** | Voice Cue Card + Voice UX | `feature/krishna/voice-cue-card` | Shravani | `IN PROGRESS` |
| **Shravani** | Reminders System | `feature/shravani/reminders` | Bhavya | `IN PROGRESS` |
| **Bhavya** | Accessibility + Patient UX | `feature/bhavya/accessibility` | Kimaya | `IN PROGRESS` |
| **Kimaya** | Sequencing + Categorisation | `feature/kimaya/sequencing-categorisation` | Venkatesh | `IN PROGRESS` |

*Status Values*: `TODO` | `IN PROGRESS` | `REVIEW` | `MERGED` | `BLOCKED`

---

## 🔄 2. Peer Review Rotation

Review responsibility is a **quality gate**, not a hierarchy. Workload is distributed evenly using a circular rotation:

```
[Venkatesh PR] ──────> Reviewed by Jasleen
[Jasleen PR]   ──────> Reviewed by Krishna
[Krishna PR]   ──────> Reviewed by Shravani
[Shravani PR]  ──────> Reviewed by Bhavya
[Bhavya PR]    ──────> Reviewed by Kimaya
[Kimaya PR]    ──────> Reviewed by Venkatesh
```

> [!IMPORTANT]
> **Shared / Core Files Rule**: PRs modifying shared files (Room schema, navigation, Gradle files, application container, common base game framework) require **TWO approvals** before merging.

---

## 🛑 3. Protected Branches & Integration Diagram

```
[main] (Production / Release) ◀───────────── Release PR + Verification
  ▲
  │
[develop] (Integration Trunk) ◀───────────── Feature PR + Peer Review + CI
  ▲
  ├─── [feature/venkatesh/game-framework]
  ├─── [feature/jasleen/family-trivia]
  ├─── [feature/krishna/voice-cue-card]
  ├─── [feature/shravani/reminders]
  ├─── [feature/bhavya/accessibility]
  └─── [feature/kimaya/sequencing-categorisation]
```

* **`main`**: Production / release branch. Direct pushes are strictly forbidden.
* **`develop`**: Integration branch. Direct pushes are strictly forbidden.
* **`feature/<member>/<feature>`**: Developer workspace where active vibecoding happens.

---

## ⚡ 4. Daily Developer Workflow & Git Commands

```bash
# 1. Start of Day: Sync with latest develop
git checkout develop
git pull origin develop
git checkout feature/<your-name>/<feature>
git merge develop

# 2. Before Committing: Review changes
git status
git diff --stat
git diff

# 3. Test locally
./gradlew testDebugUnitTest

# 4. Commit and Push
git add <modified-files>
git commit -m "feat(<scope>): descriptive message"
git push origin feature/<your-name>/<feature>
```

---

## ⚔️ 5. Conflict Resolution Protocol

1. **Never** choose `checkout --ours` or `checkout --theirs` blindly.
2. **Never** tell an AI assistant to "auto-resolve all conflicts" without inspection.
3. Open conflicting files, review both implementations, merge them logically.
4. Run `./gradlew testDebugUnitTest` to verify integrity.
5. Commit the resolution and notify your reviewer.

---

## 🛡️ 6. AI Coding Agent Safety Contract

When using AI coding assistants (Antigravity, Cursor, Claude Code, Gemini, Copilot):

1. **Scope Boundary**: AI must stay within the assigned feature module.
2. **Never Direct Push**: The AI must **never** push directly to `main` or `develop`.
3. **No Unrelated Modifying**: The AI must not touch unrelated screens or rewrite shared databases without instruction.
4. **Pre-Commit Diff Inspection**: Always run `git diff` and review every line before committing.
5. **No Force Pushes**: `git push --force` on shared branches is forbidden.
