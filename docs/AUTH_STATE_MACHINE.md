# SmritiSetu (SIH26003) — Authentication State Machine Specification

## ⚙️ 1. State Machine Blueprint

```
+---------------------------------------------------------------------------------------------------------+
|                                  SMRITISETU CORE AUTH STATE MACHINE                                     |
+---------------------------------------------------------------------------------------------------------+

              [BOOTSTRAP_START]
                     |
                     v
             (Check SecureStore)
             /                 \
     [Token/PIN Valid]     [No Valid Auth]
           /                     \
          v                       v
   +--------------+      +-------------------+
   | AUTHENTICATED|      |  UNAUTHENTICATED  | <------------------------------------+
   +--------------+      +-------------------+                                      |
          |                        |                                                |
          |               (Role Selected)                                           |
          |                        |                                                |
          |                        v                                                |
          |              +-------------------+                                      |
          |              |   ROLE_SELECTED   |                                      |
          |              +-------------------+                                      |
          |                        |                                                |
          |              (Phone Number Valid)                                       |
          |                        |                                                |
          |                        v                                                |
          |              +-------------------+                                      |
          |              |   PHONE_ENTERED   |                                      |
          |              +-------------------+                                      |
          |                        |                                                |
          |               [Check Net Connectivity]                                  |
          |               /                      \                                  |
          |        (Online: SMS Sent)      (Offline: Local PIN)                     |
          |              /                        \                                 |
          |             v                          v                                |
          |     +-------------------+      +-------------------+                    |
          |     |   OTP_PENDING     |      |  OFFLINE_CHALLENGE|                    |
          |     +-------------------+      +-------------------+                    |
          |              |                          |                               |
          |     (6-Digit Validated)        (PIN Hash Matches)                       |
          |              \                          /                               |
          |               v                        v                                |
          |              +--------------------------+                               |
          |              |     IDENTITY_VERIFIED    |                               |
          |              +--------------------------+                               |
          |                            |                                            |
          |                 [Has Profile in SQLite?]                                |
          |                 /                      \                                |
          |        (Yes: Dashboard)         (No: Setup Flow)                        |
          |               /                            \                            |
          |              v                              v                           |
          |     +------------------+          +-------------------+                 |
          |     |   PROFILE_LOAD   |          | PROFILE_INCOMPLETE|                 |
          |     +------------------+          +-------------------+                 |
          |              |                              |                           |
          |              |                     (Caregiver + Patient                 |
          |              |                      Record Committed)                   |
          |              |                              |                           |
          +------------->+<-----------------------------+                           |
                         |                                                          |
                         v                                                          |
                +------------------+                                                |
                |   ACTIVE_USER    |                                                |
                +------------------+                                                |
                         |                                                          |
                   (Explicit Logout or Consent Revocation) -------------------------+
```

---

## 🔄 2. State & Transition Details

| Current State | Event / Trigger | Target State | Guard Conditions | Side Effects / Actions |
| :--- | :--- | :--- | :--- | :--- |
| **UNAUTHENTICATED** | `SELECT_ROLE(role)` | **ROLE_SELECTED** | `role IN ['PATIENT', 'CAREGIVER', 'DOCTOR']` | Stores role in Redux; clears any previous volatile patient drafts. |
| **ROLE_SELECTED** | `ENTER_PHONE(phone)` | **PHONE_ENTERED** | `phone.length == 10 AND startsWith(6,7,8,9)` | Sanitizes number; hashes with SHA-256 in memory. |
| **PHONE_ENTERED** | `REQUEST_OTP (Online)` | **OTP_PENDING** | `NetInfo.isInternetReachable == true` | Invokes SMS Gateway; starts 600s countdown timer. |
| **PHONE_ENTERED** | `REQUEST_OTP (Offline)` | **OFFLINE_CHALLENGE**| `NetInfo.isInternetReachable == false` | Checks local Keystore; prompts for 6-digit offline PIN. |
| **OTP_PENDING** | `SUBMIT_OTP(code)` | **IDENTITY_VERIFIED** | `code.length == 6 AND code == expectedOtp` | Issues JWT; stores refresh token in `expo-secure-store`. |
| **OFFLINE_CHALLENGE** | `SUBMIT_PIN(pin)` | **IDENTITY_VERIFIED** | `PBKDF2(pin, salt) == storedHash` | Unlocks local SQLite encryption key context. |
| **IDENTITY_VERIFIED** | `CHECK_PROFILE` | **ACTIVE_USER** | `caregiverExists && patientExists` | Loads patient profile; enters game dashboard. |
| **IDENTITY_VERIFIED** | `CHECK_PROFILE` | **PROFILE_INCOMPLETE**| `!caregiverExists || !patientExists` | Directs to `CaregiverProfile` and `PatientSetup`. |
| **PROFILE_INCOMPLETE**| `SAVE_PROFILES` | **ACTIVE_USER** | `valid(caregiver) && valid(patient)` | Commits atomic transaction to SQLite; enqueues sync tasks. |
| **ACTIVE_USER** | `REVOKE_CONSENT` | **UNAUTHENTICATED** | None | Purges SQLite `patients` table; wipes Keystore PIN; returns to Splash. |

---

## 🔒 3. Offline Authentication Resilience
1. **Network Disconnection Mid-Auth**:
   * If internet drops between Phone Entry and OTP: App seamlessly switches to `OFFLINE_CHALLENGE` without discarding the entered phone number.
2. **Expired Token While In Field**:
   * If JWT token expiry (24 hours) occurs while patient is in a remote village for 3 weeks:
   * The app **never terminates patient gameplay**.
   * It downgrades network status to `'OFFLINE_RENEWAL_PENDING'`, validates the local PIN hash, and allows full access to local SQLite databases.
