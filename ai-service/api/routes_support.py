import time
from fastapi import APIRouter, Request
from models.schemas import SupportChatRequest, SupportChatResponse
from services.support_service import support_service
from security.rate_limiter import rate_limiter
from security.auth_middleware import extract_user_from_request
from app_logging.logger import logger

router = APIRouter(prefix="/support", tags=["AI Customer Support"])

@router.post("/chat", response_model=SupportChatResponse)
async def chat_with_customer_support(payload: SupportChatRequest, request: Request):
    """
    P2: AI Customer Support endpoint.
    Handles RAG policy queries and authorized live order/shipment status lookups.
    """
    client_ip = request.client.host if request.client else "127.0.0.1"
    user = extract_user_from_request(request)
    rate_limiter.check_rate_limit(client_id=user.get("sub", client_ip) if user else client_ip, is_authenticated=bool(user))

    start_time = time.time()
    response = support_service.process_support(payload)
    duration_ms = round((time.time() - start_time) * 1000, 2)
    logger.info(f"Support inquiry processed in {duration_ms}ms (type={response.queryType})")
    return response
