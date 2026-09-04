# AASRITI (SIH-26003) — Master System Blueprint & Engineering Guide

> **AI-Based Cognitive Gaming, Memory Assistance, and Care-Connection Platform for Elderly Dementia Patients in the North Eastern Region (NER) of India**  
> *Sponsoring Ministry*: Ministry of Development of North Eastern Region (MDoNER), Government of India  
> *Core Architectural Axiom*: **"ROOM LOCAL SQLITE IS KING. FIREBASE IS THE MESSENGER."**  
> *Target Environment*: 100% Autonomous Offline Execution in Rural NER (Assam, Manipur, Meghalaya)

---

## 🏛️ 1. WHAT AASRITI IS

In rural North Eastern India, elderly individuals facing Mild Cognitive Impairment (MCI) and early dementia encounter geographic isolation, intermittent 2G/zero internet connectivity, low digital literacy, and cultural alienation from generic Westernized cognitive tools.

**AASRITI** is an offline-autonomous Android companion platform engineered around elderly patients, family caregivers, rural ASHA workers, and clinicians.

### Core Capabilities:
1. **Patient Guided Companion (Very Low Density)**: Direct photo avatar entry (Zero PIN barrier), large touch targets ($\ge 64\text{dp}$), temporal orientation, daily routine assistance, and family calling.
2. **Culturally Grounded Cognitive Gaming**: Six native mini-games reflecting authentic regional heritage (Assam tea preparation, Namghar routines, local village market goods, high-contrast cultural motifs).
3. **On-Device Adaptive Difficulty (Zero Cloud ML)**: Scikit-learn trained Decision Tree classifier evaluated locally in Kotlin in $<0.5\text{ms}$ with zero cloud dependencies.
4. **Memory Garden (Reminiscence)**: High-contrast family photo album with attached spoken voice notes from children and grandchildren.
5. **Caregiver & ASHA Field Tools**: Under-30-second Quick Care Log for medications, appetite, sleep, and falls; Today's Priority triage; and multi-patient roster switching on shared devices.
6. **Clinician Longitudinal Signals**: 7/30/90-day physical reaction time and hesitation trend curves with 1-page high-contrast PDF clinical summary generation.
7. **Absolute Ethical Guardrail**: Evaluates physical interaction signals; **never** claims to diagnose dementia or compute clinical disease severity scores.

---

## 🚀 2. HOW TO RUN IT

### Prerequisites:
* **Android Studio**: Jellyfish, Iguana, or Hedgehog (2023.2+)
* **Java Development Kit**: JDK 17 (Temurin or OpenJDK)
* **Android SDK**: Compile SDK 34, Min SDK 24 (Android 7.0+)
* **Python (Optional for ML model export)**: Python 3.10+ (`scikit-learn`, `numpy`)

### Quick Start:
```bash
# 1. Clone repository
git clone https://github.com/TeamNameSIH/AI-Based-Cognitive-Gaming-Platform-for-Elderly-Dementia-Patients-in-NER.git AASRITI
cd AASRITI

# 2. Copy environment template (optional)
cp .env.example .env

# 3. Verify ML Decision Tree export (optional)
python scripts/train_decision_tree.py

# 4. Build and run unit tests
./gradlew testDebugUnitTest

# 5. Build Debug APK
./gradlew assembleDebug
```
*In Android Studio*: Click **File $\rightarrow$ Open $\rightarrow$ select AASRITI**, wait for Gradle sync, connect device via USB (or start an API 34 emulator), and press **Run (▶)**.

---

## 📂 3. HOW THE REPOSITORY IS STRUCTURED

AASRITI follows Feature-First Clean Architecture to ensure parallel work across 6 developers:

