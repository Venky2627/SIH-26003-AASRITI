# AASRITI UI CONSTITUTION & GOVERNANCE RULES
## `/UI_RULES.md` — Authoritative Master Visual & Interaction Directive

> **STATUS**: PERMANENT REPOSITORY CONSTITUTION  
> **APPLICATION**: AASRITI (SIH-26003)  
> **SCOPE**: All Developers, Designers, PR Reviewers, and AI Coding Agents  
> **CORE AXIOM**: **"AASRITI UI IS A SYSTEM, NOT A COLLECTION OF SCREENS."**

---

## 🛑 MANDATORY GOVERNANCE DECLARATIONS

1. **Reuse Before Reinventing**:
   > **"Existing AASRITI patterns MUST be reused before introducing anything new."**
2. **Strict Ban on Casual Innovation**:
   > **"New UI patterns are NOT allowed to be introduced casually."**
3. **Pre-Implementation Documentation Rule**:
   > **"If a new visual pattern is genuinely required, the design system documentation (`/UI_RULES.md`, `/UI_COMPONENT_RULES.md`, `/UI_SCREEN_SPEC.md`) must be updated before or together with its implementation."**
4. **Non-Negotiable Patient Architecture Rule**:
   > **"PATIENT UI IS NOT A DASHBOARD. The elder experience must guide gently rather than measure clinically."**

---

## 🌿 1. PRODUCT IDENTITY & EMOTIONAL PRINCIPLES

AASRITI is a dementia-care, cognitive-assistance, memory-support, and care-connection companion platform engineered around elderly individuals with cognitive impairment and the families, ASHA workers, and clinicians who support them.

### The Nine Core Emotional Principles:
* **CALM**: Low cognitive load, zero visual clutter, muted natural tones, quiet transitions.
* **FAMILIAR**: Regional North Eastern cultural motifs, recognizable household metaphors, localized language.
* **HUMAN**: Language that sounds like a caring family member, never like a database or robot.
* **RESPECTFUL**: Treating elders with mature dignity; never childish, patronizing, or juvenile.
* **SAFE**: Error-proof interaction, non-punitive feedback, obvious emergency access, zero penalties.
* **WARM**: Grounded in natural materials (ivory paper, clay pottery, muga silk, bamboo forests).
* **SIMPLE**: One primary focal action per screen, oversized touch targets, direct visual pathways.
* **TRUSTWORTHY**: Dependable offline operation, predictable back navigation, transparent local privacy.
* **CULTURALLY ROOTED**: Authentic heritage from Assam, Manipur, and Meghalaya woven subtly into the experience.

### What AASRITI Feels Like:
> *A thoughtful, quiet, handcrafted digital companion designed by a loving grandchild for their aging grandparent.*

### What AASRITI Strictly Does NOT Look Like:
* ❌ A generic AI or tech-startup product (no glowing purple gradients, glassmorphism, floating orbs, or neon accents).
* ❌ A hospital ERP or medical billing portal (no dense tabular records, ICD codes, or cold clinical charts on patient screens).
* ❌ An enterprise SaaS dashboard (no multi-column metrics, analytics sidebars, or KPI summary tiles for the patient).
* ❌ A children's game (no kindergarten primary colors, bouncy cartoon characters, or gamified confetti bursts).
* ❌ A generic government portal (no bureaucratic text walls, cramped forms, or low-contrast gray tables).
* ❌ A superficial ethnic tourist website (no overwhelming wallpaper patterns, religious stereotyping, or decorative clutter).

---

## 👥 2. ROLE-BASED INFORMATION DENSITY ARCHITECTURE

