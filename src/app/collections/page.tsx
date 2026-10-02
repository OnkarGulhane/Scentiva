import React from 'react';
import { Metadata } from 'next';
import { CategoriesPage } from '@/views/CategoriesPage';

export const metadata: Metadata = {
  title: 'Fragrance Collections & Olfactory Universes | SCENTIVA Haute Parfumerie',
  description: 'Explore curated fragrance collections at SCENTIVA: Feminine Florals, Masculine Woods, Niche Unisex, Prestige Millésimes, Everyday Signatures, and Discovery Coffrets.',
};

export default function CollectionsRoute() {
  return <CategoriesPage />;
}
