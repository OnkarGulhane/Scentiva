from agents.recommendation_agent import recommendation_agent
from models.schemas import PersonalizedRecommendationsRequest, PersonalizedRecommendationsResponse

class RecommendationService:
    @staticmethod
    def get_recommendations(request: PersonalizedRecommendationsRequest) -> PersonalizedRecommendationsResponse:
        return recommendation_agent.generate_recommendations(request)

recommendation_service = RecommendationService()
