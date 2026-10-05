/**
 * SCENTIVA — Universal Domain Contracts & TypeScript Types
 * Designed for full backend-readiness (Spring Boot REST / PostgreSQL / GraphQL).
 */

export type FragranceFamily = 
  | 'Fresh' 
  | 'Woody' 
  | 'Floral' 
  | 'Oriental' 
  | 'Amber'
  | 'Sweet & Gourmand' 
  | 'Spicy' 
  | 'Citrus' 
  | 'Aquatic' 
  | 'Aromatic';

export type FragranceCategory = 
  | 'For Her' 
  | 'For Him' 
  | 'Unisex' 
  | 'Luxury & Niche' 
  | 'Everyday' 
  | 'Gift Sets';

export type FragranceConcentration = 
  | 'Parfum' 
  | 'Eau de Parfum (EDP)' 
  | 'Eau de Toilette (EDT)' 
  | 'Eau de Cologne (EDC)' 
  | 'Extrait de Parfum';

export interface ProductVariant {
  size: string; // e.g. "30ml", "50ml", "100ml"
  price: number;
  mrp?: number;
  sku: string;
  inStock: boolean;
}

export interface OlfactoryPyramid {
  top: string[];
  heart: string[];
  base: string[];
}

export interface Product {
  id: string;
  slug: string;
  name: string;
  brandId: string;
  brandName: string;
  tagline?: string;
  category: FragranceCategory;
  fragranceFamilies: FragranceFamily[];
  concentration: FragranceConcentration;
  variants: ProductVariant[];
  selectedVariantIndex?: number;
  notes: OlfactoryPyramid;
  sillage: 'Intimate' | 'Moderate' | 'Strong' | 'Enormous';
  longevity: '4-6 Hours' | '6-8 Hours' | '8-12 Hours' | '12+ Hours';
  season: ('Spring' | 'Summer' | 'Autumn' | 'Winter' | 'All Season' | 'Night')[];
  occasion: ('Everyday' | 'Work / Office' | 'Date Night' | 'Evening Gala' | 'Party' | 'Special Occasion' | 'Casual Lunch' | 'Gifting')[];
  description: string;
  story?: string;
  images: string[];
  stock: number;
  rating: number;
  reviewCount: number;
  isBestSeller?: boolean;
  isNewArrival?: boolean;
  isFeatured?: boolean;
  discountPercentage?: number;
  metadata?: Record<string, string | number | boolean>;
}

export interface Brand {
  id: string;
  slug: string;
  name: string;
  origin: string;
  foundedYear: number;
  description: string;
  logoUrl?: string;
  bannerImage: string;
  featuredProductCount: number;
  tier: 'Luxury' | 'Niche' | 'Designer' | 'Artisanal';
  websiteUrl?: string;
}

export interface Category {
  id: string;
  slug: string;
  title: string;
  tagline: string;
  description: string;
  image: string;
  itemCount: number;
  accentColor?: string;
}

export interface CartItem {
  productId: string;
  product: Product;
  selectedVariant: ProductVariant;
  quantity: number;
}

export interface Address {
  id: string;
  fullName: string;
  phoneNumber: string;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state: string;
  pincode: string;
  type: 'Home' | 'Office' | 'Other';
  isDefault: boolean;
}

export type OrderStatus = 'Order Placed' | 'Processing' | 'Shipped' | 'Out for Delivery' | 'Delivered' | 'Cancelled';

export interface OrderItem {
  product: Product;
  variant: ProductVariant;
  quantity: number;
  price: number;
}

export interface Order {
  id: string;
  orderNumber: string;
  createdAt: string;
  items: OrderItem[];
  shippingAddress: Address;
  deliveryMethod: 'Standard Delivery' | 'Express Luxury Delivery';
  deliveryFee: number;
  subtotal: number;
  discount: number;
  couponCode?: string;
  total: number;
  status: OrderStatus;
  trackingNumber: string;
  estimatedDelivery: string;
  paymentMethod: 'Razorpay Secure (UPI, Cards, NetBanking)' | 'Razorpay' | 'UPI / QR' | 'Credit / Debit Card' | 'Net Banking' | 'Cash on Delivery' | string;
  paymentStatus: 'Paid' | 'Pending' | 'Demo Confirmed';
  timeline: {
    status: OrderStatus;
    timestamp: string;
    completed: boolean;
    description: string;
  }[];
}

