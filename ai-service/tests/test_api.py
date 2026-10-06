import pytest

def test_health_endpoint(client):
    """Test /api/v1/ai/health endpoint."""
    res = client.get("/api/v1/ai/health")
    assert res.status_code == 200
    data = res.json()
    assert data["status"] == "UP"
    assert data["documentsIndexed"] > 0

def test_shopping_assistant_endpoint(client):
    """Test /api/v1/ai/assistant/chat endpoint."""
    res = client.post("/api/v1/ai/assistant/chat", json={
        "message": "I need a fresh perfume for office wear under 8000"
    })
    assert res.status_code == 200
    data = res.json()
    assert data["intent"] == "perfume_recommendation"
    assert len(data["products"]) > 0

def test_semantic_search_endpoint(client):
    """Test /api/v1/ai/search/semantic endpoint."""
    res = client.post("/api/v1/ai/search/semantic", json={
        "query": "fresh bergamot aquatic",
        "limit": 4
    })
    assert res.status_code == 200
    data = res.json()
    assert data["totalMatches"] > 0

def test_scent_finder_endpoint(client):
    """Test /api/v1/ai/scent-finder/recommend endpoint."""
    res = client.post("/api/v1/ai/scent-finder/recommend", json={
        "fragranceFamily": "Fresh",
        "occasion": "Everyday"
    })
    assert res.status_code == 200
    data = res.json()
    assert "personaTitle" in data
    assert len(data["recommendations"]) > 0

def test_personalized_recommendations_endpoint(client):
    """Test /api/v1/ai/recommendations/personalized endpoint."""
    res = client.post("/api/v1/ai/recommendations/personalized", json={
        "viewedProductIds": ["prod-sauvage"],
        "limit": 3
    })
    assert res.status_code == 200
    data = res.json()
    assert len(data["products"]) > 0

def test_customer_support_endpoint(client):
    """Test /api/v1/ai/support/chat endpoint."""
    res = client.post("/api/v1/ai/support/chat", json={
        "message": "What is your shipping policy?"
    })
    assert res.status_code == 200
    data = res.json()
    assert data["queryType"] == "KNOWLEDGE"
    assert len(data["reply"]) > 0
