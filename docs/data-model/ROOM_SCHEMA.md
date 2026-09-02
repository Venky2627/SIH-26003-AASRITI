# SmritiSetu (SIH26003) — Room Local Database Schema

Room (`smritisetu.db`) is the absolute application source of truth.

```sql
-- Users (Local PIN storage for Caregivers and Doctors)
CREATE TABLE users (
    id TEXT PRIMARY KEY NOT NULL,
    role TEXT NOT NULL CHECK(role IN ('CAREGIVER', 'DOCTOR')),
    pinHash TEXT NOT NULL, -- SHA-256 hash
    name TEXT NOT NULL,
    phoneHash TEXT NOT NULL DEFAULT '',
    createdAt INTEGER NOT NULL
);

-- Patients (Source of Truth)
CREATE TABLE patients (
    id TEXT PRIMARY KEY NOT NULL,
    pseudonymCode TEXT NOT NULL UNIQUE,
    birthYear INTEGER NOT NULL,
    gender TEXT NOT NULL,
    primaryLanguage TEXT NOT NULL DEFAULT 'as',
    cognitiveStage TEXT NOT NULL DEFAULT 'MCI',
    photoPath TEXT,
    linkCode TEXT, -- 6-digit code
    isSynced INTEGER NOT NULL DEFAULT 0,
    createdAt INTEGER NOT NULL
);

-- Personal Relationships (Family Trivia Game)
CREATE TABLE relationships (
    id TEXT PRIMARY KEY NOT NULL,
    patientId TEXT NOT NULL,
    name TEXT NOT NULL,
    relationshipType TEXT NOT NULL,
    photoPath TEXT,
    voiceClipPath TEXT,
    isSynced INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY(patientId) REFERENCES patients(id) ON DELETE CASCADE
);

-- Game Performance Sessions
CREATE TABLE game_sessions (
    id TEXT PRIMARY KEY NOT NULL,
    patientId TEXT NOT NULL,
    gameId TEXT NOT NULL,
    difficultyLevel INTEGER NOT NULL,
    durationMs INTEGER NOT NULL,
    accuracy REAL NOT NULL,
    errors INTEGER NOT NULL,
    reactionTimeMs INTEGER NOT NULL,
    hesitationCount INTEGER NOT NULL,
    adaptationDecision INTEGER NOT NULL,
    completed INTEGER NOT NULL,
    timestamp INTEGER NOT NULL,
    isSynced INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY(patientId) REFERENCES patients(id) ON DELETE CASCADE
);

-- Offline Daily Reminders
CREATE TABLE reminders (
    id TEXT PRIMARY KEY NOT NULL,
    patientId TEXT NOT NULL,
    title TEXT NOT NULL,
    reminderType TEXT NOT NULL CHECK(reminderType IN ('MEDICINE', 'HYDRATION', 'EXERCISE', 'APPOINTMENT')),
    hour INTEGER NOT NULL,
    minute INTEGER NOT NULL,
    voicePromptKey TEXT,
    isEnabled INTEGER NOT NULL DEFAULT 1,
    isSynced INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY(patientId) REFERENCES patients(id) ON DELETE CASCADE
);

-- Doctor Access Approvals
CREATE TABLE doctor_access (
    id TEXT PRIMARY KEY NOT NULL,
    patientId TEXT NOT NULL,
    doctorAccessCode TEXT NOT NULL,
    doctorName TEXT NOT NULL,
    approvedAt INTEGER NOT NULL,
    isRevoked INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY(patientId) REFERENCES patients(id) ON DELETE CASCADE
);

-- Offline Replication Queue (ROOM IS KING, FIREBASE IS MESSENGER)
CREATE TABLE sync_queue (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    tableName TEXT NOT NULL,
    recordId TEXT NOT NULL,
    operation TEXT NOT NULL CHECK(operation IN ('INSERT', 'UPDATE', 'DELETE')),
    payloadJson TEXT NOT NULL,
    timestamp INTEGER NOT NULL,
    retryCount INTEGER NOT NULL DEFAULT 0,
    status TEXT NOT NULL DEFAULT 'PENDING'
);
```
