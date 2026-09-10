# AASRITI — UI Screen Mapping & Integration Matrix
## `/ui-reference/SCREEN_MAPPING.md`

This document maps every screen from the team UI prototype (`ui-reference/prototype/`) to the production Android Kotlin/Compose codebase (`app/src/main/`).

### Screen Mapping Matrix

| Prototype Screen | Existing AASRITI Screen | User Role | Priority | Status | Integration Strategy |
|---|---|---|---|---|---|
| **Screen 1 — Open / Welcome** | `AuthScreens.kt: WelcomeScreen` | All / New User | **P0** | **ADAPT** | Native Compose landing with language toggle, "Saved Locally" reassurance pill, large Enter CTA, and direct role paths. |
| **Screen 2 — Cultural Theme** | `AuthScreens.kt` / Theme System | Patient / All | **P0** | **ADAPT** | Present Northeast cultural motif selection (Assam / Manipur / Meghalaya) and persist theme preference. |
| **Screen 3 — Role / Login** | `AuthScreens.kt: RoleAndModeSelectScreen` | All | **P0** | **REUSE / ADAPT** | Retain hardened constant-time PIN verify, SecureRandom codes, and direct photo tap for patient mode. |
| **Screen 4 — Language Selection** | `AuthScreens.kt: LanguageSelectScreen` | All | **P0** | **ADAPT** | Bilingual English / Assamese switch utilizing native string resources and voice prompt configuration. |
| **Screen 5 — Accessibility Config** | `AuthScreens.kt: AccessibilitySetupScreen` | Elder / Patient | **P0** | **ADAPT** | High-contrast Warm Ivory styling, touch targets >= 64dp, font scaling, and voice instructions toggle. |
| **Screen 6 — Informed Consent** | `AuthScreens.kt: ConsentScreen` | Elder / Proxy | **P0** | **ADAPT** | Plain-language non-punitive consent presentation adhering to DPDPA 2023 with clear withdraw options. |
| **Screen 7 — Patient Home** | `patient/PatientHomeScreen.kt` | Patient / Elder | **P0** | **ADAPT** | Very low density layout, large touch cards for 6 games, daily routine strip, Memory Garden, Care Circle, and SOS. |
| **Screen 8 — Family Trivia** | `games/familytrivia/FamilyTriviaGame.kt` | Patient | **P0** | **ADAPT** | **Flagship Golden Screen**. Retain `FamilyTriviaEngine`, real hesitation/latency metrics, `GameSessionEntity` Room persistence, and cross-session adaptive difficulty. |
| **Screen 9 — Voice Cue Card** | `games/voicecuecard/VoiceCueCardGame.kt` | Patient | **P0** | **REUSE / ADAPT** | Visual restyle to prototype parchment cards while retaining `VoiceCueCardEngine` and voice prompt manager. |
| **Screen 10 — Daily Sequencing** | `games/sequencing/SequencingGame.kt` | Patient | **P0** | **REUSE / ADAPT** | Restyle tactile sequencing cards to Warm Ivory/Deep Forest tokens; retain step validation and latency tracking. |
| **Screen 11 — Categorisation** | `games/categorisation/CategorisationGame.kt` | Patient | **P0** | **REUSE / ADAPT** | Restyle categorisation grid to prototype tactile styling; retain category match metrics and telemetry. |
| **Screen 12 — Village Market** | `games/villagemarket/VillageMarketGame.kt` | Patient | **P0** | **REUSE / ADAPT** | Restyle cultural marketplace tokens; preserve budget calculation, item selection, and error tracking. |
| **Screen 13 — Pattern Recognition** | `games/patternrecognition/PatternRecognitionGame.kt` | Patient | **P0** | **REUSE / ADAPT** | Restyle geometric pattern tiles; preserve difficulty step-up/ease logic in `BaseGameEngine`. |
| **Screen 14 — Shared Game Feedback** | `CommonGameFramework.kt` & Game Screens | Patient | **P0** | **ADAPT** | Post-round summary card showing real latency (ms), hesitation count (>3.5s), accuracy %, and next difficulty level. |
| **Screen 15 — Memory Album** | `memoryalbum/MemoryGardenScreens.kt` | Patient / Family | **P0** | **REUSE / ADAPT** | Reminiscence album cards with authentic Assam tea garden memories and voice narrative playback. |
| **Screen 16 — One-Touch SOS** | `patient/SosScreen.kt` | Patient | **P0** | **REUSE / ADAPT** | Urgent emergency screen with countdown confirmation and direct contact to assigned ASHA worker / Caregiver. |
| **Screen 17 — Reminders** | `reminders/RemindersScreen.kt` | Patient | **P0** | **REUSE / ADAPT** | Medicine, hydration, and daily activity reminder cards with one-tap acknowledgment. |
| **Screen 18 — Caregiver Dashboard** | `caregiver/CaregiverDashboardScreen.kt` | Caregiver / ASHA | **P0** | **ADAPT** | High-level care hub displaying active patient snapshot, dynamic `PriorityEngine` triage, quick log launcher, and doctor link. |
| **Screen 19 — Patient Switching** | `caregiver/CaregiverDashboardScreen.kt` | Caregiver / ASHA | **P0** | **ADAPT** | Patient switcher sheet binding to `DemoPatientConfig.PATIENT_ID` (`"aita_borah_01"`). |
| **Screen 20 — Quick Care Log** | `caregiver/CaregiverDashboardScreen.kt` | Caregiver | **P0** | **ADAPT** | Bottom sheet dialog recording Mood, Sleep, Medication, Appetite, Fall, and Notes into Room `CareLogEntity`. |
| **Screen 21 — Today Priority** | `caregiver/CaregiverDashboardScreen.kt` | Caregiver | **P0** | **ADAPT** | Dynamic priority card (WATCH / PRIORITY / NORMAL) evaluated in real-time from persisted care logs and sessions. |
| **Screen 22 — Reminders Management** | `reminders/RemindersScreen.kt` | Caregiver | **P1** | **REUSE / ADAPT** | Caregiver interface to create, edit, and schedule daily alarms via `ReminderScheduler`. |
| **Screen 23 — Memory / Media Upload** | `memoryalbum/MemoryGardenScreens.kt` | Caregiver | **P1** | **REUSE / ADAPT** | Interface for family caregivers to add photos and voice memos to the patient's Memory Garden. |
| **Screen 24 — Doctor Link** | `AuthScreens.kt: DoctorAccessScreen` | Caregiver | **P0** | **REUSE / ADAPT** | Generate and display secure 6-digit access code with 72-hour expiration window. |
| **Screen 25 — SOS Follow-up** | `patient/SosScreen.kt` | Caregiver / ASHA | **P1** | **REUSE / ADAPT** | Follow-up triage dialog to resolve or escalate SOS trigger with logged care notes. |
| **Screen 26a / 26b — ASHA Field Visit** | `asha/AshaDashboardScreen.kt` | ASHA Worker | **P0** | **ADAPT** | Community visit roster, household visit logging, offline indicator, and quick observation entry. |
| **Screen 27 — Doctor My Patients** | `doctor/DoctorAccessScreen.kt` | Doctor / Clinician | **P0** | **ADAPT** | Patient roster showing assigned patients, cognitive stage, and consultation readiness. |
| **Screen 28 — Doctor Patient Snapshot** | `doctor/DoctorAccessScreen.kt` | Doctor / Clinician | **P0** | **ADAPT** | Clinical dossier overview showing last visit delta, trends summary, caregiver notes, and export action. |
| **Screen 29 — Since Last Visit Summary** | `doctor/DoctorAccessScreen.kt` | Doctor / Clinician | **P1** | **ADAPT** | Comparative delta view showing session count, adherence rate, and notable incidents since previous review. |
| **Screen 30 — Longitudinal Interaction Trends** | `doctor/DoctorAccessScreen.kt` | Doctor / Clinician | **P0** | **ADAPT** | 7/30-day reaction time and hesitation curves computed live by `TrendEngine` from Room `GameSessionEntity` with honest empty state. |
| **Screen 31 — Clinical Assessment Record** | `doctor/DoctorAccessScreen.kt` | Doctor / Clinician | **P1** | **NEW COMPOSE** | Clinical observation records (e.g. HMSE / MoCA consultation entry) presented as non-diagnostic review records. |
| **Screen 32 — Medication & Caregiver Summary**| `doctor/DoctorAccessScreen.kt` | Doctor / Clinician | **P1** | **NEW COMPOSE** | Integrated summary of caregiver-logged medication doses, appetite observations, sleep patterns, and fall events. |
| **Screen 33 — Care Plan & Review Notes** | `doctor/DoctorAccessScreen.kt` | Doctor / Clinician | **P1** | **NEW COMPOSE** | Clinician review notes, management advice (Stable / Monitor / Earlier Review), and scheduled follow-up date. |
| **Screen 34 — Clinician Summary & PDF Export** | `doctor/DoctorAccessScreen.kt` / `PdfReportGenerator.kt` | Doctor / Clinician | **P0** | **NEW COMPOSE** | **Offline Native PDF Export**. Generates printable consultation summary dossier using Android `PdfDocument` from Room data. |

---

### Priority Summary
- **P0 Core Pipelines (18 Screens)**: Welcome, Theme, Role/Auth, Language, Accessibility, Consent, Patient Home, 6 Cognitive Games (Family Trivia Flagship), Game Feedback, Caregiver Dashboard, Patient Switching, Quick Care Log, Today Priority, Doctor Access, Doctor Patient Snapshot, Longitudinal Trends, PDF Summary Export.
- **P1 Caregiver & Clinician Enhancements (9 Screens)**: Reminders Management, Memory Upload, SOS Follow-up, ASHA Field Visit, Since Last Visit, Clinical Assessments, Medication Summary, Care Plan.
- **P2 Secondary Polish (8 Screens)**: Micro-animations, print spooler integration, advanced PDF graphics.
