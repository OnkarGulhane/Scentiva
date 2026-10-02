import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { OffersPage } from '@/views/OffersPage';

export const metadata: Metadata = {
  title: 'Exclusive Offers & Promo Codes | SCENTIVA Haute Parfumerie',
  description: 'Unlock seasonal luxury promotions, welcome coupon codes, and discovery gift sets at SCENTIVA.',
};

export default function OffersRoute() {
  return <OffersPage />;
}
