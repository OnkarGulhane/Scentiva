import { Product, FragranceCategory, FragranceFamily } from '../types';
import { PRODUCTS } from '../data/products';

// Safe localStorage persistence key for mock repository
const PRODUCTS_STORAGE_KEY = 'scentiva_products';

export const getStoredProducts = (): Product[] => {
  if (typeof window === 'undefined') return PRODUCTS;
  try {
    const raw = localStorage.getItem(PRODUCTS_STORAGE_KEY);
    if (!raw) return PRODUCTS;
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed) && parsed.length > 0 ? parsed : PRODUCTS;
  } catch {
    return PRODUCTS;
  }
};

export const saveStoredProducts = (products: Product[]): void => {
  if (typeof window === 'undefined') return;
  try {
    localStorage.setItem(PRODUCTS_STORAGE_KEY, JSON.stringify(products));
  } catch (err) {
    console.error('Failed to save products to localStorage:', err);
  }
};

export const ProductService = {
  getAll: (): Product[] => {
    return getStoredProducts();
  },

  getBySlug: (slug: string): Product | undefined => {
    const all = getStoredProducts();
    return all.find(p => p.slug === slug || p.id === slug);
  },

  getById: (id: string): Product | undefined => {
    const all = getStoredProducts();
    return all.find(p => p.id === id);
  },

  getByBrand: (brandId: string): Product[] => {
    const all = getStoredProducts();
    return all.filter(p => p.brandId === brandId || p.brandId === `b-${brandId}`);
  },

  getByCategory: (category: FragranceCategory | string): Product[] => {
    const all = getStoredProducts();
    return all.filter(p => p.category.toLowerCase() === category.toLowerCase());
  },

  getByFamily: (family: FragranceFamily): Product[] => {
    const all = getStoredProducts();
    return all.filter(p => p.fragranceFamilies.includes(family));
  },

  getBestSellers: (limit: number = 8): Product[] => {
    const all = getStoredProducts();
    return all.filter(p => p.isBestSeller).slice(0, limit);
  },

  getNewArrivals: (limit: number = 8): Product[] => {
    const all = getStoredProducts();
    return all.filter(p => p.isNewArrival).slice(0, limit);
  },

  getRelated: (product: Product, limit: number = 4): Product[] => {
    const all = getStoredProducts();
    return all
      .filter(p => p.id !== product.id && (
        p.brandId === product.brandId ||
        p.category === product.category ||
        p.fragranceFamilies.some(f => product.fragranceFamilies.includes(f))
      ))
      .slice(0, limit);
  },

  create: (newProductData: Omit<Product, 'id' | 'slug'>): Product => {
    const all = getStoredProducts();
    const slug = newProductData.name.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)/g, '');
    const product: Product = {
      ...newProductData,
      id: `prod-${Date.now()}`,
      slug: slug || `perfume-${Date.now()}`
    };
    const updated = [product, ...all];
    saveStoredProducts(updated);
    return product;
  },

  update: (id: string, updates: Partial<Product>): Product | null => {
    const all = getStoredProducts();
    const index = all.findIndex(p => p.id === id);
    if (index === -1) return null;
    const updated = [...all];
    updated[index] = { ...updated[index], ...updates };
    saveStoredProducts(updated);
    return updated[index];
  },

  delete: (id: string): boolean => {
    const all = getStoredProducts();
    const filtered = all.filter(p => p.id !== id);
    if (filtered.length === all.length) return false;
    saveStoredProducts(filtered);
    return true;
  }
};
