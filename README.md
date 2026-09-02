# SmritiSetu (SIH26003)

**AI-Based Cognitive Gaming and Memory Assistance Platform for Elderly Dementia Patients in North Eastern Region (NER) of India**  
*Sponsoring Ministry*: Ministry of Development of North Eastern Region (MDoNER), Government of India.  
*Core Architecture Tenet*: **"ROOM IS KING. FIREBASE IS THE MESSENGER."**

---

## 🏛️ 1. Project Purpose & Architecture

SmritiSetu is engineered specifically for elderly individuals aged 60+ exhibiting Mild Cognitive Impairment (MCI) and early dementia across rural Assam, Manipur, Mizoram, and Nagaland. The platform prioritizes extreme accessibility, cultural familiarity, non-punitive gameplay, and guaranteed 100% offline functionality.

### Architecture Pillars:
* **Platform**: 100% Native Android built with Kotlin and Jetpack Compose.
* **Local Database (Room + SQLite)**: **Absolute Source of Truth**. All patients, relationships, game performance signals, and reminders are committed locally first.
* **Secondary Cloud Sync (Firebase)**: Used exclusively for opportunistic background backup and doctor telemetry. Gameplay **never** waits for network round-trips.
* **On-Device Adaptive ML**: Scikit-learn trained Decision Tree classifier exported to JSON and interpreted locally in Kotlin. Analyzes real physical latency, accuracy, errors, and hesitation to scale difficulty (1–5). **Zero cloud ML / zero LLMs**.
* **Shared Voice Layer**: Horizontal application service providing slow-paced ($0.85\times$) spoken instructions using bundled language packs (Assamese, English). Voice is **not** a 7th game.
* **Authentication**: Local 6-digit PIN (SHA-256 in Room) for Caregivers and Doctors. **Zero PIN for Patients** (direct photo/avatar tap). **Zero OTP / SMS gateways**.

---

## 🎮 2. The Six Native Playable Games

All six games inherit from the unified `BaseGameEngine` framework with strict non-punitive feedback:

| # | Game | Clinical Domain | Description | Difficulty Progression |
|---|---|---|---|---|
| **1** | **Family Trivia** (`পৰিয়ালৰ স্মৃতি`) | Memory & Identity | Personalized family photo recognition from Room database. | L1: Photo+Name (2 choices) $\rightarrow$ L2: Relationship prompt $\rightarrow$ L3: 4 choices $\rightarrow$ L4: Audio clue $\rightarrow$ L5: Direct recall. |
| **2** | **Voice Cue Card** (`কণ্ঠ আৰু ছবি`) | Attention & Auditory Recall | Spoken audio cues with visual cards and 5-second touch fallback. | L1: Single item $\rightarrow$ L2: Short instruction $\rightarrow$ L3: 2-step cue $\rightarrow$ L4: Mixed list $\rightarrow$ L5: 3-item sequence. |
| **3** | **Daily Sequencing** (`দৈনন্দিন ক্ৰম`) | Executive Function | Culturally familiar routines (Assam milk tea, morning temple routine). | L1: 2 steps $\rightarrow$ L2: 3 steps $\rightarrow$ L3: 4 steps $\rightarrow$ L4: 4 steps + distractor $\rightarrow$ L5: 5–6 chronological steps. |
| **4** | **Categorisation** (`শ্ৰেণীবিভাজন`) | Categorical Thinking | Grouping items into fruits, vegetables, animals, and traditional wear. | L1: 2 obvious groups $\rightarrow$ L2: 3 groups $\rightarrow$ L3: Less obvious items $\rightarrow$ L4: 4 groups + distractors $\rightarrow$ L5: Multi-item sorting. |
| **5** | **Village Market** (`গাঁওৰ বজাৰ`) | Working Memory | Shopping list recall with authentic NER goods (Joha rice, Kaji nemu). | L1: 2 items $\rightarrow$ L2: 3 items $\rightarrow$ L3: 3–4 items + stall distractors $\rightarrow$ L4: 8-item market stall $\rightarrow$ L5: 5 items + delayed recall. |
| **6** | **Pattern Recognition** (`আৰ্হি চিনাক্তকৰণ`) | Visuospatial Speed | High-contrast visual sequences and cultural motifs. | L1: 2-item alternating $\rightarrow$ L2: 3-item repeating $\rightarrow$ L3: Step rhythm $\rightarrow$ L4: Complex + distractors $\rightarrow$ L5: Abstract sequence. |

---

## 👥 3. Team Structure & Work Allocation

The engineering team consists of **six equal contributors**. Everyone writes code, everyone tests, and everyone reviews pull requests:

