# Contributor Directive: Jasleen

* **Role**: Backend & Data Architect
* **Designated Working Branch**: `feature/jasleen/backend-data-sync`
* **Default Peer Reviewer**: Venkatesh
* **Primary Scope**:
  - Room SQLite Database (`AppDatabase.kt`), Entities, and 7 DAOs (`data/local/`)
  - Entity $\leftrightarrow$ Domain Mappers (`data/mapper/`)
  - Domain Repository implementations (`data/repository/`)
* **P0 Task (Vertical Slice V1)**:
  - Implement Room entities, DAOs, and Mappers for `Patient`, `GameSession`, and `CareLog`.
  - Ensure Room entities NEVER leak into UI composables; always map to `domain/model/`.
* **Safe Directories**:
  - `app/src/main/java/com/sih26003/smritisetu/data/local/`
  - `app/src/main/java/com/sih26003/smritisetu/data/mapper/`
  - `app/src/main/java/com/sih26003/smritisetu/data/repository/`
* **Rules**: Always verify with `./gradlew testDebugUnitTest` before committing.
