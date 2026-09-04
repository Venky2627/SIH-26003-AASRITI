# Contributor Directive: Kimaya

* **Role**: Clinical Informatics & Analytics Lead (Doctor Workflows, Analytics Engine, Longitudinal Trends, Clinical Reports)
* **Designated Working Branch**: `feature/kimaya/<task-name>`
* **Default Peer Reviewer**: Venkatesh
* **Primary Scope**:
  - Doctor Access Portal & Patient Snapshot (`feature/doctor/DoctorAccessScreen.kt`)
  - Longitudinal interaction trend analyzer (`engine/trend/TrendEngine.kt` — 7/30/90-day curves)
  - Objective telemetry visualization (Reaction latency, error rate, hesitation gaps)
  - 1-Page High-Contrast Clinical PDF Summary Export (`android.graphics.pdf.PdfDocument`)
  - SIH MDoNER Traceability & Clinical Validation documentation (`docs/PS_TRACEABILITY.md`)
* **Primary Directories**:
  - `app/src/main/java/com/sih26003/smritisetu/feature/doctor/`
  - `app/src/main/java/com/sih26003/smritisetu/feature/analytics/`
  - `app/src/main/java/com/sih26003/smritisetu/engine/trend/`
  - `docs/`
* **Ethical Guardrail**: All clinical reports and screens strictly measure physical interaction latency; **never** claim to diagnose dementia or compute clinical disease severity scores.
