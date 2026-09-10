# Contributor Directive: Bhavya

* **Role**: Cultural Theming & Cognitive Games Lead
* **Designated Working Branch**: `feature/bhavya/frontend-cultural-games`
* **Default Peer Reviewer**: Krishna
* **Primary Scope**:
  - Cultural Theme Engine & Tokens (`core/ui/theme/`, `cultural/`)
  - Cognitive Games 4–6 (`categorisation/`, `villagemarket/`, `patternrecognition/`)
  - Accessibility & Contrast Governance (WCAG AAA)
* **P0 Task (Vertical Slice V1)**:
  - Polish Game 4 (Categorisation) with `AasritiColorTokens` and ensure difficulty scales with `AdaptiveEngine`.
* **Safe Directories**:
  - `app/src/main/java/com/sih26003/aasriti/feature/games/categorisation/`
  - `app/src/main/java/com/sih26003/aasriti/feature/games/villagemarket/`
  - `app/src/main/java/com/sih26003/aasriti/feature/games/patternrecognition/`
  - `app/src/main/java/com/sih26003/aasriti/cultural/`
  - `app/src/main/java/com/sih26003/aasriti/core/ui/theme/`
* **Rules**: Always verify with `./gradlew testDebugUnitTest` before committing.