AASRITI supports four discrete user roles. While the **visual language remains identical** (same colors, typography, corner radii, and elevation), the **information density dynamically adapts** to the user's cognitive context:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          AASRITI ROLE DENSITY SPECTRUM                      │
├─────────────────┬─────────────────┬────────────────────┬────────────────────┤
│     PATIENT     │    CAREGIVER    │    ASHA WORKER     │  DOCTOR / CLINICIAN│
├─────────────────┼─────────────────┼────────────────────┼────────────────────┤
│ Visual Density: │ Visual Density: │ Visual Density:    │ Visual Density:    │
│    VERY LOW     │   LOW-MEDIUM    │      MEDIUM        │    MEDIUM-HIGH     │
├─────────────────┼─────────────────┼────────────────────┼────────────────────┤
│ • 1 action/view │ • Today summary │ • Multi-patient    │ • Longitudinal     │
│ • Large targets │ • Priority card │   roster           │   trend curves     │
│ • Spoken audio  │ • Quick Log     │ • Rapid field logs │ • Adherence %      │
│ • Zero metrics  │ • Routine sync  │ • Triage badges    │ • Session flags    │
│ • Emotional warm│ • Family circle │ • Communal register│ • Clinical review  │
└─────────────────┴─────────────────┴────────────────────┴────────────────────┘
```

### The Non-Negotiable Patient Screen Standard:
The patient screen must never answer: *"How well am I performing?"*  
The patient screen must only answer: **"What should I do now?"**
* ✅ *"Let's play Flower Match."*
* ✅ *"Time for your morning medicine."*
* ✅ *"Would you like to listen to flute music?"*
* ✅ *"Call your daughter Mira."*
* ✅ *"Let's look at a family memory."*

---

## 🎨 3. CANONICAL COLOR SYSTEM

Every color in AASRITI is semantically grounded in natural, regional North Eastern materials. Arbitrary hex codes are strictly forbidden.

### The 13 Canonical Base Tokens:

| Token Name | Hex Code | Semantic Meaning & Approved Usage |
| :--- | :---: | :--- |
| **Warm Ivory** | `#FDFBF7` | Primary background surface for all screens. Emulates natural parchment/cotton. |
| **Soft Cream** | `#F5EFE6` | Default card and elevated surface background. Soft, glare-reducing warmth. |
| **Warm Sunken Surface** | `#EFE7DA` | Recessed container background, inset text areas, progress tracks. |
| **Deep Charcoal** | `#1C2024` | Primary typography, headers, icons. Maximum readability without harsh `#000000`. |
| **Warm Slate** | `#4A525A` | Secondary body text, timestamps, subtitles, supporting metadata. |
| **Warm Stone Border** | `#D6CBBB` | Structural borders, subtle dividers, inactive outlines (1px–2px). |
| **Deep Northeast Forest** | `#245C45` | **PRIMARY ACTION COLOR**. Normal state, affirmative buttons, success badges. |
| **Muted Heritage Terracotta**| `#9E2A2B` | **AASRITI BRAND IDENTITY**. Memories, family links, active tabs, storytelling. |
| **Muga Gold** | `#C88D34` | Audio/Voice indicators, attention accents, gentle reminders, Assam silk warmth. |
| **Supporting Sage** | `#A8B9A0` | Secondary peaceful chips, ambient card fills, calm routine completions. |
| **Soft Clay** | `#CB8067` | Warm secondary accents, tactile buttons, secondary memory tags. |
| **Deep Cranberry** | `#8B183F` | **EMERGENCY / SOS ONLY**. High-priority alerts, fall notifications, SOS buttons. |
| **Warm Amber** | `#B45309` | Warning, pending synchronization, missed routine indicator. |

### Semantic Color Rules:
1. **Never Use Cranberry as Primary**: `#8B183F` is exclusively reserved for distress, SOS, and high-risk safety triage.
2. **Forest Green is Calm Confirmation**: Use `#245C45` for "Start", "Continue", "Done", "Save", and "Play".
3. **Terracotta Anchors Reminiscence**: Use `#9E2A2B` for Memory Garden, Family portraits, and heritage framing.
4. **Zero Rainbow UI**: A single screen may contain at most 1 primary action color, 1 neutral surface, and 1 subtle semantic accent.

---

## ✍️ 4. CANONICAL TYPOGRAPHY SYSTEM

AASRITI employs a clean, open, highly accessible sans-serif system with warm letterforms and generous x-height (system default `Inter` or `Roboto` with fallback to `Anek Assamese` for Indic scripts).

### Typography Scale & Hierarchy:

| Role | Font Size | Line Height | Weight | Approved Usage |
| :--- | :---: | :---: | :---: | :--- |
| **Display** | 32sp | 40sp | Bold (700) | Patient Greeting, Screen title on primary views. |
| **Heading Large** | 24sp | 32sp | SemiBold (600) | Primary Activity Card title, Memory title. |
| **Heading Medium**| 20sp | 28sp | SemiBold (600) | Section titles (e.g. "Today's Routine", "Family Circle"). |
| **Body Large** | 18sp | 26sp | Regular (400) / Medium (500) | Primary patient instruction, spoken prompt transcript. |
| **Body** | 16sp | 24sp | Regular (400) | Standard caregiver text, descriptive body paragraphs. |
| **Label** | 14sp | 20sp | SemiBold (600) | Button labels, chip text, status tags. |
| **Caption** | 12sp | 16sp | Regular (400) | Caregiver metadata, timestamps, sync status (NEVER for patients). |

