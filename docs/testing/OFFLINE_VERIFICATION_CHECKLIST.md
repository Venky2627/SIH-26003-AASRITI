# AASRITI (SIH-26003) — Mandatory Offline Verification Checklist

## ✈️ 14-Step Offline Assurance Protocol

| Step | Action | Expected Behavior | Verification Status |
| :---: | :--- | :--- | :---: |
| **01** | Cold boot app in **Airplane Mode** (Wi-Fi OFF, Cellular OFF). | App launches cleanly into Role/Patient select in $<1500\text{ms}$. | ✅ PASS |
| **02** | Tap patient photo card (*বোপা - Grandfather*). | Patient enters Game Dashboard without PIN barrier or network error. | ✅ PASS |
| **03** | Launch **Game 1 (Family Trivia)** in Airplane Mode. | Personalised family photo/relationship loads from local Room DB. | ✅ PASS |
| **04** | Launch **Game 2 (Voice Cue Card)** in Airplane Mode. | Voice prompt plays from local language pack; visual fallback active. | ✅ PASS |
| **05** | Launch **Game 3 (Sequencing)** in Airplane Mode. | Daily tea-making routine renders and evaluates step sequence. | ✅ PASS |
| **06** | Launch **Game 4 (Categorisation)** in Airplane Mode. | Fruit/vegetable categories respond with large tactile feedback. | ✅ PASS |
| **07** | Launch **Game 5 (Village Market)** in Airplane Mode. | Local NER market shopping list stores in memory and tests recall. | ✅ PASS |
| **08** | Launch **Game 6 (Pattern Recognition)** in Airplane Mode. | Color & shape pattern evaluated with 0ms network latency. | ✅ PASS |
| **09** | On-device Decision Tree execution. | Evaluates signals locally and recommends difficulty (1-5) via JSON model. | ✅ PASS |
| **10** | Game Session Persistence. | `GameSessionEntity` written immediately to Room; enqueued in `sync_queue`. | ✅ PASS |
| **11** | Add Offline Reminder. | Medicine reminder saved to Room; exact offline AlarmManager registered. | ✅ PASS |
| **12** | Force Kill & Relaunch App in Airplane Mode. | All patients, scores, and reminders persist intact from SQLite. | ✅ PASS |
| **13** | Reconnect Network (Airplane Mode OFF). | `FirebaseSyncMessenger` detects connection and drains `sync_queue`. | ✅ PASS |
| **14** | Local data integrity check. | Local records remain completely untouched; no overwrite or data loss. | ✅ PASS |
