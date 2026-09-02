# Contributor Directive: Krishna

* **Role**: Equal Contributor (Voice Cue Card + Voice Experience)
* **Designated Working Branch**: `feature/krishna/voice-cue-card`
* **Default Peer Reviewer**: Shravani
* **Primary Scope**:
  - Game 2: Voice Cue Card (Levels 1 to 5)
  - Shared `VoicePromptManager.kt` (Offline TTS, speech rate calibration $0.85\times$)
  - Local language pack JSONs (`as_prompts.json`, `en_prompts.json`)
  - 5-second voice fallback listener and touch redundancy
* **Primary Directories**:
  - `app/src/main/java/com/sih26003/smritisetu/feature/games/voicecuecard/`
  - `app/src/main/java/com/sih26003/smritisetu/voice/`
  - `app/src/main/assets/language-packs/`
  - `assets/language-packs/`
* **Shared-File Protocol**: If changing shared voice APIs consumed by other games, coordinate with Venkatesh and Jasleen.
