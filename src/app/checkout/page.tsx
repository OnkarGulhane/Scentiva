import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { CheckoutPage } from '@/views/CheckoutPage';

export const metadata: Metadata = {
  title: 'Secure Checkout | SCENTIVA Haute Parfumerie',
  description: 'Complete your luxury fragrance order with temperature-controlled express courier and tamper-evident packaging.',
};

export default function CheckoutRoute() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-neutral-50 animate-pulse flex items-center justify-center p-8"><div className="w-8 h-8 rounded-full border-2 border-brand-plum-900 border-t-transparent animate-spin" /></div>}>
      <CheckoutPage />
    </Suspense>
  );
}
