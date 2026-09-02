# SmritiSetu (SIH26003) — Developer Onboarding Guide

Welcome to the SmritiSetu core engineering team! This guide will get your local environment configured and running your first test build in **under 15 minutes**.

---

## 🛠️ Step 1: Install System Prerequisites

Ensure the following tools are installed on your host OS (Windows, macOS, or Linux):
1. **Node.js**: Install Node.js LTS `v20.x` (or `v18.18+`) from [nodejs.org](https://nodejs.org/).
2. **Git**: Verify `git --version` (v2.30+).
3. **Java Development Kit**: Install OpenJDK 17 (Temurin recommended).
4. **Android Studio**:
   * Install Android SDK 34 (`Android 14.0 UpsideDownCake`).
   * Install Android SDK Command-line Tools & Android NDK (Side by side).
   * Configure environment variables:
     * Windows: Set `ANDROID_HOME=C:\Users\<user>\AppData\Local\Android\Sdk` and add `%ANDROID_HOME%\platform-tools` to PATH.
     * macOS/Linux: Set `export ANDROID_HOME=$HOME/Android/Sdk` in `~/.bashrc` or `~/.zshrc`.
5. **Python (Optional for Backend/ML)**: Install Python 3.11.

---

## 📥 Step 2: Clone the Repository

```bash
git clone https://github.com/TeamNameSIH/AI-Based-Cognitive-Gaming-Platform-for-Elderly-Dementia-Patients-in-NER.git
cd AI-Based-Cognitive-Gaming-Platform-for-Elderly-Dementia-Patients-in-NER
```

---

## ⚙️ Step 3: Configure Environment Variables

```bash
# Mobile client configuration
cp mobile-app/.env.example mobile-app/.env

# Backend configuration (if working on cloud sync)
cp backend/.env.example backend/.env
```

Review `mobile-app/.env` and ensure `EXPO_PUBLIC_SQLCIPHER_DEV_KEY` is set.

---

## 📦 Step 4: Install Dependencies & Run Mobile Client

```bash
# Navigate to mobile-app directory
cd mobile-app

# Install all dependencies
npm install

# Start Expo development server
npx expo start
```

### Running on Physical Android Device (Recommended for Speech & Sensors):
1. Enable **Developer Options** and **USB Debugging** on your Android phone.
2. Connect phone via USB (`adb devices` should list your device).
3. Run:
   ```bash
   npx expo run:android
   ```

---

## 🧪 Step 5: Verify Tests & Linting

Before writing code, verify that the existing test suite and linter pass cleanly:
```bash
# Run ESLint check
npm run lint

# Run Jest unit test suite
npm test
```

---

## 🌿 Step 6: Create Your First Feature Branch & Commit

Following our Git standard:
```bash
# Ensure develop branch is up to date
git checkout develop
git pull origin develop

# Create a scoped feature branch
git checkout -b feat/42-speed-match-grid-layout

# Make code changes, then stage and commit following Conventional Commits
git add .
git commit -m "feat(games/speed-match): implement 2x2 high contrast grid layout"

# Push to origin
git push origin feat/42-speed-match-grid-layout
```
Open a Pull Request on GitHub targeting `develop`.
