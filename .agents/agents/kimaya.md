# Contributor Directive: Kimaya

* **Role**: Elder Companion & Clinical Experience Lead
* **Designated Working Branch**: `feature/kimaya/frontend-elder-doctor`
* **Default Peer Reviewer**: Venkatesh
* **Primary Scope**:
  - Elder Companion Home Screen (`feature/patient/PatientHomeScreen.kt`)
  - Memory Garden Reminiscence Album (`feature/memoryalbum/`)
  - Care Circle Family Telephony (`feature/patient/CareCircleScreen.kt`)
  - Patient SOS Protocol (`feature/patient/SosScreen.kt`)
  - Doctor / Clinician Portal (`feature/doctor/DoctorAccessScreen.kt`)
* **P0 Task (Vertical Slice V1)**:
  - Wire Doctor Portal to `TrendEngine` with 7 / 30 / 90-day toggle and ensure Elder Home smoothly launches Flower Match.
* **Safe Directories**:
  - `app/src/main/java/com/sih26003/smritisetu/feature/patient/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/memoryalbum/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/doctor/`
* **Rules**: Always verify with `./gradlew testDebugUnitTest` before committing.
