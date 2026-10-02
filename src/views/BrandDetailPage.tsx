'use client';

import React, { useState, useMemo } from 'react';
import { useParams } from '../hooks/useNavigation';
import { Link } from '../components/common/Link';
import { useStore } from '../context/StoreContext';
import { BRANDS } from '../data/brands';
import { ProductCard } from '../components/product/ProductCard';
import { ArrowLeft, Sparkles, MapPin, Calendar, Award, SlidersHorizontal, ArrowUpDown } from 'lucide-react';

export const BrandDetailPage: React.FC = () => {
  const { slug } = useParams<{ slug: string }>();
  const { products } = useStore();
  const [sortBy, setSortBy] = useState<'featured' | 'price-asc' | 'price-desc' | 'rating'>('featured');

  const brand = BRANDS.find(
    b => b.slug === slug || 
         b.id === `b-${slug}` || 
         b.id === slug || 
         b.name.toLowerCase().replace(/\s+/g, '-') === slug?.toLowerCase() ||
         b.name.toLowerCase().includes(slug?.toLowerCase() || '')
  );

  if (!brand) {
    return (
      <div className="min-h-[60vh] flex flex-col items-center justify-center p-6 text-center">
        <h2 className="font-serif text-3xl font-bold text-neutral-900 mb-2">Perfume House Not Found</h2>
        <p className="text-xs sm:text-sm text-neutral-500 mb-6 max-w-md">
          The maison you are seeking is currently not registered in our active cellar directory.
        </p>
        <Link to="/brands" className="px-6 py-2.5 rounded-full bg-brand-plum-900 hover:bg-brand-plum-800 text-white text-xs font-semibold transition-all shadow-xs">
          Return to Brands Directory
        </Link>
      </div>
    );
  }

  const brandProducts = useMemo(() => {
    const raw = products.filter(
      p => p.brandId === brand.id || 
           p.brandName.toLowerCase() === brand.name.toLowerCase() ||
           p.brandId.replace('b-', '') === brand.slug ||
           p.brandName.toLowerCase().includes(brand.name.toLowerCase())
    );

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
  }, [products, brand, sortBy]);

  return (
    <div className="min-h-screen bg-neutral-50 pb-16">
      {/* Brand Hero Header */}
      <div className="relative bg-neutral-950 text-white py-16 lg:py-24 overflow-hidden">
        <img
          src={brand.bannerImage}
          alt={brand.name}
          className="absolute inset-0 w-full h-full object-cover opacity-30"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-brand-plum-950 via-brand-plum-950/70 to-transparent" />

        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10 space-y-4">
          <Link
            to="/brands"
            className="inline-flex items-center gap-1.5 text-xs font-medium text-brand-blush-200 hover:text-white transition-colors"
          >
            <ArrowLeft className="w-3.5 h-3.5" />
            <span>All Perfume Houses</span>
          </Link>

          <div className="max-w-3xl space-y-3">
            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-white/10 text-brand-gold-500 text-xs font-semibold uppercase tracking-widest border border-brand-gold-500/30">
              <Award className="w-3.5 h-3.5" />
              <span>{brand.tier} Parfumerie</span>
            </span>

            <h1 className="font-serif text-4xl sm:text-6xl font-bold tracking-tight text-white">
              {brand.name}
            </h1>

            <div className="flex items-center gap-4 text-xs text-brand-blush-200">
              <span className="flex items-center gap-1">
                <MapPin className="w-3.5 h-3.5 text-brand-rose-500" />
                {brand.origin}
              </span>
              <span>•</span>
              <span className="flex items-center gap-1">
                <Calendar className="w-3.5 h-3.5 text-brand-gold-500" />
                Founded {brand.foundedYear}
              </span>
            </div>

            <p className="text-xs sm:text-sm text-neutral-300 leading-relaxed pt-2 max-w-2xl">
              {brand.description}
            </p>
          </div>
        </div>
      </div>

      {/* Brand Catalog Section */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-12 space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-neutral-200/80 pb-4">
          <div>
            <h2 className="font-serif text-2xl font-bold text-neutral-900">
              Signature Flacons by {brand.name} ({brandProducts.length})
            </h2>
            <span className="text-xs text-neutral-500">100% Certified Authentic Bottles with Serial Verification</span>
          </div>

          {/* Sort Filter Dropdown */}
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

        {brandProducts.length > 0 ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
            {brandProducts.map(prod => (
              <ProductCard key={prod.id} product={prod} />
            ))}
          </div>
        ) : (
          <div className="p-12 text-center bg-white rounded-3xl border border-neutral-200 space-y-3">
            <Sparkles className="w-8 h-8 text-brand-gold-500 mx-auto" />
            <h3 className="font-serif text-lg font-bold text-neutral-900">Cellar Vault Restocking</h3>
            <p className="text-xs text-neutral-500 max-w-sm mx-auto">
              Additional releases from {brand.name} are currently arriving in our climate-controlled vault.
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
