# AASRITI (SIH-26003) — Architectural Decision Records (ADRs)

> This document captures the rationale, tradeoffs, and formal justification for key architectural decisions made by the engineering team.

---

## ADR 001: 100% Native Android (Kotlin + Jetpack Compose) vs Hybrid / Cross-Platform

* **Context**: Rural North Eastern India features predominantly budget Android devices (Android 8.0 to Android 14) with limited RAM (2GB–4GB) and low GPU throughput.
* **Decision**: Adopt 100% Native Kotlin with Jetpack Compose. Permanently reject React Native, Expo, Flutter, and WebViews.
* **Rationale**:
  - Direct hardware access to native Android `AlarmManager.setExactAndAllowWhileIdle` for critical offline medicine reminders.
  - Sub-millisecond touch event timestamping critical for accurate cognitive hesitation detection ($>3500\text{ms}$).
  - Minimal APK binary footprint and zero JavaScript bridge memory overhead.
* **Consequences**: Fast 60 FPS UI rendering, reliable background alarm scheduling, zero cross-platform dependency drift.

---

## ADR 002: Room SQLite as Source of Truth vs Cloud-First Realtime Database

* **Context**: Elderly patients and ASHA health workers frequently operate in zero-connectivity hilly terrains without 2G/4G coverage.
* **Decision**: Enforce **"Room is King, Firebase is the Messenger"**. Every read and write transaction occurs synchronously in local SQLite first.
* **Rationale**:
  - The application is 100% operational in Airplane Mode. Gameplay never hangs waiting for network round-trips.
  - Zero data loss: All mutations write to a local `sync_queue` table and drain opportunistically in the background when connectivity returns.
* **Consequences**: Resilient offline functionality; slightly more local storage logic (managed via Android Architecture Components).

---

## ADR 003: On-Device Scikit-Learn Decision Tree vs Cloud LLMs / Heavy Deep Learning

* **Context**: Cognitive gameplay requires dynamic difficulty adjustment (Levels 1–5) based on real-time elderly performance.
* **Decision**: Train a scikit-learn `DecisionTreeClassifier` in Python, export its tree structure to JSON, and execute local inference in Kotlin via `DecisionTreeEngine.kt`. Completely ban cloud LLMs for core game logic.
* **Rationale**:
  - **Zero Latency**: Local recursive tree evaluation takes $<1\text{ms}$ on low-end ARM CPUs.
  - **Zero Cloud Cost**: Operates 100% offline without recurring API token bills.
  - **Explainability**: Decision thresholds (e.g. `reactionTime > 3200ms && accuracy < 0.70 -> Level 2`) are fully auditable by medical evaluators, unlike black-box LLM hallucinations.
* **Consequences**: Deterministic, reproducible, medically defensible adaptive scaling.

---

## ADR 004: Local PIN & Direct Photo Tap vs Phone OTP / SMS Gateways

* **Context**: Rural elderly individuals cannot navigate SMS OTPs, while network latency causes SMS timeouts in NER hills.
* **Decision**: Caregivers, ASHA workers, and Doctors use local 6-digit PINs (SHA-256 in Room). Patients authenticate with **zero PIN** (tapping their personal photo/avatar).
* **Rationale**:
  - Eliminates the single largest barrier to elderly technology adoption (passwords/OTPs).
  - Eliminates reliance on paid third-party SMS telecom gateways (e.g. Twilio, MSG91).
* **Consequences**: Instantaneous offline login; frictionless access for elderly users.

---

## ADR 005: Decoupled Cultural Theme Engine vs UI Language Strings

* **Context**: The North Eastern Region consists of diverse ethnic identities (Assamese, Manipuri, Khasi, Garo, Bodo, Mizo). Cultural familiarity does not strictly correlate with language preference.
* **Decision**: Architect the `CulturalThemeEngine` independently from UI localization.
* **Rationale**:
  - An Assamese elder whose family prefers an English UI still engages with familiar regional assets (Assam tea, Japi, Bihu haat).
  - Enables modular addition of new regional theme packs (e.g. Tripura, Sikkim) without altering core gameplay logic.
* **Consequences**: Clean separation of cultural assets from localization strings.

---

## ADR 006: Dedicated ASHA Field Module vs Reusing Caregiver Screens

* **Context**: Accredited Social Health Activists (ASHA) visit 10–20 elderly households weekly, managing multiple patients on a single shared government smartphone or tablet.
* **Decision**: Create a dedicated `feature/asha/` module with fast multi-patient switching and batch offline registers.
* **Rationale**:
  - Caregiver screens assume a 1:1 relationship with a family member and emphasize daily emotional reassurance.
  - ASHA workers require a multi-patient triage roster, rapid observation logging, and communal health reporting.
* **Consequences**: High operational relevance for public healthcare evaluation in SIH.
