import pytest
from agents.recommendation_agent import recommendation_agent
from models.schemas import PersonalizedRecommendationsRequest

def test_personalized_recommendations_with_history():
    """Test personalized recommendations based on viewed/wishlisted products."""
    req = PersonalizedRecommendationsRequest(
        viewedProductIds=["prod-sauvage"],
        wishlistProductIds=["prod-armani-adg"],
        limit=3
    )
    res = recommendation_agent.generate_recommendations(req)
    
    assert res.recommendationType is not None
    assert len(res.products) > 0
    assert len(res.headline) > 0
    assert len(res.explanation) > 0

def test_personalized_recommendations_empty_fallback():
    """Test recommendation fallback when user has no prior history."""
    req = PersonalizedRecommendationsRequest(
        viewedProductIds=[],
        wishlistProductIds=[],
        cartProductIds=[],
        limit=4
    )
    res = recommendation_agent.generate_recommendations(req)
    
    assert res.recommendationType == "VAULT_ICONS"
    assert len(res.products) > 0
