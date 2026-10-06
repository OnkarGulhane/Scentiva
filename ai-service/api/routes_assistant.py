import time
from fastapi import APIRouter, Request, Depends
from models.schemas import AssistantChatRequest, AssistantChatResponse
from services.shopping_service import shopping_service
from security.rate_limiter import rate_limiter
from security.auth_middleware import extract_user_from_request
from app_logging.logger import logger

router = APIRouter(prefix="/assistant", tags=["AI Shopping Assistant"])

@router.post("/chat", response_model=AssistantChatResponse)
async def chat_with_shopping_assistant(payload: AssistantChatRequest, request: Request):
    """
    P1: AI Shopping Assistant endpoint.
    Natural language perfume recommendation with preference extraction,
    RAG knowledge grounding, real catalog query, and verified prices/stock.
    """
    client_ip = request.client.host if request.client else "127.0.0.1"
    user = extract_user_from_request(request)
    rate_limiter.check_rate_limit(client_id=user.get("sub", client_ip) if user else client_ip, is_authenticated=bool(user))

    start_time = time.time()
    logger.info(f"Shopping Assistant chat request from client={client_ip}: '{payload.message[:50]}...'")
    
    response = shopping_service.process_chat(payload)
    
    duration_ms = round((time.time() - start_time) * 1000, 2)
    logger.info(f"Shopping Assistant chat completed in {duration_ms}ms with {len(response.products)} verified matches")
    
    return response
