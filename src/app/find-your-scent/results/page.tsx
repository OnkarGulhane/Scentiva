import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { FragranceFinderPage } from '@/views/FragranceFinderPage';

export const metadata: Metadata = {
  title: 'Your Fragrance Recommendations | SCENTIVA Haute Parfumerie',
  description: 'Curated luxury fragrance recommendations tailored precisely to your olfactory preferences and occasion profile.',
};

export default function ScentFinderResultsRoute() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-neutral-50 animate-pulse flex items-center justify-center p-8"><div className="w-8 h-8 rounded-full border-2 border-brand-plum-900 border-t-transparent animate-spin" /></div>}>
      <FragranceFinderPage />
    </Suspense>
  );
}
