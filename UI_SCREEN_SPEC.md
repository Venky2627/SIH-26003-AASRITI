# AASRITI SCREEN SPECIFICATION ARCHITECTURE
## `/UI_SCREEN_SPEC.md` — Canonical Screen Design Contracts

> **PURPOSE**: Defines the rigid layout, hierarchy, allowed visual patterns, forbidden structures, and interaction expectations for every major screen in AASRITI.

---

## 🏛️ PART I: ONBOARDING & AUTHENTICATION FLOWS

---

### SCREEN 01: Welcome & Language Selection
* **Screen Name**: `SCREEN_ONBOARDING_WELCOME`
* **Purpose**: Greet the user with cultural warmth, establish voice comfort, and set the interface language.
* **Primary Role**: All roles (Patient, Caregiver, ASHA, Doctor).
* **Primary User Goal**: Understand what AASRITI is and select preferred language.
* **Primary Action**: Language selection card tap (Assamese, Manipuri, Khasi/Garo, English).
* **Secondary Actions**: Spoken audio preview button ("Listen to greeting").
* **Information Hierarchy**:
  1. AASRITI Brand Mark & Subtle regional textile motif.
  2. Warm Greeting in regional script: *"নমস্কাৰ / ꯈꯨꯔꯨꯝꯖꯔꯤ / Welcome"*.
  3. Simple prompt: *"Choose your language to begin."*
  4. Four oversized language selection cards ($\ge 72\text{dp}$ height).
  5. Primary "Continue" button (`Deep Northeast Forest` `#245C45`).
* **Allowed Patterns**: High-contrast language cards with native script and English phonetic subtitles; audio speaker icon.
* **Forbidden Patterns**: Long terms of service text walls, dark background themes, complex dropdown menus.
* **Accessibility**: Audio automatically plays localized welcome when card is focused. TalkBack announcements in selected language.

---

### SCREEN 02: Role Selection
* **Screen Name**: `SCREEN_ONBOARDING_ROLE_SELECT`
* **Purpose**: Route the user to their appropriate role-specific experience with zero friction.
* **Primary Role**: System Gatekeeper.
* **Primary User Goal**: Enter the correct interface for their daily need.
* **Primary Action**: Tap on one of the 4 clear role cards.
* **Secondary Actions**: "Help / What role am I?" button.
* **Information Hierarchy**:
  1. Header: *"Who is using AASRITI today?"*
  2. Large Card 1: **I am the Elder / Patient** (Features prominent avatar photo, warm terracotta accent, zero PIN requirement).
  3. Card 2: **I am a Family Caregiver** (Requires local 6-digit PIN).
  4. Card 3: **I am an ASHA / Health Worker** (Requires local PIN / Worker ID).
  5. Card 4: **I am a Doctor / Clinician** (Requires Doctor PIN & Access Code).
* **Allowed Patterns**: 4 oversized vertical tiles with distinct illustrative icons and clear single-sentence role descriptions.
* **Forbidden Patterns**: Complicated multi-step onboarding carousels, email/password login forms, phone OTP requests.

---

### SCREEN 03: Caregiver / Doctor PIN Authentication
* **Screen Name**: `SCREEN_AUTH_LOCAL_PIN`
* **Purpose**: Verify caregiver or clinician identity locally without requiring an internet connection.
* **Primary Role**: Caregiver, ASHA, Doctor (**NEVER Patient**).
* **Primary User Goal**: Enter the 6-digit local PIN to access care tools.
* **Primary Action**: Numeric keypad tap (0–9).
* **Secondary Actions**: "Forgot PIN (Caregiver Reset)", "Switch User".
* **Information Hierarchy**:
  1. Header: *"Enter Caregiver PIN"* (or *"Enter Doctor Access Code"*).
  2. 6 large circular PIN dot indicators (Soft Cream when empty, Deep Northeast Forest when filled).
  3. Oversized 3x4 numeric keypad with generous spacing ($\ge 64\text{dp}$ buttons).
* **Forbidden Patterns**: Masked password strings, alphanumeric keyboards, biometric popups that block offline use, SMS OTP gateways.
* **Accessibility**: Audio click tone on number entry; clear error announcement with gentle vibration on mismatch.

