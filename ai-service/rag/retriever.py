import re
from typing import List, Optional, Dict, Any, Tuple
from rag.vector_store import vector_store
from models.domain import RagDocumentChunk
from app_logging.logger import logger

# Marathi to English fragrance domain dictionary for intent parsing
MARATHI_PERFUMERY_MAP = {
    "office": ["office", "workplace", "professional", "subtle"],
    "summer": ["summer", "fresh", "citrus", "aquatic", "hot weather"],
    "winter": ["winter", "warm", "spicy", "oriental", "vanilla"],
    "party": ["party", "bold", "seductive", "strong sillage", "night"],
    "fresh": ["fresh", "citrus", "bergamot", "marine", "aquatic"],
    "sweet": ["sweet", "vanilla", "gourmand", "tonka"],
    "long lasting": ["long lasting", "extrait", "parfum", "12 hours"],
    "sathi": [],
    "pahije": ["recommend", "looking for", "want"],
    "madhe": [],
    "kiti": ["price", "cost"],
    "under": ["max price", "budget"],
    "chya": []
}

class ScentivaRetriever:
    """
    Intelligent multi-lingual retriever for Scentiva RAG knowledge.
    Translates Marathi-English colloquial queries and retrieves relevant context.
    """
    
    def __init__(self):
        self.vector_store = vector_store

    def retrieve_context(
        self,
        query: str,
        k: int = 4,
        source_type: Optional[str] = None,
        category: Optional[str] = None
    ) -> List[RagDocumentChunk]:
        """Retrieve most relevant chunks for user query."""
        expanded_query = self._expand_query(query)
        results_with_score = self.vector_store.similarity_search_with_score(
            query=expanded_query,
            k=k,
            source_type=source_type,
            category=category
        )
        return [doc for doc, score in results_with_score]

    def _expand_query(self, query: str) -> str:
        """Expand and normalize query with Marathi and perfume terminology."""
        q_lower = query.lower()
        expanded_terms = [q_lower]
        
        # Check budget expressions like 'under ₹2500' or '2500 chya under'
        budget_match = re.search(r'(?:under|below|budget|chya\s+under)\s*(?:₹|rs\.?|inr)?\s*(\d+)', q_lower)
        if budget_match:
            expanded_terms.append(f"budget under {budget_match.group(1)} INR")
            
        for marathi_word, english_synonyms in MARATHI_PERFUMERY_MAP.items():
            if marathi_word in q_lower:
                expanded_terms.extend(english_synonyms)
                
        return " ".join(set(expanded_terms))

# Global retriever singleton
retriever = ScentivaRetriever()
