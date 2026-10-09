'use client';

import React, { useState, useMemo, useEffect } from 'react';
import { useSearchParams } from '../hooks/useNavigation';
import { useStore } from '../context/StoreContext';
import { ProductCard } from '../components/product/ProductCard';
import { BRANDS } from '../data/brands';
import { CATEGORIES } from '../data/categories';
import { FragranceFamily, FragranceCategory } from '../types';
import { 
  Filter, 
  X, 
  ChevronDown, 
  RotateCcw, 
  Grid3X3, 
  List, 
  SlidersHorizontal,
  Search,
  Sparkles
} from 'lucide-react';

const FRAGRANCE_FAMILIES: FragranceFamily[] = [
  'Fresh',
  'Woody',
  'Floral',
  'Oriental',
  'Sweet & Gourmand',
  'Spicy',
  'Citrus',
  'Aquatic',
  'Aromatic'
];

export const ShopPage: React.FC = () => {
  const { products, formatPrice } = useStore();
  const [searchParams, setSearchParams] = useSearchParams();

  const searchQuery = searchParams.get('search') || searchParams.get('q') || '';
  const initialBrand = searchParams.get('brand') || '';
  const initialCategory = searchParams.get('category') || '';

  // Filter States
  const [selectedBrands, setSelectedBrands] = useState<string[]>(
    initialBrand ? [initialBrand] : []
  );
  const [selectedCategories, setSelectedCategories] = useState<string[]>(
    initialCategory ? [initialCategory] : []
  );
  const [selectedFamilies, setSelectedFamilies] = useState<string[]>([]);
  const [selectedConcentrations, setSelectedConcentrations] = useState<string[]>([]);
  const [priceRange, setPriceRange] = useState<number>(35000);
  const [sortBy, setSortBy] = useState<string>('recommended');
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid');
  const [mobileFiltersOpen, setMobileFiltersOpen] = useState(false);
  const [visibleCount, setVisibleCount] = useState<number>(9);

  // Sync when searchParams change
  useEffect(() => {
    if (initialBrand && !selectedBrands.includes(initialBrand)) {
      setSelectedBrands([initialBrand]);
    }
    if (initialCategory && !selectedCategories.includes(initialCategory)) {
      setSelectedCategories([initialCategory]);
    }
    setVisibleCount(9);
  }, [initialBrand, initialCategory, searchQuery]);

  // Lock body scroll when mobile filters open
  useEffect(() => {
    if (mobileFiltersOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = '';
    }
    return () => {
      document.body.style.overflow = '';
    };
  }, [mobileFiltersOpen]);

  // Toggle Handlers
  const toggleBrand = (brandId: string) => {
    setSelectedBrands(prev =>
      prev.includes(brandId) ? prev.filter(b => b !== brandId) : [...prev, brandId]
    );
    setVisibleCount(9);
  };

  const toggleCategory = (catTitle: string) => {
    setSelectedCategories(prev =>
      prev.includes(catTitle) ? prev.filter(c => c !== catTitle) : [...prev, catTitle]
    );
    setVisibleCount(9);
  };

  const toggleFamily = (family: string) => {
    setSelectedFamilies(prev =>
      prev.includes(family) ? prev.filter(f => f !== family) : [...prev, family]
    );
    setVisibleCount(9);
  };

  const toggleConcentration = (conc: string) => {
    setSelectedConcentrations(prev =>
      prev.includes(conc) ? prev.filter(c => c !== conc) : [...prev, conc]
    );
    setVisibleCount(9);
  };

  const clearAllFilters = () => {
    setSelectedBrands([]);
    setSelectedCategories([]);
    setSelectedFamilies([]);
    setSelectedConcentrations([]);
    setPriceRange(35000);
    setSearchParams({});
    setVisibleCount(9);
  };

  // Filter & Sort Logic
  const filteredProducts = useMemo(() => {
    return products.filter(product => {
      // Search
      if (searchQuery.trim()) {
        const q = searchQuery.toLowerCase();
        const matchName = product.name.toLowerCase().includes(q);
        const matchBrand = product.brandName.toLowerCase().includes(q);
        const matchNotes = product.notes.top.concat(product.notes.heart, product.notes.base).some(n => n.toLowerCase().includes(q));
        if (!matchName && !matchBrand && !matchNotes) return false;
      }

      // Brand
      if (selectedBrands.length > 0) {
        const brandMatch = selectedBrands.some(
          b => product.brandId === b || product.brandId === `b-${b}` || product.brandName.toLowerCase() === b.toLowerCase()
        );
        if (!brandMatch) return false;
      }

      // Category
      if (selectedCategories.length > 0) {
        const catMatch = selectedCategories.some(
          c => product.category.toLowerCase() === c.toLowerCase()
        );
        if (!catMatch) return false;
      }

      // Family
      if (selectedFamilies.length > 0) {
        const familyMatch = selectedFamilies.some(f =>
          product.fragranceFamilies.includes(f as FragranceFamily)
        );
        if (!familyMatch) return false;
      }

      // Concentration
      if (selectedConcentrations.length > 0) {
        const concMatch = selectedConcentrations.some(c =>
          product.concentration.toLowerCase().includes(c.toLowerCase())
        );
        if (!concMatch) return false;
      }

      // Price Range (check minimum variant price)
      const lowestPrice = Math.min(...product.variants.map(v => v.price));
      if (lowestPrice > priceRange) return false;

      return true;
    });
  }, [products, searchQuery, selectedBrands, selectedCategories, selectedFamilies, selectedConcentrations, priceRange]);

  // Sort
  const sortedProducts = useMemo(() => {
    const list = [...filteredProducts];
    switch (sortBy) {
      case 'price-asc':
        return list.sort((a, b) => a.variants[0].price - b.variants[0].price);
      case 'price-desc':
        return list.sort((a, b) => b.variants[0].price - a.variants[0].price);
      case 'rating':
        return list.sort((a, b) => b.rating - a.rating);
      case 'newest':
        return list.sort((a, b) => (b.isNewArrival ? 1 : 0) - (a.isNewArrival ? 1 : 0));
      case 'recommended':
      default:
        return list.sort((a, b) => (b.isBestSeller ? 1 : 0) - (a.isBestSeller ? 1 : 0));
    }
  }, [filteredProducts, sortBy]);

  const visibleProducts = sortedProducts.slice(0, visibleCount);
  const hasMore = visibleCount < sortedProducts.length;

  const handleLoadMore = () => {
    setVisibleCount(prev => prev + 6);
  };

  const activeFiltersCount = 
    selectedBrands.length + 
    selectedCategories.length + 
    selectedFamilies.length + 
    selectedConcentrations.length + 
    (priceRange < 35000 ? 1 : 0);

  return (
    <div className="min-h-screen bg-neutral-50 pb-20">
      {/* Header Banner */}
      <section className="bg-gradient-to-b from-brand-blush-100/50 via-white to-neutral-50 border-b border-neutral-200/80 pt-8 pb-10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
            <div className="space-y-1">
              <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-brand-plum-900 text-brand-blush-200 text-xs font-semibold tracking-wider uppercase">
                <Sparkles className="w-3.5 h-3.5 text-brand-gold-500" />
                <span>Haute Parfumerie Catalog</span>
              </div>
              <h1 className="font-serif text-3xl sm:text-4xl lg:text-5xl font-bold text-brand-plum-950">
                All Luxury Fragrances
              </h1>
              <p className="text-sm text-neutral-600 max-w-xl">
                Explore authentic luxury, designer, and artisanal creations with detailed olfactory pyramid notes.
              </p>
            </div>

            {searchQuery && (
              <div className="bg-brand-blush-100/80 border border-brand-blush-300/60 px-4 py-2 rounded-2xl flex items-center gap-3">
                <div className="text-xs">
                  <span className="text-neutral-500">Search: </span>
                  <span className="font-semibold text-brand-plum-900">"{searchQuery}"</span>
                </div>
                <button
                  onClick={() => setSearchParams({})}
                  className="p-1 hover:bg-white rounded-full text-neutral-500 hover:text-neutral-800 transition-colors"
                  aria-label="Clear Search"
                >
                  <X className="w-3.5 h-3.5" />
                </button>
              </div>
            )}
          </div>
        </div>
      </section>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-8">
        {/* Controls Bar */}
        <div className="flex flex-wrap items-center justify-between gap-4 pb-6 border-b border-neutral-200">
          <div className="flex items-center gap-2">
            <span className="text-sm font-semibold text-neutral-900">
              Showing {visibleProducts.length} of {sortedProducts.length} {sortedProducts.length === 1 ? 'Fragrance' : 'Fragrances'}
            </span>
            {activeFiltersCount > 0 && (
              <span className="text-xs px-2.5 py-0.5 rounded-full bg-brand-plum-900 text-white font-medium">
                {activeFiltersCount} filters active
              </span>
            )}
          </div>

          <div className="flex items-center gap-3">
            {/* Mobile Filter Sheet Button */}
            <button
              onClick={() => setMobileFiltersOpen(true)}
              className="lg:hidden flex items-center gap-2 px-4 py-2 rounded-xl bg-white border border-neutral-300 text-xs font-semibold text-neutral-800 shadow-xs"
            >
              <SlidersHorizontal className="w-4 h-4 text-brand-plum-900" />
              <span>Filters ({activeFiltersCount})</span>
            </button>

            {/* Sort Dropdown */}
            <div className="flex items-center gap-2">
              <span className="text-xs text-neutral-500 font-medium hidden sm:inline">Sort by:</span>
              <select
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value)}
                className="px-3.5 py-2 rounded-xl bg-white border border-neutral-300 text-xs font-semibold text-neutral-800 focus:outline-none focus:border-brand-plum-700 shadow-xs"
              >
                <option value="recommended">Curator's Pick</option>
                <option value="price-asc">Price: Low to High</option>
                <option value="price-desc">Price: High to Low</option>
                <option value="rating">Highest Rated</option>
                <option value="newest">New Arrivals</option>
              </select>
            </div>

            {/* Grid / List View Toggle */}
            <div className="hidden sm:flex items-center border border-neutral-300 rounded-xl overflow-hidden bg-white shadow-xs">
              <button
                onClick={() => setViewMode('grid')}
                className={`p-2 transition-colors ${viewMode === 'grid' ? 'bg-brand-plum-900 text-white' : 'text-neutral-500 hover:text-neutral-900'}`}
                aria-label="Grid View"
              >
                <Grid3X3 className="w-4 h-4" />
              </button>
              <button
                onClick={() => setViewMode('list')}
                className={`p-2 transition-colors ${viewMode === 'list' ? 'bg-brand-plum-900 text-white' : 'text-neutral-500 hover:text-neutral-900'}`}
                aria-label="List View"
              >
                <List className="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>

        {/* Active Filter Chips */}
        {activeFiltersCount > 0 && (
          <div className="flex flex-wrap items-center gap-2 py-3 border-b border-neutral-200/60">
            {selectedBrands.map(b => (
              <span key={b} className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-brand-blush-100 text-brand-plum-900 text-xs font-medium">
                {b}
                <button onClick={() => toggleBrand(b)}><X className="w-3.5 h-3.5" /></button>
              </span>
            ))}
            {selectedCategories.map(c => (
              <span key={c} className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-brand-blush-100 text-brand-plum-900 text-xs font-medium">
                {c}
                <button onClick={() => toggleCategory(c)}><X className="w-3.5 h-3.5" /></button>
              </span>
            ))}
            {selectedFamilies.map(f => (
              <span key={f} className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-brand-blush-100 text-brand-plum-900 text-xs font-medium">
                {f}
                <button onClick={() => toggleFamily(f)}><X className="w-3.5 h-3.5" /></button>
              </span>
            ))}
            {selectedConcentrations.map(c => (
              <span key={c} className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-brand-blush-100 text-brand-plum-900 text-xs font-medium">
                {c}
                <button onClick={() => toggleConcentration(c)}><X className="w-3.5 h-3.5" /></button>
              </span>
            ))}
            {priceRange < 35000 && (
              <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-brand-blush-100 text-brand-plum-900 text-xs font-medium">
                Up to {formatPrice(priceRange)}
                <button onClick={() => setPriceRange(35000)}><X className="w-3.5 h-3.5" /></button>
              </span>
            )}
            <button
              onClick={clearAllFilters}
              className="text-xs text-brand-rose-500 hover:text-brand-plum-900 font-semibold underline ml-2"
            >
              Clear All
            </button>
          </div>
        )}

        {/* Main Grid + Sidebar Layout */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 pt-6">
          {/* Desktop Filter Sidebar */}
          <aside className="hidden lg:block lg:col-span-3 space-y-6 bg-white p-6 rounded-2xl border border-neutral-200/80 shadow-card">
            <div className="flex items-center justify-between pb-4 border-b border-neutral-200">
              <h2 className="font-serif text-lg font-bold text-brand-plum-950 flex items-center gap-2">
                <SlidersHorizontal className="w-4 h-4 text-brand-plum-900" />
                Filter Vault
              </h2>
              {activeFiltersCount > 0 && (
                <button onClick={clearAllFilters} className="text-xs text-brand-rose-500 hover:text-brand-plum-900 font-semibold flex items-center gap-1">
                  <RotateCcw className="w-3 h-3" />
                  Reset
                </button>
              )}
            </div>

            {/* Price Filter */}
            <div className="space-y-3 pb-6 border-b border-neutral-200">
              <div className="flex items-center justify-between text-xs font-semibold text-neutral-800">
                <span>Max Budget</span>
                <span className="text-brand-plum-900">{formatPrice(priceRange)}</span>
              </div>
              <input
                type="range"
                min={3000}
                max={35000}
                step={500}
                value={priceRange}
                onChange={(e) => setPriceRange(Number(e.target.value))}
                className="w-full accent-brand-plum-900 cursor-pointer"
              />
              <div className="flex justify-between text-[10px] text-neutral-400 font-medium">
                <span>₹3,000</span>
                <span>₹35,000+</span>
              </div>
            </div>

            {/* Fragrance Families */}
            <div className="space-y-3 pb-6 border-b border-neutral-200">
              <h3 className="text-xs font-bold uppercase tracking-wider text-neutral-500">Fragrance Family</h3>
              <div className="space-y-2 max-h-48 overflow-y-auto pr-1">
                {FRAGRANCE_FAMILIES.map(fam => (
                  <label key={fam} className="flex items-center gap-2.5 text-xs text-neutral-700 hover:text-brand-plum-900 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={selectedFamilies.includes(fam)}
                      onChange={() => toggleFamily(fam)}
                      className="rounded border-neutral-300 text-brand-plum-900 focus:ring-brand-plum-700"
                    />
                    <span>{fam}</span>
                  </label>
                ))}
              </div>
            </div>

            {/* Categories */}
            <div className="space-y-3 pb-6 border-b border-neutral-200">
              <h3 className="text-xs font-bold uppercase tracking-wider text-neutral-500">Category & Target</h3>
              <div className="space-y-2">
                {CATEGORIES.map(cat => (
                  <label key={cat.id} className="flex items-center gap-2.5 text-xs text-neutral-700 hover:text-brand-plum-900 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={selectedCategories.includes(cat.title)}
                      onChange={() => toggleCategory(cat.title)}
                      className="rounded border-neutral-300 text-brand-plum-900 focus:ring-brand-plum-700"
                    />
                    <span>{cat.title}</span>
                  </label>
                ))}
              </div>
            </div>

            {/* Perfume Houses */}
            <div className="space-y-3">
              <h3 className="text-xs font-bold uppercase tracking-wider text-neutral-500">Perfume Houses</h3>
              <div className="space-y-2 max-h-52 overflow-y-auto pr-1">
                {BRANDS.map(brand => (
                  <label key={brand.id} className="flex items-center gap-2.5 text-xs text-neutral-700 hover:text-brand-plum-900 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={selectedBrands.includes(brand.name) || selectedBrands.includes(brand.id)}
                      onChange={() => toggleBrand(brand.name)}
                      className="rounded border-neutral-300 text-brand-plum-900 focus:ring-brand-plum-700"
                    />
                    <span className="flex-1">{brand.name}</span>
                    <span className="text-[10px] text-neutral-400 font-mono">({brand.tier})</span>
                  </label>
                ))}
              </div>
            </div>
          </aside>

          {/* Product Grid / Empty State */}
          <main className="lg:col-span-9 space-y-8">
            {visibleProducts.length > 0 ? (
              <>
                <div className="grid grid-cols-2 sm:grid-cols-2 lg:grid-cols-3 gap-4 sm:gap-6">
                  {visibleProducts.map(product => (
                    <ProductCard key={product.id} product={product} />
                  ))}
                </div>

                {/* Accessible Load More Pattern */}
                {hasMore && (
                  <div className="text-center pt-8">
                    <p className="text-xs text-neutral-500 mb-3">
                      Showing {visibleProducts.length} of {sortedProducts.length} items
                    </p>
                    <button
                      onClick={handleLoadMore}
                      className="px-8 py-3 rounded-full bg-white hover:bg-neutral-100 text-brand-plum-950 font-semibold text-xs border border-neutral-300 shadow-sm transition-all active:scale-98"
                    >
                      Load More Fragrances ({sortedProducts.length - visibleProducts.length} Remaining)
                    </button>
                  </div>
                )}
              </>
            ) : (
              <div className="bg-white rounded-3xl p-12 text-center border border-neutral-200/80 shadow-sm space-y-4">
                <div className="w-16 h-16 rounded-full bg-brand-blush-100 text-brand-plum-900 flex items-center justify-center mx-auto">
                  <Filter className="w-8 h-8" />
                </div>
                <h3 className="font-serif text-xl font-bold text-neutral-900">No Fragrances Match Filters</h3>
                <p className="text-xs text-neutral-500 max-w-sm mx-auto">
                  Try adjusting your budget slider or clearing family and brand filters to see more bottles.
                </p>
                <button
                  onClick={clearAllFilters}
                  className="px-6 py-2.5 rounded-full bg-brand-plum-900 text-white text-xs font-semibold shadow-sm"
                >
                  Reset All Filters
                </button>
              </div>
            )}
          </main>
        </div>
      </div>

      {/* Mobile Filter Sheet */}
      {mobileFiltersOpen && (
        <div className="fixed inset-0 z-50 lg:hidden flex flex-col justify-end">
          <div
            className="fixed inset-0 bg-neutral-950/60 backdrop-blur-xs transition-opacity"
            onClick={() => setMobileFiltersOpen(false)}
          />
          <div className="relative bg-white rounded-t-3xl max-h-[85vh] flex flex-col p-5 sm:p-6 shadow-modal border-t border-neutral-200 z-10 animate-in slide-in-from-bottom duration-300">
            {/* Native Mobile Drag Pill Handle */}
            <div className="w-12 h-1.5 bg-neutral-300/80 rounded-full mx-auto mb-3 shrink-0" />

            <div className="flex items-center justify-between pb-3 border-b border-neutral-200">
              <h2 className="font-serif text-lg font-bold text-brand-plum-950">Filters & Options</h2>
              <button
                onClick={() => setMobileFiltersOpen(false)}
                className="p-1.5 text-neutral-500 hover:text-neutral-800 rounded-full hover:bg-neutral-100"
                aria-label="Close filters"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="overflow-y-auto py-4 space-y-6 flex-1">
              {/* Price */}
              <div className="space-y-2">
                <div className="flex justify-between text-xs font-semibold">
                  <span>Max Budget</span>
                  <span className="text-brand-plum-900 font-bold">{formatPrice(priceRange)}</span>
                </div>
                <input
                  type="range"
                  min={3000}
                  max={35000}
                  step={500}
                  value={priceRange}
                  onChange={(e) => setPriceRange(Number(e.target.value))}
                  className="w-full accent-brand-plum-900"
                />
              </div>

              {/* Families */}
              <div className="space-y-2">
                <h3 className="text-xs font-bold uppercase text-neutral-500">Fragrance Family</h3>
                <div className="grid grid-cols-2 gap-2">
                  {FRAGRANCE_FAMILIES.map(fam => (
                    <label key={fam} className="flex items-center gap-2 text-xs text-neutral-700">
                      <input
                        type="checkbox"
                        checked={selectedFamilies.includes(fam)}
                        onChange={() => toggleFamily(fam)}
                        className="rounded border-neutral-300 text-brand-plum-900"
                      />
                      <span>{fam}</span>
                    </label>
                  ))}
                </div>
              </div>

              {/* Brands */}
              <div className="space-y-2">
                <h3 className="text-xs font-bold uppercase text-neutral-500">Perfume House</h3>
                <div className="grid grid-cols-2 gap-2">
                  {BRANDS.map(brand => (
                    <label key={brand.id} className="flex items-center gap-2 text-xs text-neutral-700">
                      <input
                        type="checkbox"
                        checked={selectedBrands.includes(brand.name)}
                        onChange={() => toggleBrand(brand.name)}
                        className="rounded border-neutral-300 text-brand-plum-900"
                      />
                      <span className="truncate">{brand.name}</span>
                    </label>
                  ))}
                </div>
              </div>
            </div>

            <div className="pt-4 border-t border-neutral-200 flex gap-3 pb-safe">
              <button
                onClick={clearAllFilters}
                className="w-1/3 py-3 rounded-full bg-neutral-100 text-neutral-800 text-xs font-semibold hover:bg-neutral-200 active:scale-98 transition-all"
              >
                Reset
              </button>
              <button
                onClick={() => setMobileFiltersOpen(false)}
                className="w-2/3 py-3 rounded-full bg-brand-plum-900 text-white text-xs font-semibold shadow-sm hover:bg-brand-plum-800 active:scale-98 transition-all"
              >
                Show {sortedProducts.length} Fragrances
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
