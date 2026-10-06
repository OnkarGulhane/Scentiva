from typing import List, Optional, Dict, Any
from dataclasses import dataclass, field

@dataclass
class ProductKnowledge:
    id: str
    numeric_id: Optional[int]
    name: str
    slug: str
    brand_id: str
    brand_name: str
    category: str
    fragrance_families: List[str]
    concentration: str
    top_notes: List[str]
    heart_notes: List[str]
    base_notes: List[str]
    sillage: str
    longevity: str
    season: List[str]
    occasion: List[str]
    description: str
    story: Optional[str]
    starting_price: float
    mrp: float
    in_stock: bool
    stock_count: int
    rating: float
    review_count: int
    primary_image_url: str

@dataclass
class BrandKnowledge:
    id: str
    name: str
    slug: str
    origin_country: str
    description: str
    tier: str

@dataclass
class PolicyKnowledge:
    topic: str
    title: str
    content: str
    category: str # shipping, returns, refunds, authenticity, payments, privacy
    faqs: List[Dict[str, str]] = field(default_factory=list)

@dataclass
class RagDocumentChunk:
    doc_id: str
    source_type: str # product, brand, policy, faq
    title: str
    content: str
    metadata: Dict[str, Any] = field(default_factory=dict)
