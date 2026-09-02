# SmritiSetu (SIH26003) — AI Assistant Context & Developer Directive

> This document serves as the primary system context for AI pair-programming assistants (including Claude, Gemini, and Copilot) operating on the SmritiSetu repository.

---

## 🏛️ Project Identity & Mission
* **Project Code**: SIH26003
* **Repository**: [https://github.com/TeamNameSIH/AI-Based-Cognitive-Gaming-Platform-for-Elderly-Dementia-Patients-in-NER](https://github.com/TeamNameSIH/AI-Based-Cognitive-Gaming-Platform-for-Elderly-Dementia-Patients-in-NER)
* **Problem Statement**: AI-Based Cognitive Gaming and Memory Assistance Platform for Elderly Dementia Patients in North Eastern Region (NER) of India.
* **Sponsoring Ministry**: Ministry of Development of North Eastern Region (MDoNER), Government of India.
* **Target Demographics**: Elderly individuals aged 60+ exhibiting Mild Cognitive Impairment (MCI) and early dementia in rural Assam, Manipur, Mizoram, and Nagaland. Caregivers (family members).

---

## 🛑 Strict Engineering Directives for AI Assistants

### 1. Offline-First is Absolute Law
* NEVER write code that assumes an active internet connection.
* NEVER suggest third-party cloud speech APIs (e.g., Google Speech-to-Text API, AWS Transcribe, OpenAI Whisper cloud) for the core user loop.
* ALL speech inference must use local **ONNX Runtime Mobile** with the quantized **IndicConformer** model.
* ALL game state and patient metrics must be written immediately to **local SQLite (SQLCipher)** before any background sync queueing is attempted.

### 2. Strict Healthcare Privacy & DPDA 2023
* NEVER save raw microphone audio files (`.wav`, `.mp3`) to disk storage.
* Audio streams must be processed directly from volatile memory and explicitly zeroed out after inference.
* Always enforce caregiver consent validation before unlocking assessment flows.

### 3. Elderly & Dementia Accessibility (WCAG 2.2 AAA)
* All interactive touch targets must be at least `64x64 dp`.
* Contrast ratios between text and background must exceed `7:1`.
* NEVER use complex navigation gestures (e.g., pinch-to-zoom, horizontal carousels, complex swipe gestures). Use large, explicit buttons with both visual icons and localized text.
* Provide audio feedback prompts for all key transitions in Assamese (`as`), Manipuri (`mn`), Bodo (`br`), Hindi (`hi`), and English (`en`).

### 4. Codebase Patterns
* **Mobile Runtime**: Expo SDK 50+ / React Native 0.73+ with TypeScript in strict mode.
* **State Management**: Redux Toolkit (RTK) with offline-first slicing.
* **Database**: `expo-sqlite` with SQLCipher encryption pragmas.
* **Backend**: FastAPI with async SQLAlchemy and PostgreSQL (for opportunistic sync only).

---

## 📂 Repository Directory Layout Cheat Sheet

```
├── mobile-app/              # React Native Expo Client
│   ├── src/
│   │   ├── features/games/  # Speed Match, Story Weaver, Picture Naming
│   │   ├── database/        # SQLite migrations, SQLCipher schema, repositories
│   │   ├── services/ai/     # ONNX Runtime IndicConformer ASR engine
│   │   ├── store/           # Redux Toolkit slices (offline sync queue, patient)
│   │   └── theme/           # High-contrast accessibility themes & typography
├── backend/                 # FastAPI Sync Gateway (Python 3.11)
├── infrastructure/          # Docker Compose, PostgreSQL configs
├── docs/                    # Architectural specs, clinical evidence, risk registers
└── .github/                 # Workflows, issue templates, PR checklists
```

---

## ⚡ Routine Terminal Commands

```bash
# Mobile Dev
cd mobile-app && npm install
npx expo start --clear
npx expo run:android

# Run Tests
npm test
npm run lint

# Backend Dev
cd backend && uvicorn app.main:app --reload --port 8000
```
