from datetime import datetime
from typing import Dict, Any
from rag.ingestion import DocumentIngestionPipeline
from rag.vector_store import vector_store
from app_logging.logger import logger

class RagService:
    """Service managing RAG knowledge ingestion and re-indexing."""

    @staticmethod
    def ingest_knowledge_base() -> Dict[str, Any]:
        """Ingests products, brands, policies, and FAQs into vector storage."""
        pipeline = DocumentIngestionPipeline()
        chunks = pipeline.load_and_chunk_all()
        
        vector_store.clear()
        vector_store.add_documents(chunks)
        
        categories: Dict[str, int] = {}
        for c in chunks:
            categories[c.source_type] = categories.get(c.source_type, 0) + 1
            
        logger.info(f"RAG Knowledge Ingestion complete: {len(chunks)} total documents indexed across {categories}")
        return {
            "total_documents": len(chunks),
            "categories": categories,
            "timestamp": datetime.utcnow().isoformat() + "Z"
        }

rag_service = RagService()
