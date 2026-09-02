# SmritiSetu (SIH26003) — Git Workflow, Branching & Code Review Standard

## 🌿 1. Branch Hierarchy

SmritiSetu utilizes a protected Git branching model designed to isolate stable demonstration builds from ongoing feature developments.

```
[main]          <--- Production release builds (Used for Final Hackathon Presentation)
  ^
  | (Squash / Merge on Release Gates: Sept 5, Sept 11)
[develop]       <--- Active integration branch (All verified PRs merge here)
  ^
  | (Feature PRs with at least 1 peer approval)
[feat/*, fix/*] <--- Short-lived developer branches (Lifespan < 24 hours)
```

---

## 🏷️ 2. Branch Naming Standard

| Prefix | Description | Example |
| :--- | :--- | :--- |
| `feat/` | New user or clinical capability | `feat/101-speed-match-timer` |
| `fix/` | Defect correction | `fix/102-sqlite-page-lock` |
| `perf/` | Speed or memory optimization | `perf/103-onnx-int8-quant` |
| `docs/` | Documentation update | `docs/104-api-contract-patch` |
| `test/` | Adding automated test coverage | `test/105-scoring-math-tests` |

---

## 🔍 3. Code Review Protocol (Peer Verification)

Before any PR can be merged into `develop`:
1. **Automated CI Check**: The `ci-cd.yml` workflow must have finished successfully.
2. **Offline Assertion**: The PR author must state explicitly in the PR description: *"Verified 100% offline in Airplane Mode on Android device."*
3. **Reviewer Checklist**:
   * Are there any raw audio files written to disk? (Strict rejection if found).
   * Are touch targets at least $64\times 64\text{ dp}$?
   * Are SQL queries wrapped in transactions if performing multiple mutations?
   * Are new functions typed with strict TypeScript types (no `any`)?

---

## ⚡ 4. Conflict Resolution Procedure

```bash
# If your branch is behind develop:
git checkout develop
git pull origin develop
git checkout feat/your-branch-name

# Rebase against develop
git rebase develop

# If conflicts occur:
# 1. Open conflicted files in VS Code and resolve diff blocks
# 2. Add resolved files
git add <resolved-file>

# 3. Continue rebase
git rebase --continue

# 4. Push updated history
git push origin feat/your-branch-name --force-with-lease
```
