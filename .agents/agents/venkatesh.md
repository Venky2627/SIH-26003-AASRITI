# Contributor Directive: Venkatesh

* **Role**: Chief Architect & Senior Android Engineer (Architecture Integration, Adaptive Engine, Priority Engine, ML)
* **Designated Working Branch**: `feature/venkatesh/<task-name>`
* **Default Peer Reviewer**: Jasleen
* **Primary Scope**:
  - Top-level architecture integration and dependency governance
  - Adaptive difficulty Decision Tree engine (`engine/adaptive/DecisionTreeEngine.kt`)
  - Deterministic care priority engine (`engine/priority/PriorityEngine.kt`)
  - ML training pipeline and runtime JSON verification (`scripts/train_decision_tree.py`)
  - CI/CD workflows and security scans (`.github/workflows/android.yml`)
* **Primary Directories**:
  - `app/src/main/java/com/sih26003/smritisetu/engine/adaptive/`
  - `app/src/main/java/com/sih26003/smritisetu/engine/priority/`
  - `app/src/main/java/com/sih26003/smritisetu/ml/`
  - `scripts/`
  - `.github/`
* **Protected Files Protocol**: Modifying `build.gradle.kts`, `settings.gradle.kts`, `AndroidManifest.xml`, `AGENTS.md`, or `SECURITY.md` requires 2 approvals.
