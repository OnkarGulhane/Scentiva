'use client';

import React, { useState } from 'react';
import { Link } from '@/components/common/Link';
import { useStore } from '../context/StoreContext';
import { ProductCard } from '../components/product/ProductCard';
import { 
  Gift, 
  Sparkles, 
  ShieldCheck, 
  PackageCheck, 
  Heart, 
  ArrowRight,
  Award,
  CheckCircle2
} from 'lucide-react';

export const GiftsPage: React.FC = () => {
  const { products } = useStore();
  const [recipient, setRecipient] = useState<'all' | 'her' | 'him' | 'unisex'>('all');

  // Gift sets & discovery products
  const giftProducts = products.filter(p => {
    const isGiftCategory = p.category === 'Gift Sets' || p.isFeatured || p.isBestSeller;
    if (recipient === 'all') return isGiftCategory;
    if (recipient === 'her') return p.category === 'For Her' || p.category === 'Gift Sets';
    if (recipient === 'him') return p.category === 'For Him' || p.category === 'Gift Sets';
    if (recipient === 'unisex') return p.category === 'Unisex' || p.category === 'Gift Sets';
    return true;
  });

  return (
    <div className="min-h-screen bg-neutral-50 pb-20">
      {/* Editorial Hero Banner */}
      <section className="relative overflow-hidden bg-gradient-to-r from-brand-plum-950 via-brand-plum-900 to-brand-plum-950 text-white py-14 lg:py-20 border-b border-brand-blush-300/20 shadow-modal">
        <div className="absolute inset-0 bg-[radial-gradient(circle_at_top_right,rgba(233,183,216,0.15),transparent_50%)] pointer-events-none" />
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
          <div className="max-w-3xl space-y-4 text-center sm:text-left">
            <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-brand-gold-500/20 border border-brand-gold-500/40 text-brand-gold-100 text-xs font-semibold tracking-wider uppercase">
              <Gift className="w-3.5 h-3.5 text-brand-gold-500" />
              <span>Haute Gifting & Discovery Coffrets</span>
            </div>
            <h1 className="font-serif text-3xl sm:text-5xl lg:text-6xl font-bold tracking-tight text-white leading-tight">
              The Art of Fragrance <span className="gold-gradient-text italic font-serif">Gifting</span>
            </h1>
            <p className="text-sm sm:text-base text-brand-blush-200 max-w-xl leading-relaxed">
              Celebrate life's memorable moments with bespoke discovery coffrets, travel atomizers, and prestige flacons sealed in our signature velvet-lined keepsake boxes.
            </p>

            {/* Recipient Filter Pills */}
            <div className="flex flex-wrap items-center gap-2 pt-4 justify-center sm:justify-start">
              <button
                onClick={() => setRecipient('all')}
                className={`px-4 py-2 rounded-full text-xs font-semibold transition-all ${
                  recipient === 'all'
                    ? 'bg-brand-gold-500 text-brand-plum-950 shadow-md'
                    : 'bg-white/10 text-white hover:bg-white/20'
                }`}
              >
                All Gift Selections
              </button>
              <button
                onClick={() => setRecipient('her')}
                className={`px-4 py-2 rounded-full text-xs font-semibold transition-all ${
                  recipient === 'her'
                    ? 'bg-brand-gold-500 text-brand-plum-950 shadow-md'
                    : 'bg-white/10 text-white hover:bg-white/20'
                }`}
              >
                For Her
              </button>
              <button
                onClick={() => setRecipient('him')}
                className={`px-4 py-2 rounded-full text-xs font-semibold transition-all ${
                  recipient === 'him'
                    ? 'bg-brand-gold-500 text-brand-plum-950 shadow-md'
                    : 'bg-white/10 text-white hover:bg-white/20'
                }`}
              >
                For Him
              </button>
              <button
                onClick={() => setRecipient('unisex')}
                className={`px-4 py-2 rounded-full text-xs font-semibold transition-all ${
                  recipient === 'unisex'
                    ? 'bg-brand-gold-500 text-brand-plum-950 shadow-md'
                    : 'bg-white/10 text-white hover:bg-white/20'
                }`}
              >
                Unisex Coffrets
              </button>
            </div>
          </div>
        </div>
      </section>

      {/* Gifting Perks Bar */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 -mt-6 relative z-20">
        <div className="bg-white rounded-2xl p-4 sm:p-6 border border-neutral-200 shadow-card grid grid-cols-1 sm:grid-cols-3 gap-4 text-center sm:text-left">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-full bg-brand-blush-100 text-brand-plum-900 flex items-center justify-center shrink-0">
              <PackageCheck className="w-5 h-5" />
            </div>
            <div>
              <h4 className="text-xs font-bold text-neutral-900">Complimentary Luxury Wrapping</h4>
              <p className="text-[11px] text-neutral-500">Every gift order includes embossed gold ribbon & custom card.</p>
            </div>
          </div>

          <div className="flex items-center gap-3 border-t sm:border-t-0 sm:border-l border-neutral-200 pt-3 sm:pt-0 sm:pl-4">
            <div className="w-10 h-10 rounded-full bg-brand-gold-100 text-brand-gold-500 flex items-center justify-center shrink-0">
              <Sparkles className="w-5 h-5 text-brand-plum-900" />
            </div>
            <div>
              <h4 className="text-xs font-bold text-neutral-900">Complimentary 2ml Sample</h4>
              <p className="text-[11px] text-neutral-500">Test the sample first before breaking the main bottle seal.</p>
            </div>
          </div>

          <div className="flex items-center gap-3 border-t sm:border-t-0 sm:border-l border-neutral-200 pt-3 sm:pt-0 sm:pl-4">
            <div className="w-10 h-10 rounded-full bg-brand-blush-100 text-brand-plum-900 flex items-center justify-center shrink-0">
              <ShieldCheck className="w-5 h-5" />
            </div>
            <div>
              <h4 className="text-xs font-bold text-neutral-900">Priority Express Dispatch</h4>
              <p className="text-[11px] text-neutral-500">Handled via insulated, temperature-controlled courier express.</p>
            </div>
          </div>
        </div>
      </div>

      {/* Gift Products Grid */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-12 space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="font-serif text-2xl sm:text-3xl font-bold text-brand-plum-950">
              Curated Gift Sets & Flacons
            </h2>
            <p className="text-xs text-neutral-500">Showing {giftProducts.length} exquisite gifting recommendations</p>
          </div>
          <Link
            to="/shop"
            className="text-xs font-semibold text-brand-plum-900 hover:text-brand-plum-800 flex items-center gap-1"
          >
            <span>Browse All Perfumes</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </Link>
        </div>

        <div className="grid grid-cols-2 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-6">
          {giftProducts.map(product => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      </div>
    </div>
  );
};
