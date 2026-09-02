# SmritiSetu (SIH26003) — Universal Accessibility & Cognitive Ergonomics (WCAG 2.2 AAA)

## ♿ 1. Clinical Rationale: Sensory & Motor Aging in Dementia

Elderly patients (ages 60+) with Mild Cognitive Impairment (MCI) and early dementia suffer from compounding physical and neurological limitations:
1. **Senile Cataracts & Macular Degeneration**: Reduced contrast sensitivity, yellowing of the ocular lens (impairing blue/violet discrimination), and photo-sensitivity.
2. **Essential Tremors & Parkinsonian Rigidity**: Involuntary hand tremors ($4\text{--}8\text{ Hz}$), reduced finger dexterity, accidental double-tapping, and difficulty lifting fingers promptly from capacitive touch screens.
3. **Working Memory & Executive Dysfunction**: Inability to navigate multi-level nested menus, cognitive fatigue from cluttered visual stimuli, and confusion caused by rapid visual transitions.

---

## 📋 2. Comprehensive WCAG 2.2 AAA Compliance Matrix

| WCAG 2.2 Criterion | Target Standard | SmritiSetu Implementation | Verification Method |
| :--- | :--- | :--- | :--- |
| **1.4.6 Contrast (Enhanced)** | Contrast ratio $\ge 7:1$ for normal text, $\ge 4.5:1$ for large text. | High-contrast palette: Pure Black `#121212` background with `#FFFF00` (Yellow) and `#FFFFFF` text. | Automated axe-core & Stark contrast analyzer |
| **2.5.5 Target Size (Enhanced)** | Minimum touch target area of $44 \times 44\text{ dp}$ (WCAG); $64 \times 64\text{ dp}$ (SmritiSetu Standard). | All interactive game cards and action buttons are strictly $\ge 64 \times 64\text{ dp}$ with minimum $16\text{ dp}$ padding. | Layout inspector automated assertion |
| **2.5.8 Pointer Target Spacing** | Sufficient clearance between adjacent interactive targets. | Minimum $16\text{ dp}$ separation between all touchable elements to prevent accidental adjacent presses. | Jest UI unit snapshot tests |
| **2.2.1 Timing Adjustable** | Users can turn off, adjust, or extend time limits $\ge 10\times$. | Game timeouts dynamically scale with patient cognitive baseline or can be placed in "Untimed Therapeutic Mode". | Redux timer setting validation |
| **2.5.1 Pointer Gestures** | All functionality operable with simple single-point activation. | Zero multi-touch gestures (no pinch-to-zoom, no complex swiping, no drag-and-drop). Only single tap. | Manual gesture elimination audit |
| **3.2.3 Consistent Navigation** | Navigation patterns remain identical across all screens. | Universal header with constant Back Button (top-left) and Spoken Help Button (top-right). | Navigation flow inspection |
| **3.3.4 Error Prevention (Legal/Health)** | Submissions are reversible, checked, or confirmed. | Caregiver actions (e.g., patient profile archival) require dual-confirmation dialogs with PIN. | Cypress/Detox E2E test |
| **1.4.12 Text Spacing** | Line height $\ge 1.5\times$ font size, letter spacing $\ge 0.12\times$. | Custom typography stylesheets enforcing $1.6\times$ line spacing and generous kerning for Indic scripts. | CSS/React Native StyleSheet audit |

---

## 🎨 3. Color Palette Specifications (Cataract-Resilient)

```
+-------------------------------------------------------------------------------+
|                      HIGH-CONTRAST CLINICAL PALETTE                           |
+---------------------+-------------------+---------------------+---------------+
| Token Name          | Hex Code          | Contrast Ratio (vs Background)       |
+---------------------+-------------------+---------------------+---------------+
| Surface Background  | #121212 (Dark)    | Baseline            | High ambient  |
| Primary Action      | #FFD700 (Gold)    | 13.8:1 (AAA Pass)   | Primary CTA   |
| High Priority / Mic | #00E676 (Green)   | 11.2:1 (AAA Pass)   | Audio/Record  |
| Text Normal         | #FFFFFF (White)   | 18.5:1 (AAA Pass)   | Body Text     |
| Text Secondary      | #E0E0E0 (Lt Gray) | 14.1:1 (AAA Pass)   | Metadata      |
| Danger / Stop       | #FF5252 (Coral)   | 7.4:1 (AAA Pass)    | Abort Session |
+---------------------+-------------------+---------------------+---------------+
```

