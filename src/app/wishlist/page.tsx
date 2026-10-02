import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { WishlistPage } from '@/views/WishlistPage';

export const metadata: Metadata = {
  title: 'My Wishlist & Personal Vault | SCENTIVA Haute Parfumerie',
  description: 'Manage your curated list of luxury perfumes, niche extraits, and saved signature scents.',
};

export default function WishlistRoute() {
  return <WishlistPage />;
}
