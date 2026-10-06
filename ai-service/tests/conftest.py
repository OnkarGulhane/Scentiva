import sys
import os
import pytest
from fastapi.testclient import TestClient

# Ensure ai-service root is in python path
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

from main import app
from services.rag_service import rag_service

@pytest.fixture(scope="session", autouse=True)
def setup_rag_knowledge():
    """Ensure RAG vector store is populated before running test suite."""
    rag_service.ingest_knowledge_base()

@pytest.fixture
def client():
    """FastAPI TestClient fixture."""
    with TestClient(app) as test_client:
        yield test_client
