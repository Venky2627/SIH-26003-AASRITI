# SmritiSetu (SIH26003) — Locked Architecture Specification

> **Platform**: Native Android (Kotlin + Jetpack Compose)  
> **Core Principle**: LOCAL FIRST, CLOUD SECOND ("ROOM IS KING. FIREBASE IS THE MESSENGER.")  
> **ML Engine**: Scikit-Learn Decision Tree exported to JSON for on-device local inference  
> **Target Hardware**: Low-end Android devices in rural North Eastern Region (NER) of India

---

## 🏛️ 1. Locked Architectural Directives

1. **Native Android First**: Built in Kotlin with Jetpack Compose. No React Native, no Expo, no hybrid web views.
2. **Room is the Single Source of Truth**: All patient records, relationships, game performance signals, and offline reminders are written directly to local SQLite via Room before any sync is attempted.
3. **Firebase is Only a Secondary Messenger**: Cloud sync is entirely asynchronous and non-blocking. If offline, the app executes with 100% full functionality.
4. **Authentication Architecture**:
   - **Caregiver**: 6-digit local PIN hashed with SHA-256 in Room.
   - **Doctor**: 6-digit local PIN or 6-digit Doctor Access Code.
   - **Patient**: **NO PIN**. Direct photo/card tap access to eliminate cognitive friction.
   - **NO OTP**: All SMS/OTP architectures have been permanently removed.
5. **On-Device Decision Tree Inference**: Adaptive game difficulty (levels 1–5) is evaluated using a lightweight JSON-based Decision Tree runtime in Kotlin (`DecisionTreeEngine.kt`). Zero cloud inference.
6. **Voice as a Shared Application Layer**: Voice guidance is a shared horizontal service across all screens and games, **NOT** a seventh game.

---

## 🎮 2. The Six Playable Games

All six games participate in the identical `BaseGameEngine` lifecycle:
`GAME SELECT` $\rightarrow$ `LOAD PATIENT SETTINGS` $\rightarrow$ `VOICE + VISUAL INSTRUCTIONS` $\rightarrow$ `GAMEPLAY` $\rightarrow$ `COLLECT PERFORMANCE METRICS` $\rightarrow$ `RUN ADAPTIVE DIFFICULTY` $\rightarrow$ `SAVE RESULT TO ROOM` $\rightarrow$ `FEEDBACK` $\rightarrow$ `NEXT ROUND`.

1. **Family Trivia** (Memory & Personal Identity): Personalised family member photographs and names.
2. **Voice Cue Card** (Memory & Attention): Audio cues with visual touch cards and fallback.
3. **Sequencing** (Memory & Executive Function): Daily activities like making Assam milk tea.
4. **Categorisation** (Attention & Executive Function): Grouping fruits, vegetables, and cultural items.
5. **Village Market** (Working Memory & Attention): Shopping list recall with culturally authentic NER items.
6. **Pattern Recognition** (Visuospatial Processing & Speed): Color and shape sequence completion.

---

## 🔒 3. Healthcare Ethics & DPDA 2023 Compliance
- Game metrics measure **game interaction speed, accuracy, and hesitation**, NEVER dementia diagnosis or clinical progression.
- Raw audio is never permanently stored on disk.
- Patient identity uses privacy pseudonym codes (e.g. `AS-KAM-0042`).
