'use client';

import React from 'react';
import { Link } from '@/components/common/Link';
import { useStore } from '../context/StoreContext';
import { ProductCard } from '../components/product/ProductCard';
import { Heart, ShoppingBag, Trash2, ArrowRight } from 'lucide-react';

export const WishlistPage: React.FC = () => {
  const { wishlist, clearWishlist, addToCart, formatPrice } = useStore();

  const handleMoveAllToCart = () => {
    wishlist.forEach(item => {
      addToCart(item, item.variants[0], 1);
    });
  };

  return (
    <div className="min-h-screen bg-neutral-50 py-10 lg:py-16">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-8">
        {/* Header */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-neutral-200/80 pb-6">
          <div>
            <span className="text-xs font-semibold uppercase tracking-widest text-brand-rose-500">
              Personal Vault
            </span>
            <h1 className="font-serif text-3xl sm:text-4xl font-bold text-neutral-900">
              Your Saved Fragrances ({wishlist.length})
            </h1>
          </div>

          {wishlist.length > 0 && (
            <div className="flex items-center gap-3">
              <button
                onClick={handleMoveAllToCart}
                className="px-5 py-2.5 rounded-full bg-brand-plum-900 hover:bg-brand-plum-800 text-white text-xs font-semibold flex items-center gap-1.5 shadow-sm"
              >
                <ShoppingBag className="w-3.5 h-3.5" />
                <span>Move All to Bag</span>
              </button>
              <button
                onClick={clearWishlist}
                className="px-4 py-2.5 rounded-full border border-neutral-300 text-neutral-600 hover:text-semantic-error hover:border-semantic-error text-xs font-medium"
              >
                Clear Wishlist
              </button>
            </div>
          )}
        </div>

        {wishlist.length === 0 ? (
          <div className="bg-white rounded-3xl border border-neutral-200 p-12 sm:p-16 text-center space-y-4 max-w-lg mx-auto shadow-xs">
            <div className="w-16 h-16 rounded-full bg-brand-blush-100 text-brand-rose-500 mx-auto flex items-center justify-center">
              <Heart className="w-8 h-8" />
            </div>
            <div className="space-y-1">
              <h2 className="font-serif text-2xl font-bold text-neutral-900">Your wishlist is empty</h2>
              <p className="text-xs text-neutral-500">
                Save your dream perfumes to compare notes, track special offers, or create your bespoke collection.
              </p>
            </div>
            <Link
              to="/shop"
              className="inline-flex items-center gap-2 px-6 py-3 rounded-full bg-brand-plum-900 text-white text-xs font-semibold hover:bg-brand-plum-800 shadow-sm"
            >
              <span>Explore Fragrances</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </Link>
          </div>
        ) : (
          <div className="grid grid-cols-2 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 sm:gap-6">
            {wishlist.map(product => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
