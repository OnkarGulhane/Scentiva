import React, { Suspense } from 'react';
import { ShopPage } from '@/views/ShopPage';

export const metadata = {
  title: 'Fragrance Vault & Catalog — SCENTIVA',
  description: 'Explore the complete SCENTIVA collection of luxury, niche, and designer fragrances. Filter by olfactory family, concentration, and brand.',
};

export default function Shop() {
  return <ShopPage />;
}
