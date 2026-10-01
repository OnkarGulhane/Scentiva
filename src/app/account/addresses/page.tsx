import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { AccountAddressesPage } from '@/views/AccountAddressesPage';

export const metadata: Metadata = {
  title: 'Saved Delivery Addresses | SCENTIVA Haute Parfumerie',
  description: 'Manage your primary and secondary shipping destinations across India.',
};

export default function AccountAddressesRoute() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-neutral-50 animate-pulse flex items-center justify-center p-8"><div className="w-8 h-8 rounded-full border-2 border-brand-plum-900 border-t-transparent animate-spin" /></div>}>
      <AccountAddressesPage />
    </Suspense>
  );
}
