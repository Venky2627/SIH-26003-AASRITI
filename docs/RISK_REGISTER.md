# SmritiSetu (SIH26003) — Comprehensive Risk Register & Mitigation Matrix

## 🛡️ 1. Risk Assessment Methodology

Risks are quantified based on standard Healthcare Technology Assessment criteria:
* **Probability (P)**: 1 (Rare) to 5 (Almost Certain)
* **Impact (I)**: 1 (Negligible) to 5 (Catastrophic / Disqualification)
* **Risk Score (R)**: $P \times I$ (High: 15–25, Medium: 8–14, Low: 1–7)

---

## 📊 2. Top 20 Architectural, Clinical, and Hackathon Delivery Risks

| ID | Category | Risk Description | P | I | R | Mitigation Strategy | Owner | Fallback Plan |
| :--- | :--- | :--- | :---: | :---: | :---: | :--- | :--- | :--- |
| **R01** | Edge AI | ONNX Runtime Mobile crashes or OOMs on 2GB/3GB RAM Android phones. | 4 | 5 | **20** | Enforce INT8 dynamic quantization; limit context window; test on physical Redmi 9A. | `@ai-edge` | Graceful fallback to offline phonetic rule-matcher or multi-choice tap. |
| **R02** | Speech | IndicConformer exhibits high Word Error Rate on elderly dysarthric speech. | 4 | 4 | **16** | Keyword spotting / semantic concept extraction rather than verbatim transcription. | `@ai-edge` | Offer acoustic syllable cueing + large visual choices. |
| **R03** | Privacy | Accidental caching of patient audio file to non-volatile device storage. | 2 | 5 | **10** | In-memory stream buffer zeroing (`fill(0)`); automated CI check forbidding `.wav` writes. | `@lead-architect` | Emergency disk scrubber runs on app initialization. |
| **R04** | Storage | SQLite file corruption during sudden device battery loss. | 3 | 4 | **12** | Configure `PRAGMA journal_mode = WAL;` and wrap game outcomes in transactions. | `@mobile-core` | Automated SQLite `.recover` routine on app cold start. |
| **R05** | Offline Sync | Infinite retry loop drains patient battery during intermittent 2G cellular. | 4 | 3 | **12** | Exponential backoff with jitter + max 5 retries before entering 1-hour sleep. | `@backend-sync` | Force sync to pause until unmetered Wi-Fi connection is detected. |
| **R06** | Hardware | Judges' test smartphone runs out of battery or screen breaks during demo. | 3 | 5 | **15** | Maintain 3 fully configured physical Android devices charged to 100%. | `@clinical-qa` | Switch immediately to hot-standby secondary phone in <10s. |
| **R07** | Regulatory | Evaluators question CDSCO SaMD classification and diagnostic claims. | 4 | 4 | **16** | Frame platform explicitly as "Cognitive Stimulation Therapy (CST) Adjunct", not diagnostic tool. | `@lead-architect` | Present CDSCO SaMD regulatory roadmap slide in pitch deck. |
| **R08** | Accessibility | Elderly user frustrates due to accidental double taps from hand tremors. | 4 | 4 | **16** | Implement $600\text{ms}$ debounce and $80\text{ms}$ dwell filter on all touch handlers. | `@clinical-qa` | Enable "Assistive Steady Touch" mode in caregiver preferences. |
| **R09** | UI/UX | Text illegible for elderly user suffering from mature cataracts. | 4 | 3 | **12** | Maintain strict contrast ratio $> 7:1$ (Pure Black `#121212` / Pure Yellow `#FFD700`). | `@clinical-qa` | Provide instant spoken voice readout button on every screen. |
| **R10** | Cultural | Elderly users do not recognize urban/Western visual stimuli. | 3 | 4 | **12** | Curate indigenous assets verified by regional culture experts (Japi, Rhino, Sangai). | `@clinical-qa` | Provide regional localized asset packs per state. |
| **R11** | Collaboration | Git merge conflicts in Redux store or SQLite schema during hackathon. | 4 | 3 | **12** | Strict directory domain ownership; schema changes restricted to `@mobile-core`. | `@lead-architect` | Rebase on develop immediately with peer review. |
| **R12** | Edge AI | Model loading latency ($>3\text{s}$) causes perceived app freeze on cold start. | 3 | 3 | **9** | Pre-warm ONNX inference session in background worker during splash screen. | `@ai-edge` | Display culturally soothing animated loading spinner with Assamese voice. |
| **R13** | Database | `sync_queue` table grows indefinitely if device is offline for 6 months. | 2 | 3 | **6** | Cap queue at 5,000 entries; rollup historical rounds into statistical daily aggregates. | `@mobile-core` | Trigger automated vacuum and archive old records. |
| **R14** | Mobile | React Native bridge serialization bottleneck degrades 60 FPS animation. | 3 | 3 | **9** | Use React Native Reanimated 3 with native thread execution. | `@mobile-games` | Simplify visual particle effects to static CSS transforms. |
| **R15** | Security | Malicious APK sideload decompilation exposes patient database encryption key. | 2 | 4 | **8** | Derive SQLCipher key via Android Keystore hardware-backed master key. | `@lead-architect` | Code obfuscation via ProGuard/R8 in release APK build. |
| **R16** | Ethics | Caregiver revokes consent, demanding complete data expungement. | 2 | 4 | **8** | Implement atomic "Purge Patient Record" function executing `DELETE FROM patients`. | `@mobile-core` | Cryptographic wipe of SQLCipher database file and key. |
| **R17** | Backend | FastAPI server crashes under concurrent demonstration traffic. | 2 | 3 | **6** | Dockerized deployment with Uvicorn workers and PostgreSQL connection pooling. | `@backend-sync` | App runs 100% offline anyway; backend crash does not impact demo. |
| **R18** | Evaluation | Presentation exceeds 5-minute strict hackathon judging time limit. | 4 | 4 | **16** | Rehearse timed 4-minute pitch script with stopwatch; 1 minute reserved for live ASR. | `@lead-architect` | Cut straight to the offline live airplane mode demo. |
| **R19** | Translation | Dialect variations within Assamese (Upper vs Lower Assam) reduce accuracy. | 4 | 3 | **12** | Calibrate vocabulary on standardized regional phonetic roots. | `@ai-edge` | Offer caregiver dialect tuning toggle in settings. |
| **R20** | Clinical | Patient experiences catastrophic emotional reaction to perceived test failure. | 3 | 4 | **12** | Eliminate "Game Over" or failure sounds; replace with gentle positive reinforcement. | `@clinical-qa` | Immediate soothing audio prompt encouraging the elder. |

---

## 📶 3. OFFLINE BEHAVIOR SPECIFICATION (Risk Management)

### 1. What happens when high-risk events occur with zero internet connectivity?
* Because the application has zero cloud dependencies, 75% of typical cloud risks (API downtime, SSL expiry, DNS failure, DDoS) are eliminated by design.
* Local failure modes (e.g., ASR model timeout) fail gracefully to local fallback modes (touch choice) without hanging the UI.

### 2. What data is stored locally vs. requires cloud?
* All risk monitoring logs and internal diagnostic traces are written to local SQLite `diagnostic_logs` table.

### 3. What is the fallback/degradation strategy if cloud is unreachable?
* Cloud is treated as an optional downstream mirror. Total isolation is normal operation.

### 4. How does the user know they're in offline mode? (UI indicators)
* High contrast status indicator visually informs user without triggering anxiety.

### 5. How does data integrity survive app crashes during offline operation?
* ACID transactions and Write-Ahead Logging prevent data corruption during hard power cutoffs.
