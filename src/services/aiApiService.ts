import { apiClient } from '../lib/api/apiClient';
import { ApiResponse } from '../types';

export interface ScentQuizPayload {
  fragranceFamily?: string;
  occasion?: string;
  intensity?: string;
  season?: string;
  gender?: string;
  preferredNotes?: string[];
}

export interface ScentRecommendationBackendDto {
  productId: number;
  productName: string;
  productSlug: string;
  brandName: string;
  primaryImageUrl?: string;
  startingPrice: number;
  matchScore: number;
  recommendationReason: string;
  matchedNotes: string[];
}

export interface ScentQuizBackendResponse {
  personaTitle: string;
  personaDescription: string;
  dominantAccord: string;
  recommendations: ScentRecommendationBackendDto[];
}

export interface SemanticMatchBackendDto {
  productId: number;
  productName: string;
  productSlug: string;
  brandName: string;
  primaryImageUrl?: string;
  startingPrice: number;
  relevanceScore: number;
  extractedFamily: string;
  highlightedNotes: string[];
  matchExplanation: string;
}

export interface SemanticSearchBackendResponse {
  query: string;
  totalMatches: number;
  results: SemanticMatchBackendDto[];
}

export interface AiChatBackendResponse {
  reply: string;
  suggestedFollowUps: string[];
  recommendedProductSlugs: string[];
}

export interface ProductEditorialBackendResponse {
  productId: number;
  productName: string;
  brandName: string;
  editorialHeadline: string;
  narrativeStory: string;
  poeticNoteBreakdown: string;
}

export interface ProductSentimentBackendResponse {
  productId: number;
  productName: string;
  averageRating: number;
  totalReviewsAnalyzed: number;
  sentimentClassification: string;
  keyPros: string[];
  keyCons: string[];
  communityConsensus: string;
}

export const AiApiService = {
  async findScentByQuiz(quiz: ScentQuizPayload): Promise<ScentQuizBackendResponse> {
    const res = await apiClient.post<ScentQuizBackendResponse>('/ai/scent-finder', quiz);
    return res.data;
  },

  async semanticSearch(query: string, limit: number = 6): Promise<SemanticSearchBackendResponse> {
    const res = await apiClient.get<SemanticSearchBackendResponse>('/ai/semantic-search', { q: query, limit });
    return res.data;
  },

  async chatWithConcierge(message: string, conversationHistory?: Array<{ role: string; content: string }>): Promise<AiChatBackendResponse> {
    const res = await apiClient.post<AiChatBackendResponse>('/ai/concierge/chat', {
      message,
      conversationHistory,
    });
    return res.data;
  },

  async getEditorialDescription(productId: number): Promise<ProductEditorialBackendResponse> {
    const res = await apiClient.get<ProductEditorialBackendResponse>(`/ai/product/${productId}/editorial-description`);
    return res.data;
  },

  async getProductSentiment(productId: number): Promise<ProductSentimentBackendResponse> {
    const res = await apiClient.get<ProductSentimentBackendResponse>(`/ai/product/${productId}/sentiment-summary`);
    return res.data;
  },
};
