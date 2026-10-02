import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { AccountAddressesPage } from '@/views/AccountAddressesPage';

export const metadata: Metadata = {
  title: 'Saved Delivery Addresses | SCENTIVA Haute Parfumerie',
  description: 'Manage your primary and secondary shipping destinations across India.',
};

export default function AccountAddressesRoute() {
  return <AccountAddressesPage />;
}
