from contextlib import asynccontextmanager
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from config.settings import settings
from api import api_router
from services.rag_service import rag_service
from app_logging.logger import logger

@asynccontextmanager
async def lifespan(app: FastAPI):
    """Application lifespan manager: runs RAG ingestion on startup."""
    logger.info("Initializing Scentiva AI Platform...")
    try:
        rag_service.ingest_knowledge_base()
        logger.info("Scentiva RAG Knowledge Base successfully initialized on startup.")
    except Exception as e:
        logger.error(f"Startup RAG knowledge ingestion warning: {e}")
    yield
    logger.info("Scentiva AI Platform shutting down cleanly.")

app = FastAPI(
    title=settings.APP_NAME,
    description="Scentiva Haute Parfumerie AI Platform — Shopping Assistant, Semantic Search, Scent Finder, Recommendations, and Grounded Customer Support.",
    version="1.0.0",
    lifespan=lifespan
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(api_router)

@app.get("/health")
def root_health():
    return {"status": "ok", "service": settings.APP_NAME, "version": "1.0.0"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host=settings.HOST, port=settings.PORT, reload=settings.DEBUG)
