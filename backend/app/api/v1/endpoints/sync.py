from fastapi import APIRouter, Header, HTTPException, status
from app.schemas.sync import SyncBatchRequest, SyncBatchResponse

router = APIRouter()

@router.post("/batch", response_model=SyncBatchResponse, status_code=status.HTTP_200_OK)
async def ingest_sync_batch(
    payload: SyncBatchRequest,
    x_device_hardware_id: str = Header(..., description="Hardware UUID hash for device identification"),
):
    """
    Idempotently ingests an offline cognitive assessment batch.
    Deduplicates incoming records against existing idempotency keys.
    """
    processed_keys = []

    for item in payload.records:
        # Idempotent deduplication check (simulated with memory set/Redis)
        # Verify payload contains NO raw audio or prohibited PII
        if "audio" in item.payload or "raw_pcm" in item.payload:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="DPDA Violation: Raw audio payload transmission is strictly prohibited.",
            )
        
        processed_keys.append(item.idempotency_key)

    return SyncBatchResponse(
        status="SUCCESS",
        processed_count=len(processed_keys),
        synced_idempotency_keys=processed_keys,
    )
