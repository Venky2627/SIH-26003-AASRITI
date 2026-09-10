# Contributor Directive: Krishna

* **Role**: Senior Game Engine & Telemetry Lead
* **Designated Working Branch**: `feature/krishna/frontend-games-framework`
* **Default Peer Reviewer**: Bhavya
* **Primary Scope**:
  - Reusable Game Framework (`feature/games/framework/CommonGameFramework.kt`)
  - Flagship Flower Match telemetry integration (`feature/games/flowermatch/`)
  - Games 1–3 (`familytrivia/`, `voicecuecard/`, `sequencing/`)
* **P0 Task (Vertical Slice V1)**:
  - Wire Flower Match to emit real `GameSession` telemetry (reactionTimeMs, hesitationCount, accuracy) to `CognitiveInsightOrchestrator`.
* **Safe Directories**:
  - `app/src/main/java/com/sih26003/aasriti/feature/games/framework/`
  - `app/src/main/java/com/sih26003/aasriti/feature/games/flowermatch/`
  - `app/src/main/java/com/sih26003/aasriti/feature/games/familytrivia/`
  - `app/src/main/java/com/sih26003/aasriti/feature/games/voicecuecard/`
  - `app/src/main/java/com/sih26003/aasriti/feature/games/sequencing/`
* **Rules**: Always verify with `./gradlew testDebugUnitTest` before committing.