---

## 👵 PART II: PATIENT EXPERIENCE SCREENS (VERY LOW DENSITY)

---

### SCREEN 04: Patient Home (The Guided Companion)
* **Screen Name**: `SCREEN_PATIENT_HOME`
* **Purpose**: Provide calm temporal orientation and present the single most beneficial activity for the current moment.
* **Primary Role**: Patient / Elder.
* **Primary User Goal**: Feel safe, know what day/time it is, and engage with a pleasant activity.
* **Primary Action**: Tap the primary **"Today's Activity"** button (e.g. *"Play Flower Match"* or *"Look at Memories"*).
* **Secondary Actions**:
  - Check today's routine item (e.g. *"Morning Tea ✓"*).
  - Tap Family Circle quick-call card (e.g. *"Call Mira"*).
  - Emergency SOS pill (fixed top corner).
* **Information Hierarchy**:
  1. **Top Bar**: AASRITI brand mark, peaceful voice indicator, subtle SOS pill.
  2. **Greeting & Orientation Block**:
     - *"Namaskar, Aita."* (or patient's preferred family honorific).
     - *"Thursday Morning, Sunny."*
  3. **The Single Focal Activity Card** (Hero container, `Soft Cream` with `Deep Northeast Forest` CTA):
     - Activity illustration (e.g. Marigold/Lotus for Flower Match).
     - Title: *"Flower Match"*
     - Friendly subtitle: *"Let's match familiar blooms together."*
     - Giant Action Button: **[ START ACTIVITY ]** ($\ge 72\text{dp}$ height).
  4. **Today's Gentle Routine Strip**:
     - Max 2 visual tiles: *"Morning Medicine — Done ✓"* | *"Drink Water — Next"*.
  5. **Family Connection Tile**:
     - Large photo of loved one: *"Mira is home. Call daughter?"*
* **Allowed Patterns**: Warm Ivory background (`#FDFBF7`), single primary focal point, voice read-aloud button.
* **Forbidden Patterns**:
  - ❌ **ANY DASHBOARD PANELS OR KPI CARDS**.
  - ❌ Cognitive score percentages, streaks, or performance indicators.
  - ❌ Tiny icons without text labels, multi-tab bottom bars with more than 4 items.
  - ❌ Confusing notifications or clinical medication tables.
* **Accessibility**: Screen contents automatically read aloud if auto-voice is enabled in patient profile.

---

### SCREEN 05: Memory Garden (Reminiscence Album)
* **Screen Name**: `SCREEN_PATIENT_MEMORY_GARDEN`
* **Purpose**: Stimulate autobiographical memory through beloved family photos, familiar regional places, and family voice notes.
* **Primary Role**: Patient / Elder.
* **Primary User Goal**: Relive a joyful, familiar life moment without memory pressure.
* **Primary Action**: Tap the photo to hear the attached family voice recording.
* **Secondary Actions**:
  - *"Yes, I remember"* (Affirmative gentle tap).
  - *"Tell me more"* (Plays audio narration).
  - *"Next Memory"* (Right arrow button, $\ge 68\text{dp}$).
* **Information Hierarchy**:
  1. Top navigation: Large Back button + *"Memory Garden"*.
  2. Full-width framed photograph with warm heritage border (`Warm Stone` `#D6CBBB`).
  3. Memory Caption in large font (20sp): *"Bihu celebration with Grandson Rupam, 2022"*.
  4. Audio voice note controller (Pill button with Muga Gold pulse: *"Listen to Rupam"*).
  5. Gentle conversation prompts: *"Do you remember this day?"*
* **Allowed Patterns**: Handcrafted photo album aesthetic, soft rounded photo borders, audio playback animation.
* **Forbidden Patterns**: EXIF metadata, timestamp grids, camera file names (`IMG_2022.jpg`), complex tag clouds.

---

### SCREEN 06: Cognitive Game Arena (Universal Framework)
* **Screen Name**: `SCREEN_PATIENT_GAME_ARENA`
* **Purpose**: Host all 6 cognitive activities (Flower Match, Family Trivia, Voice Cue Card, Sequencing, Categorisation, Village Market).
* **Primary Role**: Patient / Elder.
* **Primary User Goal**: Complete a calm, engaging cognitive exercise without time panic.
* **Primary Action**: Tap a large visual choice card.
* **Secondary Actions**: Tap Voice Guidance button to replay instruction; tap "Pause / Rest".
* **Information Hierarchy**:
  1. Top Bar: Back button, Game Title, Gentle Step Dots (e.g. 3 calm circles, NOT "Question 3/10!").
  2. Audio Prompt Bar: Spoken instruction with speaker icon (*"Find the Joha rice bag"*).
  3. Game Interaction Arena:
     - 2 to 4 oversized visual choice cards ($\ge 88\text{dp}$ touch targets).
     - High-contrast illustrations with bold labels underneath.
  4. Supportive Feedback Area (Appears on tap).
* **Allowed Patterns**:
  - Warm encouraging feedback: *"Well done, Aita!"*, *"That's lovely."*, *"Let's try this one together."*
* **Forbidden Patterns**:
  - ❌ Countdown timers with ticking sounds or red bars.
  - ❌ Red cross marks or buzzer sounds on incorrect selection.
  - ❌ Words like *"Wrong!"*, *"Failed!"*, *"Incorrect!"*.
  - ❌ Complex drag-and-drop mechanics (tap-to-select is mandatory).

---

### SCREEN 07: SOS & Help Dispatch
* **Screen Name**: `SCREEN_PATIENT_SOS`
* **Purpose**: Provide instantaneous, panic-free emergency assistance and caregiver connection.
* **Primary Role**: Patient / Elder.
* **Primary User Goal**: Call for help immediately with a single touch.
* **Primary Action**: Tap giant **[ CALL CAREGIVER NOW ]** button (`Deep Cranberry` `#8B183F`).
* **Secondary Actions**: *"I am okay / Cancel"* button.
* **Information Hierarchy**:
  1. Clear, calming title: *"Do you need help?"*
  2. Subtitle: *"Pressing this calls Mira immediately."*
  3. Giant Call Button ($120\text{dp} \times 120\text{dp}$ circular target with telephone handset icon).
  4. Secondary Emergency Call: *"Call Ambulance (108)"*.
  5. Cancel button: *"I pressed by mistake"*.
* **Allowed Patterns**: Extreme visual contrast, direct telephony integration (`android.intent.action.CALL`).
* **Forbidden Patterns**: Hidden menus, confirmations requiring typing, small cancel links.

---

## 👨‍👩‍👧 PART III: CAREGIVER & FAMILY WORKFLOWS (LOW-MEDIUM DENSITY)

---

### SCREEN 08: Caregiver Dashboard & Today's Priority
* **Screen Name**: `SCREEN_CAREGIVER_DASHBOARD`
* **Purpose**: Provide family caregivers with an instant overview of their loved one's daily safety, routine adherence, and priority actions.
* **Primary Role**: Family Caregiver.
* **Primary User Goal**: Understand what happened today and take required care actions in $<60\text{s}$.
* **Primary Action**: Action on **Today's Priority Card** (e.g. *"Confirm evening blood pressure medicine"*).
* **Secondary Actions**:
  - Tap **"Quick Log"** floating action button.
  - Add new Memory to Memory Garden.
  - View detailed 7-day trend history.
* **Information Hierarchy**:
  1. Patient Status Header: Patient photo, Name, Current Mood/Activity badge, Local Sync status.
  2. **Today's Priority Card** (`Muga Gold` `#C88D34` left accent bar):
     - Highest urgency item (e.g. *"Missed 2:00 PM Hydration reminder"* or *"Morning Walk completed"*).
  3. **Daily Routine Progress Strip**:
     - Medicine: 2/3 Taken | Hydration: 4/5 Glasses | Cognitive Game: Played 1 session.
  4. **Quick Action Grid**:
     - [ Log Event ] | [ Add Memory ] | [ Manage Reminders ] | [ Call Loved One ].
  5. **Recent Observations Log**: Last 3 notes recorded by caregiver or visiting ASHA worker.
* **Allowed Patterns**: Progressive disclosure (summary card with "View details" chevron), clear sync status chip.
* **Forbidden Patterns**: Complex multi-tab hospital layouts, clinical diagnostic terminology.

---

### SCREEN 09: Quick Log (<30s Triage Entry)
* **Screen Name**: `SCREEN_CAREGIVER_QUICK_LOG`
* **Purpose**: Enable caregivers and ASHA workers to record an observation in under 30 seconds.
* **Primary Role**: Caregiver & ASHA.
* **Primary User Goal**: Log an incident or routine event rapidly without typing long essays.
* **Primary Action**: Tap event category tile $\rightarrow$ Tap severity chip $\rightarrow$ Tap "Save Log".
* **Information Hierarchy**:
  1. Header: *"Quick Care Log"* with current timestamp.
  2. Event Category Grid (6 large buttons):
     - [ Medication ] | [ Appetite ] | [ Sleep Quality ] | [ Fall / Stumble ] | [ Wandering / Confusion ] | [ Agitation ].
  3. Severity Selector: `Normal (Green)` | `Watch (Amber)` | `Urgent (Cranberry)`.
  4. Optional Voice Memo or 1-line text note.
  5. Primary "Save to Local Log" button.
* **Allowed Patterns**: Big single-tap selection tiles, offline immediate write to Room SQLite database.

---

## 🏥 PART IV: ASHA WORKER & DOCTOR WORKFLOWS (MEDIUM-HIGH DENSITY)

---

### SCREEN 10: ASHA Multi-Patient Community Roster
* **Screen Name**: `SCREEN_ASHA_PATIENT_ROSTER`
* **Purpose**: Empower rural ASHA community workers to manage 10–20 village elders on a single shared device.
* **Primary Role**: ASHA Worker.
* **Primary User Goal**: Find a village elder, open their profile, and log a home visit check-up.
* **Primary Action**: Tap patient row to open visit record.
* **Secondary Actions**: Search by pseudonym or village hamlet, filter by "High Priority / Watch".
* **Information Hierarchy**:
  1. Village Hamlet Header: *"Kamrup Rural — 14 Elders Registered"*.
  2. Priority Filter Chips: `All (14)` | `Needs Visit (3)` | `Recent Falls (1)`.
  3. Patient List Cards:
     - Avatar / Photo + Pseudonym (`AS-KAM-0042`).
     - Age, Primary Language, Village quarter.
     - Last visited date (e.g. *"Visited 2 days ago"*).
     - Priority status chip (`Normal` vs `Watch`).
  4. Floating "Register New Elder" button.
* **Allowed Patterns**: High-density list with clear separation, batch offline sync indicator.
* **Forbidden Patterns**: Confusing caregiver-only personal family albums on the multi-patient screen.

---

### SCREEN 11: Doctor Patient Snapshot & Longitudinal Signals
* **Screen Name**: `SCREEN_DOCTOR_PATIENT_SNAPSHOT`
* **Purpose**: Provide clinicians with objective, longitudinal functional trends to support clinical decision-making.
* **Primary Role**: Doctor / Clinician.
* **Primary User Goal**: Review 7/30/90-day functional signals without wading through raw sensor telemetry.
* **Primary Action**: Review trend summary and add clinical recommendation note.
* **Secondary Actions**: "Generate 1-Page PDF Clinical Summary", "Revoke Access".
* **Information Hierarchy**:
  1. Patient Demographics & Access Expiry banner (6-digit code validity).
  2. **Longitudinal Signal Cards (7 / 30 / 90 day toggle)**:
     - Reaction Time Trend (e.g. *"Average 2450ms — Stable over 30 days"*).
     - Hesitation Gaps (>3500ms pauses: *"Low hesitation detected"*).
     - Routine Adherence (*"88% medication adherence"*).
  3. **Explainable Triage Signals**:
     - *"Flag: 1 bedside fall reported on Sep 1 by Caregiver."*
     - *"Recommendation Prompt: Consider balance & gait evaluation."*
  4. **Doctor Clinical Notes**: Free-text entry for prescription or therapy recommendations.
* **Forbidden Patterns**:
  - ❌ **ANY STATEMENTS CLAIMING TO DIAGNOSE DEMENTIA**.
  - ❌ Automated clinical disease severity scores (e.g. "Stage 3 Dementia Detected").
* **Accessibility**: Clean tabular data exportable directly to high-contrast PDF.
