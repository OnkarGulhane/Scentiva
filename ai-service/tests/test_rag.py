import pytest
from rag.ingestion import DocumentIngestionPipeline
from rag.vector_store import vector_store
from rag.retriever import retriever

def test_rag_ingestion_and_indexing():
    """Test that all document chunks are loaded and vector store is populated."""
    pipeline = DocumentIngestionPipeline()
    chunks = pipeline.load_and_chunk_all()
    
    assert len(chunks) > 0
    assert any(c.source_type == "product" for c in chunks)
    assert any(c.source_type == "brand" for c in chunks)
    assert any(c.source_type == "policy" for c in chunks)
    assert any(c.source_type == "faq" for c in chunks)

def test_rag_retrieval_similarity():
    """Test cosine similarity retrieval for woody perfumes."""
    results = retriever.retrieve_context("Woody sandalwood cedarwood perfume", k=3)
    assert len(results) > 0
    assert any("woody" in r.content.lower() or "cedar" in r.content.lower() for r in results)
