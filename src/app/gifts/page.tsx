import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { GiftsPage } from '@/views/GiftsPage';

export const metadata: Metadata = {
  title: 'Luxury Fragrance Gifts & Coffrets | SCENTIVA Haute Parfumerie',
  description: 'Curated luxury fragrance gift sets, discovery coffrets, and bespoke presentation boxes for him, her, and unisex.',
};

export default function GiftsRoute() {
  return <GiftsPage />;
}
