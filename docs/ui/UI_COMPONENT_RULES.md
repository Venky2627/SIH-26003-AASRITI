# AASRITI COMPONENT BEHAVIOR & VISUAL GOVERNANCE
## `/UI_COMPONENT_RULES.md` — Canonical Component Specification

> **GOVERNANCE PRINCIPLE**: Every UI element in AASRITI must strictly belong to one of these approved component specifications. No feature or screen is permitted to invent custom buttons, rogue cards, or arbitrary inputs.

---

## 🔘 1. BUTTONS & INTERACTIVE CONTROLS

### A. Primary Action Button (`AasritiPrimaryButton`)
* **Purpose**: Represents the single most important action on a screen (e.g. *Start Activity*, *Save Log*, *Continue*).
* **Visual Hierarchy**: Level 1 (Dominant). Solid fill: `Deep Northeast Forest` (`#245C45`). Text: `Warm Ivory` (`#FDFBF7`), Bold, 16sp.
* **Size**:
  - Patient Views: Height **64dp–72dp**, minimum width **200dp** (Full width with 16dp margins preferred).
  - Caregiver/Doctor Views: Height **52dp–56dp**.
* **Corner Radius**: 16dp.
* **Allowed Usage**: Exactly ONE primary button per viewport.
* **Forbidden Usage**: Never use for secondary/cancel actions; never use with red or amber fills; never smaller than 52dp.

### B. Secondary Action Button (`AasritiSecondaryButton`)
* **Purpose**: Supporting actions (e.g. *Try Again*, *Listen Again*, *Back*, *Cancel*).
* **Visual Hierarchy**: Level 2. Outline fill: Background `Soft Cream` (`#F5EFE6`), Border 1.5dp `Warm Stone` (`#D6CBBB`), Text `Deep Charcoal` (`#1C2024`).
* **Size**: Height matching primary button (52dp–64dp).
* **Corner Radius**: 16dp.
* **Allowed Usage**: Positioned adjacent to or below the primary button.

### C. Emergency SOS Button (`AasritiSosButton`)
* **Purpose**: Immediate distress call and family notification.
* **Visual Hierarchy**: Critical / Isolated. Solid fill: `Deep Cranberry` (`#8B183F`). Text: `Warm Ivory` (`#FDFBF7`), Bold, 18sp.
* **Size**: Patient Home: **64dp $\times$ 64dp** circular pill or full-width banner. Dedicated SOS Screen: **120dp $\times$ 120dp** circle.
* **Allowed Usage**: Dedicated SOS screens and the isolated emergency pill in patient header.
* **Forbidden Usage**: Never use Cranberry for ordinary buttons, form submissions, or delete confirmations.

---

## 🎴 2. CARDS & CONTAINERS

### A. Hero Activity Card (`AasritiHeroActivityCard`)
* **Purpose**: Houses the single primary cognitive or routine recommendation on the Patient Home screen.
* **Visual Hierarchy**: Dominant container. Background: `Soft Cream` (`#F5EFE6`). Border: 1.5dp `Warm Stone` (`#D6CBBB`).
* **Corner Radius**: 24dp (Very Large).
* **Internal Padding**: 24dp horizontal, 28dp vertical.
* **Structure**: Top regional illustration/icon (64dp) $\rightarrow$ Activity Title (22sp SemiBold) $\rightarrow$ Subtitle (16sp Regular) $\rightarrow$ Spacing 20dp $\rightarrow$ Primary Action Button.
* **Forbidden Usage**: Never place more than one Hero Activity Card on a single screen.

### B. Standard Content Card (`AasritiCard`)
* **Purpose**: Encloses list items, routine tasks, and observation summaries for Caregivers and ASHA workers.
* **Visual Hierarchy**: Level 2 container. Background: `Soft Cream` (`#F5EFE6`). Border: 1dp `Warm Stone` (`#D6CBBB`).
* **Corner Radius**: 16dp. Internal Padding: 16dp.
* **Allowed Usage**: Used for routine checklists, patient rows, and clinical observation notes.

---

## 🎙️ 3. VOICE CONTROLLER AFFORDANCE (`AasritiVoicePill`)

The voice button is a vital accessibility lifeline for elderly patients with low literacy or visual impairment.

### Visual States & Color Semantics:
1. **Idle State**: Solid Pill (`Muga Gold` `#C88D34`), Speaker icon + text *"Listen"* (`Deep Charcoal` `#1C2024`).
2. **Playing State**: Animated gentle pulse (opacity oscillates 100% $\leftrightarrow$ 75%), Text *"Playing..."*.
3. **Listening State**: Border highlights with `Deep Northeast Forest` (`#245C45`), Mic icon active.
4. **Paused State**: Static pill with Play arrow icon.
5. **Error State**: Subtle amber stroke (`Warm Amber` `#B45309`), Text *"Tap to retry"*.

