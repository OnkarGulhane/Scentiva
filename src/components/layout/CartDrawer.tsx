'use client';

import React, { useState } from 'react';
import { useNavigate } from '../../hooks/useNavigation';
import { useStore } from '../../context/StoreContext';
import { Link } from '../common/Link';
import { 
  X, 
  Trash2, 
  Plus, 
  Minus, 
  ShoppingBag, 
  ArrowRight, 
  Tag, 
  Check, 
  Sparkles, 
  ShieldCheck,
  Truck
} from 'lucide-react';
import { analytics } from '../../services/analyticsService';

export const CartDrawer: React.FC = () => {
  const {
    cart,
    isCartDrawerOpen,
    setIsCartDrawerOpen,
    removeFromCart,
    updateCartQuantity,
    cartSubtotal,
    cartDiscount,
    cartTotal,
    appliedCoupon,
    applyCoupon,
    removeCoupon,
    formatPrice,
    isLoggedIn
  } = useStore();

  const navigate = useNavigate();
  const [couponInput, setCouponInput] = useState('');

  if (!isCartDrawerOpen) return null;

  const freeShippingThreshold = 999;
  const amountNeededForFreeShipping = Math.max(0, freeShippingThreshold - cartSubtotal);
  const shippingProgress = Math.min(100, (cartSubtotal / freeShippingThreshold) * 100);

  const handleApplyCoupon = (e: React.FormEvent) => {
    e.preventDefault();
    if (couponInput.trim()) {
      applyCoupon(couponInput.trim());
      setCouponInput('');
    }
  };

  const handleProceedToCheckout = () => {
    analytics.trackCheckoutStarted(cartTotal, cart.length);
    setIsCartDrawerOpen(false);
    if (isLoggedIn) {
      navigate('/checkout');
    } else {
      navigate('/account/sign-in?redirect=/checkout');
    }
  };

  return (
    <div className="fixed inset-0 z-50 overflow-hidden">
      {/* Backdrop */}
      <div
        className="absolute inset-0 bg-neutral-950/60 backdrop-blur-sm transition-opacity animate-in fade-in duration-300"
        onClick={() => setIsCartDrawerOpen(false)}
      />

      <div className="fixed inset-y-0 right-0 max-w-full flex pl-10">
        <div className="w-screen max-w-md bg-white shadow-modal flex flex-col animate-in slide-in-from-right duration-300">
          {/* Header */}
          <div className="p-4 sm:p-5 border-b border-neutral-100 flex items-center justify-between bg-neutral-50/50">
            <div className="flex items-center gap-2">
              <ShoppingBag className="w-5 h-5 text-brand-plum-900" />
              <h2 className="font-serif text-xl font-semibold text-neutral-900">
                Your Fragrance Bag
              </h2>
              <span className="text-xs bg-brand-blush-100 text-brand-plum-900 font-bold px-2 py-0.5 rounded-full">
                {cart.length}
              </span>
            </div>
            <button
              onClick={() => setIsCartDrawerOpen(false)}
              className="p-1.5 text-neutral-400 hover:text-neutral-900 rounded-lg hover:bg-neutral-100 transition-colors"
              aria-label="Close cart drawer"
            >
              <X className="w-5 h-5" />
            </button>
          </div>

          {/* Free Shipping Progress Indicator */}
          <div className="px-5 py-3 bg-brand-blush-100/50 border-b border-brand-blush-200/60">
            <div className="flex items-center justify-between text-xs mb-1.5 font-medium">
              <span className="flex items-center gap-1.5 text-brand-plum-950">
                <Truck className="w-3.5 h-3.5 text-brand-rose-500" />
                {amountNeededForFreeShipping === 0 ? (
                  <span className="text-semantic-success font-semibold">
                    You've unlocked Complimentary Express Shipping!
                  </span>
                ) : (
                  <span>
                    Add <strong className="text-brand-plum-900">{formatPrice(amountNeededForFreeShipping)}</strong> more for Free Shipping
                  </span>
                )}
              </span>
              <span className="text-neutral-500 text-[11px]">{Math.round(shippingProgress)}%</span>
            </div>
            <div className="w-full bg-neutral-200 h-1.5 rounded-full overflow-hidden">
              <div
                className="bg-brand-plum-900 h-full transition-all duration-500 rounded-full"
                style={{ width: `${shippingProgress}%` }}
              />
            </div>
          </div>

          {/* Cart Item List */}
          <div className="flex-1 overflow-y-auto p-4 sm:p-5 space-y-4">
            {cart.length === 0 ? (
              <div className="h-full flex flex-col items-center justify-center text-center p-6 space-y-4">
                <div className="w-16 h-16 rounded-full bg-brand-blush-100 flex items-center justify-center text-brand-plum-900">
                  <ShoppingBag className="w-8 h-8 opacity-60" />
                </div>
                <div className="space-y-1">
                  <h3 className="font-serif text-xl font-medium text-neutral-800">Your bag is empty</h3>
                  <p className="text-xs text-neutral-500 max-w-xs">
                    Explore our curated collection of haute perfumery and find your signature scent.
                  </p>
                </div>
                <button
                  onClick={() => {
                    setIsCartDrawerOpen(false);
                    navigate('/shop');
                  }}
                  className="px-6 py-2.5 rounded-full bg-brand-plum-900 text-white text-xs font-semibold hover:bg-brand-plum-800 transition-colors shadow-md"
                >
                  Explore Catalog
                </button>
              </div>
            ) : (
              <div className="space-y-3.5">
                {cart.map(item => (
                  <div
                    key={`${item.productId}-${item.selectedVariant.sku}`}
                    className="flex gap-3.5 p-3 rounded-xl border border-neutral-100 bg-white hover:border-brand-blush-200 transition-colors shadow-sm"
                  >
                    {/* Thumbnail */}
                    <div className="w-20 h-20 rounded-lg overflow-hidden bg-neutral-100 flex-shrink-0 border border-neutral-100">
                      <img
                        src={item.product.images[0]}
                        alt={item.product.name}
                        className="w-full h-full object-cover"
                      />
                    </div>

                    {/* Details */}
                    <div className="flex-1 min-w-0 flex flex-col justify-between">
                      <div className="flex items-start justify-between gap-2">
                        <div>
                          <p className="text-[11px] font-semibold text-brand-rose-500 uppercase tracking-wider">
                            {item.product.brandName}
                          </p>
                          <h4 className="text-sm font-semibold text-neutral-900 truncate">
                            {item.product.name}
                          </h4>
                          <span className="text-[11px] text-neutral-500">
                            Size: {item.selectedVariant.size} • {item.product.concentration}
                          </span>
                        </div>
                        <button
                          onClick={() => removeFromCart(item.productId, item.selectedVariant.sku)}
                          className="text-neutral-400 hover:text-semantic-error p-1 transition-colors"
                          aria-label="Remove item"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>

                      {/* Quantity & Price */}
                      <div className="flex items-center justify-between pt-2">
                        <div className="flex items-center border border-neutral-200 rounded-lg bg-neutral-50">
                          <button
                            onClick={() =>
                              updateCartQuantity(item.productId, item.selectedVariant.sku, item.quantity - 1)
                            }
                            className="p-1 text-neutral-600 hover:text-neutral-900 hover:bg-neutral-200 rounded-l-lg transition-colors"
                            aria-label="Decrease quantity"
                          >
                            <Minus className="w-3.5 h-3.5" />
                          </button>
                          <span className="px-3 text-xs font-semibold tabular-nums text-neutral-800">
                            {item.quantity}
                          </span>
                          <button
                            onClick={() =>
                              updateCartQuantity(item.productId, item.selectedVariant.sku, item.quantity + 1)
                            }
                            className="p-1 text-neutral-600 hover:text-neutral-900 hover:bg-neutral-200 rounded-r-lg transition-colors"
                            aria-label="Increase quantity"
                          >
                            <Plus className="w-3.5 h-3.5" />
                          </button>
                        </div>

                        <div className="text-right">
                          <span className="text-sm font-semibold text-brand-plum-950 tabular-nums">
                            {formatPrice(item.selectedVariant.price * item.quantity)}
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Footer & Checkout Summary */}
          {cart.length > 0 && (
            <div className="p-4 sm:p-5 border-t border-neutral-200 bg-neutral-50/70 space-y-4">
              {/* Promo code input */}
              <div>
                {appliedCoupon ? (
                  <div className="flex items-center justify-between p-2.5 rounded-lg bg-brand-blush-100/60 border border-brand-blush-300 text-xs">
                    <div className="flex items-center gap-2">
                      <Tag className="w-4 h-4 text-brand-rose-500" />
                      <div>
                        <span className="font-bold text-brand-plum-950">{appliedCoupon.code}</span>
                        <span className="text-neutral-600 ml-1">({appliedCoupon.description})</span>
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
                      placeholder="Coupon (e.g. WELCOME10)"
                      value={couponInput}
                      onChange={e => setCouponInput(e.target.value)}
                      className="flex-1 px-3 py-2 text-xs rounded-lg border border-neutral-200 bg-white focus:outline-none focus:border-brand-plum-900 uppercase"
                    />
                    <button
                      type="submit"
                      className="px-4 py-2 bg-neutral-800 hover:bg-neutral-950 text-white rounded-lg text-xs font-semibold transition-colors"
                    >
                      Apply
                    </button>
                  </form>
                )}
              </div>

              {/* Price Breakdown */}
              <div className="space-y-1.5 text-xs text-neutral-600">
                <div className="flex justify-between">
                  <span>Subtotal</span>
                  <span className="tabular-nums font-medium text-neutral-800">{formatPrice(cartSubtotal)}</span>
                </div>
                {cartDiscount > 0 && (
                  <div className="flex justify-between text-semantic-success">
                    <span>Discount</span>
                    <span className="tabular-nums font-semibold">-{formatPrice(cartDiscount)}</span>
                  </div>
                )}
                <div className="flex justify-between">
                  <span>Delivery</span>
                  <span className="tabular-nums text-semantic-success font-medium">
                    {cartSubtotal >= freeShippingThreshold ? 'FREE' : formatPrice(199)}
                  </span>
                </div>
                <div className="flex justify-between pt-2 border-t border-neutral-200 text-sm font-semibold text-neutral-900">
                  <span>Estimated Total</span>
                  <span className="text-base text-brand-plum-950 tabular-nums">
                    {formatPrice(cartTotal)}
                  </span>
                </div>
              </div>

              {/* CTAs */}
              <div className="space-y-2">
                <button
                  onClick={handleProceedToCheckout}
                  className="w-full py-3.5 px-4 rounded-xl bg-brand-plum-900 hover:bg-brand-plum-800 text-white text-sm font-semibold flex items-center justify-center gap-2 shadow-lg transition-all active:scale-[0.99]"
                >
                  <span>Proceed to Checkout</span>
                  <ArrowRight className="w-4 h-4" />
                </button>

                <div className="flex items-center justify-center gap-4 text-[11px] text-neutral-500 pt-1">
                  <span className="flex items-center gap-1">
                    <ShieldCheck className="w-3.5 h-3.5 text-brand-rose-500" />
                    100% Authentic
                  </span>
                  <span>•</span>
                  <span>7-Day Easy Return</span>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
