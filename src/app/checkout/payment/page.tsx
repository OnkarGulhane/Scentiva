import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { CheckoutPage } from '@/views/CheckoutPage';

export const metadata: Metadata = {
  title: 'Payment Selection | SCENTIVA Haute Parfumerie',
  description: 'Choose your preferred payment method: UPI, Credit/Debit Cards, Net Banking, or Cash on Delivery.',
};

export default function CheckoutPaymentRoute() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-neutral-50 animate-pulse flex items-center justify-center p-8"><div className="w-8 h-8 rounded-full border-2 border-brand-plum-900 border-t-transparent animate-spin" /></div>}>
      <CheckoutPage />
    </Suspense>
  );
}
