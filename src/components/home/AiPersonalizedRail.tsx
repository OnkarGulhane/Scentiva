'use client';

import React, { useEffect, useState } from 'react';
import { Sparkles, ArrowRight, Compass, ShoppingBag } from 'lucide-react';
import { useStore } from '../../context/StoreContext';
import { AiApiService, AiRecommendationItem } from '../../services/aiApiService';
import { Link } from '../common/Link';
import { ProductCard } from '../product/ProductCard';

export const AiPersonalizedRail: React.FC = () => {
  const { currentUser, isLoggedIn, products, wishlist, cart, formatPrice } = useStore();
  const [headline, setHeadline] = useState('Curated For Your Olfactory Profile');
  const [explanation, setExplanation] = useState('Bespoke recommendations derived from our prestigious haute parfumerie collection.');
  const [recommendations, setRecommendations] = useState<AiRecommendationItem[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    let isMounted = true;
    const fetchRecommendations = async () => {
      setIsLoading(true);
      try {
        const viewed = typeof window !== 'undefined' 
          ? JSON.parse(localStorage.getItem('scentiva_recently_viewed') || '[]')
          : [];
        
        const res = await AiApiService.getPersonalizedRecommendations({
          userId: currentUser?.id ? String(currentUser.id) : undefined,
          viewed: viewed.slice(0, 5),
          cart: cart.map(i => String(i.product.id)),
          wishlist: wishlist.map(p => String(p.id)),
          limit: 4
        });


        if (isMounted && res.success && res.recommendations && res.recommendations.length > 0) {
          setHeadline(res.headline || 'Curated For Your Olfactory Profile');
          setExplanation(res.explanation || 'Bespoke recommendations matching your fragrance signature.');
          setRecommendations(res.recommendations);
        }
      } catch (err) {
        console.warn('Personalized recommendations fetch error:', err);
      } finally {
        if (isMounted) setIsLoading(false);
      }
    };

    fetchRecommendations();
    return () => {
      isMounted = false;
    };
  }, [currentUser, isLoggedIn, cart.length, wishlist.length]);


  // Map to store products
  const matchedProducts = recommendations
    .map(r => products.find(p => String(p.id) === String(r.productId) || p.slug === r.slug))
    .filter(Boolean);

  const displayProducts = matchedProducts.length >= 2 
    ? matchedProducts 
    : products.slice(0, 4);

  if (displayProducts.length === 0) return null;

  return (
    <section className="py-16 md:py-24 bg-neutral-950 text-neutral-100 border-t border-b border-neutral-900 relative overflow-hidden">
      {/* Subtle glowing ambient backdrop */}
      <div className="absolute top-0 right-1/4 w-96 h-96 bg-brand-gold-500/5 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute bottom-0 left-1/4 w-96 h-96 bg-brand-plum-900/10 rounded-full blur-3xl pointer-events-none" />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <div className="flex flex-col md:flex-row md:items-end justify-between mb-10 pb-4 border-b border-neutral-800">
          <div>
            <div className="flex items-center gap-2 mb-2">
              <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold uppercase tracking-wider bg-brand-gold-500/10 text-brand-gold-400 border border-brand-gold-500/20">
                <Sparkles className="w-3.5 h-3.5" />
                AI Olfactory Curation
              </span>
            </div>
            <h2 className="text-2xl md:text-3xl font-serif font-medium tracking-tight text-white">
              {headline}
            </h2>
            <p className="mt-1.5 text-xs md:text-sm text-neutral-400 max-w-xl">
              {explanation}
            </p>
          </div>

          <Link
            to="/find-your-scent"
            className="mt-4 md:mt-0 inline-flex items-center gap-2 text-xs font-semibold tracking-wider uppercase text-brand-gold-400 hover:text-brand-gold-300 transition-colors"
          >
            <span>Take Scent Finder Quiz</span>
            <ArrowRight className="w-4 h-4" />
          </Link>
        </div>

        {/* Product Cards Grid */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 md:gap-6">
          {displayProducts.map((product) => {
            if (!product) return null;
            return (
              <ProductCard
                key={product.id}
                product={product}
              />
            );
          })}
        </div>
      </div>
    </section>
  );
};
