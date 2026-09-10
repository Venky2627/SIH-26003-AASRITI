# AASRITI Screen Flow Specification

This document is the authoritative navigation specification
for the AASRITI application.

The Stitch designs are the visual source of truth.

This document is the interaction and navigation source of truth.

Do not invent navigation paths that are not specified here.

This is a frontend-only prototype.

There is no backend at this stage.

Use mock/static/local state where necessary.

The goal is a fully clickable prototype for demonstration.

# AASRITI — Screen-to-Screen Connector Flow

## ONBOARDING / ACCESS

### Screen 1 — Open / Welcome
- Enter → Screen 2
- Language → Screen 4
- ASHA / Caregiver → Screen 3
- Doctor → Screen 3

### Screen 2 — Cultural Theme
- Assam / Manipur / Meghalaya → Screen 4
- Continue → Screen 4

### Screen 3 — Role / Login
- Patient → Screen 4
- Caregiver / ASHA → Screen 18
- Doctor → Screen 27
- Back → Screen 1

### Screen 4 — Language
- Assamese / English / Continue → Screen 5

### Screen 5 — Accessibility
- Continue / Save → Screen 6
- Back → Screen 4

### Screen 6 — Consent
- Agree / Continue → Screen 7
- I don't want → Screen 1
- Change / Withdraw → Consent/settings, then Screen 1 or disable patient access
- Back → Screen 5


## PATIENT

### Screen 7 — Patient Home
- Family Trivia → Screen 8
- Voice Cue → Screen 9
- Sequencing → Screen 10
- Categorisation → Screen 11
- Village Market → Screen 12
- Pattern / Object → Screen 13
- Memory / Family / Photos → Screen 15
- Reminder → Screen 17
- Today Priority → Screen 21
- SOS → Screen 16
- Home → Screen 7

### Screen 8 — Family Trivia
- Play / Answer → Stay on Screen 8
- Correct / Continue → Screen 14
- Try Again → Screen 8
- Finish → Screen 14
- Home → Screen 7

### Screen 9 — Voice Cue
- Play → Stay on Screen 9
- Answer / Continue → Screen 14
- Repeat → Stay on Screen 9
- Home → Screen 7

### Screen 10 — Sequencing
- Select → Stay on Screen 10
- Check / Continue → Screen 14
- Try Again → Screen 10
- Home → Screen 7

### Screen 11 — Categorisation
- Select → Stay on Screen 11
- Check / Continue → Screen 14
- Try Again → Screen 11
- Home → Screen 7

### Screen 12 — Village Market
- Select / Match → Stay on Screen 12
- Check / Continue → Screen 14
- Try Again → Screen 12
- Home → Screen 7

### Screen 13 — Pattern / Object
- Same / Rotated / Mirror → Screen 14
- Try Again → Screen 13
- Home → Screen 7

### Screen 14 — Shared Feedback
- Correct / Continue → Next appropriate game / difficulty OR Screen 7
- Next → Next activity / difficulty
- Play Again → Current game
- Save / Finish → Screen 7
- Home → Screen 7

### Screen 15 — Memory Album
- Photo / Memory → Detail within Screen 15
- Voice Memory → Playback / detail within Screen 15
- Save → Stay on Screen 15
- Back / Home → Screen 7

### Screen 16 — SOS
- Confirm / Call ASHA → Screen 25
- Cancel → Screen 7
- Call Assigned ASHA → Phone intent, then Screen 25

### Screen 17 — Reminder
Categories:
- Medicine
- Hydration
- Daily Activity
- Medical Appointment

Actions:
- Taken / Done / Acknowledge → Save, then Screen 7
- Missed → Save, then Screen 7
- Back → Screen 7


## CAREGIVER / ASHA

### Screen 18 — Caregiver / ASHA Dashboard
- Patient → Screen 19
- Today Priority → Screen 21
- Quick Log → Screen 20
- Reminders → Screen 22
- Memory / Media → Screen 23
- Trends → Screen 30
- Doctor Link → Screen 24
- ASHA Field Visit → Screen 26
- Sync → Sync state, then Dashboard
- Back / Logout → Screen 3

