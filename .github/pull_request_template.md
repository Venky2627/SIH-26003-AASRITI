## 📝 Summary
<!-- Concise 1-2 sentence overview of this pull request -->

### What changed?
<!-- Detailed bullet points of modifications introduced -->

### Why?
<!-- Purpose, user requirement, or bug resolved -->

---

## 🔍 Affected Systems
Check all components impacted by this PR:
- [ ] **Frontend** (Jetpack Compose, UI Screens, Navigation)
- [ ] **Backend** (Firebase Messenger, Sync Processors, Cloud Rules)
- [ ] **Database** (Room Entities, SQLite Schema, DAOs, Migrations)
- [ ] **API** (Sync Queue Payloads, Network Contracts)
- [ ] **Authentication** (Local PIN, Role Gatekeeper, Session Tokens)
- [ ] **Authorization** (Role-Based Permissions, Data Isolation)
- [ ] **AI / ML** (Decision Tree Engine, Scikit-Learn Training Scripts)
- [ ] **Voice** (TextToSpeech, Audio Prompt Packs, Language Manager)
- [ ] **Games** (BaseGameEngine, Cognitive Mini-Games 1–6)
- [ ] **Memory** (Memory Garden, Reminiscence Media Storage)
- [ ] **Notifications** (AlarmManager Reminders, BootReceiver)
- [ ] **Security** (Encryption, CryptoUtils, Input Sanitization, DPDPA)
- [ ] **UI** (Theme Tokens, Colors, Spacing, Typography Scale)
- [ ] **Accessibility** (Touch Targets ≥ 64dp, WCAG AAA Contrast, Non-punitive copy)
- [ ] **Localization** (Assamese, Manipuri, English String Resources)
- [ ] **Infrastructure** (GitHub Actions CI, Gradle Config, ProGuard)
- [ ] **Documentation** (Architecture, Task Board, Team Guides)

---

## 🧪 Testing Performed
- [ ] Unit Tests executed locally (`./gradlew testDebugUnitTest`)
- [ ] Manual Device / Emulator run verified
- [ ] **Airplane Mode Verification** (100% offline functionality verified with 0 Kbps data)
- [ ] Test names / details: <!-- e.g., DecisionTreeEngineTest, PriorityEngineTest -->

---

## 🎨 UI Governance Verification
- Did this change follow the existing AASRITI UI system?
  - [ ] **Yes** (Used canonical colors from `AasritiColorTokens`, typography from `AasritiTypography`, spacing from `AasritiSpacing`)
  - [ ] **N/A** (Non-UI backend change)
- Are touch targets on patient screens at least 64dp $\times$ 64dp?
  - [ ] **Yes**
  - [ ] **N/A**
- Does the patient screen avoid any dashboard/score/analytics metrics?
  - [ ] **Yes**
  - [ ] **N/A**

---

## 🏛️ Architecture & Database Impact
- **Architecture**: Was an existing pattern reused?
  - [ ] **Yes** (Reused BaseGameEngine, Repository, or Clean Architecture pattern)
  - [ ] **No / New Pattern** (Requires explanation and `/UI_RULES.md` or `/docs/ARCHITECTURE.md` update)
- **Database**: Was a Room database migration or entity change required?
  - [ ] **No**
  - [ ] **Yes** (Requires 2 approvals from @Venkatesh and @Jasleen)
- **API**: Did any API or Sync Queue contract change?
  - [ ] **No**
  - [ ] **Yes** (Explain consumer impact below)

---

## 🔒 Security & Configuration
- **Security**: Were permissions, credentials, or sensitive patient data affected?
  - [ ] **No**
  - [ ] **Yes** (Explain threat model and DPDPA compliance below)
- **Configuration**: Were environment variables or `.env.example` modified?
  - [ ] **No**
  - [ ] **Yes**

---

## 💥 Breaking Changes
- [ ] **No**
- [ ] **Yes** (Provide backward-compatibility rationale below)

---

## 📌 Additional Notes & Reviewer Sign-Off
<!-- Peer review rotation:
- Venkatesh -> Jasleen
- Jasleen -> Krishna
- Krishna -> Bhavya
- Bhavya -> Shravani
- Shravani -> Kimaya
- Kimaya -> Venkatesh
-->
- **Assigned Reviewer**: @
- **Reviewer Status**: [ ] Approved [ ] Changes Requested
