# Contributor Directive: Krishna

* **Role**: Lead Game Framework & Voice Engineer (Common Game Framework, Games 1–3, Shared Voice Subsystem)
* **Designated Working Branch**: `feature/krishna/<task-name>`
* **Default Peer Reviewer**: Bhavya
* **Primary Scope**:
  - Authoritative `BaseGameEngine` round lifecycle & `PerformanceCollector` (`CommonGameFramework.kt`)
  - Game 1: Family Trivia (`feature/games/familytrivia/`)
  - Game 2: Voice Cue Card (`feature/games/voicecuecard/`)
  - Game 3: Daily Sequencing (`feature/games/sequencing/`)
  - Shared audio/TTS service (`voice/VoiceManager.kt`, offline speech packs in `assets/audio/`)
* **Primary Directories**:
  - `app/src/main/java/com/sih26003/smritisetu/feature/games/framework/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/games/familytrivia/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/games/voicecuecard/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/games/sequencing/`
  - `app/src/main/java/com/sih26003/smritisetu/voice/`
  - `assets/audio/`
* **Protected Files Protocol**: Modifying `CommonGameFramework.kt` requires 2 approvals (Krishna + Bhavya).
