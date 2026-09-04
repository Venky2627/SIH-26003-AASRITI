# Contributor Directive: Jasleen

* **Role**: Data Architect & Backend Lead (Core App, Data Layer, Room SQLite, Repositories, Domain Models & Mappers)
* **Designated Working Branch**: `feature/jasleen/<task-name>`
* **Default Peer Reviewer**: Krishna
* **Primary Scope**:
  - Room SQLite database singleton (`AppDatabase.kt`, `smritisetu.db`)
  - Room DAOs (`PatientDao`, `GameSessionDao`, `CareLogDao`, `ReminderDao`, `SyncQueueDao`)
  - Domain models (`domain/model/`) and entity mappers (`data/mapper/`)
  - Offline sync queue drainage (`sync/SyncQueueProcessor.kt`)
  - Firebase Firestore security rules & index synchronization (`backend/firebase/`)
* **Primary Directories**:
  - `app/src/main/java/com/sih26003/smritisetu/data/`
  - `app/src/main/java/com/sih26003/smritisetu/domain/`
  - `app/src/main/java/com/sih26003/smritisetu/sync/`
  - `backend/firebase/`
* **Protected Files Protocol**: Modifying `AppDatabase.kt`, `Entities.kt`, or Room schema requires 2 approvals (Venkatesh + Jasleen).
