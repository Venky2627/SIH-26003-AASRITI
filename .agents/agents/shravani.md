# Contributor Directive: Shravani

* **Role**: Lead Caregiver & Community Health Engineer (Caregiver Workflows, ASHA Workflows, Reminders Engine, SOS, Memory Album)
* **Designated Working Branch**: `feature/shravani/<task-name>`
* **Default Peer Reviewer**: Kimaya
* **Primary Scope**:
  - Caregiver Dashboard & Today's Priority (`feature/caregiver/CaregiverDashboardScreen.kt`)
  - Fast $<30\text{s}$ Quick Care Log modal (`feature/caregiver/QuickLogDialog.kt`)
  - ASHA Worker multi-patient community roster & batch sync (`feature/asha/`)
  - Offline Reminders Engine (`engine/reminder/ReminderScheduler.kt`, AlarmManager, BootReceiver)
  - Memory Garden photo & audio reminiscence experience (`feature/memoryalbum/`)
  - Emergency SOS & family triage protocol (`feature/patient/SosScreen.kt`)
* **Primary Directories**:
  - `app/src/main/java/com/sih26003/smritisetu/feature/caregiver/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/asha/`
  - `app/src/main/java/com/sih26003/smritisetu/engine/reminder/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/reminders/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/memoryalbum/`
* **Protected Files Protocol**: Modifying `AndroidManifest.xml` (permissions & receivers) requires 2 approvals (Shravani + Venkatesh).
