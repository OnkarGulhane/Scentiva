from agents.support_agent import support_agent
from models.schemas import SupportChatRequest, SupportChatResponse

class SupportService:
    @staticmethod
    def process_support(request: SupportChatRequest) -> SupportChatResponse:
        return support_agent.process_support_query(request)

support_service = SupportService()
