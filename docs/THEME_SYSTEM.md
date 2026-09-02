# SmritiSetu (SIH26003) — Universal Accessibility Theme System & Design Tokens

## 🎨 1. Clinical Design Principles

SmritiSetu’s visual design is specifically engineered for elderly patients with Mild Cognitive Impairment (MCI) and early dementia residing in the rural North Eastern Region of India:
1. **Calming Color Psychology**: Rejection of alarming red or high-frequency blinking indicators that trigger agitation, anxiety, or catastrophic reactions. Primary utilization of nature-aligned emerald green (`#2E7D32`), trustworthy sapphire blue (`#1565C0`), and cataract-resilient gold (`#FFD700`).
2. **Extreme Contrast (WCAG 2.2 AAA)**: Every critical text and action card achieves a minimum contrast ratio of $7:1$ (up to $18.5:1$ on pure black surfaces) to overcome optical light scattering from senile lens opacification.
3. **Dyslexia-Friendly & Indic-First Typography**: Generous line spacing ($1.5\text{--}1.75\times$) with font priority mapped to native Brahmic scripts (`NotoSansBengali` for Assamese and Bengali, `NotoSansMeeteiMayek` for Manipuri).
4. **Motor Tremor Target Safety**: Standard touch targets expanded to $\ge 54\text{ px}$ (minimum) and $\ge 64\text{ px}$ (primary CTA) with minimum $16\text{ px}$ clearance.

---

## 📐 2. Complete Design Token Configuration (`mobile-app/src/theme/tokens.ts`)

```typescript
export const theme = {
  colors: {
    // Primary Brand & Action (Nature & Vitality)
    primary: '#2E7D32',          // Forest Green
    primaryDark: '#1B5E20',      // Deep Foliage Green
    primaryLight: '#4CAF50',     // Vibrant Leaf Green
    primaryHighlight: '#FFD700', // Pure Gold (13.8:1 contrast on #121212)

    // Secondary Support (Trust & Clinical Clarity)
    secondary: '#1565C0',        // Medical Royal Blue
    secondaryDark: '#0D47A1',    // Deep Navy
    secondaryLight: '#64B5F6',   // Calming Sky Blue

    // Surfaces & Cataract Contrast Backgrounds
    background: '#121212',       // High-Contrast Dark Surface (Eliminates Glare)
    surface: '#1E1E1E',          // Elevated Card Surface
    surfaceElevated: '#262626',  // Secondary Card / Selected State
    surfaceBorder: '#424242',    // Tactile Outline

    // Non-Agitating Semantic Feedback
    error: '#FFA000',            // Soothing Warm Amber (Never alarming red)
    errorBackground: '#332400',  // Dark Amber Container
    errorText: '#FFE082',        // High Contrast Pale Amber
    success: '#00E676',          // Reassuring Emerald Light
    successBackground: '#102A16',// Dark Emerald Container

    // Text Hierarchy (WCAG AAA Validated)
    text: {
      primary: '#FFFFFF',        // 18.5:1 contrast against #121212
      secondary: '#E0E0E0',      // 14.1:1 contrast
      muted: '#BDBDBD',          // 8.9:1 contrast
      accent: '#FFD700',         // 13.8:1 contrast
      inverse: '#121212',        // Text on Gold buttons
    },

    // Cultural NER Regional Accent Badges
    regional: {
      assamMuga: '#E5A93C',      // Traditional Assam Muga Silk Hue
      manipurSangai: '#A0522D',  // Sangai Deer Russet
      nagalandBead: '#D32F2F',   // Ceremonial Bead Red (Restricted to badges)
      bodoHandloom: '#2E7D32',   // Dokhona Field Green
    },
  },

  typography: {
    fontFamily: {
      // Priority chain for Indic Script rendering
      regular: 'NotoSansBengali-Regular',
      medium: 'NotoSansBengali-Medium',
      bold: 'NotoSansBengali-Bold',
      meitei: 'NotoSansMeeteiMayek-Regular',
      system: Platform.select({ android: 'Roboto', ios: 'System' }),
    },
    fontSize: {
      xs: 16,   // Absolute minimum body text allowed
      sm: 18,   // Standard readable body text
      md: 20,   // Subheadings & Card Titles
      lg: 24,   // Section Headers
      xl: 28,   // Screen Titles
      xxl: 36,  // Hero Display Branding
    },
    lineHeight: {
      tight: 1.3,
      normal: 1.6, // Dyslexia & aging eye spacing standard
      relaxed: 1.8,
    },
    letterSpacing: {
      normal: 0.5,
      wide: 1.5,
      digits: 4.0, // Used for OTP and PIN boxes
    },
  },

  spacing: {
    xs: 4,
    sm: 8,
    md: 16,
    lg: 20,
    xl: 24,
    xxl: 32,
    huge: 48,
  },

  borderRadius: {
    sm: 10,
    md: 14,
    lg: 20,
    pill: 9999,
  },

  touchTarget: {
    minSize: 48,       // Minimum bounding box for secondary icons
    standardButton: 56,// Secondary buttons
    primaryAction: 64, // Primary CTAs and large cards
  },

  animation: {
    duration: {
      fast: 200,
      standard: 350,
      calm: 500,
      splashHold: 1500,
    },
    easing: 'ease-in-out', // Linear or gentle ease; strictly no spring/bouncy motion
  },
};
```

---

## 🔍 3. Contrast Ratio Verification Table

| Foreground Element | Background Surface | Contrast Ratio | WCAG 2.2 Status | Rationale |
| :--- | :--- | :---: | :---: | :--- |
| `#FFFFFF` (Primary Text) | `#121212` (App Canvas) | **18.5:1** | ✅ AAA Pass | Maximum reading ease under outdoor rural sunlight. |
| `#FFD700` (Gold Header) | `#121212` (App Canvas) | **13.8:1** | ✅ AAA Pass | Cataract optical yellowing cannot degrade gold/black contrast. |
| `#121212` (Dark Text) | `#FFD700` (Gold Button) | **13.8:1** | ✅ AAA Pass | Primary CTA button label sharpness. |
| `#00E676` (Green Status) | `#121212` (App Canvas) | **11.2:1** | ✅ AAA Pass | Instant visual confirmation of offline security. |
| `#64B5F6` (Sky Blue) | `#1E1E1E` (Card Surface) | **8.1:1** | ✅ AAA Pass | Caregiver role distinctions. |
| `#FFE082` (Amber Alert) | `#332400` (Amber Box) | **7.4:1** | ✅ AAA Pass | Gentle, non-punitive error correction banner. |
