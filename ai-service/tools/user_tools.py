import httpx
from typing import List, Optional, Dict, Any
from config.settings import settings
from app_logging.logger import logger

def get_user_wishlist(user_id: Optional[str] = None, auth_token: Optional[str] = None) -> List[Dict[str, Any]]:
    """Retrieve user wishlist through authorized backend endpoint."""
    if not auth_token:
        return []
    headers = {"Authorization": f"Bearer {auth_token}" if not auth_token.startswith("Bearer ") else auth_token}
    try:
        with httpx.Client(timeout=3.0) as client:
            resp = client.get(f"{settings.SPRING_BOOT_API_URL}/wishlist", headers=headers)
            if resp.status_code == 200:
                return resp.json().get("data", [])
    except Exception as e:
        logger.debug(f"Wishlist tool error: {e}")
    return []

def get_user_cart(user_id: Optional[str] = None, auth_token: Optional[str] = None) -> Dict[str, Any]:
    """Retrieve user active shopping bag through authorized backend endpoint."""
    if not auth_token:
        return {"items": [], "subtotal": 0.0}
    headers = {"Authorization": f"Bearer {auth_token}" if not auth_token.startswith("Bearer ") else auth_token}
    try:
        with httpx.Client(timeout=3.0) as client:
            resp = client.get(f"{settings.SPRING_BOOT_API_URL}/cart", headers=headers)
            if resp.status_code == 200:
                return resp.json().get("data", {"items": [], "subtotal": 0.0})
    except Exception as e:
        logger.debug(f"Cart tool error: {e}")
    return {"items": [], "subtotal": 0.0}