* **Size**: Height **56dp**, horizontal padding 20dp, fully rounded Pill radius.
* **Forbidden Usage**: Never render voice as an unlabelled 24dp raw icon; voice must always have text + icon.

---

## 🖼️ 4. MEMORY ALBUM ITEMS (`AasritiMemoryItem`)

* **Purpose**: Displays family photos, familiar locations, and cultural events in the Memory Garden.
* **Visual Hierarchy**: Emulates a treasured physical family photograph.
* **Structure**:
  - Frame: 12dp border padding of `Warm Ivory` around the image.
  - Image: Aspect ratio 4:3 or 1:1, corner radius 12dp, subtle warm tone filter.
  - Caption Block: Deep Charcoal text (20sp SemiBold) centered below photo.
  - Audio Button: Attached `Muga Gold` pill (*"Hear Son Rupam"*).
* **Forbidden Usage**: Never display file technical details, file sizes, or camera filenames.

---

## 📋 5. ROUTINE & CHECKLIST ITEMS (`AasritiRoutineRow`)

* **Purpose**: Displays daily routines (Medicine, Water, Walk) in a respectful, supportive format.
* **Visual Hierarchy**: Gentle horizontal row. Background: `Soft Cream` (`#F5EFE6`). Height: **64dp**.
* **Structure**:
  - Left: Status indicator (36dp circle: Green checkmark `#245C45` if completed, Sunken surface if pending).
  - Center: Routine title (18sp Medium) + scheduled time (14sp Warm Slate).
  - Right: Action arrow or audio speaker icon.
* **Forbidden Usage**: Never show red alert crosses for missed routines; use neutral warm amber clock icon.

---

## 🧩 6. COGNITIVE GAME SELECTION TILES (`AasritiChoiceTile`)

* **Purpose**: Used across all 6 cognitive games for user selection.
* **Size**: Minimum **88dp height**, full width (or 2 equal columns with 16dp gutter).
* **States**:
  - **Default**: Background `Soft Cream` (`#F5EFE6`), Border 1.5dp `Warm Stone` (`#D6CBBB`).
  - **Selected / Correct**: Background `Supporting Sage` (`#A8B9A0` at 30% alpha), Border 2.5dp `Deep Northeast Forest` (`#245C45`).
  - **Gentle Re-try**: Background `Soft Cream`, Border 2dp `Warm Amber` (`#B45309`), no red buzzer.
* **Content**: Large central icon/illustration (48dp) + clear bold label (18sp SemiBold).

---

## 📢 7. NON-PUNITIVE FEEDBACK BANNERS

* **Positive Confirmation**:
  - Background: `Supporting Sage` (`#A8B9A0` at 25% tint).
  - Text: `Deep Northeast Forest` (`#245C45`), SemiBold 18sp.
  - Copy: *"Well done, Aita!"* | *"That's lovely!"* | *"Great memory!"*
* **Gentle Encouragement (Incorrect / Timeout)**:
  - Background: `Warm Sunken Surface` (`#EFE7DA`).
  - Text: `Warm Slate` (`#4A525A`), Medium 18sp.
  - Copy: *"That's okay, let's take another look."* | *"Take your time."*
* **Strict Ban**: Never use red backgrounds, buzzer sirens, shaking animations, or skull/cross symbols.

---

## ⚠️ 8. DIALOGUES & MODAL SHEETS

### Bottom Sheet Modal (`AasritiBottomSheet`)
* **Purpose**: Progressive disclosure for Caregiver Quick Log, detail views, and settings.
* **Background**: `Warm Ivory` (`#FDFBF7`). Top Corner Radius: **24dp**.
* **Top Drag Handle**: 40dp width, 4dp height, `Warm Stone` (`#D6CBBB`).
* **Close Affordance**: Explicit [ Done ] button at bottom; never force elderly users to swipe down to dismiss.

---

## 📭 9. EMPTY & OFFLINE STATES

### Reassuring Empty State (`AasritiEmptyState`)
* **Visual**: Warm botanical or traditional textile illustration in muted tones (no broken plug graphics).
* **Headline**: Warm, reassuring sentence (e.g. *"Your Memory Garden is ready for family photos."*).
* **Action**: Single clear button: **[ Add First Family Photo ]**.

### Quiet Offline Indicator (`AasritiOfflineBadge`)
* **Visual**: Compact pill in caregiver/doctor app bar (Background `Warm Sunken Surface`, Dot `Warm Amber` `#B45309`).
* **Text**: *"Working Offline — All Care Records Safe"*.
* **Behavior**: Completely non-modal. It never interrupts gameplay or care logging with full-screen error blocks.
