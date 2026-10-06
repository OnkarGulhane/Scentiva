from typing import List, Optional, Dict, Any
from pydantic import BaseModel, Field

# ==============================================================================
# Common & Sub-Models
# ==============================================================================

class ChatMessage(BaseModel):
    role: str = Field(description="Role: user, assistant, or system")
    content: str = Field(description="Message content")

class ExtractedShoppingPreferences(BaseModel):
    occasion: Optional[str] = None
    budget_max: Optional[float] = None
    fragrance_family: Optional[str] = None
    notes: List[str] = Field(default_factory=list)
    longevity: Optional[str] = None
    season: Optional[str] = None
    day_night: Optional[str] = None
    gender: Optional[str] = None
    brand: Optional[str] = None

class VerifiedProductDto(BaseModel):
    productId: str
    numericId: Optional[int] = None
    name: str
    brandName: str
    slug: str
    price: float
    mrp: Optional[float] = None
    inStock: bool = True
    availableStock: int = 10
    reason: str
    fragranceFamily: Optional[str] = None
    primaryImageUrl: Optional[str] = None
    concentration: Optional[str] = None

class AiActionDto(BaseModel):
    type: str = Field(description="Action type: VIEW_PRODUCT, ADD_TO_CART, NAVIGATE")
    productId: Optional[str] = None
    variantSku: Optional[str] = None
    payload: Optional[Dict[str, Any]] = None

# ==============================================================================
# 1. AI Shopping Assistant
# ==============================================================================

from pydantic import model_validator

class AssistantChatRequest(BaseModel):
    message: str = Field(default="", max_length=2000)
    query: Optional[str] = None
    conversationHistory: List[ChatMessage] = Field(default_factory=list)
    userId: Optional[str] = None
    guestSessionId: Optional[str] = None
    userContext: Optional[Dict[str, Any]] = None

    @model_validator(mode="before")
    @classmethod
    def normalize_message(cls, values: Any) -> Any:
        if isinstance(values, dict):
            if not values.get("message") and values.get("query"):
                values["message"] = values["query"]
            elif not values.get("query") and values.get("message"):
                values["query"] = values["message"]
            if not values.get("message"):
                values["message"] = "Help me discover a fragrance"
        return values

class AssistantChatResponse(BaseModel):
    intent: str
    message: str
    reply: str
    extractedPreferences: ExtractedShoppingPreferences
    products: List[VerifiedProductDto] = Field(default_factory=list)
    actions: List[AiActionDto] = Field(default_factory=list)
    suggestedFollowUps: List[str] = Field(default_factory=list)


# ==============================================================================
# 2. AI Semantic Search
# ==============================================================================

class SemanticSearchQueryRequest(BaseModel):
    query: str = Field(min_length=1, max_length=500)
    limit: int = Field(default=6, ge=1, le=50)
    maxPrice: Optional[float] = None
    fragranceFamily: Optional[str] = None
    gender: Optional[str] = None

class SemanticSearchResultItem(BaseModel):
    productId: str
    numericId: Optional[int] = None
    productName: str
    productSlug: str
    brandName: str
    primaryImageUrl: Optional[str] = None
    startingPrice: float
    inStock: bool = True
    relevanceScore: float = 0.95
    extractedFamily: Optional[str] = None
    highlightedNotes: List[str] = Field(default_factory=list)
    matchExplanation: str

class SemanticSearchQueryResponse(BaseModel):
    query: str
    interpretedIntent: str
    detectedNotes: List[str] = Field(default_factory=list)
    detectedEmotions: List[str] = Field(default_factory=list)
    totalMatches: int
    results: List[SemanticSearchResultItem] = Field(default_factory=list)

# ==============================================================================
# 3. AI Scent Finder
# ==============================================================================

