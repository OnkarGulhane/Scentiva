from typing import Optional, Any, List, Dict
from langchain_core.language_models import BaseChatModel
from langchain_core.messages import BaseMessage, AIMessage, HumanMessage, SystemMessage
from langchain_core.outputs import ChatResult, ChatGeneration
from config.settings import settings
from app_logging.logger import logger

class DeterministicScentivaChatModel(BaseChatModel):
    """
    Deterministic Luxury Scentiva Language Model for local verification,
    offline testing, and environments without external API keys.
    Understands both English and Marathi queries and generates structured responses.
    """
    
    def _generate(
        self,
        messages: List[BaseMessage],
        stop: Optional[List[str]] = None,
        run_manager: Optional[Any] = None,
        **kwargs: Any,
    ) -> ChatResult:
        user_text = ""
        for m in reversed(messages):
            if isinstance(m, HumanMessage) or m.type == "human":
                user_text = m.content
                break
        
        reply_content = self._generate_intelligent_response(user_text, messages)
        generation = ChatGeneration(message=AIMessage(content=reply_content))
        return ChatResult(generations=[generation])
    
    @property
    def _llm_type(self) -> str:
        return "scentiva-deterministic-llm"
        
    def _generate_intelligent_response(self, query: str, messages: List[BaseMessage]) -> str:
        q_lower = query.lower()
        
        # Check if Marathi keywords or intent present
        is_marathi = any(k in q_lower for k in ["sathi", "pahije", "madhe", "aahe", "kiti", "kuthe", "sang", "bagh", "mala"])
        
        if "order" in q_lower or "track" in q_lower or "kuth paryant" in q_lower:
            return (
                "Your SCENTIVA orders are tracked directly through our climate-controlled vault dispatch. "
                "You can view real-time courier checkpoints, AWB tracking, and download your official GST Tax Invoice from your profile."
            )
        elif "return" in q_lower or "refund" in q_lower or "cancel" in q_lower:
            return (
                "SCENTIVA offers a 7-Day Easy Privilege Return policy on unopened flacons with intact tamper seals. "
                "Every order includes a complimentary 2ml discovery spray so you can experience the scent on skin before unsealing the main bottle. "
                "Refunds are credited to your original payment method within 3-5 business days."
            )
        elif "authentic" in q_lower or "original" in q_lower or "khara" in q_lower:
            return (
                "Every fragrance in the SCENTIVA Vault is 100% authentic, procured directly from master brand houses in France and Italy. "
                "Each bottle features original batch codes matching the flacon base, holographic security seals, and an official Digital Certificate of Authenticity."
            )
        elif "office" in q_lower or "work" in q_lower or "fresh" in q_lower or "summer" in q_lower:
            if is_marathi:
                return (
                    "Office आणि daily wear साठी आम्ही Christian Dior Sauvage EDP (₹5,499) किंवा Giorgio Armani Acqua Di Giò Parfum (₹5,999) "
                    "ची शिफारस करतो. यामध्ये ताज्या Bergamot आणि Marine notes असून दिवसभर 8-12 तास फ्रेश राहतात."
                )
            return (
                "For refined office and daytime wear, Christian Dior Sauvage EDP and Giorgio Armani Acqua Di Giò Parfum offer exceptional fresh sillage "
                "with crisp Italian citrus, oceanic mineral accords, and 8-12 hours of distinguished longevity."
            )
        else:
            return (
                "Welcome to SCENTIVA Privé Concierge. Our haute perfumery curation features authentic masterpieces "
                "from Christian Dior, Chanel, House of Creed, Tom Ford, and Maison Francis Kurkdjian with climate-regulated thermal shipping."
            )

def get_llm() -> BaseChatModel:
    """Instantiate and return the configured LangChain LLM."""
    provider = settings.LLM_PROVIDER.lower()
    
    if provider == "openai" and settings.LLM_API_KEY:
        try:
            from langchain_community.chat_models import ChatOpenAI
            logger.info(f"Initializing ChatOpenAI model={settings.LLM_MODEL}")
            return ChatOpenAI(
                model_name=settings.LLM_MODEL,
                api_key=settings.LLM_API_KEY,
                temperature=settings.LLM_TEMPERATURE,
                max_tokens=settings.LLM_MAX_TOKENS,
                timeout=settings.LLM_REQUEST_TIMEOUT_SECONDS,
            )
        except Exception as e:
            logger.warning(f"Failed to initialize ChatOpenAI ({e}), falling back to deterministic LLM")
            return DeterministicScentivaChatModel()
            
    # Default / Mock / Test provider
    logger.info(f"Using Scentiva Deterministic LLM provider={provider}")
    return DeterministicScentivaChatModel()
