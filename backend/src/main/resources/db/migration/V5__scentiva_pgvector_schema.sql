-- ==============================================================================
-- Flyway Migration: V5__scentiva_pgvector_schema.sql
-- Module: SCENTIVA AI Knowledge & Vector Embeddings
-- Adds pgvector extension and structured RAG document storage tables
-- ==============================================================================

-- Enable vector extension if available in PostgreSQL
CREATE EXTENSION IF NOT EXISTS vector;

-- Table for RAG knowledge chunks and document metadata
CREATE TABLE IF NOT EXISTS scentiva_rag_documents (
    id VARCHAR(100) PRIMARY KEY,
    doc_type VARCHAR(50) NOT NULL, -- 'product', 'brand', 'policy', 'faq'
    source_id VARCHAR(100),
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    embedding vector(1536), -- Standard OpenAI/LangChain embedding dimension
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for lightning-fast retrieval and filtering
CREATE INDEX IF NOT EXISTS idx_scentiva_rag_doc_type ON scentiva_rag_documents(doc_type);
CREATE INDEX IF NOT EXISTS idx_scentiva_rag_source_id ON scentiva_rag_documents(source_id);
CREATE INDEX IF NOT EXISTS idx_scentiva_rag_metadata ON scentiva_rag_documents USING gin (metadata);

-- Table for conversation memory and interaction logs (sanitized, non-sensitive)
CREATE TABLE IF NOT EXISTS scentiva_ai_conversations (
    id VARCHAR(100) PRIMARY KEY,
    user_id VARCHAR(100),
    feature VARCHAR(50) NOT NULL, -- 'assistant', 'scent_finder', 'search', 'support'
    session_data JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_ai_conversations_user ON scentiva_ai_conversations(user_id);
CREATE INDEX IF NOT EXISTS idx_ai_conversations_feature ON scentiva_ai_conversations(feature);
