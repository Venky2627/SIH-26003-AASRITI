# AASRITI (SIH-26003) — API & Offline Synchronization Contract

> **Core Tenet**: Local Room writes are synchronous and immediate. Cloud replication to Firebase is asynchronous, opportunistic, and strictly non-blocking.

---

## 🔄 1. The Offline-to-Online Sync Lifecycle

```
[User Action in App]
       │
       ▼
[Room Database Write (Immediate SQLite Transaction)]
       │
       ▼
[Insert into sync_queue (Status: PENDING)]
       │
       ▼
[ConnectivityObserver Checks Network State]
       ├── If OFFLINE: Retain in queue, zero network calls, zero UI blockage.
       └── If ONLINE: Trigger SyncQueueProcessor (WorkManager CoroutineWorker).
               │
               ▼
       [Batch Send to Firebase Firestore / Storage]
               │
               ├── On SUCCESS: Mark status = SYNCED (Never delete local SQLite data).
               └── On FAILURE: Increment retryCount, backoff exponentially.
```

---

## 📦 2. SyncQueue Payload Specification

Every mutation enqueued into `sync_queue` stores a serialized JSON payload:

### A. Patient Registration Payload
```json
{
  "tableName": "patients",
  "recordId": "550e8400-e29b-41d4-a716-446655440000",
  "operation": "INSERT",
  "payloadJson": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "pseudonymCode": "AS-KAM-0042",
    "birthYear": 1958,
    "gender": "F",
    "primaryLanguage": "as",
    "cognitiveStage": "MCI",
    "culturalRegion": "ASSAM",
    "createdAt": 1725350400000
  }
}
```

### B. Game Session Telemetry Payload
```json
{
  "tableName": "game_sessions",
  "recordId": "71a23c44-b49b-4652-9d32-aa5118991201",
  "operation": "INSERT",
  "payloadJson": {
    "id": "71a23c44-b49b-4652-9d32-aa5118991201",
    "patientId": "550e8400-e29b-41d4-a716-446655440000",
    "gameId": "FAMILY_TRIVIA",
    "difficultyLevel": 2,
    "durationMs": 142000,
    "accuracy": 0.85,
    "errors": 1,
    "reactionTimeMs": 2400,
    "hesitationCount": 2,
    "adaptationDecision": 3,
    "completed": true,
    "timestamp": 1725350900000
  }
}
```

### C. Care Log (Quick Log) Payload
```json
{
  "tableName": "care_logs",
  "recordId": "92f87a11-0021-4ba2-b214-411299440112",
  "operation": "INSERT",
  "payloadJson": {
    "id": "92f87a11-0021-4ba2-b214-411299440112",
    "patientId": "550e8400-e29b-41d4-a716-446655440000",
    "loggerRole": "CAREGIVER",
    "logType": "FALL",
    "severity": "URGENT",
    "notes": "Stumbled near bedside at 7 AM; no head injury",
    "timestamp": 1725351200000
  }
}
```

---

## 🗂️ 3. Cloud Firestore Collection Hierarchy

```text
firestore/
├── users/
│   └── {userId}/                         # Caregiver/ASHA/Doctor profiles
├── patients/
│   └── {patientId}/
│       ├── relationships/{relId}          # Family photos metadata
│       ├── game_sessions/{sessionId}      # Telemetry for doctor longitudinal charts
│       ├── care_logs/{logId}              # Incident logs for priority triage
│       ├── reminders/{reminderId}         # Synced reminders
│       └── doctor_access/{accessId}       # Approved 6-digit access codes
```

---

## ⚔️ 4. Conflict Resolution Strategy

1. **Local-Precedence Principle**: Because the device is the active point of patient care, local SQLite state always takes precedence over conflicting cloud edits.
2. **Append-Only Logs**: `game_sessions` and `care_logs` are immutable and append-only. There are zero update conflicts.
3. **Last-Write-Wins on Reminders**: If a caregiver updates a reminder time on multiple devices, the higher `timestamp` record wins.
