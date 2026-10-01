import { Product, SearchFilterOptions, SearchResult, FragranceFamily } from '../types';
import { ProductService } from './productService';

const RECENT_SEARCHES_KEY = 'scentiva_recent_searches';

export const POPULAR_SEARCHES = [
  'Sauvage',
  'Coco Mademoiselle',
  'Vanilla & Amber',
  'Creed Aventus',
  'Fresh Citrus',
  'Date Night Perfume',
  'Woody Oud under ₹15000',
  'Byredo'
];

interface NaturalLanguageIntent {
  cleanQuery: string;
  detectedFamily?: FragranceFamily;
  detectedMaxPrice?: number;
  detectedGender?: 'For Her' | 'For Him' | 'Unisex';
  detectedOccasion?: string;
  detectedSeason?: string;
}

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

  removeRecentSearch: (query: string): void => {
    if (typeof window === 'undefined') return;
    try {
      const recents = SearchService.getRecentSearches().filter(q => q.toLowerCase() !== query.toLowerCase());
      localStorage.setItem(RECENT_SEARCHES_KEY, JSON.stringify(recents));
    } catch (err) {
      console.error('Failed to remove recent search:', err);
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

  /**
   * Natural Language Intent Extraction (Section 21)
   * Parses queries like "fresh office perfume under ₹5000" into structured search parameters.
   */
  parseNaturalLanguageIntent: (rawQuery: string): NaturalLanguageIntent => {
    let q = rawQuery.toLowerCase();
    let detectedMaxPrice: number | undefined;
    let detectedFamily: FragranceFamily | undefined;
    let detectedGender: 'For Her' | 'For Him' | 'Unisex' | undefined;
    let detectedOccasion: string | undefined;
    let detectedSeason: string | undefined;

    // Price extraction: "under 5000", "below ₹3000", "< 10000"
    const priceMatch = q.match(/(?:under|below|<|less than)\s*₹?\s*(\d+)/i);
    if (priceMatch && priceMatch[1]) {
      detectedMaxPrice = parseInt(priceMatch[1], 10);
      q = q.replace(priceMatch[0], ' ');
    }

    // Family extraction
    const familyMap: Record<string, FragranceFamily> = {
      fresh: 'Fresh',
      woody: 'Woody',
      floral: 'Floral',
      oriental: 'Oriental',
      amber: 'Amber',
      citrus: 'Citrus',
      aquatic: 'Aquatic',
      spicy: 'Spicy',
      aromatic: 'Aromatic',
      gourmand: 'Sweet & Gourmand',
      sweet: 'Sweet & Gourmand',
      vanilla: 'Sweet & Gourmand'
    };

    for (const [key, fam] of Object.entries(familyMap)) {
      if (new RegExp(`\\b${key}\\b`, 'i').test(q)) {
        detectedFamily = fam;
        break;
      }
    }

    // Gender extraction
    if (/\b(for him|men|mens|man|male)\b/i.test(q)) {
      detectedGender = 'For Him';
    } else if (/\b(for her|women|womens|woman|female)\b/i.test(q)) {
      detectedGender = 'For Her';
    } else if (/\b(unisex|genderless)\b/i.test(q)) {
      detectedGender = 'Unisex';
    }

    // Occasion extraction
    if (/\b(office|work|corporate|formal)\b/i.test(q)) detectedOccasion = 'Work / Office';
    else if (/\b(date|dating|romance|romantic)\b/i.test(q)) detectedOccasion = 'Date Night';
    else if (/\b(party|clubbing|night out)\b/i.test(q)) detectedOccasion = 'Party';
    else if (/\b(gala|wedding|special event)\b/i.test(q)) detectedOccasion = 'Special Occasion';

    // Season extraction
    if (/\b(summer)\b/i.test(q)) detectedSeason = 'Summer';
    else if (/\b(winter)\b/i.test(q)) detectedSeason = 'Winter';
    else if (/\b(spring)\b/i.test(q)) detectedSeason = 'Spring';
    else if (/\b(autumn|fall)\b/i.test(q)) detectedSeason = 'Autumn';

    // Clean common stopwords: perfume, fragrance, cologne, scent, for, best, under
    const cleanTokens = q
      .replace(/\b(perfume|fragrances?|cologne|scent|for|best|good|under|below|top)\b/gi, ' ')
      .replace(/\s+/g, ' ')
      .trim();

    return {
      cleanQuery: cleanTokens,
      detectedFamily,
      detectedMaxPrice,
      detectedGender,
      detectedOccasion,
      detectedSeason
    };
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
   * Comprehensive search with natural language intent extraction, faceted filters and pagination
   * Clean extension point for future AI/Vector search integration
   */
  search: (
    options: SearchFilterOptions,
    page: number = 1,
    pageSize: number = 12
  ): SearchResult => {
    const all = ProductService.getAll();
    const rawQ = (options.query || '').trim();
    const intent = SearchService.parseNaturalLanguageIntent(rawQ);
    const q = intent.cleanQuery.toLowerCase();

    const matchedBrandsSet = new Set<string>();
    const matchedFamiliesSet = new Set<string>();
    const matchedNotesSet = new Set<string>();

    let filtered = all.filter(p => {
      // 1. Natural Language Max Price
      const effectiveMaxPrice = options.maxPrice ?? intent.detectedMaxPrice;
      const defaultVariantPrice = p.variants[0]?.price || 0;
      if (effectiveMaxPrice !== undefined && defaultVariantPrice > effectiveMaxPrice) {
        return false;
      }

      // 2. Natural Language Family
      if (intent.detectedFamily && !p.fragranceFamilies.includes(intent.detectedFamily)) {
        // Soft match: only filter if specific query keyword wasn't also matched in name
        if (!p.name.toLowerCase().includes(q)) {
          return false;
        }
      }

      // 3. Natural Language Gender
      if (intent.detectedGender && p.category !== 'Unisex' && p.category !== intent.detectedGender) {
        return false;
      }

      // 4. Natural Language Occasion
      if (intent.detectedOccasion && !p.occasion.some(o => o.toLowerCase().includes(intent.detectedOccasion!.toLowerCase()))) {
        // Soft match
      }

      // 5. Keyword Matching
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

      // 6. Explicit Brand Filter
      if (options.brands && options.brands.length > 0) {
        const hasBrand = options.brands.some(
          b => p.brandId === b || p.brandName.toLowerCase() === b.toLowerCase() || p.brandId === `b-${b}`
        );
        if (!hasBrand) return false;
      }

      // 7. Explicit Category Filter
      if (options.categories && options.categories.length > 0) {
        const hasCat = options.categories.some(
          c => p.category.toLowerCase() === c.toLowerCase()
        );
        if (!hasCat) return false;
      }

      // 8. Explicit Family Filter
      if (options.families && options.families.length > 0) {
        const hasFam = options.families.some(f => p.fragranceFamilies.includes(f));
        if (!hasFam) return false;
      }

      // 9. Explicit Concentration Filter
      if (options.concentrations && options.concentrations.length > 0) {
        const hasConc = options.concentrations.some(c => p.concentration.includes(c as any));
        if (!hasConc) return false;
      }

      // 10. Explicit Min Price
      if (options.minPrice !== undefined && defaultVariantPrice < options.minPrice) return false;

      // 11. In-stock Filter
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
