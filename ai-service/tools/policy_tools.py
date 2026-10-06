import json
import os
from typing import List, Optional, Dict, Any
from app_logging.logger import logger

def _load_policies() -> List[Dict[str, Any]]:
    path = os.path.join(os.path.dirname(__file__), "..", "knowledge_data", "policies.json")
    try:
        with open(path, "r", encoding="utf-8") as f:
            return json.load(f)
    except Exception as e:
        logger.error(f"Failed to load policies: {e}")
        return []

def _load_faqs() -> List[Dict[str, Any]]:
    path = os.path.join(os.path.dirname(__file__), "..", "knowledge_data", "faqs.json")
    try:
        with open(path, "r", encoding="utf-8") as f:
            return json.load(f)
    except Exception as e:
        logger.error(f"Failed to load faqs: {e}")
        return []

def get_policy(category: str) -> Optional[Dict[str, Any]]:
    """Fetch official statutory policy by category: shipping, returns, refunds, authenticity, payments."""
    policies = _load_policies()
    cat_lower = category.strip().lower()
    for p in policies:
        if p.get("category", "").lower() == cat_lower or cat_lower in p.get("topic", "").lower():
            return p
    return None

def get_faq_answer(query: str) -> Optional[Dict[str, str]]:
    """Match query against approved FAQs."""
    faqs = _load_faqs()
    q_tokens = query.lower().split()
    best_match = None
    best_score = 0
    
    for faq in faqs:
        score = sum(1 for t in q_tokens if t in faq["question"].lower())
        if score > best_score and score >= 2:
            best_score = score
            best_match = faq
            
    return best_match
