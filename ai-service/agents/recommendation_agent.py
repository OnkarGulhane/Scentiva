from typing import List, Optional, Dict, Any
from tools.catalog_tools import search_products, get_product_by_id
from validators.guardrails import ScentivaGuardrails
from validators.response_validator import ResponseValidator
from models.schemas import PersonalizedRecommendationsRequest, PersonalizedRecommendationsResponse, VerifiedProductDto
from app_logging.logger import logger

class ScentivaRecommendationAgent:
    """
    Personalized Recommendations Agent.
    Analyzes user signals (viewed, wishlist, cart, past purchases) and generates
    grounded recommendations with editorial explanations.
    """

    def generate_recommendations(self, req: PersonalizedRecommendationsRequest) -> PersonalizedRecommendationsResponse:
        # Collect candidate IDs from user behaviour
        known_ids = set(req.viewedProductIds + req.wishlistProductIds + req.cartProductIds + req.purchasedProductIds)
        
        # 1. Look up families and brands from known products to form user profile
        preferred_families = []
        preferred_brands = []
        
        for p_id in known_ids:
            p = get_product_by_id(p_id)
            if p:
                preferred_families.extend(p.get("fragrance_families", []))
                preferred_brands.append(p.get("brand_name", ""))

        target_family = preferred_families[0] if preferred_families else None
        
        # 2. Fetch recommendations from real catalog
        query = f"{target_family or 'bestseller'} luxury"
        raw_candidates = search_products(query=query, limit=req.limit + len(known_ids))

        # Filter out already owned/cart items if enough candidates exist
        filtered_candidates = [c for c in raw_candidates if c["id"] not in known_ids]
        if not filtered_candidates:
            filtered_candidates = raw_candidates

        verified = ScentivaGuardrails.validate_and_filter_products(filtered_candidates[:req.limit])
        verified_dtos = ResponseValidator.sanitize_verified_products(verified)

        if target_family:
            rec_type = "BEHAVIOURAL_AFFINITY"
            headline = f"Because You Appreciate {target_family} Notes"
            explanation = f"Curated selections mirroring the noble olfactory nuances and sillage of your recent interactions."
        else:
            rec_type = "VAULT_ICONS"
            headline = "Connoisseur Vault Signatures"
            explanation = "Acclaimed master flacons celebrated globally for distinctive sillage and French & Italian artisan provenance."

        return PersonalizedRecommendationsResponse(
            recommendationType=rec_type,
            headline=headline,
            explanation=explanation,
            products=verified_dtos
        )

recommendation_agent = ScentivaRecommendationAgent()
