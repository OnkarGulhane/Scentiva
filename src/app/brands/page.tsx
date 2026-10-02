import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { BrandsPage } from '@/views/BrandsPage';

export const metadata: Metadata = {
  title: 'Luxury Perfume Houses & Brands | SCENTIVA',
  description: 'Explore the master fragrance houses curated by SCENTIVA, including Tom Ford, Creed, Maison Francis Kurkdjian, Parfums de Marly, and Byredo.',
};

export default function Brands() {
  return <BrandsPage />;
}
