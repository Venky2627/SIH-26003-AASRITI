# AASRITI DEVELOPER ONBOARDING: GETTING STARTED
## `/docs/GETTING_STARTED_FOR_TEAM.md` — The Plain-Language Team Guide

> **WELCOME TO AASRITI!**  
> This guide is for all 6 teammates (Venkatesh, Jasleen, Krishna, Bhavya, Shravani, Kimaya) and any new contributor.  
> You do **NOT** need to be a Git expert to build AASRITI. Follow these 10 simple steps.

---

## 🚀 THE 10-STEP CONTRIBUTOR LIFECYCLE

```
[1. CLONE] ──► [2. IDENTIFY & BRANCH] ──► [3. RUN APP] ──► [4. WORK] ──► [5. TEST]
                                                                              │
[10. MERGE] ◄── [9. APPROVE] ◄── [8. REVIEW & CI] ◄── [7. PR] ◄── [6. COMMIT & PUSH]
```

---

### Step 1: CLONE THE REPOSITORY
Open your terminal (PowerShell or Bash) and clone the repo to your local machine:
```bash
git clone https://github.com/Venky2627/SIH-26003-AASRITI.git
cd SIH-26003-AASRITI
```

---

### Step 2: IDENTIFY YOURSELF TO ANTIGRAVITY & CREATE YOUR BRANCH
When you open Antigravity in this repository, simply type:
> *"I'm [Your Name], set me up."*  
*(Example: "I'm Kimaya, set me up. I want to work on Doctor Analytics.")*

Antigravity will automatically check out a clean working branch for you, following the canonical naming convention:
```bash
# General format: feature/<member>/<short-task-name>
git checkout -b feature/kimaya/doctor-analytics
```
**CRITICAL RULE**: Never write code directly on `main` or `develop`. Always work inside your own feature branch!

---

### Step 3: RUN THE APPLICATION
1. Open the project in **Android Studio Hedgehog / Iguana / Jellyfish**.
2. Let Gradle sync automatically (JDK 17 required).
3. Connect an Android phone via USB (or start an Android Virtual Device running API 34).
4. Click the green **Run (▶)** button.
5. AASRITI will install and launch with `Warm Ivory` backgrounds and high-contrast large buttons.

---

### Step 4: DO YOUR WORK (FOLLOW THE SYSTEM)
Before writing any UI or code, check the rules:
- **Changing UI?** Read [`/UI_RULES.md`](file:///UI_RULES.md) and [`/UI_COMPONENT_RULES.md`](file:///UI_COMPONENT_RULES.md).
- **Need Colors?** Use tokens from `AasritiColorTokens` (e.g. `DeepNortheastForest`, `WarmIvory`). Never invent hex codes!
- **Need Spacing?** Use `AasritiSpacing` (multiples of 4dp).
- **Patient Screen?** Touch targets must be $\ge 64\text{dp}$. No dashboards, no scores, no tiny text!

---

### Step 5: TEST YOUR CHANGES
Run the unit test suite before committing:
```bash
./gradlew testDebugUnitTest
```
Also test in **Airplane Mode**: Turn off your phone's Wi-Fi and Mobile Data. AASRITI must work 100% smoothly offline!

---

### Step 6: COMMIT & PUSH YOUR WORK
Create clear, descriptive commits:
```bash
git add .
git commit -m "feat(analytics): add 30-day reaction time trend chart for doctor review"
git push origin feature/kimaya/doctor-analytics
```

---

### Step 7: OPEN A PULL REQUEST (PR)
1. Go to the GitHub repository in your browser.
2. Click **"Compare & pull request"**.
3. Target branch: **`develop`** (or `main` as directed).
4. Fill out the pre-populated PR checklist from `.github/pull_request_template.md`.
5. Check off the systems you modified (Frontend, Database, UI, Security, etc.).

---

### Step 8: AUTOMATED CI RUNS
GitHub Actions will automatically run:
- Code checkout and Python ML verification
- Java 17 Gradle build and Unit Tests
- ProGuard syntax and APK assembly verification

If a check turns red (❌): Read the error log, fix the issue on your local branch, commit, and push again. The PR updates automatically.

---

### Step 9: HUMAN REVIEW & SIGN-OFF
Notify your designated secondary reviewer:
- **Venkatesh** $\leftrightarrow$ Reviewed by **Jasleen**
- **Jasleen** $\leftrightarrow$ Reviewed by **Krishna**
- **Krishna** $\leftrightarrow$ Reviewed by **Bhavya**
- **Bhavya** $\leftrightarrow$ Reviewed by **Shravani**
- **Shravani** $\leftrightarrow$ Reviewed by **Kimaya**
- **Kimaya** $\leftrightarrow$ Reviewed by **Venkatesh**

---

### Step 10: MERGE TO DEVELOP / MAIN
Once your reviewer approves and CI passes with green checkmarks (✅), click **Squash and Merge** (or Merge Commit). Congratulations, your code is now part of AASRITI!

---

## 🆘 TROUBLESHOOTING & EMERGENCY HELP

* **"I accidentally committed to main!"**
  - Don't panic. Run: `git branch my-backup-work`, then `git checkout develop`. Ask Venkatesh or Jasleen before pushing.
* **"Gradle sync is failing!"**
  - Make sure your Android Studio Gradle JDK is set to **Java 17** (File $\rightarrow$ Settings $\rightarrow$ Build, Execution, Deployment $\rightarrow$ Build Tools $\rightarrow$ Gradle $\rightarrow$ Gradle JDK $\rightarrow$ select JDK 17).
* **"I need to add a new dependency!"**
  - Stop and check [`/docs/TEAM_ALLOCATION.md`](file:///docs/TEAM_ALLOCATION.md). Modifying `build.gradle.kts` is a protected action requiring 2 approvals. Ask first!
