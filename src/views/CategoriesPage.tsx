'use client';

import React, { useState } from 'react';
import { Link } from '../components/common/Link';
import { CATEGORIES } from '../data/categories';
import { Sparkles, ArrowRight, Layers, Search } from 'lucide-react';

export const CategoriesPage: React.FC = () => {
  const [search, setSearch] = useState('');

  const filteredCategories = CATEGORIES.filter(cat =>
    cat.title.toLowerCase().includes(search.toLowerCase()) ||
    cat.tagline.toLowerCase().includes(search.toLowerCase()) ||
    cat.description.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="min-h-screen bg-neutral-50 py-10 lg:py-16">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-10">
        {/* Header */}
        <div className="text-center max-w-2xl mx-auto space-y-3">
          <span className="text-xs font-semibold uppercase tracking-widest text-brand-rose-500 flex items-center justify-center gap-1.5">
            <Sparkles className="w-3.5 h-3.5 text-brand-gold-500" />
            Curated Olfactory Universes
          </span>
          <h1 className="font-serif text-3xl sm:text-5xl font-bold text-neutral-900 tracking-tight">
            Fragrance Collections & Moods
          </h1>
          <p className="text-xs sm:text-sm text-neutral-600 leading-relaxed">
            Discover bespoke olfactory compositions categorized by mood, gender expression, intensity, and gifting moments.
          </p>
        </div>

        {/* Search Bar */}
        <div className="max-w-md mx-auto relative">
          <input
            type="text"
            placeholder="Search collections (e.g., For Her, Niche, Everyday)..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            className="w-full pl-10 pr-4 py-2.5 text-xs rounded-full border border-neutral-300 bg-white focus:outline-none focus:border-brand-plum-900 shadow-2xs"
          />
          <Search className="w-4 h-4 text-neutral-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
        </div>

        {/* Categories Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredCategories.map(category => (
            <Link
              key={category.id}
              to={`/collections/${category.slug}`}
              className="group bg-white rounded-2xl overflow-hidden border border-neutral-200/70 hover:border-brand-blush-300 hover:shadow-card-hover transition-all duration-300 flex flex-col justify-between"
            >
              {/* Image Preview */}
              <div className="aspect-[16/10] overflow-hidden bg-neutral-100 relative">
                <img
                  src={category.image}
                  alt={category.title}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-700 ease-out"
                />
                <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-black/20 to-transparent" />
                <div className="absolute bottom-3 left-4 right-4">
                  <span className="text-[11px] uppercase font-bold tracking-widest text-brand-blush-200">
                    {category.tagline}
                  </span>
                  <h2 className="font-serif text-2xl font-bold text-white">
                    {category.title}
                  </h2>
                </div>
              </div>

              {/* Body */}
              <div className="p-6 flex-1 flex flex-col justify-between space-y-4">
                <p className="text-xs text-neutral-600 leading-relaxed">
                  {category.description}
                </p>

                <div className="pt-3 border-t border-neutral-100 flex items-center justify-between text-xs font-semibold text-brand-plum-900">
                  <span className="text-neutral-500 font-normal flex items-center gap-1">
                    <Layers className="w-3.5 h-3.5 text-brand-rose-500" />
                    {category.productCount} Handcrafted Fragrances
                  </span>
                  <span className="flex items-center gap-1 group-hover:translate-x-1 transition-transform">
                    Explore Collection <ArrowRight className="w-3.5 h-3.5" />
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