class ScentFinderQuizRequest(BaseModel):
    occasion: Optional[str] = None
    budget: Optional[str] = None
    fragranceFamily: Optional[str] = None
    intensity: Optional[str] = None
    season: Optional[str] = None
    gender: Optional[str] = "Unisex"
    dayNight: Optional[str] = None
    preferredNotes: List[str] = Field(default_factory=list)
    brandPreference: Optional[str] = None

    @model_validator(mode="before")
    @classmethod
    def normalize_fields(cls, values: Any) -> Any:
        if isinstance(values, dict):
            if "fragrance_family" in values and "fragranceFamily" not in values:
                values["fragranceFamily"] = values["fragrance_family"]
            if "preferred_notes" in values and "preferredNotes" not in values:
                values["preferredNotes"] = values["preferred_notes"]
            if "budget_max" in values and "budget" not in values:
                values["budget"] = str(values["budget_max"])
            if "day_night" in values and "dayNight" not in values:
                values["dayNight"] = values["day_night"]
            if "brand_preference" in values and "brandPreference" not in values:
                values["brandPreference"] = values["brand_preference"]
        return values

class ScentFinderRecommendationItem(BaseModel):
    productId: str
    numericId: Optional[int] = None
    productName: str
    productSlug: str
    brandName: str
    primaryImageUrl: Optional[str] = None
    startingPrice: float
    inStock: bool = True
    matchScore: int = 95
    recommendationReason: str
    matchedNotes: List[str] = Field(default_factory=list)
    idealOccasion: Optional[str] = None

class ScentFinderQuizResponse(BaseModel):
    personaTitle: str
    personaDescription: str
    dominantAccord: str
    recommendations: List[ScentFinderRecommendationItem] = Field(default_factory=list)

# ==============================================================================
# 4. Personalized Recommendations
# ==============================================================================

class PersonalizedRecommendationsRequest(BaseModel):
    userId: Optional[str] = None
    guestSessionId: Optional[str] = None
    viewedProductIds: List[str] = Field(default_factory=list)
    wishlistProductIds: List[str] = Field(default_factory=list)
    cartProductIds: List[str] = Field(default_factory=list)
    purchasedProductIds: List[str] = Field(default_factory=list)
    limit: int = Field(default=4, ge=1, le=12)

    @model_validator(mode="before")
    @classmethod
    def normalize_fields(cls, values: Any) -> Any:
        if isinstance(values, dict):
            if "user_id" in values and "userId" not in values:
                values["userId"] = values["user_id"]
            if "viewed_product_ids" in values and "viewedProductIds" not in values:
                values["viewedProductIds"] = values["viewed_product_ids"]
            if "wishlist_product_ids" in values and "wishlistProductIds" not in values:
                values["wishlistProductIds"] = values["wishlist_product_ids"]
            if "cart_product_ids" in values and "cartProductIds" not in values:
                values["cartProductIds"] = values["cart_product_ids"]
            if "purchased_product_ids" in values and "purchasedProductIds" not in values:
                values["purchasedProductIds"] = values["purchased_product_ids"]
        return values


class PersonalizedRecommendationsResponse(BaseModel):
    recommendationType: str
    headline: str
    explanation: str
    products: List[VerifiedProductDto] = Field(default_factory=list)

# ==============================================================================
# 5. AI Customer Support (Knowledge RAG + Live Order Tools)
# ==============================================================================

class SupportChatRequest(BaseModel):
    message: str = Field(min_length=1, max_length=2000)
    orderNumber: Optional[str] = None
    userId: Optional[str] = None
    userEmail: Optional[str] = None
    conversationHistory: List[ChatMessage] = Field(default_factory=list)

class LiveOrderSummaryDto(BaseModel):
    orderNumber: str
    status: str
    createdAt: str
    totalAmount: float
    trackingNumber: Optional[str] = None
    carrierName: Optional[str] = None
    estimatedDelivery: Optional[str] = None
    itemsCount: int = 1

class SupportChatResponse(BaseModel):
    queryType: str = Field(description="KNOWLEDGE, LIVE_ORDER, PAYMENT, GENERAL")
    reply: str
    policyCitation: Optional[str] = None
    orderDetails: Optional[LiveOrderSummaryDto] = None
    suggestedActions: List[str] = Field(default_factory=list)
    isLiveAgentEscalationRecommended: bool = False

# ==============================================================================
# Ingestion & Health
# ==============================================================================

class IngestKnowledgeResponse(BaseModel):
    success: bool
    totalDocumentsIngested: int
    categories: Dict[str, int]
    timestamp: str

class HealthStatusResponse(BaseModel):
    status: str
    service: str
    llmProvider: str
    embeddingProvider: str
    vectorStoreStatus: str
    documentsIndexed: int
    uptimeSeconds: float
