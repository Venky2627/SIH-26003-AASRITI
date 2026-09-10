# AASRITI (SIH-26003) — 16-Step SIH Demonstration Script

> **Objective**: A repeatable, bulletproof 5-to-7 minute demonstration designed for Smart India Hackathon evaluators and jury members, proving 100% offline functionality, multi-role workflows, cultural grounding, and explainable AI.

---

## 🎬 Pre-Flight Demonstration Setup
1. **Device**: Low-to-mid range Android phone or tablet (API 24+).
2. **Network**: **AIRPLANE MODE ON** (Wi-Fi = OFF, Mobile Data = OFF).
3. **Audio**: Media volume at 80% to demonstrate Assamese/English speech prompts.

---

## 📋 The 16-Step Walkthrough

### Act I: Caregiver Setup & Personalization (Offline)
1. **Launch App**: Open AASRITI. Instant launch directly from local SQLite database (zero loading spinners or network timeouts).
2. **Caregiver Authentication**: Select **Caregiver Mode** $\rightarrow$ Enter 6-digit local PIN (`123456`) $\rightarrow$ SHA-256 authenticates offline in $<5\text{ms}$.
3. **Patient Profile**: Open Patient Profile (`AS-KAM-0042`, 68-year-old female, Kamrup Rural, Assam).
4. **Personalization**: Caregiver adds a family member (Photo: Son *Rupam*, Relationship: *Son*). Persists to Room `relationships` table.
5. **Offline Reminder**: Caregiver schedules 8:00 AM Blood Pressure Medicine reminder. Native Android `AlarmManager` registers the exact alarm.

### Act II: Zero-PIN Patient Experience & Cognitive Gaming (Offline)
6. **Patient Role Switch**: Return to Home $\rightarrow$ Switch to **Patient Mode**.
7. **Zero-PIN Entry**: Patient sees their familiar photograph on a large high-contrast card $\rightarrow$ Taps photo $\rightarrow$ Enters immediately without PIN or password barrier.
8. **Game 1 (Family Trivia)**: Patient selects Family Trivia $\rightarrow$ Spoken Assamese prompt plays at $0.85\times$ speed: *"এইজন কোন হয় চিনি পাইছেনে?"* $\rightarrow$ Rupam's real photo loads $\rightarrow$ Patient taps *"Son"*.
9. **Physical Metric Collection**: App captures reaction time ($2400\text{ms}$), tap accuracy ($1.0$), and hesitation ($1$ gap).
10. **Explainable Adaptive ML**: On-device Decision Tree evaluates metrics locally in $<1\text{ms}$ $\rightarrow$ Recommends next difficulty level: $2 \rightarrow 3$.
11. **Cultural Sequencing**: Patient plays Game 3 (Making Assam Milk Tea) $\rightarrow$ Arranges 3 culturally familiar steps (Boil water $\rightarrow$ Add tea leaves $\rightarrow$ Add milk).

### Act III: Caregiver Triage & ASHA Field Workflow (Offline)
12. **Quick Log (<30s)**: Caregiver re-enters $\rightarrow$ Opens Quick Log $\rightarrow$ Logs a minor stumble near bedside (Type: *Fall*, Severity: *Watch*). Saves in Room.
13. **Priority Engine**: Caregiver Dashboard refreshes $\rightarrow$ **Today's Priority** dynamically evaluates Room logs and flags:
    - 🔴 *Priority 1: Check bedside safety following morning fall event.*
    - 🟡 *Priority 2: Morning Blood Pressure medicine taken at 8:00 AM.*
14. **ASHA Worker Flow**: Switch to ASHA Mode $\rightarrow$ ASHA worker switches between multiple village patients on the same device $\rightarrow$ Verifies offline health register.

### Act IV: Doctor Longitudinal Review & Cloud Sync
15. **Doctor Portal**: Doctor enters 6-digit access code $\rightarrow$ Views 7/30/90 day longitudinal charts (Reaction time trend curve, error frequency, reminder adherence). Sees neutral functional signals: *"Consider clinical review for fall risk"* (Zero dementia diagnosis claim).
16. **Live Network Reconnect**: Disable Airplane Mode (Wi-Fi ON) $\rightarrow$ `ConnectivityObserver` detects network $\rightarrow$ `SyncQueueProcessor` automatically flushes pending records to Firebase Firestore in the background without freezing the UI.
