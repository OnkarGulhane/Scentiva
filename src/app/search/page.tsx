import React, { Suspense } from 'react';
import { SearchPage } from '@/views/SearchPage';

export const metadata = {
  title: 'Search Fragrance Vault — SCENTIVA',
  description: 'Search through hundreds of luxury perfumes, olfactory notes, brand houses, and fragrance families.',
};

export default function Search() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-neutral-50 animate-pulse flex items-center justify-center p-8"><div className="w-8 h-8 rounded-full border-2 border-brand-plum-900 border-t-transparent animate-spin" /></div>}>
      <SearchPage />
    </Suspense>
  );
}
