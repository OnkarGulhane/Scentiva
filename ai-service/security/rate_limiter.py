import time
from typing import Dict, List
from fastapi import HTTPException, status
from config.settings import settings
from app_logging.logger import logger

class InMemoryRateLimiter:
    """Sliding-window rate limiter by client IP or user ID."""
    
    def __init__(self):
        self._requests: Dict[str, List[float]] = {}

    def check_rate_limit(self, client_id: str, is_authenticated: bool = False) -> bool:
        now = time.time()
        window_seconds = 60.0
        max_allowed = settings.RATE_LIMIT_PER_MINUTE_USER if is_authenticated else settings.RATE_LIMIT_PER_MINUTE_GUEST
        
        # Clean older entries
        timestamps = self._requests.get(client_id, [])
        valid_timestamps = [t for t in timestamps if now - t < window_seconds]
        
        if len(valid_timestamps) >= max_allowed:
            logger.warning(f"Rate limit exceeded for client={client_id} ({len(valid_timestamps)}/{max_allowed} requests per minute)")
            raise HTTPException(
                status_code=status.HTTP_429_TOO_MANY_REQUESTS,
                detail=f"Rate limit exceeded. Maximum {max_allowed} requests per minute allowed."
            )
            
        valid_timestamps.append(now)
        self._requests[client_id] = valid_timestamps
        return True

rate_limiter = InMemoryRateLimiter()
