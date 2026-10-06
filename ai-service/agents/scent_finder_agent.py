from typing import List, Optional, Dict, Any
from tools.catalog_tools import search_products
from validators.guardrails import ScentivaGuardrails
from models.schemas import ScentFinderQuizRequest, ScentFinderQuizResponse, ScentFinderRecommendationItem
from app_logging.logger import logger

class ScentivaScentFinderAgent:
    """
    AI Scent Finder Agent.
    Evaluates olfactory quiz answers, assigns a high-fashion persona,
    and returns 3-5 grounded recommendations from real catalog.
    """

    def evaluate_quiz(self, quiz: ScentFinderQuizRequest) -> ScentFinderQuizResponse:
        family = quiz.fragranceFamily or "Woody"
        occasion = quiz.occasion or "Daily Signature"
        gender = quiz.gender or "Unisex"

        # Search matching products from actual catalog
        raw_matches = search_products(
            query=f"{family} {occasion} {' '.join(quiz.preferredNotes)}",
            fragrance_family=family if family != "All" else None,
            gender=gender if gender != "Unisex" else None,
            occasion=occasion if occasion != "All" else None,
            limit=4
        )

        verified = ScentivaGuardrails.validate_and_filter_products(raw_matches)

        recommendations: List[ScentFinderRecommendationItem] = []
        score = 98
        for p in verified:
            recommendations.append(ScentFinderRecommendationItem(
                productId=p["productId"],
                numericId=p.get("numericId"),
                productName=p["name"],
                productSlug=p["slug"],
                brandName=p["brandName"],
                primaryImageUrl=p.get("primaryImageUrl"),
                startingPrice=p["price"],
                inStock=p["inStock"],
                matchScore=score,
                recommendationReason=f"Balances {p['brandName']} signature artistry with vibrant {family} notes, ideal for {occasion}.",
                matchedNotes=quiz.preferredNotes or ["Bergamot", "Vanilla", "Amber"],
                idealOccasion=occasion
            ))
            score = max(82, score - 4)

        persona_title = self._compute_persona_title(family, occasion)
        persona_desc = self._compute_persona_description(family, occasion)

        return ScentFinderQuizResponse(
            personaTitle=persona_title,
            personaDescription=persona_desc,
            dominantAccord=f"Noble {family} & Artisanal Sillage",
            recommendations=recommendations
        )

    def _compute_persona_title(self, family: str, occasion: str) -> str:
        fam_lower = family.lower()
        if "woody" in fam_lower:
            return "The Grand Sovereign"
        elif "floral" in fam_lower:
            return "The Luminous Muse"
        elif "oriental" in fam_lower:
            return "The Midnight Enigma"
        elif "fresh" in fam_lower or "aquatic" in fam_lower:
            return "The Azure Visionary"
        elif "sweet" in fam_lower or "gourmand" in fam_lower:
            return "The Sensual Connoisseur"
        return "The Modern Aristocrat"

    def _compute_persona_description(self, family: str, occasion: str) -> str:
        return (
            f"Your olfactory profile reveals a refined affinity for {family} harmonies tailored for {occasion}. "
            f"You appreciate intentional sillage, complex transition between top citrus or floral accords into rich, long-lasting resinous bases."
        )

scent_finder_agent = ScentivaScentFinderAgent()
