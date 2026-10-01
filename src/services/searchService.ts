import { Product, SearchFilterOptions, SearchResult } from '../types';
import { ProductService } from './productService';

const RECENT_SEARCHES_KEY = 'scentiva_recent_searches';

export const POPULAR_SEARCHES = [
  'Sauvage',
  'Coco Mademoiselle',
  'Vanilla & Amber',
  'Creed Aventus',
  'Fresh Citrus',
  'Date Night',
  'Woody Oud',
  'Byredo'
];

export const SearchService = {
  getRecentSearches: (): string[] => {
    if (typeof window === 'undefined') return [];
    try {
      const raw = localStorage.getItem(RECENT_SEARCHES_KEY);
      return raw ? JSON.parse(raw) : [];
    } catch {
      return [];
    }
  },

  addRecentSearch: (query: string): void => {
    if (typeof window === 'undefined' || !query.trim()) return;
    try {
      const trimmed = query.trim();
      const recents = SearchService.getRecentSearches().filter(q => q.toLowerCase() !== trimmed.toLowerCase());
      const updated = [trimmed, ...recents].slice(0, 6);
      localStorage.setItem(RECENT_SEARCHES_KEY, JSON.stringify(updated));
    } catch (err) {
      console.error('Failed to save recent search:', err);
    }
  },

  clearRecentSearches: (): void => {
    if (typeof window === 'undefined') return;
    try {
      localStorage.removeItem(RECENT_SEARCHES_KEY);
    } catch (err) {
      console.error('Failed to clear recent searches:', err);
    }
  },

  getSuggestions: (query: string, limit: number = 6): {
    products: Product[];
    brands: string[];
    notes: string[];
  } => {
    const cleanQ = query.trim().toLowerCase();
    if (!cleanQ) {
      return { products: [], brands: [], notes: [] };
    }

    const allProducts = ProductService.getAll();

    const matchedProducts = allProducts.filter(p =>
      p.name.toLowerCase().includes(cleanQ) ||
      p.brandName.toLowerCase().includes(cleanQ) ||
      p.fragranceFamilies.some(f => f.toLowerCase().includes(cleanQ)) ||
      p.notes.top.concat(p.notes.heart, p.notes.base).some(n => n.toLowerCase().includes(cleanQ))
    ).slice(0, limit);

    const brandsSet = new Set<string>();
    const notesSet = new Set<string>();

    allProducts.forEach(p => {
      if (p.brandName.toLowerCase().includes(cleanQ)) {
        brandsSet.add(p.brandName);
      }
      p.notes.top.concat(p.notes.heart, p.notes.base).forEach(n => {
        if (n.toLowerCase().includes(cleanQ)) {
          notesSet.add(n);
        }
      });
    });

    return {
      products: matchedProducts,
      brands: Array.from(brandsSet).slice(0, 4),
      notes: Array.from(notesSet).slice(0, 4)
    };
  },

  /**
   * Comprehensive search with filters and pagination
   * Clean extension point for future AI/Vector search integration
   */
  search: (
    options: SearchFilterOptions,
    page: number = 1,
    pageSize: number = 12
  ): SearchResult => {
    const all = ProductService.getAll();
    const q = (options.query || '').trim().toLowerCase();

    const matchedBrandsSet = new Set<string>();
    const matchedFamiliesSet = new Set<string>();
    const matchedNotesSet = new Set<string>();

    let filtered = all.filter(p => {
      // Query filter
      if (q) {
        const nameMatch = p.name.toLowerCase().includes(q);
        const brandMatch = p.brandName.toLowerCase().includes(q);
        const familyMatch = p.fragranceFamilies.some(f => f.toLowerCase().includes(q));
        const allNotes = p.notes.top.concat(p.notes.heart, p.notes.base);
        const noteMatch = allNotes.some(n => n.toLowerCase().includes(q));
        const descMatch = p.description.toLowerCase().includes(q);

        if (!nameMatch && !brandMatch && !familyMatch && !noteMatch && !descMatch) {
          return false;
        }

        if (brandMatch) matchedBrandsSet.add(p.brandName);
        p.fragranceFamilies.forEach(f => {
          if (f.toLowerCase().includes(q)) matchedFamiliesSet.add(f);
        });
        allNotes.forEach(n => {
          if (n.toLowerCase().includes(q)) matchedNotesSet.add(n);
        });
      }

      // Brand Filter
      if (options.brands && options.brands.length > 0) {
        const hasBrand = options.brands.some(
          b => p.brandId === b || p.brandName.toLowerCase() === b.toLowerCase() || p.brandId === `b-${b}`
        );
        if (!hasBrand) return false;
      }

      // Category Filter
      if (options.categories && options.categories.length > 0) {
        const hasCat = options.categories.some(
          c => p.category.toLowerCase() === c.toLowerCase()
        );
        if (!hasCat) return false;
      }

      // Family Filter
      if (options.families && options.families.length > 0) {
        const hasFam = options.families.some(f => p.fragranceFamilies.includes(f));
        if (!hasFam) return false;
      }

      // Concentration Filter
      if (options.concentrations && options.concentrations.length > 0) {
        const hasConc = options.concentrations.some(c => p.concentration.includes(c));
        if (!hasConc) return false;
      }

      // Price Range Filter
      const defaultVariantPrice = p.variants[0]?.price || 0;
      if (options.minPrice !== undefined && defaultVariantPrice < options.minPrice) return false;
      if (options.maxPrice !== undefined && defaultVariantPrice > options.maxPrice) return false;

      // In-stock Filter
      if (options.inStockOnly && p.stock <= 0) return false;

      return true;
    });

    // Sorting
    switch (options.sortBy) {
      case 'price-asc':
        filtered.sort((a, b) => (a.variants[0]?.price || 0) - (b.variants[0]?.price || 0));
        break;
      case 'price-desc':
        filtered.sort((a, b) => (b.variants[0]?.price || 0) - (a.variants[0]?.price || 0));
        break;
      case 'rating':
        filtered.sort((a, b) => b.rating - a.rating);
        break;
      case 'newest':
        filtered.sort((a, b) => (b.isNewArrival ? 1 : 0) - (a.isNewArrival ? 1 : 0));
        break;
      case 'recommended':
      default:
        filtered.sort((a, b) => (b.isBestSeller ? 1 : 0) - (a.isBestSeller ? 1 : 0));
        break;
    }

    const totalCount = filtered.length;
    const startIndex = (page - 1) * pageSize;
    const paginatedItems = filtered.slice(0, startIndex + pageSize); // Cumulative load-more support

    return {
      items: paginatedItems,
      totalCount,
      matchedBrands: Array.from(matchedBrandsSet),
      matchedFamilies: Array.from(matchedFamiliesSet),
      matchedNotes: Array.from(matchedNotesSet),
      page,
      pageSize,
      hasMore: startIndex + pageSize < totalCount
    };
  }
};
