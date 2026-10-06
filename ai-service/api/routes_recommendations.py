import time
from fastapi import APIRouter, Request
from models.schemas import PersonalizedRecommendationsRequest, PersonalizedRecommendationsResponse
from services.recommendation_service import recommendation_service
from security.rate_limiter import rate_limiter
from security.auth_middleware import extract_user_from_request
from app_logging.logger import logger

router = APIRouter(prefix="/recommendations", tags=["Personalized Recommendations"])

@router.post("/personalized", response_model=PersonalizedRecommendationsResponse)
@router.post("/personal", response_model=PersonalizedRecommendationsResponse)
async def get_personalized_recommendations(payload: PersonalizedRecommendationsRequest, request: Request):

    """
    P2: Personalized Recommendations endpoint.
    Combines user interaction signals (viewed, wishlist, cart) into grounded recommendations.
    """
    client_ip = request.client.host if request.client else "127.0.0.1"
    user = extract_user_from_request(request)
    rate_limiter.check_rate_limit(client_id=user.get("sub", client_ip) if user else client_ip, is_authenticated=bool(user))

    start_time = time.time()
    response = recommendation_service.get_recommendations(payload)
    duration_ms = round((time.time() - start_time) * 1000, 2)
    logger.info(f"Personalized recommendations generated in {duration_ms}ms (type={response.recommendationType})")
    return response
