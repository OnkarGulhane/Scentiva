import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { FragranceFinderPage } from '@/views/FragranceFinderPage';

export const metadata: Metadata = {
  title: 'Fragrance Finder & Scent Profiler | SCENTIVA Haute Parfumerie',
  description: 'Discover your bespoke signature scent with SCENTIVA’s interactive fragrance sommelier and note-matching profile engine.',
};

export default function ScentFinderRoute() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-neutral-50 animate-pulse flex items-center justify-center p-8"><div className="w-8 h-8 rounded-full border-2 border-brand-plum-900 border-t-transparent animate-spin" /></div>}>
      <FragranceFinderPage />
    </Suspense>
  );
}
