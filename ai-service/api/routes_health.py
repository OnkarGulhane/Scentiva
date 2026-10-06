import time
from fastapi import APIRouter
from config.settings import settings
from rag.vector_store import vector_store
from models.schemas import HealthStatusResponse

router = APIRouter(tags=["Health & Observability"])
START_TIME = time.time()

@router.get("/health", response_model=HealthStatusResponse)
async def health_check():
    """Service health and observability endpoint."""
    return HealthStatusResponse(
        status="UP",
        service=settings.APP_NAME,
        llmProvider=settings.LLM_PROVIDER,
        embeddingProvider=settings.EMBEDDING_PROVIDER,
        vectorStoreStatus="READY" if vector_store.total_documents > 0 else "INITIALIZING",
        documentsIndexed=vector_store.total_documents,
        uptimeSeconds=round(time.time() - START_TIME, 2)
    )
