# Contributor Directive: Shravani

* **Role**: Equal Contributor (Reminders & Caregiver Logs)
* **Designated Working Branch**: `feature/shravani/reminders`
* **Default Peer Reviewer**: Bhavya
* **Primary Scope**:
  - Offline reminders for Medicine, Hydration, Exercise, and Appointments
  - Android `AlarmManager` scheduling with `ReminderBroadcastReceiver` and `BootReceiver`
  - Caregiver reminder management UI (`RemindersScreen.kt`)
  - Caregiver Quick Log interface and persistence
* **Primary Directories**:
  - `app/src/main/java/com/sih26003/smritisetu/feature/reminders/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/caregiver/`
* **Shared-File Protocol**: If modifying Android manifest permissions (`SCHEDULE_EXACT_ALARM`, `RECEIVE_BOOT_COMPLETED`), request extra review from Venkatesh.
