'use client';

import React from 'react';
import { COUPONS } from '../../data/coupons';
import { useStore } from '../../context/StoreContext';
import { Tag, Plus, CheckCircle2, ShieldCheck, Sparkles } from 'lucide-react';

export const AdminPromotionsPage: React.FC = () => {
  const { formatPrice } = useStore();

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="text-xs font-bold uppercase tracking-widest text-brand-rose-500">
            Campaign Engine
          </span>
          <h1 className="font-serif text-3xl font-bold text-neutral-900">
            Voucher & Coupon Management
          </h1>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {COUPONS.map(coupon => (
          <div key={coupon.code} className="bg-white p-6 rounded-3xl border border-neutral-200 shadow-xs space-y-4">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Tag className="w-5 h-5 text-brand-rose-500" />
                <span className="font-mono text-base font-bold text-brand-plum-950">{coupon.code}</span>
              </div>
              <span className="text-xs bg-green-100 text-semantic-success font-bold px-2.5 py-0.5 rounded-full uppercase">
                Active
              </span>
            </div>

            <div className="space-y-1 text-xs">
              <p className="font-semibold text-neutral-900">{coupon.description}</p>
              <p className="text-neutral-500">Min Order Requirement: <strong>{formatPrice(coupon.minOrderValue)}</strong></p>
              <p className="text-neutral-500">Expires: {coupon.expiresAt}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
