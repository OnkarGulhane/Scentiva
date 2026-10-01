'use client';

import React from 'react';
import { useParams } from '../hooks/useNavigation';
import { Link } from '../components/common/Link';
import { CATEGORIES } from '../data/categories';
import { useStore } from '../context/StoreContext';
import { ProductCard } from '../components/product/ProductCard';
import { ArrowLeft, Sparkles } from 'lucide-react';

export const CategoryDetailPage: React.FC = () => {
  const { slug } = useParams<{ slug: string }>();
  const { products } = useStore();

  const category = CATEGORIES.find(c => c.slug === slug);

  if (!category) {
    return (
      <div className="min-h-[60vh] flex flex-col items-center justify-center p-6 text-center">
        <h2 className="font-serif text-3xl font-bold text-neutral-900 mb-2">Collection Not Found</h2>
        <p className="text-xs text-neutral-500 mb-6">Explore our all perfume collections.</p>
        <Link to="/shop" className="px-6 py-2.5 rounded-full bg-brand-plum-900 text-white text-xs font-semibold">
          Explore All Perfumes
        </Link>
      </div>
    );
  }

  const categoryProducts = products.filter(
    p => p.category.toLowerCase().includes(category.title.toLowerCase()) ||
         (category.slug === 'for-her' && p.category === 'For Her') ||
         (category.slug === 'for-him' && p.category === 'For Him') ||
         (category.slug === 'unisex' && p.category === 'Unisex') ||
         (category.slug === 'luxury-niche' && p.category === 'Luxury & Niche') ||
         (category.slug === 'gift-sets' && p.category === 'Gift Sets')
  );

  return (
    <div className="min-h-screen bg-neutral-50 pb-16">
      {/* Category Hero */}
      <div className="relative bg-neutral-950 text-white py-16 overflow-hidden">
        <img
          src={category.image}
          alt={category.title}
          className="absolute inset-0 w-full h-full object-cover opacity-25"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-brand-plum-950 via-brand-plum-950/70 to-transparent" />

        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10 space-y-3">
          <Link
            to="/shop"
            className="inline-flex items-center gap-1.5 text-xs font-medium text-brand-blush-200 hover:text-white transition-colors"
          >
            <ArrowLeft className="w-3.5 h-3.5" />
            <span>All Fragrances</span>
          </Link>

          <span className="text-xs uppercase font-semibold tracking-widest text-brand-gold-500 block">
            {category.tagline}
          </span>
          <h1 className="font-serif text-4xl sm:text-5xl font-bold text-white">
            {category.title}
          </h1>
          <p className="text-xs sm:text-sm text-neutral-300 max-w-2xl leading-relaxed">
            {category.description}
          </p>
        </div>
      </div>

      {/* Products Grid */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-10 space-y-6">
        <div className="flex items-center justify-between border-b border-neutral-200/80 pb-4">
          <h2 className="font-serif text-2xl font-bold text-neutral-900">
            {category.title} Curated Selections ({categoryProducts.length})
          </h2>
          <span className="text-xs text-neutral-500">Authenticity Certificate Included</span>
        </div>

        <div className="grid grid-cols-2 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 sm:gap-6">
          {categoryProducts.map(prod => (
            <ProductCard key={prod.id} product={prod} />
          ))}
        </div>
      </div>
    </div>
  );
};
