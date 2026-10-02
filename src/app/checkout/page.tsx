import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { CheckoutPage } from '@/views/CheckoutPage';

export const metadata: Metadata = {
  title: 'Secure Checkout | SCENTIVA Haute Parfumerie',
  description: 'Complete your luxury fragrance order with temperature-controlled express courier and tamper-evident packaging.',
};

export default function CheckoutRoute() {
  return <CheckoutPage />;
}