### Screen 19 — Patient Switching
- Photo / Name / Open → Screen 18 with selected patient
- Enter Patient Mode → Screen 7
- Back → Screen 18

### Screen 20 — Quick Log
Categories:
- Mood
- Sleep
- Medication
- Activity
- Incident
- Assistance

Actions:
- Select → Stay on Screen 20
- Save → Screen 21
- Back → Screen 18

### Screen 21 — Today Priority
- Priority → Detail / Screen 20 if care logging is required
- Review → Stay on Screen 21
- Done / Acknowledge → Screen 18
- Back → Screen 18

### Screen 22 — Reminders
- Medicine / Hydration / Daily Activity / Medical Appointment → Detail / Edit within Screen 22
- Add Reminder → Create reminder within Screen 22
- Save → Screen 18
- Back → Screen 18

### Screen 23 — Memory / Media
- Add Photo → Add photo within Screen 23
- Add Voice → Add voice within Screen 23
- Save to Memory Album → Screen 15
- Back → Screen 18

### Screen 24 — Doctor Link
- Add / Link Doctor → Stay on Screen 24
- Verify ID / QR → Verification state within Screen 24
- Grant Access → Verified / access state within Screen 24
- Revoke → Access revoked state within Screen 24
- Doctor Summary / PDF → Screen 34
- Back → Screen 18

### Screen 25 — SOS Follow-up
- Call ASHA → Phone intent, then Screen 25
- Resolved → Save → Screen 18 or Screen 26
- Follow-up Needed → Save → Screen 21
- Save / Confirm → Screen 18
- Back → Screen 18

### Screen 26 — ASHA Field Visit
- Select Patient → Screen 19
- Quick Log → Screen 20
- Priority → Screen 21
- Sync → Sync state, then Screen 26
- SOS / Contact → Screen 25
- Back → Screen 18


## DOCTOR

### Screen 27 — My Patients
- Patient Name / Photo / Select → Screen 28
- Back / Logout → Screen 3

### Screen 28 — Patient Snapshot
- Since Last Visit → Screen 29
- Trends → Screen 30
- Clinical Assessments → Screen 31
- Medication / Caregiver → Screen 32
- Care Plan / Follow-up → Screen 33
- Export PDF → Screen 34
- Back → Screen 27

### Screen 29 — Since Last Visit
- Back → Screen 28
- Trends → Screen 30
- Assessment → Screen 31
- Medication / Caregiver → Screen 32
- Care Plan → Screen 33

### Screen 30 — Trends
- 7 / 30 / 90 days → Stay on Screen 30
- Assessment → Screen 31
- Back → Screen 28

### Screen 31 — Clinical Assessments
- Record / Detail → Stay on Screen 31
- Add / Record → Stay on Screen 31
- Save → Stay on Screen 31
- Back → Screen 28
- Export Summary → Screen 34

### Screen 32 — Medication / Caregiver Summary
- Medication Detail → Detail within Screen 32
- Missed Dose History → Detail within Screen 32
- Caregiver Observations → Detail within Screen 32
- Back → Screen 28
- Care Plan → Screen 33

### Screen 33 — Care Plan
- Stable / Monitor / Earlier Review → Set state within Screen 33
- Save → Screen 28
- Follow-up Date → Set date within Screen 33
- Export → Screen 34
- Back → Screen 28

### Screen 34 — Clinician Summary / PDF
- Generate / Export PDF → Generate/export prototype PDF state
- Share → Authorized sharing flow
- Back to Patient → Screen 28
- Back to My Patients → Screen 27
- Done → Screen 28


## PROTOTYPE RULES

- This is a frontend-only prototype.
- No backend is required.
- Use mock/static/local data.
- All specified buttons and interactive elements should be clickable.
- Navigation must follow this document.
- Do not invent additional screens.
- Do not invent navigation paths.
- Where a real-world action is impossible without a backend, simulate the appropriate UI state.
- Phone calls may use a simulated call state or phone intent where appropriate.
- PDF generation/export may use a simulated export state for the demonstration.
- Data changes such as reminders, logs, access permissions and game completion should be represented using local/mock state.