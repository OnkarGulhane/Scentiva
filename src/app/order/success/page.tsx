import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { OrderSuccessPage } from '@/views/OrderSuccessPage';

export const metadata: Metadata = {
  title: 'Order Confirmed | SCENTIVA Haute Parfumerie',
  description: 'Thank you for your order. Your luxury fragrance flacon is being prepared in our climate-regulated vault.',
};

export default function OrderSuccessRoute() {
  return <OrderSuccessPage />;
}
