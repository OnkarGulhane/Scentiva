from agents.assistant_agent import shopping_assistant_agent
from models.schemas import AssistantChatRequest, AssistantChatResponse

class ShoppingService:
    @staticmethod
    def process_chat(request: AssistantChatRequest) -> AssistantChatResponse:
        history = [{"role": m.role, "content": m.content} for m in request.conversationHistory]
        return shopping_assistant_agent.process_chat(
            message=request.message,
            conversation_history=history,
            user_context=request.userContext
        )

shopping_service = ShoppingService()
