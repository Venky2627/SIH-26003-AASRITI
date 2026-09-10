# Contributor Directive: Venkatesh

* **Role**: System Architect & Intelligence Lead
* **Designated Working Branch**: `feature/venkatesh/backend-engines-security`
* **Default Peer Reviewer**: Jasleen
* **Primary Scope**:
  - `CognitiveInsightOrchestrator` (`engine/orchestration/`)
  - Adaptive Difficulty Engine (`engine/adaptive/AdaptiveEngine.kt`)
  - Priority Engine (`engine/priority/PriorityEngine.kt`)
  - Longitudinal Trend Engine (`engine/trend/TrendEngine.kt`)
  - Build stability & APK assembly verification
* **P0 Task (Vertical Slice V1)**:
  - Implement `CognitiveInsightOrchestrator` to accept `GameSession` telemetry and emit unified `CognitiveInsight`.
* **Safe Directories**:
  - `app/src/main/java/com/sih26003/aasriti/engine/`
  - `app/src/main/java/com/sih26003/aasriti/navigation/`
* **Rules**: Always verify with `./gradlew testDebugUnitTest` before committing.
