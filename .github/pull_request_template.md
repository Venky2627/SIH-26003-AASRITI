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
- [ ] Confirmed that all local SQLite writes succeed and are queued in `sync_queue`.

### 🔒 2. Privacy & DPDA 2023 Compliance
- [ ] Confirmed **ZERO raw audio files** are written to persistent storage (`.wav`, `.mp3`).
- [ ] In-memory PCM buffers are explicitly zeroed out (`buffer.fill(0)`) after inference.
- [ ] No unencrypted PII or raw phone numbers are written to database or logs.

### ♿ 3. Accessibility & WCAG 2.2 AAA
- [ ] All interactive touch targets are strictly $\ge 64\times 64\text{ dp}$.
- [ ] Text contrast ratios satisfy $\ge 7:1$ (Cataract-resilient high contrast).
- [ ] Rapid double taps from hand tremors are absorbed by debounce handlers.
- [ ] Native audio prompts tested and working in Assamese / Manipuri / Bodo.

### 🧪 4. Testing & Code Quality
- [ ] `npm run lint` passes with 0 errors.
- [ ] `npm test` passes all unit tests for clinical scoring formulas and Redux reducers.
- [ ] TypeScript compiles cleanly with zero `any` leaks.
- [ ] Documentation updated in `docs/` if schema or API contract changed.

---

## 👥 Reviewer Sign-Off Required
- [ ] **Mobile Core Reviewer** (`@mobile-core` for DB / Store changes)
- [ ] **AI / Edge ML Reviewer** (`@ai-edge` for ONNX / ASR changes)
- [ ] **Lead Architect Reviewer** (`@lead-architect`)
