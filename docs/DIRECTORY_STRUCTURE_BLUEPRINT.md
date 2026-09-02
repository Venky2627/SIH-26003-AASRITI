# SmritiSetu (SIH26003) — Complete Directory Structure & File Naming Blueprint

## 🌳 1. Complete File Tree (From Root to Depth 4)

```text
smritisetu-app/
├── .editorconfig                          # Global whitespace, indentation, and newline standards
├── .gitignore                             # Exhaustive multi-stack ignore rules (annotated)
├── CLAUDE.md                              # AI developer directive & architecture cheat sheet for Claude
├── CODE_OF_CONDUCT.md                     # Contributor covenant adapted for dementia clinical ethics
├── CONTRIBUTING.md                        # Collaboration rules, branch policies, and PR requirements
├── GEMINI.md                              # AI developer directive & architecture cheat sheet for Gemini
├── LICENSE                                # MIT Open Source License
├── README.md                              # Primary project pitch, offline matrix, and architecture
├── .github/
│   ├── ISSUE_TEMPLATE/
│   │   ├── bug_report.md                  # Structured bug reporting template
│   │   └── feature_request.md             # Feature request with MoSCoW prioritization
│   ├── pull_request_template.md           # PR checklist for offline-first & DPDA compliance
│   └── workflows/
│       └── ci-cd.yml                      # GitHub Actions automated lint, test, and APK build check
├── docs/
│   ├── ACCESSIBILITY_CHECKLIST.md         # WCAG 2.2 AAA guidelines, tremor filters, cataract contrast
│   ├── API_CONTRACT.md                    # FastAPI OpenAPI specifications & batch sync schema
│   ├── ARCHITECTURE.md                    # System architecture, data flow, and Mermaid diagrams
│   ├── DATABASE_SCHEMA.md                 # Complete SQLite / SQLCipher DDL, indexes, and sync_queue
│   ├── DIRECTORY_STRUCTURE_BLUEPRINT.md   # This folder blueprint and naming convention guide
│   ├── ETHICS_COMPLIANCE.md               # DPDA 2023 checklist, proxy consent form, CDSCO SaMD roadmap
│   ├── GAME_DESIGN_DOCUMENT.md            # Clinical evidence, game loops, scoring math, NER assets
│   ├── GIT_WORKFLOW.md                    # Branch hierarchy, PR review steps, rebase conflict protocol
│   ├── JUDGE_PREPARATION_KIT.md           # Grand jury 5-minute pitch script, top 10 Q&A, scoring rubrics
│   ├── OFFLINE_FIRST_STRATEGY.md          # 5 mission-critical offline edge cases & sync state machines
│   ├── ONBOARDING_GUIDE.md                # 15-minute quickstart guide for new developers
│   ├── RISK_REGISTER.md                   # Top 20 risks, probability/impact scores, and fallbacks
│   └── ROLE_RESPONSIBILITIES.md           # RACI responsibility matrix for all 6 team members
├── mobile-app/
│   ├── .env.example                       # Mobile environment variable template
│   ├── .eslintrc.js                       # Strict ESLint configuration for React Native
│   ├── .prettierrc                        # Prettier code formatting rules
│   ├── app.json                           # Expo SDK 50 configuration, permissions, and bundle ID
│   ├── babel.config.js                    # Babel presets and path alias resolver
│   ├── package.json                       # Mobile dependencies (Expo, ONNX, SQLite, Redux)
│   ├── tsconfig.json                      # Strict TypeScript compiler options and path mappings
│   ├── assets/
│   │   ├── audio/                         # Pre-recorded audio voice prompts
│   │   │   ├── prompts_as/                # Assamese voice prompts (.m4a bundled)
│   │   │   ├── prompts_br/                # Bodo voice prompts (.m4a bundled)
│   │   │   └── prompts_mn/                # Manipuri voice prompts (.m4a bundled)
│   │   ├── icons/                         # App adaptive icons and high-contrast SVG symbols
│   │   ├── images/                        # Cultural vector illustrations for games
│   │   │   ├── animals/                   # Kaziranga Rhino, Sangai Deer, Hornbill
│   │   │   └── culture/                   # Japi hat, Gamosa, Dokhona, Loktak boat
│   │   └── models/                        # Pre-packaged quantized AI models
│   │       └── indic_conformer_stub.onnx.gitkeep # Model placeholder for CI validation
│   └── src/
│       ├── components/                    # Reusable WCAG 2.2 AAA UI components
│       │   ├── common/                    # AccessibleButton, HighContrastText, TremorContainer
│       │   └── layout/                    # OfflineStatusBar, NavigationHeader
│       ├── database/                      # Local SQLite persistence layer
│       │   ├── migrations/                # Schema migration scripts
│       │   ├── repositories/              # PatientRepo, GameSessionRepo, SyncQueueRepo
│       │   └── connection.ts              # SQLCipher initialization & PRAGMA cipher setup
│       ├── features/                      # Domain-specific application features
│       │   ├── auth/                      # Caregiver PIN setup, biometric unlock
│       │   ├── caregiver/                 # 30-day trend chart, alert threshold configuration
│       │   ├── consent/                   # Spoken privacy notice, proxy consent signing
│       │   └── games/                     # The 3 clinical cognitive exercises
│       │       ├── speed-match/           # Visual processing speed engine
│       │       ├── story-weaver/          # Audio narrative recall & speech capture
│       │       └── picture-naming/        # Cultural object confrontation naming
│       ├── services/                      # Core device and hardware services
│       │   ├── ai/                        # ONNX Runtime IndicConformer inference & buffer zeroer
│       │   ├── audio/                     # 16kHz PCM audio capture streamer
│       │   └── sync/                      # Background sync worker & NetInfo monitor
│       ├── store/                         # Redux Toolkit global state management
│       │   ├── slices/                    # patientSlice, gameSlice, syncQueueSlice
│       │   └── index.ts                   # Store configuration with offline interceptors
│       ├── theme/                         # Cataract-resilient color palette and typography
│       └── utils/                         # Tremor debounce filters, math scoring formulas
├── backend/
│   ├── .env.example                       # Backend environment variable template
│   ├── Dockerfile                         # Python 3.11 container definition
│   ├── alembic.ini                        # Alembic database migration configuration
│   ├── requirements.txt                   # Pinned Python package dependencies
│   ├── alembic/
│   │   ├── env.py                         # Alembic runtime environment
│   │   └── versions/                      # Versioned PostgreSQL migration scripts
│   └── app/
│       ├── api/                           # FastAPI route controllers
│       │   └── v1/
│       │       ├── endpoints/             # health.py, sync.py, analytics.py
│       │       └── router.py              # Top-level API router registration
│       ├── core/                          # Security, JWT, and application settings
│       │   ├── config.py                  # Pydantic environment settings
│       │   └── security.py                # Token verification and hashing utilities
│       ├── db/                            # PostgreSQL session and base model definitions
│       │   └── session.py                 # Async SQLAlchemy engine and sessionmaker
│       ├── models/                        # SQLAlchemy database models
│       │   └── clinical_records.py        # Tables matching SQLite sync schemas
│       ├── schemas/                       # Pydantic request/response validation schemas
│       │   └── sync.py                    # SyncBatchRequest, SyncBatchResponse
│       └── main.py                        # FastAPI application entry point
└── infrastructure/
    ├── docker-compose.yml                 # Local dev stack: PostgreSQL 16 + Redis 7 + FastAPI
    └── .github/
        └── workflows/
            └── ci-cd.yml                  # Mirror of CI workflow for infrastructure repo sync
```

