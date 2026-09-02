# SmritiSetu (SIH26003) — Cloud Ingestion API Contract (FastAPI)

## 📡 1. Interface Scope & Design Principles

While SmritiSetu executes 100% offline, this API contract defines the **Cloud Synchronization & Ingestion Interface** implemented in the FastAPI backend (`backend/app/main.py`).

### Key Contract Principles:
1. **Asynchronous Batch Ingestion**: Mobile devices upload batches of up to 50 records in a single payload to minimize battery drain over high-latency cellular connections.
2. **Strict Idempotency**: Every record submitted must carry an `idempotency_key`. Submitting the same key multiple times produces an identical `200 OK` response without duplicate database inserts.
3. **Data Minimization**: Endpoints strictly reject raw audio payloads, unencrypted PII, or un-hashed phone numbers.

---

## 🔐 2. Authentication & Authorization

All sync requests must include an asymmetric client token or pre-shared device token in the HTTP Header:
```http
Authorization: Bearer <JWT_DEVICE_TOKEN>
X-Device-Hardware-ID: <HARDWARE_UUID_HASH>
X-App-Version: 1.0.0-sih2025
```

---

## 📑 3. OpenAPI 3.1.0 Specification Snippets

```yaml
openapi: 3.1.0
info:
  title: SmritiSetu Clinical Telemetry Gateway
  description: Edge-to-Cloud sync interface for offline cognitive healthcare in NER India.
  version: 1.0.0
paths:
  /api/v1/health:
    get:
      summary: Lightweight health probe for ghost-connection detection
      responses:
        '200':
          description: Gateway is alive and connected to cloud PostgreSQL.
          content:
            application/json:
              schema:
                type: object
                properties:
                  status: { type: string, example: "HEALTHY" }
                  timestamp: { type: string, format: date-time }

  /api/v1/sync/batch:
    post:
      summary: Ingest batch of offline cognitive sessions and metrics
      parameters:
        - in: header
          name: X-Device-Hardware-ID
          required: true
          schema: { type: string }
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/SyncBatchRequest'
      responses:
        '200':
          description: Batch successfully ingested or deduplicated.
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/SyncBatchResponse'
        '400':
          description: Payload validation failed or PII detected.
        '401':
          description: Invalid device token.
        '422':
          description: Malformed JSON or unparseable timestamps.

components:
  schemas:
    SyncBatchRequest:
      type: object
      required: [batch_id, records]
      properties:
        batch_id:
          type: string
          format: uuid
          example: "a81bc81b-dead-4e5d-abff-90865d1e13b1"
        device_timestamp:
          type: string
          format: date-time
        records:
          type: array
          items:
            $ref: '#/components/schemas/SyncQueueRecord'

    SyncQueueRecord:
      type: object
      required: [idempotency_key, table_name, record_id, operation, payload]
      properties:
        idempotency_key:
          type: string
          example: "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        table_name:
          type: string
          enum: [patients, caregivers, consent_logs, game_sessions, cognitive_metrics]
        record_id:
          type: string
          format: uuid
        operation:
          type: string
          enum: [INSERT, UPDATE, DELETE]
        payload:
          type: object
          description: Key-value matching SQLite table columns

    SyncBatchResponse:
      type: object
      properties:
        status:
          type: string
          example: "SUCCESS"
        processed_count:
          type: integer
          example: 12
        synced_idempotency_keys:
          type: array
          items: { type: string }
        server_timestamp:
          type: string
          format: date-time
```

---

## 📤 4. Example JSON Ingestion Payload

```json
{
  "batch_id": "9f842345-38b4-4b55-a128-d748f2fa4189",
  "device_timestamp": "2025-09-02T04:20:00.000Z",
  "records": [
    {
      "idempotency_key": "c4ca4238a0b923820dcc509a6f75849b283efecc025f39d18e1e231e29388301",
      "table_name": "game_sessions",
      "record_id": "b3e94589-9821-4a33-8a71-d8204981fa01",
      "operation": "INSERT",
      "payload": {
        "id": "b3e94589-9821-4a33-8a71-d8204981fa01",
        "patient_id": "e8293041-0283-4921-9871-382910481234",
        "game_type": "SPEED_MATCH",
        "difficulty_level": 3,
        "duration_seconds": 180,
        "raw_score": 84.5,
        "clinical_normalized_score": 1.25,
        "completed_status": "COMPLETED",
        "notes": "Patient displayed sharp attention on handloom patterns.",
        "started_at": "2025-09-02T04:16:30.000Z",
        "finished_at": "2025-09-02T04:19:30.000Z"
      }
    },
    {
      "idempotency_key": "c81e728d9d4c2f636f067f89cc14862c1cd673b70e6797aab52ff71544dd7ec8",
      "table_name": "cognitive_metrics",
      "record_id": "4a739211-5321-477b-8bb1-e3749281a022",
      "operation": "INSERT",
      "payload": {
        "id": "4a739211-5321-477b-8bb1-e3749281a022",
        "session_id": "b3e94589-9821-4a33-8a71-d8204981fa01",
        "metric_type": "REACTION_TIME_MS",
        "metric_value": 720.5,
        "baseline_deviation": -0.4,
        "raw_context_json": "{\"target\":\"KAZIRANGA_RHINO\",\"correct\":true}",
        "recorded_at": "2025-09-02T04:17:15.000Z"
      }
    }
  ]
}
```

---

## 📶 5. OFFLINE BEHAVIOR SPECIFICATION (Cloud Ingestion API)

### 1. What happens when this feature runs with zero internet connectivity?
* No HTTP calls are executed against these endpoints.
* The mobile application interacts strictly with local SQLite repositories (`GameSessionRepository`, `CognitiveMetricRepository`).

### 2. What data is stored locally vs. requires cloud?
* **Stored Locally**: 100% of the session and metric records.
* **Requires Cloud**: Anonymized backup and long-term cohort analytics.

### 3. What is the fallback/degradation strategy if cloud is unreachable?
* Sync worker detects HTTP connection errors (e.g., `ECONNREFUSED`, `ETIMEDOUT`, 503 Service Unavailable).
* Backoff timer doubles the retry interval (`2^attempt * 30 seconds`, capped at 15 minutes).
* Records remain safely sealed in the local SQLite `sync_queue`.

### 4. How does the user know they're in offline mode? (UI indicators)
* If the sync fails, the mobile status header displays a neutral sync icon: 🔄 **"সংৰক্ষিত (Saved Locally)"**.
* No error popup is shown to the patient or caregiver.

### 5. How does data integrity survive app crashes during offline operation?
* The cloud API enforces a **Database Transaction Block** per batch. If 49 records succeed and the 50th throws an error, the entire batch transaction rolls back, and the client retries the batch intact.
