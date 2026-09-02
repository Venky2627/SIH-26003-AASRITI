# Rule 01: Team Ownership Map & Review Rotation

## 👥 Feature Boundaries

| Contributor | Assigned Scope | Branch Pattern | Working Directories | Default Reviewer |
| :--- | :--- | :--- | :--- | :--- |
| **Venkatesh** | Game Framework, Metrics, Adaptive ML | `feature/venkatesh/*` | `feature/games/framework/`, `ml/`, `scripts/` | **Jasleen** |
| **Jasleen** | Family Trivia & Personalization | `feature/jasleen/*` | `feature/games/familytrivia/`, `data/local/` (relationships) | **Krishna** |
| **Krishna** | Voice Cue Card & Shared Audio | `feature/krishna/*` | `feature/games/voicecuecard/`, `voice/` | **Shravani** |
| **Shravani** | Reminders & Caregiver Logs | `feature/shravani/*` | `feature/reminders/`, `feature/caregiver/` | **Bhavya** |
| **Bhavya** | Accessibility & Patient UX | `feature/bhavya/*` | `feature/patient/`, `feature/auth/`, `res/` | **Kimaya** |
| **Kimaya** | Sequencing & Categorisation Games | `feature/kimaya/*` | `feature/games/sequencing/`, `feature/games/categorisation/` | **Venkatesh** |

## 🔄 Review Rotation
Workload rotates circularly:
$$\text{Venkatesh} \rightarrow \text{Jasleen} \rightarrow \text{Krishna} \rightarrow \text{Shravani} \rightarrow \text{Bhavya} \rightarrow \text{Kimaya} \rightarrow \text{Venkatesh}$$

## ⚠️ High-Risk Shared Files (Require 2 Approvals)
- `AppDatabase.kt` & `Entities.kt` (Room schema)
- `MainActivity.kt` (Navigation routing)
- `build.gradle.kts` & `gradle/libs.versions.toml` (Dependencies)
- `CommonGameFramework.kt` (Base game lifecycle)
