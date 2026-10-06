from agents.search_agent import semantic_search_agent
from models.schemas import SemanticSearchQueryRequest, SemanticSearchQueryResponse

class SearchService:
    @staticmethod
    def execute_semantic_search(request: SemanticSearchQueryRequest) -> SemanticSearchQueryResponse:
        return semantic_search_agent.execute_search(
            query=request.query,
            limit=request.limit,
            max_price=request.maxPrice,
            fragrance_family=request.fragranceFamily,
            gender=request.gender
        )

search_service = SearchService()
