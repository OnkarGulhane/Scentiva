import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { GiftsPage } from '@/views/GiftsPage';

export const metadata: Metadata = {
  title: 'Luxury Fragrance Gifts & Coffrets | SCENTIVA Haute Parfumerie',
  description: 'Curated luxury fragrance gift sets, discovery coffrets, and bespoke presentation boxes for him, her, and unisex.',
};

export default function GiftsRoute() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-neutral-50 animate-pulse flex items-center justify-center p-8"><div className="w-8 h-8 rounded-full border-2 border-brand-plum-900 border-t-transparent animate-spin" /></div>}>
      <GiftsPage />
    </Suspense>
  );
}
