# AASRITI (SIH-26003) — Problem Statement Traceability Matrix

> **Official SIH Problem Statement**: SIH-26003 — AI-Based Cognitive Gaming and Memory Assistance Platform for Elderly Dementia Patients in the North Eastern Region (NER)  
> **Sponsoring Authority**: Ministry of Development of North Eastern Region (MDoNER), Government of India

---

## 📋 Comprehensive PS Traceability Mapping

| PS Clause & Requirement | Architectural Response | Code Location & Subsystem | Responsible Contributor |
| :--- | :--- | :--- | :--- |
| **Elderly cognitive gaming for dementia patients** | 6 standardized games on a unified `CommonGameFramework` with non-punitive audio feedback. | `feature/games/framework/`, `feature/games/` | Krishna & Bhavya |
| **Personalized memory assistance** | Family Trivia & Memory Album consuming real family photos and voice recordings. | `feature/games/familytrivia/`, `feature/memoryalbum/`, `RelationshipEntity` | Jasleen & Shravani |
| **North Eastern Region (NER) cultural adaptation** | Cultural Theme Engine with authentic regional objects, foods, and markets (Assam, Manipur, Meghalaya). | `cultural/ThemePack.kt`, `assets/cultural/` | Bhavya |
| **Low-resource rural connectivity** | 100% offline autonomy via local SQLite Room database; secondary background sync queue. | `data/local/database/`, `data/local/entity/SyncQueueEntity` | Jasleen & Venkatesh |
| **Adaptive game difficulty** | On-device Decision Tree trained in Python, exported to JSON, evaluated in Kotlin in $<1\text{ms}$. | `ml/inference/DecisionTreeEngine.kt`, `scripts/train_decision_tree.py` | Venkatesh |
| **Low digital literacy & visual impairments** | WCAG 2.2 AAA accessibility ($\ge 60\times 60\text{ dp}$ targets, $>7:1$ cataract contrast, zero PIN for patients). | `feature/patient/`, `core/ui/theme/`, `RoleAndModeSelectScreen.kt` | Bhavya |
| **Local language voice assistance** | Shared offline TextToSpeech ($0.85\times$ elderly rate) + Assamese (`as`) and English (`en`) prompt packs. | `voice/playback/VoicePromptManager.kt`, `assets/audio/` | Krishna |
| **Caregiver triage & support** | Deterministic Today's Priority Engine, $<30\text{s}$ Quick Log, and Emergency SOS toolkit. | `engine/priority/PriorityEngine.kt`, `feature/caregiver/` | Shravani |
| **Community Health Worker (ASHA) empowerment** | Dedicated ASHA workflow for shared-device multi-patient switching and batch offline registers. | `feature/asha/AshaDashboardScreen.kt` | Shravani |
| **Clinician longitudinal monitoring** | Doctor portal with 7/30/90 day physical interaction trends and explainable referral prompts. | `feature/doctor/DoctorAccessScreen.kt`, `engine/trend/` | Kimaya |
| **Ethical & non-diagnostic safety** | Measures physical touch latency, accuracy, and hesitation; strictly zero automated dementia diagnosis. | `core/constants/SafetyConstants.kt`, `GameSessionEntity` | All 6 Members |
