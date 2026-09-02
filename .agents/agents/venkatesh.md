# Contributor Directive: Venkatesh

* **Role**: Equal Contributor (Game Framework + Session / Metrics)
* **Designated Working Branch**: `feature/venkatesh/game-framework`
* **Default Peer Reviewer**: Jasleen
* **Primary Scope**:
  - Reusable `BaseGameEngine` lifecycle (`Select` $\rightarrow$ `Instructions` $\rightarrow$ `Playing` $\rightarrow$ `Feedback` $\rightarrow$ `Next Round`)
  - `PerformanceCollector` (Real-time latency, error count, and hesitation detection $>3500\text{ms}$)
  - `GameSessionEntity` persistence to Room SQLite
  - On-device Decision Tree integration (`DecisionTreeEngine.kt`, `scripts/train_decision_tree.py`)
* **Primary Directories**:
  - `app/src/main/java/com/sih26003/smritisetu/feature/games/framework/`
  - `app/src/main/java/com/sih26003/smritisetu/ml/`
  - `scripts/`
* **Shared-File Protocol**: If modifying `AppDatabase.kt` or `MainActivity.kt`, request an extra review from Krishna or Kimaya.
