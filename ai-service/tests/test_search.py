import pytest
from agents.search_agent import semantic_search_agent

def test_semantic_search_citrus_summer():
    """Test semantic search with 'Summer fresh aquatic citrus perfume'."""
    response = semantic_search_agent.execute_search("Summer fresh aquatic citrus perfume", limit=4)
    
    assert response.totalMatches > 0
    assert len(response.results) > 0
    top_result = response.results[0]
    assert top_result.productName is not None
    assert top_result.startingPrice > 0
    assert top_result.inStock is True
    assert top_result.relevanceScore >= 0.80

def test_semantic_search_price_filtered():
    """Test semantic search with strict maximum price constraint."""
    response = semantic_search_agent.execute_search("luxury perfume", limit=5, max_price=7000.0)
    
    assert response.totalMatches > 0
    for item in response.results:
        assert item.startingPrice <= 7000.0
