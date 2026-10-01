import React, { Suspense } from 'react';
import { ShopPage } from '@/views/ShopPage';

export const metadata = {
  title: 'Fragrance Vault & Catalog — SCENTIVA',
  description: 'Explore the complete SCENTIVA collection of luxury, niche, and designer fragrances. Filter by olfactory family, concentration, and brand.',
};

export default function Shop() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-neutral-50 animate-pulse flex items-center justify-center p-8"><div className="w-8 h-8 rounded-full border-2 border-brand-plum-900 border-t-transparent animate-spin" /></div>}>
      <ShopPage />
    </Suspense>
  );
}
