import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { BrandsPage } from '@/views/BrandsPage';

export const metadata: Metadata = {
  title: 'Luxury Perfume Houses & Brands | SCENTIVA',
  description: 'Explore the master fragrance houses curated by SCENTIVA, including Tom Ford, Creed, Maison Francis Kurkdjian, Parfums de Marly, and Byredo.',
};

export default function Brands() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-neutral-50 animate-pulse flex items-center justify-center p-8"><div className="w-8 h-8 rounded-full border-2 border-brand-plum-900 border-t-transparent animate-spin" /></div>}>
      <BrandsPage />
    </Suspense>
  );
}
