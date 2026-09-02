from datetime import datetime
from fastapi import APIRouter, Response, status

router = APIRouter()

@router.get("/health", status_code=status.HTTP_200_OK)
async def health_check():
    """
    Lightweight health check endpoint used by mobile clients to test
    genuine internet reachability before attempting batch sync uploads.
    """
    return {
        "status": "HEALTHY",
        "service": "smritisetu-cloud-gateway",
        "timestamp": datetime.utcnow().isoformat(),
        "dpda_compliance": "VERIFIED",
    }
