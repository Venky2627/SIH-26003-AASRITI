# AASRITI — UI Reference & Design Package
## `/ui-reference/README.md`

### Overview
This directory stores the official frontend UI prototype package created by the AASRITI design team.
It serves as the **VISUAL AND INTERACTION DESIGN SOURCE OF TRUTH**.
It is **NOT** a replacement for the production Android application.

### Structure
- `prototype/`: Raw HTML/CSS/JS prototype with 35 screens and interactive mock store.
  - `screens/`: Shell-injected HTML screens (Screen 1 to Screen 34).
  - `raw_screens/`: Standalone HTML screens.
  - `css/`: Tailwind and custom styles (`styles.css`).
  - `js/`: Mock store (`store.js`) and navigation dispatcher (`nav.js`).
  - `docs/`: Screen flow navigation specification (`screen-flow.md`).
- `SCREEN_MAPPING.md`: Authoritative matrix mapping prototype screens to production Jetpack Compose screens.

### Design System Token Translation
| Prototype CSS Variable | Compose `AasritiColorTokens` | Usage |
| :--- | :--- | :--- |
| `--parchment-bg (#FAF4ED)` | `WarmIvory (0xFFFDFBF7)` / `SoftCream (0xFFF5EFE6)` | Primary background & cards |
| `--parchment-dark (#2A1D15)` | `DeepCharcoal (0xFF1C2024)` | Primary typography & icons |
| `--crimson-primary (#720227)` | `MutedHeritageTerracotta (0xFF9E2A2B)` | Brand accent, hero CTA, patient portal |
| `--forest-green (#274133)` | `DeepNortheastForest (0xFF245C45)` | Primary affirmative action, save buttons |
| `--amber-gold (#CE9042)` | `MugaGold (0xFFC88D34)` | Attention, audio prompts, indicators |
| `--sage-green (#A8B9A0)` | `SupportingSage (0xFFA8B9A0)` | Ambient chips, calm tags |
| `--terracotta (#CB8067)` | `SoftClay (0xFFCB8067)` | Tactile accents |
| `--border-cream (#D4C3AC)` | `WarmStoneBorder (0xFFD6CBBB)` | Structural borders (1.5dp–2dp) |

### Core Rules
1. **Never import HTML/React runtime** into Android. All presentation is native Jetpack Compose.
2. **Never replace Room SQLite** or real game engines with mock prototype state.
3. **Canonical Patient Identity**: Always bind persistent records to `DemoPatientConfig.PATIENT_ID = "aita_borah_01"`.
4. **Offline First**: All screens and PDF generation operate 100% locally with zero internet dependency.
5. **No Medical Claims**: Neutral functional labels ("Interaction Trends", "Care Priority", "Review Notes").
