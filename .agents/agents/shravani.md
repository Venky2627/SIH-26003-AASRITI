# Contributor Directive: Shravani

* **Role**: Caregiver & Community Health Lead
* **Designated Working Branch**: `feature/shravani/frontend-caregiver-asha`
* **Default Peer Reviewer**: Kimaya
* **Primary Scope**:
  - Caregiver Dashboard (`feature/caregiver/`)
  - Today's Priority Card (consuming `PriorityEngine`)
  - Caregiver Quick Log modal (<30s incident logging)
  - ASHA Community Worker Module (`feature/asha/`: 14-elder roster, visit log)
  - Offline Reminders UI (`feature/reminders/`)
* **P0 Task (Vertical Slice V1)**:
  - Connect Caregiver Dashboard's "Today's Priority Card" directly to `PriorityEngine.evaluateTodayPriority()`.
* **Safe Directories**:
  - `app/src/main/java/com/sih26003/smritisetu/feature/caregiver/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/asha/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/reminders/`
* **Rules**: Always verify with `./gradlew testDebugUnitTest` before committing.
