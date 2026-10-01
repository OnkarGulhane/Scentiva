'use client';

import React, { useState } from 'react';
import { COUPONS } from '../data/coupons';
import { useStore } from '../context/StoreContext';
import { Sparkles, Copy, Check, Flame, Gift, ArrowRight } from 'lucide-react';
import { Link } from '@/components/common/Link';

export const OffersPage: React.FC = () => {
  const { formatPrice, showToast } = useStore();
  const [copiedCode, setCopiedCode] = useState<string | null>(null);

  const handleCopy = (code: string) => {
    navigator.clipboard.writeText(code);
    setCopiedCode(code);
    showToast(`Copied code ${code} to clipboard!`, 'success');
    setTimeout(() => setCopiedCode(null), 3000);
  };

  return (
    <div className="min-h-screen bg-neutral-50 py-10 lg:py-16">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 space-y-10">
        {/* Header */}
        <div className="text-center max-w-2xl mx-auto space-y-3">
          <span className="text-xs font-semibold uppercase tracking-widest text-brand-rose-500 flex items-center justify-center gap-1.5">
            <Flame className="w-3.5 h-3.5" />
            Exclusive Privileges
          </span>
          <h1 className="font-serif text-3xl sm:text-5xl font-bold text-neutral-900">
            Current Campaigns & Promotional Vouchers
          </h1>
          <p className="text-xs sm:text-sm text-neutral-600">
            Enjoy complimentary gifts, discovery coffrets, and seasonal courtesy discounts on our haute perfumery vault.
          </p>
        </div>

        {/* Coupon Cards Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {COUPONS.map(coupon => (
            <div
              key={coupon.code}
              className="bg-white rounded-3xl p-6 sm:p-8 border border-neutral-200/90 shadow-xs hover:shadow-card-hover transition-all flex flex-col justify-between space-y-6 relative overflow-hidden"
            >
              <div className="absolute top-0 right-0 w-32 h-32 bg-brand-blush-100/60 rounded-bl-full -z-0 pointer-events-none" />

              <div className="space-y-3 relative z-10">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold uppercase tracking-wider text-brand-rose-500 bg-brand-blush-100 px-3 py-1 rounded-full">
                    {coupon.discountType === 'percentage' ? `${coupon.discountValue}% SAVINGS` : `₹${coupon.discountValue} OFF`}
                  </span>
                  <span className="text-[11px] text-neutral-400">Valid until {coupon.expiresAt}</span>
                </div>

                <h3 className="font-serif text-2xl font-bold text-neutral-900">
                  {coupon.description}
                </h3>
                <p className="text-xs text-neutral-500">
                  Applicable on minimum cart value of <strong>{formatPrice(coupon.minOrderValue)}</strong>.
                </p>
              </div>

              <div className="pt-4 border-t border-neutral-100 flex items-center justify-between gap-4 relative z-10">
                <div className="p-2.5 px-4 rounded-xl bg-neutral-100 border border-neutral-200 font-mono text-sm font-bold text-brand-plum-950 tracking-wider">
                  {coupon.code}
                </div>

                <button
                  onClick={() => handleCopy(coupon.code)}
                  className="px-5 py-2.5 rounded-xl bg-brand-plum-900 hover:bg-brand-plum-800 text-white text-xs font-semibold flex items-center gap-1.5 shadow-sm transition-all active:scale-95"
                >
                  {copiedCode === coupon.code ? (
                    <>
                      <Check className="w-3.5 h-3.5 text-brand-blush-300" />
                      <span>Copied!</span>
                    </>
                  ) : (
                    <>
                      <Copy className="w-3.5 h-3.5" />
                      <span>Copy Code</span>
                    </>
                  )}
                </button>
              </div>
            </div>
          ))}
        </div>

        {/* Complimentary Gift Banner */}
        <div className="bg-gradient-to-r from-brand-plum-950 via-brand-plum-900 to-brand-plum-950 text-white rounded-3xl p-8 sm:p-12 border border-brand-blush-300/20 shadow-modal flex flex-col md:flex-row items-center justify-between gap-6">
          <div className="space-y-2 text-center md:text-left">
            <span className="text-xs font-bold uppercase tracking-widest text-brand-gold-500 flex items-center justify-center md:justify-start gap-1.5">
              <Gift className="w-4 h-4" />
              <span>Complimentary Gift with Purchase</span>
            </span>
            <h2 className="font-serif text-2xl sm:text-3xl font-bold">
              Receive a Luxury 3-Piece Miniature Set with Orders Above ₹12,000
            </h2>
            <p className="text-xs text-brand-blush-200/80 max-w-lg">
              Automatically added to your parcel at our packaging atelier. Includes velvet pouch and certificate.
            </p>
          </div>

          <Link
            to="/shop"
            className="px-7 py-3.5 rounded-full bg-brand-gold-500 hover:bg-brand-gold-500/90 text-brand-plum-950 font-bold text-xs uppercase tracking-wider shadow-md flex items-center gap-2"
          >
            <span>Shop Qualified Scents</span>
            <ArrowRight className="w-4 h-4" />
          </Link>
        </div>
      </div>
    </div>
  );
};
