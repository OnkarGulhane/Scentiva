import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { FragranceFinderPage } from '@/views/FragranceFinderPage';

export const metadata: Metadata = {
  title: 'Fragrance Finder & Scent Profiler | SCENTIVA Haute Parfumerie',
  description: 'Discover your bespoke signature scent with SCENTIVA’s interactive fragrance sommelier and note-matching profile engine.',
};

export default function ScentFinderRoute() {
  return <FragranceFinderPage />;
}
