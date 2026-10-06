import time
from fastapi import APIRouter, Request
from models.schemas import ScentFinderQuizRequest, ScentFinderQuizResponse
from services.scent_finder_service import scent_finder_service
from security.rate_limiter import rate_limiter
from security.auth_middleware import extract_user_from_request
from app_logging.logger import logger

router = APIRouter(prefix="/scent-finder", tags=["AI Scent Finder"])

@router.post("/recommend", response_model=ScentFinderQuizResponse)
@router.post("/evaluate", response_model=ScentFinderQuizResponse)
async def evaluate_scent_finder_quiz(payload: ScentFinderQuizRequest, request: Request):

    """
    P1: AI Scent Finder endpoint.
    Processes user olfactory quiz answers and generates persona title and recommendations.
    """
    client_ip = request.client.host if request.client else "127.0.0.1"
    user = extract_user_from_request(request)
    rate_limiter.check_rate_limit(client_id=user.get("sub", client_ip) if user else client_ip, is_authenticated=bool(user))

    start_time = time.time()
    response = scent_finder_service.evaluate_quiz(payload)
    duration_ms = round((time.time() - start_time) * 1000, 2)
    logger.info(f"Scent finder quiz completed in {duration_ms}ms with persona='{response.personaTitle}'")
    return response
