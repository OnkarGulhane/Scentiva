import re
from typing import List, Optional, Dict, Any
from core.llm_factory import get_llm
from prompts.domain_prompts import SUPPORT_SYSTEM_PROMPT
from rag.retriever import retriever
from tools.order_tools import get_order_status
from tools.policy_tools import get_policy, get_faq_answer
from validators.guardrails import ScentivaGuardrails
from models.schemas import SupportChatRequest, SupportChatResponse, LiveOrderSummaryDto
from app_logging.logger import logger

class ScentivaCustomerSupportAgent:
    """
    AI Customer Support Agent.
    Intelligently routes between RAG knowledge base and authorized live order tools.
    Never fabricates order status or policy facts.
    """

    def __init__(self):
        self.llm = get_llm()
        self.retriever = retriever

    def process_support_query(self, req: SupportChatRequest) -> SupportChatResponse:
        # 1. Guardrail Check
        is_safe, error = ScentivaGuardrails.inspect_prompt_safety(req.message)
        if not is_safe:
            return SupportChatResponse(
                queryType="KNOWLEDGE",
                reply=error or "How may I assist you with your SCENTIVA orders, shipping, or returns?",
                suggestedActions=["View Return Policy", "Check Order Status"]
            )

        msg_lower = req.message.lower()

        # 2. Check for Live Order queries (e.g. "where is my order SCT-12345", "track order", "order status")
        order_match = re.search(r'(?:sct|sc|ord)[-_]?\d+', msg_lower)
        extracted_order_num = req.orderNumber or (order_match.group(0).upper() if order_match else None)

        if extracted_order_num or any(w in msg_lower for w in ["where is my order", "order status", "track my order", "kuth paryant aali order"]):
            if extracted_order_num:
                order_info = get_order_status(extracted_order_num, user_email=req.userEmail)
                if order_info:
                    live_dto = LiveOrderSummaryDto(
                        orderNumber=order_info["orderNumber"],
                        status=order_info["status"],
                        createdAt=order_info["createdAt"],
                        totalAmount=order_info["totalAmount"],
                        trackingNumber=order_info.get("trackingNumber"),
                        carrierName=order_info.get("carrierName"),
                        estimatedDelivery=order_info.get("estimatedDelivery"),
                        itemsCount=order_info.get("itemsCount", 1)
                    )
                    reply = (
                        f"Your order {live_dto.orderNumber} is currently '{live_dto.status}'. "
                        f"It is dispatched in our climate-regulated thermal coffret via {live_dto.carrierName} "
                        f"(Tracking: {live_dto.trackingNumber}). Estimated delivery is within {live_dto.estimatedDelivery}."
                    )
                    return SupportChatResponse(
                        queryType="LIVE_ORDER",
                        reply=reply,
                        orderDetails=live_dto,
                        suggestedActions=["Download Tax Invoice", "Track Courier Live", "Contact Delivery Partner"]
                    )
                else:
                    return SupportChatResponse(
                        queryType="LIVE_ORDER",
                        reply=f"We could not locate order reference '{extracted_order_num}' in the active vault ledger. Please verify your order number in your profile or contact our concierge.",
                        suggestedActions=["View Order History", "Contact Support"]
                    )
            else:
                return SupportChatResponse(
                    queryType="LIVE_ORDER",
                    reply="To check your shipment tracking, please provide your SCENTIVA order number (e.g. SCT-82454) or visit your Account Orders page.",
                    suggestedActions=["View Order History", "Contact Support"]
                )

        # 3. Check for specific FAQ match
        faq = get_faq_answer(req.message)
        if faq:
            return SupportChatResponse(
                queryType="KNOWLEDGE",
                reply=faq["answer"],
                policyCitation=f"SCENTIVA Customer Care — {faq.get('category', 'FAQ').capitalize()} Policy",
                suggestedActions=["Browse Fragrances", "Explore Policies"]
            )

        # 4. RAG Knowledge Retrieval for Policies
        category = None
        if any(w in msg_lower for w in ["return", "exchange", "replace", "seal"]):
            category = "returns"
        elif any(w in msg_lower for w in ["shipping", "delivery", "courier", "heat", "transit", "free shipping"]):
            category = "shipping"
        elif any(w in msg_lower for w in ["refund", "money back", "reversal"]):
            category = "refunds"
        elif any(w in msg_lower for w in ["authentic", "original", "fake", "batch code"]):
            category = "authenticity"
        elif any(w in msg_lower for w in ["payment", "upi", "card", "cod", "netbanking", "razorpay"]):
            category = "payments"

        if category:
            pol = get_policy(category)
            if pol:
                return SupportChatResponse(
                    queryType="KNOWLEDGE",
                    reply=pol["content"],
                    policyCitation=f"Official Policy: {pol['title']}",
                    suggestedActions=["Shop with Confidence", "View Full Policy Document"]
                )

        # 5. General RAG context fallback via Retriever
        rag_chunks = self.retriever.retrieve_context(query=req.message, k=2, source_type="policy")
        if rag_chunks:
            citation = f"SCENTIVA Policy: {rag_chunks[0].title}"
            return SupportChatResponse(
                queryType="KNOWLEDGE",
                reply=rag_chunks[0].content,
                policyCitation=citation,
                suggestedActions=["Explore Catalog", "Contact Concierge"]
            )

        # Default refined support reply
        return SupportChatResponse(
            queryType="GENERAL",
            reply=(
                "Welcome to SCENTIVA Concierge Support. We are dedicated to providing an uncompromising luxury experience. "
                "All our perfumes include 100% authenticity guarantees, 7-day privilege returns with discovery samples, and thermal insulated shipping. "
                "How may we assist your olfactory journey today?"
            ),
            suggestedActions=["Check Order Status", "Return Policy", "Authenticity Guarantee"]
        )

support_agent = ScentivaCustomerSupportAgent()
