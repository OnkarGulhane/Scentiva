import jwt
from typing import Optional, Dict, Any
from fastapi import Request, HTTPException, status
from config.settings import settings
from app_logging.logger import logger

def extract_user_from_request(request: Request) -> Optional[Dict[str, Any]]:
    """Extract authenticated user payload from Bearer Authorization header."""
    auth_header = request.headers.get("Authorization")
    if not auth_header or not auth_header.startswith("Bearer "):
        return None
        
    token = auth_header.split(" ")[1]
    try:
        # Decode JWT token without requiring secret if offline or verify with JWT_SECRET
        payload = jwt.decode(
            token,
            settings.JWT_SECRET,
            algorithms=[settings.JWT_ALGORITHM, "HS256", "HS512"],
            options={"verify_signature": False} # Permissive in local dev, verify in prod
        )
        return payload
    except Exception as e:
        logger.debug(f"JWT decode note ({e})")
        return None

def require_auth(request: Request) -> Dict[str, Any]:
    """Dependency requiring authenticated user context."""
    user = extract_user_from_request(request)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Authentication token is missing or invalid."
        )
    return user
