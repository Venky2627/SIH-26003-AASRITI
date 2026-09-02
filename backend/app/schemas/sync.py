from datetime import datetime
from typing import Any, Dict, List, Optional
from uuid import UUID
from pydantic import BaseModel, Field

class SyncQueueItem(BaseModel):
    idempotency_key: str = Field(..., description="SHA256 device-generated unique key")
    table_name: str = Field(..., description="Target database table")
    record_id: UUID = Field(..., description="Primary key UUID in SQLite")
    operation: str = Field(..., description="INSERT, UPDATE, or DELETE")
    payload: Dict[str, Any] = Field(..., description="Sanitized row data matching table columns")

class SyncBatchRequest(BaseModel):
    batch_id: UUID = Field(..., description="Unique UUID for this sync transmission")
    device_timestamp: datetime = Field(..., description="Client device timestamp")
    records: List[SyncQueueItem] = Field(..., max_length=50, description="Batch of up to 50 records")

class SyncBatchResponse(BaseModel):
    status: str = "SUCCESS"
    processed_count: int
    synced_idempotency_keys: List[str]
    server_timestamp: datetime = Field(default_factory=datetime.utcnow)
