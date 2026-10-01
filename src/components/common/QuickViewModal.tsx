'use client';

import React, { useState } from 'react';
import { useStore } from '../../context/StoreContext';
import { X, Heart, ShoppingBag, Star, Check, Sparkles, ArrowRight } from 'lucide-react';
import { Link } from './Link';
import { Badge } from './Badge';

export const QuickViewModal: React.FC = () => {
  const { quickViewProduct, setQuickViewProduct, addToCart, toggleWishlist, isInWishlist, formatPrice } = useStore();

  if (!quickViewProduct) return null;

  const [selectedVariant, setSelectedVariant] = useState(
    quickViewProduct.variants[0]
  );
  const [activeImageIndex, setActiveImageIndex] = useState(0);
  const isWishlisted = isInWishlist(quickViewProduct.id);

  const handleClose = () => {
    setQuickViewProduct(null);
  };

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto">
      {/* Backdrop */}
      <div
        className="fixed inset-0 bg-neutral-950/70 backdrop-blur-sm transition-opacity animate-in fade-in"
        onClick={handleClose}
      />

      <div className="min-h-full flex items-center justify-center p-4 sm:p-6 text-center">
        <div className="relative bg-white rounded-2xl sm:rounded-3xl shadow-modal max-w-3xl w-full overflow-hidden text-left border border-neutral-200 animate-in zoom-in-95 duration-200">
          {/* Close button */}
          <button
            onClick={handleClose}
            className="absolute top-4 right-4 z-10 p-2 rounded-full bg-white/80 backdrop-blur-md text-neutral-600 hover:text-neutral-950 hover:bg-white shadow-sm transition-all"
            aria-label="Close modal"
          >
            <X className="w-5 h-5" />
          </button>

          <div className="grid grid-cols-1 md:grid-cols-2">
            {/* Gallery Column */}
            <div className="p-6 bg-neutral-100/50 flex flex-col justify-between">
              <div className="relative aspect-square rounded-2xl overflow-hidden bg-white shadow-sm border border-neutral-200/60 mb-4">
                <img
                  src={quickViewProduct.images[activeImageIndex] || quickViewProduct.images[0]}
                  alt={quickViewProduct.name}
                  className="w-full h-full object-cover"
                />
                {quickViewProduct.discountPercentage && (
                  <div className="absolute top-3 left-3">
                    <Badge variant="plum">{quickViewProduct.discountPercentage}% OFF</Badge>
                  </div>
                )}
              </div>

              {/* Thumbnails */}
              {quickViewProduct.images.length > 1 && (
                <div className="flex gap-2 justify-center">
                  {quickViewProduct.images.map((img, i) => (
                    <button
                      key={i}
                      onClick={() => setActiveImageIndex(i)}
                      className={`w-14 h-14 rounded-lg overflow-hidden border-2 transition-all ${
                        activeImageIndex === i ? 'border-brand-plum-900 shadow-sm' : 'border-transparent opacity-70 hover:opacity-100'
                      }`}
                    >
                      <img src={img} alt="" className="w-full h-full object-cover" />
                    </button>
                  ))}
                </div>
              )}
            </div>

            {/* Content Column */}
            <div className="p-6 sm:p-8 flex flex-col justify-between space-y-5">
              <div className="space-y-3">
                <div>
                  <Link
                    to={`/brands/${quickViewProduct.brandId.replace('b-', '')}`}
                    onClick={handleClose}
                    className="text-xs font-semibold uppercase tracking-widest text-brand-rose-500 hover:text-brand-plum-900"
                  >
                    {quickViewProduct.brandName}
                  </Link>
                  <h2 className="font-serif text-2xl font-bold text-neutral-900 mt-1">
                    {quickViewProduct.name}
                  </h2>
                  <p className="text-xs text-neutral-500">{quickViewProduct.concentration} • {quickViewProduct.category}</p>
                </div>

                {/* Rating */}
                <div className="flex items-center gap-2">
                  <div className="flex items-center gap-1 text-xs font-semibold text-neutral-800">
                    <Star className="w-4 h-4 fill-brand-gold-500 text-brand-gold-500" />
                    <span>{quickViewProduct.rating}</span>
                  </div>
                  <span className="text-xs text-neutral-400">({quickViewProduct.reviewCount} reviews)</span>
                  <span className="text-xs text-semantic-success font-medium ml-2">● In Stock</span>
                </div>

                {/* Price */}
                <div className="flex items-baseline gap-2 py-1">
                  <span className="text-2xl font-bold text-brand-plum-950 tabular-nums">
                    {formatPrice(selectedVariant.price)}
                  </span>
                  {selectedVariant.mrp && selectedVariant.mrp > selectedVariant.price && (
                    <span className="text-sm text-neutral-400 line-through tabular-nums">
                      {formatPrice(selectedVariant.mrp)}
                    </span>
                  )}
                </div>

                {/* Description */}
                <p className="text-xs text-neutral-600 line-clamp-3 leading-relaxed">
                  {quickViewProduct.description}
                </p>

                {/* Fragrance Pyramid Notes */}
                <div className="p-3 bg-brand-blush-100/40 rounded-xl border border-brand-blush-200/50 space-y-1 text-xs">
                  <div className="font-semibold text-brand-plum-950 text-[11px] uppercase tracking-wider flex items-center gap-1">
                    <Sparkles className="w-3 h-3 text-brand-gold-500" />
                    Key Olfactory Notes:
                  </div>
                  <div className="text-neutral-700 text-[11px]">
                    <strong>Top:</strong> {quickViewProduct.notes.top.join(', ')}
                  </div>
                  <div className="text-neutral-700 text-[11px]">
                    <strong>Heart:</strong> {quickViewProduct.notes.heart.join(', ')}
                  </div>
                </div>

                {/* Variant size selector */}
                <div className="space-y-1.5 pt-1">
                  <label className="text-xs font-semibold text-neutral-700">Select Volume:</label>
                  <div className="flex gap-2">
                    {quickViewProduct.variants.map(v => (
                      <button
                        key={v.sku}
                        onClick={() => setSelectedVariant(v)}
                        className={`px-3 py-1.5 rounded-lg text-xs font-medium border transition-all ${
                          selectedVariant.sku === v.sku
                            ? 'bg-brand-plum-900 text-white border-brand-plum-900 shadow-sm'
                            : 'bg-neutral-50 text-neutral-700 border-neutral-200 hover:border-neutral-400'
                        }`}
                      >
                        {v.size}
                      </button>
                    ))}
                  </div>
                </div>
              </div>

              {/* Actions */}
              <div className="space-y-3 pt-2">
                <div className="flex gap-3">
                  <button
                    onClick={() => {
                      addToCart(quickViewProduct, selectedVariant, 1);
                      handleClose();
                    }}
                    className="flex-1 py-3 px-4 rounded-xl bg-brand-plum-900 hover:bg-brand-plum-800 text-white text-xs font-semibold flex items-center justify-center gap-2 shadow-md transition-all active:scale-98"
                  >
                    <ShoppingBag className="w-4 h-4" />
                    <span>Add to Bag • {formatPrice(selectedVariant.price)}</span>
                  </button>

                  <button
                    onClick={() => toggleWishlist(quickViewProduct)}
                    className={`p-3 rounded-xl border transition-all ${
                      isWishlisted
                        ? 'bg-brand-blush-100 border-brand-rose-500 text-brand-rose-500'
                        : 'border-neutral-200 text-neutral-600 hover:text-brand-rose-500 hover:border-neutral-400'
                    }`}
                    aria-label="Wishlist"
                  >
                    <Heart className={`w-5 h-5 ${isWishlisted ? 'fill-current' : ''}`} />
                  </button>
                </div>

                <Link
                  to={`/product/${quickViewProduct.slug}`}
                  onClick={handleClose}
                  className="inline-flex items-center gap-1.5 text-xs font-semibold text-brand-plum-900 hover:text-brand-rose-500 transition-colors"
                >
                  <span>View Full Fragrance Details & Longevity Pyramid</span>
                  <ArrowRight className="w-3.5 h-3.5" />
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
