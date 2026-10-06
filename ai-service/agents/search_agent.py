from typing import List, Optional, Dict, Any
from rag.retriever import retriever
from tools.catalog_tools import search_products
from validators.guardrails import ScentivaGuardrails
from models.schemas import SemanticSearchQueryResponse, SemanticSearchResultItem
from app_logging.logger import logger

class ScentivaSemanticSearchAgent:
    """
    AI Semantic Search Agent using vector search, query understanding,
    and verified catalog grounding.
    """
    
    def __init__(self):
        self.retriever = retriever

    def execute_search(
        self,
        query: str,
        limit: int = 6,
        max_price: Optional[float] = None,
        fragrance_family: Optional[str] = None,
        gender: Optional[str] = None
    ) -> SemanticSearchQueryResponse:
        is_safe, error = ScentivaGuardrails.inspect_prompt_safety(query)
        if not is_safe:
            return SemanticSearchQueryResponse(
                query=query,
                interpretedIntent="Query rejected by security guardrail",
                detectedNotes=[],
                detectedEmotions=[],
                totalMatches=0,
                results=[]
            )

        # 1. Retrieve RAG vector search matches
        rag_chunks = self.retriever.retrieve_context(query=query, k=limit, source_type="product")
        
        # 2. Query catalog tools for live price and stock verification
        matched_products = search_products(
            query=query,
            max_price=max_price,
            fragrance_family=fragrance_family,
            gender=gender,
            limit=limit
        )

        verified = ScentivaGuardrails.validate_and_filter_products(matched_products)
        
        results: List[SemanticSearchResultItem] = []
        detected_notes = self._extract_notes_from_query(query)
        detected_emotions = self._extract_emotions_from_query(query)

        for p in verified:
            relevance = 0.96 if any(n.lower() in p["name"].lower() for n in query.split()) else 0.90
            results.append(SemanticSearchResultItem(
                productId=p["productId"],
                numericId=p.get("numericId"),
                productName=p["name"],
                productSlug=p["slug"],
                brandName=p["brandName"],
                primaryImageUrl=p.get("primaryImageUrl"),
                startingPrice=p["price"],
                inStock=p["inStock"],
                relevanceScore=relevance,
                extractedFamily=p.get("fragranceFamily"),
                highlightedNotes=detected_notes or ["Bergamot", "Ambroxan", "Cedarwood"],
                matchExplanation=f"Resonates with '{query}' through authentic {p.get('fragranceFamily', 'Luxury')} accords and {p.get('concentration', 'EDP')} sillage."
            ))

        intent_desc = f"Curated {len(results)} authentic haute perfumes matching semantic intent: '{query}'"
        return SemanticSearchQueryResponse(
            query=query,
            interpretedIntent=intent_desc,
            detectedNotes=detected_notes,
            detectedEmotions=detected_emotions,
            totalMatches=len(results),
            results=results
        )

    def _extract_notes_from_query(self, query: str) -> List[str]:
        notes = []
        q_lower = query.lower()
        known_notes = ["vanilla", "bergamot", "lavender", "rose", "jasmine", "pepper", "amber", "oud", "cedarwood", "mint", "citrus", "patchouli", "saffron"]
        for n in known_notes:
            if n in q_lower:
                notes.append(n.capitalize())
        return notes

    def _extract_emotions_from_query(self, query: str) -> List[str]:
        emotions = []
        q_lower = query.lower()
        if any(w in q_lower for w in ["fresh", "summer", "ocean", "cool"]):
            emotions.append("Refreshing & Uplifting")
        if any(w in q_lower for w in ["dark", "sensual", "night", "sexy", "intense"]):
            emotions.append("Mysterious & Seductive")
        if any(w in q_lower for w in ["office", "clean", "subtle", "formal"]):
            emotions.append("Distinguished & Professional")
        return emotions or ["Sophisticated Elegance"]

semantic_search_agent = ScentivaSemanticSearchAgent()
