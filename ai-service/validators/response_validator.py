from typing import List, Dict, Any
from models.schemas import VerifiedProductDto, AiActionDto
from app_logging.logger import logger

class ResponseValidator:
    """Validates and ensures clean structured responses for frontend consumption."""
    
    @staticmethod
    def sanitize_verified_products(products_raw: List[Dict[str, Any]]) -> List[VerifiedProductDto]:
        validated = []
        for p in products_raw:
            try:
                validated.append(VerifiedProductDto(
                    productId=p["productId"],
                    numericId=p.get("numericId"),
                    name=p["name"],
                    brandName=p["brandName"],
                    slug=p["slug"],
                    price=float(p["price"]),
                    mrp=float(p.get("mrp", p["price"])),
                    inStock=bool(p.get("inStock", True)),
                    availableStock=int(p.get("availableStock", 10)),
                    reason=p.get("reason", "Curated for your fragrance profile."),
                    fragranceFamily=p.get("fragranceFamily"),
                    primaryImageUrl=p.get("primaryImageUrl"),
                    concentration=p.get("concentration")
                ))
            except Exception as e:
                logger.error(f"Response validation error for product: {e}")
        return validated

    @staticmethod
    def build_product_actions(products: List[VerifiedProductDto]) -> List[AiActionDto]:
        actions = []
        for p in products:
            actions.append(AiActionDto(
                type="VIEW_PRODUCT",
                productId=p.productId,
                payload={"slug": p.slug, "name": p.name}
            ))
            if p.inStock:
                actions.append(AiActionDto(
                    type="ADD_TO_CART",
                    productId=p.productId,
                    payload={"slug": p.slug, "name": p.name, "price": p.price}
                ))
        return actions
