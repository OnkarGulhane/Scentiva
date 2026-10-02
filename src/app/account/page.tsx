import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { AccountPage } from '@/views/AccountPage';

export const metadata: Metadata = {
  title: 'Connoisseur Account | SCENTIVA Haute Parfumerie',
  description: 'Manage your SCENTIVA membership, reward tier, saved addresses, and order history.',
};

export default function AccountRoute() {
  return <AccountPage />;
}
