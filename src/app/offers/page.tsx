import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { OffersPage } from '@/views/OffersPage';

export const metadata: Metadata = {
  title: 'Exclusive Offers & Promo Codes | SCENTIVA Haute Parfumerie',
  description: 'Unlock seasonal luxury promotions, welcome coupon codes, and discovery gift sets at SCENTIVA.',
};

export default function OffersRoute() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-neutral-50 animate-pulse flex items-center justify-center p-8"><div className="w-8 h-8 rounded-full border-2 border-brand-plum-900 border-t-transparent animate-spin" /></div>}>
      <OffersPage />
    </Suspense>
  );
}
