from fastapi import APIRouter, HTTPException
from models.schemas import IngestKnowledgeResponse
from services.rag_service import rag_service
from rag.vector_store import vector_store
from app_logging.logger import logger

router = APIRouter(prefix="/rag", tags=["RAG Ingestion & Vector Management"])

@router.post("/ingest", response_model=IngestKnowledgeResponse)
async def trigger_rag_ingestion():
    """
    Triggers full re-indexing of Scentiva approved products, brands, policies, and FAQs into vector store.
    """
    try:
        result = rag_service.ingest_knowledge_base()
        return IngestKnowledgeResponse(
            success=True,
            totalDocumentsIngested=result["total_documents"],
            categories=result["categories"],
            timestamp=result["timestamp"]
        )
    except Exception as e:
        logger.error(f"RAG ingestion failed: {e}")
        raise HTTPException(status_code=500, detail=f"RAG ingestion failure: {str(e)}")

@router.get("/stats")
async def get_rag_stats():
    """Returns current vector store document counts."""
    return {
        "status": "ready",
        "totalDocumentsIndexed": vector_store.total_documents
    }
