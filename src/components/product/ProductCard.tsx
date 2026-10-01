'use client';

import React, { useState } from 'react';
import { Link } from '../common/Link';
import { Product, ProductVariant } from '../../types';
import { useStore } from '../../context/StoreContext';
import { Heart, ShoppingBag, Star, Eye } from 'lucide-react';
import { Badge } from '../common/Badge';
import { SCENTIVA_FALLBACK_IMAGE } from '../../data/mediaCatalog';
import { analytics } from '../../services/analyticsService';

interface ProductCardProps {
  product: Product;
  featured?: boolean;
}

export const ProductCard: React.FC<ProductCardProps> = ({ product, featured = false }) => {
  const { addToCart, toggleWishlist, isInWishlist, setQuickViewProduct, formatPrice } = useStore();
  
  const [selectedVariant, setSelectedVariant] = useState<ProductVariant>(
    product.variants[0] || { size: '50ml', price: 7999, sku: 'DEF', inStock: true }
  );
  const [isHovered, setIsHovered] = useState(false);

  const isWishlisted = isInWishlist(product.id);
  const isOutOfStock = product.stock <= 0 || !selectedVariant.inStock;

  const handleWishlistToggle = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    if (!isWishlisted) {
      analytics.trackWishlistAdded(product.id, product.name);
    } else {
      analytics.trackWishlistRemoved(product.id);
    }
    toggleWishlist(product);
  };

  const handleAddToCart = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    if (isOutOfStock) return;
    analytics.trackCartAdded(product.id, selectedVariant.sku, selectedVariant.price, 1);
    addToCart(product, selectedVariant, 1);
  };

  return (
    <div
      className="group relative bg-white rounded-2xl border border-neutral-200/70 hover:border-brand-blush-300 hover:shadow-card-hover transition-all duration-300 flex flex-col overflow-hidden"
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      {/* Badges & Actions Overlay */}
      <div className="absolute top-3 left-3 right-3 z-10 flex items-center justify-between pointer-events-none">
        <div className="flex flex-col gap-1 pointer-events-auto">
          {product.isBestSeller && (
            <Badge variant="plum" size="sm">
              Best Seller
            </Badge>
          )}
          {product.isNewArrival && (
            <Badge variant="gold" size="sm">
              New Arrival
            </Badge>
          )}
          {product.discountPercentage && product.discountPercentage > 0 && (
            <Badge variant="blush" size="sm">
              {product.discountPercentage}% OFF
            </Badge>
          )}
          {isOutOfStock && (
            <Badge variant="outline" size="sm" className="bg-neutral-900/80 text-white border-none">
              Out of Stock
            </Badge>
          )}
        </div>

        <div className="flex items-center gap-1.5 pointer-events-auto">
          {/* Quick View Button */}
          <button
            onClick={e => {
              e.preventDefault();
              e.stopPropagation();
              analytics.trackProductViewed(product.id, product.name, selectedVariant.price);
              setQuickViewProduct(product);
            }}
            className="w-8 h-8 rounded-full bg-white/90 backdrop-blur-md shadow-sm border border-neutral-200/80 flex items-center justify-center text-neutral-700 hover:text-brand-plum-900 hover:scale-110 transition-all opacity-0 group-hover:opacity-100 hidden sm:flex"
            aria-label={`Quick View for ${product.name}`}
          >
            <Eye className="w-4 h-4" />
          </button>

          {/* Wishlist Button */}
          <button
            onClick={handleWishlistToggle}
            className={`w-8 h-8 rounded-full bg-white/90 backdrop-blur-md shadow-sm border border-neutral-200/80 flex items-center justify-center transition-all hover:scale-110 ${
              isWishlisted ? 'text-brand-rose-500 bg-brand-blush-100/80' : 'text-neutral-500 hover:text-brand-rose-500'
            }`}
            aria-label={isWishlisted ? `Remove ${product.name} from wishlist` : `Save ${product.name} to wishlist`}
          >
            <Heart className={`w-4 h-4 ${isWishlisted ? 'fill-current' : ''}`} />
          </button>
        </div>
      </div>

      {/* Product Image Stage */}
      <Link
        to={`/product/${product.slug}`}
        onClick={() => analytics.trackProductViewed(product.id, product.name, selectedVariant.price)}
        className="block relative aspect-[4/5] overflow-hidden bg-neutral-100/60"
      >
        <img
          src={product.images[0] || SCENTIVA_FALLBACK_IMAGE}
          alt={`${product.brandName} ${product.name} Eau de Parfum flacon`}
          onError={(e) => { e.currentTarget.src = SCENTIVA_FALLBACK_IMAGE; }}
          className="w-full h-full object-cover object-center group-hover:scale-105 transition-transform duration-700 ease-out"
          loading="lazy"
        />

        {/* Secondary image preview on hover if available */}
        {product.images[1] && isHovered && (
          <img
            src={product.images[1]}
            alt={`${product.brandName} ${product.name} alternate view`}
            onError={(e) => { e.currentTarget.src = SCENTIVA_FALLBACK_IMAGE; }}
            className="absolute inset-0 w-full h-full object-cover object-center transition-opacity duration-500 animate-in fade-in"
          />
        )}

        {/* Scent Family pill at bottom of image */}
        <div className="absolute bottom-2.5 left-3 pointer-events-none">
          <span className="text-[10px] font-medium bg-black/40 backdrop-blur-md text-white px-2 py-0.5 rounded-md">
            {product.fragranceFamilies[0]}
          </span>
        </div>
      </Link>

      {/* Content Body */}
      <div className="p-4 flex-1 flex flex-col justify-between space-y-3">
        <div className="space-y-1">
          {/* Brand Name */}
          <Link
            to={`/brands/${product.brandId.replace('b-', '')}`}
            className="text-[11px] font-semibold uppercase tracking-wider text-brand-rose-500 hover:text-brand-plum-900 transition-colors"
          >
            {product.brandName}
          </Link>

          {/* Product Title */}
          <h3 className="font-serif text-base font-medium text-neutral-900 group-hover:text-brand-plum-900 transition-colors line-clamp-1">
            <Link 
              to={`/product/${product.slug}`}
              onClick={() => analytics.trackProductViewed(product.id, product.name, selectedVariant.price)}
            >
              {product.name}
            </Link>
          </h3>

          <div className="flex items-center justify-between text-xs text-neutral-500 pt-0.5">
            <span className="text-[11px] text-neutral-500 truncate">{product.concentration}</span>
            <div className="flex items-center gap-1 text-[11px] font-medium text-neutral-700">
              <Star className="w-3.5 h-3.5 fill-brand-gold-500 text-brand-gold-500" />
              <span>{product.rating}</span>
              <span className="text-neutral-400">({product.reviewCount})</span>
            </div>
          </div>
        </div>

        {/* Size Pills Selector */}
        {product.variants.length > 1 && (
          <div className="flex items-center gap-1.5 pt-1">
            {product.variants.map(variant => (
              <button
                key={variant.sku}
                onClick={() => setSelectedVariant(variant)}
                className={`text-[10px] px-2 py-0.5 rounded-md font-medium border transition-all ${
                  selectedVariant.sku === variant.sku
                    ? 'bg-brand-plum-900 text-white border-brand-plum-900 shadow-xs'
                    : 'bg-neutral-50 text-neutral-600 border-neutral-200 hover:border-neutral-400'
                }`}
              >
                {variant.size}
              </button>
            ))}
          </div>
        )}

        {/* Price and Add to Cart Action */}
        <div className="pt-2 border-t border-neutral-100 flex items-center justify-between gap-2">
          <div>
            <div className="flex items-baseline gap-1.5">
              <span className="text-base font-semibold text-neutral-950 tabular-nums">
                {formatPrice(selectedVariant.price)}
              </span>
              {selectedVariant.mrp && selectedVariant.mrp > selectedVariant.price && (
                <span className="text-xs text-neutral-400 line-through tabular-nums">
                  {formatPrice(selectedVariant.mrp)}
                </span>
              )}
            </div>
          </div>

          <button
            onClick={handleAddToCart}
            disabled={isOutOfStock}
            className={`px-3.5 py-2 rounded-xl text-xs font-semibold flex items-center gap-1.5 shadow-sm transition-all ${
              isOutOfStock
                ? 'bg-neutral-200 text-neutral-400 cursor-not-allowed'
                : 'bg-brand-plum-900 hover:bg-brand-plum-800 active:scale-95 text-white'
            }`}
            aria-label={isOutOfStock ? `${product.name} is out of stock` : `Add ${product.name} to bag`}
          >
            <ShoppingBag className="w-3.5 h-3.5" />
            <span className="hidden sm:inline">{isOutOfStock ? 'Sold Out' : 'Add'}</span>
          </button>
        </div>
      </div>
    </div>
  );
};
