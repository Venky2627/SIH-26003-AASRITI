from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.api.v1.endpoints import health, sync

app = FastAPI(
    title="SmritiSetu Cloud Sync Gateway",
    description="Asynchronous cloud ingestion interface for offline cognitive healthcare in Northeast India (SIH26003).",
    version="1.0.0",
)

# Cross-Origin Resource Sharing (CORS)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Route registrations
app.include_router(health.router, prefix="/api/v1", tags=["Health"])
app.include_router(sync.router, prefix="/api/v1/sync", tags=["Synchronization"])

@app.get("/")
async def root():
    return {
        "message": "Welcome to SmritiSetu Cloud Gateway",
        "docs": "/docs",
        "sponsoring_ministry": "Ministry of Development of North Eastern Region (MDoNER)",
    }
