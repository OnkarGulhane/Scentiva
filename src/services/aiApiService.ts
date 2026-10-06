import { apiClient } from '../lib/api/apiClient';
import { ApiResponse } from '../types';

export interface ScentQuizPayload {
  fragranceFamily?: string;
  occasion?: string;
  intensity?: string;
  season?: string;
  gender?: string;
  preferredNotes?: string[];
  budgetMax?: number;
}

export interface ScentRecommendationBackendDto {
  productId: number | string;
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
  dominantAccord?: string;
  recommendedFamily?: string;
  recommendations: ScentRecommendationBackendDto[];
}

export interface SemanticMatchBackendDto {
  productId: number | string;
  productName: string;
  productSlug: string;
  brandName: string;
  primaryImageUrl?: string;
  startingPrice: number;
  relevanceScore: number;
  extractedFamily?: string;
  highlightedNotes: string[];
  matchExplanation: string;
}

export interface SemanticSearchBackendResponse {
  query: string;
  interpretedIntent?: string;
  totalMatches: number;
  detectedNotes?: string[];
  detectedEmotions?: string[];
  results: SemanticMatchBackendDto[];
}

export interface AiShoppingAssistantPayload {
  query: string;
  conversationId?: string;
  userId?: string;
  userContext?: Record<string, any>;
  filters?: Record<string, any>;
}

export interface AiAssistantProductItem {
  id: number | string;
  productId: number | string;
  name: string;
  brandName: string;
  price: number;
  effectivePrice?: number;
  inStock: boolean;
  reason: string;
  primaryImageUrl?: string;
  slug: string;
}

export interface AiAssistantActionItem {
  type: string;
  productId?: number | string;
  slug?: string;
  label?: string;
}

export interface AiShoppingAssistantResponse {
  success: boolean;
  intent: string;
  message: string;
  recommendedProducts: AiAssistantProductItem[];
  extractedCriteria?: Record<string, any>;
  actions?: AiAssistantActionItem[];
  suggestedFollowups?: string[];
}

export interface AiRecommendationItem {
  productId: number | string;
  name: string;
  brand: string;
  family: string;
  slug: string;
  reason: string;
  effectivePrice?: number;
  imageUrl?: string;
}

export interface AiPersonalizedRecommendationsResponse {
  success: boolean;
  headline: string;
  explanation: string;
  recommendations: AiRecommendationItem[];
}

export interface AiSupportPayload {
  message: string;
  conversationId?: string;
  userId?: string;
  authToken?: string;
}

export interface AiSupportResponse {
  success: boolean;
  category: string;
  message: string;
  orderInfo?: Record<string, any> | null;
  suggestedActions?: Array<{ label: string; action: string; target: string }>;
  sources?: string[];
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
  // 1. AI Shopping Assistant
  async shoppingAssistantChat(payload: AiShoppingAssistantPayload): Promise<AiShoppingAssistantResponse> {
    try {
      const res = await apiClient.post<AiShoppingAssistantResponse>('/ai/assistant/chat', payload);
      return res.data;
    } catch (e) {
      console.warn('AI Assistant API call fallback triggered:', e);
      return {
        success: true,
        intent: 'product_search',
        message: 'Here are our most coveted signature fragrances crafted with natural essences:',
        recommendedProducts: [],
        actions: [],
        suggestedFollowups: [
          'Looking for a summer fresh perfume under ₹3000',
          'Tell me about long lasting evening woody perfumes',
          'What is the difference between Extrait and EDP?'
        ]
      };
    }
  },

  // 2. AI Semantic Search
  async semanticSearch(query: string, limit: number = 8): Promise<SemanticSearchBackendResponse> {
    try {
      const res = await apiClient.get<SemanticSearchBackendResponse>('/ai/semantic-search', { q: query, limit });
      return res.data;
    } catch (e) {
      console.warn('Semantic Search API fallback:', e);
      return {
        query,
        totalMatches: 0,
        results: []
      };
    }
  },

  // 3. AI Scent Finder Quiz
  async findScentByQuiz(quiz: ScentQuizPayload): Promise<ScentQuizBackendResponse> {
    const res = await apiClient.post<ScentQuizBackendResponse>('/ai/scent-finder', quiz);
    return res.data;
  },

  // 4. Personalized AI Recommendations
  async getPersonalizedRecommendations(params?: {
    userId?: string;
    viewed?: string[];
    cart?: string[];
    wishlist?: string[];
    brands?: string[];
    families?: string[];
    limit?: number;
  }): Promise<AiPersonalizedRecommendationsResponse> {
    try {
      const queryParams: Record<string, any> = {
        limit: params?.limit || 4
      };
      if (params?.userId) queryParams.userId = params.userId;
      if (params?.viewed && params.viewed.length) queryParams.viewed = params.viewed;
      if (params?.cart && params.cart.length) queryParams.cart = params.cart;
      if (params?.wishlist && params.wishlist.length) queryParams.wishlist = params.wishlist;
      if (params?.brands && params.brands.length) queryParams.brands = params.brands;
      if (params?.families && params.families.length) queryParams.families = params.families;

      const res = await apiClient.get<AiPersonalizedRecommendationsResponse>('/ai/recommendations', queryParams);
      return res.data;
    } catch (e) {
      console.warn('AI Recommendations fallback:', e);
      return {
        success: true,
        headline: 'Curated For You',
        explanation: 'Timeless masterpieces from our haute parfumerie collection.',
        recommendations: []
      };
    }
  },

  // 5. AI Customer Support & Live Tracking
  async customerSupportChat(payload: AiSupportPayload): Promise<AiSupportResponse> {
    try {
      const res = await apiClient.post<AiSupportResponse>('/ai/support/chat', payload);
      return res.data;
    } catch (e) {
      console.warn('AI Support API fallback:', e);
      return {
        success: true,
        category: 'policy',
        message: 'Welcome to Scentiva Concierge. All orders feature 100% authenticity guarantee, climate-controlled luxury packaging, and 7-day hassle-free returns on sealed flacons.',
        suggestedActions: [
          { label: 'View Orders', action: 'NAVIGATE', target: '/account/orders' },
          { label: 'Return Policy', action: 'NAVIGATE', target: '/returns' }
        ],
        sources: ['Scentiva Official Policies']
      };
    }
  },

  // Legacy / Editorial APIs
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
