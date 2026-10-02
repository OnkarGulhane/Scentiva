import React from 'react';
import { Metadata } from 'next';
import { CategoriesPage } from '@/views/CategoriesPage';

export const metadata: Metadata = {
  title: 'Fragrance Collections & Olfactory Families | SCENTIVA',
  description: 'Explore curated fragrance collections at SCENTIVA, including feminine florals, masculine woods, unisex amber, luxury niche, and luxury discovery sets.',
};

export default function Categories() {
  return <CategoriesPage />;
}