```text
AASRITI/
├── AGENTS.md                            # Primary Antigravity & AI agent operating system
├── README.md                            # Start-here guide & system architecture
├── CONTRIBUTING.md                      # Plain-language contributor lifecycle
├── SECURITY.md                          # Full-product security, DPDPA 2023, and RBAC
├── .env.example                         # Environment configuration template
│
├── UI_RULES.md                          # Master UI constitution & 13 canonical colors
├── UI_SCREEN_SPEC.md                    # Canonical layout contracts for all screens
├── UI_COMPONENT_RULES.md                # Component behaviors, buttons, cards, voice pill
├── UI_HERITAGE_GUIDE.md                 # Regional heritage rules (Assam, Manipur, Meghalaya)
├── UI_ACCESSIBILITY_RULES.md            # WCAG 2.2 AAA standards & >=64dp touch targets
│
├── app/
│   └── src/main/java/com/sih26003/smritisetu/
│       ├── MainActivity.kt              # Single-activity NavHost wrapped in AasritiTheme
│       ├── SmritiSetuApplication.kt     # Application container & dependency wiring
│       ├── core/
│       │   ├── ui/theme/                # AasritiTheme, Color, Type, Shape, Spacing tokens
│       │   └── security/                # CryptoUtils, SHA-256 local PIN hashing
│       ├── data/
│       │   ├── local/database/          # Room SQLite Database singleton (smritisetu.db)
│       │   ├── local/entity/            # Room SQLite Entities (Zero UI imports)
│       │   ├── local/dao/               # 7 Room DAOs
│       │   └── mapper/                  # Entity <-> Domain Model extension mappers
│       ├── domain/model/                # Pure Kotlin immutable domain data classes
│       ├── engine/
│       │   ├── adaptive/                # DecisionTreeEngine (Difficulty 1-5 scaler)
│       │   ├── priority/                # Deterministic care triage engine
│       │   ├── reminder/                # AlarmManager offline scheduling & BootReceiver
│       │   └── trend/                   # Longitudinal reaction time & hesitation analyzer
│       ├── feature/
│       │   ├── auth/                    # Role selection & local PIN auth
│       │   ├── patient/                 # Patient home (Very Low Density)
│       │   ├── games/                   # BaseGameEngine + 6 cognitive games
│       │   ├── memoryalbum/             # Memory Garden photo & voice album
│       │   ├── caregiver/               # Caregiver dashboard & Quick Log
│       │   ├── asha/                    # Multi-patient community roster
│       │   └── doctor/                  # Doctor access & patient snapshot
│       ├── sync/                        # Offline sync_queue processor & network listener
│       └── voice/                       # Shared voice manager (0.85x TTS & audio packs)
│
├── backend/firebase/                    # Security rules & indexes for background sync
├── docs/                                # Detailed technical documents
│   ├── ARCHITECTURE.md                  # Comprehensive product architecture
│   ├── GETTING_STARTED_FOR_TEAM.md      # Beginner developer onboarding guide
│   ├── DATA_MODEL.md                    # SQLite schemas and domain models
│   ├── API_SYNC_CONTRACT.md             # Offline sync queue payload contracts
│   ├── DEMO_SCRIPT.md                   # 16-step verified demonstration script
│   ├── DECISIONS.md                     # Architectural Decision Records (ADRs 001-006)
│   ├── PS_TRACEABILITY.md               # SIH Problem Statement Traceability Matrix
│   ├── IMPLEMENTATION_ROADMAP.md        # 10-day sprint execution roadmap
│   ├── PROJECT_STATE.md                 # Current phase and milestone status
│   ├── TASK_BOARD.md                    # Granular P0/P1/P2 task tickets
│   └── TEAM_ALLOCATION.md               # 6-member subsystem ownership matrix
│
├── .github/
│   ├── workflows/android.yml            # CI build, test, secret scan, UI token checks
│   ├── pull_request_template.md         # Comprehensive PR checklist
│   └── CODEOWNERS                       # Review routing for protected files & subsystems
└── scripts/train_decision_tree.py       # Scikit-learn model training script
```

---

## 👥 4. HOW TO CONTRIBUTE

All 6 team members follow the **11-Stage Contributor Lifecycle**:
```
1. CLONE ──► 2. RUN ──► 3. BRANCH ──► 4. WORK ──► 5. TEST ──► 6. COMMIT
                                                                  │
11. MERGE ◄── 10. APPROVE ◄── 9. REVIEW ◄── 8. CI ◄── 7. PUSH & PR ◄┘
```

1. **Work in a feature branch**: Never commit directly to `main` or `develop`.  
   Format: `feature/<member>/<task-name>` (e.g. `feature/kimaya/memory-garden`).
2. **Tell Antigravity**: Say *"I'm Kimaya, set me up."* Antigravity will check out your branch, load all rules, and verify your clean workspace.
3. **Commit clearly**: Use conventional commit prefixes (`feat:`, `fix:`, `docs:`, `ui:`).
4. **Open a PR**: Target `develop`. Fill out the `.github/pull_request_template.md` checklist.
5. **Get Peer Approval**: Your designated peer reviewer must approve before merge:
   $$\text{Venkatesh} \rightarrow \text{Jasleen} \rightarrow \text{Krishna} \rightarrow \text{Bhavya} \rightarrow \text{Shravani} \rightarrow \text{Kimaya} \rightarrow \text{Venkatesh}$$

