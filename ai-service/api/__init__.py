from fastapi import APIRouter
from .routes_assistant import router as assistant_router
from .routes_search import router as search_router
from .routes_scent_finder import router as scent_finder_router
from .routes_recommendations import router as recommendations_router
from .routes_support import router as support_router
from .routes_rag import router as rag_router
from .routes_health import router as health_router

api_router = APIRouter(prefix="/api/v1/ai")
api_router.include_router(assistant_router)
api_router.include_router(search_router)
api_router.include_router(scent_finder_router)
api_router.include_router(recommendations_router)
api_router.include_router(support_router)
api_router.include_router(rag_router)
api_router.include_router(health_router)

__all__ = ["api_router"]
