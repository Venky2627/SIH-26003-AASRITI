# Rule 00: Core Rules & Equality Principle

1. **Six Equal Contributors**:
   - Venkatesh, Jasleen, Krishna, Shravani, Bhavya, Kimaya.
   - All members contribute code, write tests, and review pull requests.
   - No contributor is a boss, lead architect, or junior backup. Review responsibility is a quality gate, not a rank.

2. **Absolute Architecture Priority**:
   - Room SQLite is the **single source of truth** (`"Room is King, Firebase is the Messenger"`).
   - All gameplay, metrics, and local reminders must function 100% offline (Airplane Mode).
   - Zero OTP / SMS authentication: Caregiver/Doctor use local 6-digit PIN; Patients use zero PIN (direct photo tap).

3. **No AI Slop / No Fabricated Features**:
   - Do not generate fake dashboards or placeholder buttons.
   - Metrics must originate from real physical interactions (touch timestamps, error counts, idle latency).
   - Never claim dementia diagnosis or severity scoring; use *"Recommended next difficulty"*.
