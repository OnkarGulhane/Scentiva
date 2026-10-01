import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { OrderSuccessPage } from '@/views/OrderSuccessPage';

export const metadata: Metadata = {
  title: 'Order Confirmed | SCENTIVA Haute Parfumerie',
  description: 'Thank you for your order. Your luxury fragrance flacon is being prepared in our climate-regulated vault.',
};

export default function OrderSuccessRoute() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-neutral-50 animate-pulse flex items-center justify-center p-8"><div className="w-8 h-8 rounded-full border-2 border-brand-plum-900 border-t-transparent animate-spin" /></div>}>
      <OrderSuccessPage />
    </Suspense>
  );
}
