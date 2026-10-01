'use client';

import React, { useState } from 'react';
import { Link } from '../common/Link';
import { useStore } from '../../context/StoreContext';
import { ProductCard } from '../product/ProductCard';
import { ArrowRight, Sparkles, Flame } from 'lucide-react';

export const BestSellers: React.FC = () => {
  const { products } = useStore();
  const [activeTab, setActiveTab] = useState<'All' | 'For Her' | 'For Him' | 'Unisex' | 'Luxury & Niche'>('All');

  const filteredProducts = products.filter(p => {
    if (activeTab === 'All') return true;
    return p.category === activeTab;
  }).slice(0, 8);

  return (
    <section className="py-16 bg-white border-b border-neutral-200/60">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-8">
        {/* Header & Filter Tabs */}
        <div className="flex flex-col md:flex-row md:items-end justify-between gap-4">
          <div className="space-y-1">
            <span className="text-xs font-semibold uppercase tracking-widest text-brand-rose-500 flex items-center gap-1.5">
              <Flame className="w-3.5 h-3.5" />
              Most Desired
            </span>
            <h2 className="font-serif text-3xl sm:text-4xl font-bold text-neutral-900">
              Best Sellers & New Arrivals
            </h2>
            <p className="text-xs sm:text-sm text-neutral-500">
              The signature fragrances defining elegance and allure in 2026.
            </p>
          </div>

          {/* Filter Pills */}
          <div className="flex items-center gap-2 overflow-x-auto pb-1 max-w-full">
            {(['All', 'For Her', 'For Him', 'Unisex', 'Luxury & Niche'] as const).map(tab => (
              <button
                key={tab}
                onClick={() => setActiveTab(tab)}
                className={`px-4 py-2 rounded-full text-xs font-semibold whitespace-nowrap transition-all ${
                  activeTab === tab
                    ? 'bg-brand-plum-900 text-white shadow-sm'
                    : 'bg-neutral-100 text-neutral-600 hover:bg-neutral-200/80'
                }`}
              >
                {tab}
              </button>
            ))}
          </div>
        </div>

        {/* Products Grid */}
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 sm:gap-6">
          {filteredProducts.map(prod => (
            <ProductCard key={prod.id} product={prod} />
          ))}
        </div>

        {/* View All Button */}
        <div className="text-center pt-4">
          <Link
            to="/shop"
            className="inline-flex items-center gap-2 px-8 py-3.5 rounded-full border border-neutral-300 hover:border-brand-plum-900 text-neutral-800 hover:text-brand-plum-900 text-xs font-semibold transition-all hover:shadow-card"
          >
            <span>View Complete Fragrance Vault ({products.length})</span>
            <ArrowRight className="w-4 h-4" />
          </Link>
        </div>
      </div>
    </section>
  );
};
