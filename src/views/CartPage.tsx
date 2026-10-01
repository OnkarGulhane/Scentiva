'use client';

import React, { useState } from 'react';
import { Link } from '@/components/common/Link';
import { useNavigate } from '@/hooks/useNavigation';
import { useStore } from '../context/StoreContext';
import { 
  Trash2, 
  Plus, 
  Minus, 
  ShoppingBag, 
  ArrowRight, 
  Tag, 
  ShieldCheck, 
  Truck, 
  RotateCcw,
  Sparkles
} from 'lucide-react';
import { analytics } from '../services/analyticsService';

export const CartPage: React.FC = () => {
  const {
    cart,
    removeFromCart,
    updateCartQuantity,
    cartSubtotal,
    cartDiscount,
    cartDeliveryFee,
    cartTotal,
    appliedCoupon,
    applyCoupon,
    removeCoupon,
    formatPrice
  } = useStore();

  const navigate = useNavigate();
  const [couponCode, setCouponCode] = useState('');

  const handleApplyCoupon = (e: React.FormEvent) => {
    e.preventDefault();
    if (couponCode.trim()) {
      applyCoupon(couponCode.trim());
      setCouponCode('');
    }
  };

  if (cart.length === 0) {
    return (
      <div className="min-h-[70vh] flex flex-col items-center justify-center p-6 text-center">
        <div className="w-20 h-20 rounded-full bg-brand-blush-100 flex items-center justify-center text-brand-plum-900 mb-4">
          <ShoppingBag className="w-10 h-10 opacity-70" />
        </div>
        <h1 className="font-serif text-3xl font-bold text-neutral-900 mb-2">Your Fragrance Bag is Empty</h1>
        <p className="text-xs sm:text-sm text-neutral-500 max-w-sm mb-6">
          Indulge in our curated collection of haute perfumery and find your signature scent.
        </p>
        <Link
          to="/shop"
          className="px-8 py-3.5 rounded-full bg-brand-plum-900 text-white text-xs font-semibold hover:bg-brand-plum-800 transition-all shadow-md flex items-center gap-2"
        >
          <span>Explore Catalog</span>
          <ArrowRight className="w-4 h-4" />
        </Link>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-neutral-50 py-10 lg:py-16">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-8">
        <div>
          <span className="text-xs font-semibold uppercase tracking-widest text-brand-rose-500">
            Shopping Bag
          </span>
          <h1 className="font-serif text-3xl sm:text-4xl font-bold text-neutral-900">
            Review Your Selections ({cart.length} Flacons)
          </h1>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-12 items-start">
          {/* Cart Items Table (8 Cols) */}
          <div className="lg:col-span-8 bg-white rounded-3xl p-6 sm:p-8 border border-neutral-200 shadow-xs space-y-6">
            <div className="divide-y divide-neutral-100">
              {cart.map(item => (
                <div
                  key={`${item.productId}-${item.selectedVariant.sku}`}
                  className="py-6 first:pt-0 last:pb-0 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4"
                >
                  <div className="flex items-center gap-4">
                    <img
                      src={item.product.images[0]}
                      alt={item.product.name}
                      className="w-20 h-20 sm:w-24 sm:h-24 rounded-2xl object-cover bg-neutral-100 border border-neutral-200/80 flex-shrink-0"
                    />
                    <div className="space-y-1">
                      <Link
                        to={`/brands/${item.product.brandId.replace('b-', '')}`}
                        className="text-[11px] font-semibold text-brand-rose-500 uppercase tracking-wider block"
                      >
                        {item.product.brandName}
                      </Link>
                      <Link
                        to={`/product/${item.product.slug}`}
                        className="font-serif text-base sm:text-lg font-bold text-neutral-900 hover:text-brand-plum-900 transition-colors block"
                      >
                        {item.product.name}
                      </Link>
                      <span className="text-xs text-neutral-500 block">
                        Volume: {item.selectedVariant.size} • {item.product.concentration}
                      </span>
                    </div>
                  </div>

                  <div className="flex items-center justify-between sm:justify-end gap-6 w-full sm:w-auto pt-2 sm:pt-0 border-t sm:border-t-0 border-neutral-100">
                    {/* Stepper */}
                    <div className="flex items-center border border-neutral-300 rounded-xl bg-neutral-50 px-2 py-1">
                      <button
                        onClick={() =>
                          updateCartQuantity(item.productId, item.selectedVariant.sku, item.quantity - 1)
                        }
                        className="p-1 text-neutral-600 hover:text-neutral-900"
                        aria-label="Decrease"
                      >
                        <Minus className="w-3.5 h-3.5" />
                      </button>
                      <span className="px-3 text-xs font-bold tabular-nums text-neutral-900">
                        {item.quantity}
                      </span>
                      <button
                        onClick={() =>
                          updateCartQuantity(item.productId, item.selectedVariant.sku, item.quantity + 1)
                        }
                        className="p-1 text-neutral-600 hover:text-neutral-900"
                        aria-label="Increase"
                      >
                        <Plus className="w-3.5 h-3.5" />
                      </button>
                    </div>

                    <div className="text-right">
                      <div className="text-base font-bold text-brand-plum-950 tabular-nums">
                        {formatPrice(item.selectedVariant.price * item.quantity)}
                      </div>
                      <div className="text-[10px] text-neutral-400">
                        {formatPrice(item.selectedVariant.price)} each
                      </div>
                    </div>

                    <button
                      onClick={() => removeFromCart(item.productId, item.selectedVariant.sku)}
                      className="text-neutral-400 hover:text-semantic-error p-1.5 transition-colors"
                      aria-label="Remove item"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              ))}
            </div>

            <div className="pt-4 border-t border-neutral-100 flex items-center justify-between">
              <Link
                to="/shop"
                className="text-xs font-semibold text-brand-plum-900 hover:text-brand-rose-500 flex items-center gap-1.5"
              >
                <span>← Continue Shopping Fragrances</span>
              </Link>
            </div>
          </div>

          {/* Order Summary Panel (4 Cols) */}
          <div className="lg:col-span-4 bg-white rounded-3xl p-6 sm:p-8 border border-neutral-200 shadow-xs space-y-6 sticky top-24">
            <h3 className="font-serif text-xl font-bold text-neutral-900 pb-2 border-b border-neutral-100">
              Order Summary
            </h3>

            {/* Coupon Application */}
            <div className="space-y-2">
              <label className="text-xs font-semibold text-neutral-700 block">Promotional Voucher</label>
              {appliedCoupon ? (
                <div className="flex items-center justify-between p-3 rounded-xl bg-brand-blush-100/60 border border-brand-blush-300 text-xs">
                  <div className="flex items-center gap-2">
                    <Tag className="w-4 h-4 text-brand-rose-500" />
                    <div>
                      <span className="font-bold text-brand-plum-950">{appliedCoupon.code}</span>
                      <p className="text-[10px] text-neutral-600">{appliedCoupon.description}</p>
                    </div>
                  </div>
                  <button
                    onClick={removeCoupon}
                    className="text-neutral-500 hover:text-semantic-error text-[11px] underline font-medium"
                  >
                    Remove
                  </button>
                </div>
              ) : (
                <form onSubmit={handleApplyCoupon} className="flex gap-2">
                  <input
                    type="text"
                    placeholder="e.g. WELCOME10"
                    value={couponCode}
                    onChange={e => setCouponCode(e.target.value)}
                    className="flex-1 px-3 py-2 text-xs rounded-xl border border-neutral-300 uppercase focus:outline-none focus:border-brand-plum-900"
                  />
                  <button
                    type="submit"
                    className="px-4 py-2 rounded-xl bg-neutral-800 hover:bg-neutral-950 text-white text-xs font-semibold"
                  >
                    Apply
                  </button>
                </form>
              )}
            </div>

            {/* Price Calculations */}
            <div className="space-y-2.5 text-xs text-neutral-600 pt-2 border-t border-neutral-100">
              <div className="flex justify-between">
                <span>Subtotal</span>
                <span className="font-medium text-neutral-900 tabular-nums">{formatPrice(cartSubtotal)}</span>
              </div>

              {cartDiscount > 0 && (
                <div className="flex justify-between text-semantic-success font-medium">
                  <span>Voucher Discount</span>
                  <span className="tabular-nums">-{formatPrice(cartDiscount)}</span>
                </div>
              )}

              <div className="flex justify-between">
                <span>Express Delivery</span>
                <span className="text-semantic-success font-medium">
                  {cartDeliveryFee === 0 ? 'FREE' : formatPrice(cartDeliveryFee)}
                </span>
              </div>

              <div className="flex justify-between pt-3 border-t border-neutral-200 text-sm font-bold text-neutral-900">
                <span>Total Amount</span>
                <span className="text-lg text-brand-plum-950 tabular-nums font-serif">
                  {formatPrice(cartTotal)}
                </span>
              </div>
            </div>

            {/* Checkout Button */}
            <button
              onClick={() => {
                analytics.trackCheckoutStarted(cartTotal, cart.length);
                navigate('/checkout');
              }}
              className="w-full py-4 rounded-2xl bg-brand-plum-900 hover:bg-brand-plum-800 text-white text-xs font-semibold uppercase tracking-wider flex items-center justify-center gap-2 shadow-card hover:shadow-card-hover transition-all active:scale-98"
            >
              <span>Proceed to Checkout</span>
              <ArrowRight className="w-4 h-4" />
            </button>

            {/* Trust Assurances */}
            <div className="pt-2 text-[11px] text-neutral-500 space-y-1.5 border-t border-neutral-100">
              <div className="flex items-center gap-2">
                <ShieldCheck className="w-3.5 h-3.5 text-brand-gold-500" />
                <span>100% Authentic Guaranteed Direct From Brands</span>
              </div>
              <div className="flex items-center gap-2">
                <Truck className="w-3.5 h-3.5 text-brand-gold-500" />
                <span>Temperature-Protected Priority Dispatch</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