| Member | Feature Ownership Area | Designated Working Branch | Default Peer Reviewer |
| :--- | :--- | :--- | :--- |
| **Venkatesh** | Game Framework + Session/Metrics | `feature/venkatesh/game-framework` | Jasleen |
| **Jasleen** | Family Trivia + Personalization | `feature/jasleen/family-trivia` | Krishna |
| **Krishna** | Voice Cue Card + Voice Experience | `feature/krishna/voice-cue-card` | Shravani |
| **Shravani** | Reminders System (Medicine/Alarms) | `feature/shravani/reminders` | Bhavya |
| **Bhavya** | Accessibility + Patient UI Polish | `feature/bhavya/accessibility` | Kimaya |
| **Kimaya** | Sequencing + Categorisation Games | `feature/kimaya/sequencing-categorisation` | Venkatesh |

### 🔄 Review Rotation
Workload is balanced circularly:  
`Venkatesh` $\rightarrow$ `Jasleen` $\rightarrow$ `Krishna` $\rightarrow$ `Shravani` $\rightarrow$ `Bhavya` $\rightarrow`Kimaya` $\rightarrow`Venkatesh`.  
*Shared infrastructure PRs (Room schema, navigation, Gradle) require **TWO approvals**.*

---

## 🛑 4. Branch Strategy & Contribution Workflow

### Golden Git Rule:
> **NO DIRECT PUSHES TO `main` OR `develop`.**  
> All work occurs in `feature/<member>/<feature>` branches and merges into `develop` through Pull Requests and peer review.

```
[feature/<member>/<feature>] ──(PR + Review + CI)──> [develop] ──(Release PR)──> [main]
```

### 12-Step Contributor Workflow:
1. `git checkout develop && git pull origin develop`
2. `git checkout -b feature/<member>/<feature>`
3. Guide AI coding agent within your feature scope.
4. Run `git status` and `git diff` to ensure no unrelated files were touched.
5. Run `./gradlew testDebugUnitTest` and `./gradlew assembleDebug`.
6. Commit with conventional commit messages (e.g. `feat(reminders): add alarm scheduler`).
7. Push: `git push -u origin feature/<member>/<feature>`.
8. Open Pull Request targeting `develop`.
9. Peer reviewer completes review checklist.
10. CI builds and passes.
11. Squash and merge into `develop`.
12. Delete merged feature branch.

---

## 📂 5. Repository Layout

```text
SIH26003/
├── app/
│   ├── src/main/java/com/sih26003/smritisetu/
│   │   ├── feature/games/    # Common framework + 6 native games
│   │   ├── feature/auth/     # Local PIN & Patient photo select
│   │   ├── feature/patient/  # Large-target patient dashboard
│   │   ├── feature/caregiver/# Caregiver management & doctor linking
│   │   ├── feature/doctor/   # Longitudinal game signal review
│   │   ├── feature/reminders/# Exact offline AlarmManager scheduling
│   │   ├── data/local/       # Room database (7 entities, 7 DAOs)
│   │   ├── data/firebase/    # Background sync queue messenger
│   │   ├── ml/               # On-device Decision Tree interpreter
│   │   └── voice/            # Voice prompts & language packs
│   ├── src/main/assets/      # Local ML tree JSON & language packs
│   └── src/test/java/        # Unit tests
├── assets/                   # Master ML tree model & language packs
├── docs/                     # Architecture, Room schema, game specs, offline checklist
├── gradle/                   # Gradle wrapper & libs.versions.toml
├── scripts/                  # train_decision_tree.py
├── .gitignore                # Android & OS ignore rules
├── CONTRIBUTING.md           # Developer workflow & AI safety rules
├── LICENSE                   # Open source license
├── build.gradle.kts          # Root build configuration
├── gradle.properties         # JVM & AndroidX properties
├── gradlew / gradlew.bat     # Gradle wrapper executables
└── settings.gradle.kts       # Module settings
```

---

## ⚡ 6. Build & Test Commands

```bash
# 1. Train and export Decision Tree model to JSON
python scripts/train_decision_tree.py

# 2. Build Android Debug APK
./gradlew assembleDebug

# 3. Execute local unit tests
./gradlew testDebugUnitTest
```

---

## ⚠️ 7. Known Limitations & Safety Guardrails
* **Non-Diagnostic Policy**: Game performance metrics measure reaction times, errors, and hesitation. The system **never** outputs a dementia diagnosis or severity score.
* **No Cloud Dependency**: Audio prompts are generated locally via offline TTS and pre-recorded assets; internet outages never block gameplay.
