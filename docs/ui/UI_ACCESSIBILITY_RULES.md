# AASRITI ACCESSIBILITY CONSTITUTION & COGA GUIDELINES
## `/UI_ACCESSIBILITY_RULES.md` — Permanent Accessibility Specifications

> **COMPLIANCE LEVEL**: **WCAG 2.2 LEVEL AAA** (Strict for Patient Views) + **W3C COGA** (Cognitive & Learning Disabilities Guidelines)  
> **MISSION**: Guarantee zero technical, sensory, physical, or linguistic barriers for elderly individuals exhibiting Mild Cognitive Impairment (MCI) and early dementia.

---

## 👁️ 1. CONTRAST & VISUAL CLARITY STANDARDS

Elderly vision frequently suffers from reduced contrast sensitivity, macular degeneration, and yellowing of the eye lens (cataracts).

### Minimum Contrast Ratios:
* **Patient Normal Text ($\ge 18\text{sp}$)**: Minimum **7.0:1** contrast against background (Exceeds WCAG AAA).
  - *Example*: `Deep Charcoal` (`#1C2024`) on `Warm Ivory` (`#FDFBF7`) yields **14.2:1** contrast.
* **Patient Large Titles ($\ge 24\text{sp}$)**: Minimum **8.5:1** contrast.
* **Interactive Borders & Icons**: Minimum **4.5:1** contrast against adjacent surfaces (`Warm Stone Border` `#D6CBBB` on `#FDFBF7` is enhanced to `#4A525A` for active focus).
* **Zero Low-Contrast Gray Text**: Never use light gray text (`#9E9E9E` or `#BDBDBD`) for any instruction or patient label.

---

## 👆 2. MOTOR ACCESSIBILITY & TOUCH TARGET DIMENSIONS

Tremors, arthritis, and loss of fine motor coordination are common among elderly dementia patients.

```
┌────────────────────────────────────────────────────────┐
│               PATIENT TOUCH TARGET MANDATE             │
│                                                        │
│               ┌──────────────────────────┐             │
│               │                          │             │
│   Minimum:    │   72dp HEIGHT            │   Spacing:  │
│   64dp x 64dp │   Full Width (≥ 200dp)   │   ≥ 16dp    │
│               │                          │             │
│               └──────────────────────────┘             │
└────────────────────────────────────────────────────────┘
```

### Mandatory Rules:
1. **Primary Action Targets**: Minimum height **64dp**, recommended **72dp**.
2. **Inter-Element Gutter**: Minimum **16dp** of dead space between touchable targets to prevent accidental mis-taps.
3. **Hardware Debouncing**: Every button ignores subsequent clicks within **600ms** of the initial tap to absorb hand tremors.
4. **Gesture Simplicity**:
   - Strictly single-tap only.
   - **BANNED**: Long-press, pinch-to-zoom, two-finger gestures, swipe-to-delete, drag-and-drop requiring sustained hold.

---

## 🎙️ 3. AUDIO-FIRST VOICE REDUNDANCY

Every critical visual prompt must have a dual auditory affordance.

* **Automatic Read-Aloud**: When auto-voice is enabled in patient preferences, entering a screen immediately plays the localized instruction at $0.85\times$ speed.
* **Persistent Audio Pill**: Every patient screen features a prominent, unmissable `AasritiVoicePill` (`Muga Gold` `#C88D34`) that replays instructions on tap.
* **Natural Spoken Cadence**: Instructions are narrated by natural, warm local voices (female maternal voice preferred by clinical consensus in NER).
* **Visual Transcript Synchronization**: As audio plays, the corresponding text card highlights gently in `Supporting Sage` (`#A8B9A0`).

---

## 🧠 4. COGNITIVE LOAD REDUCTION & W3C COGA RULES

1. **One Focal Task Per Screen**:
   - The elder must never encounter two competing primary actions simultaneously.
   - Secondary actions must be visibly subdued (outlined or lower on the page).
2. **Working Memory Offloading**:
   - The interface maintains state. The patient never needs to remember information from a previous screen to complete a current task.
3. **Orientation Breadcrumbs**:
   - Top of Patient Home constantly anchors: Day of week, Time of day (Morning/Afternoon/Evening), and Weather metaphor (Sunny/Rainy).
4. **Sentence Brevity**:
   - Patient instructions are capped at **12 words maximum**.
   - Use active verbs: *"Tap the red flower"*, *"Drink your water"*, *"Call Mira"*.

---

## 🚫 5. NON-PUNITIVE EMOTIONAL SAFETY RULES

Dementia patients experience profound anxiety, agitation, and catastrophic reactions when confronted with failure or confusion.

### The Strict Language Contract:
| ❌ Strictly Forbidden Language | ✅ Mandatory AASRITI Replacement |
| :--- | :--- |
| *"Wrong! / Incorrect!"* | *"That's okay, let's look together."* |
| *"You failed the level."* | *"You did very well today, Aita."* |
| *"Time's up!"* | *"Let's take a peaceful rest."* |
| *"Error 404 / Connection Failed"* | *"We're resting right now. Your care records are safe."* |
| *"Invalid input / Required field"* | *"Please tell us your name."* |

---

## 🎬 6. MOTION & SENSORY SENSITIVITY STANDARDS

* **Subtle Transitions Only**: Standard transitions use gentle fades (`FadeIn`/`FadeOut` over 250ms) or soft vertical slide (`16dp` offset over 300ms).
* **Strictly Banned Animation Patterns**:
  - ❌ Screen shake or vibration upon wrong answers.
  - ❌ Flashing lights or strobing elements (seizure and panic triggers).
  - ❌ Confetti bursts or rapid spinning objects.
  - ❌ Bouncy spring physics that disorient visual tracking.
* **`prefers-reduced-motion` Respect**: If system accessibility has reduced motion enabled, all animated transitions collapse to instantaneous, gentle cross-fades.

---

## 🌐 7. MULTILINGUAL EXPANSION & OVERFLOW SAFETY

Indic scripts (Assamese, Bengali, Manipuri, Meitei Mayek) require approximately **20% to 35% more vertical and horizontal space** than English text.

* **Dynamic Font Scaling**: UI text containers must support Android system font scaling up to **130%** without text clipping or button collapse.
* **Zero Fixed Text Heights**: Never set `height = 40.dp` on a container enclosing text. Use `wrapContentHeight()` with `minHeight = 56.dp`.
* **Ellipsis Prohibition on Critical Labels**: Primary button labels and patient instructions must **never** truncate with `...`. If text wraps, the button height expands gracefully.

---

## 📋 8. ACCESSIBILITY QA VERIFICATION CHECKLIST

Before opening any Pull Request touching user interfaces, the developer must verify:

- [ ] High contrast tested with a black-and-white / grayscale screen filter.
- [ ] Every patient button measures $\ge 64\text{dp} \times 64\text{dp}$ touch target.
- [ ] No red/green color combination is used as the sole conveyor of information (always pair with icon + label).
- [ ] Spoken audio narration exists for all instructions on the screen.
- [ ] All images have meaningful `contentDescription` in the active regional language.
- [ ] Screen operates 100% functionally in Airplane Mode with zero network error modals.
- [ ] Android TalkBack focus navigates logically from top-left to bottom-right.
