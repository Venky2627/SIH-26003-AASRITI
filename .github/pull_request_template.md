## 📌 Pull Request Overview

### Linked Issue
Fixes # (issue number)

### PR Summary
Briefly describe the architectural or feature changes introduced in this PR.

---

## 🚦 Pre-Merge Verification Checklist

### 📶 1. Offline-First Verification
- [ ] Tested in **Airplane Mode** with 100% zero network connectivity.
- [ ] Verified that no unexpected network errors or blocking spinners appear.
- [ ] Confirmed that all local SQLite writes succeed in Room and are queued in `sync_queue`.

### 🔒 2. Privacy & DPDA 2023 Compliance
- [ ] Confirmed **ZERO raw audio files** are written to persistent storage (`.wav`, `.mp3`).
- [ ] Audio streams processed in volatile memory only.
- [ ] No unencrypted PII or patient phone numbers written to database or logs.

### ♿ 3. Accessibility & WCAG 2.2 AAA
- [ ] All interactive touch targets are strictly $\ge 60\times 60\text{ dp}$ (prefer $64\times 64\text{ dp}$).
- [ ] Text contrast ratios satisfy $\ge 7:1$ (Cataract-resilient high contrast).
- [ ] Native audio prompts tested and working in Assamese / English.

### 🧪 4. Testing & Code Quality
- [ ] `./gradlew testDebugUnitTest` passes.
- [ ] `python scripts/train_decision_tree.py` passes.
- [ ] Kotlin compiles cleanly without compilation errors.
- [ ] Documentation updated in `docs/` if schema changed.

---

## 👥 Reviewer Sign-Off Required
- [ ] **Android Core Reviewer** (Room / UI changes)
- [ ] **AI / Edge ML Reviewer** (Decision Tree changes)
- [ ] **Lead Architect Reviewer**
