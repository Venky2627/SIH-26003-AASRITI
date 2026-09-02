# SmritiSetu (SIH26003) — Authoritative Locked Architecture

> **Platform**: 100% Native Android (Kotlin + Jetpack Compose)  
> **Core Architectural Tenet**: **"ROOM IS KING. FIREBASE IS THE MESSENGER."**  
> **Target Environment**: Rural North Eastern Region (NER) of India (Assam-first prototype)

---

## 🏛️ 1. Absolute Technology Lock

The architectural stack for SIH26003 is permanently locked. No alternative frameworks or cloud-first designs are permitted:

* **Operating System**: Android-first (minSdk = 24, compileSdk = 34).
* **Programming Language**: Kotlin (strict null safety, coroutines, StateFlow).
* **UI Framework**: Jetpack Compose (Declarative UI with Material 3).
* **Local Persistence (Source of Truth)**: Room Database (`smritisetu.db`) with SQLite engine.
* **Cloud Replication**: Firebase (Secondary background messenger only).
* **Authentication**: Local 6-digit PIN (SHA-256 in Room) for Caregiver and Doctor. **Zero PIN for Patients**.
* **Edge ML Engine**: Scikit-Learn trained Decision Tree classifier exported to JSON and evaluated locally in Kotlin via `DecisionTreeEngine.kt`.
* **Voice System**: Shared horizontal application service with slow-paced ($0.85\times$) offline TextToSpeech and pre-recorded language packs (`as`, `en`).
* **Media Storage**: App-private internal storage for photos and audio clips; Room stores file paths and metadata only.

---

## 🔄 2. Unidirectional Data Flow & Offline Guarantee

```
[Jetpack Compose UI]
       │  ▲
       ▼  │ (StateFlow / collectAsState)
[ViewModel / UI State Holder]
       │
       ▼
[Repository Layer]
       │
       ▼ (Immediate Synchronous Write)
[Room Local Database (SQLite)] <──── SINGLE SOURCE OF TRUTH
       │
       ▼ (Enqueue Pending Record)
[SyncQueue Table (smritisetu.db)]
       │
       ▼ (Opportunistic Sync when Online)
[Firebase Messenger]
```

### The Airplane Mode Guarantee:
The application must continue functioning with 100% feature availability when:
$$\text{Wi-Fi} = \text{OFF}, \quad \text{Mobile Data} = \text{OFF}, \quad \text{Airplane Mode} = \text{ON}$$

1. Patient launches application and enters Patient Mode without network prompts.
2. All 6 cognitive games load, run, and collect physical touch latency and hesitation metrics.
3. Decision tree executes on-device in $<1\text{ms}$ recommending the next difficulty (1–5).
4. `GameSessionEntity` commits immediately to Room SQLite.
5. Reminders schedule and fire offline via Android `AlarmManager`.
6. App restart restores all state directly from local SQLite.
7. Upon network reconnect, `FirebaseSyncMessenger` drains `sync_queue` without blocking gameplay or deleting local records.

---

## 🚫 3. Strictly Forbidden Architectures

The following paradigms are obsolete and strictly forbidden:
* ❌ **React Native / Expo / TypeScript / WebViews**: The project is native Kotlin.
* ❌ **OTP / SMS / Email-Password Auth**: Blocked in zero-connectivity rural environments; local PIN and photo select are the canonical auth models.
* ❌ **Cloud-Only ML or LLM-Powered Gameplay**: Prohibited due to latency, cost, and offline requirements.
* ❌ **Cloud-Dependent TTS or Cloud Speech APIs**: Must never block elderly gameplay.
* ❌ **Second Database or Second Navigation Graph**: All state belongs to `smritisetu.db` and the canonical Compose `NavHost`.

---

## 🎮 4. The Six Cognitive Games & Common Framework

All six games inherit from the unified `BaseGameEngine`:

$$\text{Select} \rightarrow \text{Patient Settings} \rightarrow \text{Instructions} \rightarrow \text{Gameplay} \rightarrow \text{Metrics} \rightarrow \text{Adaptive Decision} \rightarrow \text{Save to Room} \rightarrow \text{Feedback} \rightarrow \text{Next Round}$$

1. **Family Trivia** (Memory & Identity): Levels 1–5 using patient family photos and relationships from Room.
2. **Voice Cue Card** (Memory & Attention): Levels 1–5 with spoken item cues, 5-second voice fallback, and visual touch cards.
3. **Daily Sequencing** (Executive Function): Levels 1–5 with daily routines (Assam tea, morning Namghar routine) and distractors.
4. **Categorisation** (Attention & Thinking): Levels 1–5 grouping fruits, vegetables, animals, and traditional wear.
5. **Village Market** (Working Memory & Search): Levels 1–5 shopping list recall with authentic NER goods (Joha rice, Kaji nemu).
6. **Pattern Recognition** (Visuospatial Processing & Speed): Levels 1–5 with high-contrast color/shape sequence completion.

---

## 🖼️ 5. Reminiscence & Sensory Suite (Three Modules)

Reminiscence experiences provide calm, non-scored memory and sensory engagement:

* **Module 1: Memory Album**: Caregiver adds photos, names, relationships, and voice recordings. Patient views large photos and hears familiar family voices.
* **Module 2: Familiar Music / Audio**: Offline local player for traditional folk songs, borgeet, and calming melodies with play/pause/repeat controls.
* **Module 3: Festival / Food Stories**: Conversational cultural prompts (Bihu festivals, traditional pitha/tea making). Not a scored diagnostic game.

---

## 👨‍👩‍👦 6. Caregiver & Doctor Operational Boundaries

### Caregiver Suite:
* **Today's Priority**: Deterministic engine outputting max 3 actionable priorities (missed medications, appointments, recent falls).
* **Quick Log**: Fast $<30\text{s}$ event logger for medication, appetite, sleep, falls, wandering, and agitation.
* **Weekly Overview**: Real SQLite adherence percentages and incident tallies with zero fake graphs.
* **Emergency Toolkit**: Emergency Call intent, Patient Missing card with photo and emergency contacts, Fall triage steps, and De-escalation guidance.
* **Caregiver Wellbeing**: Weekly stress check-in (Doing okay, Tired, Stressed, Overwhelmed) with local history.

### Clinician Suite:
* **Doctor Access**: 6-digit Doctor Access Code entry granting scoped, revocable access.
* **Longitudinal Review**: Physical interaction trends (latency, errors, hesitation) without automated diagnosis.
* **Explainable Referral Prompts**: Rule-based prompts (*"Consider clinical review"*) triggered if $>1$ fall or adherence $<60\%$.
* **1-Page PDF Export**: Local generation via Android `PdfDocument` stored in app-private storage without cloud dependencies.

---

## 🔒 7. Healthcare Safety & DPDA 2023 Compliance

1. **Non-Diagnostic Policy**: Game performance metrics measure physical reaction speed, accuracy, and hesitation. The system **never** generates a dementia diagnosis or disease severity verdict.
2. **Volatile Audio**: Speech streams are processed in volatile memory and zeroed out; raw patient audio is never persisted.
3. **Pseudonymization**: All patient records utilize privacy-compliant pseudonym codes (e.g. `AS-KAM-0042`).
