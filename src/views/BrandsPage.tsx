'use client';

import React, { useState } from 'react';
import { Link } from '../components/common/Link';
import { BRANDS } from '../data/brands';
import { Search, Sparkles, ArrowRight, ShieldCheck } from 'lucide-react';

export const BrandsPage: React.FC = () => {
  const [search, setSearch] = useState('');
  const [selectedTier, setSelectedTier] = useState<string>('All');

  const filteredBrands = BRANDS.filter(brand => {
    const matchesSearch = brand.name.toLowerCase().includes(search.toLowerCase()) ||
                          brand.origin.toLowerCase().includes(search.toLowerCase());
    const matchesTier = selectedTier === 'All' || brand.tier === selectedTier;
    return matchesSearch && matchesTier;
  });

  return (
    <div className="min-h-screen bg-neutral-50 py-10 lg:py-16">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-10">
        {/* Header */}
        <div className="text-center max-w-2xl mx-auto space-y-3">
          <span className="text-xs font-semibold uppercase tracking-widest text-brand-rose-500 flex items-center justify-center gap-1.5">
            <Sparkles className="w-3.5 h-3.5 text-brand-gold-500" />
            Haute Parfumerie Directory
          </span>
          <h1 className="font-serif text-3xl sm:text-5xl font-bold text-neutral-900">
            Iconic Fragrance Houses
          </h1>
          <p className="text-xs sm:text-sm text-neutral-600">
            Discover master perfumeries, legendary couture houses, and avant-garde artisan laboratories.
          </p>
        </div>

        {/* Search & Tier Filters */}
        <div className="flex flex-col sm:flex-row items-center justify-between gap-4 max-w-3xl mx-auto">
          <div className="relative w-full sm:w-80">
            <input
              type="text"
              placeholder="Search perfume houses..."
              value={search}
              onChange={e => setSearch(e.target.value)}
              className="w-full pl-10 pr-4 py-2.5 text-xs rounded-full border border-neutral-300 bg-white focus:outline-none focus:border-brand-plum-900 shadow-2xs"
            />
            <Search className="w-4 h-4 text-neutral-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
          </div>

          <div className="flex items-center gap-1.5 overflow-x-auto pb-1">
            {['All', 'Luxury', 'Niche', 'Designer'].map(tier => (
              <button
                key={tier}
                onClick={() => setSelectedTier(tier)}
                className={`px-4 py-1.5 rounded-full text-xs font-semibold transition-all ${
                  selectedTier === tier
                    ? 'bg-brand-plum-900 text-white shadow-xs'
                    : 'bg-white border border-neutral-200 text-neutral-600 hover:bg-neutral-100'
                }`}
              >
                {tier}
              </button>
            ))}
          </div>
        </div>

        {/* Brands Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredBrands.map(brand => (
            <Link
              key={brand.id}
              to={`/brands/${brand.slug}`}
              className="group bg-white rounded-2xl overflow-hidden border border-neutral-200/70 hover:border-brand-blush-300 hover:shadow-card-hover transition-all duration-300 flex flex-col justify-between"
            >
              {/* Banner */}
              <div className="aspect-[16/9] overflow-hidden bg-neutral-100 relative">
                <img
                  src={brand.bannerImage}
                  alt={brand.name}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-700 ease-out"
                />
                <div className="absolute top-3 right-3">
                  <span className="text-[10px] uppercase font-bold tracking-wider px-2.5 py-1 rounded-full bg-black/60 backdrop-blur-md text-white border border-white/20">
                    {brand.tier}
                  </span>
                </div>
              </div>

              {/* Body */}
              <div className="p-6 flex-1 flex flex-col justify-between space-y-4">
                <div className="space-y-1.5">
                  <div className="flex items-center justify-between">
                    <h2 className="font-serif text-2xl font-bold text-neutral-900 group-hover:text-brand-plum-900 transition-colors">
                      {brand.name}
                    </h2>
                    <span className="text-xs text-neutral-400 font-medium">Est. {brand.foundedYear}</span>
                  </div>
                  <span className="text-xs font-semibold text-brand-rose-500 block">
                    {brand.origin}
                  </span>
                  <p className="text-xs text-neutral-600 line-clamp-2 leading-relaxed">
                    {brand.description}
                  </p>
                </div>

                <div className="pt-3 border-t border-neutral-100 flex items-center justify-between text-xs font-semibold text-brand-plum-900">
                  <span className="text-neutral-500 font-normal">{brand.featuredProductCount} Signature Fragrances</span>
                  <span className="flex items-center gap-1 group-hover:translate-x-1 transition-transform">
                    Explore House <ArrowRight className="w-3.5 h-3.5" />
                  </span>
                </div>
              </div>
            </Link>
          ))}
        </div>
      </div>
    </div>
  );
};
