'use client';

import React, { useState, useMemo, useEffect } from 'react';
import { useSearchParams } from '../hooks/useNavigation';
import { Link } from '../components/common/Link';
import { useStore } from '../context/StoreContext';
import { SearchService, POPULAR_SEARCHES } from '../services/searchService';
import { ProductCard } from '../components/product/ProductCard';
import { BRANDS } from '../data/brands';
import { FragranceFamily } from '../types';
import { 
  Search as SearchIcon, 
  SlidersHorizontal, 
  X, 
  Sparkles, 
  ArrowRight,
  Filter,
  RotateCcw,
  Tag,
  Clock
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

export const SearchPage: React.FC = () => {
  const { formatPrice } = useStore();
  const [searchParams, setSearchParams] = useSearchParams();

  const queryParam = searchParams.get('q') || searchParams.get('search') || '';
  const [inputQuery, setInputQuery] = useState(queryParam);
  
  // Filter States
  const [selectedBrands, setSelectedBrands] = useState<string[]>([]);
  const [selectedFamilies, setSelectedFamilies] = useState<FragranceFamily[]>([]);
  const [priceRange, setPriceRange] = useState<number>(35000);
  const [sortBy, setSortBy] = useState<string>('recommended');
  const [pageSize, setPageSize] = useState<number>(8);
  const [mobileFiltersOpen, setMobileFiltersOpen] = useState(false);

  // Sync input query with search param
  useEffect(() => {
    setInputQuery(queryParam);
    if (queryParam.trim()) {
      SearchService.addRecentSearch(queryParam);
    }
  }, [queryParam]);

  const recentSearches = useMemo(() => SearchService.getRecentSearches(), [queryParam]);

  // Execute Search via SearchService
  const searchResult = useMemo(() => {
    return SearchService.search(
      {
        query: queryParam,
        brands: selectedBrands.length > 0 ? selectedBrands : undefined,
        families: selectedFamilies.length > 0 ? selectedFamilies : undefined,
        maxPrice: priceRange,
        sortBy: sortBy as any
      },
      1,
      pageSize
    );
  }, [queryParam, selectedBrands, selectedFamilies, priceRange, sortBy, pageSize]);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (inputQuery.trim()) {
      setSearchParams({ q: inputQuery.trim() });
      SearchService.addRecentSearch(inputQuery.trim());
    } else {
      setSearchParams({});
    }
    setPageSize(8);
  };

  const handleClearQuery = () => {
    setInputQuery('');
    setSearchParams({});
  };

  const toggleBrand = (brandName: string) => {
    setSelectedBrands(prev =>
      prev.includes(brandName) ? prev.filter(b => b !== brandName) : [...prev, brandName]
    );
    setPageSize(8);
  };

  const toggleFamily = (fam: FragranceFamily) => {
    setSelectedFamilies(prev =>
      prev.includes(fam) ? prev.filter(f => f !== fam) : [...prev, fam]
    );
    setPageSize(8);
  };

  const clearAllFilters = () => {
    setSelectedBrands([]);
    setSelectedFamilies([]);
    setPriceRange(35000);
    setSortBy('recommended');
    setPageSize(8);
  };

  const handleLoadMore = () => {
    setPageSize(prev => prev + 4);
  };

  const activeFilterCount = selectedBrands.length + selectedFamilies.length + (priceRange < 35000 ? 1 : 0);

  return (
    <div className="min-h-screen bg-neutral-50 pb-20">
      {/* Search Header Banner */}
      <section className="bg-gradient-to-b from-brand-blush-100/50 via-white to-neutral-50 border-b border-neutral-200/80 pt-8 pb-10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="max-w-3xl mx-auto text-center space-y-4">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-brand-plum-900 text-brand-blush-200 text-xs font-semibold tracking-wider uppercase">
              <Sparkles className="w-3.5 h-3.5 text-brand-gold-500" />
              <span>Vault Search & Fragrance Discovery</span>
            </div>

            <h1 className="font-serif text-3xl sm:text-4xl lg:text-5xl font-bold text-brand-plum-950">
              {queryParam.trim() ? (
                <>
                  Results for <span className="gold-gradient-text italic font-serif">"{queryParam}"</span>
                </>
              ) : (
                <>
                  Explore Our <span className="gold-gradient-text italic font-serif">Fragrance Vault</span>
                </>
              )}
            </h1>

            {/* Live Search Form */}
            <form onSubmit={handleSearchSubmit} className="relative max-w-2xl mx-auto pt-2">
              <div className="relative flex items-center">
                <SearchIcon className="absolute left-4 w-5 h-5 text-neutral-400" />
                <input
                  type="text"
                  value={inputQuery}
                  onChange={(e) => setInputQuery(e.target.value)}
                  placeholder="Search by perfume name, brand house, or olfactory note..."
                  className="w-full pl-12 pr-28 py-3.5 sm:py-4 rounded-full bg-white border border-neutral-300 focus:border-brand-plum-700 focus:ring-2 focus:ring-brand-blush-200 text-sm sm:text-base text-neutral-900 shadow-sm transition-all"
                />
                {inputQuery && (
                  <button
                    type="button"
                    onClick={handleClearQuery}
                    className="absolute right-24 p-1.5 text-neutral-400 hover:text-neutral-700 rounded-full"
                    aria-label="Clear Search"
                  >
                    <X className="w-4 h-4" />
                  </button>
                )}
                <button
                  type="submit"
                  className="absolute right-1.5 px-5 py-2.5 rounded-full bg-brand-plum-900 hover:bg-brand-plum-800 text-white text-xs font-semibold shadow-sm transition-colors"
                >
                  Search
                </button>
              </div>
            </form>

            {/* Popular & Recent Search Tags */}
            <div className="flex flex-wrap items-center justify-center gap-2 pt-3 text-xs">
              <span className="text-neutral-400 font-medium flex items-center gap-1">
                <Tag className="w-3.5 h-3.5" /> Popular:
              </span>
              {POPULAR_SEARCHES.slice(0, 6).map((tag) => (
                <button
                  key={tag}
                  onClick={() => {
                    setInputQuery(tag);
                    setSearchParams({ q: tag });
                  }}
                  className="px-3 py-1 rounded-full bg-white hover:bg-brand-blush-100 border border-neutral-200 text-neutral-700 hover:text-brand-plum-900 font-medium transition-colors shadow-xs"
                >
                  {tag}
                </button>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Main Content Area */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-8">
        {/* Active Filter Chips & Bar */}
        <div className="flex flex-wrap items-center justify-between gap-4 pb-6 border-b border-neutral-200">
          <div className="flex items-center gap-2">
            <span className="text-sm font-semibold text-neutral-900">
              {searchResult.totalCount} {searchResult.totalCount === 1 ? 'Fragrance' : 'Fragrances'} Found
            </span>
            {activeFilterCount > 0 && (
              <span className="text-xs px-2.5 py-0.5 rounded-full bg-brand-plum-900 text-white font-medium">
                {activeFilterCount} active
              </span>
            )}
          </div>

          <div className="flex items-center gap-3">
            {/* Mobile Filter Toggle */}
            <button
              onClick={() => setMobileFiltersOpen(true)}
              className="lg:hidden flex items-center gap-2 px-4 py-2 rounded-xl bg-white border border-neutral-300 text-xs font-semibold text-neutral-800 shadow-xs"
            >
              <SlidersHorizontal className="w-4 h-4 text-brand-plum-900" />
              <span>Filters ({activeFilterCount})</span>
            </button>

            {/* Sort Dropdown */}
            <div className="flex items-center gap-2">
              <span className="text-xs text-neutral-500 font-medium hidden sm:inline">Sort:</span>
              <select
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value)}
                className="px-3 py-2 rounded-xl bg-white border border-neutral-300 text-xs font-semibold text-neutral-800 focus:outline-none focus:border-brand-plum-700 shadow-xs"
              >
                <option value="recommended">Curator's Pick</option>
                <option value="price-asc">Price: Low to High</option>
                <option value="price-desc">Price: High to Low</option>
                <option value="rating">Top Rated</option>
                <option value="newest">New Arrivals</option>
              </select>
            </div>
          </div>
        </div>

        {/* Active Filter Badges */}
        {activeFilterCount > 0 && (
          <div className="flex flex-wrap items-center gap-2 py-3">
            {selectedBrands.map(b => (
              <span key={b} className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-brand-blush-100 text-brand-plum-900 text-xs font-medium">
                {b}
                <button onClick={() => toggleBrand(b)}><X className="w-3.5 h-3.5" /></button>
              </span>
            ))}
            {selectedFamilies.map(f => (
              <span key={f} className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-brand-blush-100 text-brand-plum-900 text-xs font-medium">
                {f}
                <button onClick={() => toggleFamily(f)}><X className="w-3.5 h-3.5" /></button>
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

        {/* Grid & Sidebar Layout */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 pt-6">
          {/* Desktop Filter Sidebar */}
          <aside className="hidden lg:block lg:col-span-3 space-y-6 bg-white p-6 rounded-2xl border border-neutral-200/80 shadow-card">
            <div className="flex items-center justify-between pb-4 border-b border-neutral-200">
              <h2 className="font-serif text-lg font-bold text-brand-plum-950 flex items-center gap-2">
                <SlidersHorizontal className="w-4 h-4 text-brand-plum-900" />
                Refine Search
              </h2>
              {activeFilterCount > 0 && (
                <button onClick={clearAllFilters} className="text-xs text-brand-rose-500 hover:text-brand-plum-900 font-semibold">
                  Reset
                </button>
              )}
            </div>

            {/* Price Range Slider */}
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
              <div className="flex justify-between text-[10px] text-neutral-400">
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

            {/* Brand Houses */}
            <div className="space-y-3">
              <h3 className="text-xs font-bold uppercase tracking-wider text-neutral-500">Perfume Houses</h3>
              <div className="space-y-2 max-h-56 overflow-y-auto pr-1">
                {BRANDS.map(brand => (
                  <label key={brand.id} className="flex items-center gap-2.5 text-xs text-neutral-700 hover:text-brand-plum-900 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={selectedBrands.includes(brand.name)}
                      onChange={() => toggleBrand(brand.name)}
                      className="rounded border-neutral-300 text-brand-plum-900 focus:ring-brand-plum-700"
                    />
                    <span className="flex-1">{brand.name}</span>
                  </label>
                ))}
              </div>
            </div>
          </aside>

          {/* Product Grid / Empty State */}
          <main className="lg:col-span-9 space-y-8">
            {searchResult.items.length > 0 ? (
              <>
                <div className="grid grid-cols-2 sm:grid-cols-2 lg:grid-cols-3 gap-4 sm:gap-6">
                  {searchResult.items.map(product => (
                    <ProductCard key={product.id} product={product} />
                  ))}
                </div>

                {/* Accessible Load More Pattern */}
                {searchResult.hasMore && (
                  <div className="text-center pt-8">
                    <p className="text-xs text-neutral-500 mb-3">
                      Showing {searchResult.items.length} of {searchResult.totalCount} items
                    </p>
                    <button
                      onClick={handleLoadMore}
                      className="px-8 py-3 rounded-full bg-white hover:bg-neutral-100 text-brand-plum-950 font-semibold text-xs border border-neutral-300 shadow-sm transition-all active:scale-98"
                    >
                      Load More Fragrances ({searchResult.totalCount - searchResult.items.length} Remaining)
                    </button>
                  </div>
                )}
              </>
            ) : (
              /* No Results State with Recovery Actions */
              <div className="bg-white rounded-3xl p-10 sm:p-14 text-center border border-neutral-200/80 shadow-sm space-y-6">
                <div className="w-16 h-16 rounded-full bg-brand-blush-100 text-brand-plum-900 flex items-center justify-center mx-auto">
                  <SearchIcon className="w-8 h-8" />
                </div>

                <div className="max-w-md mx-auto space-y-2">
                  <h3 className="font-serif text-2xl font-bold text-brand-plum-950">
                    No Fragrances Matched Your Query
                  </h3>
                  <p className="text-xs sm:text-sm text-neutral-500">
                    We couldn't find any perfumes matching "{queryParam}". Try checking for spelling errors, clearing your active filters, or exploring popular selections.
                  </p>
                </div>

                <div className="flex flex-wrap items-center justify-center gap-3 pt-2">
                  <button
                    onClick={clearAllFilters}
                    className="px-5 py-2.5 rounded-full bg-brand-plum-900 text-white text-xs font-semibold shadow-sm hover:bg-brand-plum-800 transition-colors"
                  >
                    Reset All Filters
                  </button>
                  <Link
                    to="/shop"
                    className="px-5 py-2.5 rounded-full bg-white text-brand-plum-900 border border-neutral-300 text-xs font-semibold hover:bg-neutral-50 transition-colors"
                  >
                    Browse Full Catalog
                  </Link>
                  <Link
                    to="/find-your-scent"
                    className="px-5 py-2.5 rounded-full bg-brand-blush-100 text-brand-plum-950 text-xs font-semibold hover:bg-brand-blush-200 transition-colors flex items-center gap-1.5"
                  >
                    <Sparkles className="w-3.5 h-3.5 text-brand-gold-500" />
                    Take Scent Quiz
                  </Link>
                </div>
              </div>
            )}
          </main>
        </div>
      </div>

      {/* Mobile Filter Sheet with Background Scroll Lock */}
      {mobileFiltersOpen && (
        <div className="fixed inset-0 z-50 lg:hidden flex flex-col justify-end">
          <div
            className="fixed inset-0 bg-neutral-950/60 backdrop-blur-xs transition-opacity"
            onClick={() => setMobileFiltersOpen(false)}
          />
          <div className="relative bg-white rounded-t-3xl max-h-[85vh] flex flex-col p-6 shadow-modal border-t border-neutral-200 z-10 animate-in slide-in-from-bottom duration-300">
            <div className="flex items-center justify-between pb-4 border-b border-neutral-200">
              <h2 className="font-serif text-lg font-bold text-brand-plum-950">Filters & Sorting</h2>
              <button
                onClick={() => setMobileFiltersOpen(false)}
                className="p-1.5 text-neutral-500 hover:text-neutral-800"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="overflow-y-auto py-4 space-y-6 flex-1">
              {/* Price */}
              <div className="space-y-2">
                <div className="flex justify-between text-xs font-semibold">
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

            <div className="pt-4 border-t border-neutral-200 flex gap-3">
              <button
                onClick={clearAllFilters}
                className="w-1/3 py-3 rounded-full bg-neutral-100 text-neutral-800 text-xs font-semibold"
              >
                Reset
              </button>
              <button
                onClick={() => setMobileFiltersOpen(false)}
                className="w-2/3 py-3 rounded-full bg-brand-plum-900 text-white text-xs font-semibold shadow-sm"
              >
                Apply ({searchResult.totalCount} Results)
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