export interface Coupon {
  code: string;
  discountType: 'percentage' | 'flat';
  discountValue: number;
  minOrderValue: number;
  description: string;
  expiresAt: string;
}

export interface QuizAnswers {
  family?: FragranceFamily;
  occasion?: string;
  intensity?: 'Light & Subtle' | 'Balanced & Elegant' | 'Bold & Intense';
  budget?: string;
  genderPreference?: 'For Her' | 'For Him' | 'Unisex' | 'Any';
}

export interface RecommendationMatch {
  product: Product;
  matchScore: number; // e.g. 96 (%)
  rationale: string;
  highlightedNotes: string[];
}

export interface Review {
  id: string;
  userName: string;
  userAvatar?: string;
  rating: number;
  title: string;
  comment: string;
  date: string;
  verifiedBuyer: boolean;
  fragranceNotesLiked: string[];
}

export interface Customer {
  id: string;
  name: string;
  email: string;
  phone: string;
  tier: 'Privé Bronze' | 'Privé Silver' | 'Privé Gold' | 'Privé Diamond';
  totalOrders: number;
  totalSpend: number;
  joinedDate: string;
  status: 'Active' | 'VIP' | 'Inactive';
}

export interface SearchFilterOptions {
  query?: string;
  brands?: string[];
  categories?: string[];
  families?: FragranceFamily[];
  concentrations?: string[];
  minPrice?: number;
  maxPrice?: number;
  inStockOnly?: boolean;
  sortBy?: 'recommended' | 'price-asc' | 'price-desc' | 'rating' | 'newest';
}

export interface SearchResult {
  items: Product[];
  totalCount: number;
  matchedBrands: string[];
  matchedFamilies: string[];
  matchedNotes: string[];
  page: number;
  pageSize: number;
  hasMore: boolean;
}

export interface Story {
  id: string;
  slug: string;
  title: string;
  subtitle: string;
  category: string;
  readTime: string;
  date: string;
  coverImage: string;
  author: {
    name: string;
    role: string;
    avatar: string;
  };
  content: string[];
  featuredFragrances?: string[]; // Product slugs
}

// ----------------------------------------------------------------------------
// Backend API Contracts & Response Wrappers
// ----------------------------------------------------------------------------

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  timestamp: string;
}

export interface ApiPaginatedResponse<T> {
  success: boolean;
  data: T[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
  timestamp: string;
}

export interface ApiErrorResponse {
  success: false;
  error: {
    code: string;
    message: string;
    details?: Record<string, unknown>;
  };
  timestamp: string;
}

// ----------------------------------------------------------------------------
// Analytics Event Types (Section 39 Contract)
// ----------------------------------------------------------------------------

export type AnalyticsEventType =
  | 'scent_finder_started'
  | 'scent_finder_completed'
  | 'fragrance_note_clicked'
  | 'fragrance_profile_viewed'
  | '3d_product_interaction'
  | '3d_product_rotated'
  | 'product_viewed'
  | 'wishlist_added'
  | 'wishlist_removed'
  | 'cart_added'
  | 'cart_removed'
  | 'search_started'
  | 'search_zero_result'
  | 'recommendation_viewed'
  | 'recommendation_clicked'
  | 'checkout_started'
  | 'purchase_completed';

export interface AnalyticsPayload {
  eventName: AnalyticsEventType;
  properties?: Record<string, string | number | boolean | string[] | undefined>;
  timestamp: string;
}

// ----------------------------------------------------------------------------
// Feature Flags Contract (Section 40)
// ----------------------------------------------------------------------------

export interface FeatureFlags {
  ENABLE_3D_HERO: boolean;
  ENABLE_SCENT_FINDER: boolean;
  ENABLE_AI_SEARCH: boolean;
  ENABLE_PERSONALIZATION: boolean;
  ENABLE_RECOMMENDATIONS: boolean;
}
