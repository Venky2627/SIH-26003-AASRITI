# SmritiSetu (SIH26003)

**AI-Based Cognitive Gaming & Memory Assistance Platform for Elderly Dementia Patients in NER**  
*Sponsoring Ministry*: Ministry of Development of North Eastern Region (MDoNER), Govt. of India.  
*Core Philosophy*: **"ROOM IS KING. FIREBASE IS THE MESSENGER."**

---

## 🏛️ Architecture Highlights

- **Platform**: 100% Native Android (Kotlin + Jetpack Compose)
- **Local Database**: Room + SQLite (Source of truth for all game sessions, patients, and reminders)
- **Offline ML**: On-device Decision Tree Classifier in Kotlin (JSON model) for adaptive difficulty (1–5)
- **Authentication**: Local 6-digit PIN for Caregiver/Doctor. **No PIN for Patients** (direct photo tap). **Zero OTP / SMS**.
- **Voice System**: Shared horizontal layer with offline text-to-speech and local Indic language packs (Assamese, English).

---

## 🎮 The Six Games

| # | Game | Clinical Domain | Description |
|---|---|---|---|
| **1** | **Family Trivia** (`পৰিয়ালৰ স্মৃতি`) | Memory | Personal family photos & relationship recognition from Room. |
| **2** | **Voice Cue Card** (`কণ্ঠ আৰু ছবি`) | Attention | Spoken audio cues with visual cards and touch fallback. |
| **3** | **Daily Sequencing** (`দৈনন্দিন ক্ৰম`) | Executive Function | Familiar routines (making Assam tea, morning routine). |
| **4** | **Categorisation** (`শ্ৰেণীবিভাজন`) | Categorical Thinking | Sorting fruits, vegetables, animals, and traditional wear. |
| **5** | **Village Market** (`গাঁওৰ বজাৰ`) | Working Memory | Shopping list recall with authentic NER goods. |
| **6** | **Pattern Recognition** (`আৰ্হি চিনাক্তকৰণ`) | Visuospatial Speed | High-contrast visual and color sequence prediction. |

---

## 📂 Project Structure

```text
SIH26003/
├── app/
│   ├── src/main/java/com/sih26003/smritisetu/
│   │   ├── feature/games/    # Common framework + 6 native games
│   │   ├── feature/auth/     # Local PIN & photo selection
│   │   ├── feature/patient/  # Large-target patient dashboard
│   │   ├── feature/caregiver/# Profile & doctor access code management
│   │   ├── feature/doctor/   # Longitudinal game signal review
│   │   ├── feature/reminders/# Exact offline AlarmManager scheduling
│   │   ├── data/local/       # Room database, 7 entities, 7 DAOs
│   │   ├── ml/               # On-device Decision Tree engine
│   │   └── voice/            # Voice prompts & language packs
│   └── src/main/assets/      # Local ML tree JSON & language packs
├── assets/                   # Master ML model & language packs
├── docs/                     # Architecture, Room schema, game specs, offline checklist
└── scripts/                  # train_decision_tree.py
```

---

## ⚡ Quick Start Commands

```bash
# 1. Generate & export Decision Tree model to JSON
python scripts/train_decision_tree.py

# 2. Build Android Debug APK
./gradlew assembleDebug

# 3. Run unit tests
./gradlew testDebugUnitTest
```
