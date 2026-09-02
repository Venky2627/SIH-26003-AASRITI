# Antigravity Project Intelligence — SmritiSetu (SIH26003)

> This document serves as the master operational directive for Google Antigravity, Cursor, Claude Code, and all AI coding assistants operating on the SIH26003 repository.

---

## 🤖 1. Automated Branch Detection on First Open

Upon opening the repository, the AI assistant must immediately execute:
```bash
git branch --show-current
git status
```

### Branch-to-Contributor Mapping:
| Current Branch Pattern | Active Contributor | Assigned Feature Scope | Default Reviewer |
| :--- | :--- | :--- | :--- |
| `feature/venkatesh/*` | **Venkatesh** | Game Framework + Session/Metrics | Jasleen |
| `feature/jasleen/*` | **Jasleen** | Family Trivia + Personalization | Krishna |
| `feature/krishna/*` | **Krishna** | Voice Cue Card + Voice UX | Shravani |
| `feature/shravani/*` | **Shravani** | Reminders System (Medicine/Alarms) | Bhavya |
| `feature/bhavya/*` | **Bhavya** | Accessibility + Patient UX Polish | Kimaya |
| `feature/kimaya/*` | **Kimaya** | Sequencing + Categorisation Games | Venkatesh |

### Startup Greeting Protocol:
If a member branch is detected, state:
> *"You are working as **`<Member>`**. Your feature scope is **`<Feature>`**. Your working directories are `<Paths>`. Your default peer reviewer is **`<Reviewer>`**. If you need changes to shared files, remember that two approvals are required."*

* If current branch is `develop`: Instruct the developer to create a feature branch (`git checkout -b feature/<member>/<feature>`).
* If current branch is `main`: Warn the developer that `main` is protected and cannot be modified directly.
* If branch is unmapped: Ask the developer which of the six team members they are.

---

## 🛑 2. The Golden Safety Directives

1. **NO Direct Pushes to `main` or `develop`**: All code must merge through Pull Requests with peer review and green CI.
2. **NO Force Pushes**: `git push --force` is strictly prohibited on shared branches.
3. **NO Scope Creep**: Stay within your assigned module. Never touch another contributor's game or rewrite shared architecture without explicit instruction.
4. **Inspect Diffs Before Committing**: Run `git diff` and examine every changed line. Never commit accidental or auto-formatted unrelated files.
5. **No Hallucinated Implementations**: Never claim a feature exists if it has not been implemented in source code and verified with tests.

---

## 📚 3. Rule Index

Detailed instructions are organized in `.agents/`:
* [`.agents/rules/00-core-rules.md`](file:///.agents/rules/00-core-rules.md) — Core principles & 6 equal contributors
* [`.agents/rules/01-team-map.md`](file:///.agents/rules/01-team-map.md) — Work allocation & review rotation
* [`.agents/rules/02-architecture.md`](file:///.agents/rules/02-architecture.md) — Locked Kotlin/Compose/Room architecture
* [`.agents/rules/03-git-safety.md`](file:///.agents/rules/03-git-safety.md) — Git safety & conflict resolution
* [`.agents/workflows/`](file:///.agents/workflows/) — Step-by-step commands for `/start-work`, `/sync-with-develop`, `/pre-commit`, and `/prepare-pr`
