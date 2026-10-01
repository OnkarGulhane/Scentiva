import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { CartPage } from '@/views/CartPage';

export const metadata: Metadata = {
  title: 'Shopping Bag | SCENTIVA Haute Parfumerie',
  description: 'Review your selected luxury fragrances, apply privilege promo codes, and proceed to secure checkout.',
};

export default function CartRoute() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-neutral-50 animate-pulse flex items-center justify-center p-8"><div className="w-8 h-8 rounded-full border-2 border-brand-plum-900 border-t-transparent animate-spin" /></div>}>
      <CartPage />
    </Suspense>
  );
}
