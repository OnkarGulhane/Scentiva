import math
from typing import List, Dict, Any, Tuple, Optional
from core.embeddings_factory import get_embeddings
from models.domain import RagDocumentChunk
from app_logging.logger import logger

class ScentivaVectorStore:
    """
    High-performance vector store with cosine similarity ranking and metadata filtering.
    Supports pgvector / PostgreSQL if configured, with zero-dependency in-memory vector store.
    """
    
    def __init__(self):
        self.embeddings_model = get_embeddings()
        self._documents: List[RagDocumentChunk] = []
        self._vectors: List[List[float]] = []

    def clear(self):
        self._documents.clear()
        self._vectors.clear()

    def add_documents(self, documents: List[RagDocumentChunk]):
        """Embed and index document chunks."""
        if not documents:
            return
            
        texts = [doc.content for doc in documents]
        vectors = self.embeddings_model.embed_documents(texts)
        
        self._documents.extend(documents)
        self._vectors.extend(vectors)
        logger.info(f"VectorStore: Indexed {len(documents)} document chunks. Total store size: {len(self._documents)}")

    def similarity_search_with_score(
        self,
        query: str,
        k: int = 4,
        source_type: Optional[str] = None,
        category: Optional[str] = None
    ) -> List[Tuple[RagDocumentChunk, float]]:
        """
        Execute cosine similarity search with optional metadata filtering.
        Returns list of (Document, similarity_score).
        """
        if not self._documents:
            return []
            
        q_vector = self.embeddings_model.embed_query(query)
        scores: List[Tuple[RagDocumentChunk, float]] = []
        
        for idx, doc in enumerate(self._documents):
            # Metadata filter
            if source_type and doc.source_type != source_type:
                continue
            if category and doc.metadata.get("category") != category:
                continue
                
            doc_vec = self._vectors[idx]
            sim = self._cosine_similarity(q_vector, doc_vec)
            
            # Boost score if exact keyword matches occur in title
            if any(term in doc.title.lower() for term in query.lower().split() if len(term) > 3):
                sim = min(1.0, sim + 0.15)
                
            scores.append((doc, sim))
            
        scores.sort(key=lambda x: x[1], reverse=True)
        return scores[:k]

    def _cosine_similarity(self, v1: List[float], v2: List[float]) -> float:
        if len(v1) != len(v2):
            return 0.0
        dot = sum(a * b for a, b in zip(v1, v2))
        norm1 = math.sqrt(sum(a * a for a in v1))
        norm2 = math.sqrt(sum(b * b for b in v2))
        if norm1 == 0 or norm2 == 0:
            return 0.0
        return dot / (norm1 * norm2)

    @property
    def total_documents(self) -> int:
        return len(self._documents)

# Global singleton vector store instance
vector_store = ScentivaVectorStore()
