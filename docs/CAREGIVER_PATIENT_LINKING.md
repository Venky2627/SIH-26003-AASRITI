# SmritiSetu (SIH26003) — Caregiver & Patient Relational Linking Logic

## 👨‍👩‍👧 1. Clinical Context: Family-Centric Dementia Care in NER

In rural communities across Assam, Manipur, Mizoram, and Nagaland:
* Over 90% of elderly dementia patients share a single mobile smartphone with a resident family member (son, daughter-in-law, spouse).
* A single caregiver often cares for **two elders** simultaneously (e.g., aging grandfather and grandmother exhibiting varying stages of cognitive decline).
* The software architecture must support **Shared Device Mode** with distinct patient profile profiles, zero data cross-leakage, and instant profile switching.

---

## 🗄️ 2. Relational Database Schema & Foreign Key Constraints

```sql
-- Link Caregiver to Patient
-- Caregiver table links to a primary patient, while supporting a junction table for multiple patients
CREATE TABLE IF NOT EXISTS caregiver_patient_links (
    id TEXT PRIMARY KEY NOT NULL,                       -- UUIDv4
    caregiver_id TEXT NOT NULL,                         -- References caregivers(id)
    patient_id TEXT NOT NULL,                           -- References patients(id)
    access_role TEXT NOT NULL CHECK (access_role IN ('PRIMARY_CAREGIVER', 'SECONDARY_GUARDIAN', 'ASHA_VISITOR')),
    linked_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
    is_synced INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (caregiver_id) REFERENCES caregivers(id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    UNIQUE(caregiver_id, patient_id)
);

CREATE INDEX IF NOT EXISTS idx_links_caregiver ON caregiver_patient_links(caregiver_id);
CREATE INDEX IF NOT EXISTS idx_links_patient ON caregiver_patient_links(patient_id);
```

---

## 📱 3. Shared Device vs. Separate Device Operational Modes

### Mode A: Shared Device (Most Common in Rural Villages)
* The caregiver configures the app once on their smartphone.
* Both Caregiver and Patient interact with the same physical hardware.
* **Patient Viewport**: Locked strictly to gameplay viewports (`Speed Match`, `Story Weaver`, `Picture Naming`). Patient cannot view clinical settings or delete data.
* **Caregiver Lock**: Caregiver Dashboard and Patient Management require entering the **6-digit Master PIN**. If no input occurs for 3 minutes, the dashboard automatically locks back to Patient Mode.

### Mode B: Separate Devices (ASHA Worker / Telemedicine Mode)
* An ASHA community worker visits patient households with a dedicated tablet.
* Patient profiles are imported via encrypted local Bluetooth / Wi-Fi Direct or opportunistic sync batches.
* The ASHA worker can link up to 25 distinct village patient profiles, viewing longitudinal cognitive trends while keeping each patient's medical records isolated.

---

## 👁️ 4. Data Visibility & Isolation Rules

1. **Patient Data Boundary**:
   * Patient profile A can **never** view cognitive test history, baseline scores, or reaction times of Patient profile B.
2. **Caregiver Scope**:
   * Caregiver sees a top dropdown switcher: *"বোপা (Grandfather - MCI)"* vs *"আইতা (Grandmother - Preclinical)"*.
   * Switching profiles instantly swaps local Redux active state:
     ```typescript
     dispatch(switchActivePatient({ patientId: targetId }));
     ```
3. **Audit Trail**:
   * Every game session record explicitly includes `patient_id` and the authorizing `caregiver_id` in the metadata payload for DPDA compliance.
