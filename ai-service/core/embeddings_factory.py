import hashlib
import math
from typing import List
from langchain_core.embeddings import Embeddings
from config.settings import settings
from app_logging.logger import logger

class DeterministicScentivaEmbeddings(Embeddings):
    """
    Deterministic vector embedding generator for local verification,
    consistent RAG cosine similarity testing, and zero external dependency operation.
    """
    
    def __init__(self, dimension: int = 1536):
        self.dimension = dimension

    def _embed_text(self, text: str) -> List[float]:
        # Generate deterministic pseudo-random float vector based on sha256 hash
        clean = text.strip().lower()
        vec = [0.0] * self.dimension
        
        # Word token distribution
        words = clean.split()
        for idx, word in enumerate(words):
            h = int(hashlib.md5(word.encode('utf-8')).hexdigest(), 16)
            pos = h % self.dimension
            vec[pos] += 1.0 / (idx + 1.0)
            
        # Character n-gram distribution for semantic overlap
        for i in range(len(clean) - 2):
            trigram = clean[i:i+3]
            h = int(hashlib.sha256(trigram.encode('utf-8')).hexdigest()[:8], 16)
            pos = h % self.dimension
            vec[pos] += 0.5
            
        # Normalize to unit vector
        norm = math.sqrt(sum(x * x for x in vec))
        if norm > 0:
            return [x / norm for x in vec]
        return vec

    def embed_documents(self, texts: List[str]) -> List[List[float]]:
        return [self._embed_text(t) for t in texts]

    def embed_query(self, text: str) -> List[float]:
        return self._embed_text(text)

def get_embeddings() -> Embeddings:
    """Instantiate and return the configured LangChain Embeddings model."""
    provider = settings.EMBEDDING_PROVIDER.lower()
    
    if provider == "openai" and settings.EMBEDDING_API_KEY:
        try:
            from langchain_community.embeddings import OpenAIEmbeddings
            logger.info(f"Initializing OpenAIEmbeddings model={settings.EMBEDDING_MODEL}")
            return OpenAIEmbeddings(
                model=settings.EMBEDDING_MODEL,
                api_key=settings.EMBEDDING_API_KEY
            )
        except Exception as e:
            logger.warning(f"Failed to initialize OpenAIEmbeddings ({e}), falling back to deterministic embeddings")
            return DeterministicScentivaEmbeddings(dimension=settings.EMBEDDING_DIMENSION)
            
    return DeterministicScentivaEmbeddings(dimension=settings.EMBEDDING_DIMENSION)