### Patient Typography Constraints:
* **Minimum Font Size for Patients**: **18sp** for all instructions and interactive choices.
* **No Thin Weights**: Font weights below 400 (Light, Thin) are strictly banned across the entire application.
* **No ALL CAPS**: All-caps text is harder for elderly brains to read. Sentence case is mandatory everywhere.
* **Short Sentences**: Patient instructions must be capped at 12 words per sentence.

---

## 📐 5. SPACING, RADIUS & ELEVATION SYSTEM

### Base Spacing Rhythm (Multiples of 4dp):
```
4dp  ── Micro spacing between badge icon and text
8dp  ── Internal padding of chips and compact controls
12dp ── Space between related list items
16dp ── Standard card internal padding, screen edge margins (mobile)
20dp ── Spacing between distinct component groups
24dp ── Screen horizontal padding on tablet / generous patient margins
32dp ── Space between major vertical sections
40dp ── Patient primary activity card vertical padding
48dp ── Header-to-content breathing room
64dp ── Maximum patient breathing separation
```

### Corner Radius System:
* **Small (8dp)**: Input fields, badges, status chips.
* **Medium (12dp)**: Standard secondary cards, caregiver summary tiles.
* **Large (16dp)**: Patient interactive cards, game choice tiles, memory frames.
* **Very Large (20–24dp)**: Bottom sheets, modal dialogues, primary hero activity card.
* **Pill (Fully Rounded)**: Voice control button, audio playback pill, emergency SOS trigger.

### Elevation & Shadows:
* **Zero Heavy Shadows**: Dramatic black drop-shadows and 3D floating cards are forbidden.
* **Surface Separation over Blur**: Separate layers using a 1dp `Warm Stone Border` (`#D6CBBB`) and tonal contrast (`Warm Ivory` against `Soft Cream`).
* **Subtle Elevation**: If elevation is required, use maximum `elevation = 2dp` with soft warm diffusion.

---

## 🔘 6. INTERACTION & TOUCH TARGET STANDARDS

### Minimum Touch Targets by Role:
* **Patient Touch Targets**: Minimum **64dp $\times$ 64dp** (Recommended **72dp $\times$ 72dp** for Start, Voice, Back, and SOS).
* **Caregiver / ASHA / Doctor Touch Targets**: Minimum **48dp $\times$ 48dp** (Standard **56dp**).

### Tremor & Motor Friction Mitigation:
* **Generous Tap Margins**: Interactive buttons must have at least 16dp of empty space between one another.
* **Debounced Clicks**: Accidental rapid double-taps are ignored for 600ms on all patient controls.
* **Zero Precise Gestures**: No swiping, pinching, double-tapping, or long-pressing required for patient navigation. Single tap is the universal interaction.

---

## 🚫 7. THE ABSOLUTE FORBIDDEN LIST

Developers and AI agents are strictly prohibited from implementing:
1. ❌ **Patient Analytics Dashboard**: Never show graphs, cognitive scores, or error counts to the elder.
2. ❌ **Shaming Language**: Never say *"Wrong!"*, *"Failed!"*, *"Error!"*, or *"Game Over"*.
3. ❌ **Rainbow Palettes**: Never introduce purple, blue, magenta, neon green, or electric accents.
4. ❌ **Childish Visuals**: Never introduce cartoon animals, kindergarten doodles, emoji walls, or confetti.
5. ❌ **Generic Stock Photography**: Never use corporate Western stock photos. Use authentic NER cultural assets or user-provided family photos.
6. ❌ **Violent Motion**: Never use rapid shaking, flashing, vibrating screen shakes, or bouncy physics.
7. ❌ **Technical Error Codes**: Never show `HTTP 500`, `SQLiteException`, or `NullPointerException` to users.

---

## ✅ 8. THE ABSOLUTE COMPLIANCE LIST

Always ensure:
1. ✔ **One Focal Task Per Screen**: The user's eye is immediately drawn to the single primary action.
2. ✔ **Progressive Disclosure**: Show a calm summary first; provide detail only on explicit demand.
3. ✔ **Dual Sensory Feedback**: High-contrast visual highlight paired with calming audio confirmation.
4. ✔ **Honest Regional Representation**: Distinct recognition of Assam, Manipur, and Meghalaya heritage.
5. ✔ **Quiet Offline Continuity**: App states gracefully communicate: *"You are offline. Your care information is safely stored."*
