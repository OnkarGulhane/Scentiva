import { CategoryItem, CATEGORIES } from '../data/categories';

const CATEGORIES_STORAGE_KEY = 'scentiva_categories';

export const getStoredCategories = (): CategoryItem[] => {
  if (typeof window === 'undefined') return CATEGORIES;
  try {
    const raw = localStorage.getItem(CATEGORIES_STORAGE_KEY);
    if (!raw) return CATEGORIES;
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed) && parsed.length > 0 ? parsed : CATEGORIES;
  } catch {
    return CATEGORIES;
  }
};

export const saveStoredCategories = (cats: CategoryItem[]): void => {
  if (typeof window === 'undefined') return;
  try {
    localStorage.setItem(CATEGORIES_STORAGE_KEY, JSON.stringify(cats));
  } catch (err) {
    console.error('Failed to save categories to localStorage:', err);
  }
};

export const CategoryService = {
  getAll: (): CategoryItem[] => {
    return getStoredCategories();
  },

  getBySlug: (slug: string): CategoryItem | undefined => {
    const all = getStoredCategories();
    return all.find(c => c.slug === slug || c.id === slug || c.title.toLowerCase() === slug.toLowerCase());
  },

  create: (catData: Omit<CategoryItem, 'id' | 'slug'>): CategoryItem => {
    const all = getStoredCategories();
    const slug = catData.title.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)/g, '');
    const category: CategoryItem = {
      ...catData,
      id: `cat-${slug}-${Date.now()}`,
      slug
    };
    const updated = [...all, category];
    saveStoredCategories(updated);
    return category;
  },

  update: (id: string, updates: Partial<CategoryItem>): CategoryItem | null => {
    const all = getStoredCategories();
    const index = all.findIndex(c => c.id === id);
    if (index === -1) return null;
    const updated = [...all];
    updated[index] = { ...updated[index], ...updates };
    saveStoredCategories(updated);
    return updated[index];
  },

  delete: (id: string): boolean => {
    const all = getStoredCategories();
    const filtered = all.filter(c => c.id !== id);
    if (filtered.length === all.length) return false;
    saveStoredCategories(filtered);
    return true;
  }
};