---

## 🏷️ 2. Folder Purpose & Ownership Annotations

| Folder Path | Primary Purpose | Primary Owner | Secondary Reviewer |
| :--- | :--- | :--- | :--- |
| `mobile-app/src/features/games/` | Contains the 3 clinical CST game engines, animation frames, and user loops. | `@mobile-games` | `@clinical-qa` |
| `mobile-app/src/services/ai/` | ONNX Runtime Mobile integration, audio memory zeroing, and ASR inference. | `@ai-edge` | `@lead-architect` |
| `mobile-app/src/database/` | SQLite / SQLCipher connection, DDL migration scripts, and transactional repos. | `@mobile-core` | `@lead-architect` |
| `mobile-app/src/store/` | Redux Toolkit slices, offline state persistence, and sync queue dispatchers. | `@mobile-core` | `@mobile-games` |
| `mobile-app/src/components/` | WCAG 2.2 AAA accessible UI components, high-contrast text, tremor wrappers. | `@clinical-qa` | `@mobile-core` |
| `backend/app/api/` | Cloud synchronization ingestion endpoints, health probes, and authentication. | `@backend-sync` | `@lead-architect` |
| `backend/app/models/` | PostgreSQL relational and time-series schemas for longitudinal telemetry. | `@backend-sync` | `@mobile-core` |
| `infrastructure/` | Docker Compose orchestration, container definitions, and CI/CD pipelines. | `@backend-sync` | `@lead-architect` |
| `docs/` | Comprehensive technical architecture, clinical evidence, and judging materials. | `@lead-architect` | All 6 Members |

---

## 🔤 3. File Naming Conventions by File Type

| Artifact / File Type | Convention | Suffix / Extension | Example |
| :--- | :--- | :--- | :--- |
| **React Components** | PascalCase | `.tsx` | `SpeedMatchCard.tsx`, `OfflineStatusBar.tsx` |
| **Hooks** | camelCase with `use` prefix | `.ts` | `useTremorFilter.ts`, `useNetworkStatus.ts` |
| **TypeScript Utilities / Services**| camelCase | `.ts` | `scoringAlgorithms.ts`, `indicConformerASR.ts` |
| **Redux Slices** | camelCase with `Slice` suffix | `.ts` | `patientSlice.ts`, `syncQueueSlice.ts` |
| **Database Repositories** | PascalCase with `Repository` suffix | `.ts` | `PatientRepository.ts`, `GameSessionRepository.ts` |
| **Feature Directories** | kebab-case | N/A | `speed-match/`, `story-weaver/`, `picture-naming/` |
| **Python Modules** | snake_case | `.py` | `sync_gateway.py`, `clinical_records.py` |
| **Documentation Files** | UPPERCASE_SNAKE_CASE | `.md` | `OFFLINE_FIRST_STRATEGY.md`, `SECURITY.md` |
| **Configuration Files** | Standard dotfiles or kebab | `.json`, `.js`, `.ini` | `app.json`, `alembic.ini`, `docker-compose.yml` |
