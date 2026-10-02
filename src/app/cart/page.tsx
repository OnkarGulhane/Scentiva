import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { CartPage } from '@/views/CartPage';

export const metadata: Metadata = {
  title: 'Shopping Bag | SCENTIVA Haute Parfumerie',
  description: 'Review your selected luxury fragrances, apply privilege promo codes, and proceed to secure checkout.',
};

export default function CartRoute() {
  return <CartPage />;
}
