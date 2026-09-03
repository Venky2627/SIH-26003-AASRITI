# SmritiSetu (SIH-26003) — Data Model & Schema Specification

> **CORE PRINCIPLE**: Strict separation between SQLite Database Entities (`data/local/entity/`) and Application Domain Models (`domain/model/`). UI composables and ViewModels must **never** consume Room entities directly.

---

## 🗄️ 1. Room SQLite Schema (`data/local/entity/`)

### A. `users` Table (`UserEntity`)
Stores local PIN hashes for Caregivers, Doctors, and ASHA workers. Patients do not exist in this table.
```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,                 // UUID
    val role: String,                           // "CAREGIVER", "ASHA", "DOCTOR"
    val pinHash: String,                       // SHA-256 hash of 6-digit local PIN
    val name: String,
    val phoneHash: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
```

### B. `patients` Table (`PatientEntity`)
Patient profiles stored locally in Room. Zero internet required to create or select.
```kotlin
@Entity(
    tableName = "patients",
    indices = [Index(value = ["pseudonymCode"], unique = true)]
)
data class PatientEntity(
    @PrimaryKey val id: String,                 // UUID
    val pseudonymCode: String,                 // e.g. "AS-KAM-0042"
    val birthYear: Int,
    val gender: String,                        // "M", "F", "O"
    val primaryLanguage: String = "as",        // "as", "mn", "br", "hi", "en"
    val cognitiveStage: String = "MCI",        // "MCI", "Early Dementia", "Wellness"
    val photoPath: String? = null,             // App-private file storage path
    val culturalRegion: String = "ASSAM",      // "ASSAM", "MANIPUR", "MEGHALAYA"
    val linkCode: String? = null,              // 6-digit link code
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
```

### C. `relationships` Table (`RelationshipEntity`)
Family members for Game 1 (Family Trivia) and Memory Album.
```kotlin
@Entity(
    tableName = "relationships",
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("patientId")]
)
data class RelationshipEntity(
    @PrimaryKey val id: String,
    val patientId: String,
    val name: String,
    val relationshipType: String,              // "SPOUSE", "SON", "DAUGHTER", "GRANDCHILD"
    val photoPath: String? = null,
    val voiceClipPath: String? = null,
    val isSynced: Boolean = false
)
```

### D. `game_sessions` Table (`GameSessionEntity`)
Telemetry and physical performance metrics for all 6 cognitive games.
```kotlin
@Entity(
    tableName = "game_sessions",
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("patientId"), Index("gameId")]
)
data class GameSessionEntity(
    @PrimaryKey val id: String,
    val patientId: String,
    val gameId: String,                        // "FAMILY_TRIVIA", "VOICE_CUE_CARD", etc.
    val difficultyLevel: Int,                  // 1 to 5
    val durationMs: Long,
    val accuracy: Float,                       // 0.0f to 1.0f
    val errors: Int,
    val reactionTimeMs: Long,
    val hesitationCount: Int,                  // Pauses > 3500ms
    val adaptationDecision: Int,               // Recommended next difficulty (1 to 5)
    val completed: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
```

### E. `care_logs` Table (`CareLogEntity`)
Quick Log observations recorded by Caregivers and ASHA workers.
```kotlin
@Entity(
    tableName = "care_logs",
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("patientId"), Index("timestamp")]
)
data class CareLogEntity(
    @PrimaryKey val id: String,
    val patientId: String,
    val loggerRole: String,                    // "CAREGIVER", "ASHA"
    val logType: String,                       // "MEDICATION", "APPETITE", "SLEEP", "FALL", "WANDERING", "AGITATION"
    val severity: String,                      // "NORMAL", "WATCH", "URGENT"
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
```

### F. `reminders` Table (`ReminderEntity`)
Offline reminders scheduled with Android `AlarmManager`.
```kotlin
@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("patientId")]
)
data class ReminderEntity(
    @PrimaryKey val id: String,
    val patientId: String,
    val title: String,
    val reminderType: String,                  // "MEDICINE", "HYDRATION", "EXERCISE", "APPOINTMENT"
    val hour: Int,                             // 0 to 23
    val minute: Int,                           // 0 to 59
    val voicePromptKey: String? = null,
    val isEnabled: Boolean = true,
    val isSynced: Boolean = false
)
```

### G. `sync_queue` Table (`SyncQueueEntity`)
Durable offline queue for opportunistic Firebase synchronization.
```kotlin
@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tableName: String,
    val recordId: String,
    val operation: String,                     // "INSERT", "UPDATE", "DELETE"
    val payloadJson: String,
    val timestamp: Long = System.currentTimeMillis(),
    val retryCount: Int = 0,
    val status: String = "PENDING"             // "PENDING", "SYNCED", "FAILED"
)
```

---

## 🏛️ 2. Domain Models (`domain/model/`)

Clean, pure Kotlin data classes consumed by ViewModels and Composable screens:

```kotlin
data class Patient(
    val id: String,
    val pseudonymCode: String,
    val birthYear: Int,
    val gender: String,
    val primaryLanguage: String,
    val cognitiveStage: String,
    val photoPath: String?,
    val culturalRegion: String
)

data class GameSession(
    val id: String,
    val patientId: String,
    val gameId: String,
    val difficultyLevel: Int,
    val durationMs: Long,
    val accuracy: Float,
    val errors: Int,
    val reactionTimeMs: Long,
    val hesitationCount: Int,
    val adaptationDecision: Int,
    val completed: Boolean,
    val timestamp: Long
)

data class CareLog(
    val id: String,
    val patientId: String,
    val loggerRole: String,
    val logType: String,
    val severity: String,
    val notes: String,
    val timestamp: Long
)

data class Reminder(
    val id: String,
    val patientId: String,
    val title: String,
    val reminderType: String,
    val hour: Int,
    val minute: Int,
    val isEnabled: Boolean
)
```

---

## 🔄 3. Explicit Data Mappers (`data/mapper/`)

```kotlin
fun PatientEntity.toDomain() = Patient(
    id = id,
    pseudonymCode = pseudonymCode,
    birthYear = birthYear,
    gender = gender,
    primaryLanguage = primaryLanguage,
    cognitiveStage = cognitiveStage,
    photoPath = photoPath,
    culturalRegion = culturalRegion
)

fun Patient.toEntity(isSynced: Boolean = false) = PatientEntity(
    id = id,
    pseudonymCode = pseudonymCode,
    birthYear = birthYear,
    gender = gender,
    primaryLanguage = primaryLanguage,
    cognitiveStage = cognitiveStage,
    photoPath = photoPath,
    culturalRegion = culturalRegion,
    isSynced = isSynced
)
```
