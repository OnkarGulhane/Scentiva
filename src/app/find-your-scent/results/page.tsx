import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { FragranceFinderPage } from '@/views/FragranceFinderPage';

export const metadata: Metadata = {
  title: 'Your Fragrance Recommendations | SCENTIVA Haute Parfumerie',
  description: 'Curated luxury fragrance recommendations tailored precisely to your olfactory preferences and occasion profile.',
};

export default function ScentFinderResultsRoute() {
  return <FragranceFinderPage />;
}
