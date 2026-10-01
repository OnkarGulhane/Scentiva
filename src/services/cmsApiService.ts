import { apiClient } from '../lib/api/apiClient';
import { ApiPaginatedResponse, ApiResponse } from '../types';

export interface EditorialStoryBackendResponse {
  id: number;
  title: string;
  slug: string;
  subtitle?: string;
  excerpt?: string;
  contentHtml: string;
  coverImageUrl?: string;
  authorName: string;
  readingTimeMinutes: number;
  isFeatured: boolean;
  publishedAt?: string;
  tags?: string;
  createdAt: string;
}

export interface BannerBackendResponse {
  id: number;
  title: string;
  subtitle?: string;
  ctaText?: string;
  ctaLink?: string;
  imageUrl: string;
  mobileImageUrl?: string;
  placement: string;
  displayOrder: number;
  isActive: boolean;
  isCurrentlyRunning: boolean;
  startsAt?: string;
  endsAt?: string;
}

export interface ProductSeoBackendResponse {
  title: string;
  description: string;
  canonicalUrl: string;
  openGraph: Record<string, string>;
  jsonLdSchema: string;
}

export interface BrandSeoBackendResponse {
  title: string;
  description: string;
  canonicalUrl: string;
  openGraph: Record<string, string>;
  jsonLdSchema: string;
}

export interface SitemapEntryBackendDto {
  url: string;
  lastModified: string;
  changeFrequency: string;
  priority: number;
}

export const CmsApiService = {
  async getPublishedStories(page: number = 0, size: number = 10): Promise<ApiPaginatedResponse<EditorialStoryBackendResponse>> {
    return apiClient.getPaginated<EditorialStoryBackendResponse>('/stories', { page, size });
  },

  async getFeaturedStories(): Promise<EditorialStoryBackendResponse[]> {
    const res = await apiClient.get<EditorialStoryBackendResponse[]>('/stories/featured');
    return res.data;
  },

  async getStoryBySlug(slug: string): Promise<EditorialStoryBackendResponse> {
    const res = await apiClient.get<EditorialStoryBackendResponse>(`/stories/${slug}`);
    return res.data;
  },

  async getBanners(placement?: string): Promise<BannerBackendResponse[]> {
    const res = await apiClient.get<BannerBackendResponse[]>('/banners', { placement });
    return res.data;
  },

  async getProductSeo(slug: string): Promise<ProductSeoBackendResponse> {
    const res = await apiClient.get<ProductSeoBackendResponse>(`/seo/product/${slug}`);
    return res.data;
  },

  async getBrandSeo(slug: string): Promise<BrandSeoBackendResponse> {
    const res = await apiClient.get<BrandSeoBackendResponse>(`/seo/brand/${slug}`);
    return res.data;
  },

  async getSitemapEntries(): Promise<SitemapEntryBackendDto[]> {
    const res = await apiClient.get<SitemapEntryBackendDto[]>('/seo/sitemap');
    return res.data;
  },
};
