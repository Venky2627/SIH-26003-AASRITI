# AASRITI (SIH-26003) — 10-Day Implementation Roadmap

> **Notice**: This document defines the **execution blueprint and engineering sequence** for the 6-person team. No application code is executed during this planning phase.

---

## 🗓️ 10-Day Sprint Schedule

```
Day 1: Audit & Git Hardening ──────> Day 2: Game Framework & ML ────> Day 3: Six Games Complete
                                                                                 │
Day 6: Caregiver Suite <─────────── Day 5: Reminiscence & Safety <───────────────┘
  │
Day 7: Meds, Alarms & Emergency ───> Day 8: Clinician Summary & PDF ──> Day 9: Firebase Sync
                                                                                 │
Day 10: QA, Debloat, Verification & Final Build Freeze <─────────────────────────┘
```

---

### Day 1: Audit, Cleanup, Renaming & Git Hardening
* **Focus**: Repository normalization and branch protection.
* **Tasks**:
  * Verify clean repository root without legacy React Native or OTP artifacts.
  * Establish GitHub branch protection rules on `main` and `develop`.
  * Verify CI build pipeline (`.github/workflows/android.yml`).
  * Verify `.github/CODEOWNERS` and `.github/pull_request_template.md`.
* **Definition of Done**: One clean, understandable repository with working Gradle wrapper, clean tree, and verified CI.

---

### Day 2: Common Game Framework, GameSession & Adaptive ML
* **Responsible Member**: Venkatesh (Reviewer: Jasleen)
* **Tasks**:
  * Verify multi-round game lifecycle: `Instructions` $\rightarrow$ `Playing` $\rightarrow$ `Feedback` $\rightarrow$ `Next Round`.
  * Validate `PerformanceCollector` accuracy, latency, error count, and hesitation detection ($>3500\text{ms}$).
  * Verify `DecisionTreeEngine` offline inference ($<1\text{ms}$) reading `assets/ml/decision_tree_difficulty.json`.
  * Confirm deterministic fallback (difficulty 1–5) if JSON load fails.
* **Definition of Done**: Real multi-round lifecycle with real physical metrics persisted to Room `game_sessions` table.

---

### Day 3: The Six Cognitive Games Deepening
* **Responsible Members**: Jasleen, Krishna, Kimaya (Reviewers: Krishna, Shravani, Venkatesh)
* **Tasks**:
  * Game 1 (Family Trivia): Verify L1–L5 progression with local Room `relationships`.
  * Game 2 (Voice Cue Card): Verify L1–L5 cues with 5-second voice fallback and touch redundancy.
  * Game 3 (Sequencing): Verify L1–L5 daily activities with distractor handling in L4/L5.
  * Game 4 (Categorisation): Verify L1–L5 classification (fruits, vegetables, animals, textiles).
  * Game 5 (Village Market): Verify L1–L5 shopping list recall with authentic NER goods.
  * Game 6 (Pattern Recognition): Verify L1–L5 shape/color pattern sequences.
* **Definition of Done**: All six games genuinely playable with meaningful level differences and zero crashes.

---

### Day 4: Personalization, Voice & Patient Game Flow
* **Responsible Members**: Jasleen & Krishna (Reviewers: Krishna & Shravani)
* **Tasks**:
  * Connect caregiver-entered patient family photos and voice recordings to Game 1.
  * Calibrate speech rate to $0.85\times$ for elderly cognitive comfort in Assamese (`as`) and English (`en`).
  * Verify patient home navigation with large touch targets ($\ge 60\times 60\text{ dp}$).
* **Definition of Done**: Patient family data and voice prompts directly influence gameplay without cloud network calls.

---

