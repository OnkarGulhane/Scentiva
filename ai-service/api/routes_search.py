import time
from typing import Optional
from fastapi import APIRouter, Request, Query
from models.schemas import SemanticSearchQueryRequest, SemanticSearchQueryResponse
from services.search_service import search_service
from security.rate_limiter import rate_limiter
from security.auth_middleware import extract_user_from_request
from app_logging.logger import logger

router = APIRouter(prefix="/search", tags=["AI Semantic Search"])

@router.post("/semantic", response_model=SemanticSearchQueryResponse)
async def semantic_search_post(payload: SemanticSearchQueryRequest, request: Request):
    """
    P1: AI Semantic Search endpoint (POST).
    Natural language search with intent understanding, vector retrieval, and price/stock verification.
    """
    client_ip = request.client.host if request.client else "127.0.0.1"
    user = extract_user_from_request(request)
    rate_limiter.check_rate_limit(client_id=user.get("sub", client_ip) if user else client_ip, is_authenticated=bool(user))

    start_time = time.time()
    response = search_service.execute_semantic_search(payload)
    duration_ms = round((time.time() - start_time) * 1000, 2)
    logger.info(f"Semantic search (POST) completed in {duration_ms}ms with {response.totalMatches} results")
    return response

@router.get("/semantic", response_model=SemanticSearchQueryResponse)
async def semantic_search_get(
    request: Request,
    q: str = Query(..., min_length=1, max_length=500),
    limit: int = Query(default=6, ge=1, le=50),
    maxPrice: Optional[float] = Query(default=None),
    fragranceFamily: Optional[str] = Query(default=None),
    gender: Optional[str] = Query(default=None)
):
    """
    P1: AI Semantic Search endpoint (GET).
    Convenience endpoint for frontend search bars and instant autocomplete.
    """
    payload = SemanticSearchQueryRequest(
        query=q,
        limit=limit,
        maxPrice=maxPrice,
        fragranceFamily=fragranceFamily,
        gender=gender
    )
    return await semantic_search_post(payload, request)
