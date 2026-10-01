import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { AccountPage } from '@/views/AccountPage';

export const metadata: Metadata = {
  title: 'Connoisseur Account | SCENTIVA Haute Parfumerie',
  description: 'Manage your SCENTIVA membership, reward tier, saved addresses, and order history.',
};

export default function AccountRoute() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-neutral-50 animate-pulse flex items-center justify-center p-8"><div className="w-8 h-8 rounded-full border-2 border-brand-plum-900 border-t-transparent animate-spin" /></div>}>
      <AccountPage />
    </Suspense>
  );
}