### Day 5: Reminiscence Modules, Safety Screening & Consent
* **Responsible Members**: Jasleen, Krishna, Bhavya (Reviewers: Krishna, Shravani, Kimaya)
* **Tasks**:
  * Module 1 (Memory Album): High-contrast family photos, relationship labels, and voice recordings.
  * Module 2 (Familiar Music / Audio): Local audio player for traditional Assamese folk tunes (play/pause/repeat).
  * Module 3 (Festival / Food Stories): Cultural prompts for Bihu and traditional dishes (non-scored).
  * Safety Screening Questionnaire: Severe hallucinations / wandering risk routing (Safe vs Supervised Mode).
  * Consent Manager: Granular checkboxes for participation, photos, cloud sync, doctor sharing.
* **Definition of Done**: Reminiscence suite usable offline; safety screening routes flagged users to calm sensory mode.

---

### Day 6: Caregiver Suite (Today's Priority, Quick Log, Weekly Overview)
* **Responsible Member**: Shravani (Reviewer: Bhavya)
* **Tasks**:
  * Today's Priority Engine: Deterministic logic selecting max 3 actionable items from missed meds, appointments, or falls.
  * Quick Log: Fast $<30\text{s}$ interface recording medication, appetite, sleep, falls, wandering, agitation.
  * Weekly Overview: Aggregate real Room records for weekly adherence and incident counts (no fake graphs).
* **Definition of Done**: Real SQLite data drives the caregiver dashboard with zero fabricated numbers.

---

### Day 7: Medication, Appointments, Reminders & Emergency Toolkit
* **Responsible Members**: Shravani & Bhavya (Reviewers: Bhavya & Kimaya)
* **Tasks**:
  * Medication Entity & Schedule: Name, dose, frequency, active status, refill reminder.
  * Appointments Entity: Date, time, clinician, notes.
  * Android `AlarmManager`: Exact offline alarms registered across device reboots via `BootReceiver`.
  * Emergency Toolkit: Emergency Call intent, Patient Missing card with photo and emergency contacts, Fall triage guide, and De-escalation steps.
* **Definition of Done**: Caregiver can manage medications and appointments; offline alarms fire reliably without network.

---

### Day 8: Clinician Summary, Explainable Referral Prompts & PDF Export
* **Responsible Member**: Venkatesh (Reviewer: Jasleen)
* **Tasks**:
  * Doctor Access Screen: 6-digit Doctor Access Code entry and validation.
  * Longitudinal Review: Trends in game latency, error frequency, and hesitation counts.
  * Explainable Referral Prompts: Rule-based recommendation (*"Consider clinical review"*) triggered if $>1$ fall or adherence $<60\%$ (Never diagnosing dementia).
  * 1-Page PDF Generator: Native Android `android.graphics.pdf.PdfDocument` generation stored in app-private storage.
* **Definition of Done**: Clinician generates a clean 1-page summary PDF locally on device without cloud dependencies.

---

### Day 9: Secondary Firebase Sync & Offline / Reconnect Verification
* **Responsible Member**: Venkatesh (Reviewer: Jasleen)
* **Tasks**:
  * Audit `FirebaseSyncMessenger` against the core rule: *"Room is King, Firebase is the Messenger"*.
  * Verify that Room operations enqueue to `sync_queue` and drain opportunistically when connectivity is detected.
  * Execute 14-Step Offline Verification Checklist (Airplane Mode testing).
  * Confirm that local SQLite records are never deleted post-sync.
* **Definition of Done**: 100% autonomous offline gameplay with seamless background synchronization on network reconnect.

---

### Day 10: Full QA, Code Debloat, Documentation & Build Freeze
* **Responsible Members**: All 6 Equal Contributors
* **Tasks**:
  * Audit entire codebase for unused classes, duplicate functions, and stale imports.
  * Verify that no obsolete React Native, Expo, or OTP references remain.
  * Run full test suite: `./gradlew testDebugUnitTest`.
  * Assemble final signed/debug APK: `./gradlew assembleDebug`.
  * Rehearse and verify the 16-step SIH demo flow.
* **Definition of Done**: Zero critical bugs, clean git tree, accurate README, and canonical repository freeze.
