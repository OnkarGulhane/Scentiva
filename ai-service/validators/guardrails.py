import re
from typing import Tuple, List, Dict, Any, Optional
from app_logging.logger import logger
from tools.catalog_tools import get_product_by_id

INJECTION_PATTERNS = [
    re.compile(r'(?i)ignore\s+(all\s+)?(previous|prior|above)\s+instructions'),
    re.compile(r'(?i)system\s+prompt'),
    re.compile(r'(?i)you\s+are\s+now\s+(an\s+unrestricted|a\s+different)\s+ai'),
    re.compile(r'(?i)show\s+me\s+(all\s+)?(passwords|api_keys|tokens|secrets|database)'),
    re.compile(r'(?i)drop\s+table'),
    re.compile(r'(?i)delete\s+from'),
    re.compile(r'(?i)select\s+\*\s+from\s+users'),
    re.compile(r'(?i)developer\s+mode\s+activated')
]

class ScentivaGuardrails:
    """
    Security and Hallucination Prevention Guardrails for Scentiva AI.
    Blocks prompt injection, sensitive data leakage, and product hallucinations.
    """
    
    @staticmethod
    def inspect_prompt_safety(prompt: str) -> Tuple[bool, Optional[str]]:
        """
        Check incoming user prompt for injection attempts or malicious payload.
        Returns (is_safe: bool, reason: Optional[str]).
        """
        if not prompt or len(prompt.strip()) == 0:
            return False, "Empty query provided."
            
        if len(prompt) > 2000:
            return False, "Query length exceeds safety limit (maximum 2000 characters)."
            
        for pattern in INJECTION_PATTERNS:
            if pattern.search(prompt):
                logger.warning(f"Guardrail triggered: Prompt injection attempt detected in prompt: '{prompt[:60]}...'")
                return False, "I am the SCENTIVA Privé Concierge. I can only assist with haute perfumery, fragrance discovery, orders, and store policies."
                
        return True, None

    @staticmethod
    def validate_and_filter_products(products: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        """
        Strict Hallucination Prevention:
        Verify that every product returned exists in the real Scentiva catalog.
        Filter out any fabricated product names or unverifiable IDs.
        """
        verified = []
        for p in products:
            p_id = str(p.get("productId") or p.get("id") or "")
            real_product = get_product_by_id(p_id)
            if real_product:
                # Merge authoritative real price, stock, and slug
                verified.append({
                    "productId": real_product["id"],
                    "numericId": real_product.get("numeric_id"),
                    "name": real_product["name"],
                    "brandName": real_product["brand_name"],
                    "slug": real_product["slug"],
                    "price": float(real_product["starting_price"]),
                    "mrp": float(real_product.get("mrp", real_product["starting_price"])),
                    "inStock": bool(real_product.get("in_stock", True)),
                    "availableStock": int(real_product.get("stock_count", 10)),
                    "reason": p.get("reason") or f"Signature {real_product['brand_name']} olfactory balance with notable {real_product.get('longevity', 'all-day')} longevity.",
                    "fragranceFamily": real_product.get("fragrance_families", ["Haute Parfumerie"])[0] if real_product.get("fragrance_families") else "Haute Parfumerie",
                    "primaryImageUrl": real_product.get("primary_image_url"),
                    "concentration": real_product.get("concentration")
                })
            else:
                logger.warning(f"Hallucination Guard: Filtered out non-existent product ID '{p_id}'")
                
        return verified
