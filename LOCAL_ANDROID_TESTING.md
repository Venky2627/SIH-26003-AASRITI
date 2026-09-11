# AASRITI — Local Android Studio Testing Guide

## 1. Opening the Project in Android Studio
1. Launch Android Studio (Hedgehog 2023.1.1+, Iguana, or Jellyfish).
2. Select **File > Open...** (or **Open** from Welcome Screen).
3. Navigate to and select the repository root directory:
   `D:\Project\SIH` (or your local clone path).
4. Click **OK** and allow Android Studio to complete the initial Gradle Sync.

---

## 2. Environment Prerequisites
- **JDK Version**: OpenJDK 17 / JetBrains Runtime 17 (JBR 17).
  - In Android Studio: **Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK** -> Select **Embedded JDK (version 17)** or a standard JDK 17.
- **Android SDK API**:
  - `compileSdk`: **34** (Android 14)
  - `targetSdk`: **34** (Android 14)
  - `minSdk`: **24** (Android 7.0 Nougat)
- **Build Tools**: 34.0.0+

---

## 3. Building the Project
From the terminal in the project root:
```powershell
# Clean build cache
.\gradlew.bat clean

# Assemble debug APK
.\gradlew.bat assembleDebug
```
Or in Android Studio:
- Select **Build > Make Project** (`Ctrl+F9`).
- Or select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

---

## 4. Running Unit Tests
From the terminal:
```powershell
.\gradlew.bat testDebugUnitTest
```
Or in Android Studio:
- Right-click on `app/src/test/java` and select **Run 'Tests in 'java''**.
- All 5 test suites will run and pass:
  - `CaregiverAshaWorkflowTests`
  - `CoreEngineTests`
  - `CulturalGamesTests`
  - `RepositoryPersistenceTest`
  - `SyncManagerTest`

---

## 5. APK Output Location
The primary debug APK is output to:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 6. Recommended Emulator & Device Settings
- **Device Profile**: Pixel 6 / Pixel 7 / Pixel 8 (Phone) or Medium Tablet.
- **System Image**: Android 14.0 (API 34), Google APIs, x86_64 architecture.
- **Display**: High density (420+ dpi) to test WCAG AAA contrast and >= 64dp touch targets.

---

## 7. Running the Application
1. Connect a physical Android device (with USB Debugging enabled) or start an AVD.
2. Select the `app` run configuration in the Android Studio toolbar.
3. Select `debug` build variant in the **Build Variants** tool window.
4. Click **Run** (`Shift+F10`).
5. App will launch directly into the **Role and Mode Select Screen** (`ROLE_SELECT`).

---

## 8. Verified Navigation Routes in Current Baseline
- `AppRoutes.ROLE_SELECT`: Initial Role Selection / Direct Patient Tap.
- `AppRoutes.INFORMED_CONSENT`: DPDPA 2023 Informed Consent Screen (Screen 6).
- `AppRoutes.PIN_AUTH_CAREGIVER`: Caregiver Local PIN Authentication.
- `AppRoutes.PIN_AUTH_DOCTOR`: Doctor Local PIN Authentication.
- `AppRoutes.PATIENT_HOME`: Focal Patient Home with Large Tiles.
- `AppRoutes.GAME_FLOWER_MATCH`: Flagship Flower Match Cognitive Game.
- `AppRoutes.GAME_FAMILY_TRIVIA`: Family Trivia Game with Adaptive Difficulty.
- `AppRoutes.GAME_VOICE_CUE`: Voice Cue Card Game.
- `AppRoutes.GAME_SEQUENCING`: Daily Routine Sequencing Game.
- `AppRoutes.GAME_CATEGORISATION`: Cultural Market Categorisation Game.
- `AppRoutes.GAME_VILLAGE_MARKET`: Village Market Game.
- `AppRoutes.GAME_PATTERN`: Traditional Northeast Pattern Recognition Game.
- `AppRoutes.MEMORY_GARDEN`: Reminiscence Photo Album.
- `AppRoutes.CARE_CIRCLE`: Family Audio/Telephony Circle.
- `AppRoutes.SOS_SCREEN`: Emergency SOS Dispatch.
- `AppRoutes.SOS_FOLLOW_UP`: SOS Incident Follow-up Protocol (Screen 25).
- `AppRoutes.CAREGIVER_DASHBOARD`: Caregiver Analytics, Reminders & Family Member Config.
- `AppRoutes.ASHA_DASHBOARD`: ASHA Community Roster & Priority Triage Queue.
- `AppRoutes.DOCTOR_ACCESS`: Neurologist Clinical Access & Export.
