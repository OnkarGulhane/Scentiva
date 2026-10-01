import { apiClient } from '../lib/api/apiClient';
import { ApiPaginatedResponse, ApiResponse } from '../types';

export interface ProductVariantBackendDto {
  id: number;
  sku: string;
  volumeMl: number;
  concentration: string;
  basePrice: number;
  salePrice?: number;
  effectivePrice: number;
  isAvailable: boolean;
  totalAvailableQuantity?: number;
}

export interface OlfactoryPyramidBackendDto {
  topNotes: string[];
  heartNotes: string[];
  baseNotes: string[];
  sillage: string;
  longevityHours: number;
  seasonality: string[];
}

export interface ProductImageBackendDto {
  id: number;
  imageUrl: string;
  altText: string;
  isPrimary: boolean;
  displayOrder: number;
}

export interface ProductSummaryBackendResponse {
  id: number;
  name: string;
  slug: string;
  brandName: string;
  brandSlug: string;
  categoryName: string;
  categorySlug: string;
  gender: string;
  primaryImageUrl?: string;
  minPrice: number;
  maxPrice: number;
  ratingAverage: number;
  reviewCount: number;
  isBestSeller: boolean;
  isNewArrival: boolean;
  isFeatured: boolean;
}

export interface ProductDetailBackendResponse {
  id: number;
  name: string;
  slug: string;
  brandName: string;
  brandSlug: string;
  categoryName: string;
  categorySlug: string;
  description: string;
  perfumerName?: string;
  gender: string;
  originCountry: string;
  yearReleased?: number;
  isBestSeller: boolean;
  isNewArrival: boolean;
  isFeatured: boolean;
  ratingAverage: number;
  reviewCount: number;
  variants: ProductVariantBackendDto[];
  images: ProductImageBackendDto[];
  pyramid?: OlfactoryPyramidBackendDto;
}

export interface BrandBackendResponse {
  id: number;
  name: string;
  slug: string;
  description?: string;
  logoUrl?: string;
  heroBannerUrl?: string;
  originCountry: string;
  foundedYear?: number;
  tier: string;
  isFeatured: boolean;
  productCount?: number;
}

export interface CategoryBackendResponse {
  id: number;
  name: string;
  slug: string;
  description?: string;
  imageUrl?: string;
  displayOrder: number;
  isActive: boolean;
  productCount?: number;
}

export const CatalogApiService = {
  async fetchProducts(params?: {
    page?: number;
    size?: number;
    brandId?: number;
    categoryId?: number;
    gender?: string;
    minPrice?: number;
    maxPrice?: number;
    search?: string;
    sort?: string;
  }): Promise<ApiPaginatedResponse<ProductSummaryBackendResponse>> {
    return apiClient.getPaginated<ProductSummaryBackendResponse>('/products', params);
  },

  async fetchProductBySlug(slug: string): Promise<ProductDetailBackendResponse> {
    const res = await apiClient.get<ProductDetailBackendResponse>(`/products/${slug}`);
    return res.data;
  },

  async fetchFeaturedProducts(): Promise<ProductSummaryBackendResponse[]> {
    const res = await apiClient.get<ProductSummaryBackendResponse[]>('/products/featured');
    return res.data;
  },

  async fetchBrands(): Promise<BrandBackendResponse[]> {
    const res = await apiClient.get<BrandBackendResponse[]>('/brands');
    return res.data;
  },

  async fetchCategories(): Promise<CategoryBackendResponse[]> {
    const res = await apiClient.get<CategoryBackendResponse[]>('/categories');
    return res.data;
  },
};
