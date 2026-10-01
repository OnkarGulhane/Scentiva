import { Brand } from '../types';
import { BRANDS } from '../data/brands';

const BRANDS_STORAGE_KEY = 'scentiva_brands';

export const getStoredBrands = (): Brand[] => {
  if (typeof window === 'undefined') return BRANDS;
  try {
    const raw = localStorage.getItem(BRANDS_STORAGE_KEY);
    if (!raw) return BRANDS;
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed) && parsed.length > 0 ? parsed : BRANDS;
  } catch {
    return BRANDS;
  }
};

export const saveStoredBrands = (brands: Brand[]): void => {
  if (typeof window === 'undefined') return;
  try {
    localStorage.setItem(BRANDS_STORAGE_KEY, JSON.stringify(brands));
  } catch (err) {
    console.error('Failed to save brands to localStorage:', err);
  }
};

export const BrandService = {
  getAll: (): Brand[] => {
    return getStoredBrands();
  },

  getBySlug: (slug: string): Brand | undefined => {
    const all = getStoredBrands();
    return all.find(b => b.slug === slug || b.id === slug || b.id === `b-${slug}`);
  },

  getById: (id: string): Brand | undefined => {
    const all = getStoredBrands();
    return all.find(b => b.id === id);
  },

  create: (brandData: Omit<Brand, 'id' | 'slug'>): Brand => {
    const all = getStoredBrands();
    const slug = brandData.name.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)/g, '');
    const brand: Brand = {
      ...brandData,
      id: `b-${slug}-${Date.now()}`,
      slug
    };
    const updated = [...all, brand];
    saveStoredBrands(updated);
    return brand;
  },

  update: (id: string, updates: Partial<Brand>): Brand | null => {
    const all = getStoredBrands();
    const index = all.findIndex(b => b.id === id);
    if (index === -1) return null;
    const updated = [...all];
    updated[index] = { ...updated[index], ...updates };
    saveStoredBrands(updated);
    return updated[index];
  },

  delete: (id: string): boolean => {
    const all = getStoredBrands();
    const filtered = all.filter(b => b.id !== id);
    if (filtered.length === all.length) return false;
    saveStoredBrands(filtered);
    return true;
  }
};
