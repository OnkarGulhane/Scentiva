import { Product, FragranceFamily } from '../types';
import { ProductService } from './productService';

export interface FragranceProfileRequest {
  family?: FragranceFamily;
  occasion?: string;
  intensity?: 'Subtle' | 'Moderate' | 'Intense' | 'Beast Mode';
  budget?: string;
  gender?: 'Women' | 'Men' | 'Unisex' | 'All';
}

export interface FragranceRecommendationResult {
  primaryMatch: Product;
  confidenceScore: number;
  reasoning: string;
  alternativeMatches: Product[];
}

export interface IFragranceRecommendationService {
  getRecommendations(request: FragranceProfileRequest): Promise<FragranceRecommendationResult>;
}

/**
 * Deterministic Scent Profiler Engine
 * Implements the baseline recommendation algorithm with structured confidence weighting.
 * Architecture is prepared for seamless drop-in replacement by Spring Boot AI / Vector embedding service.
 */
export class DeterministicRecommendationService implements IFragranceRecommendationService {
  async getRecommendations(request: FragranceProfileRequest): Promise<FragranceRecommendationResult> {
    const allProducts = ProductService.getAll();

    // Score each product based on requested dimensions
    const scoredProducts = allProducts.map(product => {
      let score = 50; // base score

      if (request.family && product.fragranceFamilies && product.fragranceFamilies.includes(request.family)) {
        score += 30;
      }

      if (request.gender) {
        if (request.gender === 'All' || product.category === 'Unisex') {
          score += 15;
        } else if (request.gender === 'Women' && product.category === 'For Her') {
          score += 15;
        } else if (request.gender === 'Men' && product.category === 'For Him') {
          score += 15;
        }
      }

      if (request.intensity) {
        if (product.sillage === request.intensity) {
          score += 10;
        }
      }

      if (product.isBestSeller) score += 5;
      if (product.rating && product.rating >= 4.8) score += 5;

      return { product, score };
    });

    scoredProducts.sort((a, b) => b.score - a.score);

    const primary = scoredProducts[0]?.product || allProducts[0];
    const score = Math.min(99, Math.max(82, scoredProducts[0]?.score || 92));
    const alternatives = scoredProducts.slice(1, 4).map(s => s.product);

    return {
      primaryMatch: primary,
      confidenceScore: score,
      reasoning: `Based on your inclination towards ${request.family || 'niche'} accords and ${request.intensity || 'signature'} sillage, ${primary.name} presents the optimal balance of longevity and exquisite top-note diffusion.`,
      alternativeMatches: alternatives,
    };
  }
}

export const RecommendationService: IFragranceRecommendationService = new DeterministicRecommendationService();