For full details, read [`/docs/GETTING_STARTED_FOR_TEAM.md`](file:///docs/GETTING_STARTED_FOR_TEAM.md) and [`/CONTRIBUTING.md`](file:///CONTRIBUTING.md).

---

## 📜 5. WHERE THE RULES ARE (SOURCE OF TRUTH)

The Git repository itself is the permanent guardian of all product rules:

| Rule Category | Authoritative File | Key Takeaway |
| :--- | :--- | :--- |
| **AI Agent Operating System** | [`/AGENTS.md`](file:///AGENTS.md) | 20 golden rules, developer detection, and task contract. |
| **UI Design Constitution** | [`/UI_RULES.md`](file:///UI_RULES.md) | 13 canonical colors, no dashboards for patients, typography scale. |
| **Screen Layout Contracts** | [`/UI_SCREEN_SPEC.md`](file:///UI_SCREEN_SPEC.md) | Rigid information hierarchy for every major screen. |
| **Component Specifications**| [`/UI_COMPONENT_RULES.md`](file:///UI_COMPONENT_RULES.md)| Buttons, cards, voice pill states, and feedback banners. |
| **Cultural Heritage Guide** | [`/UI_HERITAGE_GUIDE.md`](file:///UI_HERITAGE_GUIDE.md) | Authentic Assam, Manipur, and Meghalaya visual integration. |
| **Accessibility & COGA** | [`/UI_ACCESSIBILITY_RULES.md`](file:///UI_ACCESSIBILITY_RULES.md)| $\ge 64\text{dp}$ touch targets, non-punitive copy, WCAG AAA. |
| **Full-Product Security** | [`/SECURITY.md`](file:///SECURITY.md) | RBAC matrix, local PIN hashing, zero plaintext secrets. |
| **System Architecture** | [`/docs/ARCHITECTURE.md`](file:///docs/ARCHITECTURE.md)| Room persistence, sync queue, and Decision Tree runtime. |
| **Team Subsystem Allocation** | [`/docs/TEAM_ALLOCATION.md`](file:///docs/TEAM_ALLOCATION.md)| 6-member vertical ownership, safe directories, reviewers. |
| **Sprint State & Task Board**| [`/docs/PROJECT_STATE.md`](file:///docs/PROJECT_STATE.md)| Active Phase 1 status, milestones, and P0/P1/P2 task tickets. |

---

## ⚙️ 6. HOW CI & PULL REQUESTS WORK

Every Pull Request targeting `develop` or `main` automatically triggers **GitHub Actions CI** (`.github/workflows/android.yml`):
1. **Security Scan**: Automatically searches for accidental commits of `.env`, private keys (`.pem`, `.p12`), or `.keystore` files.
2. **Governance Verification**: Verifies presence and integrity of all constitutional UI and security files.
3. **ML Engine Validation**: Runs `scripts/train_decision_tree.py` to confirm model training and JSON export.
4. **Android Build & Test**: Runs `./gradlew testDebugUnitTest` and `./gradlew assembleDebug` using Java 17.

*PRs cannot be merged if any CI step fails or if the designated peer reviewer has not approved.*

---

## 📦 7. DEPLOYMENT & VERIFICATION

### Offline Airplane Mode Verification:
AASRITI is engineered to operate 100% autonomously without internet connectivity.
To verify:
1. Install debug APK on physical Android device:
   ```bash
   ./gradlew installDebug
   ```
2. Turn **Airplane Mode ON** (0 Kbps internet).
3. Execute the 16-step demonstration script in [`/docs/DEMO_SCRIPT.md`](file:///docs/DEMO_SCRIPT.md):
   - Authenticate caregiver locally via 6-digit PIN.
   - Switch to Patient (Zero PIN photo tap).
   - Play Family Trivia and Voice Cue Card; verify adaptive difficulty updates in SQLite.
   - Log a care observation in Quick Log (<30s).
   - Switch to Doctor view via 6-digit access code and verify 7-day reaction time trend.
4. Turn **Airplane Mode OFF**: Verify that `SyncQueueProcessor` opportunistically drains mutations to Firebase Firestore in the background without user interruption.
