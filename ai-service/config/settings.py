import os
from typing import Optional, List
from pydantic_settings import BaseSettings
from pydantic import Field

class Settings(BaseSettings):
    """Scentiva AI Platform Configuration Settings."""
    
    APP_NAME: str = "Scentiva AI Service"
    APP_ENV: str = Field(default="development", env="APP_ENV")
    PORT: int = Field(default=8000, env="PORT")
    HOST: str = Field(default="0.0.0.0", env="HOST")
    DEBUG: bool = Field(default=False, env="DEBUG")
    
    # LLM Configuration
    LLM_PROVIDER: str = Field(default="mock", env="LLM_PROVIDER") # options: mock, openai, anthropic, ollama, gemini
    LLM_MODEL: str = Field(default="gpt-4o-mini", env="LLM_MODEL")
    LLM_API_KEY: Optional[str] = Field(default=None, env="LLM_API_KEY")
    LLM_TEMPERATURE: float = Field(default=0.2, env="LLM_TEMPERATURE")
    LLM_MAX_TOKENS: int = Field(default=1024, env="LLM_MAX_TOKENS")
    LLM_REQUEST_TIMEOUT_SECONDS: int = Field(default=30, env="LLM_REQUEST_TIMEOUT_SECONDS")
    
    # Embedding Configuration
    EMBEDDING_PROVIDER: str = Field(default="mock", env="EMBEDDING_PROVIDER") # options: mock, openai, huggingface
    EMBEDDING_MODEL: str = Field(default="text-embedding-3-small", env="EMBEDDING_MODEL")
    EMBEDDING_API_KEY: Optional[str] = Field(default=None, env="EMBEDDING_API_KEY")
    EMBEDDING_DIMENSION: int = Field(default=1536, env="EMBEDDING_DIMENSION")
    
    # Database / Vector Storage
    DATABASE_URL: Optional[str] = Field(default=None, env="DATABASE_URL")
    VECTOR_DATABASE_URL: Optional[str] = Field(default=None, env="VECTOR_DATABASE_URL")
    VECTOR_TABLE_NAME: str = Field(default="scentiva_rag_embeddings", env="VECTOR_TABLE_NAME")
    
    # Backend Integration (Spring Boot)
    SPRING_BOOT_API_URL: str = Field(default="http://localhost:8080/api/v1", env="SPRING_BOOT_API_URL")
    SPRING_BOOT_INTERNAL_SECRET: Optional[str] = Field(default="scentiva_ai_internal_secure_token_2026", env="SPRING_BOOT_INTERNAL_SECRET")
    
    # Security & Auth
    JWT_SECRET: str = Field(default="scentiva-master-secret-key-2026-luxury-perfumery-enterprise-jwt-token-key-minimum-512-bits-long-signature", env="JWT_SECRET")
    JWT_ALGORITHM: str = Field(default="HS512", env="JWT_ALGORITHM")
    CORS_ORIGINS: List[str] = ["http://localhost:3000", "http://localhost:8080", "https://scentiva.luxury"]
    
    # Rate Limiting
    RATE_LIMIT_PER_MINUTE_GUEST: int = Field(default=30, env="RATE_LIMIT_PER_MINUTE_GUEST")
    RATE_LIMIT_PER_MINUTE_USER: int = Field(default=120, env="RATE_LIMIT_PER_MINUTE_USER")
    MAX_PROMPT_LENGTH: int = Field(default=2000, env="MAX_PROMPT_LENGTH")
    
    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"
        extra = "ignore"

settings = Settings()
