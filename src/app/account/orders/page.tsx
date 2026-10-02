import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { AccountOrdersPage } from '@/views/AccountOrdersPage';

export const metadata: Metadata = {
  title: 'Order History | SCENTIVA Haute Parfumerie',
  description: 'View and track past luxury perfume acquisitions and ongoing deliveries.',
};

export default function AccountOrdersRoute() {
  return <AccountOrdersPage />;
}
