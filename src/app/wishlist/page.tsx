import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { WishlistPage } from '@/views/WishlistPage';

export const metadata: Metadata = {
  title: 'My Wishlist & Personal Vault | SCENTIVA Haute Parfumerie',
  description: 'Manage your curated list of luxury perfumes, niche extraits, and saved signature scents.',
};

export default function WishlistRoute() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-neutral-50 animate-pulse flex items-center justify-center p-8"><div className="w-8 h-8 rounded-full border-2 border-brand-plum-900 border-t-transparent animate-spin" /></div>}>
      <WishlistPage />
    </Suspense>
  );
}
