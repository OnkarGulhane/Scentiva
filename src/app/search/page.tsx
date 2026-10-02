import React, { Suspense } from 'react';
import { SearchPage } from '@/views/SearchPage';

export const metadata = {
  title: 'Search Fragrance Vault — SCENTIVA',
  description: 'Search through hundreds of luxury perfumes, olfactory notes, brand houses, and fragrance families.',
};

export default function Search() {
  return <SearchPage />;
}
