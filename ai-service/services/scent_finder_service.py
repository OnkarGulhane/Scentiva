from agents.scent_finder_agent import scent_finder_agent
from models.schemas import ScentFinderQuizRequest, ScentFinderQuizResponse

class ScentFinderService:
    @staticmethod
    def evaluate_quiz(request: ScentFinderQuizRequest) -> ScentFinderQuizResponse:
        return scent_finder_agent.evaluate_quiz(request)

scent_finder_service = ScentFinderService()
