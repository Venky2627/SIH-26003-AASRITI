# AASRITI SYSTEM SECURITY & DATA PRIVACY CONSTITUTION
## `/SECURITY.md` — Full-Product Security Governance

> **SECURITY AXIOM**: **"Defense in Depth: UI obscurity is NOT security. Authorization and data isolation must be enforced at the persistence, repository, and service boundaries."**  
> **LEGAL COMPLIANCE**: Digital Personal Data Protection Act (DPDPA 2023, India) • MDoNER Patient Confidentiality Mandate

---

## 🔒 1. THREAT MODEL & SECURITY PHILOSOPHY

AASRITI manages sensitive health and cognitive telemetry for elderly dementia patients, family care notes, clinical records, and autobiographical family photographs.

### Core Security Tenets:
1. **Local Isolation First**: SQLite Room database (`smritisetu.db`) is stored strictly in app-private storage (`context.getDatabasePath()`).
2. **Zero Plaintext Secrets**: Passwords, PINs, access tokens, and encryption salts must never exist in plaintext in code, logs, or databases.
3. **No Patient Authentication Barrier**: Patient UX uses direct photo selection (Zero PIN) to preserve cognitive dignity, but the Patient role has strictly **Read-Only / Self-Session-Write** privileges. Administrative, medication, and clinical settings are completely barricaded behind authenticated roles.
4. **Zero Diagnostic Claims**: To prevent clinical liability and patient distress, no automated diagnostic scores are generated or stored.

---

## 👥 2. ROLE-BASED ACCESS CONTROL (RBAC) MATRIX

Authorization is enforced at the Repository and ViewModel layers. The frontend hiding a button is never considered sufficient protection.

| System Capability / Data Asset | PATIENT | CAREGIVER | ASHA WORKER | DOCTOR / CLINICIAN |
| :--- | :---: | :---: | :---: | :---: |
| **Cognitive Games (Play & Record)** | ✅ Full | ❌ Play only | ❌ Supervise | ❌ Demo only |
| **Memory Garden (View & Listen)** | ✅ Full | ✅ Full | ❌ View Only | ❌ View Only |
| **Memory Garden (Upload / Edit)** | ❌ Blocked | ✅ Full | ❌ Blocked | ❌ Blocked |
| **Daily Routine (Mark Self Done)** | ✅ Tap Done | ✅ Manage | ✅ Log on Visit | ❌ View Only |
| **Medication Schedule (Edit/Add)** | ❌ Blocked | ✅ Full | ❌ Blocked | ⚠️ Recommend Only |
| **Quick Care Log (<30s Triage)** | ❌ Blocked | ✅ Full | ✅ Full (Field) | ❌ View Only |
| **Multi-Patient Community Roster** | ❌ Blocked | ❌ Blocked | ✅ Assigned Hamlet| ✅ Assigned Patients|
| **Longitudinal Trend Curves (7/30/90d)**| ❌ Blocked | ⚠️ 7-Day Summary| ⚠️ Weekly Flag | ✅ Full Analytics |
| **Doctor Access Code Generation** | ❌ Blocked | ✅ Generate/Revoke| ❌ Blocked | ❌ Blocked |
| **Clinical Recommendation Notes** | ❌ Blocked | ❌ Blocked | ❌ Blocked | ✅ Full Write |
| **Database Export / Cloud Sync Admin**| ❌ Blocked | ✅ Manual Sync | ✅ Batch Sync | ❌ Read-Only |

---

## 🔑 3. AUTHENTICATION MECHANISMS & CRYPTOGRAPHY

### A. Patient Profile Selection (Zero-PIN Authentication)
* **Design**: Direct tap on registered photo avatar on `SCREEN_ONBOARDING_ROLE_SELECT`.
* **Security Guardrail**: Patient session tokens grant access only to `game_sessions` insert, `reminders` read, and `memory_items` read for the selected `patient_id`. Any attempt to access other patient tables throws a `SecurityException`.

### B. Caregiver Authentication (Local 6-Digit PIN)
* **Storage**: In SQLite `users` table, salted with a device-unique salt and hashed via **SHA-256** (or **PBKDF2WithHmacSHA256**).
* **Rate Limiting & Lockout**: After 5 consecutive failed PIN attempts, authentication is locked for **5 minutes** to prevent brute-force attacks.
* **Offline Independence**: Completely autonomous; never transmits the PIN over HTTP or Firebase.

### C. Doctor Access Protocol (6-Digit Dynamic Access Code)
* **Mechanics**: Caregivers generate a temporary 6-digit random authorization code inside their authenticated portal.
* **Expiration**: The code is cryptographically bound to `patient_id`, contains a strict expiration timestamp (default: 72 hours), and can be instantly revoked by the caregiver with one tap.
* **Clinician Entry**: Doctor enters the 6-digit code on their portal. Validation verifies hash and non-expired status before granting read-only access to trends.

---

## 🗄️ 4. DATA STORAGE & MEDIA PRIVACY

1. **Local SQLite Storage**:
   - SQLite database files (`smritisetu.db`, `smritisetu.db-wal`, `smritisetu.db-shm`) are located in `/data/data/com.sih26003.smritisetu/databases/`.
   - External SD-card or public storage storage of database files is strictly prohibited.
2. **Memory Garden Media (Photos & Voice Recordings)**:
   - Media files are stored in `context.filesDir/media/memories/`.
   - Files are named with UUIDv4 hashes (e.g. `uuid-4f9e.jpg`), never with patient names, Aadhaar numbers, or personal phone numbers.
   - Android `FileProvider` is used for sharing; raw `file://` URIs are forbidden.
3. **Secondary Firebase Cloud Replication**:
   - Firestore security rules (`backend/firebase/firestore.rules`) strictly require authenticated user claims matching `request.auth.uid`.
   - Patient records can only be queried by caregivers who have an active link in `relationships` or doctors holding an active `doctor_access` authorization token.

---

## 🛡️ 5. LOGGING & SENSITIVE DATA LEAKAGE

1. **Prohibited Log Contents**:
   - Never log patient full names, relationship records, or medical notes.
   - Never log raw PINs, hashed PINs, or device encryption keys.
   - Never log file paths containing recognizable patient identifiers.
2. **Production Log Stripping**:
   - ProGuard / R8 rules (`app/proguard-rules.pro`) strip `android.util.Log.v`, `d`, and `i` calls in release builds.
   - Only non-identifiable, sanitized error codes are captured for crashes.

---

## 🚨 6. SECRET DETECTION & VULNERABILITY REPORTING

1. **Automated Secret Scanning**:
   - Pre-commit hooks and GitHub Actions CI actively scan for high-entropy strings, private keys, `.keystore` files, and `.env` leaks.
   - If a credential is committed accidentally:
     - The commit must NOT merely be deleted in a subsequent commit.
     - The credential must be immediately invalidated/rotated at the service provider.
     - Git history must be purged using `git filter-repo` or BFG Repo-Cleaner.
2. **Reporting a Security Vulnerability**:
   - Please report security vulnerabilities to the core repository maintainers via security advisory or private email rather than opening a public issue.
   - Maintainers commit to triaging reports within 48 hours.
