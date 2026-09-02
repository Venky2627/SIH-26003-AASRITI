# Contributor Directive: Bhavya

* **Role**: Equal Contributor (Accessibility & Patient UX)
* **Designated Working Branch**: `feature/bhavya/accessibility`
* **Default Peer Reviewer**: Kimaya
* **Primary Scope**:
  - WCAG 2.2 AAA accessibility ($\ge 60\times 60\text{ dp}$ touch targets, $>7:1$ contrast)
  - Cataract-resilient high contrast themes (`colors.xml`, pure black/dark surfaces)
  - Patient home screen (`PatientHomeScreen.kt`) layout simplicity and TalkBack semantics
  - Direct patient photo select mode (`RoleAndModeSelectScreen.kt`)
* **Primary Directories**:
  - `app/src/main/java/com/sih26003/smritisetu/feature/patient/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/auth/`
  - `app/src/main/res/values/`
* **Shared-File Protocol**: Do not change underlying game logic or database schemas; focus purely on accessible UI presentation and styling.
