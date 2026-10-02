'use client';

import React, { useState, useMemo } from 'react';
import { useParams } from '../hooks/useNavigation';
import { Link } from '../components/common/Link';
import { CATEGORIES } from '../data/categories';
import { useStore } from '../context/StoreContext';
import { ProductCard } from '../components/product/ProductCard';
import { ArrowLeft, Sparkles, Layers, ArrowUpDown } from 'lucide-react';

export const CategoryDetailPage: React.FC = () => {
  const { slug } = useParams<{ slug: string }>();
  const { products } = useStore();
  const [sortBy, setSortBy] = useState<'featured' | 'price-asc' | 'price-desc' | 'rating'>('featured');

  const category = CATEGORIES.find(
    c => c.slug === slug || 
         c.id === slug || 
         c.id === `cat-${slug}` || 
         c.title.toLowerCase().replace(/\s+/g, '-') === slug?.toLowerCase() ||
         c.title.toLowerCase().includes(slug?.toLowerCase() || '')
  );

  if (!category) {
    return (
      <div className="min-h-[60vh] flex flex-col items-center justify-center p-6 text-center">
        <h2 className="font-serif text-3xl font-bold text-neutral-900 mb-2">Collection Not Found</h2>
        <p className="text-xs sm:text-sm text-neutral-500 mb-6 max-w-md">
          The curated fragrance collection you requested is currently not available.
        </p>
        <Link to="/collections" className="px-6 py-2.5 rounded-full bg-brand-plum-900 hover:bg-brand-plum-800 text-white text-xs font-semibold transition-all shadow-xs">
          Explore All Collections
        </Link>
      </div>
    );
  }

  const categoryProducts = useMemo(() => {
    const raw = products.filter(p => {
      if (category.slug === 'for-her') return p.category === 'For Her';
      if (category.slug === 'for-him') return p.category === 'For Him';
      if (category.slug === 'unisex') return p.category === 'Unisex' || p.category === 'Luxury & Niche';
      if (category.slug === 'luxury-niche') return p.category === 'Luxury & Niche' || p.variants.some(v => v.price > 18000);
      if (category.slug === 'gift-sets') return p.category === 'Gift Sets';
      if (category.slug === 'everyday') return p.occasion?.some(o => o.toLowerCase().includes('everyday') || o.toLowerCase().includes('work')) || p.fragranceFamilies.includes('Fresh') || p.fragranceFamilies.includes('Citrus');
      
      // Fragrance families fallback
      if (category.slug.includes('woody')) return p.fragranceFamilies.includes('Woody');
      if (category.slug.includes('oriental') || category.slug.includes('amber')) return p.fragranceFamilies.includes('Oriental');
      if (category.slug.includes('floral')) return p.fragranceFamilies.includes('Floral');
      if (category.slug.includes('fresh') || category.slug.includes('citrus')) return p.fragranceFamilies.includes('Fresh');
      if (category.slug.includes('gourmand')) return p.fragranceFamilies.includes('Sweet & Gourmand');

      return p.category.toLowerCase().includes(category.title.toLowerCase()) ||
             p.fragranceFamilies.some(f => f.toLowerCase().includes(category.title.toLowerCase()));
    });

    switch (sortBy) {
      case 'price-asc':
        return [...raw].sort((a, b) => (a.variants[0]?.price || 0) - (b.variants[0]?.price || 0));
      case 'price-desc':
        return [...raw].sort((a, b) => (b.variants[0]?.price || 0) - (a.variants[0]?.price || 0));
      case 'rating':
        return [...raw].sort((a, b) => b.rating - a.rating);
      case 'featured':
      default:
        return raw;
    }
  }, [products, category, sortBy]);

  return (
    <div className="min-h-screen bg-neutral-50 pb-16">
      {/* Category Hero */}
      <div className="relative bg-neutral-950 text-white py-16 lg:py-20 overflow-hidden">
        <img
          src={category.image}
          alt={category.title}
          className="absolute inset-0 w-full h-full object-cover opacity-30"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-brand-plum-950 via-brand-plum-950/75 to-transparent" />

        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10 space-y-3">
          <Link
            to="/collections"
            className="inline-flex items-center gap-1.5 text-xs font-medium text-brand-blush-200 hover:text-white transition-colors"
          >
            <ArrowLeft className="w-3.5 h-3.5" />
            <span>All Curated Collections</span>
          </Link>

          <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-white/10 text-brand-gold-400 text-xs font-semibold uppercase tracking-widest border border-brand-gold-500/30">
            <Layers className="w-3.5 h-3.5" />
            <span>{category.tagline}</span>
          </span>

          <h1 className="font-serif text-4xl sm:text-6xl font-bold text-white tracking-tight">
            {category.title}
          </h1>
          <p className="text-xs sm:text-sm text-neutral-300 max-w-2xl leading-relaxed pt-1">
            {category.description}
          </p>
        </div>
      </div>

      {/* Products Grid */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-10 space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-neutral-200/80 pb-4">
          <div>
            <h2 className="font-serif text-2xl font-bold text-neutral-900">
              {category.title} Curated Selections ({categoryProducts.length})
            </h2>
            <span className="text-xs text-neutral-500">Includes 2ml Complimentary Discovery Vial</span>
          </div>

          {/* Sort Dropdown */}
          <div className="flex items-center gap-2 shrink-0">
            <ArrowUpDown className="w-3.5 h-3.5 text-neutral-500" />
            <span className="text-xs text-neutral-500 font-medium">Sort:</span>
            <select
              value={sortBy}
              onChange={e => setSortBy(e.target.value as any)}
              className="text-xs font-semibold bg-white border border-neutral-300 rounded-lg px-3 py-1.5 text-neutral-800 focus:outline-none focus:border-brand-plum-900"
            >
              <option value="featured">Featured Curations</option>
              <option value="rating">Highest Rated</option>
              <option value="price-asc">Price: Low to High</option>
              <option value="price-desc">Price: High to Low</option>
            </select>
          </div>
        </div>

        {categoryProducts.length > 0 ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
            {categoryProducts.map(prod => (
              <ProductCard key={prod.id} product={prod} />
            ))}
          </div>
        ) : (
          <div className="p-12 text-center bg-white rounded-3xl border border-neutral-200 space-y-3">
            <Sparkles className="w-8 h-8 text-brand-gold-500 mx-auto" />
            <h3 className="font-serif text-lg font-bold text-neutral-900">Curating New Arrivals</h3>
            <p className="text-xs text-neutral-500 max-w-sm mx-auto">
              Master perfumer creations for this collection are currently being assembled by our sommeliers.
            </p>
            <Link
              to="/shop"
              className="inline-block mt-2 px-6 py-2 rounded-full bg-brand-plum-900 text-white text-xs font-semibold"
            >
              Explore All Perfumes
            </Link>
          </div>
        )}
      </div>
    </div>
  );
};