> [!IMPORTANT]
> The color blue (`#0000FF`) is strictly avoided for critical UI controls because senile cataract lens yellowing absorbs short-wavelength blue light, rendering it nearly indistinguishable from black or dark gray.

---

## 🖐️ 4. Motor Tremor Damping & Tap Absorption Algorithm

Elderly individuals frequently experience involuntary micro-tremors, causing rapid duplicate tap events (`touch-up` followed by `touch-down` within $150\text{ms}$).

### Implementation Pattern (`mobile-app/src/utils/tremorFilter.ts`):
```typescript
/**
 * Wraps any touch event with debounce and minimum dwell time filtering
 * to prevent accidental double taps from essential tremors.
 */
export const createTremorResistantPressHandler = (
  callback: () => void,
  debounceMs: number = 600,
  minDwellMs: number = 80
) => {
  let lastPressTime = 0;
  let touchStartTime = 0;

  return {
    onTouchStart: () => {
      touchStartTime = Date.now();
    },
    onTouchEnd: () => {
      const touchDuration = Date.now() - touchStartTime;
      const now = Date.now();

      // Filter 1: Ignore accidental brushing touches shorter than minDwellMs
      if (touchDuration < minDwellMs) {
        return;
      }

      // Filter 2: Debounce rapid duplicate taps
      if (now - lastPressTime > debounceMs) {
        lastPressTime = now;
        callback();
      }
    },
  };
};
```

---

## 🗣️ 5. Multilingual Spoken Audio Prompts

For elderly illiterates or patients who have lost the ability to read written text:
* Every screen features a persistent **Audio Speaker Button** (🔉).
* Tapping the button plays a warm, human-recorded voice prompt in the patient's selected mother tongue:
  * **Assamese (`as`)**: *"এই ছবিখন চাওক আৰু ইয়াৰ নাম কওক।"* (Look at this picture and tell me its name.)
  * **Manipuri (`mn`)**: *"ꯃꯁꯤꯒꯤ ꯐꯣꯇꯣ ꯑꯁꯤ ꯌꯦꯡꯕꯤꯌꯨ ꯑꯃꯁꯨꯡ ꯃꯤꯡ ꯍꯥꯌꯕꯤꯌꯨ।"*
  * **Bodo (`br`)**: *"बे सावगारिखौ नाय आरो बेनि मुं बुं।"*
* All audio clips are pre-compressed and bundled into the local APK assets to ensure instant playback without network buffering.

---

## 📶 6. OFFLINE BEHAVIOR SPECIFICATION (Accessibility Subsystem)

### 1. What happens when this feature runs with zero internet connectivity?
* All accessibility features (tremor damping, high-contrast themes, local text-to-speech audio files, large touch targets) function identically offline.
* Voice prompts are loaded directly from local application assets (`assets/audio/prompts/`).

### 2. What data is stored locally vs. requires cloud?
* **Stored Locally**: All audio prompt sound files, SVG icons, font definitions, and user accessibility preference toggles.
* **Requires Cloud**: Zero accessibility assets require cloud access.

### 3. What is the fallback/degradation strategy if cloud is unreachable?
* No degradation occurs; accessibility is entirely on-device.

### 4. How does the user know they're in offline mode? (UI indicators)
* High contrast offline status indicator remains visible in the header without disrupting reading flow.

### 5. How does data integrity survive app crashes during offline operation?
* User accessibility preferences (font scale, contrast mode, audio speed) are persisted in SQLite and cached in `AsyncStorage`/Redux Persist, surviving application restarts immediately.
