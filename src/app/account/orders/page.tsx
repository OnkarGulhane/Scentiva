import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { AccountOrdersPage } from '@/views/AccountOrdersPage';

export const metadata: Metadata = {
  title: 'Order History | SCENTIVA Haute Parfumerie',
  description: 'View and track past luxury perfume acquisitions and ongoing deliveries.',
};

export default function AccountOrdersRoute() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-neutral-50 animate-pulse flex items-center justify-center p-8"><div className="w-8 h-8 rounded-full border-2 border-brand-plum-900 border-t-transparent animate-spin" /></div>}>
      <AccountOrdersPage />
    </Suspense>
  );
}
