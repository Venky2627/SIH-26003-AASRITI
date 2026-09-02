# SmritiSetu (SIH26003) — Master Game Design & UX Technical Specification (Screens 1 to 7)

> **Document Status**: Production Complete & Implementation Ready  
> **Author**: SIH26003-UX-Architect  
> **Target Audience**: 6 Core Mobile, AI, and Backend Developers  
> **Target Region**: North Eastern Region (NER) of India (Assam, Manipur, Mizoram, Nagaland)  
> **Accessibility Standard**: WCAG 2.2 AAA & W3C Cognitive Accessibility (COGA)

---

## 📑 TABLE OF CONTENTS
1. [Screen 1: Splash Screen (`SplashScreen.tsx`)](SCREEN_01_SPLASH.md)
2. [Screen 2: Role Selection Screen (`RoleSelectionScreen.tsx`)](SCREEN_02_ROLE_SELECTION.md)
3. [Screen 3: Mobile Number Entry Screen (`MobileEntryScreen.tsx`)](SCREEN_03_MOBILE_ENTRY.md)
4. [Screen 4: OTP Verification Screen (`OTPVerificationScreen.tsx`)](SCREEN_04_OTP_VERIFICATION.md)
5. [Screen 5: Caregiver Profile Setup Screen (`CaregiverProfileScreen.tsx`)](SCREEN_05_CAREGIVER_PROFILE.md)
6. [Screen 6: Patient Setup & Language Screen (`PatientSetupScreen.tsx`)](SCREEN_06_PATIENT_SETUP.md)
7. [Screen 7: Accessibility Settings Screen (`AccessibilityScreen.tsx`)](SCREEN_07_ACCESSIBILITY.md)
8. [Cross-Cutting Concern A: Navigation Architecture](NAVIGATION_ARCHITECTURE.md)
9. [Cross-Cutting Concern B: Theme System (Design Tokens)](THEME_SYSTEM.md)
10. [Cross-Cutting Concern C: Authentication State Machine](AUTH_STATE_MACHINE.md)
11. [Cross-Cutting Concern D: Caregiver/Patient Linking Logic](CAREGIVER_PATIENT_LINKING.md)
12. [Cross-Cutting Concern E: Accessibility Settings Persistence](ACCESSIBILITY_PERSISTENCE.md)
13. [Implementation Roadmap (Sprint Schedule)](TIMELINE_MILESTONES.md)

---

## 🏆 EXECUTIVE SUMMARY: THE 7 CORE SCREENS AT A GLANCE

| Screen # | Screen Name | Primary Target User | Key Accessibility Feature | Offline Behavior |
| :---: | :--- | :--- | :--- | :--- |
| **01** | **Splash Screen** | All (Patient/Caregiver) | Prefers-reduced-motion check; TalkBack announcement. | 100% offline bootstrap of SQLCipher & ONNX session. |
| **02** | **Role Selection** | All | Spoken audio instructions in Assamese; Miller's Law (3 choices). | Zero network sockets; writes choice to local storage. |
| **03** | **Mobile Entry** | Caregiver / Doctor | 72px hit targets; non-agitating amber error banner; phone formatting. | Detects offline mode and offers local 6-digit PIN login. |
| **04** | **OTP Verification** | Caregiver / Doctor | 10-minute timer for slow motor typing; large 56x64px digit boxes. | Verifies local PBKDF2 PIN hash if offline. |
| **05** | **Caregiver Profile** | Family Caregiver | Large touch options; DPDA 2023 proxy consent notification. | Saves profile to local SQLite immediately; syncs later. |
| **06** | **Patient Setup** | Caregiver / Patient | Native regional languages (as, mn, br); privacy pseudonym codes. | Links to caregiver in SQLite; sets active regional ASR model. |
| **07** | **Accessibility** | All Users | Cataract high-contrast switch; tremor touch filter (80ms dwell). | Persists to AsyncStorage; updates React Context globally. |

---

## 🛠️ TEAM RACI IMPLEMENTATION MATRIX (SPRINT AUG 29 - SEPT 5)

| Screen / Deliverable | Responsible Developer (R) | Accountable Lead (A) | Verification Gate |
| :--- | :--- | :--- | :--- |
| **Screen 1 (Splash)** | **Mobile Dev Lead** (`@lead-architect`) | `@lead-architect` | Android cold boot $<1500\text{ms}$ in Airplane Mode. |
| **Screen 2 (Role Selection)** | **Clinical UX / QA** (`@clinical-qa`) | `@lead-architect` | Voice prompt plays in Assamese, Manipuri, and Bodo. |
| **Screen 3 (Mobile Entry)** | **Mobile Core** (`@mobile-core`) | `@lead-architect` | Phone numbers formatted cleanly with $+91$; no raw PII in logs. |
| **Screen 4 (OTP Verification)**| **Mobile Core** (`@mobile-core`) | `@lead-architect` | 10-min countdown timer; auto-focus; local PIN fallback. |
| **Screen 5 (Caregiver Setup)** | **Mobile Games** (`@mobile-games`) | `@mobile-core` | Inserts record into `caregivers` SQLite table atomically. |
| **Screen 6 (Patient Setup)** | **AI / Speech Engr** (`@ai-edge`) | `@mobile-core` | Regional language switches ASR dictionary and UI labels. |
| **Screen 7 (Accessibility)** | **Clinical UX / QA** (`@clinical-qa`) | `@clinical-qa` | Tremor damping absorbs rapid double taps ($<600\text{ms}$). |
| **Theme System** | **Clinical UX / QA** (`@clinical-qa`) | `@lead-architect` | Contrast ratios strictly exceed $7:1$ across all cards. |
| **Navigation Graph** | **Mobile Dev Lead** (`@lead-architect`) | `@lead-architect` | Linear stack prevents accidental back-button traps. |
