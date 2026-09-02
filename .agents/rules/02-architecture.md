# Rule 02: Locked Architecture Specification

1. **Android Native**: Kotlin + Jetpack Compose with Material 3. Target SDK 34, Min SDK 24.
2. **Room is King**:
   - Every read and write begins locally.
   - All mutations write synchronously to SQLite and enqueue a record to `sync_queue`.
3. **Firebase is the Messenger**:
   - Secondary cloud backup only.
   - Zero gameplay blocks when offline.
   - Never delete local data after sync.
4. **On-Device Adaptive ML**:
   - Scikit-learn `DecisionTreeClassifier` trained in Python and exported to JSON (`assets/ml/decision_tree_difficulty.json`).
   - Evaluated by `DecisionTreeEngine.kt` in Kotlin CPU runtime ($<1\text{ms}$).
   - Recommends difficulty levels 1 to 5 based on accuracy, reaction time, errors, and hesitation.
5. **Shared Voice Service**:
   - `VoicePromptManager.kt` provides $0.85\times$ slow-paced TTS and pre-recorded prompts in Assamese (`as`) and English (`en`).
   - Voice is a shared utility across the app, **NOT** a seventh game.
