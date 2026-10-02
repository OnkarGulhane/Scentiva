import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { CheckoutPage } from '@/views/CheckoutPage';

export const metadata: Metadata = {
  title: 'Payment Selection | SCENTIVA Haute Parfumerie',
  description: 'Choose your preferred payment method: UPI, Credit/Debit Cards, Net Banking, or Cash on Delivery.',
};

export default function CheckoutPaymentRoute() {
  return <CheckoutPage />;
}
