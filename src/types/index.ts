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

export type ProductVariant = {
  size: string; // e.g. "30ml", "50ml", "100ml"
  price: number;
  mrp?: number;
  sku: string;
  inStock: boolean;
};

export type Product = {
  id: string;
  slug: string;
  name: string;
  brandId: string;
  brandName: string;
  tagline?: string;
  category: FragranceCategory;
  fragranceFamilies: FragranceFamily[];
  concentration: 'Parfum' | 'Eau de Parfum (EDP)' | 'Eau de Toilette (EDT)' | 'Eau de Cologne (EDC)' | 'Extrait de Parfum';
  variants: ProductVariant[];
  selectedVariantIndex?: number;
  notes: {
    top: string[];
    heart: string[];
    base: string[];
  };
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
};

export type Brand = {
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
};

export type CartItem = {
  productId: string;
  product: Product;
  selectedVariant: ProductVariant;
  quantity: number;
};

export type Address = {
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
};

export type OrderStatus = 'Order Placed' | 'Processing' | 'Shipped' | 'Out for Delivery' | 'Delivered' | 'Cancelled';

export type OrderItem = {
  product: Product;
  variant: ProductVariant;
  quantity: number;
  price: number;
};

export type Order = {
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
  paymentMethod: 'UPI / QR' | 'Credit / Debit Card' | 'Net Banking' | 'Cash on Delivery';
  paymentStatus: 'Paid' | 'Pending' | 'Demo Confirmed';
  timeline: {
    status: OrderStatus;
    timestamp: string;
    completed: boolean;
    description: string;
  }[];
};

export type Coupon = {
  code: string;
  discountType: 'percentage' | 'flat';
  discountValue: number;
  minOrderValue: number;
  description: string;
  expiresAt: string;
};

export type QuizAnswers = {
  family?: FragranceFamily;
  occasion?: string;
  intensity?: 'Light & Subtle' | 'Balanced & Elegant' | 'Bold & Intense';
  budget?: string;
  genderPreference?: 'For Her' | 'For Him' | 'Unisex' | 'Any';
};

export type Review = {
  id: string;
  userName: string;
  userAvatar?: string;
  rating: number;
  title: string;
  comment: string;
  date: string;
  verifiedBuyer: boolean;
  fragranceNotesLiked: string[];
};

export type Customer = {
  id: string;
  name: string;
  email: string;
  phone: string;
  tier: 'Privé Bronze' | 'Privé Silver' | 'Privé Gold' | 'Privé Diamond';
  totalOrders: number;
  totalSpend: number;
  joinedDate: string;
  status: 'Active' | 'VIP' | 'Inactive';
};

export type SearchFilterOptions = {
  query?: string;
  brands?: string[];
  categories?: string[];
  families?: FragranceFamily[];
  concentrations?: string[];
  minPrice?: number;
  maxPrice?: number;
  inStockOnly?: boolean;
  sortBy?: 'recommended' | 'price-asc' | 'price-desc' | 'rating' | 'newest';
};

export type SearchResult = {
  items: Product[];
  totalCount: number;
  matchedBrands: string[];
  matchedFamilies: string[];
  matchedNotes: string[];
  page: number;
  pageSize: number;
  hasMore: boolean;
};

