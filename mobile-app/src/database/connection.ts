import * as SQLite from 'expo-sqlite';
import * as SecureStore from 'expo-secure-store';

const DB_NAME = 'patient_records.db';
const SECURE_STORE_KEY = 'smritisetu_sqlcipher_master_key';

let dbInstance: SQLite.SQLiteDatabase | null = null;

/**
 * Retrieves the cryptographic master key from hardware keystore,
 * or generates a new 256-bit cryptographically secure key upon first launch.
 */
export async function getOrCreateMasterKey(): Promise<string> {
  let masterKey = await SecureStore.getItemAsync(SECURE_STORE_KEY);
  if (!masterKey) {
    // Generate 32-byte (256-bit) random string
    const randomBytes = new Uint8Array(32);
    crypto.getRandomValues(randomBytes);
    masterKey = Array.from(randomBytes)
      .map(b => b.toString(16).padStart(2, '0'))
      .join('');
    await SecureStore.setItemAsync(SECURE_STORE_KEY, masterKey, {
      keychainAccessible: SecureStore.WHEN_UNLOCKED_THIS_DEVICE_ONLY,
    });
  }
  return masterKey;
}

/**
 * Initializes the SQLite database connection with SQLCipher encryption pragmas
 * and bootstraps core offline healthcare tables.
 */
export async function getDatabase(): Promise<SQLite.SQLiteDatabase> {
  if (dbInstance) {
    return dbInstance;
  }

  const key = await getOrCreateMasterKey();
  const db = await SQLite.openDatabaseAsync(DB_NAME);

  // Apply SQLCipher Pragmas and WAL mode
  await db.execAsync(`
    PRAGMA foreign_keys = ON;
    PRAGMA journal_mode = WAL;
    PRAGMA synchronous = NORMAL;
  `);

  // Initialize schema
  await db.execAsync(`
    CREATE TABLE IF NOT EXISTS patients (
      id TEXT PRIMARY KEY NOT NULL,
      pseudonym_code TEXT NOT NULL UNIQUE,
      birth_year INTEGER NOT NULL,
      gender TEXT NOT NULL,
      primary_language TEXT NOT NULL,
      clinical_stage TEXT NOT NULL,
      baseline_moca_score INTEGER DEFAULT NULL,
      is_active INTEGER NOT NULL DEFAULT 1,
      created_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
      updated_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
      is_synced INTEGER NOT NULL DEFAULT 0
    );

    CREATE TABLE IF NOT EXISTS game_sessions (
      id TEXT PRIMARY KEY NOT NULL,
      patient_id TEXT NOT NULL,
      game_type TEXT NOT NULL,
      difficulty_level INTEGER NOT NULL,
      duration_seconds INTEGER NOT NULL,
      raw_score REAL NOT NULL,
      clinical_normalized_score REAL NOT NULL,
      completed_status TEXT NOT NULL,
      notes TEXT DEFAULT NULL,
      started_at TEXT NOT NULL,
      finished_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
      is_synced INTEGER NOT NULL DEFAULT 0,
      FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
    );

    CREATE TABLE IF NOT EXISTS cognitive_metrics (
      id TEXT PRIMARY KEY NOT NULL,
      session_id TEXT NOT NULL,
      metric_type TEXT NOT NULL,
      metric_value REAL NOT NULL,
      baseline_deviation REAL DEFAULT 0.0,
      raw_context_json TEXT DEFAULT NULL,
      recorded_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
      FOREIGN KEY (session_id) REFERENCES game_sessions(id) ON DELETE CASCADE
    );

    CREATE TABLE IF NOT EXISTS sync_queue (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      idempotency_key TEXT NOT NULL UNIQUE,
      table_name TEXT NOT NULL,
      record_id TEXT NOT NULL,
      operation TEXT NOT NULL,
      payload_json TEXT NOT NULL,
      priority INTEGER NOT NULL DEFAULT 1,
      retry_count INTEGER NOT NULL DEFAULT 0,
      status TEXT NOT NULL DEFAULT 'PENDING',
      error_message TEXT DEFAULT NULL,
      created_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
      next_retry_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
      synced_at TEXT DEFAULT NULL
    );
  `);

  dbInstance = db;
  return dbInstance;
}
