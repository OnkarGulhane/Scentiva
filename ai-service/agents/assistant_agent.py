import re
from typing import List, Dict, Any, Optional
from langchain_core.messages import SystemMessage, HumanMessage, AIMessage
from core.llm_factory import get_llm
from prompts.assistant_prompts import SHOPPING_ASSISTANT_SYSTEM_PROMPT
from rag.retriever import retriever
from tools.catalog_tools import search_products, check_inventory_and_price
from validators.guardrails import ScentivaGuardrails
from validators.response_validator import ResponseValidator
from models.schemas import AssistantChatResponse, ExtractedShoppingPreferences, VerifiedProductDto
from app_logging.logger import logger

class ScentivaShoppingAssistantAgent:
    """
    Core AI Shopping Assistant Agent using LangChain.
    Extracts multi-lingual preferences, retrieves RAG context, queries real catalog,
    and returns verified products with structured actions.
    """
    
    def __init__(self):
        self.llm = get_llm()
        self.retriever = retriever

    def process_chat(
        self,
        message: str,
        conversation_history: Optional[List[Dict[str, str]]] = None,
        user_context: Optional[Dict[str, Any]] = None
    ) -> AssistantChatResponse:
        # 1. Guardrail Safety Check
        is_safe, safety_error = ScentivaGuardrails.inspect_prompt_safety(message)
        if not is_safe:
            return AssistantChatResponse(
                intent="safety_block",
                message=safety_error or "Query could not be processed safely.",
                reply=safety_error or "Query could not be processed safely.",
                extractedPreferences=ExtractedShoppingPreferences(),
                products=[],
                actions=[],
                suggestedFollowUps=["Show Best-Selling Perfumes", "Explore Fresh Fragrances"]
            )

        # 2. Extract Preferences & Intent (supports English & Marathi)
        prefs = self._extract_preferences(message)
        
        # 3. Retrieve RAG context
        rag_chunks = self.retriever.retrieve_context(query=message, k=3, source_type="product")
        rag_context = "\n---\n".join([chunk.content for chunk in rag_chunks]) if rag_chunks else ""

        # 4. Search Real Catalog
        catalog_matches = search_products(
            query=message,
            max_price=prefs.budget_max,
            fragrance_family=prefs.fragrance_family,
            gender=prefs.gender,
            occasion=prefs.occasion,
            limit=4
        )

        # 5. Hallucination Guard: Validate Products
        verified_products_raw = ScentivaGuardrails.validate_and_filter_products(catalog_matches)
        verified_dtos = ResponseValidator.sanitize_verified_products(verified_products_raw)
        actions = ResponseValidator.build_product_actions(verified_dtos)

        # 6. Generate Conversational Reply via LangChain LLM
        messages = [
            SystemMessage(content=SHOPPING_ASSISTANT_SYSTEM_PROMPT),
            HumanMessage(content=(
                f"User Request: '{message}'\n\n"
                f"Extracted Criteria: Occasion={prefs.occasion}, Budget={prefs.budget_max}, Family={prefs.fragrance_family}, Season={prefs.season}\n\n"
                f"Verified Catalog Matches:\n" +
                "\n".join([f"- {p.brandName} {p.name} (₹{p.price}, {p.concentration}): {p.reason}" for p in verified_dtos]) +
                f"\n\nRAG Perfumery Context:\n{rag_context}\n\n"
                f"Please compose an elegant, connoisseur recommendation reply in the user's language style."
            ))
        ]
        
        llm_output = self.llm.invoke(messages)
        reply_text = llm_output.content if hasattr(llm_output, "content") else str(llm_output)

        follow_ups = self._generate_follow_ups(prefs, verified_dtos)

        return AssistantChatResponse(
            intent="perfume_recommendation",
            message=reply_text,
            reply=reply_text,
            extractedPreferences=prefs,
            products=verified_dtos,
            actions=actions,
            suggestedFollowUps=follow_ups
        )

    def _extract_preferences(self, query: str) -> ExtractedShoppingPreferences:
        """Parse natural language query into structured preferences."""
        q_lower = query.lower()
        prefs = ExtractedShoppingPreferences()

        # Budget extraction (e.g. "under 2500", "₹5000 chya under", "below 10000")
        budget_match = re.search(r'(?:under|below|budget|chya\s+under|upto)\s*(?:₹|rs\.?|inr)?\s*(\d+)', q_lower)
        if budget_match:
            try:
                prefs.budget_max = float(budget_match.group(1))
            except ValueError:
                pass

        # Occasion extraction
        if any(w in q_lower for w in ["office", "work", "daily", "daily use", "formal", "meeting"]):
            prefs.occasion = "Work / Office"
        elif any(w in q_lower for w in ["party", "club", "night out", "pub"]):
            prefs.occasion = "Party"
        elif any(w in q_lower for w in ["date", "date night", "romantic", "dinner"]):
            prefs.occasion = "Date Night"
        elif any(w in q_lower for w in ["wedding", "gala", "special occasion", "reception"]):
            prefs.occasion = "Evening Gala"

        # Fragrance Family
        if any(w in q_lower for w in ["fresh", "citrus", "aquatic", "ocean", "marine"]):
            prefs.fragrance_family = "Fresh"
        elif any(w in q_lower for w in ["woody", "cedar", "sandalwood", "oud"]):
            prefs.fragrance_family = "Woody"
        elif any(w in q_lower for w in ["floral", "rose", "jasmine"]):
            prefs.fragrance_family = "Floral"
        elif any(w in q_lower for w in ["sweet", "vanilla", "gourmand"]):
            prefs.fragrance_family = "Sweet & Gourmand"
        elif any(w in q_lower for w in ["spicy", "oriental", "amber"]):
            prefs.fragrance_family = "Oriental"

        # Season
        if "summer" in q_lower:
            prefs.season = "Summer"
        elif "winter" in q_lower:
            prefs.season = "Winter"
        elif "monsoon" in q_lower or "rain" in q_lower:
            prefs.season = "Monsoon"

        # Gender
        if any(w in q_lower for w in ["for him", "men", "male", "husband", "boyfriend", "purush"]):
            prefs.gender = "For Him"
        elif any(w in q_lower for w in ["for her", "women", "female", "wife", "girlfriend", "mahila"]):
            prefs.gender = "For Her"

        return prefs

    def _generate_follow_ups(self, prefs: ExtractedShoppingPreferences, products: List[VerifiedProductDto]) -> List[str]:
        suggestions = []
        if products:
            suggestions.append(f"Tell me more about {products[0].name}")
        if prefs.occasion != "Party":
            suggestions.append("Show bold party perfumes for night wear")
        suggestions.append("What is your 7-day discovery sample policy?")
        suggestions.append("How do I track my active delivery?")
        return suggestions[:3]

shopping_assistant_agent = ScentivaShoppingAssistantAgent()
