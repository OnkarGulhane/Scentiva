import pytest
from agents.scent_finder_agent import scent_finder_agent
from models.schemas import ScentFinderQuizRequest

def test_scent_finder_evaluation():
    """Test scent finder quiz matchmaking."""
    req = ScentFinderQuizRequest(
        occasion="Date Night",
        fragranceFamily="Oriental",
        intensity="Strong",
        season="Winter",
        gender="Unisex",
        preferredNotes=["Vanilla", "Amber", "Black Orchid"]
    )
    res = scent_finder_agent.evaluate_quiz(req)
    
    assert res.personaTitle is not None
    assert "Oriental" in res.personaDescription or "Date Night" in res.personaDescription
    assert len(res.recommendations) > 0
    assert res.recommendations[0].matchScore >= 90
    assert res.recommendations[0].productName is not None
